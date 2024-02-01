package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class GenericMediaInfoBox extends FullBox {
    private short bOpColor;
    private short balance;
    private short gOpColor;
    private short graphicsMode;
    private short rOpColor;

    public static String fourcc() {
        return "gmin";
    }

    public GenericMediaInfoBox(short graphicsMode, short rOpColor, short gOpColor, short bOpColor, short balance) {
        this();
        this.graphicsMode = graphicsMode;
        this.rOpColor = rOpColor;
        this.gOpColor = gOpColor;
        this.bOpColor = bOpColor;
        this.balance = balance;
    }

    public GenericMediaInfoBox(Header atom) {
        super(atom);
    }

    public GenericMediaInfoBox() {
        this(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.graphicsMode = input.getShort();
        this.rOpColor = input.getShort();
        this.gOpColor = input.getShort();
        this.bOpColor = input.getShort();
        this.balance = input.getShort();
        input.getShort();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort(this.graphicsMode);
        out.putShort(this.rOpColor);
        out.putShort(this.gOpColor);
        out.putShort(this.bOpColor);
        out.putShort(this.balance);
        out.putShort((short) 0);
    }
}
