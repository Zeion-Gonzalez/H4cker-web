package org.jcodec.containers.mxf.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class BER {
    public static final byte ASN_LONG_LEN = Byte.MIN_VALUE;

    public static final long decodeLength(SeekableByteChannel is) throws IOException {
        long length = 0;
        int lengthbyte = NIOUtils.readByte(is) & 255;
        if ((lengthbyte & (-128)) > 0) {
            int lengthbyte2 = lengthbyte & 127;
            if (lengthbyte2 == 0) {
                throw new IOException("Indefinite lengths are not supported");
            }
            if (lengthbyte2 > 8) {
                throw new IOException("Data length > 4 bytes are not supported!");
            }
            byte[] bb = NIOUtils.readNByte(is, lengthbyte2);
            for (int i = 0; i < lengthbyte2; i++) {
                length = (length << 8) | (bb[i] & 255);
            }
            if (length < 0) {
                throw new IOException("mxflib does not support data lengths > 2^63");
            }
            return length;
        }
        long length2 = lengthbyte & 255;
        return length2;
    }

    public static long decodeLength(ByteBuffer buffer) {
        long length = 0;
        int lengthbyte = buffer.get() & 255;
        if ((lengthbyte & (-128)) > 0) {
            int lengthbyte2 = lengthbyte & 127;
            if (lengthbyte2 == 0) {
                throw new RuntimeException("Indefinite lengths are not supported");
            }
            if (lengthbyte2 > 8) {
                throw new RuntimeException("Data length > 8 bytes are not supported!");
            }
            for (int i = 0; i < lengthbyte2; i++) {
                length = (length << 8) | (buffer.get() & 255);
            }
            if (length < 0) {
                throw new RuntimeException("mxflib does not support data lengths > 2^63");
            }
            return length;
        }
        long length2 = lengthbyte & 255;
        return length2;
    }
}
