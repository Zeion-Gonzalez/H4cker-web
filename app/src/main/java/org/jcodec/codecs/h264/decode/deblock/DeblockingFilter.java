package org.jcodec.codecs.h264.decode.deblock;

import java.lang.reflect.Array;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class DeblockingFilter {
    private int[][] mbQps;
    private MBType[] mbTypes;
    private int[][][][] mvs;
    private int[][] nCoeff;
    private Picture[][][] refsUsed;
    private SliceHeader[] shs;
    private boolean[] tr8x8Used;
    static int[] alphaTab = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 17, 20, 22, 25, 28, 32, 36, 40, 45, 50, 56, 63, 71, 80, 90, 101, 113, 127, 144, 162, 182, 203, JpegConst.APP2, 255, 255};
    static int[] betaTab = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18};
    static int[][] tcs = {new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 6, 6, 7, 8, 9, 10, 11, 13}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 7, 8, 8, 10, 11, 12, 13, 15, 17}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 6, 6, 7, 8, 9, 10, 11, 13, 14, 16, 18, 20, 23, 25}};
    static int[] inverse = {0, 1, 4, 5, 2, 3, 6, 7, 8, 9, 12, 13, 10, 11, 14, 15};

    public DeblockingFilter(int bitDepthLuma, int bitDepthChroma, int[][] nCoeff, int[][][][] mvs, MBType[] mbTypes, int[][] mbQps, SliceHeader[] shs, boolean[] tr8x8Used, Picture[][][] refsUsed) {
        this.nCoeff = nCoeff;
        this.mvs = mvs;
        this.mbTypes = mbTypes;
        this.mbQps = mbQps;
        this.shs = shs;
        this.tr8x8Used = tr8x8Used;
        this.refsUsed = refsUsed;
    }

    public void deblockFrame(Picture result) {
        ColorSpace color = result.getColor();
        int[][] bsV = (int[][]) Array.newInstance(Integer.TYPE, 4, 4);
        int[][] bsH = (int[][]) Array.newInstance(Integer.TYPE, 4, 4);
        for (int i = 0; i < this.shs.length; i++) {
            calcBsH(result, i, bsH);
            calcBsV(result, i, bsV);
            for (int c = 0; c < color.nComp; c++) {
                fillVerticalEdge(result, c, i, bsV);
                fillHorizontalEdge(result, c, i, bsH);
            }
        }
    }

    private int calcBoundaryStrenth(boolean atMbBoundary, boolean leftIntra, boolean rightIntra, int leftCoeff, int rightCoeff, int[] mvA0, int[] mvB0, int[] mvA1, int[] mvB1, int mbAddrA, int mbAddrB) {
        if (atMbBoundary && (leftIntra || rightIntra)) {
            return 4;
        }
        if (leftIntra || rightIntra) {
            return 3;
        }
        if (leftCoeff > 0 || rightCoeff > 0) {
            return 2;
        }
        int nA = (mvA0[2] == -1 ? 0 : 1) + (mvA1[2] == -1 ? 0 : 1);
        int nB = (mvB0[2] == -1 ? 0 : 1) + (mvB1[2] == -1 ? 0 : 1);
        if (nA != nB) {
            return 1;
        }
        Picture ra0 = mvA0[2] < 0 ? null : this.refsUsed[mbAddrA][0][mvA0[2]];
        Picture ra1 = mvA1[2] < 0 ? null : this.refsUsed[mbAddrA][1][mvA1[2]];
        Picture rb0 = mvB0[2] < 0 ? null : this.refsUsed[mbAddrB][0][mvB0[2]];
        Picture rb1 = mvB1[2] < 0 ? null : this.refsUsed[mbAddrB][1][mvB1[2]];
        if ((ra0 != rb0 && ra0 != rb1) || ((ra1 != rb0 && ra1 != rb1) || ((rb0 != ra0 && rb0 != ra1) || (rb1 != ra0 && rb1 != ra1)))) {
            return 1;
        }
        if (ra0 == ra1 && ra1 == rb0 && rb0 == rb1) {
            return (ra0 == null || !(mvThresh(mvA0, mvB0) || mvThresh(mvA1, mvB0) || mvThresh(mvA0, mvB1) || mvThresh(mvA1, mvB1))) ? 0 : 1;
        }
        if (ra0 == rb0 && ra1 == rb1) {
            return ((ra0 == null || !mvThresh(mvA0, mvB0)) && (ra1 == null || !mvThresh(mvA1, mvB1))) ? 0 : 1;
        }
        if (ra0 == rb1 && ra1 == rb0) {
            return ((ra0 == null || !mvThresh(mvA0, mvB1)) && (ra1 == null || !mvThresh(mvA1, mvB0))) ? 0 : 1;
        }
        return 0;
    }

    private boolean mvThresh(int[] v0, int[] v1) {
        return Math.abs(v0[0] - v1[0]) >= 4 || Math.abs(v0[1] - v1[1]) >= 4;
    }

    private static int getIdxBeta(int sliceBetaOffset, int avgQp) {
        return MathUtil.clip(avgQp + sliceBetaOffset, 0, 51);
    }

    private static int getIdxAlpha(int sliceAlphaC0Offset, int avgQp) {
        return MathUtil.clip(avgQp + sliceAlphaC0Offset, 0, 51);
    }

    private void calcBsH(Picture pic, int mbAddr, int[][] bs) {
        SliceHeader sh = this.shs[mbAddr];
        int mbWidth = sh.sps.pic_width_in_mbs_minus1 + 1;
        int mbX = mbAddr % mbWidth;
        int mbY = mbAddr / mbWidth;
        boolean topAvailable = mbY > 0 && (sh.disable_deblocking_filter_idc != 2 || this.shs[mbAddr - mbWidth] == sh);
        boolean thisIntra = this.mbTypes[mbAddr] != null && this.mbTypes[mbAddr].isIntra();
        if (topAvailable) {
            boolean topIntra = this.mbTypes[mbAddr - mbWidth] != null && this.mbTypes[mbAddr - mbWidth].isIntra();
            for (int blkX = 0; blkX < 4; blkX++) {
                int thisBlkX = (mbX << 2) + blkX;
                int thisBlkY = mbY << 2;
                bs[0][blkX] = calcBoundaryStrenth(true, topIntra, thisIntra, this.nCoeff[thisBlkY][thisBlkX], this.nCoeff[thisBlkY - 1][thisBlkX], this.mvs[0][thisBlkY][thisBlkX], this.mvs[0][thisBlkY - 1][thisBlkX], this.mvs[1][thisBlkY][thisBlkX], this.mvs[1][thisBlkY - 1][thisBlkX], mbAddr, mbAddr - mbWidth);
            }
        }
        for (int blkY = 1; blkY < 4; blkY++) {
            for (int blkX2 = 0; blkX2 < 4; blkX2++) {
                int thisBlkX2 = (mbX << 2) + blkX2;
                int thisBlkY2 = (mbY << 2) + blkY;
                bs[blkY][blkX2] = calcBoundaryStrenth(false, thisIntra, thisIntra, this.nCoeff[thisBlkY2][thisBlkX2], this.nCoeff[thisBlkY2 - 1][thisBlkX2], this.mvs[0][thisBlkY2][thisBlkX2], this.mvs[0][thisBlkY2 - 1][thisBlkX2], this.mvs[1][thisBlkY2][thisBlkX2], this.mvs[1][thisBlkY2 - 1][thisBlkX2], mbAddr, mbAddr);
            }
        }
    }

    private void fillHorizontalEdge(Picture pic, int comp, int mbAddr, int[][] bs) {
        SliceHeader sh = this.shs[mbAddr];
        int mbWidth = sh.sps.pic_width_in_mbs_minus1 + 1;
        int alpha = sh.slice_alpha_c0_offset_div2 << 1;
        int beta = sh.slice_beta_offset_div2 << 1;
        int mbX = mbAddr % mbWidth;
        int mbY = mbAddr / mbWidth;
        boolean topAvailable = mbY > 0 && (sh.disable_deblocking_filter_idc != 2 || this.shs[mbAddr - mbWidth] == sh);
        int curQp = this.mbQps[comp][mbAddr];
        int cW = 2 - pic.getColor().compWidth[comp];
        int cH = 2 - pic.getColor().compHeight[comp];
        if (topAvailable) {
            int topQp = this.mbQps[comp][mbAddr - mbWidth];
            int avgQp = ((topQp + curQp) + 1) >> 1;
            for (int blkX = 0; blkX < 4; blkX++) {
                int thisBlkX = (mbX << 2) + blkX;
                int thisBlkY = mbY << 2;
                filterBlockEdgeHoris(pic, comp, thisBlkX << cW, thisBlkY << cH, getIdxAlpha(alpha, avgQp), getIdxBeta(beta, avgQp), bs[0][blkX], 1 << cW);
            }
        }
        boolean skip4x4 = (comp == 0 && this.tr8x8Used[mbAddr]) || cH == 1;
        for (int blkY = 1; blkY < 4; blkY++) {
            if (!skip4x4 || (blkY & 1) != 1) {
                for (int blkX2 = 0; blkX2 < 4; blkX2++) {
                    int thisBlkX2 = (mbX << 2) + blkX2;
                    int thisBlkY2 = (mbY << 2) + blkY;
                    filterBlockEdgeHoris(pic, comp, thisBlkX2 << cW, thisBlkY2 << cH, getIdxAlpha(alpha, curQp), getIdxBeta(beta, curQp), bs[blkY][blkX2], 1 << cW);
                }
            }
        }
    }

    private void calcBsV(Picture pic, int mbAddr, int[][] bs) {
        SliceHeader sh = this.shs[mbAddr];
        int mbWidth = sh.sps.pic_width_in_mbs_minus1 + 1;
        int mbX = mbAddr % mbWidth;
        int mbY = mbAddr / mbWidth;
        boolean leftAvailable = mbX > 0 && (sh.disable_deblocking_filter_idc != 2 || this.shs[mbAddr + (-1)] == sh);
        boolean thisIntra = this.mbTypes[mbAddr] != null && this.mbTypes[mbAddr].isIntra();
        if (leftAvailable) {
            boolean leftIntra = this.mbTypes[mbAddr + (-1)] != null && this.mbTypes[mbAddr + (-1)].isIntra();
            for (int blkY = 0; blkY < 4; blkY++) {
                int thisBlkX = mbX << 2;
                int thisBlkY = (mbY << 2) + blkY;
                bs[blkY][0] = calcBoundaryStrenth(true, leftIntra, thisIntra, this.nCoeff[thisBlkY][thisBlkX], this.nCoeff[thisBlkY][thisBlkX - 1], this.mvs[0][thisBlkY][thisBlkX], this.mvs[0][thisBlkY][thisBlkX - 1], this.mvs[1][thisBlkY][thisBlkX], this.mvs[1][thisBlkY][thisBlkX - 1], mbAddr, mbAddr - 1);
            }
        }
        for (int blkX = 1; blkX < 4; blkX++) {
            for (int blkY2 = 0; blkY2 < 4; blkY2++) {
                int thisBlkX2 = (mbX << 2) + blkX;
                int thisBlkY2 = (mbY << 2) + blkY2;
                bs[blkY2][blkX] = calcBoundaryStrenth(false, thisIntra, thisIntra, this.nCoeff[thisBlkY2][thisBlkX2], this.nCoeff[thisBlkY2][thisBlkX2 - 1], this.mvs[0][thisBlkY2][thisBlkX2], this.mvs[0][thisBlkY2][thisBlkX2 - 1], this.mvs[1][thisBlkY2][thisBlkX2], this.mvs[1][thisBlkY2][thisBlkX2 - 1], mbAddr, mbAddr);
            }
        }
    }

    private void fillVerticalEdge(Picture pic, int comp, int mbAddr, int[][] bs) {
        SliceHeader sh = this.shs[mbAddr];
        int mbWidth = sh.sps.pic_width_in_mbs_minus1 + 1;
        int alpha = sh.slice_alpha_c0_offset_div2 << 1;
        int beta = sh.slice_beta_offset_div2 << 1;
        int mbX = mbAddr % mbWidth;
        int mbY = mbAddr / mbWidth;
        boolean leftAvailable = mbX > 0 && (sh.disable_deblocking_filter_idc != 2 || this.shs[mbAddr + (-1)] == sh);
        int curQp = this.mbQps[comp][mbAddr];
        int cW = 2 - pic.getColor().compWidth[comp];
        int cH = 2 - pic.getColor().compHeight[comp];
        if (leftAvailable) {
            int leftQp = this.mbQps[comp][mbAddr - 1];
            int avgQpV = ((leftQp + curQp) + 1) >> 1;
            for (int blkY = 0; blkY < 4; blkY++) {
                int thisBlkX = mbX << 2;
                int thisBlkY = (mbY << 2) + blkY;
                filterBlockEdgeVert(pic, comp, thisBlkX << cW, thisBlkY << cH, getIdxAlpha(alpha, avgQpV), getIdxBeta(beta, avgQpV), bs[blkY][0], 1 << cH);
            }
        }
        boolean skip4x4 = (comp == 0 && this.tr8x8Used[mbAddr]) || cW == 1;
        for (int blkX = 1; blkX < 4; blkX++) {
            if (!skip4x4 || (blkX & 1) != 1) {
                for (int blkY2 = 0; blkY2 < 4; blkY2++) {
                    int thisBlkX2 = (mbX << 2) + blkX;
                    int thisBlkY2 = (mbY << 2) + blkY2;
                    filterBlockEdgeVert(pic, comp, thisBlkX2 << cW, thisBlkY2 << cH, getIdxAlpha(alpha, curQp), getIdxBeta(beta, curQp), bs[blkY2][blkX], 1 << cH);
                }
            }
        }
    }

    private void filterBlockEdgeHoris(Picture pic, int comp, int x, int y, int indexAlpha, int indexBeta, int bs, int blkW) {
        int stride = pic.getPlaneWidth(comp);
        int offset = (y * stride) + x;
        for (int pixOff = 0; pixOff < blkW; pixOff++) {
            int p2Idx = (offset - (stride * 3)) + pixOff;
            int p1Idx = (offset - (stride * 2)) + pixOff;
            int p0Idx = (offset - stride) + pixOff;
            int q0Idx = offset + pixOff;
            int q1Idx = offset + stride + pixOff;
            int q2Idx = (stride * 2) + offset + pixOff;
            if (bs == 4) {
                int p3Idx = (offset - (stride * 4)) + pixOff;
                int q3Idx = (stride * 3) + offset + pixOff;
                filterBs4(indexAlpha, indexBeta, pic.getPlaneData(comp), p3Idx, p2Idx, p1Idx, p0Idx, q0Idx, q1Idx, q2Idx, q3Idx, comp != 0);
            } else if (bs > 0) {
                filterBs(bs, indexAlpha, indexBeta, pic.getPlaneData(comp), p2Idx, p1Idx, p0Idx, q0Idx, q1Idx, q2Idx, comp != 0);
            }
        }
    }

    private void filterBlockEdgeVert(Picture pic, int comp, int x, int y, int indexAlpha, int indexBeta, int bs, int blkH) {
        int stride = pic.getPlaneWidth(comp);
        for (int i = 0; i < blkH; i++) {
            int offsetQ = ((y + i) * stride) + x;
            int p2Idx = offsetQ - 3;
            int p1Idx = offsetQ - 2;
            int p0Idx = offsetQ - 1;
            int q1Idx = offsetQ + 1;
            int q2Idx = offsetQ + 2;
            if (bs == 4) {
                int p3Idx = offsetQ - 4;
                int q3Idx = offsetQ + 3;
                filterBs4(indexAlpha, indexBeta, pic.getPlaneData(comp), p3Idx, p2Idx, p1Idx, p0Idx, offsetQ, q1Idx, q2Idx, q3Idx, comp != 0);
            } else if (bs > 0) {
                filterBs(bs, indexAlpha, indexBeta, pic.getPlaneData(comp), p2Idx, p1Idx, p0Idx, offsetQ, q1Idx, q2Idx, comp != 0);
            }
        }
    }

    private void filterBs(int bs, int indexAlpha, int indexBeta, int[] pels, int p2Idx, int p1Idx, int p0Idx, int q0Idx, int q1Idx, int q2Idx, boolean isChroma) {
        int tC;
        boolean conditionP;
        boolean conditionQ;
        int p1 = pels[p1Idx];
        int p0 = pels[p0Idx];
        int q0 = pels[q0Idx];
        int q1 = pels[q1Idx];
        int alphaThresh = alphaTab[indexAlpha];
        int betaThresh = betaTab[indexBeta];
        boolean filterEnabled = Math.abs(p0 - q0) < alphaThresh && Math.abs(p1 - p0) < betaThresh && Math.abs(q1 - q0) < betaThresh;
        if (filterEnabled) {
            int tC0 = tcs[bs - 1][indexAlpha];
            if (!isChroma) {
                int ap = Math.abs(pels[p2Idx] - p0);
                int aq = Math.abs(pels[q2Idx] - q0);
                tC = tC0 + (ap < betaThresh ? 1 : 0) + (aq < betaThresh ? 1 : 0);
                conditionP = ap < betaThresh;
                conditionQ = aq < betaThresh;
            } else {
                tC = tC0 + 1;
                conditionP = false;
                conditionQ = false;
            }
            int sigma = ((((q0 - p0) << 2) + (p1 - q1)) + 4) >> 3;
            if (sigma < (-tC)) {
                sigma = -tC;
            } else if (sigma > tC) {
                sigma = tC;
            }
            int p0n = p0 + sigma;
            if (p0n < 0) {
                p0n = 0;
            }
            int q0n = q0 - sigma;
            if (q0n < 0) {
                q0n = 0;
            }
            if (conditionP) {
                int p2 = pels[p2Idx];
                int diff = (((((p0 + q0) + 1) >> 1) + p2) - (p1 << 1)) >> 1;
                if (diff < (-tC0)) {
                    diff = -tC0;
                } else if (diff > tC0) {
                    diff = tC0;
                }
                int p1n = p1 + diff;
                pels[p1Idx] = MathUtil.clip(p1n, 0, 255);
            }
            if (conditionQ) {
                int q2 = pels[q2Idx];
                int diff2 = (((((p0 + q0) + 1) >> 1) + q2) - (q1 << 1)) >> 1;
                if (diff2 < (-tC0)) {
                    diff2 = -tC0;
                } else if (diff2 > tC0) {
                    diff2 = tC0;
                }
                int q1n = q1 + diff2;
                pels[q1Idx] = MathUtil.clip(q1n, 0, 255);
            }
            pels[q0Idx] = MathUtil.clip(q0n, 0, 255);
            pels[p0Idx] = MathUtil.clip(p0n, 0, 255);
        }
    }

    private void filterBs4(int indexAlpha, int indexBeta, int[] pels, int p3Idx, int p2Idx, int p1Idx, int p0Idx, int q0Idx, int q1Idx, int q2Idx, int q3Idx, boolean isChroma) {
        boolean conditionP;
        boolean conditionQ;
        int p0 = pels[p0Idx];
        int q0 = pels[q0Idx];
        int p1 = pels[p1Idx];
        int q1 = pels[q1Idx];
        int alphaThresh = alphaTab[indexAlpha];
        int betaThresh = betaTab[indexBeta];
        boolean filterEnabled = Math.abs(p0 - q0) < alphaThresh && Math.abs(p1 - p0) < betaThresh && Math.abs(q1 - q0) < betaThresh;
        if (filterEnabled) {
            if (isChroma) {
                conditionP = false;
                conditionQ = false;
            } else {
                int ap = Math.abs(pels[p2Idx] - p0);
                int aq = Math.abs(pels[q2Idx] - q0);
                conditionP = ap < betaThresh && Math.abs(p0 - q0) < (alphaThresh >> 2) + 2;
                conditionQ = aq < betaThresh && Math.abs(p0 - q0) < (alphaThresh >> 2) + 2;
            }
            if (conditionP) {
                int p3 = pels[p3Idx];
                int p2 = pels[p2Idx];
                int p0n = ((((((p1 * 2) + p2) + (p0 * 2)) + (q0 * 2)) + q1) + 4) >> 3;
                int p1n = ((((p2 + p1) + p0) + q0) + 2) >> 2;
                int p2n = ((((((p3 * 2) + (p2 * 3)) + p1) + p0) + q0) + 4) >> 3;
                pels[p0Idx] = MathUtil.clip(p0n, 0, 255);
                pels[p1Idx] = MathUtil.clip(p1n, 0, 255);
                pels[p2Idx] = MathUtil.clip(p2n, 0, 255);
            } else {
                int p0n2 = ((((p1 * 2) + p0) + q1) + 2) >> 2;
                pels[p0Idx] = MathUtil.clip(p0n2, 0, 255);
            }
            if (conditionQ && !isChroma) {
                int q2 = pels[q2Idx];
                int q3 = pels[q3Idx];
                int q0n = ((((((p0 * 2) + p1) + (q0 * 2)) + (q1 * 2)) + q2) + 4) >> 3;
                int q1n = ((((p0 + q0) + q1) + q2) + 2) >> 2;
                int q2n = ((((((q3 * 2) + (q2 * 3)) + q1) + q0) + p0) + 4) >> 3;
                pels[q0Idx] = MathUtil.clip(q0n, 0, 255);
                pels[q1Idx] = MathUtil.clip(q1n, 0, 255);
                pels[q2Idx] = MathUtil.clip(q2n, 0, 255);
                return;
            }
            int q0n2 = ((((q1 * 2) + q0) + p1) + 2) >> 2;
            pels[q0Idx] = MathUtil.clip(q0n2, 0, 255);
        }
    }
}
