package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.encode.H264FixedRateControl;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.mpeg12.Mpeg2Thumb2x2;
import org.jcodec.codecs.mpeg12.Mpeg2Thumb4x4;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.model.Size;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public class MPEGToAVCTranscoder {
    private VideoDecoder decoder;
    private Picture pic0;
    private Picture pic1;
    private int scaleFactor;
    private int thumbHeight;
    private int thumbWidth;
    private Transform transform;

    /* renamed from: rc */
    private H264FixedRateControl f1564rc = new H264FixedRateControl(1024);
    private H264Encoder encoder = new H264Encoder(this.f1564rc);

    public MPEGToAVCTranscoder(int scaleFactor) {
        this.scaleFactor = scaleFactor;
        this.decoder = getDecoder(scaleFactor);
    }

    protected VideoDecoder getDecoder(int scaleFactor) {
        switch (scaleFactor) {
            case 0:
                return new MPEGDecoder();
            case 1:
                return new Mpeg2Thumb4x4();
            case 2:
                return new Mpeg2Thumb2x2();
            default:
                throw new IllegalArgumentException("Unsupported scale factor: " + scaleFactor);
        }
    }

    public ByteBuffer transcodeFrame(ByteBuffer src, ByteBuffer dst, boolean iframe, int poc) throws IOException {
        Picture toEnc;
        if (src == null) {
            return null;
        }
        if (this.pic0 == null) {
            Size size = MPEGDecoder.getSize(src.duplicate());
            this.thumbWidth = size.getWidth() >> this.scaleFactor;
            this.thumbHeight = size.getHeight() >> this.scaleFactor;
            int mbW = (this.thumbWidth + 8) >> 4;
            int mbH = (this.thumbHeight + 8) >> 4;
            this.pic0 = Picture.create(mbW << 4, (mbH + 1) << 4, ColorSpace.YUV444);
        }
        Picture decoded = this.decoder.decodeFrame(src, this.pic0.getData());
        if (this.pic1 == null) {
            this.pic1 = Picture.create(decoded.getWidth(), decoded.getHeight(), this.encoder.getSupportedColorSpaces()[0]);
            this.transform = ColorUtil.getTransform(decoded.getColor(), this.encoder.getSupportedColorSpaces()[0]);
        }
        if (this.transform != null) {
            this.transform.transform(decoded, this.pic1);
            toEnc = this.pic1;
        } else {
            toEnc = decoded;
        }
        this.pic1.setCrop(new Rect(0, 0, this.thumbWidth, this.thumbHeight));
        int rate = 1024;
        do {
            try {
                this.encoder.encodeFrame(toEnc, dst, iframe, poc);
                break;
            } catch (BufferOverflowException e) {
                Logger.warn("Abandon frame, buffer too small: " + dst.capacity());
                rate -= 10;
                this.f1564rc.setRate(rate);
            }
        } while (rate > 10);
        this.f1564rc.setRate(1024);
        H264Utils.encodeMOVPacket(dst);
        return dst;
    }
}
