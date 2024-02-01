package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class RgbToYuv422p implements Transform {
    private int downShift;
    private int downShiftChr;
    private int upShift;

    public RgbToYuv422p(int upShift, int downShift) {
        this.upShift = upShift;
        this.downShift = downShift;
        this.downShiftChr = downShift + 1;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture img, Picture dst) {
        int[] y = img.getData()[0];
        int[][] dstData = dst.getData();
        int off = 0;
        int offSrc = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < (img.getWidth() >> 1); j++) {
                dstData[1][off] = 0;
                dstData[2][off] = 0;
                int offY = off << 1;
                int offSrc2 = offSrc + 1;
                int i2 = y[offSrc];
                int offSrc3 = offSrc2 + 1;
                int i3 = y[offSrc2];
                int offSrc4 = offSrc3 + 1;
                RgbToYuv420p.rgb2yuv(i2, i3, y[offSrc3], dstData[0], offY, dstData[1], off, dstData[2], off);
                dstData[0][offY] = (dstData[0][offY] << this.upShift) >> this.downShift;
                int offSrc5 = offSrc4 + 1;
                int i4 = y[offSrc4];
                int offSrc6 = offSrc5 + 1;
                int i5 = y[offSrc5];
                offSrc = offSrc6 + 1;
                RgbToYuv420p.rgb2yuv(i4, i5, y[offSrc6], dstData[0], offY + 1, dstData[1], off, dstData[2], off);
                dstData[0][offY + 1] = (dstData[0][offY + 1] << this.upShift) >> this.downShift;
                dstData[1][off] = (dstData[1][off] << this.upShift) >> this.downShiftChr;
                dstData[2][off] = (dstData[2][off] << this.upShift) >> this.downShiftChr;
                off++;
            }
        }
    }
}
