package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;

/* renamed from: org.jcodec.codecs.mpeg4.es.ES */
/* loaded from: classes.dex */
public class C0865ES extends NodeDescriptor {
    private int trackId;

    public C0865ES(int tag, int size) {
        super(tag, size);
    }

    public C0865ES(int trackId, Descriptor... children) {
        super(tag(), children);
        this.trackId = trackId;
    }

    public static int tag() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.NodeDescriptor, org.jcodec.codecs.mpeg4.es.Descriptor
    public void doWrite(ByteBuffer out) {
        out.putShort((short) this.trackId);
        out.put((byte) 0);
        super.doWrite(out);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.NodeDescriptor, org.jcodec.codecs.mpeg4.es.Descriptor
    public void parse(ByteBuffer input) {
        this.trackId = input.getShort();
        input.get();
        super.parse(input);
    }

    public int getTrackId() {
        return this.trackId;
    }
}
