package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class FormatBox extends Box {
    private String fmt;

    public FormatBox(Box other) {
        super(other);
    }

    public FormatBox(Header header) {
        super(header);
    }

    public FormatBox(String fmt) {
        super(new Header(fourcc()));
        this.fmt = fmt;
    }

    public static String fourcc() {
        return "frma";
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.fmt = NIOUtils.readString(input, 4);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.put(JCodecUtil.asciiString(this.fmt));
    }
}
