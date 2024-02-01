package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv420pToRgb implements Transform {
    private final int downShift;
    private final int upShift;

    public Yuv420pToRgb(int upShift, int downShift) {
        this.upShift = upShift;
        this.downShift = downShift;
    }

    @Override // org.jcodec.scale.Transform
    public final void transform(Picture src, Picture dst) {
        int[] y = src.getPlaneData(0);
        int[] u = src.getPlaneData(1);
        int[] v = src.getPlaneData(2);
        int[] data = dst.getPlaneData(0);
        int offLuma = 0;
        int offChroma = 0;
        int stride = dst.getWidth();
        for (int i = 0; i < (dst.getHeight() >> 1); i++) {
            for (int k = 0; k < (dst.getWidth() >> 1); k++) {
                int j = k << 1;
                Yuv422pToRgb.YUV444toRGB888((y[offLuma + j] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j) * 3);
                Yuv422pToRgb.YUV444toRGB888((y[(offLuma + j) + 1] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j + 1) * 3);
                Yuv422pToRgb.YUV444toRGB888((y[(offLuma + j) + stride] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j + stride) * 3);
                Yuv422pToRgb.YUV444toRGB888((y[((offLuma + j) + stride) + 1] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j + stride + 1) * 3);
                offChroma++;
            }
            if ((dst.getWidth() & 1) != 0) {
                int j2 = dst.getWidth() - 1;
                Yuv422pToRgb.YUV444toRGB888((y[offLuma + j2] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j2) * 3);
                Yuv422pToRgb.YUV444toRGB888((y[(offLuma + j2) + stride] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j2 + stride) * 3);
                offChroma++;
            }
            offLuma += stride * 2;
        }
        if ((dst.getHeight() & 1) != 0) {
            for (int k2 = 0; k2 < (dst.getWidth() >> 1); k2++) {
                int j3 = k2 << 1;
                Yuv422pToRgb.YUV444toRGB888((y[offLuma + j3] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j3) * 3);
                Yuv422pToRgb.YUV444toRGB888((y[(offLuma + j3) + 1] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j3 + 1) * 3);
                offChroma++;
            }
            if ((dst.getWidth() & 1) != 0) {
                int j4 = dst.getWidth() - 1;
                Yuv422pToRgb.YUV444toRGB888((y[offLuma + j4] << this.upShift) >> this.downShift, (u[offChroma] << this.upShift) >> this.downShift, (v[offChroma] << this.upShift) >> this.downShift, data, (offLuma + j4) * 3);
                int i2 = offChroma + 1;
            }
        }
    }
}
