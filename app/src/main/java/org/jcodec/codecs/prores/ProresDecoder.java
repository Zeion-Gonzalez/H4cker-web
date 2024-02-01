package org.jcodec.codecs.prores;

import android.support.v4.view.PointerIconCompat;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.codecs.prores.ProresConsts;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.dct.SimpleIDCT10Bit;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class ProresDecoder implements VideoDecoder {
    static final int[] table = {8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static final int[] mask = {0, 0, 0, 0, 0, 0, 0, 0, -1};

    public static final int nZeros(int check16Bit) {
        int low = table[check16Bit & 255];
        int high = table[check16Bit >> 8];
        return (mask[high] & low) + high;
    }

    public static final int readCodeword(BitReader reader, Codebook codebook) {
        int q = nZeros(reader.check16Bits());
        reader.skipFast(q + 1);
        if (q > codebook.switchBits) {
            int bits = codebook.golombBits + q;
            if (bits > 16) {
                Logger.error("Broken prores slice");
            }
            return ((1 << bits) | reader.readFast16(bits)) - codebook.golombOffset;
        }
        if (codebook.riceOrder > 0) {
            return (q << codebook.riceOrder) | reader.readFast16(codebook.riceOrder);
        }
        return q;
    }

    public static final int golumbToSigned(int val) {
        return (val >> 1) ^ golumbSign(val);
    }

    public static final int golumbSign(int val) {
        return -(val & 1);
    }

    public static final void readDCCoeffs(BitReader bits, int[] qMat, int[] out, int blocksPerSlice, int blkSize) {
        int c = readCodeword(bits, ProresConsts.firstDCCodebook);
        if (c >= 0) {
            int prevDc = golumbToSigned(c);
            out[0] = qScale(qMat, 0, prevDc) + 4096;
            int code = 5;
            int sign = 0;
            int idx = blkSize;
            int i = 1;
            while (i < blocksPerSlice) {
                code = readCodeword(bits, ProresConsts.dcCodebooks[Math.min(code, 6)]);
                if (code >= 0) {
                    if (code != 0) {
                        sign ^= golumbSign(code);
                    } else {
                        sign = 0;
                    }
                    prevDc += MathUtil.toSigned((code + 1) >> 1, sign);
                    out[idx] = qScale(qMat, 0, prevDc) + 4096;
                    i++;
                    idx += blkSize;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final void readACCoeffs(BitReader bits, int[] qMat, int[] out, int blocksPerSlice, int[] scan, int max, int log2blkSize) {
        int run = 4;
        int level = 2;
        int blockMask = blocksPerSlice - 1;
        int log2BlocksPerSlice = MathUtil.log2(blocksPerSlice);
        int maxCoeffs = 64 << log2BlocksPerSlice;
        int pos = blockMask;
        while (true) {
            if ((bits.remaining() > 32 || bits.checkAllBits() != 0) && (run = readCodeword(bits, ProresConsts.runCodebooks[Math.min(run, 15)])) >= 0 && run < (maxCoeffs - pos) - 1) {
                pos += run + 1;
                level = readCodeword(bits, ProresConsts.levCodebooks[Math.min(level, 9)]) + 1;
                if (level >= 0 && level <= 65535) {
                    int sign = -bits.read1Bit();
                    int ind = pos >> log2BlocksPerSlice;
                    if (ind < max) {
                        out[((pos & blockMask) << log2blkSize) + scan[ind]] = qScale(qMat, ind, MathUtil.toSigned(level, sign));
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static final int qScale(int[] qMat, int ind, int val) {
        return (qMat[ind] * val) >> 2;
    }

    protected int[] decodeOnePlane(BitReader bits, int blocksPerSlice, int[] qMat, int[] scan, int mbX, int mbY, int plane) {
        int[] out = new int[blocksPerSlice << 6];
        try {
            readDCCoeffs(bits, qMat, out, blocksPerSlice, 64);
            readACCoeffs(bits, qMat, out, blocksPerSlice, scan, 64, 6);
        } catch (RuntimeException e) {
            System.err.println("Suppressing slice error at [" + mbX + ", " + mbY + "].");
        }
        for (int i = 0; i < blocksPerSlice; i++) {
            SimpleIDCT10Bit.idct10(out, i << 6);
        }
        return out;
    }

    @Override // org.jcodec.common.VideoDecoder
    public Picture decodeFrame(ByteBuffer data, int[][] target) {
        ProresConsts.FrameHeader fh = readFrameHeader(data);
        int codedWidth = (fh.width + 15) & (-16);
        int codedHeight = (fh.height + 15) & (-16);
        int lumaSize = codedWidth * codedHeight;
        int chromaSize = lumaSize >> (3 - fh.chromaType);
        if (target == null || target[0].length < lumaSize || target[1].length < chromaSize || target[2].length < chromaSize) {
            throw new RuntimeException("Provided output picture won't fit into provided buffer");
        }
        if (fh.frameType == 0) {
            decodePicture(data, target, fh.width, fh.height, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, 0, fh.chromaType);
        } else {
            decodePicture(data, target, fh.width, fh.height >> 1, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, fh.topFieldFirst ? 1 : 2, fh.chromaType);
            decodePicture(data, target, fh.width, fh.height >> 1, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, fh.topFieldFirst ? 2 : 1, fh.chromaType);
        }
        return new Picture(codedWidth, codedHeight, target, fh.chromaType == 2 ? ColorSpace.YUV422_10 : ColorSpace.YUV444_10);
    }

    public Picture[] decodeFields(ByteBuffer data, int[][][] target) {
        ProresConsts.FrameHeader fh = readFrameHeader(data);
        int codedWidth = (fh.width + 15) & (-16);
        int codedHeight = (fh.height + 15) & (-16);
        int lumaSize = codedWidth * codedHeight;
        int chromaSize = lumaSize >> 1;
        if (fh.frameType == 0) {
            if (target == null || target[0][0].length < lumaSize || target[0][1].length < chromaSize || target[0][2].length < chromaSize) {
                throw new RuntimeException("Provided output picture won't fit into provided buffer");
            }
            decodePicture(data, target[0], fh.width, fh.height, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, 0, fh.chromaType);
            return new Picture[]{new Picture(codedWidth, codedHeight, target[0], ColorSpace.YUV422_10)};
        }
        int lumaSize2 = lumaSize >> 1;
        int chromaSize2 = chromaSize >> 1;
        if (target == null || target[0][0].length < lumaSize2 || target[0][1].length < chromaSize2 || target[0][2].length < chromaSize2 || target[1][0].length < lumaSize2 || target[1][1].length < chromaSize2 || target[1][2].length < chromaSize2) {
            throw new RuntimeException("Provided output picture won't fit into provided buffer");
        }
        decodePicture(data, target[fh.topFieldFirst ? (char) 0 : (char) 1], fh.width, fh.height >> 1, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, 0, fh.chromaType);
        decodePicture(data, target[fh.topFieldFirst ? (char) 1 : (char) 0], fh.width, fh.height >> 1, codedWidth >> 4, fh.qMatLuma, fh.qMatChroma, fh.scan, 0, fh.chromaType);
        return new Picture[]{new Picture(codedWidth, codedHeight >> 1, target[0], ColorSpace.YUV422_10), new Picture(codedWidth, codedHeight >> 1, target[1], ColorSpace.YUV422_10)};
    }

    public static ProresConsts.FrameHeader readFrameHeader(ByteBuffer inp) {
        int[] scan;
        int frameSize = inp.getInt();
        String sig = readSig(inp);
        if (!"icpf".equals(sig)) {
            throw new RuntimeException("Not a prores frame");
        }
        short hdrSize = inp.getShort();
        inp.getShort();
        inp.getInt();
        short width = inp.getShort();
        short height = inp.getShort();
        int flags1 = inp.get();
        int frameType = (flags1 >> 2) & 3;
        int chromaType = (flags1 >> 6) & 3;
        boolean topFieldFirst = false;
        if (frameType == 0) {
            scan = ProresConsts.progressive_scan;
        } else {
            scan = ProresConsts.interlaced_scan;
            if (frameType == 1) {
                topFieldFirst = true;
            }
        }
        inp.get();
        inp.get();
        inp.get();
        inp.get();
        inp.get();
        inp.get();
        int flags2 = inp.get() & 255;
        int[] qMatLuma = new int[64];
        int[] qMatChroma = new int[64];
        if (hasQMatLuma(flags2)) {
            readQMat(inp, qMatLuma, scan);
        } else {
            Arrays.fill(qMatLuma, 4);
        }
        if (hasQMatChroma(flags2)) {
            readQMat(inp, qMatChroma, scan);
        } else {
            Arrays.fill(qMatChroma, 4);
        }
        inp.position((inp.position() + hdrSize) - ((hasQMatChroma(flags2) ? 64 : 0) + ((hasQMatLuma(flags2) ? 64 : 0) + 20)));
        return new ProresConsts.FrameHeader((frameSize - hdrSize) - 8, width, height, frameType, topFieldFirst, scan, qMatLuma, qMatChroma, chromaType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String readSig(ByteBuffer inp) {
        byte[] sig = new byte[4];
        inp.get(sig);
        return new String(sig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void decodePicture(ByteBuffer data, int[][] result, int width, int height, int mbWidth, int[] qMatLuma, int[] qMatChroma, int[] scan, int pictureType, int chromaType) {
        ProresConsts.PictureHeader ph = readPictureHeader(data);
        int mbX = 0;
        int mbY = 0;
        int sliceMbCount = 1 << ph.log2SliceMbWidth;
        for (int i = 0; i < ph.sliceSizes.length; i++) {
            while (mbWidth - mbX < sliceMbCount) {
                sliceMbCount >>= 1;
            }
            decodeSlice(NIOUtils.read(data, ph.sliceSizes[i]), qMatLuma, qMatChroma, scan, sliceMbCount, mbX, mbY, ph.sliceSizes[i], result, width, pictureType, chromaType);
            mbX += sliceMbCount;
            if (mbX == mbWidth) {
                sliceMbCount = 1 << ph.log2SliceMbWidth;
                mbX = 0;
                mbY++;
            }
        }
    }

    public static ProresConsts.PictureHeader readPictureHeader(ByteBuffer inp) {
        int i = (inp.get() & 255) >> 3;
        inp.getInt();
        int sliceCount = inp.getShort();
        int a = inp.get() & 255;
        int log2SliceMbWidth = a >> 4;
        short[] sliceSizes = new short[sliceCount];
        for (int i2 = 0; i2 < sliceCount; i2++) {
            sliceSizes[i2] = inp.getShort();
        }
        return new ProresConsts.PictureHeader(log2SliceMbWidth, sliceSizes);
    }

    private void decodeSlice(ByteBuffer data, int[] qMatLuma, int[] qMatChroma, int[] scan, int sliceMbCount, int mbX, int mbY, short sliceSize, int[][] result, int lumaStride, int pictureType, int chromaType) {
        int hdrSize = (data.get() & 255) >> 3;
        int qScale = clip(data.get() & 255, 1, JpegConst.APP0);
        if (qScale > 128) {
            qScale = (qScale - 96) << 2;
        }
        int yDataSize = data.getShort();
        int uDataSize = data.getShort();
        int vDataSize = hdrSize > 7 ? data.getShort() : ((sliceSize - uDataSize) - yDataSize) - hdrSize;
        int[] y = decodeOnePlane(bitstream(data, yDataSize), sliceMbCount << 2, scaleMat(qMatLuma, qScale), scan, mbX, mbY, 0);
        int chromaBlkCount = (sliceMbCount << chromaType) >> 1;
        int[] u = decodeOnePlane(bitstream(data, uDataSize), chromaBlkCount, scaleMat(qMatChroma, qScale), scan, mbX, mbY, 1);
        int[] v = decodeOnePlane(bitstream(data, vDataSize), chromaBlkCount, scaleMat(qMatChroma, qScale), scan, mbX, mbY, 2);
        putSlice(result, lumaStride, mbX, mbY, y, u, v, pictureType == 0 ? 0 : 1, pictureType == 2 ? 1 : 0, chromaType);
    }

    public static final int[] scaleMat(int[] qMatLuma, int qScale) {
        int[] res = new int[qMatLuma.length];
        for (int i = 0; i < qMatLuma.length; i++) {
            res[i] = qMatLuma[i] * qScale;
        }
        return res;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final BitReader bitstream(ByteBuffer data, int dataSize) {
        return new BitReader(NIOUtils.read(data, dataSize));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int clip(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    protected void putSlice(int[][] result, int lumaStride, int mbX, int mbY, int[] y, int[] u, int[] v, int dist, int shift, int chromaType) {
        int mbPerSlice = y.length >> 8;
        int chromaStride = lumaStride >> 1;
        putLuma(result[0], shift * lumaStride, lumaStride << dist, mbX, mbY, y, mbPerSlice, dist, shift);
        if (chromaType == 2) {
            putChroma(result[1], shift * chromaStride, chromaStride << dist, mbX, mbY, u, mbPerSlice, dist, shift);
            putChroma(result[2], shift * chromaStride, chromaStride << dist, mbX, mbY, v, mbPerSlice, dist, shift);
        } else {
            putLuma(result[1], shift * lumaStride, lumaStride << dist, mbX, mbY, u, mbPerSlice, dist, shift);
            putLuma(result[2], shift * lumaStride, lumaStride << dist, mbX, mbY, v, mbPerSlice, dist, shift);
        }
    }

    private void putLuma(int[] y, int off, int stride, int mbX, int mbY, int[] luma, int mbPerSlice, int dist, int shift) {
        int off2 = off + (mbX << 4) + ((mbY << 4) * stride);
        for (int k = 0; k < mbPerSlice; k++) {
            putBlock(y, off2, stride, luma, k << 8, dist, shift);
            putBlock(y, off2 + 8, stride, luma, (k << 8) + 64, dist, shift);
            putBlock(y, off2 + (stride * 8), stride, luma, (k << 8) + 128, dist, shift);
            putBlock(y, (stride * 8) + off2 + 8, stride, luma, (k << 8) + JpegConst.SOF0, dist, shift);
            off2 += 16;
        }
    }

    private void putChroma(int[] y, int off, int stride, int mbX, int mbY, int[] chroma, int mbPerSlice, int dist, int shift) {
        int off2 = off + (mbX << 3) + ((mbY << 4) * stride);
        for (int k = 0; k < mbPerSlice; k++) {
            putBlock(y, off2, stride, chroma, k << 7, dist, shift);
            putBlock(y, off2 + (stride * 8), stride, chroma, (k << 7) + 64, dist, shift);
            off2 += 8;
        }
    }

    private void putBlock(int[] square, int sqOff, int sqStride, int[] flat, int flOff, int dist, int shift) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                square[j + sqOff] = clip(flat[j + flOff], 4, PointerIconCompat.TYPE_ZOOM_OUT);
            }
            sqOff += sqStride;
            flOff += 8;
        }
    }

    static final boolean hasQMatChroma(int flags2) {
        return (flags2 & 1) != 0;
    }

    static final void readQMat(ByteBuffer inp, int[] qMatLuma, int[] scan) {
        byte[] b = new byte[64];
        inp.get(b);
        for (int i = 0; i < 64; i++) {
            qMatLuma[i] = b[scan[i]] & 255;
        }
    }

    static final boolean hasQMatLuma(int flags2) {
        return (flags2 & 2) != 0;
    }

    public boolean isProgressive(ByteBuffer data) {
        return (((data.get(20) & 255) >> 2) & 3) == 0;
    }

    @Override // org.jcodec.common.VideoDecoder
    public int probe(ByteBuffer data) {
        return (data.get(4) == 105 && data.get(5) == 99 && data.get(6) == 112 && data.get(7) == 102) ? 100 : 0;
    }
}
