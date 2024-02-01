package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class EndianBox extends Box {
    private Endian endian;

    /* loaded from: classes.dex */
    public enum Endian {
        LITTLE_ENDIAN,
        BIG_ENDIAN
    }

    public EndianBox(Box other) {
        super(other);
    }

    public static String fourcc() {
        return "enda";
    }

    public EndianBox(Header header) {
        super(header);
    }

    public EndianBox(Endian endian) {
        super(new Header(fourcc()));
        this.endian = endian;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        long end = input.getShort();
        if (end == 1) {
            this.endian = Endian.LITTLE_ENDIAN;
        } else {
            this.endian = Endian.BIG_ENDIAN;
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putShort((short) (this.endian == Endian.LITTLE_ENDIAN ? 1 : 0));
    }

    public Endian getEndian() {
        return this.endian;
    }

    protected int calcSize() {
        return 2;
    }
}
