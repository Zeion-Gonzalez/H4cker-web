package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class MediaBox extends NodeBox {
    public static String fourcc() {
        return "mdia";
    }

    public MediaBox(Header atom) {
        super(atom);
    }

    public MediaBox() {
        super(new Header(fourcc()));
    }

    public MediaInfoBox getMinf() {
        return (MediaInfoBox) Box.findFirst(this, MediaInfoBox.class, "minf");
    }
}
