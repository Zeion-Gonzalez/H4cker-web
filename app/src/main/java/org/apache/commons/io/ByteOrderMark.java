package org.apache.commons.io;

import java.io.Serializable;
import org.jcodec.codecs.mjpeg.JpegConst;

/* loaded from: classes.dex */
public class ByteOrderMark implements Serializable {
    private static final long serialVersionUID = 1;
    private final int[] bytes;
    private final String charsetName;
    public static final ByteOrderMark UTF_8 = new ByteOrderMark("UTF-8", JpegConst.APPF, 187, 191);
    public static final ByteOrderMark UTF_16BE = new ByteOrderMark("UTF-16BE", JpegConst.COM, 255);
    public static final ByteOrderMark UTF_16LE = new ByteOrderMark("UTF-16LE", 255, JpegConst.COM);
    public static final ByteOrderMark UTF_32BE = new ByteOrderMark("UTF-32BE", 0, 0, JpegConst.COM, 255);
    public static final ByteOrderMark UTF_32LE = new ByteOrderMark("UTF-32LE", 255, JpegConst.COM, 0, 0);

    public ByteOrderMark(String charsetName, int... bytes) {
        if (charsetName == null || charsetName.length() == 0) {
            throw new IllegalArgumentException("No charsetName specified");
        }
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("No bytes specified");
        }
        this.charsetName = charsetName;
        this.bytes = new int[bytes.length];
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }

    public String getCharsetName() {
        return this.charsetName;
    }

    public int length() {
        return this.bytes.length;
    }

    public int get(int pos) {
        return this.bytes[pos];
    }

    public byte[] getBytes() {
        byte[] copy = new byte[this.bytes.length];
        for (int i = 0; i < this.bytes.length; i++) {
            copy[i] = (byte) this.bytes[i];
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ByteOrderMark)) {
            return false;
        }
        ByteOrderMark bom = (ByteOrderMark) obj;
        if (this.bytes.length != bom.length()) {
            return false;
        }
        for (int i = 0; i < this.bytes.length; i++) {
            if (this.bytes[i] != bom.get(i)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = getClass().hashCode();
        int[] arr$ = this.bytes;
        for (int b : arr$) {
            hashCode += b;
        }
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append('[');
        builder.append(this.charsetName);
        builder.append(": ");
        for (int i = 0; i < this.bytes.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("0x");
            builder.append(Integer.toHexString(this.bytes[i] & 255).toUpperCase());
        }
        builder.append(']');
        return builder.toString();
    }
}
