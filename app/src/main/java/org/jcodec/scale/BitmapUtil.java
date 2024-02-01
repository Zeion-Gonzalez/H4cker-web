package org.jcodec.scale;

import android.graphics.Bitmap;
import java.nio.IntBuffer;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class BitmapUtil {
    private static ThreadLocal<int[]> buffer = new ThreadLocal<>();

    public static Picture fromBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        Picture dst = Picture.create(src.getWidth(), src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        return dst;
    }

    public static void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];
        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        int srcOff = 0;
        int dstOff = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            int j = 0;
            while (j < src.getWidth()) {
                int rgb = packed[srcOff];
                dstData[dstOff] = (rgb >> 16) & 255;
                dstData[dstOff + 1] = (rgb >> 8) & 255;
                dstData[dstOff + 2] = rgb & 255;
                j++;
                srcOff++;
                dstOff += 3;
            }
        }
    }

    public static Bitmap toBitmap(Picture pic) {
        if (pic == null) {
            return null;
        }
        Bitmap dst = Bitmap.createBitmap(pic.getCroppedWidth(), pic.getCroppedHeight(), Bitmap.Config.ARGB_8888);
        toBitmap(pic, dst);
        return dst;
    }

    public static void toBitmap(Picture src, Bitmap dst) {
        int[] srcData = src.getPlaneData(0);
        int[] packed = getBuffer(src);
        int dstOff = 0;
        int srcOff = 0;
        for (int i = 0; i < src.getCroppedHeight(); i++) {
            int j = 0;
            while (j < src.getCroppedWidth()) {
                packed[dstOff] = (-16777216) | (srcData[srcOff] << 16) | (srcData[srcOff + 1] << 8) | srcData[srcOff + 2];
                j++;
                dstOff++;
                srcOff += 3;
            }
            srcOff += src.getWidth() - src.getCroppedWidth();
        }
        dst.copyPixelsFromBuffer(IntBuffer.wrap(packed));
    }

    private static int[] getBuffer(Picture src) {
        int[] result = buffer.get();
        if (result == null) {
            int[] result2 = new int[src.getWidth() * src.getHeight()];
            buffer.set(result2);
            return result2;
        }
        return result;
    }
}
