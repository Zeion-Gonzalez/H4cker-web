package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FielExtension extends Box {
    private int order;
    private int type;

    public FielExtension(byte type, byte order) {
        super(new Header(fourcc()));
        this.type = type;
        this.order = order;
    }

    public FielExtension() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "fiel";
    }

    public boolean isInterlaced() {
        return this.type == 2;
    }

    public boolean topFieldFirst() {
        return this.order == 1 || this.order == 6;
    }

    public String getOrderInterpretation() {
        if (isInterlaced()) {
            switch (this.order) {
                case 1:
                    return "top";
                case 6:
                    return "bottom";
                case 9:
                    return "bottomtop";
                case 14:
                    return "topbottom";
            }
        }
        return "";
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.type = input.get() & 255;
        if (isInterlaced()) {
            this.order = input.get() & 255;
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.put((byte) this.type);
        out.put((byte) this.order);
    }
}
