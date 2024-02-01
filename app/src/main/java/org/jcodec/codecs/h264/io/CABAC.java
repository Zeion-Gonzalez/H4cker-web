package org.jcodec.codecs.h264.io;

import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.PointerIconCompat;
import java.lang.reflect.Array;
import org.jcodec.codecs.common.biari.MDecoder;
import org.jcodec.codecs.common.biari.MEncoder;
import org.jcodec.codecs.h264.H264Const;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.decode.CABACContst;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.SliceType;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.common.tools.MathUtil;
import org.jcodec.containers.mps.MPSUtils;

/* loaded from: classes.dex */
public class CABAC {
    private int[] chromaPredModeTop;
    private int[][] codedBlkDCTop;
    private int[][] codedBlkTop;
    private int[][][] mvdTop;
    private int prevCBP;
    private int prevMbQpDelta;
    private int[][] refIdxTop;
    private boolean skipFlagLeft;
    private boolean[] skipFlagsTop;
    public int[] tmp = new int[16];
    private int chromaPredModeLeft = 0;
    private int[][] codedBlkLeft = {new int[4], new int[2], new int[2]};
    private int[] codedBlkDCLeft = new int[3];
    private int[][] refIdxLeft = (int[][]) Array.newInstance(Integer.TYPE, 2, 4);
    private int[][][] mvdLeft = (int[][][]) Array.newInstance(Integer.TYPE, 2, 2, 4);

    /* loaded from: classes.dex */
    public enum BlockType {
        LUMA_16_DC(85, 105, 166, 277, 338, JpegConst.APP3, 0),
        LUMA_15_AC(89, 120, MPEGConst.EXTENSION_START_CODE, 292, 353, JpegConst.APPD, 0),
        LUMA_16(93, 134, JpegConst.SOF3, 306, 367, 247, 0),
        CHROMA_DC(97, 149, JpegConst.RST2, 321, 382, InputDeviceCompat.SOURCE_KEYBOARD, 1),
        CHROMA_AC(101, 152, JpegConst.RST5, 324, 385, 266, 0),
        LUMA_64(PointerIconCompat.TYPE_NO_DROP, 402, 417, 436, 451, 426, 0),
        CB_16_DC(460, 484, 572, 776, 864, 952, 0),
        CB_15x16_AC(464, 499, 587, 791, 879, 962, 0),
        CB_16(468, InputDeviceCompat.SOURCE_DPAD, 601, 805, 893, 972, 0),
        CB_64(PointerIconCompat.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW, 660, 690, 675, 699, 708, 0),
        CR_16_DC(472, 528, 616, 820, 908, 982, 0),
        CR_15x16_AC(476, 543, 631, 835, 923, 992, 0),
        CR_16(MPSUtils.VIDEO_MIN, 557, 645, 849, 937, PointerIconCompat.TYPE_HAND, 0),
        CR_64(PointerIconCompat.TYPE_GRAB, 718, 748, 733, 757, 766, 0);

        public int codedBlockCtxOff;
        public int coeffAbsLevelAdjust;
        public int coeffAbsLevelCtxOff;
        public int lastSigCoeffCtxOff;
        public int lastSigCoeffFldCtxOff;
        public int sigCoeffFlagCtxOff;
        public int sigCoeffFlagFldCtxOff;

        BlockType(int codecBlockCtxOff, int sigCoeffCtxOff, int lastSigCoeffCtxOff, int sigCoeffFlagFldCtxOff, int lastSigCoeffFldCtxOff, int coeffAbsLevelCtxOff, int coeffAbsLevelAdjust) {
            this.codedBlockCtxOff = codecBlockCtxOff;
            this.sigCoeffFlagCtxOff = sigCoeffCtxOff;
            this.lastSigCoeffCtxOff = lastSigCoeffCtxOff;
            this.sigCoeffFlagFldCtxOff = sigCoeffFlagFldCtxOff;
            this.lastSigCoeffFldCtxOff = sigCoeffFlagFldCtxOff;
            this.coeffAbsLevelCtxOff = coeffAbsLevelCtxOff;
            this.coeffAbsLevelAdjust = coeffAbsLevelAdjust;
        }
    }

