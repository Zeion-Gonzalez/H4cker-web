package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class MovieExtendsBox extends NodeBox {
    public static String fourcc() {
        return "mvex";
    }

    public MovieExtendsBox() {
        super(new Header(fourcc()));
    }
}
