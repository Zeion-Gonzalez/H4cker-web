package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class MediaInfoBox extends NodeBox {
    public static String fourcc() {
        return "minf";
    }

    public MediaInfoBox(Header atom) {
        super(atom);
    }

    public MediaInfoBox() {
        super(new Header(fourcc()));
    }

    public DataInfoBox getDinf() {
        return (DataInfoBox) findFirst(this, DataInfoBox.class, "dinf");
    }

    public NodeBox getStbl() {
        return (NodeBox) findFirst(this, NodeBox.class, "stbl");
    }
}
