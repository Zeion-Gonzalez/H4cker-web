package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SoundMediaHeaderBox extends FullBox {
    private short balance;

    public static String fourcc() {
        return "smhd";
    }

    public SoundMediaHeaderBox(Header atom) {
        super(atom);
    }

    public SoundMediaHeaderBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.balance = input.getShort();
        input.getShort();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort(this.balance);
        out.putShort((short) 0);
    }

    public short getBalance() {
        return this.balance;
    }
}
