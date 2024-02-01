package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class EbmlUint extends EbmlBin {
    public EbmlUint(byte[] id) {
        super(id);
    }

    public EbmlUint(byte[] id, long value) {
        super(id);
        set(value);
    }

    public void set(long value) {
        this.data = ByteBuffer.wrap(longToBytes(value));
        this.dataLen = this.data.limit();
    }

    public long get() {
        long l = 0;
        for (int i = 0; i < this.data.limit(); i++) {
            long tmp = this.data.get((this.data.limit() - 1) - i) << 56;
            l |= tmp >>> (56 - (i * 8));
        }
        return l;
    }

    public static byte[] longToBytes(long value) {
        byte[] b = new byte[calculatePayloadSize(value)];
        for (int i = b.length - 1; i >= 0; i--) {
            b[i] = (byte) (value >>> (((b.length - i) - 1) * 8));
        }
        return b;
    }

    public static int calculatePayloadSize(long value) {
        if (value == 0) {
            return 1;
        }
        int i = 0;
        while ((((-72057594037927936) >>> (i * 8)) & value) == 0 && i < 8) {
            i++;
        }
        return 8 - i;
    }
}
