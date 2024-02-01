package org.jcodec.codecs.mpeg12;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class SegmentReader {
    private ByteBuffer buf;
    private ReadableByteChannel channel;
    protected int curMarker;
    private boolean done;
    private int fetchSize;
    private long pos;

    public SegmentReader(ReadableByteChannel channel) throws IOException {
        this(channel, 4096);
    }

    public SegmentReader(ReadableByteChannel channel, int fetchSize) throws IOException {
        this.channel = channel;
        this.fetchSize = fetchSize;
        this.buf = NIOUtils.fetchFrom(channel, 4);
        this.pos = this.buf.remaining();
        this.curMarker = this.buf.getInt();
    }

    public final boolean readToNextMarker(ByteBuffer out) throws IOException {
        if (this.done) {
            return false;
        }
        int n = 1;
        while (true) {
            if (this.buf.hasRemaining()) {
                if (this.curMarker >= 256 && this.curMarker <= 511) {
                    if (n == 0) {
                        return true;
                    }
                    n--;
                }
                out.put((byte) (this.curMarker >>> 24));
                this.curMarker = (this.curMarker << 8) | (this.buf.get() & 255);
            } else {
                this.buf = NIOUtils.fetchFrom(this.channel, this.fetchSize);
                this.pos += this.buf.remaining();
                if (!this.buf.hasRemaining()) {
                    out.putInt(this.curMarker);
                    this.done = true;
                    return false;
                }
            }
        }
    }

    public final boolean skipToMarker() throws IOException {
        if (this.done) {
            return false;
        }
        while (true) {
            if (this.buf.hasRemaining()) {
                this.curMarker = (this.curMarker << 8) | (this.buf.get() & 255);
                if (this.curMarker >= 256 && this.curMarker <= 511) {
                    return true;
                }
            } else {
                this.buf = NIOUtils.fetchFrom(this.channel, this.fetchSize);
                this.pos += this.buf.remaining();
                if (!this.buf.hasRemaining()) {
                    this.done = true;
                    return false;
                }
            }
        }
    }

    public final boolean read(ByteBuffer out, int length) throws IOException {
        if (this.done) {
            return false;
        }
        while (true) {
            if (this.buf.hasRemaining()) {
                int length2 = length - 1;
                if (length == 0) {
                    return true;
                }
                out.put((byte) (this.curMarker >>> 24));
                this.curMarker = (this.curMarker << 8) | (this.buf.get() & 255);
                length = length2;
            } else {
                this.buf = NIOUtils.fetchFrom(this.channel, this.fetchSize);
                this.pos += this.buf.remaining();
                if (!this.buf.hasRemaining()) {
                    out.putInt(this.curMarker);
                    this.done = true;
                    return false;
                }
            }
        }
    }

    public final long curPos() {
        return (this.pos - this.buf.remaining()) - 4;
    }
}
