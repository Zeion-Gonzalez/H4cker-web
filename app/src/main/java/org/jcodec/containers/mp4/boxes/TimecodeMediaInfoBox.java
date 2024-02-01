package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class TimecodeMediaInfoBox extends FullBox {
    private short[] bgcolor;
    private short[] color;
    private short face;
    private short font;
    private String name;
    private short size;

    public static String fourcc() {
        return "tcmi";
    }

    public TimecodeMediaInfoBox(short font, short face, short size, short[] color, short[] bgcolor, String name) {
        this(new Header(fourcc()));
        this.font = font;
        this.face = face;
        this.size = size;
        this.color = color;
        this.bgcolor = bgcolor;
        this.name = name;
    }

    public TimecodeMediaInfoBox(Header atom) {
        super(atom);
        this.color = new short[3];
        this.bgcolor = new short[3];
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.font = input.getShort();
        this.face = input.getShort();
        this.size = input.getShort();
        input.getShort();
        this.color[0] = input.getShort();
        this.color[1] = input.getShort();
        this.color[2] = input.getShort();
        this.bgcolor[0] = input.getShort();
        this.bgcolor[1] = input.getShort();
        this.bgcolor[2] = input.getShort();
        this.name = NIOUtils.readPascalString(input);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort(this.font);
        out.putShort(this.face);
        out.putShort(this.size);
        out.putShort((short) 0);
        out.putShort(this.color[0]);
        out.putShort(this.color[1]);
        out.putShort(this.color[2]);
        out.putShort(this.bgcolor[0]);
        out.putShort(this.bgcolor[1]);
        out.putShort(this.bgcolor[2]);
        NIOUtils.writePascalString(out, this.name);
    }
}
