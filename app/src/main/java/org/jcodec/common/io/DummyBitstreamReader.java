package org.jcodec.common.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class DummyBitstreamReader {
    protected static int bitsRead;
    int cnt = 0;
    private int curByte;

    /* renamed from: is */
    private InputStream f1525is;
    int nBit;
    private int nextByte;
    private int secondByte;

    public DummyBitstreamReader(InputStream is) throws IOException {
        this.f1525is = is;
        this.curByte = is.read();
        this.nextByte = is.read();
        this.secondByte = is.read();
    }

    public int read1Bit() throws IOException {
        return read1BitInt();
    }

    public int read1BitInt() throws IOException {
        if (this.nBit == 8) {
            advance();
            if (this.curByte == -1) {
                return -1;
            }
        }
        int i = (this.curByte >> (7 - this.nBit)) & 1;
        this.nBit++;
        bitsRead++;
        return i;
    }

    public int readNBit(int n) throws IOException {
        if (n > 32) {
            throw new IllegalArgumentException("Can not read more then 32 bit");
        }
        int val = 0;
        for (int i = 0; i < n; i++) {
            val = (val << 1) | read1BitInt();
        }
        return val;
    }

    private final void advance1() throws IOException {
        this.curByte = this.nextByte;
        this.nextByte = this.secondByte;
        this.secondByte = this.f1525is.read();
    }

    private final void advance() throws IOException {
        advance1();
        this.nBit = 0;
    }

    public int readByte() throws IOException {
        if (this.nBit > 0) {
            advance();
        }
        int res = this.curByte;
        advance();
        return res;
    }

    public boolean moreRBSPData() throws IOException {
        if (this.nBit == 8) {
            advance();
        }
        int tail = 1 << ((8 - this.nBit) - 1);
        int mask = (tail << 1) - 1;
        boolean hasTail = (this.curByte & mask) == tail;
        return (this.curByte == -1 || (this.nextByte == -1 && hasTail)) ? false : true;
    }

    public long getBitPosition() {
        return (bitsRead * 8) + (this.nBit % 8);
    }

    public boolean moreData() throws IOException {
        if (this.nBit == 8) {
            advance();
        }
        if (this.curByte == -1) {
            return false;
        }
        if (this.nextByte != -1 && (this.nextByte != 0 || this.secondByte != -1)) {
            return true;
        }
        int mask = (1 << (8 - this.nBit)) - 1;
        return (this.curByte & mask) != 0;
    }

    public long readRemainingByte() throws IOException {
        return readNBit(8 - this.nBit);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0016, code lost:
    
        if (r8.curByte == (-1)) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int peakNextBits(int r9) throws java.io.IOException {
        /*
            r8 = this;
            r4 = -1
            r7 = 8
            if (r9 <= r7) goto Ld
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "N should be less then 8"
            r5.<init>(r6)
            throw r5
        Ld:
            int r5 = r8.nBit
            if (r5 != r7) goto L19
            r8.advance()
            int r5 = r8.curByte
            if (r5 != r4) goto L19
        L18:
            return r4
        L19:
            int r5 = r8.nBit
            int r5 = 16 - r5
            int[] r0 = new int[r5]
            r1 = 0
            int r3 = r8.nBit
            r2 = r1
        L23:
            if (r3 >= r7) goto L34
            int r1 = r2 + 1
            int r5 = r8.curByte
            int r6 = 7 - r3
            int r5 = r5 >> r6
            r5 = r5 & 1
            r0[r2] = r5
            int r3 = r3 + 1
            r2 = r1
            goto L23
        L34:
            r3 = 0
        L35:
            if (r3 >= r7) goto L46
            int r1 = r2 + 1
            int r5 = r8.nextByte
            int r6 = 7 - r3
            int r5 = r5 >> r6
            r5 = r5 & 1
            r0[r2] = r5
            int r3 = r3 + 1
            r2 = r1
            goto L35
        L46:
            r4 = 0
            r3 = 0
        L48:
            if (r3 >= r9) goto L18
            int r4 = r4 << 1
            r5 = r0[r3]
            r4 = r4 | r5
            int r3 = r3 + 1
            goto L48
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.common.io.DummyBitstreamReader.peakNextBits(int):int");
    }

    public boolean isByteAligned() {
        return this.nBit % 8 == 0;
    }

    public void close() throws IOException {
        this.f1525is.close();
    }

    public int getCurBit() {
        return this.nBit;
    }

    public boolean moreData(int bits) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final int skip(int bits) throws IOException {
        this.nBit += bits;
        int was = this.nBit;
        while (this.nBit >= 8 && this.curByte != -1) {
            advance1();
            this.nBit -= 8;
        }
        return was - this.nBit;
    }

    public int align() throws IOException {
        int n = (8 - this.nBit) & 7;
        skip((8 - this.nBit) & 7);
        return n;
    }

    public int checkNBit(int n) throws IOException {
        return peakNextBits(n);
    }

    public int curBit() {
        return this.nBit;
    }

    public boolean lastByte() throws IOException {
        return this.nextByte == -1 && this.secondByte == -1;
    }
}
