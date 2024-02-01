package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;
import org.jcodec.common.dct.IDCT4x4;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class JpegToThumb4x4 extends JpegDecoder {
    private static final int[] mapping4x4 = {0, 1, 4, 8, 5, 2, 3, 6, 9, 12, 16, 13, 10, 7, 16, 16, 16, 11, 14, 16, 16, 16, 16, 16, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16};

    public JpegToThumb4x4() {
    }

    public JpegToThumb4x4(boolean interlace, boolean topFieldFirst) {
        super(interlace, topFieldFirst);
    }

    @Override // org.jcodec.codecs.mjpeg.JpegDecoder
    void decodeBlock(BitReader bits, int[] dcPredictor, int[][] quant, VLC[] huff, Picture result, int[] buf, int blkX, int blkY, int plane, int chroma, int field, int step) {
        buf[15] = 0;
        buf[14] = 0;
        buf[13] = 0;
        buf[12] = 0;
        buf[11] = 0;
        buf[10] = 0;
        buf[9] = 0;
        buf[8] = 0;
        buf[7] = 0;
        buf[6] = 0;
        buf[5] = 0;
        buf[4] = 0;
        buf[3] = 0;
        buf[2] = 0;
        buf[1] = 0;
        int readDCValue = (readDCValue(bits, huff[chroma]) * quant[chroma][0]) + dcPredictor[plane];
        buf[0] = readDCValue;
        dcPredictor[plane] = readDCValue;
        readACValues(bits, buf, huff[chroma + 2], quant[chroma]);
        IDCT4x4.idct(buf, 0);
        putBlock4x4(result.getPlaneData(plane), result.getPlaneWidth(plane), buf, blkX, blkY, field, step);
    }

    private void putBlock4x4(int[] plane, int stride, int[] patch, int x, int y, int field, int step) {
        int stride2 = stride >> 1;
        int dstride = step * stride2;
        int off = (field * stride2) + ((y >> 1) * dstride) + (x >> 1);
        for (int i = 0; i < 16; i += 4) {
            plane[off] = MathUtil.clip(patch[i], 0, 255);
            plane[off + 1] = MathUtil.clip(patch[i + 1], 0, 255);
            plane[off + 2] = MathUtil.clip(patch[i + 2], 0, 255);
            plane[off + 3] = MathUtil.clip(patch[i + 3], 0, 255);
            off += dstride;
        }
    }

    @Override // org.jcodec.codecs.mjpeg.JpegDecoder
    void readACValues(BitReader in, int[] target, VLC table, int[] quantTable) {
        int code;
        int curOff = 1;
        do {
            code = table.readVLC16(in);
            if (code == 240) {
                curOff += 16;
            } else if (code > 0) {
                int rle = code >> 4;
                int curOff2 = curOff + rle;
                int len = code & 15;
                target[mapping4x4[curOff2]] = toValue(in.readNBit(len), len) * quantTable[curOff2];
                curOff = curOff2 + 1;
            }
            if (code == 0) {
                break;
            }
        } while (curOff < 19);
        if (code == 0) {
            return;
        }
        do {
            int code2 = table.readVLC16(in);
            if (code2 == 240) {
                curOff += 16;
            } else if (code2 > 0) {
                int rle2 = code2 >> 4;
                in.skip(code2 & 15);
                curOff = curOff + rle2 + 1;
            }
            if (code2 == 0) {
                return;
            }
        } while (curOff < 64);
    }

    @Override // org.jcodec.codecs.mjpeg.JpegDecoder
    public Picture decodeField(ByteBuffer data, int[][] data2, int field, int step) {
        Picture res = super.decodeField(data, data2, field, step);
        return new Picture(res.getWidth() >> 1, res.getHeight() >> 1, res.getData(), res.getColor(), new Rect(0, 0, res.getCroppedWidth() >> 1, res.getCroppedHeight() >> 1));
    }
}
