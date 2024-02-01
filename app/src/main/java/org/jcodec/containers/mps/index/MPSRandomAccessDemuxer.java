package org.jcodec.containers.mps.index;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.model.Packet;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mps.index.MPSIndex;

/* loaded from: classes.dex */
public class MPSRandomAccessDemuxer {
    private int[] pesStreamIds;
    private long[] pesTokens;
    private Stream[] streams;

    public MPSRandomAccessDemuxer(SeekableByteChannel ch, MPSIndex mpsIndex) throws IOException {
        this.pesTokens = mpsIndex.getPesTokens();
        this.pesStreamIds = mpsIndex.getPesStreamIds().flattern();
        MPSIndex.MPSStreamIndex[] streamIndices = mpsIndex.getStreams();
        this.streams = new Stream[streamIndices.length];
        for (int i = 0; i < streamIndices.length; i++) {
            this.streams[i] = newStream(ch, streamIndices[i]);
        }
    }

    protected Stream newStream(SeekableByteChannel ch, MPSIndex.MPSStreamIndex streamIndex) throws IOException {
        return new Stream(streamIndex, ch);
    }

    public Stream[] getStreams() {
        return this.streams;
    }

    /* loaded from: classes.dex */
    public class Stream extends MPSIndex.MPSStreamIndex implements SeekableDemuxerTrack {
        private static final int MPEG_TIMESCALE = 90000;
        private int curFrame;
        private int curPesIdx;
        private long[] foffs;
        private ByteBuffer pesBuf;
        private int seekToFrame;
        protected SeekableByteChannel source;

        public Stream(MPSIndex.MPSStreamIndex streamIndex, SeekableByteChannel source) throws IOException {
            super(streamIndex);
            this.seekToFrame = -1;
            this.source = source;
            this.foffs = new long[this.fsizes.length];
            long curOff = 0;
            for (int i = 0; i < this.fsizes.length; i++) {
                this.foffs[i] = curOff;
                curOff += this.fsizes[i];
            }
            int[] seg = Arrays.copyOf(streamIndex.getFpts(), 100);
            Arrays.sort(seg);
            this.seekToFrame = 0;
            seekToFrame();
        }

        @Override // org.jcodec.common.DemuxerTrack
        public Packet nextFrame() throws IOException {
            seekToFrame();
            if (this.curFrame >= this.fsizes.length) {
                return null;
            }
            int fs = this.fsizes[this.curFrame];
            ByteBuffer result = ByteBuffer.allocate(fs);
            return nextFrame(result);
        }

        public Packet nextFrame(ByteBuffer buf) throws IOException {
            seekToFrame();
            if (this.curFrame >= this.fsizes.length) {
                return null;
            }
            int fs = this.fsizes[this.curFrame];
            ByteBuffer result = buf.duplicate();
            result.limit(result.position() + fs);
            while (result.hasRemaining()) {
                if (this.pesBuf.hasRemaining()) {
                    result.put(NIOUtils.read(this.pesBuf, Math.min(this.pesBuf.remaining(), result.remaining())));
                } else {
                    this.curPesIdx++;
                    long posShift = 0;
                    while (MPSRandomAccessDemuxer.this.pesStreamIds[this.curPesIdx] != this.streamId) {
                        posShift += MPSIndex.pesLen(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]) + MPSIndex.leadingSize(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]);
                        this.curPesIdx++;
                    }
                    skip(MPSIndex.leadingSize(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]) + posShift);
                    int pesLen = MPSIndex.pesLen(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]);
                    this.pesBuf = fetch(pesLen);
                    MPSUtils.readPESHeader(this.pesBuf, 0L);
                }
            }
            result.flip();
            Packet packet = new Packet(result, this.fpts[this.curFrame], 90000L, this.fdur[this.curFrame], this.curFrame, this.sync.length == 0 || Arrays.binarySearch(this.sync, this.curFrame) >= 0, null);
            this.curFrame++;
            return packet;
        }

        protected ByteBuffer fetch(int pesLen) throws IOException {
            return NIOUtils.fetchFrom(this.source, pesLen);
        }

        protected void skip(long leadingSize) throws IOException {
            this.source.position(this.source.position() + leadingSize);
        }

        protected void reset() throws IOException {
            this.source.position(0L);
        }

        @Override // org.jcodec.common.DemuxerTrack
        public DemuxerTrackMeta getMeta() {
            return null;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoFrame(long frameNo) {
            this.seekToFrame = (int) frameNo;
            return true;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public boolean gotoSyncFrame(long frameNo) {
            int i = 0;
            while (true) {
                if (i >= this.sync.length) {
                    this.seekToFrame = this.sync[this.sync.length - 1];
                    break;
                }
                if (this.sync[i] <= frameNo) {
                    i++;
                } else {
                    this.seekToFrame = this.sync[i - 1];
                    break;
                }
            }
            return true;
        }

        private void seekToFrame() throws IOException {
            if (this.seekToFrame != -1) {
                this.curFrame = this.seekToFrame;
                long payloadOff = this.foffs[this.curFrame];
                long posShift = 0;
                reset();
                this.curPesIdx = 0;
                while (true) {
                    if (MPSRandomAccessDemuxer.this.pesStreamIds[this.curPesIdx] == this.streamId) {
                        int payloadSize = MPSIndex.payLoadSize(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]);
                        if (payloadOff < payloadSize) {
                            skip(MPSIndex.leadingSize(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]) + posShift);
                            this.pesBuf = fetch(MPSIndex.pesLen(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]));
                            MPSUtils.readPESHeader(this.pesBuf, 0L);
                            NIOUtils.skip(this.pesBuf, (int) payloadOff);
                            this.seekToFrame = -1;
                            return;
                        }
                        payloadOff -= payloadSize;
                    }
                    posShift += MPSIndex.pesLen(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]) + MPSIndex.leadingSize(MPSRandomAccessDemuxer.this.pesTokens[this.curPesIdx]);
                    this.curPesIdx++;
                }
            }
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public long getCurFrame() {
            return this.curFrame;
        }

        @Override // org.jcodec.common.SeekableDemuxerTrack
        public void seek(double second) {
            throw new UnsupportedOperationException();
        }
    }
}
