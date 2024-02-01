package org.jcodec.codecs.h264.decode;

/* loaded from: classes.dex */
public class Interpolator {
    private static int PADDING = 16;

    public int[] interpolateChroma(int[] src, int width, int height) {
        int refWidth = width << 3;
        int refHeight = height << 3;
        int[] result = new int[refWidth * refHeight];
        int j = 0;
        while (j < height) {
            int i = 0;
            while (i < width) {
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        int resultOff = (((j << 3) + y) * refWidth) + (i << 3) + x;
                        int w00 = (j * width) + i;
                        int w01 = w00 + (j < height + (-1) ? width : 0);
                        int w10 = w00 + (i < width + (-1) ? 1 : 0);
                        int w11 = (w10 + w01) - w00;
                        int eMx = 8 - x;
                        int eMy = 8 - y;
                        result[resultOff] = ((((((eMx * eMy) * src[w00]) + ((x * eMy) * src[w10])) + ((eMx * y) * src[w01])) + ((x * y) * src[w11])) + 32) >> 6;
                    }
                }
                i++;
            }
            j++;
        }
        return result;
    }

    public int[] interpolateLuma(int[] src, int width, int height) {
        int refWidth = ((PADDING * 2) + width) * 4;
        int refHeight = ((PADDING * 2) + height) * 4;
        int[] result = new int[refWidth * refHeight];
        fillFullPel(src, width, height, result);
        scanHPelHorizontalWithRound(refWidth, refHeight, result);
        scanHPelVertical(refWidth, refHeight, result);
        scanHPelCenterWidhRound(refWidth, refHeight, result);
        roundHPelVertical(refWidth, refHeight, result);
        scanQPel(refWidth, refHeight, result);
        return result;
    }

    protected void scanQPel(int width, int height, int[] result) {
        int rightBottomHpel;
        int j = 0;
        while (j < height) {
            int i = 0;
            while (i < width) {
                int pos = (j * width) + i;
                int bottomHpel = j < height + (-2) ? result[(width * 2) + pos] : result[pos];
                int rightHpel = i < width + (-2) ? result[pos + 2] : result[pos];
                if (j < height - 2 && i < width - 2) {
                    rightBottomHpel = result[(width * 2) + pos + 2];
                } else if (j < height - 2) {
                    rightBottomHpel = result[(width * 2) + pos];
                } else if (i < width - 2) {
                    rightBottomHpel = result[pos + 2];
                } else {
                    rightBottomHpel = result[pos];
                }
                result[pos + width] = ((result[pos] + bottomHpel) + 1) >> 1;
                result[pos + 1] = ((result[pos] + rightHpel) + 1) >> 1;
                if (i % 4 == j % 4) {
                    result[pos + width + 1] = ((rightHpel + bottomHpel) + 1) >> 1;
                } else {
                    result[pos + width + 1] = ((result[pos] + rightBottomHpel) + 1) >> 1;
                }
                i += 2;
            }
            j += 2;
        }
    }

    protected void fillFullPel(int[] src, int width, int height, int[] result) {
        int stride = ((PADDING * 2) + width) * 4;
        for (int j = 0; j < height; j++) {
            int y = (PADDING + j) * 4;
            for (int i = 0; i < width; i++) {
                int x = (PADDING + i) * 4;
                result[(y * stride) + x] = src[(j * width) + i];
            }
            for (int i2 = 0; i2 < PADDING; i2++) {
                int x2 = i2 * 4;
                result[(y * stride) + x2] = src[j * width];
            }
            for (int i3 = width + PADDING; i3 < (PADDING * 2) + width; i3++) {
                int x3 = i3 * 4;
                result[(y * stride) + x3] = src[((j * width) + width) - 1];
            }
        }
        for (int j2 = 0; j2 < PADDING; j2++) {
            int y2 = j2 * 4;
            for (int i4 = 0; i4 < width; i4++) {
                int x4 = (PADDING + i4) * 4;
                result[(y2 * stride) + x4] = src[i4];
            }
            for (int i5 = 0; i5 < PADDING; i5++) {
                int x5 = i5 * 4;
                result[(y2 * stride) + x5] = src[0];
            }
            for (int i6 = width + PADDING; i6 < (PADDING * 2) + width; i6++) {
                int x6 = i6 * 4;
                result[(y2 * stride) + x6] = src[width - 1];
            }
        }
        for (int j3 = height + PADDING; j3 < (PADDING * 2) + height; j3++) {
            int y3 = j3 * 4;
            for (int i7 = 0; i7 < width; i7++) {
                int x7 = (PADDING + i7) * 4;
                result[(y3 * stride) + x7] = src[((height - 1) * width) + i7];
            }
            for (int i8 = 0; i8 < PADDING; i8++) {
                int x8 = i8 * 4;
                result[(y3 * stride) + x8] = src[(height - 1) * width];
            }
            for (int i9 = width + PADDING; i9 < (PADDING * 2) + width; i9++) {
                int x9 = i9 * 4;
                result[(y3 * stride) + x9] = src[(((height - 1) * width) + width) - 1];
            }
        }
    }

    protected void scanHPelVertical(int width, int height, int[] result) {
        for (int i = 0; i < width; i += 4) {
            int E = result[i];
            int F = result[i];
            int G = result[i];
            int H = result[(width * 4) + i];
            int I = result[(width * 8) + i];
            int J = result[(width * 12) + i];
            for (int j = 0; j < height; j += 4) {
                int val = ((((E - (F * 5)) + (G * 20)) + (H * 20)) - (I * 5)) + J;
                result[((j + 2) * width) + i] = val;
                E = F;
                F = G;
                G = H;
                H = I;
                I = J;
                int nextPix = j + 16;
                if (nextPix < height) {
                    J = result[(nextPix * width) + i];
                }
            }
        }
    }

    protected void roundHPelVertical(int width, int height, int[] result) {
        for (int i = 0; i < width; i += 4) {
            for (int j = 0; j < height; j += 4) {
                result[((j + 2) * width) + i] = roundAndClip32(result[((j + 2) * width) + i]);
            }
        }
    }

    protected void scanHPelHorizontalWithRound(int width, int height, int[] result) {
        for (int j = 0; j < height; j += 4) {
            int lineStart = j * width;
            int E = result[lineStart];
            int F = result[lineStart];
            int G = result[lineStart];
            int H = result[lineStart + 4];
            int I = result[lineStart + 8];
            int J = result[lineStart + 12];
            for (int i = 0; i < width; i += 4) {
                int val = ((((E - (F * 5)) + (G * 20)) + (H * 20)) - (I * 5)) + J;
                result[lineStart + i + 2] = roundAndClip32(val);
                E = F;
                F = G;
                G = H;
                H = I;
                I = J;
                int nextPix = i + 16;
                if (nextPix < width) {
                    J = result[lineStart + nextPix];
                }
            }
        }
    }

    protected void scanHPelCenterWidhRound(int width, int height, int[] result) {
        for (int j = 0; j < height; j += 4) {
            int lineStart = (j + 2) * width;
            int E = result[lineStart];
            int F = result[lineStart];
            int G = result[lineStart];
            int H = result[lineStart + 4];
            int I = result[lineStart + 8];
            int J = result[lineStart + 12];
            for (int i = 0; i < width; i += 4) {
                int val = ((((E - (F * 5)) + (G * 20)) + (H * 20)) - (I * 5)) + J;
                result[lineStart + i + 2] = roundAndClip1024(val);
                E = F;
                F = G;
                G = H;
                H = I;
                I = J;
                int nextPix = i + 16;
                if (nextPix < width) {
                    J = result[lineStart + nextPix];
                }
            }
        }
    }

    private int roundAndClip32(int val) {
        int val2 = (val + 16) >> 5;
        if (val2 < 0) {
            return 0;
        }
        if (val2 > 255) {
            return 255;
        }
        return val2;
    }

    private int roundAndClip1024(int val) {
        int val2 = (val + 512) >> 10;
        if (val2 < 0) {
            return 0;
        }
        if (val2 > 255) {
            return 255;
        }
        return val2;
    }
}
