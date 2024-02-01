package org.jcodec.common.dct;

/* loaded from: classes.dex */
public class DCTRef {
    static double[] coefficients = new double[64];

    static {
        for (int j = 0; j < 8; j++) {
            coefficients[j] = Math.sqrt(0.125d);
            for (int i = 8; i < 64; i += 8) {
                coefficients[i + j] = Math.cos(((i * (j + 0.5d)) * 3.141592653589793d) / 64.0d) * 0.5d;
            }
        }
    }

    public static void fdct(int[] block, int off) {
        double[] out = new double[64];
        for (int i = 0; i < 64; i += 8) {
            for (int j = 0; j < 8; j++) {
                double tmp = 0.0d;
                for (int k = 0; k < 8; k++) {
                    tmp += coefficients[i + k] * block[(k * 8) + j + off];
                }
                out[i + j] = 4.0d * tmp;
            }
        }
        for (int j2 = 0; j2 < 8; j2++) {
            for (int i2 = 0; i2 < 64; i2 += 8) {
                double tmp2 = 0.0d;
                for (int k2 = 0; k2 < 8; k2++) {
                    tmp2 += out[i2 + k2] * coefficients[(j2 * 8) + k2];
                }
                block[i2 + j2 + off] = (int) (0.499999999999d + tmp2);
            }
        }
    }

    public static void idct(int[] block, int off) {
        double[] out = new double[64];
        for (int i = 0; i < 64; i += 8) {
            for (int j = 0; j < 8; j++) {
                double tmp = 0.0d;
                for (int k = 0; k < 8; k++) {
                    tmp += block[i + k] * coefficients[(k * 8) + j];
                }
                out[i + j] = tmp;
            }
        }
        for (int i2 = 0; i2 < 8; i2++) {
            for (int j2 = 0; j2 < 8; j2++) {
                double tmp2 = 0.0d;
                for (int k2 = 0; k2 < 64; k2 += 8) {
                    tmp2 += coefficients[k2 + i2] * out[k2 + j2];
                }
                block[(i2 * 8) + j2] = (int) (0.5d + tmp2);
            }
        }
    }
}
