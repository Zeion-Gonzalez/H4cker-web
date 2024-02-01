package org.jcodec.containers.mp4.boxes;

import android.support.v4.view.ViewCompat;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FullBox extends Box {
    protected int flags;
    protected byte version;

    public FullBox(Header atom) {
        super(atom);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        int vf = input.getInt();
        this.version = (byte) ((vf >> 24) & 255);
        this.flags = 16777215 & vf;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.putInt((this.version << 24) | (this.flags & ViewCompat.MEASURED_SIZE_MASK));
    }

    public byte getVersion() {
        return this.version;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