    public CABAC(int mbWidth) {
        this.chromaPredModeTop = new int[mbWidth];
        this.codedBlkTop = new int[][]{new int[mbWidth << 2], new int[mbWidth << 1], new int[mbWidth << 1]};
        this.codedBlkDCTop = (int[][]) Array.newInstance(Integer.TYPE, 3, mbWidth);
        this.refIdxTop = (int[][]) Array.newInstance(Integer.TYPE, 2, mbWidth << 2);
        this.skipFlagsTop = new boolean[mbWidth];
        this.mvdTop = (int[][][]) Array.newInstance(Integer.TYPE, 2, 2, mbWidth << 2);
    }

    public int readCoeffs(MDecoder decoder, BlockType blockType, int[] out, int first, int num, int[] reorder, int[] scMapping, int[] lscMapping) {
        int numCoeff;
        boolean[] sigCoeff = new boolean[num];
        int numCoeff2 = 0;
        while (true) {
            numCoeff = numCoeff2;
            if (numCoeff >= num - 1) {
                break;
            }
            sigCoeff[numCoeff] = decoder.decodeBin(blockType.sigCoeffFlagCtxOff + scMapping[numCoeff]) == 1;
            if (sigCoeff[numCoeff] && decoder.decodeBin(blockType.lastSigCoeffCtxOff + lscMapping[numCoeff]) == 1) {
                break;
            }
            numCoeff2 = numCoeff + 1;
        }
        sigCoeff[numCoeff] = true;
        int numGt1 = 0;
        int numEq1 = 0;
        for (int j = (numCoeff + 1) - 1; j >= 0; j--) {
            if (sigCoeff[j]) {
                int absLev = readCoeffAbsLevel(decoder, blockType, numGt1, numEq1);
                if (absLev == 0) {
                    numEq1++;
                } else {
                    numGt1++;
                }
                out[reorder[j + first]] = MathUtil.toSigned(absLev + 1, -decoder.decodeBinBypass());
            }
        }
        return numGt1 + numEq1;
    }

    private int readCoeffAbsLevel(MDecoder decoder, BlockType blockType, int numDecodAbsLevelGt1, int numDecodAbsLevelEq1) {
        int b;
        int incB0 = numDecodAbsLevelGt1 != 0 ? 0 : Math.min(4, numDecodAbsLevelEq1 + 1);
        int incBN = Math.min(4 - blockType.coeffAbsLevelAdjust, numDecodAbsLevelGt1) + 5;
        int b2 = decoder.decodeBin(blockType.coeffAbsLevelCtxOff + incB0);
        int val = 0;
        while (b2 != 0 && val < 13) {
            b2 = decoder.decodeBin(blockType.coeffAbsLevelCtxOff + incBN);
            val++;
        }
        int val2 = val + b2;
        if (val2 == 14) {
            int log = -2;
            int add = 0;
            int sum = 0;
            do {
                log++;
                b = decoder.decodeBinBypass();
            } while (b != 0);
            while (log >= 0) {
                add |= decoder.decodeBinBypass() << log;
                sum += 1 << log;
                log--;
            }
            return val2 + add + sum;
        }
        return val2;
    }

    public void writeCoeffs(MEncoder encoder, BlockType blockType, int[] _out, int first, int num, int[] reorder) {
        for (int i = 0; i < num; i++) {
            this.tmp[i] = _out[reorder[first + i]];
        }
        int numCoeff = 0;
        for (int i2 = 0; i2 < num; i2++) {
            if (this.tmp[i2] != 0) {
                numCoeff = i2 + 1;
            }
        }
        int i3 = 0;
        while (i3 < Math.min(numCoeff, num - 1)) {
            if (this.tmp[i3] != 0) {
                encoder.encodeBin(blockType.sigCoeffFlagCtxOff + i3, 1);
                encoder.encodeBin(blockType.lastSigCoeffCtxOff + i3, i3 == numCoeff + (-1) ? 1 : 0);
            } else {
                encoder.encodeBin(blockType.sigCoeffFlagCtxOff + i3, 0);
            }
            i3++;
        }
        int numGt1 = 0;
        int numEq1 = 0;
        for (int j = numCoeff - 1; j >= 0; j--) {
            if (this.tmp[j] != 0) {
                int absLev = MathUtil.abs(this.tmp[j]) - 1;
                writeCoeffAbsLevel(encoder, blockType, numGt1, numEq1, absLev);
                if (absLev == 0) {
                    numEq1++;
                } else {
                    numGt1++;
                }
                encoder.encodeBinBypass(MathUtil.sign(this.tmp[j]));
            }
        }
    }

