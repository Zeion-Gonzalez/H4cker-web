package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv422pToYuv420j implements Transform {
    public static int COEFF = 9362;
    private int halfDst;
    private int halfSrc;
    private int shift;

    public Yuv422pToYuv420j(int upshift, int downshift) {
        this.shift = (downshift + 13) - upshift;
        if (this.shift < 0) {
            throw new IllegalArgumentException("Maximum upshift allowed: " + (downshift + 13));
        }
        this.halfSrc = 128 << Math.max(downshift - upshift, 0);
        this.halfDst = 128 << Math.max(upshift - downshift, 0);
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] sy = src.getPlaneData(0);
        int[] dy = dst.getPlaneData(0);
        for (int i = 0; i < src.getPlaneWidth(0) * src.getPlaneHeight(0); i++) {
            dy[i] = ((sy[i] - 16) * COEFF) >> this.shift;
        }
        copyAvg(src.getPlaneData(1), dst.getPlaneData(1), src.getPlaneWidth(1), src.getPlaneHeight(1));
        copyAvg(src.getPlaneData(2), dst.getPlaneData(2), src.getPlaneWidth(2), src.getPlaneHeight(2));
    }

    private void copyAvg(int[] src, int[] dst, int width, int height) {
        int offSrc = 0;
        int offDst = 0;
        for (int y = 0; y < height / 2; y++) {
            int x = 0;
            while (x < width) {
                int a = (((src[offSrc] - this.halfSrc) * COEFF) >> this.shift) + this.halfDst;
                int b = (((src[offSrc + width] - this.halfSrc) * COEFF) >> this.shift) + this.halfDst;
                dst[offDst] = ((a + b) + 1) >> 1;
                x++;
                offDst++;
                offSrc++;
            }
            offSrc += width;
        }
    }
}
