package org.jcodec.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/* loaded from: classes.dex */
public class FileChannelWrapper implements SeekableByteChannel {

    /* renamed from: ch */
    private FileChannel f1498ch;

    public FileChannelWrapper(FileChannel ch) throws FileNotFoundException {
        this.f1498ch = ch;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer arg0) throws IOException {
        return this.f1498ch.read(arg0);
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.f1498ch.close();
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.f1498ch.isOpen();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer arg0) throws IOException {
        return this.f1498ch.write(arg0);
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long position() throws IOException {
        return this.f1498ch.position();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel position(long newPosition) throws IOException {
        this.f1498ch.position(newPosition);
        return this;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long size() throws IOException {
        return this.f1498ch.size();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel truncate(long size) throws IOException {
        this.f1498ch.truncate(size);
        return this;
    }
}
