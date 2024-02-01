package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.FielExtension;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.PixelAspectExt;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;
import org.jcodec.containers.mp4.boxes.channel.ChannelUtils;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.FramesMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.PCMMP4DemuxerTrack;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class RealTrack implements VirtualTrack {
    private AbstractMP4DemuxerTrack demuxer;
    private MovieBox movie;
    private ByteChannelPool pool;
    private TrakBox trak;

    public RealTrack(MovieBox movie, TrakBox trak, ByteChannelPool pool) {
        SeekableByteChannel seekableByteChannel = null;
        this.movie = movie;
        SampleSizesBox stsz = (SampleSizesBox) Box.findFirst(trak, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        if (stsz.getDefaultSize() == 0) {
            this.demuxer = new FramesMP4DemuxerTrack(movie, trak, seekableByteChannel) { // from class: org.jcodec.movtool.streaming.tracks.RealTrack.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
                public ByteBuffer readPacketData(SeekableByteChannel ch, ByteBuffer buffer, long position, int size) throws IOException {
                    return buffer;
                }
            };
        } else {
            this.demuxer = new PCMMP4DemuxerTrack(movie, trak, seekableByteChannel) { // from class: org.jcodec.movtool.streaming.tracks.RealTrack.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
                public ByteBuffer readPacketData(SeekableByteChannel ch, ByteBuffer buffer, long position, int size) throws IOException {
                    return buffer;
                }
            };
        }
        this.trak = trak;
        this.pool = pool;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        MP4Packet pkt = this.demuxer.nextFrame(null);
        if (pkt == null) {
            return null;
        }
        return new RealPacket(pkt);
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        SampleEntry se = this.trak.getSampleEntries()[0];
        if (se instanceof VideoSampleEntry) {
            VideoSampleEntry vse = (VideoSampleEntry) se;
            PixelAspectExt pasp = (PixelAspectExt) Box.findFirst(se, PixelAspectExt.class, "pasp");
            FielExtension fiel = (FielExtension) Box.findFirst(se, FielExtension.class, "fiel");
            boolean interlace = false;
            boolean topField = false;
            if (fiel != null) {
                interlace = fiel.isInterlaced();
                topField = fiel.topFieldFirst();
            }
            return new VideoCodecMeta(se.getFourcc(), extractVideoCodecPrivate(se), new Size(vse.getWidth(), vse.getHeight()), pasp != null ? pasp.getRational() : null, interlace, topField);
        }
        if (se instanceof AudioSampleEntry) {
            AudioSampleEntry ase = (AudioSampleEntry) se;
            ByteBuffer codecPrivate = null;
            if ("mp4a".equals(ase.getFourcc())) {
                LeafBox lb = (LeafBox) Box.findFirst(se, LeafBox.class, "esds");
                if (lb == null) {
                    lb = (LeafBox) Box.findFirst(se, LeafBox.class, null, "esds");
                }
                codecPrivate = lb.getData();
            }
            return new AudioCodecMeta(se.getFourcc(), ase.calcSampleSize(), ase.getChannelCount(), (int) ase.getSampleRate(), ase.getEndian(), ase.isPCM(), ChannelUtils.getLabels(ase), codecPrivate);
        }
        throw new RuntimeException("Sample entry '" + se.getFourcc() + "' is not supported.");
    }

    private ByteBuffer extractVideoCodecPrivate(SampleEntry se) {
        if (!"avc1".equals(se.getFourcc())) {
            return null;
        }
        LeafBox leaf = (LeafBox) Box.findFirst(se, LeafBox.class, "avcC");
        return leaf.getData();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() {
        this.pool.close();
    }

    /* loaded from: classes.dex */
    public class RealPacket implements VirtualPacket {
        private MP4Packet packet;

        public RealPacket(MP4Packet nextFrame) {
            this.packet = nextFrame;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(this.packet.getSize());
            SeekableByteChannel ch = null;
            try {
                ch = RealTrack.this.pool.getChannel();
                if (this.packet.getFileOff() >= ch.size()) {
                    bb = null;
                } else {
                    ch.position(this.packet.getFileOff());
                    ch.read(bb);
                    bb.flip();
                    if (ch != null) {
                        ch.close();
                    }
                }
                return bb;
            } finally {
                if (ch != null) {
                    ch.close();
                }
            }
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return this.packet.getSize();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.packet.getMediaPts() / this.packet.getTimescale();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return this.packet.getDuration() / this.packet.getTimescale();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return this.packet.isKeyFrame() || this.packet.isPsync();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return (int) this.packet.getFrameNo();
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        List<Edit> edits = this.demuxer.getEdits();
        if (edits == null) {
            return null;
        }
        VirtualTrack.VirtualEdit[] result = new VirtualTrack.VirtualEdit[edits.size()];
        for (int i = 0; i < edits.size(); i++) {
            Edit ee = edits.get(i);
            result[i] = new VirtualTrack.VirtualEdit(ee.getMediaTime() / this.trak.getTimescale(), ee.getDuration() / this.movie.getTimescale());
        }
        return result;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return (int) this.demuxer.getTimescale();
    }
}
