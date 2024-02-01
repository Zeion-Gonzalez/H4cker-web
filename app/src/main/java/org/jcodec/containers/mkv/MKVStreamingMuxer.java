package org.jcodec.containers.mkv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.Assert;
import org.jcodec.containers.mkv.CuesFactory;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.MkvBlock;
import org.jcodec.containers.mkv.boxes.MkvSegment;
import org.jcodec.containers.mkv.muxer.MKVMuxer;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.MovieSegment;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class MKVStreamingMuxer {
    private static final int DEFAULT_TIMESCALE = 1000000000;
    private static final int MULTIPLIER = 1000;
    private static final int TIMESCALE = 1000000;
    private static final String VP80_FOURCC = "avc1";
    public MovieSegment headerChunk;
    private EbmlMaster mkvCues;
    private EbmlMaster mkvInfo;
    private EbmlMaster mkvSeekHead;
    private EbmlMaster mkvTracks;
    private EbmlMaster segmentElem;
    private LinkedList<WebmCluster> webmClusters;

    public MovieSegment preparePacket(VirtualTrack track, VirtualPacket pkt, int chunkNo, int trackNo, long previousClustersSize) {
        WebmCluster wmc = new WebmCluster(track, pkt, chunkNo, trackNo, previousClustersSize);
        if (this.webmClusters == null) {
            this.webmClusters = new LinkedList<>();
        }
        this.webmClusters.add(wmc);
        return wmc;
    }

    public MovieSegment prepareHeader(List<MovieSegment> chunks, VirtualTrack[] tracks) throws IOException {
        EbmlMaster ebmlHeader = muxEbmlHeader();
        this.segmentElem = (EbmlMaster) MKVType.createByType(MKVType.Segment);
        this.mkvInfo = muxInfo(tracks);
        this.mkvTracks = muxTracks(tracks);
        this.mkvCues = (EbmlMaster) MKVType.createByType(MKVType.Cues);
        this.mkvSeekHead = muxSeekHead();
        muxCues(tracks);
        this.segmentElem.add(this.mkvSeekHead);
        this.segmentElem.add(this.mkvInfo);
        this.segmentElem.add(this.mkvTracks);
        this.segmentElem.add(this.mkvCues);
        Iterator i$ = this.webmClusters.iterator();
        while (i$.hasNext()) {
            WebmCluster wc = i$.next();
            this.segmentElem.add(wc.f1536c);
        }
        List<EbmlMaster> header = new ArrayList<>();
        header.add(ebmlHeader);
        header.add(this.segmentElem);
        this.headerChunk = new HeaderSegment(header);
        return this.headerChunk;
    }

    private EbmlMaster muxEbmlHeader() {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.EBML);
        MKVMuxer.createChild(master, MKVType.EBMLVersion, 1L);
        MKVMuxer.createChild(master, MKVType.EBMLReadVersion, 1L);
        MKVMuxer.createChild(master, MKVType.EBMLMaxIDLength, 4L);
        MKVMuxer.createChild(master, MKVType.EBMLMaxSizeLength, 8L);
        MKVMuxer.createChild(master, MKVType.DocType, "webm");
        MKVMuxer.createChild(master, MKVType.DocTypeVersion, 2L);
        MKVMuxer.createChild(master, MKVType.DocTypeReadVersion, 2L);
        return master;
    }

    private EbmlMaster muxInfo(VirtualTrack[] tracks) {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.Info);
        MKVMuxer.createChild(master, MKVType.TimecodeScale, 1000000L);
        MKVMuxer.createChild(master, MKVType.WritingApp, "JCodec v0.1.7");
        MKVMuxer.createChild(master, MKVType.MuxingApp, "JCodec MKVStreamingMuxer v0.1.7");
        WebmCluster lastCluster = this.webmClusters.get(this.webmClusters.size() - 1);
        MKVMuxer.createChild(master, MKVType.Duration, (lastCluster.pkt.getPts() + lastCluster.pkt.getDuration()) * 1000.0d);
        MKVMuxer.createChild(master, MKVType.DateUTC, new Date());
        return master;
    }

    private EbmlMaster muxTracks(VirtualTrack[] tracks) {
        EbmlMaster master = (EbmlMaster) MKVType.createByType(MKVType.Tracks);
        for (int i = 0; i < tracks.length; i++) {
            VirtualTrack track = tracks[i];
            EbmlMaster trackEntryElem = (EbmlMaster) MKVType.createByType(MKVType.TrackEntry);
            MKVMuxer.createChild(trackEntryElem, MKVType.TrackNumber, i + 1);
            MKVMuxer.createChild(trackEntryElem, MKVType.TrackUID, i + 1);
            CodecMeta codecMeta = track.getCodecMeta();
            if (VP80_FOURCC.equalsIgnoreCase(track.getCodecMeta().getFourcc())) {
                MKVMuxer.createChild(trackEntryElem, MKVType.TrackType, 1L);
                MKVMuxer.createChild(trackEntryElem, MKVType.Name, "Track " + (i + 1) + " Video");
                MKVMuxer.createChild(trackEntryElem, MKVType.CodecID, "V_VP8");
                MKVMuxer.createChild(trackEntryElem, MKVType.CodecPrivate, codecMeta.getCodecPrivate());
                if (codecMeta instanceof VideoCodecMeta) {
                    VideoCodecMeta vcm = (VideoCodecMeta) codecMeta;
                    EbmlMaster trackVideoElem = (EbmlMaster) MKVType.createByType(MKVType.Video);
                    MKVMuxer.createChild(trackVideoElem, MKVType.PixelWidth, vcm.getSize().getWidth());
                    MKVMuxer.createChild(trackVideoElem, MKVType.PixelHeight, vcm.getSize().getHeight());
                    trackEntryElem.add(trackVideoElem);
                }
            } else if ("vrbs".equalsIgnoreCase(track.getCodecMeta().getFourcc())) {
                MKVMuxer.createChild(trackEntryElem, MKVType.TrackType, 2L);
                MKVMuxer.createChild(trackEntryElem, MKVType.Name, "Track " + (i + 1) + " Audio");
                MKVMuxer.createChild(trackEntryElem, MKVType.CodecID, "A_VORBIS");
                MKVMuxer.createChild(trackEntryElem, MKVType.CodecPrivate, codecMeta.getCodecPrivate());
                if (codecMeta instanceof AudioCodecMeta) {
                    AudioCodecMeta acm = (AudioCodecMeta) codecMeta;
                    EbmlMaster trackAudioElem = (EbmlMaster) MKVType.createByType(MKVType.Audio);
                    MKVMuxer.createChild(trackAudioElem, MKVType.Channels, acm.getChannelCount());
                    MKVMuxer.createChild(trackAudioElem, MKVType.BitDepth, acm.getSampleSize());
                    MKVMuxer.createChild(trackAudioElem, MKVType.SamplingFrequency, acm.getSampleRate());
                    trackEntryElem.add(trackAudioElem);
                }
            }
            master.add(trackEntryElem);
        }
        return master;
    }

    private EbmlMaster muxSeekHead() {
        SeekHeadFactory shi = new SeekHeadFactory();
        shi.add(this.mkvInfo);
        shi.add(this.mkvTracks);
        shi.add(this.mkvCues);
        return shi.indexSeekHead();
    }

    private void muxCues(VirtualTrack[] tracks) {
        int trackIndex = findFirstVP8TrackIndex(tracks);
        CuesFactory ci = new CuesFactory(this.mkvSeekHead.size() + this.mkvInfo.size() + this.mkvTracks.size(), trackIndex + 1);
        Iterator i$ = this.webmClusters.iterator();
        while (i$.hasNext()) {
            WebmCluster aCluster = i$.next();
            ci.add(CuesFactory.CuePointMock.make(aCluster.f1536c));
        }
        EbmlMaster indexedCues = ci.createCues();
        Iterator i$2 = indexedCues.children.iterator();
        while (i$2.hasNext()) {
            EbmlBase aCuePoint = i$2.next();
            this.mkvCues.add(aCuePoint);
        }
    }

    private static int findFirstVP8TrackIndex(VirtualTrack[] tracks) {
        for (int i = 0; i < tracks.length; i++) {
            if (VP80_FOURCC.equalsIgnoreCase(tracks[i].getCodecMeta().getFourcc())) {
                return i;
            }
        }
        return -1;
    }

    /* loaded from: classes.dex */
    public class WebmCluster implements MovieSegment {

        /* renamed from: be */
        MkvBlock f1535be = (MkvBlock) MKVType.createByType(MKVType.SimpleBlock);

        /* renamed from: c */
        EbmlMaster f1536c = (EbmlMaster) MKVType.createByType(MKVType.Cluster);
        private int chunkNo;
        public VirtualPacket pkt;
        private long previousClustersSize;
        private int trackNo;

        public WebmCluster(VirtualTrack track, VirtualPacket pkt, int chunkNo, int trackNo, long previousClustersSize) {
            this.pkt = pkt;
            this.chunkNo = chunkNo;
            this.trackNo = trackNo + 1;
            this.previousClustersSize = previousClustersSize;
            long timecode = (long) (pkt.getPts() * 1000.0d);
            MKVMuxer.createChild(this.f1536c, MKVType.Timecode, timecode);
            try {
                this.f1535be.frameSizes = new int[]{this.pkt.getDataLen()};
                this.f1535be.timecode = 0;
                this.f1535be.trackNumber = this.trackNo;
                this.f1535be.discardable = false;
                this.f1535be.lacingPresent = false;
                this.f1535be.dataLen = this.f1535be.getDataSize();
                this.f1536c.add(this.f1535be);
            } catch (IOException ioe) {
                throw new RuntimeException("Failed to read size of the frame", ioe);
            }
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public ByteBuffer getData() throws IOException {
            this.f1535be.frames = new ByteBuffer[1];
            this.f1535be.frames[0] = this.pkt.getData().duplicate();
            ByteBuffer data = this.f1536c.getData();
            Assert.assertEquals("computed and actuall cluster sizes MUST match", (int) this.f1536c.size(), data.remaining());
            return data;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getNo() {
            return this.chunkNo;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public long getPos() {
            try {
                return this.previousClustersSize + MKVStreamingMuxer.this.headerChunk.getDataLen();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't compute header length", e);
            }
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getDataLen() throws IOException {
            return (int) this.f1536c.size();
        }
    }

    /* loaded from: classes.dex */
    public static class HeaderSegment implements MovieSegment {
        private List<EbmlMaster> header;

        public HeaderSegment(List<EbmlMaster> header) {
            this.header = header;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public long getPos() {
            return 0L;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getNo() {
            return 0;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getDataLen() throws IOException {
            int size = 0;
            for (EbmlMaster m : this.header) {
                if (MKVType.Segment.equals(m.type)) {
                    size = (int) (size + ((MkvSegment) m).getHeaderSize());
                } else {
                    size = (int) (size + m.size());
                }
            }
            return size;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public ByteBuffer getData() throws IOException {
            ByteBuffer data = ByteBuffer.allocate(getDataLen());
            for (EbmlMaster m : this.header) {
                if (MKVType.Segment.equals(m.type)) {
                    data.put(((MkvSegment) m).getHeader());
                } else {
                    data.put(m.getData());
                }
            }
            data.flip();
            return data;
        }
    }
}
