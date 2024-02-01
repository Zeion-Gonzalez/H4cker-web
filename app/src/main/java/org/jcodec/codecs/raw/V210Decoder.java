package org.jcodec.codecs.raw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class V210Decoder {
    private int height;
    private int width;

    public V210Decoder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Picture decode(byte[] data) {
        IntBuffer dat = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        IntBuffer y = IntBuffer.wrap(new int[this.width * this.height]);
        IntBuffer cb = IntBuffer.wrap(new int[(this.width * this.height) / 2]);
        IntBuffer cr = IntBuffer.wrap(new int[(this.width * this.height) / 2]);
        while (dat.hasRemaining()) {
            int i = dat.get();
            cr.put(i >> 20);
            y.put((i >> 10) & 1023);
            cb.put(i & 1023);
            int i2 = dat.get();
            y.put(i2 & 1023);
            y.put(i2 >> 20);
            cb.put((i2 >> 10) & 1023);
            int i3 = dat.get();
            cb.put(i3 >> 20);
            y.put((i3 >> 10) & 1023);
            cr.put(i3 & 1023);
            int i4 = dat.get();
            y.put(i4 & 1023);
            y.put(i4 >> 20);
            cr.put((i4 >> 10) & 1023);
        }
        return new Picture(this.width, this.height, new int[][]{y.array(), cb.array(), cr.array()}, ColorSpace.YUV422_10);
    }
}
