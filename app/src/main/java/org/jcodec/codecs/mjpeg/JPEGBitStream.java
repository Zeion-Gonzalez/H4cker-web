package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;

/* loaded from: classes.dex */
public class JPEGBitStream {
    private int[] dcPredictor = new int[3];
    private VLC[] huff;

    /* renamed from: in */
    private BitReader f1464in;
    private int lumaLen;

    public JPEGBitStream(ByteBuffer b, VLC[] huff, int lumaLen) {
        this.f1464in = new BitReader(b);
        this.huff = huff;
        this.lumaLen = lumaLen;
    }

    public void readMCU(int[][] buf) {
        int blk = 0;
        int i = 0;
        while (i < this.lumaLen) {
            int[] iArr = this.dcPredictor;
            int[] iArr2 = buf[blk];
            int readDCValue = readDCValue(this.dcPredictor[0], this.huff[0]);
            iArr2[0] = readDCValue;
            iArr[0] = readDCValue;
            readACValues(buf[blk], this.huff[2]);
            i++;
            blk++;
        }
        int[] iArr3 = this.dcPredictor;
        int[] iArr4 = buf[blk];
        int readDCValue2 = readDCValue(this.dcPredictor[1], this.huff[1]);
        iArr4[0] = readDCValue2;
        iArr3[1] = readDCValue2;
        readACValues(buf[blk], this.huff[3]);
        int blk2 = blk + 1;
        int[] iArr5 = this.dcPredictor;
        int[] iArr6 = buf[blk2];
        int readDCValue3 = readDCValue(this.dcPredictor[2], this.huff[1]);
        iArr6[0] = readDCValue3;
        iArr5[2] = readDCValue3;
        readACValues(buf[blk2], this.huff[3]);
        int i2 = blk2 + 1;
    }

    public int readDCValue(int prevDC, VLC table) {
        int code = table.readVLC(this.f1464in);
        return code != 0 ? prevDC + toValue(this.f1464in.readNBit(code), code) : prevDC;
    }

    public void readACValues(int[] target, VLC table) {
        int curOff = 1;
        do {
            int code = table.readVLC(this.f1464in);
            if (code == 240) {
                curOff += 16;
            } else if (code > 0) {
                int rle = code >> 4;
                int curOff2 = curOff + rle;
                int len = code & 15;
                target[curOff2] = toValue(this.f1464in.readNBit(len), len);
                curOff = curOff2 + 1;
            }
            if (code == 0) {
                return;
            }
        } while (curOff < 64);
    }

    public final int toValue(int raw, int length) {
        return (length < 1 || raw >= (1 << (length + (-1)))) ? raw : raw + (-(1 << length)) + 1;
    }
}
