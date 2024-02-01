package org.jcodec.codecs.h264;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.decode.CoeffTransformer;
import org.jcodec.codecs.h264.encode.DumbRateControl;
import org.jcodec.codecs.h264.encode.RateControl;
import org.jcodec.codecs.h264.io.CAVLC;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.RefPicMarkingIDR;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.io.model.SliceType;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.codecs.h264.io.write.SliceHeaderWriter;
import org.jcodec.common.ArrayUtil;
import org.jcodec.common.VideoEncoder;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class H264Encoder implements VideoEncoder {
    private CAVLC[] cavlc;
    private int[][] leftRow;

    /* renamed from: rc */
    private RateControl f1451rc;
    private int[][] topLine;

    public H264Encoder() {
        this(new DumbRateControl());
    }

    public H264Encoder(RateControl rc) {
        this.f1451rc = rc;
    }

    @Override // org.jcodec.common.VideoEncoder
    public ByteBuffer encodeFrame(Picture pic, ByteBuffer _out) {
        return encodeFrame(pic, _out, true, 0);
    }

    public ByteBuffer encodeFrame(Picture pic, ByteBuffer _out, boolean idr, int poc) {
        ByteBuffer dup = _out.duplicate();
        SeqParameterSet sps = initSPS(new Size(pic.getCroppedWidth(), pic.getCroppedHeight()));
        PictureParameterSet pps = initPPS();
        if (idr) {
            dup.putInt(1);
            new NALUnit(NALUnitType.SPS, 3).write(dup);
            writeSPS(dup, sps);
            dup.putInt(1);
            new NALUnit(NALUnitType.PPS, 3).write(dup);
            writePPS(dup, pps);
        }
        int mbWidth = sps.pic_width_in_mbs_minus1 + 1;
        this.leftRow = new int[][]{new int[16], new int[8], new int[8]};
        this.topLine = new int[][]{new int[mbWidth << 4], new int[mbWidth << 3], new int[mbWidth << 3]};
        encodeSlice(sps, pps, pic, dup, idr, poc);
        dup.flip();
        return dup;
    }

    private void writePPS(ByteBuffer dup, PictureParameterSet pps) {
        ByteBuffer tmp = ByteBuffer.allocate(1024);
        pps.write(tmp);
        tmp.flip();
        H264Utils.escapeNAL(tmp, dup);
    }

    private void writeSPS(ByteBuffer dup, SeqParameterSet sps) {
        ByteBuffer tmp = ByteBuffer.allocate(1024);
        sps.write(tmp);
        tmp.flip();
        H264Utils.escapeNAL(tmp, dup);
    }

    public PictureParameterSet initPPS() {
        PictureParameterSet pps = new PictureParameterSet();
        pps.pic_init_qp_minus26 = this.f1451rc.getInitQp() - 26;
        return pps;
    }

    public SeqParameterSet initSPS(Size sz) {
        boolean z = true;
        SeqParameterSet sps = new SeqParameterSet();
        sps.pic_width_in_mbs_minus1 = ((sz.getWidth() + 15) >> 4) - 1;
        sps.pic_height_in_map_units_minus1 = ((sz.getHeight() + 15) >> 4) - 1;
        sps.chroma_format_idc = ColorSpace.YUV420;
        sps.profile_idc = 66;
        sps.level_idc = 40;
        sps.frame_mbs_only_flag = true;
        int codedWidth = (sps.pic_width_in_mbs_minus1 + 1) << 4;
        int codedHeight = (sps.pic_height_in_map_units_minus1 + 1) << 4;
        if (codedWidth == sz.getWidth() && codedHeight == sz.getHeight()) {
            z = false;
        }
        sps.frame_cropping_flag = z;
        sps.frame_crop_right_offset = ((codedWidth - sz.getWidth()) + 1) >> 1;
        sps.frame_crop_bottom_offset = ((codedHeight - sz.getHeight()) + 1) >> 1;
        return sps;
    }

    private void encodeSlice(SeqParameterSet sps, PictureParameterSet pps, Picture pic, ByteBuffer dup, boolean idr, int poc) {
        BitWriter candidate;
        int qpDelta;
        this.cavlc = new CAVLC[]{new CAVLC(sps, pps, 2, 2), new CAVLC(sps, pps, 1, 1), new CAVLC(sps, pps, 1, 1)};
        this.f1451rc.reset();
        int qp = this.f1451rc.getInitQp();
        dup.putInt(1);
        new NALUnit(idr ? NALUnitType.IDR_SLICE : NALUnitType.NON_IDR_SLICE, 2).write(dup);
        SliceHeader sh = new SliceHeader();
        sh.slice_type = SliceType.I;
        if (idr) {
            sh.refPicMarkingIDR = new RefPicMarkingIDR(false, false);
        }
        sh.pps = pps;
        sh.sps = sps;
        sh.pic_order_cnt_lsb = poc << 1;
        BitWriter sliceData = new BitWriter(ByteBuffer.allocate(pic.getWidth() * pic.getHeight()));
        new SliceHeaderWriter().write(sh, idr, 2, sliceData);
        Picture outMB = Picture.create(16, 16, ColorSpace.YUV420);
        for (int mbY = 0; mbY < sps.pic_height_in_map_units_minus1 + 1; mbY++) {
            for (int mbX = 0; mbX < sps.pic_width_in_mbs_minus1 + 1; mbX++) {
                CAVLCWriter.writeUE(sliceData, 23);
                do {
                    candidate = sliceData.fork();
                    qpDelta = this.f1451rc.getQpDelta();
                    encodeMacroblock(pic, mbX, mbY, candidate, outMB, qp + qpDelta, qpDelta);
                } while (!this.f1451rc.accept(candidate.position() - sliceData.position()));
                sliceData = candidate;
                qp += qpDelta;
                collectPredictors(outMB, mbX);
            }
        }
        sliceData.write1Bit(1);
        sliceData.flush();
        ByteBuffer buf = sliceData.getBuffer();
        buf.flip();
        H264Utils.escapeNAL(buf, dup);
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

    private void encodeMacroblock(Picture pic, int mbX, int mbY, BitWriter out, Picture outMB, int qp, int qpDelta) {
        CAVLCWriter.writeUE(out, 0);
        CAVLCWriter.writeSE(out, qpDelta);
        luma(pic, mbX, mbY, out, qp, outMB);
        chroma(pic, mbX, mbY, out, qp, outMB);
    }

    private void chroma(Picture pic, int mbX, int mbY, BitWriter out, int qp, Picture outMB) {
        int cw = pic.getColor().compWidth[1];
        int ch = pic.getColor().compHeight[1];
        int x = mbX << (4 - cw);
        int y = mbY << (4 - ch);
        int[][] ac1 = transformChroma(pic, 1, qp, cw, ch, x, y, outMB);
        int[][] ac2 = transformChroma(pic, 2, qp, cw, ch, x, y, outMB);
        int[] dc1 = extractDC(ac1);
        int[] dc2 = extractDC(ac2);
        writeDC(1, mbX, mbY, out, qp, mbX << 1, mbY << 1, dc1);
        writeDC(2, mbX, mbY, out, qp, mbX << 1, mbY << 1, dc2);
        writeAC(1, mbX, mbY, out, mbX << 1, mbY << 1, ac1, qp);
        writeAC(2, mbX, mbY, out, mbX << 1, mbY << 1, ac2, qp);
        restorePlane(dc1, ac1, qp);
        putChroma(outMB.getData()[1], 1, x, y, ac1);
        restorePlane(dc2, ac2, qp);
        putChroma(outMB.getData()[2], 2, x, y, ac2);
    }

    private void luma(Picture pic, int mbX, int mbY, BitWriter out, int qp, Picture outMB) {
        int x = mbX << 4;
        int y = mbY << 4;
        int[][] ac = transform(pic, 0, qp, 0, 0, x, y);
        int[] dc = extractDC(ac);
        writeDC(0, mbX, mbY, out, qp, mbX << 2, mbY << 2, dc);
        writeAC(0, mbX, mbY, out, mbX << 2, mbY << 2, ac, qp);
        restorePlane(dc, ac, qp);
        putLuma(outMB.getPlaneData(0), lumaDCPred(x, y), ac, 4);
    }

    private void putChroma(int[] mb, int comp, int x, int y, int[][] ac) {
        putBlk(mb, chromaPredBlk0(comp, x, y), ac[0], 3, 0, 0);
        putBlk(mb, chromaPredBlk1(comp, x, y), ac[1], 3, 4, 0);
        putBlk(mb, chromaPredBlk2(comp, x, y), ac[2], 3, 0, 4);
        putBlk(mb, chromaPredBlk3(comp, x, y), ac[3], 3, 4, 4);
    }

    private void putLuma(int[] planeData, int pred, int[][] ac, int log2stride) {
        for (int blk = 0; blk < ac.length; blk++) {
            putBlk(planeData, pred, ac[blk], log2stride, H264Const.BLK_X[blk], H264Const.BLK_Y[blk]);
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

    private void restorePlane(int[] dc, int[][] ac, int qp) {
        if (dc.length == 4) {
            CoeffTransformer.invDC2x2(dc);
            CoeffTransformer.dequantizeDC2x2(dc, qp);
        } else if (dc.length == 8) {
            CoeffTransformer.invDC4x2(dc);
            CoeffTransformer.dequantizeDC4x2(dc, qp);
        } else {
            CoeffTransformer.invDC4x4(dc);
            CoeffTransformer.dequantizeDC4x4(dc, qp);
            CoeffTransformer.reorderDC4x4(dc);
        }
        for (int i = 0; i < ac.length; i++) {
            CoeffTransformer.dequantizeAC(ac[i], qp);
            ac[i][0] = dc[i];
            CoeffTransformer.idct4x4(ac[i]);
        }
    }

    private int[] extractDC(int[][] ac) {
        int[] dc = new int[ac.length];
        for (int i = 0; i < ac.length; i++) {
            dc[i] = ac[i][0];
            ac[i][0] = 0;
        }
        return dc;
    }

    private void writeAC(int comp, int mbX, int mbY, BitWriter out, int mbLeftBlk, int mbTopBlk, int[][] ac, int qp) {
        for (int i = 0; i < ac.length; i++) {
            CoeffTransformer.quantizeAC(ac[i], qp);
            this.cavlc[comp].writeACBlock(out, mbLeftBlk + H264Const.MB_BLK_OFF_LEFT[i], mbTopBlk + H264Const.MB_BLK_OFF_TOP[i], MBType.I_16x16, MBType.I_16x16, ac[i], H264Const.totalZeros16, 1, 15, CoeffTransformer.zigzag4x4);
        }
    }

    private void writeDC(int comp, int mbX, int mbY, BitWriter out, int qp, int mbLeftBlk, int mbTopBlk, int[] dc) {
        if (dc.length == 4) {
            CoeffTransformer.quantizeDC2x2(dc, qp);
            CoeffTransformer.fvdDC2x2(dc);
            this.cavlc[comp].writeChrDCBlock(out, dc, H264Const.totalZeros4, 0, dc.length, new int[]{0, 1, 2, 3});
        } else if (dc.length == 8) {
            CoeffTransformer.quantizeDC4x2(dc, qp);
            CoeffTransformer.fvdDC4x2(dc);
            this.cavlc[comp].writeChrDCBlock(out, dc, H264Const.totalZeros8, 0, dc.length, new int[]{0, 1, 2, 3, 4, 5, 6, 7});
        } else {
            CoeffTransformer.reorderDC4x4(dc);
            CoeffTransformer.quantizeDC4x4(dc, qp);
            CoeffTransformer.fvdDC4x4(dc);
            this.cavlc[comp].writeLumaDCBlock(out, mbLeftBlk, mbTopBlk, MBType.I_16x16, MBType.I_16x16, dc, H264Const.totalZeros16, 0, 16, CoeffTransformer.zigzag4x4);
        }
    }

    private int[][] transformChroma(Picture pic, int comp, int qp, int cw, int ch, int x, int y, Picture outMB) {
        int[][] ac = (int[][]) Array.newInstance(Integer.TYPE, 16 >> (cw + ch), 16);
        takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x, y, ac[0], chromaPredBlk0(comp, x, y));
        CoeffTransformer.fdct4x4(ac[0]);
        takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x + 4, y, ac[1], chromaPredBlk1(comp, x, y));
        CoeffTransformer.fdct4x4(ac[1]);
        takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x, y + 4, ac[2], chromaPredBlk2(comp, x, y));
        CoeffTransformer.fdct4x4(ac[2]);
        takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x + 4, y + 4, ac[3], chromaPredBlk3(comp, x, y));
        CoeffTransformer.fdct4x4(ac[3]);
        return ac;
    }

    private final int chromaPredOne(int[] pix, int x) {
        return ((((pix[x] + pix[x + 1]) + pix[x + 2]) + pix[x + 3]) + 2) >> 2;
    }

    private final int chromaPredTwo(int[] pix1, int[] pix2, int x, int y) {
        return ((((((((pix1[x] + pix1[x + 1]) + pix1[x + 2]) + pix1[x + 3]) + pix2[y]) + pix2[y + 1]) + pix2[y + 2]) + pix2[y + 3]) + 4) >> 3;
    }

    private int chromaPredBlk0(int comp, int x, int y) {
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

    private int chromaPredBlk1(int comp, int x, int y) {
        int predY = y & 7;
        if (y != 0) {
            return chromaPredOne(this.topLine[comp], x + 4);
        }
        if (x != 0) {
            return chromaPredOne(this.leftRow[comp], predY);
        }
        return 128;
    }

    private int chromaPredBlk2(int comp, int x, int y) {
        int predY = y & 7;
        if (x != 0) {
            return chromaPredOne(this.leftRow[comp], predY + 4);
        }
        if (y != 0) {
            return chromaPredOne(this.topLine[comp], x);
        }
        return 128;
    }

    private int chromaPredBlk3(int comp, int x, int y) {
        int predY = y & 7;
        if (x != 0 && y != 0) {
            return chromaPredTwo(this.leftRow[comp], this.topLine[comp], predY + 4, x + 4);
        }
        if (x != 0) {
            return chromaPredOne(this.leftRow[comp], predY + 4);
        }
        if (y != 0) {
            return chromaPredOne(this.topLine[comp], x + 4);
        }
        return 128;
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

    private int[][] transform(Picture pic, int comp, int qp, int cw, int ch, int x, int y) {
        int dcc = lumaDCPred(x, y);
        int[][] ac = (int[][]) Array.newInstance(Integer.TYPE, 16 >> (cw + ch), 16);
        for (int i = 0; i < ac.length; i++) {
            int[] coeff = ac[i];
            takeSubtract(pic.getPlaneData(comp), pic.getPlaneWidth(comp), pic.getPlaneHeight(comp), x + H264Const.BLK_X[i], y + H264Const.BLK_Y[i], coeff, dcc);
            CoeffTransformer.fdct4x4(coeff);
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
