package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class VideoMediaHeaderBox extends FullBox {
    int bOpColor;
    int gOpColor;
    int graphicsMode;
    int rOpColor;

    public static String fourcc() {
        return "vmhd";
    }

    public VideoMediaHeaderBox() {
        super(new Header(fourcc()));
    }

    public VideoMediaHeaderBox(Header header) {
        super(header);
    }

    public VideoMediaHeaderBox(int graphicsMode, int rOpColor, int gOpColor, int bOpColor) {
        super(new Header(fourcc()));
        this.graphicsMode = graphicsMode;
        this.rOpColor = rOpColor;
        this.gOpColor = gOpColor;
        this.bOpColor = bOpColor;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.graphicsMode = input.getShort();
        this.rOpColor = input.getShort();
        this.gOpColor = input.getShort();
        this.bOpColor = input.getShort();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort((short) this.graphicsMode);
        out.putShort((short) this.rOpColor);
        out.putShort((short) this.gOpColor);
        out.putShort((short) this.bOpColor);
    }

    public int getGraphicsMode() {
        return this.graphicsMode;
    }

    public int getrOpColor() {
        return this.rOpColor;
    }

    public int getgOpColor() {
        return this.gOpColor;
    }

    public int getbOpColor() {
        return this.bOpColor;
    }
}
