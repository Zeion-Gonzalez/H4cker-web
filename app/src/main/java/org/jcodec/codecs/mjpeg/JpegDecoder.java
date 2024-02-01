package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.codecs.mjpeg.tools.Asserts;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.dct.SimpleIDCT10Bit;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;
import org.jcodec.common.io.VLCBuilder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class JpegDecoder implements VideoDecoder {
    int[] buf;
    private boolean interlace;
    private boolean topFieldFirst;

    public JpegDecoder() {
        this(false, false);
    }

    public JpegDecoder(boolean interlace, boolean topFieldFirst) {
        this.buf = new int[64];
        this.interlace = interlace;
        this.topFieldFirst = topFieldFirst;
    }

    private Picture decodeScan(ByteBuffer data, FrameHeader header, ScanHeader scan, VLC[] huffTables, int[][] quant, int[][] data2, int field, int step) {
        ColorSpace colorSpace;
        int blockW = header.getHmax();
        int blockH = header.getVmax();
        int mcuW = blockW << 3;
        int mcuH = blockH << 3;
        int width = header.width;
        int height = header.height;
        int xBlocks = ((width + mcuW) - 1) >> (blockW + 2);
        int yBlocks = ((height + mcuH) - 1) >> (blockH + 2);
        int nn = blockW + blockH;
        int i = xBlocks << (blockW + 2);
        int i2 = yBlocks << (blockH + 2);
        if (nn == 4) {
            colorSpace = ColorSpace.YUV420J;
        } else {
            colorSpace = nn == 3 ? ColorSpace.YUV422J : ColorSpace.YUV444J;
        }
        Picture result = new Picture(i, i2, data2, colorSpace, new Rect(0, 0, width, height));
        BitReader bits = new BitReader(data);
        int[] dcPredictor = {1024, 1024, 1024};
        for (int by = 0; by < yBlocks; by++) {
            for (int bx = 0; bx < xBlocks && bits.moreData(); bx++) {
                decodeMCU(bits, dcPredictor, quant, huffTables, result, bx, by, blockW, blockH, field, step);
            }
        }
        return result;
    }

    void putBlock(int[] plane, int stride, int[] patch, int x, int y, int field, int step) {
        int dstride = step * stride;
        int off = (field * stride) + (y * dstride) + x;
        int poff = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                plane[j + off] = MathUtil.clip(patch[j + poff], 0, 255);
            }
            off += dstride;
            poff += 8;
        }
    }

    void decodeMCU(BitReader bits, int[] dcPredictor, int[][] quant, VLC[] huff, Picture result, int bx, int by, int blockH, int blockV, int field, int step) {
        int sx = bx << (blockH - 1);
        int sy = by << (blockV - 1);
        for (int i = 0; i < blockV; i++) {
            for (int j = 0; j < blockH; j++) {
                decodeBlock(bits, dcPredictor, quant, huff, result, this.buf, (sx + j) << 3, (sy + i) << 3, 0, 0, field, step);
            }
        }
        decodeBlock(bits, dcPredictor, quant, huff, result, this.buf, bx << 3, by << 3, 1, 1, field, step);
        decodeBlock(bits, dcPredictor, quant, huff, result, this.buf, bx << 3, by << 3, 2, 1, field, step);
    }

    void decodeBlock(BitReader bits, int[] dcPredictor, int[][] quant, VLC[] huff, Picture result, int[] buf, int blkX, int blkY, int plane, int chroma, int field, int step) {
        Arrays.fill(buf, 0);
        int readDCValue = (readDCValue(bits, huff[chroma]) * quant[chroma][0]) + dcPredictor[plane];
        buf[0] = readDCValue;
        dcPredictor[plane] = readDCValue;
        readACValues(bits, buf, huff[chroma + 2], quant[chroma]);
        SimpleIDCT10Bit.idct10(buf, 0);
        putBlock(result.getPlaneData(plane), result.getPlaneWidth(plane), buf, blkX, blkY, field, step);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int readDCValue(BitReader in, VLC table) {
        int code = table.readVLC16(in);
        if (code != 0) {
            return toValue(in.readNBit(code), code);
        }
        return 0;
    }

    void readACValues(BitReader in, int[] target, VLC table, int[] quantTable) {
        int curOff = 1;
        do {
            int code = table.readVLC16(in);
            if (code == 240) {
                curOff += 16;
            } else if (code > 0) {
                int rle = code >> 4;
                int curOff2 = curOff + rle;
                int len = code & 15;
                target[JpegConst.naturalOrder[curOff2]] = toValue(in.readNBit(len), len) * quantTable[curOff2];
                curOff = curOff2 + 1;
            }
            if (code == 0) {
                return;
            }
        } while (curOff < 64);
    }

    public static final int toValue(int raw, int length) {
        return (length < 1 || raw >= (1 << (length + (-1)))) ? raw : raw + (-(1 << length)) + 1;
    }

    @Override // org.jcodec.common.VideoDecoder
    public Picture decodeFrame(ByteBuffer data, int[][] data2) {
        if (this.interlace) {
            Picture r1 = decodeField(data, data2, this.topFieldFirst ? 0 : 1, 2);
            decodeField(data, data2, this.topFieldFirst ? 1 : 0, 2);
            return new Picture(r1.getWidth(), r1.getHeight() << 1, data2, r1.getColor());
        }
        return decodeField(data, data2, 0, 1);
    }

    public Picture decodeField(ByteBuffer data, int[][] data2, int field, int step) {
        int b;
        Picture result = null;
        FrameHeader header = null;
        VLC[] huffTables = new VLC[4];
        huffTables[0] = JpegConst.YDC_DEFAULT;
        huffTables[1] = JpegConst.CDC_DEFAULT;
        huffTables[2] = JpegConst.YAC_DEFAULT;
        huffTables[3] = JpegConst.CAC_DEFAULT;
        int[][] quant = new int[4];
        ScanHeader scan = null;
        while (data.hasRemaining()) {
            int marker = data.get() & 255;
            if (marker != 0) {
                if (marker != 255) {
                    throw new RuntimeException("@" + Long.toHexString(data.position()) + " Marker expected: 0x" + Integer.toHexString(marker));
                }
                do {
                    b = data.get() & 255;
                } while (b == 255);
                if (b == 192) {
                    header = FrameHeader.read(data);
                } else if (b == 196) {
                    int len1 = data.getShort() & 65535;
                    ByteBuffer buf = NIOUtils.read(data, len1 - 2);
                    while (buf.hasRemaining()) {
                        int tableNo = buf.get() & 255;
                        huffTables[(tableNo & 1) | ((tableNo >> 3) & 2)] = readHuffmanTable(buf);
                    }
                } else if (b == 219) {
                    int len4 = data.getShort() & 65535;
                    ByteBuffer buf2 = NIOUtils.read(data, len4 - 2);
                    while (buf2.hasRemaining()) {
                        int ind = buf2.get() & 255;
                        quant[ind] = readQuantTable(buf2);
                    }
                } else if (b == 218) {
                    if (scan != null) {
                        throw new IllegalStateException("unhandled - more than one scan header");
                    }
                    scan = ScanHeader.read(data);
                    result = decodeScan(readToMarker(data), header, scan, huffTables, quant, data2, field, step);
                } else if (b != 216 && (b < 208 || b > 215)) {
                    if (b == 217) {
                        break;
                    }
                    if (b >= 224 && b <= 254) {
                        int len3 = data.getShort() & 65535;
                        NIOUtils.read(data, len3 - 2);
                    } else if (b == 221) {
                        int i = data.getShort() & 65535;
                        int ri = data.getShort() & 65535;
                        Asserts.assertEquals(0, ri);
                    } else {
                        throw new IllegalStateException("unhandled marker " + JpegConst.toString(b));
                    }
                }
            }
        }
        return result;
    }

    private static ByteBuffer readToMarker(ByteBuffer data) {
        ByteBuffer out = ByteBuffer.allocate(data.remaining());
        while (true) {
            if (!data.hasRemaining()) {
                break;
            }
            byte b0 = data.get();
            if (b0 == -1) {
                byte b1 = data.get();
                if (b1 == 0) {
                    out.put((byte) -1);
                } else {
                    data.position(data.position() - 2);
                    break;
                }
            } else {
                out.put(b0);
            }
        }
        out.flip();
        return out;
    }

    private static VLC readHuffmanTable(ByteBuffer data) {
        VLCBuilder builder = new VLCBuilder();
        byte[] levelSizes = NIOUtils.toArray(NIOUtils.read(data, 16));
        int levelStart = 0;
        for (int i = 0; i < 16; i++) {
            int length = levelSizes[i] & 255;
            int c = 0;
            int levelStart2 = levelStart;
            while (c < length) {
                int val = data.get() & 255;
                int code = levelStart2;
                builder.set(code, i + 1, val);
                c++;
                levelStart2++;
            }
            levelStart = levelStart2 << 1;
        }
        return builder.getVLC();
    }

    private static int[] readQuantTable(ByteBuffer data) {
        int[] result = new int[64];
        for (int i = 0; i < 64; i++) {
            result[i] = data.get() & 255;
        }
        return result;
    }

    @Override // org.jcodec.common.VideoDecoder
    public int probe(ByteBuffer data) {
        return 0;
    }
}
