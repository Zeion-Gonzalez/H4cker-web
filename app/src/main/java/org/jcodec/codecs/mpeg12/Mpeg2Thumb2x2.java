package org.jcodec.codecs.mpeg12;

import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.mpeg12.bitstream.PictureHeader;
import org.jcodec.codecs.mpeg12.bitstream.SequenceHeader;
import org.jcodec.common.dct.IDCT2x2;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;

/* loaded from: classes.dex */
public class Mpeg2Thumb2x2 extends MPEGDecoder {
    public static int[] BLOCK_POS_X = {0, 2, 0, 2, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 2, 2, 2, 2};
    public static int[] BLOCK_POS_Y = {0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
    public static int[][] scan2x2 = {new int[]{0, 1, 2, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, new int[]{0, 2, 4, 4, 1, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}};
    private MPEGPred localPred;
    private MPEGPred oldPred;

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void blockIntra(BitReader bits, VLC vlcCoeff, int[] block, int[] intra_dc_predictor, int blkIdx, int[] scan, int escSize, int intra_dc_mult, int qScale, int[] qmat) {
        int level;
        int cc = MPEGConst.BLOCK_TO_CC[blkIdx];
        int size = (cc == 0 ? MPEGConst.vlcDCSizeLuma : MPEGConst.vlcDCSizeChroma).readVLC(bits);
        int delta = size != 0 ? mpegSigned(bits, size) : 0;
        intra_dc_predictor[cc] = intra_dc_predictor[cc] + delta;
        block[0] = intra_dc_predictor[cc] * intra_dc_mult;
        block[3] = 0;
        block[2] = 0;
        block[1] = 0;
        int readVLC = 0;
        int idx = 0;
        while (idx < 6 && (readVLC = vlcCoeff.readVLC(bits)) != 2048) {
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
        IDCT2x2.idct(block, 0);
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
        block[3] = 0;
        block[2] = 0;
        block[1] = 0;
        int idx = -1;
        if (vlcCoeff == MPEGConst.vlcCoeff0 && bits.checkNBit(1) == 1) {
            bits.read1Bit();
            block[0] = toSigned(quantInter(1, qmat[0] * qScale), bits.read1Bit());
            idx = (-1) + 1;
        } else {
            block[0] = 0;
        }
        int readVLC = 0;
        while (idx < 6 && (readVLC = vlcCoeff.readVLC(bits)) != 2048) {
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
        IDCT2x2.idct(block, 0);
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    public int decodeMacroblock(PictureHeader ph, MPEGDecoder.Context context, int prevAddr, int[] qScaleCode, int[][] buf, int stride, BitReader bits, int vertOff, int vertStep, MPEGPred pred) {
        if (this.localPred == null || this.oldPred != pred) {
            this.localPred = new MPEGPredOct(pred);
            this.oldPred = pred;
        }
        return super.decodeMacroblock(ph, context, prevAddr, qScaleCode, buf, stride, bits, vertOff, vertStep, this.localPred);
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void mapBlock(int[] block, int[] out, int blkIdx, int dctType, int chromaFormat) {
        int stepVert = (chromaFormat == 1 && (blkIdx == 4 || blkIdx == 5)) ? 0 : dctType;
        int log2stride = blkIdx < 4 ? 2 : 2 - MPEGConst.SQUEEZE_X[chromaFormat];
        int blkIdxExt = blkIdx + (dctType << 4);
        int x = BLOCK_POS_X[blkIdxExt];
        int y = BLOCK_POS_Y[blkIdxExt];
        int off = (y << log2stride) + x;
        int stride = 1 << (log2stride + stepVert);
        out[off] = out[off] + block[0];
        int i = off + 1;
        out[i] = out[i] + block[1];
        int i2 = off + stride;
        out[i2] = out[i2] + block[2];
        int i3 = off + stride + 1;
        out[i3] = out[i3] + block[3];
    }

    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    protected void put(int[][] mbPix, int[][] buf, int stride, int chromaFormat, int mbX, int mbY, int width, int height, int vertOff, int vertStep) {
        int chromaStride = (((1 << MPEGConst.SQUEEZE_X[chromaFormat]) + stride) - 1) >> MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBW = 2 - MPEGConst.SQUEEZE_X[chromaFormat];
        int chromaMBH = 2 - MPEGConst.SQUEEZE_Y[chromaFormat];
        putSub(buf[0], (mbX << 2) + ((mbY << 2) * (stride << vertStep)) + (vertOff * stride), stride << vertStep, mbPix[0], 2, 2);
        putSub(buf[1], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[1], chromaMBW, chromaMBH);
        putSub(buf[2], (mbX << chromaMBW) + ((mbY << chromaMBH) * (chromaStride << vertStep)) + (vertOff * chromaStride), chromaStride << vertStep, mbPix[2], chromaMBW, chromaMBH);
    }

    private final void putSub(int[] big, int off, int stride, int[] block, int mbW, int mbH) {
        int blOff = 0;
        if (mbW == 1) {
            big[off] = clip(block[0]);
            big[off + 1] = clip(block[1]);
            big[off + stride] = clip(block[2]);
            big[off + stride + 1] = clip(block[3]);
            if (mbH == 2) {
                int off2 = off + (stride << 1);
                big[off2] = clip(block[4]);
                big[off2 + 1] = clip(block[5]);
                big[off2 + stride] = clip(block[6]);
                big[off2 + stride + 1] = clip(block[7]);
                return;
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            big[off] = clip(block[blOff]);
            big[off + 1] = clip(block[blOff + 1]);
            big[off + 2] = clip(block[blOff + 2]);
            big[off + 3] = clip(block[blOff + 3]);
            off += stride;
            blOff += 4;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg12.MPEGDecoder
    public MPEGDecoder.Context initContext(SequenceHeader sh, PictureHeader ph) {
        MPEGDecoder.Context context = super.initContext(sh, ph);
        context.codedWidth >>= 2;
        context.codedHeight >>= 2;
        context.picWidth >>= 2;
        context.picHeight >>= 2;
        context.scan = scan2x2[ph.pictureCodingExtension == null ? 0 : ph.pictureCodingExtension.alternate_scan];
        return context;
    }
}
