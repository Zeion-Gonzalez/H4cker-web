package org.jcodec.common.io;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class BitWriter {
    private final ByteBuffer buf;
    private int curBit;
    private int curInt;
    private int initPos;

    public BitWriter(ByteBuffer buf) {
        this.buf = buf;
        this.initPos = buf.position();
    }

    private BitWriter(ByteBuffer os, int curBit, int curInt, int initPos) {
        this.buf = os;
        this.curBit = curBit;
        this.curInt = curInt;
        this.initPos = initPos;
    }

    public void flush() {
        int toWrite = (this.curBit + 7) >> 3;
        for (int i = 0; i < toWrite; i++) {
            this.buf.put((byte) (this.curInt >>> 24));
            this.curInt <<= 8;
        }
    }

    private final void putInt(int i) {
        this.buf.put((byte) (i >>> 24));
        this.buf.put((byte) (i >> 16));
        this.buf.put((byte) (i >> 8));
        this.buf.put((byte) i);
    }

    public final void writeNBit(int value, int n) {
        if (n > 32) {
            throw new IllegalArgumentException("Max 32 bit to write");
        }
        if (n != 0) {
            int value2 = value & ((-1) >>> (32 - n));
            if (32 - this.curBit >= n) {
                this.curInt |= value2 << ((32 - this.curBit) - n);
                this.curBit += n;
                if (this.curBit == 32) {
                    putInt(this.curInt);
                    this.curBit = 0;
                    this.curInt = 0;
                    return;
                }
                return;
            }
            int secPart = n - (32 - this.curBit);
            this.curInt |= value2 >>> secPart;
            putInt(this.curInt);
            this.curInt = value2 << (32 - secPart);
            this.curBit = secPart;
        }
    }

    public void write1Bit(int bit) {
        this.curInt |= bit << ((32 - this.curBit) - 1);
        this.curBit++;
        if (this.curBit == 32) {
            putInt(this.curInt);
            this.curBit = 0;
            this.curInt = 0;
        }
    }

    public int curBit() {
        return this.curBit & 7;
    }

    public BitWriter fork() {
        return new BitWriter(this.buf.duplicate(), this.curBit, this.curInt, this.initPos);
    }

    public int position() {
        return ((this.buf.position() - this.initPos) << 3) + this.curBit;
    }

    public ByteBuffer getBuffer() {
        return this.buf;
    }
}
