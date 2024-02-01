package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;
import org.jcodec.common.dct.IDCT2x2;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class JpegToThumb2x2 extends JpegDecoder {
    private static final int[] mapping2x2 = {0, 1, 2, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

    public JpegToThumb2x2() {
    }

    public JpegToThumb2x2(boolean interlace, boolean topFieldFirst) {
        super(interlace, topFieldFirst);
    }

    @Override // org.jcodec.codecs.mjpeg.JpegDecoder
    void decodeBlock(BitReader bits, int[] dcPredictor, int[][] quant, VLC[] huff, Picture result, int[] buf, int blkX, int blkY, int plane, int chroma, int field, int step) {
        buf[3] = 0;
        buf[2] = 0;
        buf[1] = 0;
        int readDCValue = (readDCValue(bits, huff[chroma]) * quant[chroma][0]) + dcPredictor[plane];
        buf[0] = readDCValue;
        dcPredictor[plane] = readDCValue;
        readACValues(bits, buf, huff[chroma + 2], quant[chroma]);
        IDCT2x2.idct(buf, 0);
        putBlock2x2(result.getPlaneData(plane), result.getPlaneWidth(plane), buf, blkX, blkY, field, step);
    }

    private void putBlock2x2(int[] plane, int stride, int[] patch, int x, int y, int field, int step) {
        int stride2 = stride >> 2;
        int dstride = stride2 * step;
        int off = (field * stride2) + ((y >> 2) * dstride) + (x >> 2);
        plane[off] = MathUtil.clip(patch[0], 0, 255);
        plane[off + 1] = MathUtil.clip(patch[1], 0, 255);
        plane[off + dstride] = MathUtil.clip(patch[2], 0, 255);
        plane[off + dstride + 1] = MathUtil.clip(patch[3], 0, 255);
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
                target[mapping2x2[curOff2]] = toValue(in.readNBit(len), len) * quantTable[curOff2];
                curOff = curOff2 + 1;
            }
            if (code == 0) {
                break;
            }
        } while (curOff < 5);
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
        return new Picture(res.getWidth() >> 2, res.getHeight() >> 2, res.getData(), res.getColor(), new Rect(0, 0, res.getCroppedWidth() >> 2, res.getCroppedHeight() >> 2));
    }
}
