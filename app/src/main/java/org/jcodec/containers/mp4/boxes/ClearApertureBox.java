package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ClearApertureBox extends FullBox {
    private float height;
    private float width;

    public static String fourcc() {
        return "clef";
    }

    public ClearApertureBox(Header atom) {
        super(atom);
    }

    public ClearApertureBox() {
        super(new Header(fourcc()));
    }

    public ClearApertureBox(int width, int height) {
        this();
        this.width = width;
        this.height = height;
    }

    public ClearApertureBox(Header header, int width, int height) {
        super(header);
        this.width = width;
        this.height = height;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.width = input.getInt() / 65536.0f;
        this.height = input.getInt() / 65536.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt((int) (this.width * 65536.0f));
        out.putInt((int) (this.height * 65536.0f));
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}
