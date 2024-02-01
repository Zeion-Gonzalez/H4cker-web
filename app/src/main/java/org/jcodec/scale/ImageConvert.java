package org.jcodec.scale;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mjpeg.JpegConst;

/* loaded from: classes.dex */
public class ImageConvert {
    private static final int CROP = 1024;
    private static final int ONE_HALF = 512;
    private static final int SCALEBITS = 10;
    private static final int FIX_0_71414 = FIX(0.71414d);
    private static final int FIX_1_772 = FIX(1.772d);
    private static final int _FIX_0_34414 = -FIX(0.34414d);
    private static final int FIX_1_402 = FIX(1.402d);
    private static final byte[] cropTable = new byte[2304];
    private static final int[] intCropTable = new int[2304];
    private static final byte[] y_ccir_to_jpeg = new byte[256];
    private static final byte[] y_jpeg_to_ccir = new byte[256];

    private static final int FIX(double x) {
        return (int) ((1024.0d * x) + 0.5d);
    }

    static {
        for (int i = -1024; i < 0; i++) {
            cropTable[i + 1024] = 0;
            intCropTable[i + 1024] = 0;
        }
        for (int i2 = 0; i2 < 256; i2++) {
            cropTable[i2 + 1024] = (byte) i2;
            intCropTable[i2 + 1024] = i2;
        }
        for (int i3 = 256; i3 < 1024; i3++) {
            cropTable[i3 + 1024] = -1;
            intCropTable[i3 + 1024] = 255;
        }
        for (int i4 = 0; i4 < 256; i4++) {
            y_ccir_to_jpeg[i4] = crop(Y_CCIR_TO_JPEG(i4));
            y_jpeg_to_ccir[i4] = crop(Y_JPEG_TO_CCIR(i4));
        }
    }

    public static final int ycbcr_to_rgb24(int y, int cb, int cr) {
        int y2 = y << 10;
        int cb2 = cb - 128;
        int cr2 = cr - 128;
        int add_r = (FIX_1_402 * cr2) + 512;
        int add_g = ((_FIX_0_34414 * cb2) - (FIX_0_71414 * cr2)) + 512;
        int add_b = (FIX_1_772 * cb2) + 512;
        int r = (y2 + add_r) >> 10;
        int g = (y2 + add_g) >> 10;
        int b = (y2 + add_b) >> 10;
        return ((crop(r) & 255) << 16) | ((crop(g) & 255) << 8) | (crop(b) & 255);
    }

    static final int Y_JPEG_TO_CCIR(int y) {
        return ((FIX(0.8588235294117647d) * y) + 16896) >> 10;
    }

    static final int Y_CCIR_TO_JPEG(int y) {
        return ((FIX(1.1643835616438356d) * y) + (512 - (FIX(1.1643835616438356d) * 16))) >> 10;
    }

    public static final int icrop(int i) {
        return intCropTable[i + 1024];
    }

    public static final byte crop(int i) {
        return cropTable[i + 1024];
    }

    public static final byte y_ccir_to_jpeg(byte y) {
        return y_ccir_to_jpeg[y & 255];
    }

    public static final byte y_jpeg_to_ccir(byte y) {
        return y_jpeg_to_ccir[y & 255];
    }

    public static void YUV444toRGB888(int y, int u, int v, ByteBuffer rgb) {
        int c = y - 16;
        int d = u - 128;
        int e = v - 128;
        int r = (((c * 298) + (e * 409)) + 128) >> 8;
        int g = ((((c * 298) - (d * 100)) - (e * JpegConst.RST0)) + 128) >> 8;
        int b = (((c * 298) + (d * 516)) + 128) >> 8;
        rgb.put(crop(r));
        rgb.put(crop(g));
        rgb.put(crop(b));
    }

    public static void RGB888toYUV444(ByteBuffer rgb, ByteBuffer Y, ByteBuffer U, ByteBuffer V) {
        int r = rgb.get() & 255;
        int g = rgb.get() & 255;
        int b = rgb.get() & 255;
        int y = (r * 66) + (g * 129) + (b * 25);
        int u = ((r * (-38)) - (g * 74)) + (b * 112);
        int v = ((r * 112) - (g * 94)) - (b * 18);
        Y.put(crop(((y + 128) >> 8) + 16));
        U.put(crop(((u + 128) >> 8) + 128));
        V.put(crop(((v + 128) >> 8) + 128));
    }

    public static byte RGB888toY4(int r, int g, int b) {
        int y = (r * 66) + (g * 129) + (b * 25);
        return crop(((y + 128) >> 8) + 16);
    }

    public static byte RGB888toU4(int r, int g, int b) {
        int u = ((r * (-38)) - (g * 74)) + (b * 112);
        return crop(((u + 128) >> 8) + 128);
    }

    public static byte RGB888toV4(int r, int g, int b) {
        int v = ((r * 112) - (g * 94)) - (b * 18);
        return crop(((v + 128) >> 8) + 128);
    }
}
