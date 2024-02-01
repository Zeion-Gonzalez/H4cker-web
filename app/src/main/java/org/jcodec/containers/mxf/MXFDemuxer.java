package org.jcodec.containers.mxf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Size;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mxf.MXFConst;
import org.jcodec.containers.mxf.model.C0893UL;
import org.jcodec.containers.mxf.model.FileDescriptor;
import org.jcodec.containers.mxf.model.GenericDescriptor;
import org.jcodec.containers.mxf.model.GenericPictureEssenceDescriptor;
import org.jcodec.containers.mxf.model.GenericSoundEssenceDescriptor;
import org.jcodec.containers.mxf.model.IndexSegment;
import org.jcodec.containers.mxf.model.KLV;
import org.jcodec.containers.mxf.model.MXFMetadata;
import org.jcodec.containers.mxf.model.MXFPartition;
import org.jcodec.containers.mxf.model.MXFUtil;
import org.jcodec.containers.mxf.model.TimecodeComponent;
import org.jcodec.containers.mxf.model.TimelineTrack;
import org.jcodec.containers.mxf.model.WaveAudioDescriptor;

/* loaded from: classes.dex */
public class MXFDemuxer {

    /* renamed from: ch */
    protected SeekableByteChannel f1550ch;
    protected double duration;
    protected MXFPartition header;
    protected List<IndexSegment> indexSegments;
    protected List<MXFMetadata> metadata;
    protected List<MXFPartition> partitions;
    protected TimecodeComponent timecode;
    protected int totalFrames;
    protected MXFDemuxerTrack[] tracks;

    public MXFDemuxer(SeekableByteChannel ch) throws IOException {
        this.f1550ch = ch;
        ch.position(0L);
        parseHeader(ch);
        findIndex();
        this.tracks = findTracks();
        this.timecode = (TimecodeComponent) MXFUtil.findMeta(this.metadata, TimecodeComponent.class);
    }

    /* renamed from: org.jcodec.containers.mxf.MXFDemuxer$OP */
    /* loaded from: classes.dex */
    public enum EnumC0892OP {
        OP1a(1, 1),
        OP1b(1, 2),
        OP1c(1, 3),
        OP2a(2, 1),
        OP2b(2, 2),
        OP2c(2, 3),
        OP3a(3, 1),
        OP3b(3, 2),
        OP3c(3, 3),
        OPAtom(16, 0);

        public int major;
        public int minor;

        EnumC0892OP(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
    }

    public EnumC0892OP getOp() {
        C0893UL op = this.header.getPack().getOp();
        EnumSet<EnumC0892OP> allOf = EnumSet.allOf(EnumC0892OP.class);
        Iterator i$ = allOf.iterator();
        while (i$.hasNext()) {
            EnumC0892OP op2 = (EnumC0892OP) i$.next();
            if (op.get(12) == op2.major && op.get(13) == op2.minor) {
                return op2;
            }
        }
        return EnumC0892OP.OPAtom;
    }

    private MXFDemuxerTrack[] findTracks() throws IOException {
        List<MXFDemuxerTrack> rt = new ArrayList<>();
        List<TimelineTrack> tracks = MXFUtil.findAllMeta(this.metadata, TimelineTrack.class);
        List<FileDescriptor> descriptors = MXFUtil.findAllMeta(this.metadata, FileDescriptor.class);
        for (TimelineTrack track : tracks) {
            if (track.getTrackNumber() != 0) {
                int trackNumber = track.getTrackNumber();
                FileDescriptor descriptor = findDescriptor(descriptors, track.getTrackId());
                if (descriptor == null) {
                    Logger.warn("No generic descriptor for track: " + track.getTrackId());
                    if (descriptors.size() == 1 && descriptors.get(0).getLinkedTrackId() == 0) {
                        descriptor = descriptors.get(0);
                    }
                }
                if (descriptor == null) {
                    Logger.warn("Track without descriptor: " + track.getTrackId());
                } else {
                    MXFDemuxerTrack dt = createTrack(new C0893UL(6, 14, 43, 52, 1, 2, 1, 1, 13, 1, 3, 1, (trackNumber >>> 24) & 255, (trackNumber >>> 16) & 255, (trackNumber >>> 8) & 255, trackNumber & 255), track, descriptor);
                    if (dt.getCodec() != null || (descriptor instanceof WaveAudioDescriptor)) {
                        rt.add(dt);
                    }
                }
            }
        }
        return (MXFDemuxerTrack[]) rt.toArray(new MXFDemuxerTrack[0]);
    }

