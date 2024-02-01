package org.jcodec.codecs.mpeg12;

import android.support.v4.internal.view.SupportMenu;
import java.lang.reflect.Array;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class MPEGPred {
    private int chromaFormat;
    private int[][] fCode;
    protected int[][][] mvPred = (int[][][]) Array.newInstance(Integer.TYPE, 2, 2, 2);
    private boolean topFieldFirst;

    public MPEGPred(int[][] fCode, int chromaFormat, boolean topFieldFirst) {
        this.fCode = fCode;
        this.chromaFormat = chromaFormat;
        this.topFieldFirst = topFieldFirst;
    }

    public MPEGPred(MPEGPred other) {
        this.fCode = other.fCode;
        this.chromaFormat = other.chromaFormat;
        this.topFieldFirst = other.topFieldFirst;
    }

    public void predictEvenEvenSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef;
        int offRef2 = (((refY << refVertStep) + refVertOff) * refW) + refX;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                offRef = offRef2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    offRef2 = offRef + 1;
                    tgt[offTgt] = ref[offRef];
                    j++;
                }
            }
            offRef2 = offRef + lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    public void predictEvenOddSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((refY << refVertStep) + refVertOff) * refW) + refX;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = ((ref[offRef] + ref[offRef + 1]) + 1) >> 1;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    public void predictOddEvenSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((refY << refVertStep) + refVertOff) * refW) + refX;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        int stride = refW << refVertStep;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = ((ref[offRef] + ref[offRef + stride]) + 1) >> 1;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    public void predictOddOddSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int offTgt;
        int offRef = (((refY << refVertStep) + refVertOff) * refW) + refX;
        int offTgt2 = tgtW * tgtY;
        int lfRef = (refW << refVertStep) - tgtW;
        int lfTgt = tgtVertStep * tgtW;
        int stride = refW << refVertStep;
        for (int i = 0; i < tgtH; i++) {
            int j = 0;
            while (true) {
                offTgt = offTgt2;
                if (j < tgtW) {
                    offTgt2 = offTgt + 1;
                    tgt[offTgt] = ((((ref[offRef] + ref[offRef + 1]) + ref[offRef + stride]) + ref[(offRef + stride) + 1]) + 3) >> 2;
                    offRef++;
                    j++;
                }
            }
            offRef += lfRef;
            offTgt2 = offTgt + lfTgt;
        }
    }

    protected final int getPix1(int[] ref, int refW, int refH, int x, int y, int refVertStep, int refVertOff) {
        return ref[(MathUtil.clip(y, 0, (refH - (1 << refVertStep)) + refVertOff) * refW) + MathUtil.clip(x, 0, refW - 1)];
    }

    protected final int getPix2(int[] ref, int refW, int refH, int x1, int y1, int x2, int y2, int refVertStep, int refVertOff) {
        int lastLine = (refH - (1 << refVertStep)) + refVertOff;
        return ((ref[(MathUtil.clip(y1, 0, lastLine) * refW) + MathUtil.clip(x1, 0, refW - 1)] + ref[(MathUtil.clip(y2, 0, lastLine) * refW) + MathUtil.clip(x2, 0, refW - 1)]) + 1) >> 1;
    }

    protected final int getPix4(int[] ref, int refW, int refH, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int refVertStep, int refVertOff) {
        int lastLine = (refH - (1 << refVertStep)) + refVertOff;
        return ((((ref[(MathUtil.clip(y1, 0, lastLine) * refW) + MathUtil.clip(x1, 0, refW - 1)] + ref[(MathUtil.clip(y2, 0, lastLine) * refW) + MathUtil.clip(x2, 0, refW - 1)]) + ref[(MathUtil.clip(y3, 0, lastLine) * refW) + MathUtil.clip(x3, 0, refW - 1)]) + ref[(MathUtil.clip(y4, 0, lastLine) * refW) + MathUtil.clip(x4, 0, refW - 1)]) + 3) >> 2;
    }

    public void predictEvenEvenUnSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y = ((j + refY) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix1(ref, refW, refH, i + refX, y, refVertStep, refVertOff);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    public void predictEvenOddUnSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y = ((j + refY) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix2(ref, refW, refH, i + refX, y, i + refX + 1, y, refVertStep, refVertOff);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    public void predictOddEvenUnSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y1 = ((j + refY) << refVertStep) + refVertOff;
            int y2 = (((j + refY) + 1) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix2(ref, refW, refH, i + refX, y1, i + refX, y2, refVertStep, refVertOff);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    public void predictOddOddUnSafe(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int tgtOff;
        int tgtOff2 = tgtW * tgtY;
        int jump = tgtVertStep * tgtW;
        for (int j = 0; j < tgtH; j++) {
            int y1 = ((j + refY) << refVertStep) + refVertOff;
            int y2 = (((j + refY) + 1) << refVertStep) + refVertOff;
            int i = 0;
            while (true) {
                tgtOff = tgtOff2;
                if (i < tgtW) {
                    int ptX = i + refX;
                    tgtOff2 = tgtOff + 1;
                    tgt[tgtOff] = getPix4(ref, refW, refH, ptX, y1, ptX + 1, y1, ptX, y2, ptX + 1, y2, refVertStep, refVertOff);
                    i++;
                }
            }
            tgtOff2 = tgtOff + jump;
        }
    }

    public void predictPlane(int[] ref, int refX, int refY, int refW, int refH, int refVertStep, int refVertOff, int[] tgt, int tgtY, int tgtW, int tgtH, int tgtVertStep) {
        int rx2 = refX >> 1;
        int ry = refY >> 1;
        boolean safe = rx2 >= 0 && ry >= 0 && rx2 + tgtW < refW && ((ry + tgtH) << refVertStep) < refH;
        if ((refX & 1) == 0) {
            if ((refY & 1) == 0) {
                if (safe) {
                    predictEvenEvenSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                    return;
                } else {
                    predictEvenEvenUnSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                    return;
                }
            }
            if (safe) {
                predictOddEvenSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            } else {
                predictOddEvenUnSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            }
        }
        if ((refY & 1) == 0) {
            if (safe) {
                predictEvenOddSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            } else {
                predictEvenOddUnSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
                return;
            }
        }
        if (safe) {
            predictOddOddSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
        } else {
            predictOddOddUnSafe(ref, rx2, ry, refW, refH, refVertStep, refVertOff, tgt, tgtY, tgtW, tgtH, tgtVertStep);
        }
    }

    public void predictInField(Picture[] reference, int x, int y, int[][] mbPix, BitReader bits, int motionType, int backward, int fieldNo) {
        switch (motionType) {
            case 1:
                predict16x16Field(reference, x, y, bits, backward, mbPix);
                return;
            case 2:
                predict16x8MC(reference, x, y, bits, backward, mbPix, 0, 0);
                predict16x8MC(reference, x, y, bits, backward, mbPix, 8, 1);
                return;
            case 3:
                predict16x16DualPrimeField(reference, x, y, bits, mbPix, fieldNo);
                return;
            default:
                return;
        }
    }

    public void predictInFrame(Picture reference, int x, int y, int[][] mbPix, BitReader in, int motionType, int backward, int spatial_temporal_weight_code) {
        Picture[] refs = {reference, reference};
        switch (motionType) {
            case 1:
                predictFieldInFrame(reference, x, y, mbPix, in, backward, spatial_temporal_weight_code);
                return;
            case 2:
                predict16x16Frame(reference, x, y, in, backward, mbPix);
                return;
            case 3:
                predict16x16DualPrimeFrame(refs, x, y, in, backward, mbPix);
                return;
            default:
                return;
        }
    }

    private void predict16x16DualPrimeField(Picture[] reference, int x, int y, BitReader bits, int[][] mbPix, int fieldNo) {
        int vect1X = mvectDecode(bits, this.fCode[0][0], this.mvPred[0][0][0]);
        int dmX = MPEGConst.vlcDualPrime.readVLC(bits) - 1;
        int vect1Y = mvectDecode(bits, this.fCode[0][1], this.mvPred[0][0][1]);
        int dmY = MPEGConst.vlcDualPrime.readVLC(bits) - 1;
        int vect2X = dpXField(vect1X, dmX, 1 - fieldNo);
        int vect2Y = dpYField(vect1Y, dmY, 1 - fieldNo);
        int ch = this.chromaFormat == 1 ? 1 : 0;
        int cw = this.chromaFormat == 3 ? 0 : 1;
        int sh = this.chromaFormat == 1 ? 2 : 1;
        int sw = this.chromaFormat == 3 ? 1 : 2;
        int[][] mbPix1 = (int[][]) Array.newInstance(Integer.TYPE, 3, 256);
        int[][] mbPix2 = (int[][]) Array.newInstance(Integer.TYPE, 3, 256);
        int refX1 = (x << 1) + vect1X;
        int refY1 = (y << 1) + vect1Y;
        int refX1Chr = ((x << 1) >> cw) + (vect1X / sw);
        int refY1Chr = ((y << 1) >> ch) + (vect1Y / sh);
        predictPlane(reference[fieldNo].getPlaneData(0), refX1, refY1, reference[fieldNo].getPlaneWidth(0), reference[fieldNo].getPlaneHeight(0), 1, fieldNo, mbPix1[0], 0, 16, 16, 0);
        predictPlane(reference[fieldNo].getPlaneData(1), refX1Chr, refY1Chr, reference[fieldNo].getPlaneWidth(1), reference[fieldNo].getPlaneHeight(1), 1, fieldNo, mbPix1[1], 0, 16 >> cw, 16 >> ch, 0);
        predictPlane(reference[fieldNo].getPlaneData(2), refX1Chr, refY1Chr, reference[fieldNo].getPlaneWidth(2), reference[fieldNo].getPlaneHeight(2), 1, fieldNo, mbPix1[2], 0, 16 >> cw, 16 >> ch, 0);
        int refX2 = (x << 1) + vect2X;
        int refY2 = (y << 1) + vect2Y;
        int refX2Chr = ((x << 1) >> cw) + (vect2X / sw);
        int refY2Chr = ((y << 1) >> ch) + (vect2Y / sh);
        int opposite = 1 - fieldNo;
        predictPlane(reference[opposite].getPlaneData(0), refX2, refY2, reference[opposite].getPlaneWidth(0), reference[opposite].getPlaneHeight(0), 1, opposite, mbPix2[0], 0, 16, 16, 0);
        predictPlane(reference[opposite].getPlaneData(1), refX2Chr, refY2Chr, reference[opposite].getPlaneWidth(1), reference[opposite].getPlaneHeight(1), 1, opposite, mbPix2[1], 0, 16 >> cw, 16 >> ch, 0);
        predictPlane(reference[opposite].getPlaneData(2), refX2Chr, refY2Chr, reference[opposite].getPlaneWidth(2), reference[opposite].getPlaneHeight(2), 1, opposite, mbPix2[2], 0, 16 >> cw, 16 >> ch, 0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < mbPix[i].length; j++) {
                mbPix[i][j] = ((mbPix1[i][j] + mbPix2[i][j]) + 1) >> 1;
            }
        }
        int[] iArr = this.mvPred[1][0];
        this.mvPred[0][0][0] = vect1X;
        iArr[0] = vect1X;
        int[] iArr2 = this.mvPred[1][0];
        this.mvPred[0][0][1] = vect1Y;
        iArr2[1] = vect1Y;
    }

    private final int dpYField(int vect1y, int dmY, int topField) {
        return (((vect1y > 0 ? 1 : 0) + vect1y) >> 1) + (1 - (topField << 1)) + dmY;
    }

    private final int dpXField(int vect1x, int dmX, int topField) {
        return (((vect1x > 0 ? 1 : 0) + vect1x) >> 1) + dmX;
    }

    private void predict16x8MC(Picture[] reference, int x, int y, BitReader bits, int backward, int[][] mbPix, int vertPos, int vectIdx) {
        int field = bits.read1Bit();
        predictGeneric(reference[field], x, y + vertPos, bits, backward, mbPix, vertPos, 16, 8, 1, field, 0, vectIdx, 0);
    }

    protected void predict16x16Field(Picture[] reference, int x, int y, BitReader bits, int backward, int[][] mbPix) {
        int field = bits.read1Bit();
        predictGeneric(reference[field], x, y, bits, backward, mbPix, 0, 16, 16, 1, field, 0, 0, 0);
        this.mvPred[1][backward][0] = this.mvPred[0][backward][0];
        this.mvPred[1][backward][1] = this.mvPred[0][backward][1];
    }

    private void predict16x16DualPrimeFrame(Picture[] reference, int x, int y, BitReader bits, int backward, int[][] mbPix) {
        int vect1X = mvectDecode(bits, this.fCode[0][0], this.mvPred[0][0][0]);
        int dmX = MPEGConst.vlcDualPrime.readVLC(bits) - 1;
        int vect1Y = mvectDecode(bits, this.fCode[0][1], this.mvPred[0][0][1] >> 1);
        int dmY = MPEGConst.vlcDualPrime.readVLC(bits) - 1;
        int m = this.topFieldFirst ? 1 : 3;
        int vect2X = (((vect1X > 0 ? 1 : 0) + (vect1X * m)) >> 1) + dmX;
        int vect2Y = ((((vect1Y > 0 ? 1 : 0) + (vect1Y * m)) >> 1) + dmY) - 1;
        int m2 = 4 - m;
        int vect3X = (((vect1X > 0 ? 1 : 0) + (vect1X * m2)) >> 1) + dmX;
        int vect3Y = (((vect1Y > 0 ? 1 : 0) + (vect1Y * m2)) >> 1) + dmY + 1;
        int ch = this.chromaFormat == 1 ? 1 : 0;
        int cw = this.chromaFormat == 3 ? 0 : 1;
        int sh = this.chromaFormat == 1 ? 2 : 1;
        int sw = this.chromaFormat == 3 ? 1 : 2;
        int[][] mbPix1 = (int[][]) Array.newInstance(Integer.TYPE, 3, 256);
        int[][] mbPix2 = (int[][]) Array.newInstance(Integer.TYPE, 3, 256);
        int refX1 = (x << 1) + vect1X;
        int refY1 = y + vect1Y;
        int refX1Chr = ((x << 1) >> cw) + (vect1X / sw);
        int refY1Chr = (y >> ch) + (vect1Y / sh);
        predictPlane(reference[0].getPlaneData(0), refX1, refY1, reference[0].getPlaneWidth(0), reference[0].getPlaneHeight(0), 1, 0, mbPix1[0], 0, 16, 8, 1);
        predictPlane(reference[0].getPlaneData(1), refX1Chr, refY1Chr, reference[0].getPlaneWidth(1), reference[0].getPlaneHeight(1), 1, 0, mbPix1[1], 0, 16 >> cw, 8 >> ch, 1);
        predictPlane(reference[0].getPlaneData(2), refX1Chr, refY1Chr, reference[0].getPlaneWidth(2), reference[0].getPlaneHeight(2), 1, 0, mbPix1[2], 0, 16 >> cw, 8 >> ch, 1);
        predictPlane(reference[1].getPlaneData(0), refX1, refY1, reference[1].getPlaneWidth(0), reference[1].getPlaneHeight(0), 1, 1, mbPix1[0], 1, 16, 8, 1);
        predictPlane(reference[1].getPlaneData(1), refX1Chr, refY1Chr, reference[1].getPlaneWidth(1), reference[1].getPlaneHeight(1), 1, 1, mbPix1[1], 1, 16 >> cw, 8 >> ch, 1);
        predictPlane(reference[1].getPlaneData(2), refX1Chr, refY1Chr, reference[1].getPlaneWidth(2), reference[1].getPlaneHeight(2), 1, 1, mbPix1[2], 1, 16 >> cw, 8 >> ch, 1);
        int refX2 = (x << 1) + vect2X;
        int refY2 = y + vect2Y;
        int refX2Chr = ((x << 1) >> cw) + (vect2X / sw);
        int refY2Chr = (y >> ch) + (vect2Y / sh);
        predictPlane(reference[1].getPlaneData(0), refX2, refY2, reference[1].getPlaneWidth(0), reference[1].getPlaneHeight(0), 1, 1, mbPix2[0], 0, 16, 8, 1);
        predictPlane(reference[1].getPlaneData(1), refX2Chr, refY2Chr, reference[1].getPlaneWidth(1), reference[1].getPlaneHeight(1), 1, 1, mbPix2[1], 0, 16 >> cw, 8 >> ch, 1);
        predictPlane(reference[1].getPlaneData(2), refX2Chr, refY2Chr, reference[1].getPlaneWidth(2), reference[1].getPlaneHeight(2), 1, 1, mbPix2[2], 0, 16 >> cw, 8 >> ch, 1);
        int refX3 = (x << 1) + vect3X;
        int refY3 = y + vect3Y;
        int refX3Chr = ((x << 1) >> cw) + (vect3X / sw);
        int refY3Chr = (y >> ch) + (vect3Y / sh);
        predictPlane(reference[0].getPlaneData(0), refX3, refY3, reference[0].getPlaneWidth(0), reference[0].getPlaneHeight(0), 1, 0, mbPix2[0], 1, 16, 8, 1);
        predictPlane(reference[0].getPlaneData(1), refX3Chr, refY3Chr, reference[0].getPlaneWidth(1), reference[0].getPlaneHeight(1), 1, 0, mbPix2[1], 1, 16 >> cw, 8 >> ch, 1);
        predictPlane(reference[0].getPlaneData(2), refX3Chr, refY3Chr, reference[0].getPlaneWidth(2), reference[0].getPlaneHeight(2), 1, 0, mbPix2[2], 1, 16 >> cw, 8 >> ch, 1);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < mbPix[i].length; j++) {
                mbPix[i][j] = ((mbPix1[i][j] + mbPix2[i][j]) + 1) >> 1;
            }
        }
        int[] iArr = this.mvPred[1][0];
        this.mvPred[0][0][0] = vect1X;
        iArr[0] = vect1X;
        int[] iArr2 = this.mvPred[1][0];
        int i2 = vect1Y << 1;
        this.mvPred[0][0][1] = i2;
        iArr2[1] = i2;
    }

    protected void predict16x16Frame(Picture reference, int x, int y, BitReader bits, int backward, int[][] mbPix) {
        predictGeneric(reference, x, y, bits, backward, mbPix, 0, 16, 16, 0, 0, 0, 0, 0);
        this.mvPred[1][backward][0] = this.mvPred[0][backward][0];
        this.mvPred[1][backward][1] = this.mvPred[0][backward][1];
    }

    private final int mvectDecode(BitReader bits, int fcode, int pred) {
        int code = MPEGConst.vlcMotionCode.readVLC(bits);
        if (code != 0) {
            if (code < 0) {
                return SupportMenu.USER_MASK;
            }
            int sign = bits.read1Bit();
            int shift = fcode - 1;
            int val = code;
            if (shift > 0) {
                val = (((val - 1) << shift) | bits.readNBit(shift)) + 1;
            }
            if (sign != 0) {
                val = -val;
            }
            return sign_extend(val + pred, shift + 5);
        }
        return pred;
    }

    private final int sign_extend(int val, int bits) {
        int shift = 32 - bits;
        return (val << shift) >> shift;
    }

    protected void predictGeneric(Picture reference, int x, int y, BitReader bits, int backward, int[][] mbPix, int tgtY, int blkW, int blkH, int isSrcField, int srcField, int isDstField, int vectIdx, int predScale) {
        int vectX = mvectDecode(bits, this.fCode[backward][0], this.mvPred[vectIdx][backward][0]);
        int vectY = mvectDecode(bits, this.fCode[backward][1], this.mvPred[vectIdx][backward][1] >> predScale);
        predictMB(reference, x << 1, vectX, y << 1, vectY, blkW, blkH, isSrcField, srcField, mbPix, tgtY, isDstField);
        this.mvPred[vectIdx][backward][0] = vectX;
        this.mvPred[vectIdx][backward][1] = vectY << predScale;
    }

    private void predictFieldInFrame(Picture reference, int x, int y, int[][] mbPix, BitReader bits, int backward, int spatial_temporal_weight_code) {
        int y2 = y >> 1;
        int field = bits.read1Bit();
        predictGeneric(reference, x, y2, bits, backward, mbPix, 0, 16, 8, 1, field, 1, 0, 1);
        if (spatial_temporal_weight_code == 0 || spatial_temporal_weight_code == 1) {
            predictGeneric(reference, x, y2, bits, backward, mbPix, 1, 16, 8, 1, bits.read1Bit(), 1, 1, 1);
            return;
        }
        this.mvPred[1][backward][0] = this.mvPred[0][backward][0];
        this.mvPred[1][backward][1] = this.mvPred[0][backward][1];
        predictMB(reference, this.mvPred[1][backward][0], 0, this.mvPred[1][backward][1], 0, 16, 8, 1, 1 - field, mbPix, 1, 1);
    }

    public void predictMB(Picture ref, int refX, int vectX, int refY, int vectY, int blkW, int blkH, int refVertStep, int refVertOff, int[][] tgt, int tgtY, int tgtVertStep) {
        int ch = this.chromaFormat == 1 ? 1 : 0;
        int cw = this.chromaFormat == 3 ? 0 : 1;
        int sh = this.chromaFormat == 1 ? 2 : 1;
        int sw = this.chromaFormat == 3 ? 1 : 2;
        predictPlane(ref.getPlaneData(0), refX + vectX, refY + vectY, ref.getPlaneWidth(0), ref.getPlaneHeight(0), refVertStep, refVertOff, tgt[0], tgtY, blkW, blkH, tgtVertStep);
        predictPlane(ref.getPlaneData(1), (vectX / sw) + (refX >> cw), (vectY / sh) + (refY >> ch), ref.getPlaneWidth(1), ref.getPlaneHeight(1), refVertStep, refVertOff, tgt[1], tgtY, blkW >> cw, blkH >> ch, tgtVertStep);
        predictPlane(ref.getPlaneData(2), (vectX / sw) + (refX >> cw), (vectY / sh) + (refY >> ch), ref.getPlaneWidth(2), ref.getPlaneHeight(2), refVertStep, refVertOff, tgt[2], tgtY, blkW >> cw, blkH >> ch, tgtVertStep);
    }

    public void predict16x16NoMV(Picture picture, int x, int y, int pictureStructure, int backward, int[][] mbPix) {
        if (pictureStructure == 3) {
            predictMB(picture, x << 1, this.mvPred[0][backward][0], y << 1, this.mvPred[0][backward][1], 16, 16, 0, 0, mbPix, 0, 0);
        } else {
            predictMB(picture, x << 1, this.mvPred[0][backward][0], y << 1, this.mvPred[0][backward][1], 16, 16, 1, pictureStructure - 1, mbPix, 0, 0);
        }
    }

    public void reset() {
        int[] iArr = this.mvPred[0][0];
        int[] iArr2 = this.mvPred[0][0];
        int[] iArr3 = this.mvPred[0][1];
        int[] iArr4 = this.mvPred[0][1];
        int[] iArr5 = this.mvPred[1][0];
        int[] iArr6 = this.mvPred[1][0];
        int[] iArr7 = this.mvPred[1][1];
        this.mvPred[1][1][1] = 0;
        iArr7[0] = 0;
        iArr6[1] = 0;
        iArr5[0] = 0;
        iArr4[1] = 0;
        iArr3[0] = 0;
        iArr2[1] = 0;
        iArr[0] = 0;
    }
}
