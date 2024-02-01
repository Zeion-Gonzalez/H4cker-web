package org.jcodec.codecs.mpeg12;

import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class MPEGPredQuad extends MPEGPred {
    public MPEGPredQuad(MPEGPred other) {
        super(other);
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGPred
    public void predictPlane(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int rx2 = refX >> 2;
        int ry = refY >> 2;
        boolean safe = rx2 >= 0 && ry >= 0 && rx2 + tgtW < refW && ((ry + tgtH) << refVertStep) < refH;
        if ((refX & 3) == 0) {
            if ((refY & 3) == 0) {
                if (safe) {
                    predictEvenEvenSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                    return;
                } else {
                    predictEvenEvenUnSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                    return;
                }
            }
            if (safe) {
                predictOddEvenSafe(ref, rx2, ry, refY - (ry << 2), refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            } else {
                predictOddEvenUnSafe(ref, rx2, ry, refY - (ry << 2), refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            }
        }
        if ((refY & 3) == 0) {
            if (safe) {
                predictEvenOddSafe(ref, rx2, refX - (rx2 << 2), ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            } else {
                predictEvenOddUnSafe(ref, rx2, refX - (rx2 << 2), ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            }
        }
        if (safe) {
            predictOddOddSafe(ref, rx2, refX - (rx2 << 2), ry, refY - (ry << 2), refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
        } else {
            predictOddOddUnSafe(ref, rx2, refX - (rx2 << 2), ry, refY - (ry << 2), refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
        }
    }

    private void predictOddOddUnSafe(int[] ref, int rx2, int ix, int ry, int iy, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y1 = ((j + ry) << refVertStep) + refVertOff;
            int y2 = (((j + ry) + 1) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    int ptX = i + rx2;
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix4(ref, refW, refH, ptX, y1, ptX + 1, y1, ptX, y2, ptX + 1, y2, refVertStep, refVertOff, ix, iy);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    protected int getPix4(int[] ref, int refW, int refH, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int refVertStep, int refVertOff, int ix, int iy) {
        int lastLine = (refH - (1 << refVertStep)) + refVertOff;
        int nix = 4 - ix;
        int niy = 4 - iy;
        return ((((((ref[(MathUtil.clip(y1, 0, lastLine) * refW) + MathUtil.clip(x1, 0, refW - 1)] * nix) * niy) + ((ref[(MathUtil.clip(y2, 0, lastLine) * refW) + MathUtil.clip(x2, 0, refW - 1)] * ix) * niy)) + ((ref[(MathUtil.clip(y3, 0, lastLine) * refW) + MathUtil.clip(x3, 0, refW - 1)] * nix) * iy)) + ((ref[(MathUtil.clip(y4, 0, lastLine) * refW) + MathUtil.clip(x4, 0, refW - 1)] * ix) * iy)) + 8) >> 4;
    }

    private void predictOddOddSafe(int[] ref, int rx2, int ix, int ry, int iy, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((ry << refVertStep) + refVertOff) * refW) + rx2;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        int stride = refW << refVertStep;
        int nix = 4 - ix;
        int niy = 4 - iy;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = ((((((ref[offRef] * nix) * niy) + ((ref[offRef + 1] * ix) * niy)) + ((ref[offRef + stride] * nix) * iy)) + ((ref[(offRef + stride) + 1] * ix) * iy)) + 8) >> 4;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    protected int getPix2(int[] ref, int refW, int refH, int x1, int y1, int x2, int y2, int refVertStep, int refVertOff, int i) {
        int ni = 4 - i;
        int lastLine = (refH - (1 << refVertStep)) + refVertOff;
        return (((ref[(MathUtil.clip(y1, 0, lastLine) * refW) + MathUtil.clip(x1, 0, refW - 1)] * ni) + (ref[(MathUtil.clip(y2, 0, lastLine) * refW) + MathUtil.clip(x2, 0, refW - 1)] * i)) + 2) >> 2;
    }

    private void predictEvenOddUnSafe(int[] ref, int rx2, int ix, int ry, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y = ((j + ry) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix2(ref, refW, refH, i + rx2, y, i + rx2 + 1, y, refVertStep, refVertOff, ix);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    private void predictEvenOddSafe(int[] ref, int rx2, int ix, int ry, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((ry << refVertStep) + refVertOff) * refW) + rx2;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        int nix = 4 - ix;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = (((ref[offRef] * nix) + (ref[offRef + 1] * ix)) + 2) >> 2;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    private void predictOddEvenUnSafe(int[] ref, int rx2, int ry, int iy, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y1 = ((j + ry) << refVertStep) + refVertOff;
            int y2 = (((j + ry) + 1) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix2(ref, refW, refH, i + rx2, y1, i + rx2, y2, refVertStep, refVertOff, iy);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    private void predictOddEvenSafe(int[] ref, int rx2, int ry, int iy, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((ry << refVertStep) + refVertOff) * refW) + rx2;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        int stride = refW << refVertStep;
        int niy = 4 - iy;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = (((ref[offRef] * niy) + (ref[offRef + stride] * iy)) + 2) >> 2;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }
}
