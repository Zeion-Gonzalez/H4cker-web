package org.jcodec.containers.mkv.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mkv.CuesFactory;
import org.jcodec.containers.mkv.MKVType;
import org.jcodec.containers.mkv.SeekHeadFactory;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlBin;
import org.jcodec.containers.mkv.boxes.EbmlDate;
import org.jcodec.containers.mkv.boxes.EbmlFloat;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlString;
import org.jcodec.containers.mkv.boxes.EbmlUint;
import org.jcodec.containers.mkv.boxes.MkvBlock;
import org.jcodec.containers.mkv.muxer.MKVMuxerTrack;

/* loaded from: classes.dex */
public class MKVMuxer {
    private EbmlMaster mkvCues;
    private EbmlMaster mkvInfo;
    private EbmlMaster mkvSeekHead;
    private EbmlMaster mkvTracks;
    private List<MKVMuxerTrack> tracks = new ArrayList();
    private MKVMuxerTrack videoTrack = null;
    private List<EbmlMaster> clusterList = new LinkedList();

    public MKVMuxerTrack createVideoTrack(Size dimentions, String codecId) {
        if (this.videoTrack == null) {
            this.videoTrack = new MKVMuxerTrack();
            this.tracks.add(this.videoTrack);
            this.videoTrack.codecId = codecId;
            this.videoTrack.frameDimentions = dimentions;
            this.videoTrack.trackNo = this.tracks.size();
        }
        return this.videoTrack;
    }

    public void mux(SeekableByteChannel s) throws IOException {
        List<EbmlMaster> mkvFile = new ArrayList<>();
        EbmlMaster ebmlHeader = defaultEbmlHeader();
        mkvFile.add(ebmlHeader);
        EbmlMaster segmentElem = (EbmlMaster) MKVType.createByType(MKVType.Segment);
        this.mkvInfo = muxInfo();
        this.mkvTracks = muxTracks();
        this.mkvCues = (EbmlMaster) MKVType.createByType(MKVType.Cues);
        this.mkvSeekHead = muxSeekHead();
        muxCues();
        segmentElem.add(this.mkvSeekHead);
        segmentElem.add(this.mkvInfo);
        segmentElem.add(this.mkvTracks);
        segmentElem.add(this.mkvCues);
        for (EbmlMaster aCluster : this.clusterList) {
            segmentElem.add(aCluster);
        }
        mkvFile.add(segmentElem);
        for (EbmlMaster el : mkvFile) {
            el.mux(s);
        }
    }

