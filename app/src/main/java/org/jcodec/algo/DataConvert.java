package org.jcodec.algo;

/* loaded from: classes.dex */
public class DataConvert {
    public static int[] from16BE(byte[] b) {
        int[] result = new int[b.length >> 1];
        int off = 0;
        for (int i = 0; i < result.length; i++) {
            int off2 = off + 1;
            int i2 = (b[off] & 255) << 8;
            off = off2 + 1;
            result[i] = i2 | (b[off2] & 255);
        }
        return result;
    }

    public static int[] from24BE(byte[] b) {
        int[] result = new int[b.length / 3];
        int off = 0;
        int i = 0;
        while (i < result.length) {
            int off2 = off + 1;
            int i2 = (b[off] & 255) << 16;
            int off3 = off2 + 1;
            result[i] = i2 | ((b[off2] & 255) << 8) | (b[off3] & 255);
            i++;
            off = off3 + 1;
        }
        return result;
    }

    public static int[] from16LE(byte[] b) {
        int[] result = new int[b.length >> 1];
        int off = 0;
        for (int i = 0; i < result.length; i++) {
            int off2 = off + 1;
            int i2 = b[off] & 255;
            off = off2 + 1;
            result[i] = i2 | ((b[off2] & 255) << 8);
        }
        return result;
    }

    public static int[] from24LE(byte[] b) {
        int[] result = new int[b.length / 3];
        int off = 0;
        int i = 0;
        while (i < result.length) {
            int off2 = off + 1;
            int i2 = b[off] & 255;
            int off3 = off2 + 1;
            result[i] = i2 | ((b[off2] & 255) << 8) | ((b[off3] & 255) << 16);
            i++;
            off = off3 + 1;
        }
        return result;
    }

    public static byte[] to16BE(int[] ia) {
        byte[] result = new byte[ia.length << 1];
        int off = 0;
        for (int i = 0; i < ia.length; i++) {
            int off2 = off + 1;
            result[off] = (byte) ((ia[i] >> 8) & 255);
            off = off2 + 1;
            result[off2] = (byte) (ia[i] & 255);
        }
        return result;
    }

    public static byte[] to24BE(int[] ia) {
        byte[] result = new byte[ia.length * 3];
        int off = 0;
        int i = 0;
        while (i < ia.length) {
            int off2 = off + 1;
            result[off] = (byte) ((ia[i] >> 16) & 255);
            int off3 = off2 + 1;
            result[off2] = (byte) ((ia[i] >> 8) & 255);
            result[off3] = (byte) (ia[i] & 255);
            i++;
            off = off3 + 1;
        }
        return result;
    }

    public static byte[] to16LE(int[] ia) {
        byte[] result = new byte[ia.length << 1];
        int off = 0;
        for (int i = 0; i < ia.length; i++) {
            int off2 = off + 1;
            result[off] = (byte) (ia[i] & 255);
            off = off2 + 1;
            result[off2] = (byte) ((ia[i] >> 8) & 255);
        }
        return result;
    }

    public static byte[] to24LE(int[] ia) {
        byte[] result = new byte[ia.length * 3];
        int off = 0;
        int i = 0;
        while (i < ia.length) {
            int off2 = off + 1;
            result[off] = (byte) (ia[i] & 255);
            int off3 = off2 + 1;
            result[off2] = (byte) ((ia[i] >> 8) & 255);
            result[off3] = (byte) ((ia[i] >> 16) & 255);
            i++;
            off = off3 + 1;
        }
        return result;
    }

    public static int[] fromByte(byte[] b, int depth, boolean isBe) {
        if (depth == 24) {
            if (isBe) {
                return from24BE(b);
            }
            return from24LE(b);
        }
        if (depth == 16) {
            if (isBe) {
                return from16BE(b);
            }
            return from16LE(b);
        }
        throw new IllegalArgumentException("Conversion from " + depth + "bit " + (isBe ? "big endian" : "little endian") + " is not supported.");
    }

    public static byte[] toByte(int[] ia, int depth, boolean isBe) {
        if (depth == 24) {
            if (isBe) {
                return to24BE(ia);
            }
            return to24LE(ia);
        }
        if (depth == 16) {
            if (isBe) {
                return to16BE(ia);
            }
            return to16LE(ia);
        }
        throw new IllegalArgumentException("Conversion to " + depth + "bit " + (isBe ? "big endian" : "little endian") + " is not supported.");
    }
}
