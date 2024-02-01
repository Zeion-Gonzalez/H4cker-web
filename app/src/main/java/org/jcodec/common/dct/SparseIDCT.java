package org.jcodec.common.dct;

import java.util.Arrays;

/* loaded from: classes.dex */
public class SparseIDCT {
    public static final int[][] COEFF = new int[64];
    public static final int DC_SHIFT = 10;
    public static final int PRECISION = 13;

    static {
        COEFF[0] = new int[64];
        Arrays.fill(COEFF[0], 1024);
        for (int i = 1; i < 64; i++) {
            COEFF[i] = new int[64];
            COEFF[i][i] = 8192;
            SimpleIDCT10Bit.idct10(COEFF[i], 0);
        }
    }

    public static final void start(int[] block, int dc) {
        int dc2 = dc << 10;
        for (int i = 0; i < 64; i += 4) {
            block[i + 0] = dc2;
            block[i + 1] = dc2;
            block[i + 2] = dc2;
            block[i + 3] = dc2;
        }
    }

    public static final void coeff(int[] block, int ind, int level) {
        for (int i = 0; i < 64; i += 4) {
            block[i] = block[i] + (COEFF[ind][i] * level);
            int i2 = i + 1;
            block[i2] = block[i2] + (COEFF[ind][i + 1] * level);
            int i3 = i + 2;
            block[i3] = block[i3] + (COEFF[ind][i + 2] * level);
            int i4 = i + 3;
            block[i4] = block[i4] + (COEFF[ind][i + 3] * level);
        }
    }

    public static final void finish(int[] block) {
        for (int i = 0; i < 64; i += 4) {
            block[i] = div(block[i]);
            block[i + 1] = div(block[i + 1]);
            block[i + 2] = div(block[i + 2]);
            block[i + 3] = div(block[i + 3]);
        }
    }

    private static final int div(int x) {
        int m = x >> 31;
        int n = x >>> 31;
        return ((((x ^ m) + n) >> 13) ^ m) + n;
    }
}
