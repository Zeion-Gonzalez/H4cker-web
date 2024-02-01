package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Rational;

/* loaded from: classes.dex */
public class PixelAspectExt extends Box {
    private int hSpacing;
    private int vSpacing;

    public PixelAspectExt(Header header) {
        super(header);
    }

    public PixelAspectExt() {
        super(new Header(fourcc()));
    }

    public PixelAspectExt(Rational par) {
        this();
        this.hSpacing = par.getNum();
        this.vSpacing = par.getDen();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.hSpacing = input.getInt();
        this.vSpacing = input.getInt();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putInt(this.hSpacing);
        out.putInt(this.vSpacing);
    }

    public int gethSpacing() {
        return this.hSpacing;
    }

    public int getvSpacing() {
        return this.vSpacing;
    }

    public Rational getRational() {
        return new Rational(this.hSpacing, this.vSpacing);
    }

    public static String fourcc() {
        return "pasp";
    }
}
