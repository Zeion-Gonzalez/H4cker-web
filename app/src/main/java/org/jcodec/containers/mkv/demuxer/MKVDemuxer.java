package org.jcodec.containers.mkv.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mkv.MKVParser;
import org.jcodec.containers.mkv.MKVType;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlBin;
import org.jcodec.containers.mkv.boxes.EbmlFloat;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlUint;
import org.jcodec.containers.mkv.boxes.MkvBlock;

/* loaded from: classes.dex */
public final class MKVDemuxer {
    private static final TapeTimecode ZERO_TAPE_TIMECODE = new TapeTimecode(0, (byte) 0, (byte) 0, (byte) 0, false);
    private SeekableByteChannel channel;
    int pictureHeight;
    int pictureWidth;

    /* renamed from: t */
    private List<EbmlMaster> f1541t;
    private VideoTrack vTrack = null;
    private List<DemuxerTrack> aTracks = new ArrayList();
    long timescale = 1;

    public MKVDemuxer(List<EbmlMaster> t, SeekableByteChannel fileChannelWrapper) {
        this.f1541t = t;
        this.channel = fileChannelWrapper;
        demux();
    }

    private void demux() {
        EbmlUint ts = (EbmlUint) MKVType.findFirst(this.f1541t, MKVType.Segment, MKVType.Info, MKVType.TimecodeScale);
        if (ts != null) {
            this.timescale = ts.get();
        }
        for (EbmlMaster aTrack : MKVType.findList(this.f1541t, EbmlMaster.class, MKVType.Segment, MKVType.Tracks, MKVType.TrackEntry)) {
            long type = ((EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.TrackType)).get();
            long id = ((EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.TrackNumber)).get();
            if (type == 1) {
                if (this.vTrack != null) {
                    throw new RuntimeException("More then 1 video track, can not compute...");
                }
                EbmlBin videoCodecState = (EbmlBin) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.CodecPrivate);
                ByteBuffer state = null;
                if (videoCodecState != null) {
                    state = videoCodecState.data;
                }
                EbmlUint width = (EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Video, MKVType.PixelWidth);
                EbmlUint height = (EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Video, MKVType.PixelHeight);
                EbmlUint dwidth = (EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Video, MKVType.DisplayWidth);
                EbmlUint dheight = (EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Video, MKVType.DisplayHeight);
                EbmlUint unit = (EbmlUint) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Video, MKVType.DisplayUnit);
                if (width != null && height != null) {
                    this.pictureWidth = (int) width.get();
                    this.pictureHeight = (int) height.get();
                } else if (dwidth != null && dheight != null) {
                    if (unit == null || unit.get() == 0) {
                        this.pictureHeight = (int) dheight.get();
                        this.pictureWidth = (int) dwidth.get();
                    } else {
                        throw new RuntimeException("DisplayUnits other then 0 are not implemented yet");
                    }
                }
                this.vTrack = new VideoTrack((int) id, state);
            } else if (type == 2) {
                AudioTrack audioTrack = new AudioTrack((int) id);
                EbmlFloat sf = (EbmlFloat) MKVType.findFirst(aTrack, MKVType.TrackEntry, MKVType.Audio, MKVType.SamplingFrequency);
                if (sf != null) {
                    audioTrack.samplingFrequency = sf.get();
                }
                this.aTracks.add(audioTrack);
            }
        }
        for (EbmlMaster aCluster : MKVType.findList(this.f1541t, EbmlMaster.class, MKVType.Segment, MKVType.Cluster)) {
            long baseTimecode = ((EbmlUint) MKVType.findFirst(aCluster, MKVType.Cluster, MKVType.Timecode)).get();
            Iterator<EbmlBase> it = aCluster.children.iterator();
            while (it.hasNext()) {
                EbmlBase child = it.next();
                if (MKVType.SimpleBlock.equals(child.type)) {
                    MkvBlock b = (MkvBlock) child;
                    b.absoluteTimecode = b.timecode + baseTimecode;
                    putIntoRightBasket(b);
                } else if (MKVType.BlockGroup.equals(child.type)) {
                    EbmlMaster group = (EbmlMaster) child;
                    Iterator i$ = group.children.iterator();
                    while (i$.hasNext()) {
                        EbmlBase grandChild = i$.next();
                        if (MKVType.Block.equals(grandChild)) {
                            MkvBlock b2 = (MkvBlock) child;
                            b2.absoluteTimecode = b2.timecode + baseTimecode;
                            putIntoRightBasket(b2);
                        }
                    }
                }
            }
        }
    }

    private void putIntoRightBasket(MkvBlock b) {
        if (b.trackNumber == this.vTrack.trackNo) {
            this.vTrack.blocks.add(b);
            return;
        }
        for (int i = 0; i < this.aTracks.size(); i++) {
            AudioTrack audio = (AudioTrack) this.aTracks.get(i);
            if (b.trackNumber == audio.trackNo) {
                audio.blocks.add(IndexedBlock.make(audio.framesCount, b));
                AudioTrack.access$012(audio, b.frameSizes.length);
            }
        }
    }

    public static MKVDemuxer getDemuxer(SeekableByteChannel channel) throws IOException {
        MKVParser parser = new MKVParser(channel);
        return new MKVDemuxer(parser.parse(), channel);
    }

    public DemuxerTrack getVideoTrack() {
        return this.vTrack;
    }

    /* loaded from: classes.dex */
    public class VideoTrack implements SeekableDemuxerTrack {
        private ByteBuffer state;
        public final int trackNo;
        private int frameIdx = 0;
        List<MkvBlock> blocks = new ArrayList();

        public VideoTrack(int trackNo, ByteBuffer state) {
            this.trackNo = trackNo;
            this.state = state;
        }

        @Override // org.jcodec.common.DemuxerTrack
        public Packet nextFrame() throws IOException {
            if (this.frameIdx >= this.blocks.size()) {
                return null;
            }
            MkvBlock b = this.blocks.get(this.frameIdx);
            if (b == null) {
                throw new RuntimeException("Something somewhere went wrong.");
            }
            this.frameIdx++;
            MKVDemuxer.this.channel.position(b.dataOffset);
            ByteBuffer data = ByteBuffer.allocate(b.dataLen);
            MKVDemuxer.this.channel.read(data);
            data.flip();
            b.readFrames(data.duplicate());
            long duration = 1;
            if (this.frameIdx < this.blocks.size()) {
                duration = this.blocks.get(this.frameIdx).absoluteTimecode - b.absoluteTimecode;
            }
            return new Packet(b.frames[0].duplicate(), b.absoluteTimecode, MKVDemuxer.this.timescale, duration, (long) (this.frameIdx - 1), b.keyFrame, MKVDemuxer.ZERO_TAPE_TIMECODE);
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoFrame(long i) {
            if (i > 2147483647L || i > this.blocks.size()) {
                return false;
            }
            this.frameIdx = (int) i;
            return true;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public long getCurFrame() {
            return this.frameIdx;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public void seek(double second) {
            throw new RuntimeException("Not implemented yet");
        }

        public int getFrameCount() {
            return this.blocks.size();
        }

        public ByteBuffer getCodecState() {
            return this.state;
        }

        @Override // org.jcodec.common.DemuxerTrack
        public DemuxerTrackMeta getMeta() {
            throw new RuntimeException("Unsupported");
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoSyncFrame(long i) {
            throw new RuntimeException("Unsupported");
        }
    }

    /* loaded from: classes.dex */
    public static class IndexedBlock {
        public MkvBlock block;
        public int firstFrameNo;

        public static IndexedBlock make(int no, MkvBlock b) {
            IndexedBlock ib = new IndexedBlock();
            ib.firstFrameNo = no;
            ib.block = b;
            return ib;
        }
    }

    /* loaded from: classes.dex */
    public class AudioTrack implements SeekableDemuxerTrack {
        public double samplingFrequency;
        public final int trackNo;
        List<IndexedBlock> blocks = new ArrayList();
        private int framesCount = 0;
        private int frameIdx = 0;
        private int blockIdx = 0;
        private int frameInBlockIdx = 0;

        static /* synthetic */ int access$012(AudioTrack x0, int x1) {
            int i = x0.framesCount + x1;
            x0.framesCount = i;
            return i;
        }

        public AudioTrack(int trackNo) {
            this.trackNo = trackNo;
        }

        @Override // org.jcodec.common.DemuxerTrack
        public Packet nextFrame() throws IOException {
            if (this.frameIdx > this.blocks.size()) {
                return null;
            }
            MkvBlock b = this.blocks.get(this.blockIdx).block;
            if (b == null) {
                throw new RuntimeException("Something somewhere went wrong.");
            }
            if (b.frames == null || b.frames.length == 0) {
                MKVDemuxer.this.channel.position(b.dataOffset);
                ByteBuffer data = ByteBuffer.allocate(b.dataLen);
                MKVDemuxer.this.channel.read(data);
                b.readFrames(data);
            }
            ByteBuffer data2 = b.frames[this.frameInBlockIdx].duplicate();
            this.frameInBlockIdx++;
            this.frameIdx++;
            if (this.frameInBlockIdx >= b.frames.length) {
                this.blockIdx++;
                this.frameInBlockIdx = 0;
            }
            return new Packet(data2, b.absoluteTimecode, Math.round(this.samplingFrequency), 1L, 0L, false, MKVDemuxer.ZERO_TAPE_TIMECODE);
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoFrame(long i) {
            int frameBlockIdx;
            if (i > 2147483647L || i > this.framesCount || (frameBlockIdx = findBlockIndex(i)) == -1) {
                return false;
            }
            this.frameIdx = (int) i;
            this.blockIdx = frameBlockIdx;
            this.frameInBlockIdx = ((int) i) - this.blocks.get(this.blockIdx).firstFrameNo;
            return true;
        }

        private int findBlockIndex(long i) {
            for (int blockIndex = 0; blockIndex < this.blocks.size(); blockIndex++) {
                if (i >= this.blocks.get(blockIndex).block.frameSizes.length) {
                    i -= this.blocks.get(blockIndex).block.frameSizes.length;
                } else {
                    return blockIndex;
                }
            }
            return -1;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public long getCurFrame() {
            return this.frameIdx;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public void seek(double second) {
            throw new RuntimeException("Not implemented yet");
        }

        public Packet getFrames(int count) {
            if (this.frameIdx + count >= this.framesCount) {
                return null;
            }
            List<ByteBuffer> packetFrames = new ArrayList<>();
            MkvBlock firstBlockInAPacket = this.blocks.get(this.blockIdx).block;
            while (count > 0) {
                MkvBlock b = this.blocks.get(this.blockIdx).block;
                if (b.frames == null || b.frames.length == 0) {
                    try {
                        MKVDemuxer.this.channel.position(b.dataOffset);
                        ByteBuffer data = ByteBuffer.allocate(b.dataLen);
                        MKVDemuxer.this.channel.read(data);
                        b.readFrames(data);
                    } catch (IOException ioe) {
                        throw new RuntimeException("while reading frames of a Block at offset 0x" + Long.toHexString(b.dataOffset).toUpperCase() + ")", ioe);
                    }
                }
                packetFrames.add(b.frames[this.frameInBlockIdx].duplicate());
                this.frameIdx++;
                this.frameInBlockIdx++;
                if (this.frameInBlockIdx >= b.frames.length) {
                    this.frameInBlockIdx = 0;
                    this.blockIdx++;
                }
                count--;
            }
            int size = 0;
            for (ByteBuffer aFrame : packetFrames) {
                size += aFrame.limit();
            }
            ByteBuffer data2 = ByteBuffer.allocate(size);
            for (ByteBuffer aFrame2 : packetFrames) {
                data2.put(aFrame2);
            }
            return new Packet(data2, firstBlockInAPacket.absoluteTimecode, Math.round(this.samplingFrequency), packetFrames.size(), 0L, false, MKVDemuxer.ZERO_TAPE_TIMECODE);
        }

        @Override // org.jcodec.common.DemuxerTrack
        public DemuxerTrackMeta getMeta() {
            throw new RuntimeException("Unsupported");
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoSyncFrame(long frame) {
            return gotoFrame(frame);
        }
    }

    public int getPictureWidth() {
        return this.pictureWidth;
    }

    public int getPictureHeight() {
        return this.pictureHeight;
    }

    public List<DemuxerTrack> getAudioTracks() {
        return this.aTracks;
    }
}
