package org.jcodec.codecs.vpx;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.jcodec.codecs.common.biari.VPxBooleanEncoder;
import org.jcodec.common.ArrayUtil;
import org.jcodec.common.VideoEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class VP8Encoder implements VideoEncoder {
    private VPXBitstream bitstream;
    private ByteBuffer dataBuffer;
    private ByteBuffer headerBuffer;
    private int[][] leftRow;
    private VPXQuantizer quantizer;

    /* renamed from: rc */
    private RateControl f1493rc;
    private int[] tmp;
    private int[][] topLine;

    public VP8Encoder(int qp) {
        this(new NopRateControl(qp));
    }

    public VP8Encoder(RateControl rc) {
        this.tmp = new int[16];
        this.f1493rc = rc;
    }

    @Override // org.jcodec.common.VideoEncoder
    public ByteBuffer encodeFrame(Picture pic, ByteBuffer _buf) {
        ByteBuffer out = _buf.duplicate();
        int mbWidth = (pic.getWidth() + 15) >> 4;
        int mbHeight = (pic.getHeight() + 15) >> 4;
        prepareBuffers(mbWidth, mbHeight);
        this.bitstream = new VPXBitstream(VPXConst.tokenDefaultBinProbs, mbWidth);
        this.leftRow = new int[][]{new int[16], new int[8], new int[8]};
        this.topLine = new int[][]{new int[mbWidth << 4], new int[mbWidth << 3], new int[mbWidth << 3]};
        initValue(this.leftRow, 129);
        initValue(this.topLine, 127);
        this.quantizer = new VPXQuantizer();
        Picture outMB = Picture.create(16, 16, ColorSpace.YUV420);
        int[] segmentQps = this.f1493rc.getSegmentQps();
        VPxBooleanEncoder boolEnc = new VPxBooleanEncoder(this.dataBuffer);
        int[] segmentMap = new int[mbWidth * mbHeight];
        int mbAddr = 0;
        for (int mbY = 0; mbY < mbHeight; mbY++) {
            initValue(this.leftRow, 129);
            int mbX = 0;
            while (mbX < mbWidth) {
                int before = boolEnc.position();
                int segment = this.f1493rc.getSegment();
                segmentMap[mbAddr] = segment;
                luma(pic, mbX, mbY, boolEnc, segmentQps[segment], outMB);
                chroma(pic, mbX, mbY, boolEnc, segmentQps[segment], outMB);
                this.f1493rc.report(boolEnc.position() - before);
                collectPredictors(outMB, mbX);
                mbX++;
                mbAddr++;
            }
        }
        boolEnc.stop();
        this.dataBuffer.flip();
        VPxBooleanEncoder boolEnc2 = new VPxBooleanEncoder(this.headerBuffer);
        int[] probs = calcSegmentProbs(segmentMap);
        writeHeader2(boolEnc2, segmentQps, probs);
        int mbAddr2 = 0;
        for (int mbY2 = 0; mbY2 < mbHeight; mbY2++) {
            int mbX2 = 0;
            while (mbX2 < mbWidth) {
                writeSegmetId(boolEnc2, segmentMap[mbAddr2], probs);
                boolEnc2.writeBit(145, 1);
                boolEnc2.writeBit(156, 0);
                boolEnc2.writeBit(163, 0);
                boolEnc2.writeBit(142, 0);
                mbX2++;
                mbAddr2++;
            }
        }
        boolEnc2.stop();
        this.headerBuffer.flip();
        out.order(ByteOrder.LITTLE_ENDIAN);
        writeHeader(out, pic.getWidth(), pic.getHeight(), this.headerBuffer.remaining());
        out.put(this.headerBuffer);
        out.put(this.dataBuffer);
        out.flip();
        return out;
    }

    private void prepareBuffers(int mbWidth, int mbHeight) {
        int dataBufSize = (mbHeight * mbHeight) << 10;
        int headerBufSize = (mbWidth * mbHeight) + 256;
        if (this.headerBuffer == null || this.headerBuffer.capacity() < headerBufSize) {
            this.headerBuffer = ByteBuffer.allocate(headerBufSize);
        } else {
            this.headerBuffer.clear();
        }
        if (this.dataBuffer == null || this.dataBuffer.capacity() < dataBufSize) {
            this.dataBuffer = ByteBuffer.allocate(dataBufSize);
        } else {
            this.dataBuffer.clear();
        }
    }

    private void writeSegmetId(VPxBooleanEncoder boolEnc, int id, int[] probs) {
        int bit1 = (id >> 1) & 1;
        boolEnc.writeBit(probs[0], bit1);
        boolEnc.writeBit(probs[bit1 + 1], id & 1);
    }

    private int[] calcSegmentProbs(int[] segmentMap) {
        int[] result = new int[3];
        for (int i : segmentMap) {
            switch (i) {
                case 0:
                    result[0] = result[0] + 1;
                    result[1] = result[1] + 1;
                    break;
                case 1:
                    result[0] = result[0] + 1;
                    break;
                case 2:
                    result[2] = result[2] + 1;
                    break;
            }
        }
        for (int i2 = 0; i2 < 3; i2++) {
            result[i2] = MathUtil.clip((result[i2] << 8) / segmentMap.length, 1, 255);
        }
        return result;
    }

    private void initValue(int[][] leftRow2, int val) {
        Arrays.fill(leftRow2[0], val);
        Arrays.fill(leftRow2[1], val);
        Arrays.fill(leftRow2[2], val);
    }

    private void writeHeader2(VPxBooleanEncoder boolEnc, int[] segmentQps, int[] probs) {
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 1);
        boolEnc.writeBit(128, 1);
        boolEnc.writeBit(128, 1);
        boolEnc.writeBit(128, 1);
        for (int i : segmentQps) {
            boolEnc.writeBit(128, 1);
            writeInt(boolEnc, i, 7);
            boolEnc.writeBit(128, 0);
        }
        for (int i2 = segmentQps.length; i2 < 4; i2++) {
            boolEnc.writeBit(128, 0);
        }
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        for (int i3 = 0; i3 < 3; i3++) {
            boolEnc.writeBit(128, 1);
            writeInt(boolEnc, probs[i3], 8);
        }
        boolEnc.writeBit(128, 0);
        writeInt(boolEnc, 1, 6);
        writeInt(boolEnc, 0, 3);
        boolEnc.writeBit(128, 0);
        writeInt(boolEnc, 0, 2);
        writeInt(boolEnc, segmentQps[0], 7);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        boolEnc.writeBit(128, 0);
        int[][][][] probFlags = VPXConst.tokenProbUpdateFlagProbs;
        for (int i4 = 0; i4 < probFlags.length; i4++) {
            for (int j = 0; j < probFlags[i4].length; j++) {
                for (int k = 0; k < probFlags[i4][j].length; k++) {
                    for (int l = 0; l < probFlags[i4][j][k].length; l++) {
                        boolEnc.writeBit(probFlags[i4][j][k][l], 0);
                    }
                }
            }
        }
        boolEnc.writeBit(128, 0);
    }

    void writeInt(VPxBooleanEncoder boolEnc, int data, int bits) {
        for (int bit = bits - 1; bit >= 0; bit--) {
            boolEnc.writeBit(128, (data >> bit) & 1);
        }
    }

    private void writeHeader(ByteBuffer out, int width, int height, int firstPart) {
        int header = (firstPart << 5) | 16 | 0 | 0;
        out.put((byte) (header & 255));
        out.put((byte) ((header >> 8) & 255));
        out.put((byte) ((header >> 16) & 255));
        out.put((byte) -99);
        out.put((byte) 1);
        out.put((byte) 42);
        out.putShort((short) width);
        out.putShort((short) height);
    }

    private void collectPredictors(Picture outMB, int mbX) {
        System.arraycopy(outMB.getPlaneData(0), 240, this.topLine[0], mbX << 4, 16);
        System.arraycopy(outMB.getPlaneData(1), 56, this.topLine[1], mbX << 3, 8);
        System.arraycopy(outMB.getPlaneData(2), 56, this.topLine[2], mbX << 3, 8);
        copyCol(outMB.getPlaneData(0), 15, 16, this.leftRow[0]);
        copyCol(outMB.getPlaneData(1), 7, 8, this.leftRow[1]);
        copyCol(outMB.getPlaneData(2), 7, 8, this.leftRow[2]);
    }

    private void copyCol(int[] planeData, int off, int stride, int[] out) {
        for (int i = 0; i < out.length; i++) {
            out[i] = planeData[off];
            off += stride;
        }
    }

    private void luma(Picture pic, int mbX, int mbY, VPxBooleanEncoder out, int qp, Picture outMB) {
        int x = mbX << 4;
        int y = mbY << 4;
        int[][] ac = transform(pic, 0, qp, x, y);
        int[] dc = extractDC(ac);
        writeLumaDC(mbX, mbY, out, qp, dc);
        writeLumaAC(mbX, mbY, out, ac, qp);
        restorePlaneLuma(dc, ac, qp);
        putLuma(outMB.getPlaneData(0), lumaDCPred(x, y), ac, 4);
    }

    private void writeLumaAC(int mbX, int mbY, VPxBooleanEncoder out, int[][] ac, int qp) {
        for (int i = 0; i < 16; i++) {
            this.quantizer.quantizeY(ac[i], qp);
            this.bitstream.encodeCoeffsDCT15(out, zigzag(ac[i], this.tmp), mbX, i & 3, i >> 2);
        }
    }

    private void writeLumaDC(int mbX, int mbY, VPxBooleanEncoder out, int qp, int[] dc) {
        VPXDCT.walsh4x4(dc);
        this.quantizer.quantizeY2(dc, qp);
        this.bitstream.encodeCoeffsWHT(out, zigzag(dc, this.tmp), mbX);
    }

    private void writeChroma(int comp, int mbX, int mbY, VPxBooleanEncoder boolEnc, int[][] ac, int qp) {
        for (int i = 0; i < 4; i++) {
            this.quantizer.quantizeUV(ac[i], qp);
            this.bitstream.encodeCoeffsDCTUV(boolEnc, zigzag(ac[i], this.tmp), comp, mbX, i & 1, i >> 1);
        }
    }

    private int[] zigzag(int[] zz, int[] tmp2) {
        for (int i = 0; i < 16; i++) {
            tmp2[i] = zz[VPXConst.zigzag[i]];
        }
        return tmp2;
    }

    private void chroma(Picture pic, int mbX, int mbY, VPxBooleanEncoder boolEnc, int qp, Picture outMB) {
        int x = mbX << 3;
        int y = mbY << 3;
        int chromaPred1 = chromaPredBlk(1, x, y);
        int chromaPred2 = chromaPredBlk(2, x, y);
        int[][] ac1 = transformChroma(pic, 1, qp, x, y, outMB, chromaPred1);
        int[][] ac2 = transformChroma(pic, 2, qp, x, y, outMB, chromaPred2);
        writeChroma(1, mbX, mbY, boolEnc, ac1, qp);
        writeChroma(2, mbX, mbY, boolEnc, ac2, qp);
        restorePlaneChroma(ac1, qp);
        putChroma(outMB.getData()[1], 1, x, y, ac1, chromaPred1);
        restorePlaneChroma(ac2, qp);
        putChroma(outMB.getData()[2], 2, x, y, ac2, chromaPred2);
    }

    private int[][] transformChroma(Picture pic, int comp, int qp, int x, int y, Picture outMB, int chromaPred) {
        int[][] ac = (int[][]) Array.newInstance(Integer.TYPE, 4, 16);
        for (int blk = 0; blk < ac.length; blk++) {
            int blkOffX = (blk & 1) << 2;
            int blkOffY = (blk >> 1) << 2;
            takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x + blkOffX, y + blkOffY, ac[blk], chromaPred);
            VPXDCT.fdct4x4(ac[blk]);
        }
        return ac;
    }

    private void putChroma(int[] mb, int comp, int x, int y, int[][] ac, int chromaPred) {
        for (int blk = 0; blk < 4; blk++) {
            putBlk(mb, chromaPred, ac[blk], 3, (blk & 1) << 2, (blk >> 1) << 2);
        }
    }

    private final int chromaPredOne(int[] pix, int x) {
        return ((((((((pix[x] + pix[x + 1]) + pix[x + 2]) + pix[x + 3]) + pix[x + 4]) + pix[x + 5]) + pix[x + 6]) + pix[x + 7]) + 4) >> 3;
    }

    private final int chromaPredTwo(int[] pix1, int[] pix2, int x, int y) {
        return ((((((((((((((((pix1[x] + pix1[x + 1]) + pix1[x + 2]) + pix1[x + 3]) + pix1[x + 4]) + pix1[x + 5]) + pix1[x + 6]) + pix1[x + 7]) + pix2[y]) + pix2[y + 1]) + pix2[y + 2]) + pix2[y + 3]) + pix2[y + 4]) + pix2[y + 5]) + pix2[y + 6]) + pix2[y + 7]) + 8) >> 4;
    }

    private int chromaPredBlk(int comp, int x, int y) {
        int predY = y & 7;
        if (x != 0 && y != 0) {
            return chromaPredTwo(this.leftRow[comp], this.topLine[comp], predY, x);
        }
        if (x != 0) {
            return chromaPredOne(this.leftRow[comp], predY);
        }
        if (y != 0) {
            return chromaPredOne(this.topLine[comp], x);
        }
        return 128;
    }

    private void putLuma(int[] planeData, int pred, int[][] ac, int log2stride) {
        for (int blk = 0; blk < ac.length; blk++) {
            int blkOffX = (blk & 3) << 2;
            int blkOffY = blk & (-4);
            putBlk(planeData, pred, ac[blk], log2stride, blkOffX, blkOffY);
        }
    }

    private void putBlk(int[] planeData, int pred, int[] block, int log2stride, int blkX, int blkY) {
        int stride = 1 << log2stride;
        int srcOff = 0;
        int dstOff = (blkY << log2stride) + blkX;
        for (int line = 0; line < 4; line++) {
            planeData[dstOff] = MathUtil.clip(block[srcOff] + pred, 0, 255);
            planeData[dstOff + 1] = MathUtil.clip(block[srcOff + 1] + pred, 0, 255);
            planeData[dstOff + 2] = MathUtil.clip(block[srcOff + 2] + pred, 0, 255);
            planeData[dstOff + 3] = MathUtil.clip(block[srcOff + 3] + pred, 0, 255);
            srcOff += 4;
            dstOff += stride;
        }
    }

    private void restorePlaneChroma(int[][] ac, int qp) {
        for (int i = 0; i < 4; i++) {
            this.quantizer.dequantizeUV(ac[i], qp);
            VPXDCT.idct4x4(ac[i]);
        }
    }

    private void restorePlaneLuma(int[] dc, int[][] ac, int qp) {
        this.quantizer.dequantizeY2(dc, qp);
        VPXDCT.iwalsh4x4(dc);
        for (int i = 0; i < 16; i++) {
            this.quantizer.dequantizeY(ac[i], qp);
            ac[i][0] = dc[i];
            VPXDCT.idct4x4(ac[i]);
        }
    }

    private int[] extractDC(int[][] ac) {
        int[] dc = new int[ac.length];
        for (int i = 0; i < ac.length; i++) {
            dc[i] = ac[i][0];
        }
        return dc;
    }

    private int lumaDCPred(int x, int y) {
        if (x == 0 && y == 0) {
            return 128;
        }
        if (y == 0) {
            return (ArrayUtil.sum(this.leftRow[0]) + 8) >> 4;
        }
        if (x == 0) {
            return (ArrayUtil.sum(this.topLine[0], x, 16) + 8) >> 4;
        }
        return ((ArrayUtil.sum(this.leftRow[0]) + ArrayUtil.sum(this.topLine[0], x, 16)) + 16) >> 5;
    }

    private int[][] transform(Picture pic, int comp, int qp, int x, int y) {
        int dcc = lumaDCPred(x, y);
        int[][] ac = (int[][]) Array.newInstance(Integer.TYPE, 16, 16);
        for (int i = 0; i < ac.length; i++) {
            int[] coeff = ac[i];
            int blkOffX = (i & 3) << 2;
            int blkOffY = i & (-4);
            takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x + blkOffX, y + blkOffY, coeff, dcc);
            VPXDCT.fdct4x4(coeff);
        }
        return ac;
    }

    private final void takeSubtract(int[] planeData, int planeWidth, int planeHeight, int x, int y, int[] coeff, int dc) {
        if (x + 4 < planeWidth && y + 4 < planeHeight) {
            takeSubtractSafe(planeData, planeWidth, planeHeight, x, y, coeff, dc);
        } else {
            takeSubtractUnsafe(planeData, planeWidth, planeHeight, x, y, coeff, dc);
        }
    }

    private final void takeSubtractSafe(int[] planeData, int planeWidth, int planeHeight, int x, int y, int[] coeff, int dc) {
        int i = 0;
        int srcOff = (y * planeWidth) + x;
        int dstOff = 0;
        while (i < 4) {
            coeff[dstOff] = planeData[srcOff] - dc;
            coeff[dstOff + 1] = planeData[srcOff + 1] - dc;
            coeff[dstOff + 2] = planeData[srcOff + 2] - dc;
            coeff[dstOff + 3] = planeData[srcOff + 3] - dc;
            i++;
            srcOff += planeWidth;
            dstOff += 4;
        }
    }

    private final void takeSubtractUnsafe(int[] planeData, int planeWidth, int planeHeight, int x, int y, int[] coeff, int dc) {
        int outOff;
        int outOff2;
        int outOff3 = 0;
        int i = y;
        while (i < Math.min(y + 4, planeHeight)) {
            int off = (i * planeWidth) + Math.min(x, planeWidth);
            int j = x;
            while (j < Math.min(x + 4, planeWidth)) {
                coeff[outOff3] = planeData[off] - dc;
                j++;
                off++;
                outOff3++;
            }
            int off2 = off - 1;
            while (true) {
                outOff2 = outOff3;
                if (j < x + 4) {
                    outOff3 = outOff2 + 1;
                    coeff[outOff2] = planeData[off2] - dc;
                    j++;
                }
            }
            i++;
            outOff3 = outOff2;
        }
        while (i < y + 4) {
            int off3 = ((planeHeight * planeWidth) - planeWidth) + Math.min(x, planeWidth);
            int j2 = x;
            while (j2 < Math.min(x + 4, planeWidth)) {
                coeff[outOff3] = planeData[off3] - dc;
                j2++;
                off3++;
                outOff3++;
            }
            int off4 = off3 - 1;
            while (true) {
                outOff = outOff3;
                if (j2 < x + 4) {
                    outOff3 = outOff + 1;
                    coeff[outOff] = planeData[off4] - dc;
                    j2++;
                }
            }
            i++;
            outOff3 = outOff;
        }
    }

    @Override // org.jcodec.common.VideoEncoder
    public ColorSpace[] getSupportedColorSpaces() {
        return new ColorSpace[]{ColorSpace.YUV420J};
    }
}
