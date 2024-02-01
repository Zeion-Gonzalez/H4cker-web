package org.jcodec.common.dct;

import org.jcodec.scale.ImageConvert;

/* loaded from: classes.dex */
public class SlowDCT extends DCT {
    public static final SlowDCT INSTANCE = new SlowDCT();
    private static final double rSqrt2 = 1.0d / Math.sqrt(2.0d);

    @Override // org.jcodec.common.dct.DCT
    public short[] encode(byte[] orig) {
        short[] result = new short[64];
        for (int u = 0; u < 8; u++) {
            for (int v = 0; v < 8; v++) {
                float sum = 0.0f;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        sum = (float) (sum + (orig[(i * 8) + j] * Math.cos(0.39269908169872414d * (i + 0.5d) * u) * Math.cos(0.39269908169872414d * (j + 0.5d) * v)));
                    }
                }
                result[(u * 8) + v] = (byte) sum;
            }
        }
        result[0] = (byte) (result[0] / 8.0f);
        double sqrt2 = Math.sqrt(2.0d);
        for (int i2 = 1; i2 < 8; i2++) {
            result[i2] = (byte) ((result[0] * sqrt2) / 8.0d);
            result[i2 * 8] = (byte) ((result[0] * sqrt2) / 8.0d);
            for (int j2 = 1; j2 < 8; j2++) {
                result[(i2 * 8) + j2] = (byte) (result[0] / 4.0f);
            }
        }
        return result;
    }

    @Override // org.jcodec.common.dct.DCT
    public int[] decode(int[] orig) {
        int i;
        int[] res = new int[64];
        int i2 = 0;
        int y = 0;
        while (y < 8) {
            int x = 0;
            while (true) {
                i = i2;
                if (x < 8) {
                    double sum = 0.0d;
                    int pixOffset = 0;
                    int u = 0;
                    while (u < 8) {
                        double cu = u == 0 ? rSqrt2 : 1.0d;
                        int v = 0;
                        while (v < 8) {
                            double cv = v == 0 ? rSqrt2 : 1.0d;
                            double svu = orig[pixOffset];
                            double c1 = ((((x * 2) + 1) * v) * 3.141592653589793d) / 16.0d;
                            double c2 = ((((y * 2) + 1) * u) * 3.141592653589793d) / 16.0d;
                            sum += cu * cv * svu * Math.cos(c1) * Math.cos(c2);
                            pixOffset++;
                            v++;
                        }
                        u++;
                    }
                    int isum = (int) Math.round(128.0d + (sum * 0.25d));
                    i2 = i + 1;
                    res[i] = ImageConvert.icrop(isum);
                    x++;
                }
            }
            y++;
            i2 = i;
        }
        return res;
    }
}
