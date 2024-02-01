package org.jcodec.common.tools;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;

/* loaded from: classes.dex */
public class ImageOP {
    public static void subImageWithFill(int[] src, int width, int height, int[] dst, int dstW, int dstH, int offX, int offY) {
        int srcHeight = Math.min(height - offY, dstH);
        int srcWidth = Math.min(width - offX, dstW);
        int dstOff = 0;
        int srcOff = (offY * width) + offX;
        int i = 0;
        while (i < srcHeight) {
            int j = 0;
            while (j < srcWidth) {
                dst[dstOff + j] = src[srcOff + j];
                j++;
            }
            int lastPix = dst[j - 1];
            while (j < dstW) {
                dst[dstOff + j] = lastPix;
                j++;
            }
            srcOff += width;
            dstOff += dstW;
            i++;
        }
        int lastLine = dstOff - dstW;
        while (i < dstH) {
            System.arraycopy(dst, lastLine, dst, dstOff, dstW);
            dstOff += dstW;
            i++;
        }
    }

    public static void subImageWithFill(Picture in, Picture out, Rect rect) {
        int width = in.getWidth();
        int height = in.getHeight();
        ColorSpace color = in.getColor();
        int[][] data = in.getData();
        for (int i = 0; i < data.length; i++) {
            subImageWithFill(data[i], width >> color.compWidth[i], height >> color.compHeight[i], out.getPlaneData(i), rect.getWidth() >> color.compWidth[i], rect.getHeight() >> color.compHeight[i], rect.getX() >> color.compWidth[i], rect.getY() >> color.compHeight[i]);
        }
    }
}
