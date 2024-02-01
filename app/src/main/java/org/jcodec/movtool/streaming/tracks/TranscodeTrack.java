package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.VideoEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Rect;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public abstract class TranscodeTrack implements VirtualTrack {
    private static final int TARGET_RATE = 1024;
    private int frameSize;
    private int mbH;
    private int mbW;
    private int scaleFactor;

    /* renamed from: se */
    private CodecMeta f1572se;
    private VirtualTrack src;
    private int thumbHeight;
    private int thumbWidth;
    private ThreadLocal<Transcoder> transcoders = new ThreadLocal<>();

    protected abstract void getCodecPrivate(ByteBuffer byteBuffer, Size size);

    protected abstract VideoDecoder getDecoder(int i);

    protected abstract VideoEncoder getEncoder(int i);

    protected abstract int getFrameSize(int i, int i2);

    public TranscodeTrack(VirtualTrack proresTrack, Size frameDim) {
        this.src = proresTrack;
        this.scaleFactor = frameDim.getWidth() >= 960 ? 2 : 1;
        this.thumbWidth = frameDim.getWidth() >> this.scaleFactor;
        this.thumbHeight = (frameDim.getHeight() >> this.scaleFactor) & (-2);
        this.mbW = (this.thumbWidth + 15) >> 4;
        this.mbH = (this.thumbHeight + 15) >> 4;
        Size size = new Size(this.thumbWidth, this.thumbHeight);
        Rational pasp = ((VideoCodecMeta) proresTrack.getCodecMeta()).getPasp();
        ByteBuffer codecPrivate = ByteBuffer.allocate(1024);
        getCodecPrivate(codecPrivate, size);
        this.f1572se = new VideoCodecMeta("avc1", codecPrivate, size, pasp);
        this.frameSize = getFrameSize(this.mbW * this.mbH, 1024);
        this.frameSize += this.frameSize >> 4;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1572se;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        VirtualPacket nextPacket = this.src.nextPacket();
        if (nextPacket == null) {
            return null;
        }
        return new TranscodePacket(nextPacket);
    }

    /* loaded from: classes.dex */
    private class TranscodePacket extends VirtualPacketWrapper {
        public TranscodePacket(VirtualPacket nextPacket) {
            super(nextPacket);
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return TranscodeTrack.this.frameSize;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            Transcoder t = (Transcoder) TranscodeTrack.this.transcoders.get();
            if (t == null) {
                t = new Transcoder();
                TranscodeTrack.this.transcoders.set(t);
            }
            ByteBuffer buf = ByteBuffer.allocate(TranscodeTrack.this.frameSize);
            ByteBuffer data = this.src.getData();
            return t.transcodeFrame(data, buf);
        }
    }

    /* loaded from: classes.dex */
    class Transcoder {
        private VideoDecoder decoder;
        private VideoEncoder[] encoder = new VideoEncoder[3];
        private Picture pic0;
        private Picture pic1;
        private Transform transform;

        public Transcoder() {
            this.decoder = TranscodeTrack.this.getDecoder(TranscodeTrack.this.scaleFactor);
            this.encoder[0] = TranscodeTrack.this.getEncoder(1024);
            this.encoder[1] = TranscodeTrack.this.getEncoder(921);
            this.encoder[2] = TranscodeTrack.this.getEncoder(819);
            this.pic0 = Picture.create(TranscodeTrack.this.mbW << 4, TranscodeTrack.this.mbH << 4, ColorSpace.YUV444);
        }

        public ByteBuffer transcodeFrame(ByteBuffer src, ByteBuffer dst) {
            Picture decoded = this.decoder.decodeFrame(src, this.pic0.getData());
            if (this.pic1 == null) {
                this.pic1 = Picture.create(decoded.getWidth(), decoded.getHeight(), ColorSpace.YUV420);
                this.transform = ColorUtil.getTransform(decoded.getColor(), ColorSpace.YUV420);
            }
            this.transform.transform(decoded, this.pic1);
            this.pic1.setCrop(new Rect(0, 0, TranscodeTrack.this.thumbWidth, TranscodeTrack.this.thumbHeight));
            for (int i = 0; i < this.encoder.length; i++) {
                try {
                    dst.clear();
                    this.encoder[i].encodeFrame(this.pic1, dst);
                    break;
                } catch (BufferOverflowException e) {
                    System.out.println("Abandon frame!!!");
                }
            }
            return dst;
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return this.src.getEdits();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }
}
