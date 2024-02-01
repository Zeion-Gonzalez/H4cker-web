package org.jcodec.codecs.mpeg12;

import java.util.Arrays;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.mpeg12.bitstream.PictureHeader;
import org.jcodec.codecs.mpeg12.bitstream.SequenceHeader;
import org.jcodec.common.dct.IDCT4x4;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;

/* loaded from: classes.dex */
public class Mpeg2Thumb4x4 extends MPEGDecoder {
    public static int[] BLOCK_POS_X = {0, 4, 0, 4, 0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0, 0, 0, 4, 0, 4, 0, 0, 0, 0, 4, 4, 4, 4};
    public static int[] BLOCK_POS_Y = {0, 0, 4, 4, 0, 0, 4, 4, 0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
    public static int[][] scan4x4 = {new int[]{0, 1, 4, 8, 5, 2, 3, 6, 9, 12, 16, 13, 10, 7, 16, 16, 16, 11, 14, 16, 16, 16, 16, 16, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16}, new int[]{0, 4, 8, 12, 1, 5, 2, 6, 9, 13, 16, 16, 16, 16, 16, 16, 16, 16, 14, 10, 3, 7, 16, 16, 11, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16}};
    private MPEGPred localPred;
    private MPEGPred oldPred;

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void blockIntra(BitReader bits, VLC vlcCoeff, int[] block, int[] intra_dc_predictor, int blkIdx, int[] scan, int escSize, int intra_dc_mult, int qScale, int[] qmat) {
        int level;
        int cc = MPEGConst.BLOCK_TO_CC[blkIdx];
        int size = (cc == 0 ? MPEGConst.vlcDCSizeLuma : MPEGConst.vlcDCSizeChroma).readVLC(bits);
        int delta = size != 0 ? mpegSigned(bits, size) : 0;
        intra_dc_predictor[cc] = intra_dc_predictor[cc] + delta;
        Arrays.fill(block, 1, 16, 0);
        block[0] = intra_dc_predictor[cc] * intra_dc_mult;
        int readVLC = 0;
        int idx = 0;
        while (true) {
            if (idx >= (scan == scan4x4[1] ? 7 : 0) + 19 || (readVLC = vlcCoeff.readVLC(bits)) == 2048) {
                break;
            }
            if (readVLC == 2049) {
                idx += bits.readNBit(6) + 1;
                int level2 = twosSigned(bits, escSize) * qScale * qmat[idx];
                level = level2 >= 0 ? level2 >> 4 : -((-level2) >> 4);
            } else {
                idx += (readVLC >> 6) + 1;
                level = toSigned((((readVLC & 63) * qScale) * qmat[idx]) >> 4, bits.read1Bit());
            }
            block[scan[idx]] = level;
        }
        if (readVLC != 2048) {
            finishOff(bits, idx, vlcCoeff, escSize);
        }
        IDCT4x4.idct(block, 0);
    }

    private void finishOff(BitReader bits, int idx, VLC vlcCoeff, int escSize) {
        int readVLC;
        while (idx < 64 && (readVLC = vlcCoeff.readVLC(bits)) != 2048) {
            if (readVLC == 2049) {
                idx += bits.readNBit(6) + 1;
                bits.readNBit(escSize);
            } else {
                bits.read1Bit();
            }
        }
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void blockInter(BitReader bits, VLC vlcCoeff, int[] block, int[] scan, int escSize, int qScale, int[] qmat) {
        int ac;
        Arrays.fill(block, 1, 16, 0);
        int idx = -1;
        if (vlcCoeff == MPEGConst.vlcCoeff0 && bits.checkNBit(1) == 1) {
            bits.read1Bit();
            block[0] = toSigned(quantInter(1, qmat[0] * qScale), bits.read1Bit());
            idx = (-1) + 1;
        } else {
            block[0] = 0;
        }
        int readVLC = 0;
        while (true) {
            if (idx >= (scan == scan4x4[1] ? 7 : 0) + 19 || (readVLC = vlcCoeff.readVLC(bits)) == 2048) {
                break;
            }
            if (readVLC == 2049) {
                idx += bits.readNBit(6) + 1;
                ac = quantInterSigned(twosSigned(bits, escSize), qmat[idx] * qScale);
            } else {
                idx += (readVLC >> 6) + 1;
                ac = toSigned(quantInter(readVLC & 63, qmat[idx] * qScale), bits.read1Bit());
            }
            block[scan[idx]] = ac;
        }
        if (readVLC != 2048) {
            finishOff(bits, idx, vlcCoeff, escSize);
        }
        IDCT4x4.idct(block, 0);
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    public int decodeMacroblock(PictureHeader ph, MPEGDecoder.Context context, int prevAddr, int[] qScaleCode, int[][] buf, int stride, BitReader bits, int vertOff, int vertStep, MPEGPred pred) {
        if (this.localPred == null || this.oldPred != pred) {
            this.localPred = new MPEGPredQuad(pred);
            this.oldPred = pred;
        }
        return super.decodeMacroblock(ph, context, prevAddr, qScaleCode, buf, stride, bits, vertOff, vertStep, this.localPred);
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void mapBlock(int[] block, int[] out, int blkIdx, int dctType, int chromaFormat) {
        int stepVert = (chromaFormat == 1 && (blkIdx == 4 || blkIdx == 5)) ? 0 : dctType;
        int log2stride = blkIdx < 4 ? 3 : 3 - MPEGConst.SQUEEZE_X[chromaFormat];
        int blkIdxExt = blkIdx + (dctType << 4);
        int x = BLOCK_POS_X[blkIdxExt];
        int y = BLOCK_POS_Y[blkIdxExt];
        int off = (y << log2stride) + x;
        int stride = 1 << (log2stride + stepVert);
        int i = 0;
        while (i < 16) {
            out[off] = out[off] + block[i];
            int i2 = off + 1;
            out[i2] = out[i2] + block[i + 1];
            int i3 = off + 2;
            out[i3] = out[i3] + block[i + 2];
            int i4 = off + 3;
            out[i4] = out[i4] + block[i + 3];
            i += 4;
            off += stride;
        }
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void put(int[][] mbPix, int[][] buf, int stride, int chromaFormat, int mbX, int mbY, int width, int height, int vertOff, int vertStep) {
        int chromaStride = (((1 << MPEGConst.SQUEEZE_X[chromaFormat]) + stride) - 1) >> MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBW = 3 - MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBH = 3 - MPEGConst.SQUEEZE_Y[chromaFormat];
        putSub(buf[0], (mbX << 3) + ((mbY << 3) * (stride << vertStep)) + (vertOff * stride), stride << vertStep, mbPix[0], 3, 3);
        putSub(buf[1], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[1], chromaMBW, chromaMBH);
        putSub(buf[2], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[2], chromaMBW, chromaMBH);
    }

    private final void putSub(int[] big, int off, int stride, int[] block, int mbW, int mbH) {
        int blOff = 0;
        if (mbW == 2) {
            for (int i = 0; i < (1 << mbH); i++) {
                big[off] = clip(block[blOff]);
                big[off + 1] = clip(block[blOff + 1]);
                big[off + 2] = clip(block[blOff + 2]);
                big[off + 3] = clip(block[blOff + 3]);
                blOff += 4;
                off += stride;
            }
            return;
        }
        for (int i2 = 0; i2 < (1 << mbH); i2++) {
            big[off] = clip(block[blOff]);
            big[off + 1] = clip(block[blOff + 1]);
            big[off + 2] = clip(block[blOff + 2]);
            big[off + 3] = clip(block[blOff + 3]);
            big[off + 4] = clip(block[blOff + 4]);
            big[off + 5] = clip(block[blOff + 5]);
            big[off + 6] = clip(block[blOff + 6]);
            big[off + 7] = clip(block[blOff + 7]);
            blOff += 8;
            off += stride;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    public MPEGDecoder.Context initContext(SequenceHeader sh, PictureHeader ph) {
        MPEGDecoder.Context context = super.initContext(sh, ph);
        context.codedWidth >>= 1;
        context.codedHeight >>= 1;
        context.picWidth >>= 1;
        context.picHeight >>= 1;
        context.scan = scan4x4[ph.pictureCodingExtension == null ? 0 : ph.pictureCodingExtension.alternate_scan];
        return context;
    }
}
