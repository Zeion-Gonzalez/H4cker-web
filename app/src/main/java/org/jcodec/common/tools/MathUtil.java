package org.jcodec.common.tools;

import android.support.v7.widget.helper.ItemTouchHelper;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.RationalLarge;

/* loaded from: classes.dex */
public class MathUtil {
    private static final int[] logTab = {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
    private static final int[] reverseTab = {0, 128, 64, JpegConst.SOF0, 32, 160, 96, JpegConst.APP0, 16, 144, 80, JpegConst.RST0, 48, 176, 112, 240, 8, 136, 72, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 40, 168, 104, JpegConst.APP8, 24, 152, 88, JpegConst.SOI, 56, MPEGConst.GROUP_START_CODE, 120, 248, 4, 132, 68, JpegConst.DHT, 36, 164, 100, JpegConst.APP4, 20, 148, 84, JpegConst.RST4, 52, MPEGConst.SEQUENCE_ERROR_CODE, 116, 244, 12, 140, 76, 204, 44, 172, 108, JpegConst.APPC, 28, 156, 92, 220, 60, 188, 124, 252, 2, 130, 66, JpegConst.SOF2, 34, 162, 98, JpegConst.APP2, 18, 146, 82, JpegConst.RST2, 50, MPEGConst.USER_DATA_START_CODE, 114, 242, 10, 138, 74, 202, 42, 170, 106, JpegConst.APPA, 26, 154, 90, JpegConst.SOS, 58, 186, 122, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 6, 134, 70, 198, 38, 166, 102, JpegConst.APP6, 22, 150, 86, JpegConst.RST6, 54, 182, 118, 246, 14, 142, 78, 206, 46, 174, 110, JpegConst.APPE, 30, 158, 94, 222, 62, 190, 126, JpegConst.COM, 1, 129, 65, JpegConst.SOF1, 33, 161, 97, JpegConst.APP1, 17, 145, 81, JpegConst.RST1, 49, 177, 113, 241, 9, 137, 73, 201, 41, 169, 105, JpegConst.APP9, 25, 153, 89, JpegConst.EOI, 57, 185, 121, 249, 5, 133, 69, 197, 37, 165, 101, JpegConst.APP5, 21, 149, 85, JpegConst.RST5, 53, MPEGConst.EXTENSION_START_CODE, 117, 245, 13, 141, 77, 205, 45, 173, 109, JpegConst.APPD, 29, 157, 93, JpegConst.DRI, 61, 189, 125, 253, 3, 131, 67, JpegConst.SOF3, 35, 163, 99, JpegConst.APP3, 19, 147, 83, JpegConst.RST3, 51, MPEGConst.SEQUENCE_HEADER_CODE, 115, 243, 11, 139, 75, 203, 43, 171, 107, JpegConst.APPB, 27, 155, 91, JpegConst.DQT, 59, 187, 123, 251, 7, 135, 71, 199, 39, 167, 103, JpegConst.APP7, 23, 151, 87, JpegConst.RST7, 55, MPEGConst.SEQUENCE_END_CODE, 119, 247, 15, 143, 79, 207, 47, MPEGConst.SLICE_START_CODE_LAST, 111, JpegConst.APPF, 31, 159, 95, 223, 63, 191, 127, 255};

    public static int log2(int v) {
        int n = 0;
        if (((-65536) & v) != 0) {
            v >>= 16;
            n = 0 + 16;
        }
        if ((65280 & v) != 0) {
            v >>= 8;
            n += 8;
        }
        return n + logTab[v];
    }

    public static int log2(long v) {
        int n = 0;
        if (((-4294967296L) & v) != 0) {
            v >>= 32;
            n = 0 + 32;
        }
        if ((4294901760L & v) != 0) {
            v >>= 16;
            n += 16;
        }
        if ((65280 & v) != 0) {
            v >>= 8;
            n += 8;
        }
        return n + logTab[(int) v];
    }

    public static int log2Slow(int val) {
        int i = 0;
        while ((Integer.MIN_VALUE & val) == 0) {
            val <<= 1;
            i++;
        }
        return 31 - i;
    }

    public static int gcd(int a, int b) {
        if (b != 0) {
            return gcd(b, a % b);
        }
        return a;
    }

    public static long gcd(long a, long b) {
        if (b != 0) {
            return gcd(b, a % b);
        }
        return a;
    }

    public static Rational reduce(Rational r) {
        return reduce(r.getNum(), r.getDen());
    }

    public static Rational reduce(int num, int den) {
        int gcd = gcd(num, den);
        return new Rational(num / gcd, den / gcd);
    }

    public static RationalLarge reduce(long num, long den) {
        long gcd = gcd(num, den);
        return new RationalLarge(num / gcd, den / gcd);
    }

    public static final int clip(int val, int from, int to) {
        return val < from ? from : val > to ? to : val;
    }

    public static final int clip(int val, int max) {
        return val < max ? val : max;
    }

    public static int cubeRoot(int n) {
        return 0;
    }

    public static final int reverse(int b) {
        return reverseTab[b & 255];
    }

    public static int nextPowerOfTwo(int n) {
        int n2 = n - 1;
        int n3 = n2 | (n2 >> 1);
        int n4 = n3 | (n3 >> 2);
        int n5 = n4 | (n4 >> 4);
        int n6 = n5 | (n5 >> 8);
        return (n6 | (n6 >> 16)) + 1;
    }

    public static final int abs(int val) {
        int sign = val >> 31;
        return (val ^ sign) - sign;
    }

    public static final int golomb(int signedLevel) {
        if (signedLevel == 0) {
            return 0;
        }
        return (abs(signedLevel) << 1) - ((signedLevel ^ (-1)) >>> 31);
    }

    public static final int toSigned(int val, int sign) {
        return (val ^ sign) - sign;
    }

    public static final int sign(int val) {
        return -(val >> 31);
    }

    public static int wrap(int picNo, int maxFrames) {
        return picNo < 0 ? picNo + maxFrames : picNo >= maxFrames ? picNo - maxFrames : picNo;
    }
}
