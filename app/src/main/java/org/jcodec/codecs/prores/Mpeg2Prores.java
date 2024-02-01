package org.jcodec.codecs.prores;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.mpeg12.bitstream.GOPHeader;
import org.jcodec.codecs.mpeg12.bitstream.SequenceHeader;
import org.jcodec.codecs.prores.ProresEncoder;
import org.jcodec.common.dct.DCTRef;
import org.jcodec.common.dct.SimpleIDCT10Bit;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public class Mpeg2Prores extends MPEGDecoder {
    private DCT2Prores dct2Prores;

    public Mpeg2Prores(SequenceHeader sh, GOPHeader gh, ProresEncoder.Profile profile) {
        super(sh, gh);
        this.dct2Prores = new DCT2Prores(profile);
    }

    protected void idctPut(int[] block, int[][] buf, int stride, int chromaFormat, int blkNo, int mbX, int mbY, int dctType) {
        int mbAddr = ((stride >> 4) * mbY) + mbX;
        int off = blkNo < 4 ? (mbAddr << 8) + (blkNo << 6) : (mbAddr << (chromaFormat + 5)) + (((blkNo - 4) >> 1) << 6);
        System.arraycopy(block, 0, buf[MPEGConst.BLOCK_TO_CC[blkNo]], off, 64);
        buf[3][mbAddr] = dctType;
    }

    public ByteBuffer transcode(ByteBuffer in, ByteBuffer _out) {
        ByteBuffer out = _out.slice();
        int width = (this.f1475sh.horizontal_size + 15) & (-16);
        int height = (this.f1475sh.vertical_size + 15) & (-16);
        int[][] buffer = {new int[width * height], new int[width * height], new int[width * height], new int[(width >> 4) * (height >> 4)]};
        Picture dct = decodeFrame(in, buffer);
        Picture[] pic = convert(dct);
        if (pic.length == 1) {
            this.dct2Prores.encodeFrame(out, pic[0]);
        } else {
            this.dct2Prores.encodeFrame(out, pic[0], pic[1]);
        }
        out.flip();
        return out;
    }

    private Picture[] convert(Picture dct) {
        int nInterlaced = 0;
        int[] arr$ = dct.getPlaneData(3);
        for (int i : arr$) {
            nInterlaced += i;
        }
        if (nInterlaced == 0) {
            upShift(dct);
            Picture[] result = {colorCvt(dct)};
            return result;
        }
        Picture[] field = interlaced(dct);
        Picture[] result2 = {colorCvt(field[0]), colorCvt(field[1])};
        return result2;
    }

    private void upShift(Picture dct) {
        int[][] arr$ = dct.getData();
        for (int[] is : arr$) {
            upShift(is, 0, is.length);
        }
    }

    private Picture[] interlaced(Picture dct) {
        int mbWidth = (dct.getWidth() + 15) >> 4;
        int mbHeight = (dct.getHeight() + 15) >> 4;
        Picture field1 = Picture.create(dct.getWidth(), dct.getHeight() >> 1, dct.getColor());
        Picture field2 = Picture.create(dct.getWidth(), dct.getHeight() >> 1, dct.getColor());
        splitY(mbWidth, mbHeight, dct.getPlaneData(0), field1.getPlaneData(0), field2.getPlaneData(0), dct.getPlaneData(3));
        splitCbCr(mbWidth, mbHeight, dct.getPlaneData(1), field1.getPlaneData(1), field2.getPlaneData(1), dct.getPlaneData(3));
        splitCbCr(mbWidth, mbHeight, dct.getPlaneData(2), field1.getPlaneData(2), field2.getPlaneData(2), dct.getPlaneData(3));
        return new Picture[]{field1, field2};
    }

    private final void splitY(int mbWidth, int mbHeight, int[] y, int[] y1, int[] y2, int[] dctTypes) {
        int dstOff = 0;
        int srcOff = 0;
        int i = 0;
        for (int mbY = 0; mbY < mbHeight; mbY++) {
            int mbX = 0;
            while (mbX < mbWidth) {
                if (dctTypes[i] == 0) {
                    SimpleIDCT10Bit.idct10(y, srcOff);
                    SimpleIDCT10Bit.idct10(y, srcOff + 64);
                    SimpleIDCT10Bit.idct10(y, srcOff + 128);
                    SimpleIDCT10Bit.idct10(y, srcOff + JpegConst.SOF0);
                    deinterleave(y, srcOff, srcOff + 128, y1, y2, dstOff);
                    deinterleave(y, srcOff + 64, srcOff + JpegConst.SOF0, y1, y2, dstOff + 64);
                    DCTRef.fdct(y1, dstOff);
                    DCTRef.fdct(y1, dstOff + 64);
                    DCTRef.fdct(y2, dstOff);
                    DCTRef.fdct(y2, dstOff + 64);
                } else {
                    copyShift(y, srcOff, y1, dstOff, 128);
                    copyShift(y, srcOff + 128, y2, dstOff, 128);
                }
                mbX++;
                i++;
                dstOff += 256;
                srcOff += 256;
            }
            dstOff = (mbY & 1) == 0 ? dstOff - ((mbWidth << 8) - 128) : dstOff - 128;
        }
    }

    private final void copyShift(int[] src, int srcOff, int[] dst, int dstOff, int len) {
        int i = 0;
        int dstOff2 = dstOff;
        int srcOff2 = srcOff;
        while (i < len) {
            src[srcOff2] = dst[dstOff2] << 2;
            i++;
            dstOff2++;
            srcOff2++;
        }
    }

    private final void splitCbCr(int mbWidth, int mbHeight, int[] y, int[] y1, int[] y2, int[] dctTypes) {
        int dstOff = 0;
        int srcOff = 0;
        int i = 0;
        for (int mbY = 0; mbY < mbHeight; mbY++) {
            int mbX = 0;
            while (mbX < mbWidth) {
                if (dctTypes[i] == 0) {
                    SimpleIDCT10Bit.idct10(y, srcOff);
                    SimpleIDCT10Bit.idct10(y, srcOff + 64);
                    deinterleave(y, srcOff, srcOff + 64, y1, y2, dstOff);
                    DCTRef.fdct(y1, dstOff);
                    DCTRef.fdct(y2, dstOff);
                } else {
                    copyShift(y, srcOff, y1, dstOff, 64);
                    copyShift(y, srcOff + 64, y2, dstOff, 64);
                }
                mbX++;
                i++;
                dstOff += 128;
                srcOff += 128;
            }
            dstOff = (mbY & 1) == 0 ? dstOff - ((mbWidth << 7) - 64) : dstOff - 64;
        }
    }

    private void deinterleave(int[] y, int topOff, int botOff, int[] y1, int[] y2, int blkOff) {
        copyLine(y, y1, topOff + 0, blkOff + 0);
        copyLine(y, y1, topOff + 16, blkOff + 8);
        copyLine(y, y1, topOff + 32, blkOff + 16);
        copyLine(y, y1, topOff + 48, blkOff + 24);
        copyLine(y, y1, botOff + 0, blkOff + 32);
        copyLine(y, y1, botOff + 16, blkOff + 40);
        copyLine(y, y1, botOff + 32, blkOff + 48);
        copyLine(y, y1, botOff + 48, blkOff + 56);
        copyLine(y, y2, topOff + 8, blkOff + 0);
        copyLine(y, y2, topOff + 24, blkOff + 8);
        copyLine(y, y2, topOff + 40, blkOff + 16);
        copyLine(y, y2, topOff + 56, blkOff + 24);
        copyLine(y, y2, botOff + 8, blkOff + 32);
        copyLine(y, y2, botOff + 24, blkOff + 40);
        copyLine(y, y2, botOff + 40, blkOff + 48);
        copyLine(y, y2, botOff + 56, blkOff + 56);
    }

    private Picture progressive(Picture dct) {
        progressiveY(dct.getPlaneData(0), dct.getPlaneData(3));
        progressiveCbCr(dct.getPlaneData(0), dct.getPlaneData(3));
        progressiveCbCr(dct.getPlaneData(0), dct.getPlaneData(3));
        return dct;
    }

    private void progressiveY(int[] y, int[] dctTypes) {
        for (int i = 0; i < dctTypes.length; i++) {
            if (dctTypes[i] == 1) {
                SimpleIDCT10Bit.idct10(y, (i << 8) + 0);
                SimpleIDCT10Bit.idct10(y, (i << 8) + 64);
                SimpleIDCT10Bit.idct10(y, (i << 8) + 128);
                SimpleIDCT10Bit.idct10(y, (i << 8) + JpegConst.SOF0);
                interleave(y, (i << 8) + 0, (i << 8) + 128);
                interleave(y, (i << 8) + 64, (i << 8) + JpegConst.SOF0);
                DCTRef.fdct(y, (i << 8) + 0);
                DCTRef.fdct(y, (i << 8) + 64);
                DCTRef.fdct(y, (i << 8) + 128);
                DCTRef.fdct(y, (i << 8) + JpegConst.SOF0);
            } else {
                upShift(y, i << 8, 256);
            }
        }
    }

    private void upShift(int[] y, int off, int len) {
        int i = 0;
        int off2 = off;
        while (i < len) {
            y[off2] = y[off2] << 2;
            i++;
            off2++;
        }
    }

    private void progressiveCbCr(int[] y, int[] dctTypes) {
        for (int i = 0; i < dctTypes.length; i++) {
            if (dctTypes[i] == 1) {
                SimpleIDCT10Bit.idct10(y, (i << 7) + 0);
                SimpleIDCT10Bit.idct10(y, (i << 7) + 64);
                interleave(y, (i << 7) + 0, (i << 7) + 64);
                DCTRef.fdct(y, (i << 7) + 0);
                DCTRef.fdct(y, (i << 7) + 64);
            } else {
                upShift(y, i << 7, 128);
            }
        }
    }

    private void interleave(int[] y, int off1, int off2) {
        int[] tmp = new int[64];
        for (int i = 0; i < 64; i++) {
            tmp[i] = y[off2 + i];
        }
        copyLine(y, y, off1 + 56, off2 + 48);
        copyLine(y, y, off1 + 48, off2 + 32);
        copyLine(y, y, off1 + 40, off2 + 16);
        copyLine(y, y, off1 + 32, off2);
        copyLine(y, y, off1 + 24, off1 + 48);
        copyLine(y, y, off1 + 16, off1 + 32);
        copyLine(y, y, off1 + 8, off1 + 16);
        copyLine(tmp, y, 0, off1 + 8);
        copyLine(tmp, y, 8, off1 + 24);
        copyLine(tmp, y, 16, off1 + 40);
        copyLine(tmp, y, 24, off1 + 56);
        copyLine(tmp, y, 32, off2 + 8);
        copyLine(tmp, y, 40, off2 + 24);
        copyLine(tmp, y, 48, off2 + 40);
    }

    private final void copyLine(int[] from, int[] to, int offFrom, int offTo) {
        int i = 0;
        while (true) {
            int offTo2 = offTo;
            int offFrom2 = offFrom;
            if (i < 8) {
                offTo = offTo2 + 1;
                offFrom = offFrom2 + 1;
                to[offTo2] = from[offFrom2];
                i++;
            } else {
                return;
            }
        }
    }

    private Picture colorCvt(Picture in) {
        if (in.getColor() == ColorSpace.YUV422_10) {
            return in;
        }
        Transform trans = ColorUtil.getTransform(in.getColor(), ColorSpace.YUV422_10);
        Picture out = Picture.create(in.getWidth(), in.getHeight(), ColorSpace.YUV422_10);
        trans.transform(in, out);
        return out;
    }
}
