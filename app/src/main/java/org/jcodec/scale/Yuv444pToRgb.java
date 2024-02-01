package org.jcodec.scale;

import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv444pToRgb implements Transform {
    private int downShift;
    private int upShift;

    public Yuv444pToRgb(int downShift, int upShift) {
        this.downShift = downShift;
        this.upShift = upShift;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] y = src.getPlaneData(0);
        int[] u = src.getPlaneData(1);
        int[] v = src.getPlaneData(2);
        int[] data = dst.getPlaneData(0);
        int srcOff = 0;
        int dstOff = 0;
        for (int i = 0; i < dst.getHeight(); i++) {
            int j = 0;
            while (j < dst.getWidth()) {
                YUV444toRGB888((y[srcOff] << this.upShift) >> this.downShift, (u[srcOff] << this.upShift) >> this.downShift, (v[srcOff] << this.upShift) >> this.downShift, data, dstOff);
                j++;
                srcOff++;
                dstOff += 3;
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