    private void writeCoeffAbsLevel(MEncoder encoder, BlockType blockType, int numDecodAbsLevelGt1, int numDecodAbsLevelEq1, int absLev) {
        int incB0 = numDecodAbsLevelGt1 != 0 ? 0 : Math.min(4, numDecodAbsLevelEq1 + 1);
        int incBN = Math.min(4 - blockType.coeffAbsLevelAdjust, numDecodAbsLevelGt1) + 5;
        if (absLev == 0) {
            encoder.encodeBin(blockType.coeffAbsLevelCtxOff + incB0, 0);
            return;
        }
        encoder.encodeBin(blockType.coeffAbsLevelCtxOff + incB0, 1);
        if (absLev < 14) {
            for (int i = 1; i < absLev; i++) {
                encoder.encodeBin(blockType.coeffAbsLevelCtxOff + incBN, 1);
            }
            encoder.encodeBin(blockType.coeffAbsLevelCtxOff + incBN, 0);
            return;
        }
        for (int i2 = 1; i2 < 14; i2++) {
            encoder.encodeBin(blockType.coeffAbsLevelCtxOff + incBN, 1);
        }
        int absLev2 = absLev - 14;
        int sufLen = 0;
        int pow = 1;
        while (absLev2 >= pow) {
            encoder.encodeBinBypass(1);
            absLev2 -= pow;
            sufLen++;
            pow = 1 << sufLen;
        }
        encoder.encodeBinBypass(0);
        for (int sufLen2 = sufLen - 1; sufLen2 >= 0; sufLen2--) {
            encoder.encodeBinBypass((absLev2 >> sufLen2) & 1);
        }
    }

    public void initModels(int[][] cm, SliceType sliceType, int cabacIdc, int sliceQp) {
        int[] tabA = sliceType.isIntra() ? CABACContst.cabac_context_init_I_A : CABACContst.cabac_context_init_PB_A[cabacIdc];
        int[] tabB = sliceType.isIntra() ? CABACContst.cabac_context_init_I_B : CABACContst.cabac_context_init_PB_B[cabacIdc];
        for (int i = 0; i < 1024; i++) {
            int preCtxState = MathUtil.clip(((tabA[i] * MathUtil.clip(sliceQp, 0, 51)) >> 4) + tabB[i], 1, 126);
            if (preCtxState <= 63) {
                cm[0][i] = 63 - preCtxState;
                cm[1][i] = 0;
            } else {
                cm[0][i] = preCtxState - 64;
                cm[1][i] = 1;
            }
        }
    }

    public int readMBTypeI(MDecoder decoder, MBType left, MBType top, boolean leftAvailable, boolean topAvailable) {
        int ctx = 3 + ((!leftAvailable || left == MBType.I_NxN) ? 0 : 1);
        if (decoder.decodeBin(ctx + ((!topAvailable || top == MBType.I_NxN) ? 0 : 1)) == 0) {
            return 0;
        }
        return decoder.decodeFinalBin() == 1 ? 25 : readMBType16x16(decoder) + 1;
    }

    private int readMBType16x16(MDecoder decoder) {
        int type = decoder.decodeBin(6) * 12;
        return decoder.decodeBin(7) == 0 ? (decoder.decodeBin(9) << 1) + type + decoder.decodeBin(10) : (decoder.decodeBin(8) << 2) + type + (decoder.decodeBin(9) << 1) + decoder.decodeBin(10) + 4;
    }

    public int readMBTypeP(MDecoder decoder) {
        if (decoder.decodeBin(14) == 1) {
            return readIntraP(decoder, 17) + 5;
        }
        return decoder.decodeBin(15) == 0 ? decoder.decodeBin(16) == 0 ? 0 : 3 : decoder.decodeBin(17) == 0 ? 2 : 1;
    }

    private int readIntraP(MDecoder decoder, int ctxOff) {
        if (decoder.decodeBin(ctxOff) == 0) {
            return 0;
        }
        if (decoder.decodeFinalBin() == 1) {
            return 25;
        }
        return readMBType16x16P(decoder, ctxOff) + 1;
    }

