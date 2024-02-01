package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class EbmlFloat extends EbmlBin {
    public EbmlFloat(byte[] id) {
        super(id);
    }

    public void set(double value) {
        if (value < 3.4028234663852886E38d) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putFloat((float) value);
            bb.flip();
            this.data = bb;
            return;
        }
        if (value < Double.MAX_VALUE) {
            ByteBuffer bb2 = ByteBuffer.allocate(8);
            bb2.putDouble(value);
            bb2.flip();
            this.data = bb2;
        }
    }

    public double get() {
        return this.data.limit() == 4 ? this.data.duplicate().getFloat() : this.data.duplicate().getDouble();
    }
}
