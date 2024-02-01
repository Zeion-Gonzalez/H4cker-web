package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv420pToYuv422p implements Transform {
    private int shiftDown;
    private int shiftUp;

    public Yuv420pToYuv422p(int shiftUp, int shiftDown) {
        this.shiftUp = shiftUp;
        this.shiftDown = shiftDown;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        copy(src.getPlaneData(0), dst.getPlaneData(0), src.getWidth(), dst.getWidth(), dst.getHeight(), this.shiftUp, this.shiftDown);
        copy(src.getPlaneData(1), dst.getPlaneData(1), 0, 0, 1, 2, src.getWidth() >> 1, dst.getWidth() >> 1, src.getHeight() >> 1, dst.getHeight(), this.shiftUp, this.shiftDown);
        copy(src.getPlaneData(1), dst.getPlaneData(1), 0, 1, 1, 2, src.getWidth() >> 1, dst.getWidth() >> 1, src.getHeight() >> 1, dst.getHeight(), this.shiftUp, this.shiftDown);
        copy(src.getPlaneData(2), dst.getPlaneData(2), 0, 0, 1, 2, src.getWidth() >> 1, dst.getWidth() >> 1, src.getHeight() >> 1, dst.getHeight(), this.shiftUp, this.shiftDown);
        copy(src.getPlaneData(2), dst.getPlaneData(2), 0, 1, 1, 2, src.getWidth() >> 1, dst.getWidth() >> 1, src.getHeight() >> 1, dst.getHeight(), this.shiftUp, this.shiftDown);
    }

    private static final void copy(int[] src, int[] dest, int offX, int offY, int stepX, int stepY, int strideSrc, int strideDest, int heightSrc, int heightDst, int upShift, int downShift) {
        int offD = offX + (offY * strideDest);
        int srcOff = 0;
        int i = 0;
        while (i < heightSrc) {
            int j = 0;
            int srcOff2 = srcOff;
            while (j < strideSrc) {
                dest[offD] = (src[srcOff2] & 255) << 2;
                offD += stepX;
                j++;
                srcOff2++;
            }
            int lastOff = offD - stepX;
            int j2 = strideSrc * stepX;
            while (j2 < strideDest) {
                dest[offD] = dest[lastOff];
                offD += stepX;
                j2 += stepX;
            }
            offD += (stepY - 1) * strideDest;
            i++;
            srcOff = srcOff2;
        }
        int lastLine = offD - (stepY * strideDest);
        int i2 = heightSrc * stepY;
        while (i2 < heightDst) {
            int j3 = 0;
            while (j3 < strideDest) {
                dest[offD] = dest[lastLine + j3];
                offD += stepX;
                j3 += stepX;
            }
            offD += (stepY - 1) * strideDest;
            i2 += stepY;
        }
    }

    private static void copy(int[] src, int[] dest, int srcWidth, int dstWidth, int dstHeight, int shiftUp, int shiftDown) {
        int height = src.length / srcWidth;
        int dstOff = 0;
        int srcOff = 0;
        int i = 0;
        while (i < height) {
            int j = 0;
            int srcOff2 = srcOff;
            int dstOff2 = dstOff;
            while (j < srcWidth) {
                dest[dstOff2] = (src[srcOff2] & 255) << 2;
                j++;
                srcOff2++;
                dstOff2++;
            }
            int j2 = srcWidth;
            while (j2 < dstWidth) {
                dest[dstOff2] = dest[srcWidth - 1];
                j2++;
                dstOff2++;
            }
            i++;
            srcOff = srcOff2;
            dstOff = dstOff2;
        }
        int lastLine = (height - 1) * dstWidth;
        int i2 = height;
        while (i2 < dstHeight) {
            int j3 = 0;
            int dstOff3 = dstOff;
            while (j3 < dstWidth) {
                dest[dstOff3] = dest[lastLine + j3];
                j3++;
                dstOff3++;
            }
            i2++;
            dstOff = dstOff3;
        }
    }
}
