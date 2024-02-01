package org.jcodec.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.jcodec.common.io.AutoPool;
import org.jcodec.common.io.AutoResource;

/* loaded from: classes.dex */
public class AutoFileChannelWrapper implements SeekableByteChannel, AutoResource {
    private static final long THRESHOLD = 5000;
    private long accessTime;

    /* renamed from: ch */
    private FileChannel f1496ch;
    private long curTime = System.currentTimeMillis();
    private File file;
    private long savedPos;

    public AutoFileChannelWrapper(File file) throws IOException {
        this.file = file;
        AutoPool.getInstance().add(this);
        ensureOpen();
    }

    private void ensureOpen() throws IOException {
        this.accessTime = this.curTime;
        if (this.f1496ch == null || !this.f1496ch.isOpen()) {
            this.f1496ch = new FileInputStream(this.file).getChannel();
            this.f1496ch.position(this.savedPos);
        }
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer arg0) throws IOException {
        ensureOpen();
        int r = this.f1496ch.read(arg0);
        this.savedPos = this.f1496ch.position();
        return r;
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.f1496ch != null && this.f1496ch.isOpen()) {
            this.savedPos = this.f1496ch.position();
            this.f1496ch.close();
            this.f1496ch = null;
        }
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.f1496ch != null && this.f1496ch.isOpen();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer arg0) throws IOException {
        ensureOpen();
        int w = this.f1496ch.write(arg0);
        this.savedPos = this.f1496ch.position();
        return w;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long position() throws IOException {
        ensureOpen();
        return this.f1496ch.position();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel position(long newPosition) throws IOException {
        ensureOpen();
        this.f1496ch.position(newPosition);
        this.savedPos = newPosition;
        return this;
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public long size() throws IOException {
        ensureOpen();
        return this.f1496ch.size();
    }

    @Override // org.jcodec.common.SeekableByteChannel
    public SeekableByteChannel truncate(long size) throws IOException {
        ensureOpen();
        this.f1496ch.truncate(size);
        this.savedPos = this.f1496ch.position();
        return this;
    }

    @Override // org.jcodec.common.io.AutoResource
    public void setCurTime(long curTime) {
        this.curTime = curTime;
        if (this.f1496ch != null && this.f1496ch.isOpen() && curTime - this.accessTime > THRESHOLD) {
            try {
                close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
