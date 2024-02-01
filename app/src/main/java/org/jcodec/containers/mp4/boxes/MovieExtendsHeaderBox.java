package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MovieExtendsHeaderBox extends FullBox {
    private int fragmentDuration;

    public MovieExtendsHeaderBox() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "mehd";
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.fragmentDuration = input.getInt();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.fragmentDuration);
    }

    public int getFragmentDuration() {
        return this.fragmentDuration;
    }

    public void setFragmentDuration(int fragmentDuration) {
        this.fragmentDuration = fragmentDuration;
    }
}
