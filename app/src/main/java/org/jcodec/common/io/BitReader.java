package org.jcodec.common.io;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class BitReader {

    /* renamed from: bb */
    private ByteBuffer f1524bb;
    protected int curInt;
    protected int deficit;
    private int initPos;

    public BitReader(ByteBuffer bb) {
        this.f1524bb = bb;
        this.initPos = bb.position();
        this.curInt = readInt();
        this.deficit = 0;
    }

    private BitReader(BitReader other) {
        this.f1524bb = other.f1524bb.duplicate();
        this.curInt = other.curInt;
        this.deficit = other.deficit;
    }

    public final int readInt() {
        if (this.f1524bb.remaining() < 4) {
            return readIntSafe();
        }
        this.deficit -= 32;
        return ((this.f1524bb.get() & 255) << 24) | ((this.f1524bb.get() & 255) << 16) | ((this.f1524bb.get() & 255) << 8) | (this.f1524bb.get() & 255);
    }

    private int readIntSafe() {
        this.deficit -= this.f1524bb.remaining() << 3;
        int res = (this.f1524bb.hasRemaining() ? 0 | (this.f1524bb.get() & 255) : 0) << 8;
        if (this.f1524bb.hasRemaining()) {
            res |= this.f1524bb.get() & 255;
        }
        int res2 = res << 8;
        if (this.f1524bb.hasRemaining()) {
            res2 |= this.f1524bb.get() & 255;
        }
        int res3 = res2 << 8;
        if (this.f1524bb.hasRemaining()) {
            return res3 | (this.f1524bb.get() & 255);
        }
        return res3;
    }

    public int read1Bit() {
        int ret = this.curInt >>> 31;
        this.curInt <<= 1;
        this.deficit++;
        if (this.deficit == 32) {
            this.curInt = readInt();
        }
        return ret;
    }

    public int readNBit(int n) {
        if (n > 32) {
            throw new IllegalArgumentException("Can not read more then 32 bit");
        }
        int ret = 0;
        if (this.deficit + n > 31) {
            int ret2 = 0 | (this.curInt >>> this.deficit);
            n -= 32 - this.deficit;
            ret = ret2 << n;
            this.deficit = 32;
            this.curInt = readInt();
        }
        if (n != 0) {
            int ret3 = ret | (this.curInt >>> (32 - n));
            this.curInt <<= n;
            this.deficit += n;
            return ret3;
        }
        return ret;
    }

    public boolean moreData() {
        int remaining = (this.f1524bb.remaining() + 4) - ((this.deficit + 7) >> 3);
        if (remaining <= 1) {
            return remaining == 1 && this.curInt != 0;
        }
        return true;
    }

    public int remaining() {
        return ((this.f1524bb.remaining() << 3) + 32) - this.deficit;
    }

    public final boolean isByteAligned() {
        return (this.deficit & 7) == 0;
    }

    public int skip(int bits) {
        int left = bits;
        if (this.deficit + left > 31) {
            left -= 32 - this.deficit;
            this.deficit = 32;
            if (left > 31) {
                int skip = Math.min(left >> 3, this.f1524bb.remaining());
                this.f1524bb.position(this.f1524bb.position() + skip);
                left -= skip << 3;
            }
            this.curInt = readInt();
        }
        this.deficit += left;
        this.curInt <<= left;
        return bits;
    }

    public int skipFast(int bits) {
        this.deficit += bits;
        this.curInt <<= bits;
        return bits;
    }

    public int align() {
        if ((this.deficit & 7) > 0) {
            return skip(8 - (this.deficit & 7));
        }
        return 0;
    }

    public int check24Bits() {
        if (this.deficit > 16) {
            this.deficit -= 16;
            this.curInt |= nextIgnore16() << this.deficit;
        }
        if (this.deficit > 8) {
            this.deficit -= 8;
            this.curInt |= nextIgnore() << this.deficit;
        }
        return this.curInt >>> 8;
    }

    public int check16Bits() {
        if (this.deficit > 16) {
            this.deficit -= 16;
            this.curInt |= nextIgnore16() << this.deficit;
        }
        return this.curInt >>> 16;
    }

    public int readFast16(int n) {
        if (n == 0) {
            return 0;
        }
        if (this.deficit > 16) {
            this.deficit -= 16;
            this.curInt |= nextIgnore16() << this.deficit;
        }
        int i = this.curInt >>> (32 - n);
        this.deficit += n;
        this.curInt <<= n;
        return i;
    }

    public int checkNBit(int n) {
        if (n > 24) {
            throw new IllegalArgumentException("Can not check more then 24 bit");
        }
        while (this.deficit + n > 32) {
            this.deficit -= 8;
            this.curInt |= nextIgnore() << this.deficit;
        }
        int res = this.curInt >>> (32 - n);
        return res;
    }

    private int nextIgnore16() {
        if (this.f1524bb.remaining() > 1) {
            return this.f1524bb.getShort() & 65535;
        }
        if (this.f1524bb.hasRemaining()) {
            return (this.f1524bb.get() & 255) << 8;
        }
        return 0;
    }

    private int nextIgnore() {
        if (this.f1524bb.hasRemaining()) {
            return this.f1524bb.get() & 255;
        }
        return 0;
    }

    public int curBit() {
        return this.deficit & 7;
    }

    public boolean lastByte() {
        return (this.f1524bb.remaining() + 4) - (this.deficit >> 3) <= 1;
    }

    public BitReader fork() {
        return new BitReader(this);
    }

    public void terminate() {
        int putBack = (32 - this.deficit) >> 3;
        this.f1524bb.position(this.f1524bb.position() - putBack);
    }

    public int position() {
        return (((this.f1524bb.position() - this.initPos) - 4) << 3) + this.deficit;
    }

    public void stop() {
        this.f1524bb.position(this.f1524bb.position() - ((32 - this.deficit) >> 3));
    }

    public int checkAllBits() {
        return this.curInt;
    }
}
