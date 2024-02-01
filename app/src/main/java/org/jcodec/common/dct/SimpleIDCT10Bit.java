package org.jcodec.common.dct;

import android.support.v4.internal.view.SupportMenu;

/* loaded from: classes.dex */
public class SimpleIDCT10Bit {

    /* renamed from: W1 */
    public static int f1517W1 = 90901;

    /* renamed from: W2 */
    public static int f1518W2 = 85627;

    /* renamed from: W3 */
    public static int f1519W3 = 77062;

    /* renamed from: W4 */
    public static int f1520W4 = SupportMenu.USER_MASK;

    /* renamed from: W5 */
    public static int f1521W5 = 51491;

    /* renamed from: W6 */
    public static int f1522W6 = 35468;

    /* renamed from: W7 */
    public static int f1523W7 = 18081;
    public static int ROW_SHIFT = 15;
    public static int COL_SHIFT = 20;

    public static final void idct10(int[] buf, int off) {
        for (int i = 0; i < 8; i++) {
            idctRow(buf, (i << 3) + off);
        }
        for (int i2 = 0; i2 < 8; i2++) {
            idctCol(buf, off + i2);
        }
    }

    private static final void idctCol(int[] buf, int off) {
        int a0 = f1520W4 * (buf[off + 0] + ((1 << (COL_SHIFT - 1)) / f1520W4));
        int a02 = a0 + (f1518W2 * buf[off + 16]);
        int a1 = a0 + (f1522W6 * buf[off + 16]);
        int a2 = a0 + ((-f1522W6) * buf[off + 16]);
        int a3 = a0 + ((-f1518W2) * buf[off + 16]);
        int b0 = f1517W1 * buf[off + 8];
        int b1 = f1519W3 * buf[off + 8];
        int b2 = f1521W5 * buf[off + 8];
        int b3 = f1523W7 * buf[off + 8];
        int b02 = b0 + (f1519W3 * buf[off + 24]);
        int b12 = b1 + ((-f1523W7) * buf[off + 24]);
        int b22 = b2 + ((-f1517W1) * buf[off + 24]);
        int b32 = b3 + ((-f1521W5) * buf[off + 24]);
        if (buf[off + 32] != 0) {
            a02 += f1520W4 * buf[off + 32];
            a1 += (-f1520W4) * buf[off + 32];
            a2 += (-f1520W4) * buf[off + 32];
            a3 += f1520W4 * buf[off + 32];
        }
        if (buf[off + 40] != 0) {
            b02 += f1521W5 * buf[off + 40];
            b12 += (-f1517W1) * buf[off + 40];
            b22 += f1523W7 * buf[off + 40];
            b32 += f1519W3 * buf[off + 40];
        }
        if (buf[off + 48] != 0) {
            a02 += f1522W6 * buf[off + 48];
            a1 += (-f1518W2) * buf[off + 48];
            a2 += f1518W2 * buf[off + 48];
            a3 += (-f1522W6) * buf[off + 48];
        }
        if (buf[off + 56] != 0) {
            b02 += f1523W7 * buf[off + 56];
            b12 += (-f1521W5) * buf[off + 56];
            b22 += f1519W3 * buf[off + 56];
            b32 += (-f1517W1) * buf[off + 56];
        }
        buf[off] = (a02 + b02) >> COL_SHIFT;
        buf[off + 8] = (a1 + b12) >> COL_SHIFT;
        buf[off + 16] = (a2 + b22) >> COL_SHIFT;
        buf[off + 24] = (a3 + b32) >> COL_SHIFT;
        buf[off + 32] = (a3 - b32) >> COL_SHIFT;
        buf[off + 40] = (a2 - b22) >> COL_SHIFT;
        buf[off + 48] = (a1 - b12) >> COL_SHIFT;
        buf[off + 56] = (a02 - b02) >> COL_SHIFT;
    }

    private static final void idctRow(int[] buf, int off) {
        int a0 = (f1520W4 * buf[off]) + (1 << (ROW_SHIFT - 1));
        int a02 = a0 + (f1518W2 * buf[off + 2]);
        int a1 = a0 + (f1522W6 * buf[off + 2]);
        int a2 = a0 - (f1522W6 * buf[off + 2]);
        int a3 = a0 - (f1518W2 * buf[off + 2]);
        int b0 = (f1517W1 * buf[off + 1]) + (f1519W3 * buf[off + 3]);
        int b1 = (f1519W3 * buf[off + 1]) + ((-f1523W7) * buf[off + 3]);
        int b2 = (f1521W5 * buf[off + 1]) + ((-f1517W1) * buf[off + 3]);
        int b3 = (f1523W7 * buf[off + 1]) + ((-f1521W5) * buf[off + 3]);
        if (buf[off + 4] != 0 || buf[off + 5] != 0 || buf[off + 6] != 0 || buf[off + 7] != 0) {
            a02 += (f1520W4 * buf[off + 4]) + (f1522W6 * buf[off + 6]);
            a1 += ((-f1520W4) * buf[off + 4]) - (f1518W2 * buf[off + 6]);
            a2 += ((-f1520W4) * buf[off + 4]) + (f1518W2 * buf[off + 6]);
            a3 += (f1520W4 * buf[off + 4]) - (f1522W6 * buf[off + 6]);
            b0 = b0 + (f1521W5 * buf[off + 5]) + (f1523W7 * buf[off + 7]);
            b1 = b1 + ((-f1517W1) * buf[off + 5]) + ((-f1521W5) * buf[off + 7]);
            b2 = b2 + (f1523W7 * buf[off + 5]) + (f1519W3 * buf[off + 7]);
            b3 = b3 + (f1519W3 * buf[off + 5]) + ((-f1517W1) * buf[off + 7]);
        }
        buf[off + 0] = (a02 + b0) >> ROW_SHIFT;
        buf[off + 7] = (a02 - b0) >> ROW_SHIFT;
        buf[off + 1] = (a1 + b1) >> ROW_SHIFT;
        buf[off + 6] = (a1 - b1) >> ROW_SHIFT;
        buf[off + 2] = (a2 + b2) >> ROW_SHIFT;
        buf[off + 5] = (a2 - b2) >> ROW_SHIFT;
        buf[off + 3] = (a3 + b3) >> ROW_SHIFT;
        buf[off + 4] = (a3 - b3) >> ROW_SHIFT;
    }

    private static void fdctRow(int[] buf, int off) {
    }
}