    private EbmlMaster defaultEbmlHeader() {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.EBML);
        createChild(master, MKVType.EBMLVersion, 1L);
        createChild(master, MKVType.EBMLReadVersion, 1L);
        createChild(master, MKVType.EBMLMaxIDLength, 4L);
        createChild(master, MKVType.EBMLMaxSizeLength, 8L);
        createChild(master, MKVType.DocType, "webm");
        createChild(master, MKVType.DocTypeVersion, 2L);
        createChild(master, MKVType.DocTypeReadVersion, 2L);
        return master;
    }

    private EbmlMaster muxInfo() {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.Info);
        createChild(master, MKVType.TimecodeScale, 40000000);
        createChild(master, MKVType.WritingApp, "JCodec v0.1.7");
        createChild(master, MKVType.MuxingApp, "JCodec MKVStreamingMuxer v0.1.7");
        MkvBlock lastBlock = this.videoTrack.trackBlocks.get(this.videoTrack.trackBlocks.size() - 1);
        createChild(master, MKVType.Duration, (lastBlock.absoluteTimecode + 1) * 40000000 * 1.0d);
        createChild(master, MKVType.DateUTC, new Date());
        return master;
    }

    private EbmlMaster muxTracks() {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.Tracks);
        for (int i = 0; i < this.tracks.size(); i++) {
            MKVMuxerTrack track = this.tracks.get(i);
            EbmlMaster trackEntryElem = (EbmlMaster) MKVType.createByType(MKVType.TrackEntry);
            createChild(trackEntryElem, MKVType.TrackNumber, track.trackNo);
            createChild(trackEntryElem, MKVType.TrackUID, track.trackNo);
            if (MKVMuxerTrack.MKVMuxerTrackType.VIDEO.equals(track.type)) {
                createChild(trackEntryElem, MKVType.TrackType, 1L);
                createChild(trackEntryElem, MKVType.Name, "Track " + (i + 1) + " Video");
                createChild(trackEntryElem, MKVType.CodecID, track.codecId);
                EbmlMaster trackVideoElem = (EbmlMaster) MKVType.createByType(MKVType.Video);
                createChild(trackVideoElem, MKVType.PixelWidth, track.frameDimentions.getWidth());
                createChild(trackVideoElem, MKVType.PixelHeight, track.frameDimentions.getHeight());
                trackEntryElem.add(trackVideoElem);
            } else {
                createChild(trackEntryElem, MKVType.TrackType, 2L);
                createChild(trackEntryElem, MKVType.Name, "Track " + (i + 1) + " Audio");
                createChild(trackEntryElem, MKVType.CodecID, track.codecId);
            }
            master.add(trackEntryElem);
        }
        return master;
    }

    private void muxCues() {
        CuesFactory cf = new CuesFactory(this.mkvSeekHead.size() + this.mkvInfo.size() + this.mkvTracks.size(), (long) (this.videoTrack.trackNo - 1));
        for (MkvBlock aBlock : this.videoTrack.trackBlocks) {
            EbmlMaster mkvCluster = singleBlockedCluster(aBlock);
            this.clusterList.add(mkvCluster);
            cf.add(CuesFactory.CuePointMock.make(mkvCluster));
        }
        EbmlMaster indexedCues = cf.createCues();
        Iterator i$ = indexedCues.children.iterator();
        while (i$.hasNext()) {
            EbmlBase aCuePoint = i$.next();
            this.mkvCues.add(aCuePoint);
        }
    }

    private EbmlMaster singleBlockedCluster(MkvBlock aBlock) {
        EbmlMaster mkvCluster = (EbmlMaster) MKVType.createByType(MKVType.Cluster);
        createChild(mkvCluster, MKVType.Timecode, aBlock.absoluteTimecode - aBlock.timecode);
        mkvCluster.add(aBlock);
        return mkvCluster;
    }

    private EbmlMaster muxSeekHead() {
        SeekHeadFactory shi = new SeekHeadFactory();
        shi.add(this.mkvInfo);
        shi.add(this.mkvTracks);
        shi.add(this.mkvCues);
        return shi.indexSeekHead();
    }

    public static void createChild(EbmlMaster parent, MKVType type, long value) {
        EbmlUint se = (EbmlUint) MKVType.createByType(type);
        se.set(value);
        parent.add(se);
    }

    public static void createChild(EbmlMaster parent, MKVType type, String value) {
        EbmlString se = (EbmlString) MKVType.createByType(type);
        se.set(value);
        parent.add(se);
    }

    public static void createChild(EbmlMaster parent, MKVType type, Date value) {
        EbmlDate se = (EbmlDate) MKVType.createByType(type);
        se.setDate(value);
        parent.add(se);
    }

    public static void createChild(EbmlMaster parent, MKVType type, ByteBuffer value) {
        EbmlBin se = (EbmlBin) MKVType.createByType(type);
        se.set(value);
        parent.add(se);
    }

    public static void createChild(EbmlMaster parent, MKVType type, double value) {
        try {
            EbmlFloat se = (EbmlFloat) MKVType.createByType(type);
            se.set(value);
            parent.add(se);
        } catch (ClassCastException cce) {
            throw new RuntimeException("Element of type " + type + " can't be cast to EbmlFloat", cce);
        }
    }
}