    private int readMBType16x16P(MDecoder decoder, int ctxOff) {
        int ctxOff2 = ctxOff + 1;
        int type = decoder.decodeBin(ctxOff2) * 12;
        int ctxOff3 = ctxOff2 + 1;
        if (decoder.decodeBin(ctxOff3) != 0) {
            return (decoder.decodeBin(ctxOff3) << 2) + type + (decoder.decodeBin(ctxOff3 + 1) << 1) + decoder.decodeBin(ctxOff3 + 1) + 4;
        }
        int ctxOff4 = ctxOff3 + 1;
        return (decoder.decodeBin(ctxOff4) << 1) + type + decoder.decodeBin(ctxOff4);
    }

    public int readMBTypeB(MDecoder mDecoder, MBType left, MBType top, boolean leftAvailable, boolean topAvailable) {
        int ctx = 27 + ((!leftAvailable || left == null || left == MBType.B_Direct_16x16) ? 0 : 1);
        if (mDecoder.decodeBin(ctx + ((!topAvailable || top == null || top == MBType.B_Direct_16x16) ? 0 : 1)) == 0) {
            return 0;
        }
        if (mDecoder.decodeBin(30) == 0) {
            return mDecoder.decodeBin(32) + 1;
        }
        int b1 = mDecoder.decodeBin(31);
        if (b1 == 0) {
            return ((mDecoder.decodeBin(32) << 2) | (mDecoder.decodeBin(32) << 1) | mDecoder.decodeBin(32)) + 3;
        }
        if (mDecoder.decodeBin(32) == 0) {
            return ((mDecoder.decodeBin(32) << 2) | (mDecoder.decodeBin(32) << 1) | mDecoder.decodeBin(32)) + 12;
        }
        switch ((mDecoder.decodeBin(32) << 1) + mDecoder.decodeBin(32)) {
            case 0:
                return mDecoder.decodeBin(32) + 20;
            case 1:
                return readIntraP(mDecoder, 32) + 23;
            case 2:
                return 11;
            case 3:
                return 22;
            default:
                return 0;
        }
    }

    public void writeMBTypeI(MEncoder encoder, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int mbType) {
        int ctx = 3 + ((!leftAvailable || left == MBType.I_NxN) ? 0 : 1) + ((!topAvailable || top == MBType.I_NxN) ? 0 : 1);
        if (mbType == 0) {
            encoder.encodeBin(ctx, 0);
            return;
        }
        encoder.encodeBin(ctx, 1);
        if (mbType == 25) {
            encoder.encodeBinFinal(1);
        } else {
            encoder.encodeBinFinal(0);
            writeMBType16x16(encoder, mbType - 1);
        }
    }

    private void writeMBType16x16(MEncoder encoder, int mbType) {
        if (mbType < 12) {
            encoder.encodeBin(6, 0);
        } else {
            encoder.encodeBin(6, 1);
            mbType -= 12;
        }
        if (mbType < 4) {
            encoder.encodeBin(7, 0);
            encoder.encodeBin(9, mbType >> 1);
            encoder.encodeBin(10, mbType & 1);
        } else {
            int mbType2 = mbType - 4;
            encoder.encodeBin(7, 1);
            encoder.encodeBin(8, mbType2 >> 2);
            encoder.encodeBin(9, (mbType2 >> 1) & 1);
            encoder.encodeBin(10, mbType2 & 1);
        }
    }

    public int readMBQpDelta(MDecoder decoder, MBType prevMbType) {
        int ctx = 60 + ((prevMbType == null || prevMbType == MBType.I_PCM || (prevMbType != MBType.I_16x16 && this.prevCBP == 0) || this.prevMbQpDelta == 0) ? 0 : 1);
        int val = 0;
        if (decoder.decodeBin(ctx) == 1) {
            val = 0 + 1;
            if (decoder.decodeBin(62) == 1) {
                do {
                    val++;
                } while (decoder.decodeBin(63) == 1);
            }
        }
        this.prevMbQpDelta = H264Utils.golomb2Signed(val);
        return this.prevMbQpDelta;
    }

    public void writeMBQpDelta(MEncoder encoder, MBType prevMbType, int mbQpDelta) {
        int ctx = 60 + ((prevMbType == null || prevMbType == MBType.I_PCM || (prevMbType != MBType.I_16x16 && this.prevCBP == 0) || this.prevMbQpDelta == 0) ? 0 : 1);
        this.prevMbQpDelta = mbQpDelta;
        int mbQpDelta2 = mbQpDelta - 1;
        if (mbQpDelta == 0) {
            encoder.encodeBin(ctx, 0);
            return;
        }
        encoder.encodeBin(ctx, 1);
        int mbQpDelta3 = mbQpDelta2 - 1;
        if (mbQpDelta2 == 0) {
            encoder.encodeBin(62, 0);
            return;
        }
        while (true) {
            int mbQpDelta4 = mbQpDelta3;
            mbQpDelta3 = mbQpDelta4 - 1;
            if (mbQpDelta4 > 0) {
                encoder.encodeBin(63, 1);
            } else {
                encoder.encodeBin(63, 0);
                return;
            }
        }
    }

