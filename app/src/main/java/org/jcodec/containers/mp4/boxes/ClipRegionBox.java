package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ClipRegionBox extends Box {
    private short height;
    private short rgnSize;
    private short width;

    /* renamed from: x */
    private short f1543x;

    /* renamed from: y */
    private short f1544y;

    public static String fourcc() {
        return "crgn";
    }

    public ClipRegionBox(Header atom) {
        super(atom);
    }

    public ClipRegionBox() {
        super(new Header(fourcc()));
    }

    public ClipRegionBox(short x, short y, short width, short height) {
        this();
        this.rgnSize = (short) 10;
        this.f1543x = x;
        this.f1544y = y;
        this.width = width;
        this.height = height;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.rgnSize = input.getShort();
        this.f1544y = input.getShort();
        this.f1543x = input.getShort();
        this.height = input.getShort();
        this.width = input.getShort();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putShort(this.rgnSize);
        out.putShort(this.f1544y);
        out.putShort(this.f1543x);
        out.putShort(this.height);
        out.putShort(this.width);
    }

    public short getRgnSize() {
        return this.rgnSize;
    }

    public short getY() {
        return this.f1544y;
    }

    public short getX() {
        return this.f1543x;
    }

    public short getHeight() {
        return this.height;
    }

    public short getWidth() {
        return this.width;
    }
}
