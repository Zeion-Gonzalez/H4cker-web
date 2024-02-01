package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class SeekableByteChannelWrapper implements SeekableByteChannel {
    protected SeekableByteChannel src;

    public SeekableByteChannelWrapper(SeekableByteChannel src) {
        this.src = src;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        return this.src.read(dst);
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.src.isOpen();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.src.close();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer buf) throws IOException {
        return this.src.write(buf);
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long position() throws IOException {
        return this.src.position();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel position(long newPosition) throws IOException {
        this.src.position(newPosition);
        return this;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long size() throws IOException {
        return this.src.size();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel truncate(long size) throws IOException {
        this.src.truncate(size);
        return this;
    }
}
