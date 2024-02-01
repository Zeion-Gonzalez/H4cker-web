package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class EbmlUlong extends EbmlBin {
    public EbmlUlong(byte[] id) {
        super(id);
        this.data = ByteBuffer.allocate(8);
    }

    public EbmlUlong(byte[] id, long value) {
        super(id);
        set(value);
    }

    public void set(long value) {
        this.data.putLong(value);
        this.data.flip();
    }

    public long get() {
        return this.data.duplicate().getLong();
    }
}
