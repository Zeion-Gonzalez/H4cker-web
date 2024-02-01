package org.jcodec.containers.mxf.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.channel.Label;
import org.jcodec.containers.mxf.MXFConst;
import org.jcodec.containers.mxf.MXFDemuxer;
import org.jcodec.containers.mxf.model.C0893UL;
import org.jcodec.containers.mxf.model.GenericDescriptor;
import org.jcodec.containers.mxf.model.GenericPictureEssenceDescriptor;
import org.jcodec.containers.mxf.model.GenericSoundEssenceDescriptor;
import org.jcodec.containers.mxf.model.KLV;
import org.jcodec.containers.mxf.model.TimelineTrack;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;
import org.jcodec.movtool.streaming.tracks.ByteChannelPool;

/* loaded from: classes.dex */
public class MXFVirtualTrack implements VirtualTrack {
    private C0893UL essenceUL;

    /* renamed from: fp */
    private ByteChannelPool f1555fp;
    private MXFDemuxer.MXFDemuxerTrack track;

    public MXFVirtualTrack(MXFDemuxer.MXFDemuxerTrack track, ByteChannelPool fp) throws IOException {
        this.f1555fp = fp;
        this.track = track;
        this.essenceUL = track.getEssenceUL();
    }

    public static MXFDemuxer createDemuxer(SeekableByteChannel channel) throws IOException {
        return new PatchedMXFDemuxer(channel);
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        MXFDemuxer.MXFPacket nextFrame = (MXFDemuxer.MXFPacket) this.track.nextFrame();
        if (nextFrame == null) {
            return null;
        }
        return new MXFVirtualPacket(nextFrame);
    }

    /* loaded from: classes.dex */
    public class MXFVirtualPacket implements VirtualPacket {
        private MXFDemuxer.MXFPacket pkt;

        public MXFVirtualPacket(MXFDemuxer.MXFPacket pkt) {
            this.pkt = pkt;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            SeekableByteChannel ch = null;
            try {
                ch = MXFVirtualTrack.this.f1555fp.getChannel();
                ch.position(this.pkt.getOffset());
                KLV kl = KLV.readKL(ch);
                while (kl != null && !MXFVirtualTrack.this.essenceUL.equals(kl.key)) {
                    ch.position(ch.position() + kl.len);
                    kl = KLV.readKL(ch);
                }
                return (kl == null || !MXFVirtualTrack.this.essenceUL.equals(kl.key)) ? null : NIOUtils.fetchFrom(ch, (int) kl.len);
            } finally {
                NIOUtils.closeQuietly(ch);
            }
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() throws IOException {
            return this.pkt.getLen();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.pkt.getPtsD();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return this.pkt.getDurationD();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return this.pkt.isKeyFrame();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return (int) this.pkt.getFrameNo();
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return toSampleEntry(this.track.getDescriptor());
    }

    private CodecMeta toSampleEntry(GenericDescriptor d) {
        if (this.track.isVideo()) {
            GenericPictureEssenceDescriptor ped = (GenericPictureEssenceDescriptor) d;
            Rational ar = ped.getAspectRatio();
            return new VideoCodecMeta(MP4Util.getFourcc(this.track.getCodec().getCodec()), null, new Size(ped.getDisplayWidth(), ped.getDisplayHeight()), new Rational(((ar.getNum() * 1000) * ped.getDisplayHeight()) / (ar.getDen() * ped.getDisplayWidth()), 1000));
        }
        if (this.track.isAudio()) {
            GenericSoundEssenceDescriptor sed = (GenericSoundEssenceDescriptor) d;
            int sampleSize = sed.getQuantizationBits() >> 3;
            MXFConst.MXFCodecMapping codec = this.track.getCodec();
            Label[] labels = new Label[sed.getChannelCount()];
            Arrays.fill(labels, Label.Mono);
            return new AudioCodecMeta(sampleSize == 3 ? "in24" : "sowt", sampleSize, sed.getChannelCount(), (int) sed.getAudioSamplingRate().scalar(), codec == MXFConst.MXFCodecMapping.PCM_S16BE ? EndianBox.Endian.BIG_ENDIAN : EndianBox.Endian.LITTLE_ENDIAN, true, labels, null);
        }
        throw new RuntimeException("Can't get sample entry");
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return -1;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() {
        this.f1555fp.close();
    }

    /* loaded from: classes.dex */
    public static class PatchedMXFDemuxer extends MXFDemuxer {
        public PatchedMXFDemuxer(SeekableByteChannel ch) throws IOException {
            super(ch);
        }

        @Override // org.jcodec.containers.mxf.MXFDemuxer
        protected MXFDemuxer.MXFDemuxerTrack createTrack(C0893UL ul, TimelineTrack track, GenericDescriptor descriptor) throws IOException {
            return new MXFDemuxer.MXFDemuxerTrack(ul, track, descriptor) { // from class: org.jcodec.containers.mxf.streaming.MXFVirtualTrack.PatchedMXFDemuxer.1
                @Override // org.jcodec.containers.mxf.MXFDemuxer.MXFDemuxerTrack
                public MXFDemuxer.MXFPacket readPacket(long off, int len, long pts, int timescale, int duration, int frameNo, boolean kf) throws IOException {
                    return new MXFDemuxer.MXFPacket(null, pts, timescale, duration, frameNo, kf, null, off, len);
                }
            };
        }
    }

    public int getTrackId() {
        return this.track.getTrackId();
    }
}
