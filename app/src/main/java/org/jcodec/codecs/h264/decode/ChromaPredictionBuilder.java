package org.jcodec.codecs.h264.decode;

import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class ChromaPredictionBuilder {
    public static void predictWithMode(int[] planeData, int chromaMode, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine, int[] topLeft) {
        switch (chromaMode) {
            case 0:
                predictDC(planeData, mbX, leftAvailable, topAvailable, leftRow, topLine);
                return;
            case 1:
                predictHorizontal(planeData, mbX, leftAvailable, leftRow);
                return;
            case 2:
                predictVertical(planeData, mbX, topAvailable, topLine);
                return;
            case 3:
                predictPlane(planeData, mbX, leftAvailable, topAvailable, leftRow, topLine, topLeft);
                return;
            default:
                return;
        }
    }

    public static void predictDC(int[] planeData, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine) {
        predictDCInside(planeData, 0, 0, mbX, leftAvailable, topAvailable, leftRow, topLine);
        predictDCTopBorder(planeData, 1, 0, mbX, leftAvailable, topAvailable, leftRow, topLine);
        predictDCLeftBorder(planeData, 0, 1, mbX, leftAvailable, topAvailable, leftRow, topLine);
        predictDCInside(planeData, 1, 1, mbX, leftAvailable, topAvailable, leftRow, topLine);
    }

    public static void predictVertical(int[] planeData, int mbX, boolean topAvailable, int[] topLine) {
        int off = 0;
        for (int j = 0; j < 8; j++) {
            int i = 0;
            while (i < 8) {
                planeData[off] = MathUtil.clip(planeData[off] + topLine[(mbX << 3) + i], 0, 255);
                i++;
                off++;
            }
        }
    }

    public static void predictHorizontal(int[] planeData, int mbX, boolean leftAvailable, int[] leftRow) {
        int off = 0;
        for (int j = 0; j < 8; j++) {
            int i = 0;
            while (i < 8) {
                planeData[off] = MathUtil.clip(planeData[off] + leftRow[j], 0, 255);
                i++;
                off++;
            }
        }
    }

    public static void predictDCInside(int[] planeData, int blkX, int blkY, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine) {
        int s0;
        int blkOffX = (blkX << 2) + (mbX << 3);
        int blkOffY = blkY << 2;
        if (leftAvailable && topAvailable) {
            int s02 = 0;
            for (int i = 0; i < 4; i++) {
                s02 += leftRow[i + blkOffY];
            }
            for (int i2 = 0; i2 < 4; i2++) {
                s02 += topLine[blkOffX + i2];
            }
            s0 = (s02 + 4) >> 3;
        } else if (leftAvailable) {
            int s03 = 0;
            for (int i3 = 0; i3 < 4; i3++) {
                s03 += leftRow[blkOffY + i3];
            }
            s0 = (s03 + 2) >> 2;
        } else if (topAvailable) {
            int s04 = 0;
            for (int i4 = 0; i4 < 4; i4++) {
                s04 += topLine[blkOffX + i4];
            }
            s0 = (s04 + 2) >> 2;
        } else {
            s0 = 128;
        }
        int off = (blkY << 5) + (blkX << 2);
        int j = 0;
        while (j < 4) {
            planeData[off] = MathUtil.clip(planeData[off] + s0, 0, 255);
            planeData[off + 1] = MathUtil.clip(planeData[off + 1] + s0, 0, 255);
            planeData[off + 2] = MathUtil.clip(planeData[off + 2] + s0, 0, 255);
            planeData[off + 3] = MathUtil.clip(planeData[off + 3] + s0, 0, 255);
            j++;
            off += 8;
        }
    }

    public static void predictDCTopBorder(int[] planeData, int blkX, int blkY, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine) {
        int s1;
        int blkOffX = (blkX << 2) + (mbX << 3);
        int blkOffY = blkY << 2;
        if (topAvailable) {
            int s12 = 0;
            for (int i = 0; i < 4; i++) {
                s12 += topLine[blkOffX + i];
            }
            s1 = (s12 + 2) >> 2;
        } else if (leftAvailable) {
            int s13 = 0;
            for (int i2 = 0; i2 < 4; i2++) {
                s13 += leftRow[blkOffY + i2];
            }
            s1 = (s13 + 2) >> 2;
        } else {
            s1 = 128;
        }
        int off = (blkY << 5) + (blkX << 2);
        int j = 0;
        while (j < 4) {
            planeData[off] = MathUtil.clip(planeData[off] + s1, 0, 255);
            planeData[off + 1] = MathUtil.clip(planeData[off + 1] + s1, 0, 255);
            planeData[off + 2] = MathUtil.clip(planeData[off + 2] + s1, 0, 255);
            planeData[off + 3] = MathUtil.clip(planeData[off + 3] + s1, 0, 255);
            j++;
            off += 8;
        }
    }

    public static void predictDCLeftBorder(int[] planeData, int blkX, int blkY, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine) {
        int s2;
        int blkOffX = (blkX << 2) + (mbX << 3);
        int blkOffY = blkY << 2;
        if (leftAvailable) {
            int s22 = 0;
            for (int i = 0; i < 4; i++) {
                s22 += leftRow[blkOffY + i];
            }
            s2 = (s22 + 2) >> 2;
        } else if (topAvailable) {
            int s23 = 0;
            for (int i2 = 0; i2 < 4; i2++) {
                s23 += topLine[blkOffX + i2];
            }
            s2 = (s23 + 2) >> 2;
        } else {
            s2 = 128;
        }
        int off = (blkY << 5) + (blkX << 2);
        int j = 0;
        while (j < 4) {
            planeData[off] = MathUtil.clip(planeData[off] + s2, 0, 255);
            planeData[off + 1] = MathUtil.clip(planeData[off + 1] + s2, 0, 255);
            planeData[off + 2] = MathUtil.clip(planeData[off + 2] + s2, 0, 255);
            planeData[off + 3] = MathUtil.clip(planeData[off + 3] + s2, 0, 255);
            j++;
            off += 8;
        }
    }

    public static void predictPlane(int[] planeData, int mbX, boolean leftAvailable, boolean topAvailable, int[] leftRow, int[] topLine, int[] topLeft) {
        int H = 0;
        int blkOffX = mbX << 3;
        for (int i = 0; i < 3; i++) {
            H += (i + 1) * (topLine[(blkOffX + 4) + i] - topLine[(blkOffX + 2) - i]);
        }
        int H2 = H + ((topLine[blkOffX + 7] - topLeft[0]) * 4);
        int V = 0;
        for (int j = 0; j < 3; j++) {
            V += (j + 1) * (leftRow[j + 4] - leftRow[2 - j]);
        }
        int c = (((V + ((leftRow[7] - topLeft[0]) * 4)) * 34) + 32) >> 6;
        int b = ((H2 * 34) + 32) >> 6;
        int a = (leftRow[7] + topLine[blkOffX + 7]) * 16;
        int off = 0;
        for (int j2 = 0; j2 < 8; j2++) {
            int i2 = 0;
            while (i2 < 8) {
                int val = (((((i2 - 3) * b) + a) + ((j2 - 3) * c)) + 16) >> 5;
                planeData[off] = MathUtil.clip(planeData[off] + MathUtil.clip(val, 0, 255), 0, 255);
                i2++;
                off++;
            }
        }
    }
}
