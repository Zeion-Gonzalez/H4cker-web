package org.jcodec.codecs.vp8;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class BooleanArithmeticDecoder {
    int bit_count;
    long callCounter;
    private String debugName;
    ByteBuffer input;
    int offset;
    int range;
    int value;

    public BooleanArithmeticDecoder(ByteBuffer input, int offset, String debugName) {
        this(input, offset);
        this.debugName = debugName;
    }

    public BooleanArithmeticDecoder(ByteBuffer input, int offset) {
        this.callCounter = 0L;
        this.input = input;
        this.offset = offset;
        initBoolDecoder();
    }

    void initBoolDecoder() {
        this.value = 0;
        this.value = (this.input.get() & 255) << 8;
        this.offset++;
        this.range = 255;
        this.bit_count = 0;
    }

    public int decodeBit() {
        return decodeBool(128);
    }

    public int decodeBool(int probability) {
        int bit = 0;
        int range = this.range;
        int value = this.value;
        int split = (((range - 1) * probability) >> 8) + 1;
        int bigsplit = split << 8;
        this.callCounter++;
        int range2 = split;
        if (value >= bigsplit) {
            range2 = this.range - range2;
            value -= bigsplit;
            bit = 1;
        }
        int count = this.bit_count;
        int shift = leadingZeroCountInByte((byte) range2);
        int range3 = range2 << shift;
        int value2 = value << shift;
        int count2 = count - shift;
        if (count2 <= 0) {
            value2 |= (this.input.get() & 255) << (-count2);
            this.offset++;
            count2 += 8;
        }
        this.bit_count = count2;
        this.value = value2;
        this.range = range3;
        return bit;
    }

    public int decodeInt(int sizeInBits) {
        int v = 0;
        while (true) {
            int sizeInBits2 = sizeInBits;
            sizeInBits = sizeInBits2 - 1;
            if (sizeInBits2 > 0) {
                v = (v << 1) | decodeBool(128);
            } else {
                return v;
            }
        }
    }

    public int readTree(int[] tree, int[] probability) {
        int i = 0;
        do {
            i = tree[decodeBool(probability[i >> 1]) + i];
        } while (i > 0);
        return -i;
    }

    public int readTreeSkip(int[] t, int[] p, int skip_branches) {
        int i = skip_branches * 2;
        do {
            i = t[decodeBool(p[i >> 1]) + i];
        } while (i > 0);
        return -i;
    }

    public void seek() {
        this.input.position(this.offset);
    }

    public String toString() {
        return "bc: " + this.value;
    }

    public static int getBitInBytes(byte[] bs, int i) {
        int byteIndex = i >> 3;
        int bitIndex = i & 7;
        return (bs[byteIndex] >> (7 - bitIndex)) & 1;
    }

    public static int getBitsInBytes(byte[] bytes, int idx, int len) {
        int val = 0;
        for (int i = 0; i < len; i++) {
            val = (val << 1) | getBitInBytes(bytes, idx + i);
        }
        return val;
    }

    public static int leadingZeroCountInByte(byte b) {
        int i = b & 255;
        if (i >= 128 || i == 0) {
            return 0;
        }
        return Integer.numberOfLeadingZeros(b) - 24;
    }
}
