package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class DecoderSpecific extends Descriptor {
    private ByteBuffer data;

    public DecoderSpecific(int tag, int size) {
        super(tag, size);
    }

    public DecoderSpecific(ByteBuffer data) {
        super(tag());
        this.data = data;
    }

    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    protected void doWrite(ByteBuffer out) {
        NIOUtils.write(out, this.data);
    }

    public static int tag() {
        return 5;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    protected void parse(ByteBuffer input) {
        this.data = NIOUtils.read(input);
    }
}