    public int readIntraChromaPredMode(MDecoder decoder, int mbX, MBType left, MBType top, boolean leftAvailable, boolean topAvailable) {
        int mode;
        int ctx = 64 + ((!leftAvailable || left == null || !left.isIntra() || this.chromaPredModeLeft == 0) ? 0 : 1);
        if (decoder.decodeBin(ctx + ((!topAvailable || top == null || !top.isIntra() || this.chromaPredModeTop[mbX] == 0) ? 0 : 1)) == 0) {
            mode = 0;
        } else if (decoder.decodeBin(67) == 0) {
            mode = 1;
        } else if (decoder.decodeBin(67) == 0) {
            mode = 2;
        } else {
            mode = 3;
        }
        this.chromaPredModeTop[mbX] = mode;
        this.chromaPredModeLeft = mode;
        return mode;
    }

    public void writeIntraChromaPredMode(MEncoder encoder, int mbX, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int mode) {
        int ctx = 64 + ((leftAvailable && left.isIntra() && this.chromaPredModeLeft != 0) ? 1 : 0);
        int mode2 = mode - 1;
        encoder.encodeBin(ctx + ((topAvailable && top.isIntra() && this.chromaPredModeTop[mbX] != 0) ? 1 : 0), mode == 0 ? 0 : 1);
        int i = 0;
        while (mode2 >= 0 && i < 2) {
            int mode3 = mode2 - 1;
            encoder.encodeBin(67, mode2 == 0 ? 0 : 1);
            i++;
            mode2 = mode3;
        }
        this.chromaPredModeTop[mbX] = mode2;
        this.chromaPredModeLeft = mode2;
    }

    public int condTerm(MBType mbCur, boolean nAvb, MBType mbN, boolean nBlkAvb, int cbpN) {
        if (!nAvb) {
            return mbCur.isIntra() ? 1 : 0;
        }
        if (mbN == MBType.I_PCM) {
            return 1;
        }
        if (nBlkAvb) {
            return cbpN;
        }
        return 0;
    }

    public int readCodedBlockFlagLumaDC(MDecoder decoder, int mbX, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, MBType cur) {
        int tLeft = condTerm(cur, leftAvailable, left, left == MBType.I_16x16, this.codedBlkDCLeft[0]);
        int tTop = condTerm(cur, topAvailable, top, top == MBType.I_16x16, this.codedBlkDCTop[0][mbX]);
        int decoded = decoder.decodeBin(BlockType.LUMA_16_DC.codedBlockCtxOff + tLeft + (tTop * 2));
        this.codedBlkDCLeft[0] = decoded;
        this.codedBlkDCTop[0][mbX] = decoded;
        return decoded;
    }

    public int readCodedBlockFlagChromaDC(MDecoder decoder, int mbX, int comp, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int leftCBPChroma, int topCBPChroma, MBType cur) {
        int tLeft = condTerm(cur, leftAvailable, left, (left == null || leftCBPChroma == 0) ? false : true, this.codedBlkDCLeft[comp]);
        int tTop = condTerm(cur, topAvailable, top, (top == null || topCBPChroma == 0) ? false : true, this.codedBlkDCTop[comp][mbX]);
        int decoded = decoder.decodeBin(BlockType.CHROMA_DC.codedBlockCtxOff + tLeft + (tTop * 2));
        this.codedBlkDCLeft[comp] = decoded;
        this.codedBlkDCTop[comp][mbX] = decoded;
        return decoded;
    }

