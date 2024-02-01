package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class NameBox extends Box {
    private String name;

    public static String fourcc() {
        return "name";
    }

    public NameBox(String name) {
        this();
        this.name = name;
    }

    public NameBox() {
        super(new Header(fourcc()));
    }

    private NameBox(Header header) {
        super(header);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.name = NIOUtils.readNullTermString(input);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.put(JCodecUtil.asciiString(this.name));
        out.putInt(0);
    }

    public String getName() {
        return this.name;
    }
}
