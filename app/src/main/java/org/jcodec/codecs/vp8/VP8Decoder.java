package org.jcodec.codecs.vp8;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import org.jcodec.codecs.vp8.Macroblock;
import org.jcodec.codecs.vp8.VP8Util;
import org.jcodec.common.Assert;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class VP8Decoder {
    private int height;
    private Macroblock[][] mbs;
    private int width;

    public void decode(ByteBuffer frame) throws IOException {
        int filterType;
        int fixedMode;
        byte[] firstThree = new byte[3];
        frame.get(firstThree);
        boolean keyFrame = VP8Util.getBitInBytes(firstThree, 0) == 0;
        VP8Util.getBitsInBytes(firstThree, 1, 3);
        if (VP8Util.getBitInBytes(firstThree, 4) > 0) {
        }
        int partitionSize = VP8Util.getBitsInBytes(firstThree, 5, 19);
        String str = printHexByte(frame.get()) + " " + printHexByte(frame.get()) + " " + printHexByte(frame.get());
        int twoBytesWidth = (frame.get() & 255) | ((frame.get() & 255) << 8);
        int twoBytesHeight = (frame.get() & 255) | ((frame.get() & 255) << 8);
        this.width = twoBytesWidth & 16383;
        this.height = twoBytesHeight & 16383;
        int numberOfMBRows = VP8Util.getMacroblockCount(this.height);
        int numberOfMBCols = VP8Util.getMacroblockCount(this.width);
        this.mbs = (Macroblock[][]) Array.newInstance(Macroblock.class, numberOfMBRows + 2, numberOfMBCols + 2);
        for (int row = 0; row < numberOfMBRows + 2; row++) {
            for (int col = 0; col < numberOfMBCols + 2; col++) {
                this.mbs[row][col] = new Macroblock(row, col);
            }
        }
        int headerOffset = frame.position();
        BooleanArithmeticDecoder headerDecoder = new BooleanArithmeticDecoder(frame, 0);
        if (headerDecoder.decodeBit() == 0) {
        }
        if (headerDecoder.decodeBit() == 0) {
        }
        int segmentation = headerDecoder.decodeBit();
        Assert.assertEquals("Frame has segmentation, segment decoding is not ", 0, segmentation);
        int simpleFilter = headerDecoder.decodeBit();
        int filterLevel = headerDecoder.decodeInt(6);
        if (filterLevel == 0) {
            filterType = 0;
        } else {
            filterType = simpleFilter > 0 ? 1 : 2;
        }
        int sharpnessLevel = headerDecoder.decodeInt(3);
        int loopFilterDeltaFlag = headerDecoder.decodeBit();
        Assert.assertEquals(1, loopFilterDeltaFlag);
        int loopFilterDeltaUpdate = headerDecoder.decodeBit();
        Assert.assertEquals(1, loopFilterDeltaUpdate);
        int[] refLoopFilterDeltas = new int[VP8Util.MAX_REF_LF_DELTAS];
        int[] modeLoopFilterDeltas = new int[VP8Util.MAX_MODE_LF_DELTAS];
        for (int i = 0; i < VP8Util.MAX_REF_LF_DELTAS; i++) {
            if (headerDecoder.decodeBit() > 0) {
                refLoopFilterDeltas[i] = headerDecoder.decodeInt(6);
                if (headerDecoder.decodeBit() > 0) {
                    refLoopFilterDeltas[i] = refLoopFilterDeltas[i] * (-1);
                }
            }
        }
        for (int i2 = 0; i2 < VP8Util.MAX_MODE_LF_DELTAS; i2++) {
            if (headerDecoder.decodeBit() > 0) {
                modeLoopFilterDeltas[i2] = headerDecoder.decodeInt(6);
                if (headerDecoder.decodeBit() > 0) {
                    modeLoopFilterDeltas[i2] = modeLoopFilterDeltas[i2] * (-1);
                }
            }
        }
        int log2OfPartCnt = headerDecoder.decodeInt(2);
        Assert.assertEquals(0, log2OfPartCnt);
        long limit = frame.limit() - (partitionSize + headerOffset);
        ByteBuffer tokenBuffer = frame.duplicate();
        tokenBuffer.position(partitionSize + headerOffset);
        BooleanArithmeticDecoder decoder = new BooleanArithmeticDecoder(tokenBuffer, 0);
        int yacIndex = headerDecoder.decodeInt(7);
        int ydcDelta = headerDecoder.decodeBit() > 0 ? VP8Util.delta(headerDecoder) : 0;
        int y2dcDelta = headerDecoder.decodeBit() > 0 ? VP8Util.delta(headerDecoder) : 0;
        int y2acDelta = headerDecoder.decodeBit() > 0 ? VP8Util.delta(headerDecoder) : 0;
        int chromaDCDelta = headerDecoder.decodeBit() > 0 ? VP8Util.delta(headerDecoder) : 0;
        int chromaACDelta = headerDecoder.decodeBit() > 0 ? VP8Util.delta(headerDecoder) : 0;
        if (headerDecoder.decodeBit() == 0) {
        }
        VP8Util.QuantizationParams quants = new VP8Util.QuantizationParams(yacIndex, ydcDelta, y2dcDelta, y2acDelta, chromaDCDelta, chromaACDelta);
        int[][][][] coefProbs = VP8Util.getDefaultCoefProbs();
        for (int i3 = 0; i3 < VP8Util.BLOCK_TYPES; i3++) {
            for (int j = 0; j < VP8Util.COEF_BANDS; j++) {
                for (int k = 0; k < VP8Util.PREV_COEF_CONTEXTS; k++) {
                    for (int l = 0; l < VP8Util.MAX_ENTROPY_TOKENS - 1; l++) {
                        if (headerDecoder.decodeBool(VP8Util.vp8CoefUpdateProbs[i3][j][k][l]) > 0) {
                            int newp = headerDecoder.decodeInt(8);
                            coefProbs[i3][j][k][l] = newp;
                        }
                    }
                }
            }
        }
        int macroBlockNoCoeffSkip = headerDecoder.decodeBit();
        Assert.assertEquals(1, macroBlockNoCoeffSkip);
        int probSkipFalse = headerDecoder.decodeInt(8);
        for (int mbRow = 0; mbRow < numberOfMBRows; mbRow++) {
            for (int mbCol = 0; mbCol < numberOfMBCols; mbCol++) {
                Macroblock mb = this.mbs[mbRow + 1][mbCol + 1];
                if (segmentation > 0) {
                    throw new UnsupportedOperationException("TODO: frames with multiple segments are not supported yet");
                }
                if (loopFilterDeltaFlag > 0) {
                    int level = filterLevel + refLoopFilterDeltas[0];
                    if (level < 0) {
                        level = 0;
                    } else if (level > 63) {
                        level = 63;
                    }
                    mb.filterLevel = level;
                    if (macroBlockNoCoeffSkip > 0) {
                        mb.skipCoeff = headerDecoder.decodeBool(probSkipFalse);
                    }
                    mb.lumaMode = headerDecoder.readTree(VP8Util.keyFrameYModeTree, VP8Util.keyFrameYModeProb);
                    if (mb.lumaMode == 4) {
                        for (int sbRow = 0; sbRow < 4; sbRow++) {
                            for (int sbCol = 0; sbCol < 4; sbCol++) {
                                Macroblock.Subblock sb = mb.ySubblocks[sbRow][sbCol];
                                Macroblock.Subblock A = sb.getAbove(VP8Util.PLANE.Y1, this.mbs);
                                Macroblock.Subblock L = sb.getLeft(VP8Util.PLANE.Y1, this.mbs);
                                sb.mode = headerDecoder.readTree(VP8Util.SubblockConstants.subblockModeTree, VP8Util.SubblockConstants.keyFrameSubblockModeProb[A.mode][L.mode]);
                            }
                        }
                    } else {
                        switch (mb.lumaMode) {
                            case 0:
                                fixedMode = 0;
                                break;
                            case 1:
                                fixedMode = 2;
                                break;
                            case 2:
                                fixedMode = 3;
                                break;
                            case 3:
                                fixedMode = 1;
                                break;
                            default:
                                fixedMode = 0;
                                break;
                        }
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 4; y++) {
                                mb.ySubblocks[y][x].mode = fixedMode;
                            }
                        }
                    }
                    mb.chromaMode = headerDecoder.readTree(VP8Util.vp8UVModeTree, VP8Util.vp8KeyFrameUVModeProb);
                } else {
                    throw new UnsupportedOperationException("TODO: frames with loopFilterDeltaFlag <= 0 are not supported yet");
                }
            }
        }
        for (int mbRow2 = 0; mbRow2 < numberOfMBRows; mbRow2++) {
            for (int mbCol2 = 0; mbCol2 < numberOfMBCols; mbCol2++) {
                Macroblock mb2 = this.mbs[mbRow2 + 1][mbCol2 + 1];
                mb2.decodeMacroBlock(this.mbs, decoder, coefProbs);
                mb2.dequantMacroBlock(this.mbs, quants);
            }
        }
        if (filterType > 0 && filterLevel != 0) {
            if (filterType == 2) {
                FilterUtil.loopFilterUV(this.mbs, sharpnessLevel, keyFrame);
                FilterUtil.loopFilterY(this.mbs, sharpnessLevel, keyFrame);
            } else if (filterType == 1) {
            }
        }
    }

    public Picture getPicture() {
        Picture p = Picture.create(this.width, this.height, ColorSpace.YUV420);
        int[] luma = p.getPlaneData(0);
        int[] cb = p.getPlaneData(1);
        int[] cr = p.getPlaneData(2);
        int mbWidth = VP8Util.getMacroblockCount(this.width);
        int mbHeight = VP8Util.getMacroblockCount(this.height);
        int strideLuma = mbWidth * 16;
        int strideChroma = mbWidth * 8;
        for (int mbRow = 0; mbRow < mbHeight; mbRow++) {
            for (int mbCol = 0; mbCol < mbWidth; mbCol++) {
                Macroblock mb = this.mbs[mbRow + 1][mbCol + 1];
                for (int lumaRow = 0; lumaRow < 4; lumaRow++) {
                    for (int lumaCol = 0; lumaCol < 4; lumaCol++) {
                        for (int lumaPRow = 0; lumaPRow < 4; lumaPRow++) {
                            for (int lumaPCol = 0; lumaPCol < 4; lumaPCol++) {
                                int y = (mbRow << 4) + (lumaRow << 2) + lumaPRow;
                                int x = (mbCol << 4) + (lumaCol << 2) + lumaPCol;
                                if (x < strideLuma && y < luma.length / strideLuma) {
                                    int yy = mb.ySubblocks[lumaRow][lumaCol].val[(lumaPRow * 4) + lumaPCol];
                                    luma[(strideLuma * y) + x] = yy;
                                }
                            }
                        }
                    }
                }
                for (int chromaRow = 0; chromaRow < 2; chromaRow++) {
                    for (int chromaCol = 0; chromaCol < 2; chromaCol++) {
                        for (int chromaPRow = 0; chromaPRow < 4; chromaPRow++) {
                            for (int chromaPCol = 0; chromaPCol < 4; chromaPCol++) {
                                int y2 = (mbRow << 3) + (chromaRow << 2) + chromaPRow;
                                int x2 = (mbCol << 3) + (chromaCol << 2) + chromaPCol;
                                if (x2 < strideChroma && y2 < cb.length / strideChroma) {
                                    int u = mb.uSubblocks[chromaRow][chromaCol].val[(chromaPRow * 4) + chromaPCol];
                                    int v = mb.vSubblocks[chromaRow][chromaCol].val[(chromaPRow * 4) + chromaPCol];
                                    cb[(strideChroma * y2) + x2] = u;
                                    cr[(strideChroma * y2) + x2] = v;
                                }
                            }
                        }
                    }
                }
            }
        }
        return p;
    }

    public static String printHexByte(byte b) {
        return "0x" + Integer.toHexString(b & 255);
    }
}
