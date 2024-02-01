package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class UrlBox extends FullBox {
    private String url;

    public static String fourcc() {
        return "url ";
    }

    public UrlBox(String url) {
        super(new Header(fourcc()));
        this.url = url;
    }

    public UrlBox(Header atom) {
        super(atom);
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        if ((this.flags & 1) == 0) {
            Charset utf8 = Charset.forName("utf-8");
            this.url = NIOUtils.readNullTermString(input, utf8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        Charset utf8 = Charset.forName("utf-8");
        if (this.url != null) {
            NIOUtils.write(out, ByteBuffer.wrap(this.url.getBytes(utf8)));
            out.put((byte) 0);
        }
    }

    public String getUrl() {
        return this.url;
    }
}
