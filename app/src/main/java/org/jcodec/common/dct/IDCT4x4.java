package org.jcodec.common.dct;

/* loaded from: classes.dex */
public class IDCT4x4 {
    public static final int CN_SHIFT = 12;
    public static final int C_SHIFT = 18;
    public static final int RN_SHIFT = 15;
    public static final int R_SHIFT = 11;

    /* renamed from: C1 */
    public static final int f1511C1 = C_FIX(0.6532814824d);

    /* renamed from: C2 */
    public static final int f1512C2 = C_FIX(0.2705980501d);

    /* renamed from: C3 */
    public static final int f1513C3 = C_FIX(0.5d);

    /* renamed from: R1 */
    public static final int f1514R1 = R_FIX(0.6532814824d);

    /* renamed from: R2 */
    public static final int f1515R2 = R_FIX(0.2705980501d);

    /* renamed from: R3 */
    public static final int f1516R3 = R_FIX(0.5d);

    public static void idct(int[] blk, int off) {
        for (int i = 0; i < 4; i++) {
            idct4row(blk, (i << 2) + off);
        }
        for (int i2 = 0; i2 < 4; i2++) {
            idct4col_add(blk, off + i2);
        }
    }

    public static final int C_FIX(double x) {
        return (int) ((1.414213562d * x * 4096.0d) + 0.5d);
    }

    private static void idct4col_add(int[] blk, int off) {
        int a0 = blk[off];
        int a1 = blk[off + 4];
        int a2 = blk[off + 8];
        int a3 = blk[off + 12];
        int c0 = ((a0 + a2) * f1513C3) + 131072;
        int c2 = ((a0 - a2) * f1513C3) + 131072;
        int c1 = (f1511C1 * a1) + (f1512C2 * a3);
        int c3 = (f1512C2 * a1) - (f1511C1 * a3);
        blk[off] = (c0 + c1) >> 18;
        blk[off + 4] = (c2 + c3) >> 18;
        blk[off + 8] = (c2 - c3) >> 18;
        blk[off + 12] = (c0 - c1) >> 18;
    }

    public static final int R_FIX(double x) {
        return (int) ((1.414213562d * x * 32768.0d) + 0.5d);
    }

    private static void idct4row(int[] blk, int off) {
        int a0 = blk[off];
        int a1 = blk[off + 1];
        int a2 = blk[off + 2];
        int a3 = blk[off + 3];
        int c0 = ((a0 + a2) * f1516R3) + 1024;
        int c2 = ((a0 - a2) * f1516R3) + 1024;
        int c1 = (f1514R1 * a1) + (f1515R2 * a3);
        int c3 = (f1515R2 * a1) - (f1514R1 * a3);
        blk[off] = (c0 + c1) >> 11;
        blk[off + 1] = (c2 + c3) >> 11;
        blk[off + 2] = (c2 - c3) >> 11;
        blk[off + 3] = (c0 - c1) >> 11;
    }
}