    public static FileDescriptor findDescriptor(List<FileDescriptor> descriptors, int trackId) {
        for (FileDescriptor descriptor : descriptors) {
            if (descriptor.getLinkedTrackId() == trackId) {
                return descriptor;
            }
        }
        return null;
    }

    protected MXFDemuxerTrack createTrack(C0893UL ul, TimelineTrack track, GenericDescriptor descriptor) throws IOException {
        return new MXFDemuxerTrack(ul, track, descriptor);
    }

    public List<IndexSegment> getIndexes() {
        return this.indexSegments;
    }

    public List<MXFPartition> getEssencePartitions() {
        return this.partitions;
    }

    public TimecodeComponent getTimecode() {
        return this.timecode;
    }

    public void parseHeader(SeekableByteChannel ff) throws IOException {
        this.header = readHeaderPartition(ff);
        this.metadata = new ArrayList();
        this.partitions = new ArrayList();
        long nextPartition = ff.size();
        ff.position(this.header.getPack().getFooterPartition());
        do {
            long thisPartition = ff.position();
            KLV kl = KLV.readKL(ff);
            ByteBuffer fetchFrom = NIOUtils.fetchFrom(ff, (int) kl.len);
            this.header = MXFPartition.read(kl.key, fetchFrom, ff.position() - kl.offset, nextPartition);
            if (this.header.getPack().getNbEssenceContainers() > 0) {
                this.partitions.add(0, this.header);
            }
            this.metadata.addAll(0, readPartitionMeta(ff, this.header));
            ff.position(this.header.getPack().getPrevPartition());
            nextPartition = thisPartition;
        } while (this.header.getPack().getThisPartition() != 0);
    }

    public static List<MXFMetadata> readPartitionMeta(SeekableByteChannel ff, MXFPartition header) throws IOException {
        KLV kl;
        long basePos = ff.position();
        List<MXFMetadata> local = new ArrayList<>();
        ByteBuffer metaBuffer = NIOUtils.fetchFrom(ff, (int) Math.max(0L, header.getEssenceFilePos() - basePos));
        while (metaBuffer.hasRemaining() && (kl = KLV.readKL(metaBuffer, basePos)) != null) {
            MXFMetadata meta = parseMeta(kl.key, NIOUtils.read(metaBuffer, (int) kl.len));
            if (meta != null) {
                local.add(meta);
            }
        }
        return local;
    }

    public static MXFPartition readHeaderPartition(SeekableByteChannel ff) throws IOException {
        while (true) {
            KLV kl = KLV.readKL(ff);
            if (kl == null) {
                return null;
            }
            if (MXFConst.HEADER_PARTITION_KLV.equals(kl.key)) {
                ByteBuffer data = NIOUtils.fetchFrom(ff, (int) kl.len);
                MXFPartition header = MXFPartition.read(kl.key, data, ff.position() - kl.offset, 0L);
                return header;
            }
            ff.position(ff.position() + kl.len);
        }
    }

    private static MXFMetadata parseMeta(C0893UL ul, ByteBuffer _bb) {
        Class<? extends MXFMetadata> class1 = MXFConst.klMetadataMapping.get(ul);
        if (class1 == null) {
            Logger.warn("Unknown metadata piece: " + ul);
            return null;
        }
        try {
            MXFMetadata meta = class1.getConstructor(C0893UL.class).newInstance(ul);
            meta.read(_bb);
            return meta;
        } catch (Exception e) {
            Logger.warn("Unknown metadata piece: " + ul);
            return null;
        }
    }

