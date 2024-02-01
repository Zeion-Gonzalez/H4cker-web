package org.jcodec.containers.mxf.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class KLV {
    public final long dataOffset;
    public final C0893UL key;
    public final long len;
    public final long offset;
    ByteBuffer value;

    public KLV(C0893UL k, long len, long offset, long dataOffset) {
        this.key = k;
        this.len = len;
        this.offset = offset;
        this.dataOffset = dataOffset;
    }

    public String toString() {
        return "KLV [offset=" + this.offset + ", dataOffset=" + this.dataOffset + ", key=" + this.key + ", len=" + this.len + ", value=" + this.value + "]";
    }

    public static KLV readKL(SeekableByteChannel ch) throws IOException {
        long offset = ch.position();
        if (offset >= ch.size() - 1) {
            return null;
        }
        byte[] key = new byte[16];
        ch.read(ByteBuffer.wrap(key));
        long len = BER.decodeLength(ch);
        long dataOffset = ch.position();
        return new KLV(new C0893UL(key), len, offset, dataOffset);
    }

    public int getLenByteCount() {
        int berlen = (int) ((this.dataOffset - this.offset) - 16);
        if (berlen <= 0) {
            return 4;
        }
        return berlen;
    }

    public static boolean matches(byte[] key1, byte[] key2, int len) {
        for (int i = 0; i < len; i++) {
            if (key1[i] != key2[i]) {
                return false;
            }
        }
        return true;
    }

    public static KLV readKL(ByteBuffer buffer, long baseOffset) {
        if (buffer.remaining() < 17) {
            return null;
        }
        long offset = baseOffset + buffer.position();
        C0893UL ul = C0893UL.read(buffer);
        long len = BER.decodeLength(buffer);
        return new KLV(ul, len, offset, buffer.position() + baseOffset);
    }
}
