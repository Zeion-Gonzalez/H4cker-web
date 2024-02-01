package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class DataInfoBox extends NodeBox {
    public static String fourcc() {
        return "dinf";
    }

    public DataInfoBox() {
        super(new Header(fourcc()));
    }

    public DataInfoBox(Header atom) {
        super(atom);
    }

    public DataRefBox getDref() {
        return (DataRefBox) findFirst(this, DataRefBox.class, "dref");
    }
}
