package org.jcodec.common;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ByteBufferSeekableByteChannel implements SeekableByteChannel {
    private ByteBuffer backing;
    private int contentLength;
    private boolean open = true;

    public ByteBufferSeekableByteChannel(ByteBuffer backing) {
        this.backing = backing;
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.open;
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.open = false;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        if (!this.backing.hasRemaining()) {
            return -1;
        }
        int toRead = Math.min(this.backing.remaining(), dst.remaining());
        dst.put(NIOUtils.read(this.backing, toRead));
        this.contentLength = Math.max(this.contentLength, this.backing.position());
        return toRead;
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        int toWrite = Math.min(this.backing.remaining(), src.remaining());
        this.backing.put(NIOUtils.read(src, toWrite));
        this.contentLength = Math.max(this.contentLength, this.backing.position());
        return toWrite;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long position() throws IOException {
        return this.backing.position();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel position(long newPosition) throws IOException {
        this.backing.position((int) newPosition);
        this.contentLength = Math.max(this.contentLength, this.backing.position());
        return this;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long size() throws IOException {
        return this.contentLength;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel truncate(long size) throws IOException {
        this.contentLength = (int) size;
        return this;
    }

    public ByteBuffer getContents() {
        ByteBuffer contents = this.backing.duplicate();
        contents.position(0);
        contents.limit(this.contentLength);
        return contents;
    }
}
