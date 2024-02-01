package org.jcodec.containers.mkv.util;

import org.jcodec.containers.mxf.model.BER;

/* loaded from: classes.dex */
public class EbmlUtil {
    public static final long one = 127;
    public static final byte[] lengthOptions = {0, BER.ASN_LONG_LEN, 64, 32, 16, 8, 4, 2, 1};
    public static final long two = 16256;
    public static final long three = 2080768;
    public static final long four = 266338304;
    public static final long five = 34091302912L;
    public static final long six = 4363686772736L;
    public static final long seven = 558551906910208L;
    public static final long eight = 71494644084506624L;
    public static final long[] ebmlLengthMasks = {0, 127, two, three, four, five, six, seven, eight};

    public static byte[] ebmlEncode(long value, int length) {
        byte[] b = new byte[length];
        for (int idx = 0; idx < length; idx++) {
            b[(length - idx) - 1] = (byte) ((value >>> (idx * 8)) & 255);
        }
        b[0] = (byte) (b[0] | (128 >>> (length - 1)));
        return b;
    }

    public static byte[] ebmlEncode(long value) {
        return ebmlEncode(value, ebmlLength(value));
    }

    public static int computeLength(byte b) {
        if (b == 0) {
            throw new RuntimeException("Invalid head element for ebml sequence");
        }
        int i = 1;
        while ((lengthOptions[i] & b) == 0) {
            i++;
        }
        return i;
    }

    public static int ebmlLength(long v) {
        if (v == 0) {
            return 1;
        }
        int length = 8;
        while (length > 0 && (ebmlLengthMasks[length] & v) == 0) {
            length--;
        }
        return length;
    }

    public static String toHexString(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (byte b : a) {
            sb.append(String.format("0x%02x ", Integer.valueOf(b & 255)));
        }
        return sb.toString();
    }
}
