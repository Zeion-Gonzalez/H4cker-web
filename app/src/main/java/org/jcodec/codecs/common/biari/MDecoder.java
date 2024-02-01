package org.jcodec.codecs.common.biari;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MDecoder {

    /* renamed from: cm */
    private int[][] f1444cm;
    private int code;

    /* renamed from: in */
    private ByteBuffer f1445in;
    private int nBitsPending;
    private int range = 510;

    public MDecoder(ByteBuffer in, int[][] cm) {
        this.f1445in = in;
        this.f1444cm = cm;
        initCodeRegister();
    }

    protected void initCodeRegister() {
        readOneByte();
        if (this.nBitsPending != 8) {
            throw new RuntimeException("Empty stream");
        }
        this.code <<= 8;
        readOneByte();
        this.code <<= 1;
        this.nBitsPending -= 9;
    }

    protected void readOneByte() {
        if (this.f1445in.hasRemaining()) {
            int b = this.f1445in.get() & 255;
            this.code |= b;
            this.nBitsPending += 8;
        }
    }

    public int decodeBin(int m) {
        int qIdx = (this.range >> 6) & 3;
        int rLPS = MConst.rangeLPS[qIdx][this.f1444cm[0][m]];
        this.range -= rLPS;
        int rs8 = this.range << 8;
        if (this.code < rs8) {
            if (this.f1444cm[0][m] < 62) {
                int[] iArr = this.f1444cm[0];
                iArr[m] = iArr[m] + 1;
            }
            renormalize();
            int bin = this.f1444cm[1][m];
            return bin;
        }
        this.range = rLPS;
        this.code -= rs8;
        renormalize();
        int bin2 = 1 - this.f1444cm[1][m];
        if (this.f1444cm[0][m] == 0) {
            this.f1444cm[1][m] = 1 - this.f1444cm[1][m];
        }
        this.f1444cm[0][m] = MConst.transitLPS[this.f1444cm[0][m]];
        return bin2;
    }

    public int decodeFinalBin() {
        this.range -= 2;
        if (this.code >= (this.range << 8)) {
            return 1;
        }
        renormalize();
        return 0;
    }

    public int decodeBinBypass() {
        this.code <<= 1;
        this.nBitsPending--;
        if (this.nBitsPending <= 0) {
            readOneByte();
        }
        int tmp = this.code - (this.range << 8);
        if (tmp < 0) {
            return 0;
        }
        this.code = tmp;
        return 1;
    }

    private void renormalize() {
        while (this.range < 256) {
            this.range <<= 1;
            this.code <<= 1;
            this.code &= 131071;
            this.nBitsPending--;
            if (this.nBitsPending <= 0) {
                readOneByte();
            }
        }
    }
}
