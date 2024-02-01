package org.jcodec.containers.mp4.boxes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.jcodec.codecs.wav.StringReader;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class Header {
    private static final long MAX_UNSIGNED_INT = 4294967296L;
    private String fourcc;
    private boolean lng;
    private long size;

    public Header(String fourcc) {
        this.fourcc = fourcc;
    }

    public Header(String fourcc, long size) {
        this.size = size;
        this.fourcc = fourcc;
    }

    public Header(Header h) {
        this.fourcc = h.fourcc;
        this.size = h.size;
    }

    public Header(String fourcc, long size, boolean lng) {
        this(fourcc, size);
        this.lng = lng;
    }

    public static Header read(ByteBuffer input) {
        long size = 0;
        while (input.remaining() >= 4) {
            size = input.getInt() & 4294967295L;
            if (size != 0) {
                break;
            }
        }
        if (input.remaining() < 4 || (size < 8 && size != 1)) {
            Logger.error("Broken atom of size " + size);
            return null;
        }
        String fourcc = NIOUtils.readString(input, 4);
        boolean lng = false;
        if (size == 1) {
            if (input.remaining() >= 8) {
                lng = true;
                size = input.getLong();
            } else {
                Logger.error("Broken atom of size " + size);
                return null;
            }
        }
        return new Header(fourcc, size, lng);
    }

    public void skip(InputStream di) throws IOException {
        StringReader.sureSkip(di, this.size - headerSize());
    }

    public long headerSize() {
        return (this.lng || this.size > MAX_UNSIGNED_INT) ? 16L : 8L;
    }

    public byte[] readContents(InputStream di) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < this.size - headerSize(); i++) {
            baos.write(di.read());
        }
        return baos.toByteArray();
    }

    public String getFourcc() {
        return this.fourcc;
    }

    public long getBodySize() {
        return this.size - headerSize();
    }

    public void setBodySize(int length) {
        this.size = length + headerSize();
    }

    public void write(ByteBuffer out) {
        if (this.size > MAX_UNSIGNED_INT) {
            out.putInt(1);
        } else {
            out.putInt((int) this.size);
        }
        out.put(JCodecUtil.asciiString(this.fourcc));
        if (this.size > MAX_UNSIGNED_INT) {
            out.putLong(this.size);
        }
    }

    public void write(SeekableByteChannel output) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(16);
        write(bb);
        bb.flip();
        output.write(bb);
    }

    public long getSize() {
        return this.size;
    }

    public int hashCode() {
        int result = (this.fourcc == null ? 0 : this.fourcc.hashCode()) + 31;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Header other = (Header) obj;
            return this.fourcc == null ? other.fourcc == null : this.fourcc.equals(other.fourcc);
        }
        return false;
    }
}
