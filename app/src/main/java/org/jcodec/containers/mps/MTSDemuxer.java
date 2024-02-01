package org.jcodec.containers.mps;

import android.support.v4.view.InputDeviceCompat;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jcodec.common.Assert;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.IntObjectMap;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.MPEGDemuxer;

/* loaded from: classes.dex */
public class MTSDemuxer implements MPEGDemuxer {
    private MPSDemuxer psDemuxer;
    private SeekableByteChannel tsChannel;

    public static Set<Integer> getPrograms(SeekableByteChannel src) throws IOException {
        MTSPacket pkt;
        long rem = src.position();
        Set<Integer> guids = new HashSet<>();
        int i = 0;
        while (true) {
            if ((guids.size() == 0 || i < guids.size() * 500) && (pkt = readPacket(src)) != null) {
                if (pkt.payload != null) {
                    ByteBuffer payload = pkt.payload;
                    if (!guids.contains(Integer.valueOf(pkt.pid)) && (payload.duplicate().getInt() & InputDeviceCompat.SOURCE_ANY) == 256) {
                        guids.add(Integer.valueOf(pkt.pid));
                    }
                }
                i++;
            }
        }
        src.position(rem);
        return guids;
    }

    public static Set<Integer> getPrograms(File file) throws IOException {
        FileChannelWrapper fc = null;
        try {
            fc = NIOUtils.readableFileChannel(file);
            return getPrograms(fc);
        } finally {
            NIOUtils.closeQuietly(fc);
        }
    }

    public MTSDemuxer(SeekableByteChannel src, int filterGuid) throws IOException {
        this.tsChannel = new TSChannel(src, filterGuid);
        this.psDemuxer = new MPSDemuxer(this.tsChannel);
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<? extends MPEGDemuxer.MPEGDemuxerTrack> getTracks() {
        return this.psDemuxer.getTracks();
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<? extends MPEGDemuxer.MPEGDemuxerTrack> getVideoTracks() {
        return this.psDemuxer.getVideoTracks();
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<? extends MPEGDemuxer.MPEGDemuxerTrack> getAudioTracks() {
        return this.psDemuxer.getAudioTracks();
    }

    /* loaded from: classes.dex */
    private static class TSChannel implements SeekableByteChannel {
        private ByteBuffer data;
        private int filterGuid;
        private SeekableByteChannel src;

        public TSChannel(SeekableByteChannel source, int filterGuid) {
            this.src = source;
            this.filterGuid = filterGuid;
        }

        @Override // java.nio.channels.Channel
        public boolean isOpen() {
            return this.src.isOpen();
        }

        @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.src.close();
        }

        @Override // java.nio.channels.ReadableByteChannel
        public int read(ByteBuffer dst) throws IOException {
            while (true) {
                if (this.data == null || !this.data.hasRemaining()) {
                    MTSPacket packet = getPacket(this.src);
                    if (packet == null) {
                        return -1;
                    }
                    this.data = packet.payload;
                } else {
                    int toRead = Math.min(dst.remaining(), this.data.remaining());
                    dst.put(NIOUtils.read(this.data, toRead));
                    return toRead;
                }
            }
        }

        @Override // java.nio.channels.WritableByteChannel
        public int write(ByteBuffer src) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // org.jcodec.common.SeekableByteChannel
        public long position() throws IOException {
            return this.src.position();
        }

        @Override // org.jcodec.common.SeekableByteChannel
        public SeekableByteChannel position(long newPosition) throws IOException {
            this.src.position(newPosition);
            this.data = null;
            return this;
        }

        @Override // org.jcodec.common.SeekableByteChannel
        public long size() throws IOException {
            return this.src.size();
        }

        @Override // org.jcodec.common.SeekableByteChannel
        public SeekableByteChannel truncate(long size) throws IOException {
            return this.src.truncate(size);
        }

        protected MTSPacket getPacket(ReadableByteChannel channel) throws IOException {
            while (true) {
                MTSPacket pkt = MTSDemuxer.readPacket(channel);
                if (pkt == null) {
                    return null;
                }
                if (pkt.pid > 15 && pkt.pid != 8191 && pkt.payload != null) {
                    while (pkt.pid != this.filterGuid) {
                        pkt = MTSDemuxer.readPacket(channel);
                        if (pkt == null) {
                            return null;
                        }
                    }
                    return pkt;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MTSPacket {
        public ByteBuffer payload;
        public boolean payloadStart;
        public int pid;

        public MTSPacket(int guid, boolean payloadStart, ByteBuffer payload) {
            this.pid = guid;
            this.payloadStart = payloadStart;
            this.payload = payload;
        }
    }

    public static MTSPacket readPacket(ReadableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(188);
        if (NIOUtils.read(channel, buffer) != 188) {
            return null;
        }
        buffer.flip();
        return parsePacket(buffer);
    }

    public static MTSPacket parsePacket(ByteBuffer buffer) {
        int marker = buffer.get() & 255;
        Assert.assertEquals(71, marker);
        int guidFlags = buffer.getShort();
        int guid = guidFlags & 8191;
        int payloadStart = (guidFlags >> 14) & 1;
        int b0 = buffer.get() & 255;
        int i = b0 & 15;
        if ((b0 & 32) != 0) {
            int taken = (buffer.get() & 255) + 1;
            NIOUtils.skip(buffer, taken - 1);
        }
        boolean z = payloadStart == 1;
        if ((b0 & 16) == 0) {
            buffer = null;
        }
        return new MTSPacket(guid, z, buffer);
    }

    public static int probe(ByteBuffer b) {
        MTSPacket tsPkt;
        IntObjectMap<List<ByteBuffer>> streams = new IntObjectMap<>();
        while (true) {
            try {
                ByteBuffer sub = NIOUtils.read(b, 188);
                if (sub.remaining() < 188 || (tsPkt = parsePacket(sub)) == null) {
                    break;
                }
                List<ByteBuffer> data = streams.get(tsPkt.pid);
                if (data == null) {
                    data = new ArrayList<>();
                    streams.put(tsPkt.pid, data);
                }
                if (tsPkt.payload != null) {
                    data.add(tsPkt.payload);
                }
            } catch (Throwable th) {
            }
        }
        int maxScore = 0;
        int[] keys = streams.keys();
        for (int i : keys) {
            int score = MPSDemuxer.probe(NIOUtils.combine(streams.get(i)));
            if (score > maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public void seekByte(long offset) throws IOException {
        this.tsChannel.position(offset - (offset % 188));
        this.psDemuxer.reset();
    }
}
