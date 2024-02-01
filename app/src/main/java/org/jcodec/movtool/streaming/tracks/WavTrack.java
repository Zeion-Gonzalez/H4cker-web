package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.codecs.wav.WavHeader;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.boxes.channel.Label;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class WavTrack implements VirtualTrack {
    public static final int FRAMES_PER_PKT = 1024;
    private int frameNo;
    private WavHeader header;
    private long offset;
    private int pktDataLen;
    private double pktDuration;
    private ByteChannelPool pool;
    private double pts;

    /* renamed from: se */
    private AudioCodecMeta f1573se;
    private long size;

    public WavTrack(ByteChannelPool pool, Label... labels) throws IOException {
        this.pool = pool;
        SeekableByteChannel ch = null;
        try {
            ch = pool.getChannel();
            this.header = WavHeader.read(ch);
            this.size = this.header.dataSize <= 0 ? ch.size() : this.header.dataSize;
            ch.close();
            this.f1573se = new AudioCodecMeta("sowt", ByteBuffer.allocate(0), new AudioFormat(this.header.fmt.sampleRate, this.header.fmt.bitsPerSample >> 3, this.header.fmt.numChannels, true, false), true, labels);
            this.pktDataLen = this.header.fmt.numChannels * 1024 * (this.header.fmt.bitsPerSample >> 3);
            this.pktDuration = 1024.0d / this.header.fmt.sampleRate;
            this.offset = this.header.dataOffset;
            this.pts = 0.0d;
            this.frameNo = 0;
        } catch (Throwable th) {
            ch.close();
            throw th;
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        if (this.offset >= this.size) {
            return null;
        }
        WavPacket wavPacket = new WavPacket(this.frameNo, this.pts, this.offset, (int) Math.min(this.size - this.offset, this.pktDataLen));
        this.offset += this.pktDataLen;
        this.frameNo += 1024;
        this.pts = this.frameNo / this.header.fmt.sampleRate;
        return wavPacket;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1573se;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.header.fmt.sampleRate;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.pool.close();
    }

    /* loaded from: classes.dex */
    public class WavPacket implements VirtualPacket {
        private int dataLen;
        private int frameNo;
        private long offset;
        private double pts;

        public WavPacket(int frameNo, double pts, long offset, int dataLen) {
            this.frameNo = frameNo;
            this.pts = pts;
            this.offset = offset;
            this.dataLen = dataLen;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            SeekableByteChannel ch = null;
            try {
                ch = WavTrack.this.pool.getChannel();
                ch.position(this.offset);
                ByteBuffer buffer = ByteBuffer.allocate(this.dataLen);
                NIOUtils.read(ch, buffer);
                buffer.flip();
                return buffer;
            } finally {
                ch.close();
            }
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() throws IOException {
            return this.dataLen;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.pts;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return WavTrack.this.pktDuration;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return true;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return this.frameNo;
        }
    }
}