    private void findIndex() {
        this.indexSegments = new ArrayList();
        for (MXFMetadata meta : this.metadata) {
            if (meta instanceof IndexSegment) {
                IndexSegment is = (IndexSegment) meta;
                this.indexSegments.add(is);
                this.totalFrames = (int) (this.totalFrames + is.getIndexDuration());
                this.duration += (is.getIndexEditRateDen() * is.getIndexDuration()) / is.getIndexEditRateNum();
            }
        }
    }

    public MXFDemuxerTrack[] getTracks() {
        return this.tracks;
    }

    public MXFDemuxerTrack getVideoTrack() {
        MXFDemuxerTrack[] arr$ = this.tracks;
        for (MXFDemuxerTrack track : arr$) {
            if (track.isVideo()) {
                return track;
            }
        }
        return null;
    }

    public MXFDemuxerTrack[] getAudioTracks() {
        List<MXFDemuxerTrack> audio = new ArrayList<>();
        MXFDemuxerTrack[] arr$ = this.tracks;
        for (MXFDemuxerTrack track : arr$) {
            if (track.isAudio()) {
                audio.add(track);
            }
        }
        return (MXFDemuxerTrack[]) audio.toArray(new MXFDemuxerTrack[0]);
    }

    /* loaded from: classes.dex */
    public class MXFDemuxerTrack implements SeekableDemuxerTrack {
        private boolean audio;
        private int audioFrameDuration;
        private int audioTimescale;
        private MXFConst.MXFCodecMapping codec;
        private int dataLen;
        private GenericDescriptor descriptor;
        private C0893UL essenceUL;
        private int frameNo;
        private int indexSegmentIdx;
        private int indexSegmentSubIdx;
        private long partEssenceOffset;
        private int partIdx;
        private long pts;
        private TimelineTrack track;
        private boolean video;

        public MXFDemuxerTrack(C0893UL essenceUL, TimelineTrack track, GenericDescriptor descriptor) throws IOException {
            this.essenceUL = essenceUL;
            this.track = track;
            this.descriptor = descriptor;
            if (descriptor instanceof GenericPictureEssenceDescriptor) {
                this.video = true;
            } else if (descriptor instanceof GenericSoundEssenceDescriptor) {
                this.audio = true;
            }
            this.codec = resolveCodec();
            if (this.codec != null || (descriptor instanceof WaveAudioDescriptor)) {
                Logger.warn("Track type: " + this.video + ", " + this.audio);
                if (this.audio && (descriptor instanceof WaveAudioDescriptor)) {
                    WaveAudioDescriptor wave = (WaveAudioDescriptor) descriptor;
                    cacheAudioFrameSizes(MXFDemuxer.this.f1550ch);
                    this.audioFrameDuration = this.dataLen / ((wave.getQuantizationBits() >> 3) * wave.getChannelCount());
                    this.audioTimescale = (int) wave.getAudioSamplingRate().scalar();
                }
            }
        }

        public boolean isAudio() {
            return this.audio;
        }

        public boolean isVideo() {
            return this.video;
        }

        public double getDuration() {
            return MXFDemuxer.this.duration;
        }

        public int getNumFrames() {
            return MXFDemuxer.this.totalFrames;
        }

        public String getName() {
            return this.track.getName();
        }

        private void cacheAudioFrameSizes(SeekableByteChannel ch) throws IOException {
            KLV kl;
            for (MXFPartition mxfPartition : MXFDemuxer.this.partitions) {
                if (mxfPartition.getEssenceLength() > 0) {
                    ch.position(mxfPartition.getEssenceFilePos());
                    do {
                        kl = KLV.readKL(ch);
                        if (kl == null) {
                            break;
                        } else {
                            ch.position(ch.position() + kl.len);
                        }
                    } while (!this.essenceUL.equals(kl.key));
                    if (this.essenceUL.equals(kl.key)) {
                        this.dataLen = (int) kl.len;
                        return;
                    }
                }
            }
        }

