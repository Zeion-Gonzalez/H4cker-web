package org.jcodec.movtool.streaming.tracks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class FilePool implements ByteChannelPool {
    private List<SeekableByteChannel> allChannels = Collections.synchronizedList(new ArrayList());
    private BlockingQueue<SeekableByteChannel> channels = new LinkedBlockingQueue();
    private File file;
    private int max;

    public FilePool(File file, int max) {
        this.file = file;
        this.max = max;
    }

    @Override // org.jcodec.movtool.streaming.tracks.ByteChannelPool
    public SeekableByteChannel getChannel() throws IOException {
        SeekableByteChannel channel = this.channels.poll();
        if (channel == null) {
            if (this.allChannels.size() < this.max) {
                channel = newChannel(this.file);
                this.allChannels.add(channel);
            } else {
                while (true) {
                    try {
                        channel = this.channels.take();
                        break;
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        return new PoolChannel(channel);
    }

    protected SeekableByteChannel newChannel(File file) throws FileNotFoundException {
        return NIOUtils.readableFileChannel(file);
    }

    /* loaded from: classes.dex */
    public class PoolChannel extends SeekableByteChannelWrapper {
        public PoolChannel(SeekableByteChannel src) throws IOException {
            super(src);
            src.position(0L);
        }

        @Override // org.jcodec.movtool.streaming.tracks.SeekableByteChannelWrapper, java.nio.channels.Channel
        public boolean isOpen() {
            return this.src != null;
        }

        @Override // org.jcodec.movtool.streaming.tracks.SeekableByteChannelWrapper, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            SeekableByteChannel ret = this.src;
            this.src = null;
            while (true) {
                try {
                    FilePool.this.channels.put(ret);
                    return;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override // org.jcodec.movtool.streaming.tracks.ByteChannelPool
    public void close() {
        while (!this.allChannels.isEmpty()) {
            SeekableByteChannel channel = this.allChannels.remove(0);
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
