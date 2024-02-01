package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;
import org.jcodec.common.Assert;

/* renamed from: org.jcodec.codecs.mpeg4.es.SL */
/* loaded from: classes.dex */
public class C0866SL extends Descriptor {
    public C0866SL(int tag, int size) {
        super(tag, size);
    }

    public C0866SL() {
        super(tag());
    }

    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    protected void doWrite(ByteBuffer out) {
        out.put((byte) 2);
    }

    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    protected void parse(ByteBuffer input) {
        Assert.assertEquals(2, input.get() & 255);
    }

    public static int tag() {
        return 6;
    }
}
