package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class LeafBox extends Box {
    private ByteBuffer data;

    public LeafBox(Header atom) {
        super(atom);
    }

    public LeafBox(Header atom, ByteBuffer data) {
        super(atom);
        this.data = data;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.data = NIOUtils.read(input, (int) this.header.getBodySize());
    }

    public ByteBuffer getData() {
        return this.data.duplicate();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        NIOUtils.write(out, this.data);
    }
}
