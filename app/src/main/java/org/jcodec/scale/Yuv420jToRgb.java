package org.jcodec.scale;

import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class Yuv420jToRgb implements Transform {
    private static final int ONE_HALF = 512;
    private static final int SCALEBITS = 10;
    private static final int FIX_0_71414 = FIX(0.71414d);
    private static final int FIX_1_772 = FIX(1.772d);
    private static final int _FIX_0_34414 = -FIX(0.34414d);
    private static final int FIX_1_402 = FIX(1.402d);

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
                YUVJtoRGB(y[offLuma + j], u[offChroma], v[offChroma], data, (offLuma + j) * 3);
                YUVJtoRGB(y[offLuma + j + 1], u[offChroma], v[offChroma], data, (offLuma + j + 1) * 3);
                YUVJtoRGB(y[offLuma + j + stride], u[offChroma], v[offChroma], data, (offLuma + j + stride) * 3);
                YUVJtoRGB(y[offLuma + j + stride + 1], u[offChroma], v[offChroma], data, (offLuma + j + stride + 1) * 3);
                offChroma++;
            }
            if ((dst.getWidth() & 1) != 0) {
                int j2 = dst.getWidth() - 1;
                YUVJtoRGB(y[offLuma + j2], u[offChroma], v[offChroma], data, (offLuma + j2) * 3);
                YUVJtoRGB(y[offLuma + j2 + stride], u[offChroma], v[offChroma], data, (offLuma + j2 + stride) * 3);
                offChroma++;
            }
            offLuma += stride * 2;
        }
        if ((dst.getHeight() & 1) != 0) {
            for (int k2 = 0; k2 < (dst.getWidth() >> 1); k2++) {
                int j3 = k2 << 1;
                YUVJtoRGB(y[offLuma + j3], u[offChroma], v[offChroma], data, (offLuma + j3) * 3);
                YUVJtoRGB(y[offLuma + j3 + 1], u[offChroma], v[offChroma], data, (offLuma + j3 + 1) * 3);
                offChroma++;
            }
            if ((dst.getWidth() & 1) != 0) {
                int j4 = dst.getWidth() - 1;
                YUVJtoRGB(y[offLuma + j4], u[offChroma], v[offChroma], data, (offLuma + j4) * 3);
                int i2 = offChroma + 1;
            }
        }
    }

    private static final int FIX(double x) {
        return (int) ((1024.0d * x) + 0.5d);
    }

    public static final void YUVJtoRGB(int y, int cb, int cr, int[] data, int off) {
        int y2 = y << 10;
        int cb2 = cb - 128;
        int cr2 = cr - 128;
        int add_r = (FIX_1_402 * cr2) + 512;
        int add_g = ((_FIX_0_34414 * cb2) - (FIX_0_71414 * cr2)) + 512;
        int add_b = (FIX_1_772 * cb2) + 512;
        int r = (y2 + add_r) >> 10;
        int g = (y2 + add_g) >> 10;
        int b = (y2 + add_b) >> 10;
        data[off] = MathUtil.clip(b, 0, 255);
        data[off + 1] = MathUtil.clip(g, 0, 255);
        data[off + 2] = MathUtil.clip(r, 0, 255);
    }
}
