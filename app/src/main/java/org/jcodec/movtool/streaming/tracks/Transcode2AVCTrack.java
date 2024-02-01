package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.encode.H264FixedRateControl;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public abstract class Transcode2AVCTrack implements VirtualTrack {
    private static final int TARGET_RATE = 1024;
    private int frameSize;
    private int mbH;
    private int mbW;
    private int scaleFactor;

    /* renamed from: se */
    private CodecMeta f1570se;
    protected VirtualTrack src;
    private int thumbHeight;
    private int thumbWidth;
    private ThreadLocal<Transcoder> transcoders = new ThreadLocal<>();

    protected abstract void checkFourCC(VirtualTrack virtualTrack);

    protected abstract VideoDecoder getDecoder(int i);

    protected abstract int selectScaleFactor(Size size);

    public Transcode2AVCTrack(VirtualTrack src, Size frameDim) {
        checkFourCC(src);
        this.src = src;
        H264FixedRateControl rc = new H264FixedRateControl(1024);
        H264Encoder encoder = new H264Encoder(rc);
        this.scaleFactor = selectScaleFactor(frameDim);
        this.thumbWidth = frameDim.getWidth() >> this.scaleFactor;
        this.thumbHeight = (frameDim.getHeight() >> this.scaleFactor) & (-2);
        this.mbW = (this.thumbWidth + 15) >> 4;
        this.mbH = (this.thumbHeight + 15) >> 4;
        this.f1570se = createCodecMeta(src, encoder, this.thumbWidth, this.thumbHeight);
        this.frameSize = rc.calcFrameSize(this.mbW * this.mbH);
        this.frameSize += this.frameSize >> 4;
    }

    public static VideoCodecMeta createCodecMeta(VirtualTrack src, H264Encoder encoder, int thumbWidth, int thumbHeight) {
        VideoCodecMeta codecMeta = (VideoCodecMeta) src.getCodecMeta();
        AvcCBox createAvcC = H264Utils.createAvcC(encoder.initSPS(new Size(thumbWidth, thumbHeight)), encoder.initPPS(), 4);
        return new VideoCodecMeta("avc1", H264Utils.getAvcCData(createAvcC), new Size(thumbWidth, thumbHeight), codecMeta.getPasp());
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1570se;
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
            return Transcode2AVCTrack.this.frameSize;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            Transcoder t = (Transcoder) Transcode2AVCTrack.this.transcoders.get();
            if (t == null) {
                t = new Transcoder();
                Transcode2AVCTrack.this.transcoders.set(t);
            }
            ByteBuffer buf = ByteBuffer.allocate(Transcode2AVCTrack.this.frameSize);
            ByteBuffer data = this.src.getData();
            return t.transcodeFrame(data, buf);
        }
    }

    /* loaded from: classes.dex */
    class Transcoder {
        private VideoDecoder decoder;
        private Picture pic0;
        private Picture pic1;
        private Transform transform;

        /* renamed from: rc */
        private H264FixedRateControl f1571rc = new H264FixedRateControl(1024);
        private H264Encoder encoder = new H264Encoder(this.f1571rc);

        public Transcoder() {
            this.decoder = Transcode2AVCTrack.this.getDecoder(Transcode2AVCTrack.this.scaleFactor);
            this.pic0 = Picture.create(Transcode2AVCTrack.this.mbW << 4, (Transcode2AVCTrack.this.mbH + 1) << 4, ColorSpace.YUV444);
        }

        public ByteBuffer transcodeFrame(ByteBuffer src, ByteBuffer dst) throws IOException {
            if (src == null) {
                return null;
            }
            Picture decoded = this.decoder.decodeFrame(src, this.pic0.getData());
            if (this.pic1 == null) {
                this.pic1 = Picture.create(decoded.getWidth(), decoded.getHeight(), this.encoder.getSupportedColorSpaces()[0]);
                this.transform = ColorUtil.getTransform(decoded.getColor(), this.encoder.getSupportedColorSpaces()[0]);
            }
            this.transform.transform(decoded, this.pic1);
            this.pic1.setCrop(new Rect(0, 0, Transcode2AVCTrack.this.thumbWidth, Transcode2AVCTrack.this.thumbHeight));
            int rate = 1024;
            do {
                try {
                    this.encoder.encodeFrame(this.pic1, dst);
                    break;
                } catch (BufferOverflowException e) {
                    Logger.warn("Abandon frame, buffer too small: " + dst.capacity());
                    rate -= 10;
                    this.f1571rc.setRate(rate);
                }
            } while (rate > 10);
            this.f1571rc.setRate(1024);
            H264Utils.encodeMOVPacket(dst);
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
