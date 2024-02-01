package org.jcodec.filters.color;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class CVTColorFilter {
    public void yuv42210BitTObgr24(Picture yuv, ByteBuffer rgb32) {
        IntBuffer y = IntBuffer.wrap(yuv.getPlaneData(0));
        IntBuffer cb = IntBuffer.wrap(yuv.getPlaneData(1));
        IntBuffer cr = IntBuffer.wrap(yuv.getPlaneData(2));
        while (y.hasRemaining()) {
            int c1 = y.get() - 64;
            int c2 = y.get() - 64;
            int d = cb.get() - 512;
            int e = cr.get() - 512;
            rgb32.put(blue(d, c1));
            rgb32.put(green(d, e, c1));
            rgb32.put(red(e, c1));
            rgb32.put(blue(d, c2));
            rgb32.put(green(d, e, c2));
            rgb32.put(red(e, c2));
        }
    }

    private static byte blue(int d, int c) {
        int blue = (((c * 1192) + (d * 2064)) + 512) >> 10;
        if (blue < 0) {
            blue = 0;
        } else if (blue > 1023) {
            blue = 1023;
        }
        return (byte) ((blue >> 2) & 255);
    }

    private static byte green(int d, int e, int c) {
        int green = ((((c * 1192) - (d * 400)) - (e * 832)) + 512) >> 10;
        if (green < 0) {
            green = 0;
        } else if (green > 1023) {
            green = 1023;
        }
        return (byte) ((green >> 2) & 255);
    }

    private static byte red(int e, int c) {
        int red = (((c * 1192) + (e * 1636)) + 512) >> 10;
        if (red < 0) {
            red = 0;
        } else if (red > 1023) {
            red = 1023;
        }
        return (byte) ((red >> 2) & 255);
    }
}
