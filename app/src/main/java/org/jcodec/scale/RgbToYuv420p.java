package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class RgbToYuv420p implements Transform {
    private int downShift;
    private int downShiftChr;
    private int upShift;

    public RgbToYuv420p(int upShift, int downShift) {
        this.upShift = upShift;
        this.downShift = downShift;
        this.downShiftChr = downShift + 2;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture img, Picture dst) {
        int[] y = img.getData()[0];
        int[][] dstData = dst.getData();
        int offChr = 0;
        int offLuma = 0;
        int offSrc = 0;
        int strideSrc = img.getWidth() * 3;
        int strideDst = dst.getWidth();
        for (int i = 0; i < (img.getHeight() >> 1); i++) {
            for (int j = 0; j < (img.getWidth() >> 1); j++) {
                dstData[1][offChr] = 0;
                dstData[2][offChr] = 0;
                rgb2yuv(y[offSrc], y[offSrc + 1], y[offSrc + 2], dstData[0], offLuma, dstData[1], offChr, dstData[2], offChr);
                dstData[0][offLuma] = (dstData[0][offLuma] << this.upShift) >> this.downShift;
                rgb2yuv(y[offSrc + strideSrc], y[offSrc + strideSrc + 1], y[offSrc + strideSrc + 2], dstData[0], offLuma + strideDst, dstData[1], offChr, dstData[2], offChr);
                dstData[0][offLuma + strideDst] = (dstData[0][offLuma + strideDst] << this.upShift) >> this.downShift;
                int offLuma2 = offLuma + 1;
                rgb2yuv(y[offSrc + 3], y[offSrc + 4], y[offSrc + 5], dstData[0], offLuma2, dstData[1], offChr, dstData[2], offChr);
                dstData[0][offLuma2] = (dstData[0][offLuma2] << this.upShift) >> this.downShift;
                rgb2yuv(y[offSrc + strideSrc + 3], y[offSrc + strideSrc + 4], y[offSrc + strideSrc + 5], dstData[0], offLuma2 + strideDst, dstData[1], offChr, dstData[2], offChr);
                dstData[0][offLuma2 + strideDst] = (dstData[0][offLuma2 + strideDst] << this.upShift) >> this.downShift;
                offLuma = offLuma2 + 1;
                dstData[1][offChr] = (dstData[1][offChr] << this.upShift) >> this.downShiftChr;
                dstData[2][offChr] = (dstData[2][offChr] << this.upShift) >> this.downShiftChr;
                offChr++;
                offSrc += 6;
            }
            offLuma += strideDst;
            offSrc += strideSrc;
        }
    }

    public static final void rgb2yuv(int r, int g, int b, int[] Y, int offY, int[] U, int offU, int[] V, int offV) {
        int y = (r * 66) + (g * 129) + (b * 25);
        int u = ((r * (-38)) - (g * 74)) + (b * 112);
        int v = ((r * 112) - (g * 94)) - (b * 18);
        Y[offY] = clip(((y + 128) >> 8) + 16);
        U[offU] = U[offU] + clip(((u + 128) >> 8) + 128);
        V[offV] = V[offV] + clip(((v + 128) >> 8) + 128);
    }

    private static final int clip(int val) {
        if (val < 0) {
            return 0;
        }
        if (val > 255) {
            return 255;
        }
        return val;
    }
}
