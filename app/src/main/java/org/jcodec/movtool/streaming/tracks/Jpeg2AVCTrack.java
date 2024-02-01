package org.jcodec.movtool.streaming.tracks;

import org.jcodec.codecs.mjpeg.JpegDecoder;
import org.jcodec.codecs.mjpeg.JpegToThumb2x2;
import org.jcodec.codecs.mjpeg.JpegToThumb4x4;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class Jpeg2AVCTrack extends Transcode2AVCTrack {
    public Jpeg2AVCTrack(VirtualTrack proresTrack, Size frameDim) {
        super(proresTrack, frameDim);
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected void checkFourCC(VirtualTrack jpegTrack) {
        String fourcc = jpegTrack.getCodecMeta().getFourcc();
        if ("jpeg".equals(fourcc) || "mjpa".equals(fourcc)) {
        } else {
            throw new IllegalArgumentException("Input track is not Jpeg");
        }
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected int selectScaleFactor(Size frameDim) {
        if (frameDim.getWidth() >= 960) {
            return 2;
        }
        return frameDim.getWidth() > 480 ? 1 : 0;
    }

    @Override // org.jcodec.movtool.streaming.tracks.Transcode2AVCTrack
    protected VideoDecoder getDecoder(int scaleFactor) {
        VideoCodecMeta meta = (VideoCodecMeta) this.src.getCodecMeta();
        switch (scaleFactor) {
            case 0:
                return new JpegDecoder(meta.isInterlaced(), meta.isTopFieldFirst());
            case 1:
                return new JpegToThumb4x4(meta.isInterlaced(), meta.isTopFieldFirst());
            case 2:
                return new JpegToThumb2x2(meta.isInterlaced(), meta.isTopFieldFirst());
            default:
                throw new IllegalArgumentException("Unsupported scale factor: " + scaleFactor);
        }
    }
}