    public int readCodedBlockFlagLumaAC(MDecoder decoder, BlockType blkType, int blkX, int blkY, int comp, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int leftCBPLuma, int topCBPLuma, int curCBPLuma, MBType cur) {
        int tLeft;
        int tTop;
        int blkOffLeft = blkX & 3;
        int blkOffTop = blkY & 3;
        if (blkOffLeft == 0) {
            tLeft = condTerm(cur, leftAvailable, left, (left == null || left == MBType.I_PCM || !cbp(leftCBPLuma, 3, blkOffTop)) ? false : true, this.codedBlkLeft[comp][blkOffTop]);
        } else {
            tLeft = condTerm(cur, true, cur, cbp(curCBPLuma, blkOffLeft - 1, blkOffTop), this.codedBlkLeft[comp][blkOffTop]);
        }
        if (blkOffTop == 0) {
            tTop = condTerm(cur, topAvailable, top, (top == null || top == MBType.I_PCM || !cbp(topCBPLuma, blkOffLeft, 3)) ? false : true, this.codedBlkTop[comp][blkX]);
        } else {
            tTop = condTerm(cur, true, cur, cbp(curCBPLuma, blkOffLeft, blkOffTop - 1), this.codedBlkTop[comp][blkX]);
        }
        int decoded = decoder.decodeBin(blkType.codedBlockCtxOff + tLeft + (tTop * 2));
        this.codedBlkLeft[comp][blkOffTop] = decoded;
        this.codedBlkTop[comp][blkX] = decoded;
        return decoded;
    }

    public int readCodedBlockFlagLuma64(MDecoder decoder, int blkX, int blkY, int comp, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int leftCBPLuma, int topCBPLuma, int curCBPLuma, MBType cur, boolean is8x8Left, boolean is8x8Top) {
        int tLeft;
        int tTop;
        int blkOffLeft = blkX & 3;
        int blkOffTop = blkY & 3;
        if (blkOffLeft == 0) {
            tLeft = condTerm(cur, leftAvailable, left, left != null && left != MBType.I_PCM && is8x8Left && cbp(leftCBPLuma, 3, blkOffTop), this.codedBlkLeft[comp][blkOffTop]);
        } else {
            tLeft = condTerm(cur, true, cur, cbp(curCBPLuma, blkOffLeft - 1, blkOffTop), this.codedBlkLeft[comp][blkOffTop]);
        }
        if (blkOffTop == 0) {
            tTop = condTerm(cur, topAvailable, top, top != null && top != MBType.I_PCM && is8x8Top && cbp(topCBPLuma, blkOffLeft, 3), this.codedBlkTop[comp][blkX]);
        } else {
            tTop = condTerm(cur, true, cur, cbp(curCBPLuma, blkOffLeft, blkOffTop - 1), this.codedBlkTop[comp][blkX]);
        }
        int decoded = decoder.decodeBin(BlockType.LUMA_64.codedBlockCtxOff + tLeft + (tTop * 2));
        this.codedBlkLeft[comp][blkOffTop] = decoded;
        this.codedBlkTop[comp][blkX] = decoded;
        return decoded;
    }

    private boolean cbp(int cbpLuma, int blkX, int blkY) {
        int x8x8 = (blkY & 2) + (blkX >> 1);
        return ((cbpLuma >> x8x8) & 1) == 1;
    }

    public int readCodedBlockFlagChromaAC(MDecoder decoder, int blkX, int blkY, int comp, MBType left, MBType top, boolean leftAvailable, boolean topAvailable, int leftCBPChroma, int topCBPChroma, MBType cur) {
        int tLeft;
        int tTop;
        int blkOffLeft = blkX & 1;
        int blkOffTop = blkY & 1;
        if (blkOffLeft == 0) {
            tLeft = condTerm(cur, leftAvailable, left, (left == null || left == MBType.I_PCM || (leftCBPChroma & 2) == 0) ? false : true, this.codedBlkLeft[comp][blkOffTop]);
        } else {
            tLeft = condTerm(cur, true, cur, true, this.codedBlkLeft[comp][blkOffTop]);
        }
        if (blkOffTop == 0) {
            tTop = condTerm(cur, topAvailable, top, (top == null || top == MBType.I_PCM || (topCBPChroma & 2) == 0) ? false : true, this.codedBlkTop[comp][blkX]);
        } else {
            tTop = condTerm(cur, true, cur, true, this.codedBlkTop[comp][blkX]);
        }
        int decoded = decoder.decodeBin(BlockType.CHROMA_AC.codedBlockCtxOff + tLeft + (tTop * 2));
        this.codedBlkLeft[comp][blkOffTop] = decoded;
        this.codedBlkTop[comp][blkX] = decoded;
        return decoded;
    }

    public boolean prev4x4PredModeFlag(MDecoder decoder) {
        return decoder.decodeBin(68) == 1;
    }

