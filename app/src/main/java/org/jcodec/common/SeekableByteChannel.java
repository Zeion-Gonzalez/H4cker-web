package org.jcodec.common;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/* loaded from: classes.dex */
public interface SeekableByteChannel extends ByteChannel, Channel, Closeable, ReadableByteChannel, WritableByteChannel {
    long position() throws IOException;

    SeekableByteChannel position(long j) throws IOException;

    long size() throws IOException;

    SeekableByteChannel truncate(long j) throws IOException;
}
