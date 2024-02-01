package org.jcodec.codecs.vpx;

/* loaded from: classes.dex */
public class VPXDCT {
    public static int cospi8sqrt2minus1 = 20091;
    public static int sinpi8sqrt2 = 35468;

    public static void fdct4x4(int[] coef) {
        for (int i = 0; i < 16; i += 4) {
            int a1 = (coef[i] + coef[i + 3]) << 3;
            int b1 = (coef[i + 1] + coef[i + 2]) << 3;
            int c1 = (coef[i + 1] - coef[i + 2]) << 3;
            int d1 = (coef[i] - coef[i + 3]) << 3;
            coef[i] = a1 + b1;
            coef[i + 2] = a1 - b1;
            coef[i + 1] = (((c1 * 2217) + (d1 * 5352)) + 14500) >> 12;
            coef[i + 3] = (((d1 * 2217) - (c1 * 5352)) + 7500) >> 12;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = coef[i2] + coef[i2 + 12];
            int b12 = coef[i2 + 4] + coef[i2 + 8];
            int c12 = coef[i2 + 4] - coef[i2 + 8];
            int d12 = coef[i2] - coef[i2 + 12];
            coef[i2] = ((a12 + b12) + 7) >> 4;
            coef[i2 + 8] = ((a12 - b12) + 7) >> 4;
            coef[i2 + 4] = (d12 != 0 ? 1 : 0) + ((((c12 * 2217) + (d12 * 5352)) + 12000) >> 16);
            coef[i2 + 12] = (((d12 * 2217) - (c12 * 5352)) + 51000) >> 16;
        }
    }

    public static void walsh4x4(int[] coef) {
        for (int i = 0; i < 16; i += 4) {
            int a1 = (coef[i] + coef[i + 2]) << 2;
            int d1 = (coef[i + 1] + coef[i + 3]) << 2;
            int c1 = (coef[i + 1] - coef[i + 3]) << 2;
            int b1 = (coef[i] - coef[i + 2]) << 2;
            coef[i] = (a1 != 0 ? 1 : 0) + a1 + d1;
            coef[i + 1] = b1 + c1;
            coef[i + 2] = b1 - c1;
            coef[i + 3] = a1 - d1;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = coef[i2] + coef[i2 + 8];
            int d12 = coef[i2 + 4] + coef[i2 + 12];
            int c12 = coef[i2 + 4] - coef[i2 + 12];
            int b12 = coef[i2] - coef[i2 + 8];
            int a2 = a12 + d12;
            int b2 = b12 + c12;
            int c2 = b12 - c12;
            int d2 = a12 - d12;
            int a22 = a2 + (a2 < 0 ? 1 : 0);
            int b22 = b2 + (b2 < 0 ? 1 : 0);
            int c22 = c2 + (c2 < 0 ? 1 : 0);
            int d22 = d2 + (d2 < 0 ? 1 : 0);
            coef[i2] = (a22 + 3) >> 3;
            coef[i2 + 4] = (b22 + 3) >> 3;
            coef[i2 + 8] = (c22 + 3) >> 3;
            coef[i2 + 12] = (d22 + 3) >> 3;
        }
    }

    public static void idct4x4(int[] coef) {
        for (int i = 0; i < 4; i++) {
            int a1 = coef[i] + coef[i + 8];
            int b1 = coef[i] - coef[i + 8];
            int temp1 = (coef[i + 4] * sinpi8sqrt2) >> 16;
            int temp2 = coef[i + 12] + ((coef[i + 12] * cospi8sqrt2minus1) >> 16);
            int c1 = temp1 - temp2;
            int temp12 = coef[i + 4] + ((coef[i + 4] * cospi8sqrt2minus1) >> 16);
            int temp22 = (coef[i + 12] * sinpi8sqrt2) >> 16;
            int d1 = temp12 + temp22;
            coef[i] = a1 + d1;
            coef[i + 12] = a1 - d1;
            coef[i + 4] = b1 + c1;
            coef[i + 8] = b1 - c1;
        }
        for (int i2 = 0; i2 < 16; i2 += 4) {
            int a12 = coef[i2] + coef[i2 + 2];
            int b12 = coef[i2] - coef[i2 + 2];
            int temp13 = (coef[i2 + 1] * sinpi8sqrt2) >> 16;
            int temp23 = coef[i2 + 3] + ((coef[i2 + 3] * cospi8sqrt2minus1) >> 16);
            int c12 = temp13 - temp23;
            int temp14 = coef[i2 + 1] + ((coef[i2 + 1] * cospi8sqrt2minus1) >> 16);
            int temp24 = (coef[i2 + 3] * sinpi8sqrt2) >> 16;
            int d12 = temp14 + temp24;
            coef[i2] = ((a12 + d12) + 4) >> 3;
            coef[i2 + 3] = ((a12 - d12) + 4) >> 3;
            coef[i2 + 1] = ((b12 + c12) + 4) >> 3;
            coef[i2 + 2] = ((b12 - c12) + 4) >> 3;
        }
    }

    public static void iwalsh4x4(int[] coef) {
        for (int i = 0; i < 4; i++) {
            int a1 = coef[i] + coef[i + 12];
            int b1 = coef[i + 4] + coef[i + 8];
            int c1 = coef[i + 4] - coef[i + 8];
            int d1 = coef[i] - coef[i + 12];
            coef[i] = a1 + b1;
            coef[i + 4] = c1 + d1;
            coef[i + 8] = a1 - b1;
            coef[i + 12] = d1 - c1;
        }
        for (int i2 = 0; i2 < 16; i2 += 4) {
            int a12 = coef[i2] + coef[i2 + 3];
            int b12 = coef[i2 + 1] + coef[i2 + 2];
            int c12 = coef[i2 + 1] - coef[i2 + 2];
            int d12 = coef[i2] - coef[i2 + 3];
            int a2 = a12 + b12;
            int b2 = c12 + d12;
            int c2 = a12 - b12;
            int d2 = d12 - c12;
            coef[i2] = (a2 + 3) >> 3;
            coef[i2 + 1] = (b2 + 3) >> 3;
            coef[i2 + 2] = (c2 + 3) >> 3;
            coef[i2 + 3] = (d2 + 3) >> 3;
        }
    }
}