    public int rem4x4PredMode(MDecoder decoder) {
        return decoder.decodeBin(69) | (decoder.decodeBin(69) << 1) | (decoder.decodeBin(69) << 2);
    }

    public int codedBlockPatternIntra(MDecoder mDecoder, boolean leftAvailable, boolean topAvailable, int cbpLeft, int cbpTop, MBType mbLeft, MBType mbTop) {
        int cbp0 = mDecoder.decodeBin(condTerm(leftAvailable, mbLeft, (cbpLeft >> 1) & 1) + 73 + (condTerm(topAvailable, mbTop, (cbpTop >> 2) & 1) * 2));
        int cbp1 = mDecoder.decodeBin((1 - cbp0) + 73 + (condTerm(topAvailable, mbTop, (cbpTop >> 3) & 1) * 2));
        int cbp2 = mDecoder.decodeBin(condTerm(leftAvailable, mbLeft, (cbpLeft >> 3) & 1) + 73 + ((1 - cbp0) * 2));
        int cbp3 = mDecoder.decodeBin((1 - cbp2) + 73 + ((1 - cbp1) * 2));
        int cr0 = mDecoder.decodeBin(condTermCr0(leftAvailable, mbLeft, cbpLeft >> 4) + 77 + (condTermCr0(topAvailable, mbTop, cbpTop >> 4) * 2));
        int cr1 = cr0 != 0 ? mDecoder.decodeBin(condTermCr1(leftAvailable, mbLeft, cbpLeft >> 4) + 81 + (condTermCr1(topAvailable, mbTop, cbpTop >> 4) * 2)) : 0;
        return (cbp1 << 1) | cbp0 | (cbp2 << 2) | (cbp3 << 3) | (cr0 << 4) | (cr1 << 5);
    }

    private int condTermCr0(boolean avb, MBType mbt, int cbpChroma) {
        return (!avb || (mbt != MBType.I_PCM && (mbt == null || cbpChroma == 0))) ? 0 : 1;
    }

    private int condTermCr1(boolean avb, MBType mbt, int cbpChroma) {
        return (!avb || (mbt != MBType.I_PCM && (mbt == null || (cbpChroma & 2) == 0))) ? 0 : 1;
    }

    private int condTerm(boolean avb, MBType mbt, int cbp) {
        return (!avb || mbt == MBType.I_PCM || (mbt != null && cbp == 1)) ? 0 : 1;
    }

    public void setPrevCBP(int prevCBP) {
        this.prevCBP = prevCBP;
    }

    public int readMVD(MDecoder decoder, int comp, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred curPred, int mbX, int partX, int partY, int partW, int partH, int list) {
        int i;
        int b;
        int ctx = comp == 0 ? 40 : 47;
        int partAbsX = (mbX << 2) + partX;
        boolean predEqA = (leftPred == null || leftPred == H264Const.PartPred.Direct || (leftPred != H264Const.PartPred.Bi && leftPred != curPred && (curPred != H264Const.PartPred.Bi || !leftPred.usesList(list)))) ? false : true;
        boolean predEqB = (topPred == null || topPred == H264Const.PartPred.Direct || (topPred != H264Const.PartPred.Bi && topPred != curPred && (curPred != H264Const.PartPred.Bi || !topPred.usesList(list)))) ? false : true;
        int absMvdComp = ((!leftAvailable || leftType == null || leftType.isIntra() || !predEqA) ? 0 : Math.abs(this.mvdLeft[list][comp][partY])) + ((!topAvailable || topType == null || topType.isIntra() || !predEqB) ? 0 : Math.abs(this.mvdTop[list][comp][partAbsX]));
        if (absMvdComp < 3) {
            i = 0;
        } else {
            i = absMvdComp > 32 ? 2 : 1;
        }
        int b2 = decoder.decodeBin(i + ctx);
        int val = 0;
        while (b2 != 0 && val < 8) {
            b2 = decoder.decodeBin(Math.min(ctx + val + 3, ctx + 6));
            val++;
        }
        int val2 = val + b2;
        if (val2 != 0) {
            if (val2 == 9) {
                int log = 2;
                int add = 0;
                int sum = 0;
                int leftover = 0;
                do {
                    sum += leftover;
                    log++;
                    b = decoder.decodeBinBypass();
                    leftover = 1 << log;
                } while (b != 0);
                for (int log2 = log - 1; log2 >= 0; log2--) {
                    add |= decoder.decodeBinBypass() << log2;
                }
                val2 += add + sum;
            }
            val2 = MathUtil.toSigned(val2, -decoder.decodeBinBypass());
        }
        for (int i2 = 0; i2 < partW; i2++) {
            this.mvdTop[list][comp][partAbsX + i2] = val2;
        }
        for (int i3 = 0; i3 < partH; i3++) {
            this.mvdLeft[list][comp][partY + i3] = val2;
        }
        return val2;
    }

