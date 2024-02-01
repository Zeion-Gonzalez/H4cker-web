package org.jcodec.codecs.common.biari;

import android.support.v4.app.FrameMetricsAggregator;
import java.nio.ByteBuffer;
import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public class MEncoder {
    private int bitsInOutReg;
    private int[][] models;
    private int offset;
    private int onesOutstanding;
    private ByteBuffer out;
    private int outReg;
    private int range = 510;
    private boolean zeroBorrowed;

    public MEncoder(ByteBuffer out, int[][] models) {
        this.models = models;
        this.out = out;
    }

    public void encodeBin(int model, int bin) {
        int qs = (this.range >> 6) & 3;
        int rangeLPS = MConst.rangeLPS[qs][this.models[0][model]];
        this.range -= rangeLPS;
        if (bin != this.models[1][model]) {
            this.offset += this.range;
            this.range = rangeLPS;
            if (this.models[0][model] == 0) {
                this.models[1][model] = 1 - this.models[1][model];
            }
            this.models[0][model] = MConst.transitLPS[this.models[0][model]];
        } else if (this.models[0][model] < 62) {
            int[] iArr = this.models[0];
            iArr[model] = iArr[model] + 1;
        }
        renormalize();
    }

    public void encodeBinBypass(int bin) {
        this.offset <<= 1;
        if (bin == 1) {
            this.offset += this.range;
        }
        if ((this.offset & 1024) != 0) {
            flushOutstanding(1);
            this.offset &= 1023;
        } else if ((this.offset & 512) != 0) {
            this.offset &= FrameMetricsAggregator.EVERY_DURATION;
            this.onesOutstanding++;
        } else {
            flushOutstanding(0);
        }
    }

    public void encodeBinFinal(int bin) {
        this.range -= 2;
        if (bin == 0) {
            renormalize();
            return;
        }
        this.offset += this.range;
        this.range = 2;
        renormalize();
    }

    public void finishEncoding() {
        flushOutstanding((this.offset >> 9) & 1);
        putBit((this.offset >> 8) & 1);
        stuffBits();
    }

    private void renormalize() {
        while (this.range < 256) {
            if (this.offset < 256) {
                flushOutstanding(0);
            } else if (this.offset < 512) {
                this.offset &= 255;
                this.onesOutstanding++;
            } else {
                this.offset &= FrameMetricsAggregator.EVERY_DURATION;
                flushOutstanding(1);
            }
            this.range <<= 1;
            this.offset <<= 1;
        }
    }

    private void flushOutstanding(int hasCarry) {
        if (this.zeroBorrowed) {
            putBit(hasCarry);
        }
        int trailingBit = 1 - hasCarry;
        while (this.onesOutstanding > 0) {
            putBit(trailingBit);
            this.onesOutstanding--;
        }
        this.zeroBorrowed = true;
    }

    private void putBit(int bit) {
        this.outReg = (this.outReg << 1) | bit;
        this.bitsInOutReg++;
        if (this.bitsInOutReg == 8) {
            this.out.put((byte) this.outReg);
            this.outReg = 0;
            this.bitsInOutReg = 0;
        }
    }

    private void stuffBits() {
        if (this.bitsInOutReg == 0) {
            this.out.put(BER.ASN_LONG_LEN);
            return;
        }
        this.outReg = (this.outReg << 1) | 1;
        this.outReg <<= 8 - (this.bitsInOutReg + 1);
        this.out.put((byte) this.outReg);
        this.outReg = 0;
        this.bitsInOutReg = 0;
    }
}
