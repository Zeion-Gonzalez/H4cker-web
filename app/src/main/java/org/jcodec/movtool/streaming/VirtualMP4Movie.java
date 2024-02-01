package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jcodec.containers.mp4.Brand;

/* loaded from: classes.dex */
public class VirtualMP4Movie extends VirtualMovie {
    private Brand brand;

    public VirtualMP4Movie(VirtualTrack... tracks) throws IOException {
        this(Brand.MP4, tracks);
    }

    public VirtualMP4Movie(Brand brand, VirtualTrack... tracks) throws IOException {
        super(tracks);
        this.brand = brand;
        muxTracks();
    }

    @Override // org.jcodec.movtool.streaming.VirtualMovie
    protected MovieSegment headerChunk(List<MovieSegment> ch, VirtualTrack[] tracks, long dataSize) throws IOException {
        PacketChunk[] chunks = (PacketChunk[]) ch.toArray(new PacketChunk[0]);
        int headerSize = MovieHelper.produceHeader(chunks, tracks, dataSize, this.brand).remaining();
        for (PacketChunk chunk : chunks) {
            chunk.offset(headerSize);
        }
        final ByteBuffer header = MovieHelper.produceHeader(chunks, tracks, dataSize, this.brand);
        return new MovieSegment() { // from class: org.jcodec.movtool.streaming.VirtualMP4Movie.1
            @Override // org.jcodec.movtool.streaming.MovieSegment
            public ByteBuffer getData() {
                return header.duplicate();
            }

            @Override // org.jcodec.movtool.streaming.MovieSegment
            public int getNo() {
                return 0;
            }

            @Override // org.jcodec.movtool.streaming.MovieSegment
            public long getPos() {
                return 0L;
            }

            @Override // org.jcodec.movtool.streaming.MovieSegment
            public int getDataLen() {
                return header.remaining();
            }
        };
    }

    @Override // org.jcodec.movtool.streaming.VirtualMovie
    protected MovieSegment packetChunk(VirtualTrack track, VirtualPacket pkt, int chunkNo, int trackNo, long pos) {
        return new PacketChunk(pkt, trackNo, chunkNo, pos, track.getCodecMeta().getFourcc());
    }

    /* loaded from: classes.dex */
    public class PacketChunk implements MovieSegment {
        private String fourcc;

        /* renamed from: no */
        private int f1560no;
        private VirtualPacket packet;
        private long pos;
        private int track;

        public PacketChunk(VirtualPacket packet, int track, int no, long pos, String fourcc) {
            this.packet = packet;
            this.track = track;
            this.f1560no = no;
            this.pos = pos;
            this.fourcc = fourcc;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public ByteBuffer getData() throws IOException {
            ByteBuffer buf = this.packet.getData().duplicate();
            return buf;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getNo() {
            return this.f1560no;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public long getPos() {
            return this.pos;
        }

        public void offset(int off) {
            this.pos += off;
        }

        @Override // org.jcodec.movtool.streaming.MovieSegment
        public int getDataLen() throws IOException {
            return this.packet.getDataLen();
        }

        public VirtualPacket getPacket() {
            return this.packet;
        }

        public int getTrackNo() {
            return this.track;
        }
    }
}
