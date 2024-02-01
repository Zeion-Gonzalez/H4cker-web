package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MP4ABox extends Box {
    private int val;

    public MP4ABox(int val) {
        super(new Header("mp4a"));
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putInt(this.val);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.val = input.getInt();
    }
}
