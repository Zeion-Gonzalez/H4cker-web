package org.jcodec.common.model;

/* loaded from: classes.dex */
public class Picture8Bit {

    /* renamed from: cb */
    private byte[] f1526cb;

    /* renamed from: cr */
    private byte[] f1527cr;
    private int height;
    private int width;

    /* renamed from: y */
    private byte[] f1528y;

    public Picture8Bit(int width, int height, byte[] y, byte[] cb, byte[] cr) {
        this.width = width;
        this.height = height;
        this.f1528y = y;
        this.f1526cb = cb;
        this.f1527cr = cr;
    }

    public static Picture8Bit create422(int width, int height) {
        return new Picture8Bit(width, height, new byte[width * height], new byte[(width * height) >> 1], new byte[(width * height) >> 1]);
    }

    public static Picture8Bit create420(int width, int height) {
        return new Picture8Bit(width, height, new byte[width * height], new byte[(width * height) >> 2], new byte[(width * height) >> 2]);
    }

    public Picture8Bit(Picture8Bit other) {
        this(other.width, other.height, other.f1528y, other.f1526cb, other.f1527cr);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getY() {
        return this.f1528y;
    }

    public byte[] getCb() {
        return this.f1526cb;
    }

    public byte[] getCr() {
        return this.f1527cr;
    }
}