        @Override // org.jcodec.common.DemuxerTrack
        public Packet nextFrame() throws IOException {
            Packet result;
            if (this.indexSegmentIdx >= MXFDemuxer.this.indexSegments.size()) {
                return null;
            }
            IndexSegment seg = MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx);
            long[] off = seg.getIe().getFileOff();
            int erDen = seg.getIndexEditRateNum();
            int erNum = seg.getIndexEditRateDen();
            long frameEssenceOffset = off[this.indexSegmentSubIdx];
            byte toff = seg.getIe().getDisplayOff()[this.indexSegmentSubIdx];
            boolean kf = seg.getIe().getKeyFrameOff()[this.indexSegmentSubIdx] == 0;
            while (frameEssenceOffset >= this.partEssenceOffset + MXFDemuxer.this.partitions.get(this.partIdx).getEssenceLength() && this.partIdx < MXFDemuxer.this.partitions.size() - 1) {
                this.partEssenceOffset += MXFDemuxer.this.partitions.get(this.partIdx).getEssenceLength();
                this.partIdx++;
            }
            long frameFileOffset = (frameEssenceOffset - this.partEssenceOffset) + MXFDemuxer.this.partitions.get(this.partIdx).getEssenceFilePos();
            if (!this.audio) {
                int i = this.dataLen;
                long j = this.pts + (erNum * toff);
                int i2 = this.frameNo;
                this.frameNo = i2 + 1;
                result = readPacket(frameFileOffset, i, j, erDen, erNum, i2, kf);
                this.pts += erNum;
            } else {
                int i3 = this.dataLen;
                long j2 = this.pts;
                int i4 = this.audioTimescale;
                int i5 = this.audioFrameDuration;
                int i6 = this.frameNo;
                this.frameNo = i6 + 1;
                result = readPacket(frameFileOffset, i3, j2, i4, i5, i6, kf);
                this.pts += this.audioFrameDuration;
            }
            this.indexSegmentSubIdx++;
            if (this.indexSegmentSubIdx >= off.length) {
                this.indexSegmentIdx++;
                this.indexSegmentSubIdx = 0;
                if (this.dataLen == 0 && this.indexSegmentIdx < MXFDemuxer.this.indexSegments.size()) {
                    IndexSegment nseg = MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx);
                    this.pts = (this.pts * nseg.getIndexEditRateNum()) / erDen;
                    return result;
                }
                return result;
            }
            return result;
        }

