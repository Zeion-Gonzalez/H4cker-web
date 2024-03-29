package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class EncodedPixelBox extends ClearApertureBox {
    public static String fourcc() {
        return "enof";
    }

    public EncodedPixelBox(Header atom) {
        super(atom);
    }

    public EncodedPixelBox() {
        super(new Header(fourcc()));
    }

    public EncodedPixelBox(int width, int height) {
        super(new Header(fourcc()), width, height);
    }
}
