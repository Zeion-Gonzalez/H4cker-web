package org.jcodec.scale;

import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv422pToRgb implements Transform {
    private int downShift;
    private int upShift;

    public Yuv422pToRgb(int downShift, int upShift) {
        this.downShift = downShift;
        this.upShift = upShift;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] y = src.getPlaneData(0);
        int[] u = src.getPlaneData(1);
        int[] v = src.getPlaneData(2);
        int[] data = dst.getPlaneData(0);
        int offLuma = 0;
        int offChroma = 0;
        for (int i = 0; i < dst.getHeight(); i++) {
            for (int j = 0; j < dst.getWidth(); j += 2) {
                YUV444toRGB888((y[offLuma] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, offLuma * 3);
                YUV444toRGB888((y[offLuma + 1] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + 1) * 3);
                offLuma += 2;
                offChroma++;
            }
        }
    }

    public static final void YUV444toRGB888(int y, int u, int v, int[] data, int off) {
        int c = y - 16;
        int d = u - 128;
        int e = v - 128;
        int r = (((c * 298) + (e * 409)) + 128) >> 8;
        int g = ((((c * 298) - (d * 100)) - (e * JpegConst.RST0)) + 128) >> 8;
        int b = (((c * 298) + (d * 516)) + 128) >> 8;
        data[off] = crop(b);
        data[off + 1] = crop(g);
        data[off + 2] = crop(r);
    }

    private static int crop(int val) {
        if (val < 0) {
            return 0;
        }
        if (val > 255) {
            return 255;
        }
        return val;
    }
}