        public MXFPacket readPacket(long off, int len, long pts, int timescale, int duration, int frameNo, boolean kf) throws IOException {
            MXFPacket mXFPacket;
            synchronized (MXFDemuxer.this.f1550ch) {
                MXFDemuxer.this.f1550ch.position(off);
                KLV kl = KLV.readKL(MXFDemuxer.this.f1550ch);
                while (kl != null && !this.essenceUL.equals(kl.key)) {
                    MXFDemuxer.this.f1550ch.position(MXFDemuxer.this.f1550ch.position() + kl.len);
                    kl = KLV.readKL(MXFDemuxer.this.f1550ch);
                }
                mXFPacket = (kl == null || !this.essenceUL.equals(kl.key)) ? null : new MXFPacket(NIOUtils.fetchFrom(MXFDemuxer.this.f1550ch, (int) kl.len), pts, timescale, duration, frameNo, kf, null, off, len);
            }
            return mXFPacket;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoFrame(long frameNo) {
            if (frameNo != this.frameNo) {
                this.indexSegmentSubIdx = (int) frameNo;
                this.indexSegmentIdx = 0;
                while (this.indexSegmentIdx < MXFDemuxer.this.indexSegments.size() && this.indexSegmentSubIdx >= MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx).getIndexDuration()) {
                    this.indexSegmentSubIdx = (int) (this.indexSegmentSubIdx - MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx).getIndexDuration());
                    this.indexSegmentIdx++;
                }
                this.indexSegmentSubIdx = Math.min(this.indexSegmentSubIdx, (int) MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx).getIndexDuration());
            }
            return true;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoSyncFrame(long frameNo) {
            if (!gotoFrame(frameNo)) {
                return false;
            }
            IndexSegment seg = MXFDemuxer.this.indexSegments.get(this.indexSegmentIdx);
            byte kfOff = seg.getIe().getKeyFrameOff()[this.indexSegmentSubIdx];
            return gotoFrame(kfOff + frameNo);
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public long getCurFrame() {
            return this.frameNo;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public void seek(double second) {
            throw new UnsupportedOperationException();
        }

        public C0893UL getEssenceUL() {
            return this.essenceUL;
        }

        public GenericDescriptor getDescriptor() {
            return this.descriptor;
        }

        public MXFConst.MXFCodecMapping getCodec() {
            return this.codec;
        }

        private MXFConst.MXFCodecMapping resolveCodec() {
            C0893UL codecUL;
            if (this.video) {
                codecUL = ((GenericPictureEssenceDescriptor) this.descriptor).getPictureEssenceCoding();
            } else {
                if (!this.audio) {
                    return null;
                }
                codecUL = ((GenericSoundEssenceDescriptor) this.descriptor).getSoundEssenceCompression();
            }
            Iterator i$ = EnumSet.allOf(MXFConst.MXFCodecMapping.class).iterator();
            while (i$.hasNext()) {
                MXFConst.MXFCodecMapping codec = (MXFConst.MXFCodecMapping) i$.next();
                if (codec.getUl().equals(codecUL, 65407)) {
                    return codec;
                }
            }
            Logger.warn("Unknown codec: " + codecUL);
            return null;
        }

        public int getTrackId() {
            return this.track.getTrackId();
        }

        @Override // org.jcodec.common.DemuxerTrack
        public DemuxerTrackMeta getMeta() {
            Size size = null;
            if (this.video) {
                GenericPictureEssenceDescriptor pd = (GenericPictureEssenceDescriptor) this.descriptor;
                size = new Size(pd.getStoredWidth(), pd.getStoredHeight());
            }
            return new DemuxerTrackMeta(this.video ? DemuxerTrackMeta.Type.VIDEO : this.audio ? DemuxerTrackMeta.Type.AUDIO : DemuxerTrackMeta.Type.OTHER, null, MXFDemuxer.this.totalFrames, MXFDemuxer.this.duration, size);
        }
    }

    /* loaded from: classes.dex */
    public static class MXFPacket extends Packet {
        private int len;
        private long offset;

        public MXFPacket(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean keyFrame, TapeTimecode tapeTimecode, long offset, int len) {
            super(data, pts, timescale, duration, frameNo, keyFrame, tapeTimecode);
            this.offset = offset;
            this.len = len;
        }

        public long getOffset() {
            return this.offset;
        }

        public int getLen() {
            return this.len;
        }
    }

    /* loaded from: classes.dex */
    public static class Fast extends MXFDemuxer {
        public Fast(SeekableByteChannel ch) throws IOException {
            super(ch);
        }

        @Override // org.jcodec.containers.mxf.MXFDemuxer
        public void parseHeader(SeekableByteChannel ff) throws IOException {
            this.partitions = new ArrayList();
            this.metadata = new ArrayList();
            this.header = readHeaderPartition(ff);
            this.metadata.addAll(readPartitionMeta(ff, this.header));
            this.partitions.add(this.header);
            ff.position(this.header.getPack().getFooterPartition());
            KLV kl = KLV.readKL(ff);
            ByteBuffer fetchFrom = NIOUtils.fetchFrom(ff, (int) kl.len);
            MXFPartition footer = MXFPartition.read(kl.key, fetchFrom, ff.position() - kl.offset, ff.size());
            this.metadata.addAll(readPartitionMeta(ff, footer));
        }
    }
}
