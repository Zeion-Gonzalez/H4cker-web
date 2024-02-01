package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv444jToYuv420p implements Transform {
    public static int Y_COEFF = 7168;

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] sy = src.getPlaneData(0);
        int[] dy = dst.getPlaneData(0);
        for (int i = 0; i < src.getPlaneWidth(0) * src.getPlaneHeight(0); i++) {
            dy[i] = ((sy[i] * Y_COEFF) >> 13) + 16;
        }
        copyAvg(src.getPlaneData(1), dst.getPlaneData(1), src.getPlaneWidth(1), src.getPlaneHeight(1));
        copyAvg(src.getPlaneData(2), dst.getPlaneData(2), src.getPlaneWidth(2), src.getPlaneHeight(2));
    }

    private void copyAvg(int[] src, int[] dst, int width, int height) {
        int offSrc = 0;
        int offDst = 0;
        for (int y = 0; y < (height >> 1); y++) {
            int x = 0;
            while (x < width) {
                int a = (((src[offSrc] - 128) * Y_COEFF) >> 13) + 128;
                int b = (((src[offSrc + 1] - 128) * Y_COEFF) >> 13) + 128;
                int c = (((src[offSrc + width] - 128) * Y_COEFF) >> 13) + 128;
                int d = (((src[(offSrc + width) + 1] - 128) * Y_COEFF) >> 13) + 128;
                dst[offDst] = ((((a + b) + c) + d) + 2) >> 2;
                x += 2;
                offDst++;
                offSrc += 2;
            }
            offSrc += width;
        }
    }
}
