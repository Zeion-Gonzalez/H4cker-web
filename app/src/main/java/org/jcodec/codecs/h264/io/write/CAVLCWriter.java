package org.jcodec.codecs.h264.io.write;

import org.jcodec.common.io.BitWriter;
import org.jcodec.common.tools.Debug;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class CAVLCWriter {
    private CAVLCWriter() {
    }

    public static void writeU(BitWriter out, int value, int n, String message) {
        out.writeNBit(value, n);
        Debug.trace(message, Integer.valueOf(value));
    }

    public static void writeUE(BitWriter out, int value) {
        int bits = 0;
        int cumul = 0;
        int i = 0;
        while (true) {
            if (i >= 15) {
                break;
            }
            if (value < (1 << i) + cumul) {
                bits = i;
                break;
            } else {
                cumul += 1 << i;
                i++;
            }
        }
        out.writeNBit(0, bits);
        out.write1Bit(1);
        out.writeNBit(value - cumul, bits);
    }

    public static void writeSE(BitWriter out, int value) {
        writeUE(out, MathUtil.golomb(value));
    }

    public static void writeUE(BitWriter out, int value, String message) {
        writeUE(out, value);
        Debug.trace(message, Integer.valueOf(value));
    }

    public static void writeSE(BitWriter out, int value, String message) {
        writeUE(out, MathUtil.golomb(value));
        Debug.trace(message, Integer.valueOf(value));
    }

    public static void writeBool(BitWriter out, boolean value, String message) {
        out.write1Bit(value ? 1 : 0);
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(value ? 1 : 0);
        Debug.trace(message, objArr);
    }

    public static void writeU(BitWriter out, int i, int n) {
        out.writeNBit(i, n);
    }

    public static void writeNBit(BitWriter out, long value, int n, String message) {
        for (int i = 0; i < n; i++) {
            out.write1Bit(((int) (value >> ((n - i) - 1))) & 1);
        }
        Debug.trace(message, Long.valueOf(value));
    }

    public static void writeTrailingBits(BitWriter out) {
        out.write1Bit(1);
        out.flush();
    }

    public static void writeSliceTrailingBits() {
        throw new IllegalStateException("todo");
    }
}
