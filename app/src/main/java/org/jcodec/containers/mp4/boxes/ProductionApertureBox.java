package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class ProductionApertureBox extends ClearApertureBox {
    public static String fourcc() {
        return "prof";
    }

    public ProductionApertureBox(Header atom) {
        super(atom);
    }

    public ProductionApertureBox() {
        super(new Header(fourcc()));
    }

    public ProductionApertureBox(int width, int height) {
        super(new Header(fourcc()), width, height);
    }
}