    public int readRefIdx(MDecoder mDecoder, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, H264Const.PartPred leftPred, H264Const.PartPred topPred, H264Const.PartPred curPred, int mbX, int partX, int partY, int partW, int partH, int list) {
        int val;
        int partAbsX = (mbX << 2) + partX;
        boolean predEqA = (leftPred == null || leftPred == H264Const.PartPred.Direct || (leftPred != H264Const.PartPred.Bi && leftPred != curPred && (curPred != H264Const.PartPred.Bi || !leftPred.usesList(list)))) ? false : true;
        boolean predEqB = (topPred == null || topPred == H264Const.PartPred.Direct || (topPred != H264Const.PartPred.Bi && topPred != curPred && (curPred != H264Const.PartPred.Bi || !topPred.usesList(list)))) ? false : true;
        int ctA = (!leftAvailable || leftType == null || leftType.isIntra() || !predEqA || this.refIdxLeft[list][partY] == 0) ? 0 : 1;
        int ctB = (!topAvailable || topType == null || topType.isIntra() || !predEqB || this.refIdxTop[list][partAbsX] == 0) ? 0 : 1;
        int b0 = mDecoder.decodeBin(ctA + 54 + (ctB * 2));
        if (b0 == 0) {
            val = 0;
        } else {
            int b1 = mDecoder.decodeBin(58);
            if (b1 == 0) {
                val = 1;
            } else {
                val = 2;
                while (mDecoder.decodeBin(59) == 1) {
                    val++;
                }
            }
        }
        for (int i = 0; i < partW; i++) {
            this.refIdxTop[list][partAbsX + i] = val;
        }
        for (int i2 = 0; i2 < partH; i2++) {
            this.refIdxLeft[list][partY + i2] = val;
        }
        return val;
    }

    public boolean readMBSkipFlag(MDecoder mDecoder, SliceType slType, boolean leftAvailable, boolean topAvailable, int mbX) {
        int base = slType == SliceType.P ? 11 : 24;
        boolean ret = mDecoder.decodeBin(((!topAvailable || this.skipFlagsTop[mbX]) ? 0 : 1) + (base + ((!leftAvailable || this.skipFlagLeft) ? 0 : 1))) == 1;
        this.skipFlagsTop[mbX] = ret;
        this.skipFlagLeft = ret;
        return ret;
    }

    public int readSubMbTypeP(MDecoder mDecoder) {
        if (mDecoder.decodeBin(21) == 1) {
            return 0;
        }
        if (mDecoder.decodeBin(22) == 0) {
            return 1;
        }
        if (mDecoder.decodeBin(23) == 1) {
            return 2;
        }
        return 3;
    }

    public int readSubMbTypeB(MDecoder mDecoder) {
        if (mDecoder.decodeBin(36) == 0) {
            return 0;
        }
        if (mDecoder.decodeBin(37) == 0) {
            return mDecoder.decodeBin(39) + 1;
        }
        if (mDecoder.decodeBin(38) == 0) {
            return (mDecoder.decodeBin(39) << 1) + 3 + mDecoder.decodeBin(39);
        }
        if (mDecoder.decodeBin(39) == 0) {
            return (mDecoder.decodeBin(39) << 1) + 7 + mDecoder.decodeBin(39);
        }
        return mDecoder.decodeBin(39) + 11;
    }

    public boolean readTransform8x8Flag(MDecoder mDecoder, boolean leftAvailable, boolean topAvailable, MBType leftType, MBType topType, boolean is8x8Left, boolean is8x8Top) {
        int ctx = ((leftAvailable && leftType != null && is8x8Left) ? 1 : 0) + 399 + ((topAvailable && topType != null && is8x8Top) ? 1 : 0);
        return mDecoder.decodeBin(ctx) == 1;
    }

    public void setCodedBlock(int blkX, int blkY) {
        this.codedBlkTop[0][blkX] = 1;
        this.codedBlkLeft[0][blkY & 3] = 1;
    }
}
