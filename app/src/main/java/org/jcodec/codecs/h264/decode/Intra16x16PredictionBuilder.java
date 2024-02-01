package org.jcodec.codecs.h264.decode;

import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class Intra16x16PredictionBuilder {
    public static void predictWithMode(int predMode, int[] residual, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine, int[] topLeft, int x) {
        switch (predMode) {
            case 0:
                predictVertical(residual, topAvailable, topLine, x);
                return;
            case 1:
                predictHorizontal(residual, leftAvailable, leftRow, x);
                return;
            case 2:
                predictDC(residual, leftAvailable, topAvailable, leftRow, topLine, x);
                return;
            case 3:
                predictPlane(residual, leftAvailable, topAvailable, leftRow, topLine, topLeft, x);
                return;
            default:
                return;
        }
    }

    public static void predictVertical(int[] residual, boolean topAvailable, int[] topLine, int x) {
        int off = 0;
        for (int j = 0; j < 16; j++) {
            int i = 0;
            while (i < 16) {
                residual[off] = MathUtil.clip(residual[off] + topLine[x + i], 0, 255);
                i++;
                off++;
            }
        }
    }

    public static void predictHorizontal(int[] residual, boolean leftAvailable, int[] leftRow, int x) {
        int off = 0;
        for (int j = 0; j < 16; j++) {
            int i = 0;
            while (i < 16) {
                residual[off] = MathUtil.clip(residual[off] + leftRow[j], 0, 255);
                i++;
                off++;
            }
        }
    }

    public static void predictDC(int[] residual, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine, int x) {
        int s0;
        if (leftAvailable && topAvailable) {
            int s02 = 0;
            for (int i = 0; i < 16; i++) {
                s02 += leftRow[i];
            }
            for (int i2 = 0; i2 < 16; i2++) {
                s02 += topLine[x + i2];
            }
            s0 = (s02 + 16) >> 5;
        } else if (leftAvailable) {
            int s03 = 0;
            for (int i3 = 0; i3 < 16; i3++) {
                s03 += leftRow[i3];
            }
            s0 = (s03 + 8) >> 4;
        } else if (topAvailable) {
            int s04 = 0;
            for (int i4 = 0; i4 < 16; i4++) {
                s04 += topLine[x + i4];
            }
            s0 = (s04 + 8) >> 4;
        } else {
            s0 = 128;
        }
        for (int i5 = 0; i5 < 256; i5++) {
            residual[i5] = MathUtil.clip(residual[i5] + s0, 0, 255);
        }
    }

    public static void predictPlane(int[] residual, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine, int[] topLeft, int x) {
        int H = 0;
        for (int i = 0; i < 7; i++) {
            H += (i + 1) * (topLine[(x + 8) + i] - topLine[(x + 6) - i]);
        }
        int H2 = H + ((topLine[x + 15] - topLeft[0]) * 8);
        int V = 0;
        for (int j = 0; j < 7; j++) {
            V += (j + 1) * (leftRow[j + 8] - leftRow[6 - j]);
        }
        int c = (((V + ((leftRow[15] - topLeft[0]) * 8)) * 5) + 32) >> 6;
        int b = ((H2 * 5) + 32) >> 6;
        int a = (leftRow[15] + topLine[x + 15]) * 16;
        int off = 0;
        for (int j2 = 0; j2 < 16; j2++) {
            int i2 = 0;
            while (i2 < 16) {
                int val = MathUtil.clip((((((i2 - 7) * b) + a) + ((j2 - 7) * c)) + 16) >> 5, 0, 255);
                residual[off] = MathUtil.clip(residual[off] + val, 0, 255);
                i2++;
                off++;
            }
        }
    }
}
