package org.jcodec.movtool.streaming.tracks;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.RunLength;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class MPSTrackFactory {

    /* renamed from: fp */
    private FilePool f1565fp;
    private long[] pesTokens;
    private int[] streams;
    private Map<Integer, Stream> tracks = new HashMap();

    public MPSTrackFactory(ByteBuffer index, FilePool fp) throws IOException {
        this.f1565fp = fp;
        readIndex(index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readIndex(ByteBuffer index) throws IOException {
        int nTokens = index.getInt();
        this.pesTokens = new long[nTokens];
        for (int i = 0; i < this.pesTokens.length; i++) {
            this.pesTokens[i] = index.getLong();
        }
        this.streams = RunLength.Integer.parse(index).flattern();
        while (index.hasRemaining()) {
            int stream = index.get() & 255;
            getStream(this.tracks, stream).parseIndex(index);
        }
    }

    private Stream getStream(Map<Integer, Stream> streams, int streamId) {
        Stream stream = streams.get(Integer.valueOf(streamId));
        if (stream == null) {
            Stream stream2 = createStream(streamId);
            streams.put(Integer.valueOf(streamId), stream2);
            return stream2;
        }
        return stream;
    }

    protected Stream createStream(int streamId) {
        return new Stream(streamId);
    }

    /* loaded from: classes.dex */
    public class Stream implements VirtualTrack {
        private int curFrame;
        private long duration;
        private long fileOff;
        private long[] fpts;
        private int[] fsizes;
        private int offInPayload;
        private int pesIdx;

        /* renamed from: si */
        private ByteBuffer f1566si;
        private int siLen;
        private int streamId;
        private int[] sync;

        public Stream(int streamId) {
            this.streamId = streamId;
        }

        public void parseIndex(ByteBuffer index) throws IOException {
            this.siLen = index.getInt();
            int fCnt = index.getInt();
            this.fsizes = new int[fCnt];
            this.fpts = new long[fCnt];
            for (int i = 0; i < fCnt; i++) {
                int size = index.getInt();
                this.fsizes[i] = size;
            }
            int syncCount = index.getInt();
            this.sync = new int[syncCount];
            for (int i2 = 0; i2 < syncCount; i2++) {
                this.sync[i2] = index.getInt();
            }
            for (int i3 = 0; i3 < fCnt; i3++) {
                this.fpts[i3] = index.getInt() & 4294967295L;
            }
            long[] seg0 = Arrays.copyOf(this.fpts, 10);
            Arrays.sort(seg0);
            long[] seg1 = new long[10];
            System.arraycopy(this.fpts, this.fpts.length - 10, seg1, 0, 10);
            Arrays.sort(seg1);
            this.duration = ((seg1[9] - seg0[0]) + (this.fpts.length >> 1)) / this.fpts.length;
            this.offInPayload = this.siLen;
            this.fileOff = 0L;
            while (MPSTrackFactory.this.streams[this.pesIdx] != this.streamId) {
                this.fileOff += pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]) + leadingSize(MPSTrackFactory.this.pesTokens[this.pesIdx]);
                this.pesIdx++;
            }
            this.fileOff += leadingSize(MPSTrackFactory.this.pesTokens[this.pesIdx]);
            SeekableByteChannel ch = null;
            try {
                ch = MPSTrackFactory.this.f1565fp.getChannel();
                ByteBuffer firstPes = readPes(ch, this.fileOff, pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]), payloadLen(MPSTrackFactory.this.pesTokens[this.pesIdx]), this.pesIdx);
                this.f1566si = NIOUtils.read(firstPes, this.siLen);
            } finally {
                NIOUtils.closeQuietly(ch);
            }
        }

        protected ByteBuffer readPes(SeekableByteChannel ch, long pesPosition, int pesSize, int payloadSize, int pesIdx) throws IOException {
            ch.position(pesPosition);
            ByteBuffer pes = NIOUtils.fetchFrom(ch, pesSize);
            MPSUtils.readPESHeader(pes, 0L);
            return pes;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int pesLen(long token) {
            return (int) ((token >>> 24) & 16777215);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int payloadLen(long token) {
            return (int) (16777215 & token);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int leadingSize(long token) {
            return (int) ((token >>> 48) & 65535);
        }

        @Override // org.jcodec.movtool.streaming.VirtualTrack
        public VirtualPacket nextPacket() throws IOException {
            if (this.curFrame >= this.fsizes.length) {
                return null;
            }
            MPSPacket mPSPacket = new MPSPacket(this.offInPayload, this.fileOff, this.curFrame, this.pesIdx);
            this.offInPayload += this.fsizes[this.curFrame];
            while (this.pesIdx < MPSTrackFactory.this.streams.length && this.offInPayload >= payloadLen(MPSTrackFactory.this.pesTokens[this.pesIdx])) {
                int ps = payloadLen(MPSTrackFactory.this.pesTokens[this.pesIdx]);
                this.offInPayload -= ps;
                this.fileOff += pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]);
                this.pesIdx++;
                if (this.pesIdx < MPSTrackFactory.this.streams.length) {
                    long posShift = 0;
                    while (MPSTrackFactory.this.streams[this.pesIdx] != this.streamId) {
                        posShift += pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]) + leadingSize(MPSTrackFactory.this.pesTokens[this.pesIdx]);
                        this.pesIdx++;
                    }
                    this.fileOff += leadingSize(MPSTrackFactory.this.pesTokens[this.pesIdx]) + posShift;
                }
            }
            this.curFrame++;
            return mPSPacket;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public class MPSPacket implements VirtualPacket {
            private int curFrame;
            private long fileOff;
            private int pesIdx;
            private int pesOff;

            public MPSPacket(int pesOff, long fileOff, int curFrame, int pesIdx) {
                this.pesOff = pesOff;
                this.fileOff = fileOff;
                this.curFrame = curFrame;
                this.pesIdx = pesIdx;
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public ByteBuffer getData() throws IOException {
                ByteBuffer result = ByteBuffer.allocate(Stream.this.siLen + Stream.this.fsizes[this.curFrame]);
                result.put(Stream.this.f1566si.duplicate());
                SeekableByteChannel ch = null;
                try {
                    ch = MPSTrackFactory.this.f1565fp.getChannel();
                    long curOff = this.fileOff;
                    ByteBuffer pesBuf = Stream.this.readPes(ch, curOff, Stream.this.pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]), Stream.this.payloadLen(MPSTrackFactory.this.pesTokens[this.pesIdx]), this.pesIdx);
                    long curOff2 = curOff + Stream.this.pesLen(MPSTrackFactory.this.pesTokens[this.pesIdx]);
                    NIOUtils.skip(pesBuf, this.pesOff);
                    result.put(NIOUtils.read(pesBuf, Math.min(pesBuf.remaining(), result.remaining())));
                    int idx = this.pesIdx;
                    while (result.hasRemaining()) {
                        long posShift = 0;
                        idx++;
                        while (MPSTrackFactory.this.streams[idx] != Stream.this.streamId && idx < MPSTrackFactory.this.pesTokens.length) {
                            posShift += Stream.this.pesLen(MPSTrackFactory.this.pesTokens[idx]) + Stream.this.leadingSize(MPSTrackFactory.this.pesTokens[idx]);
                            idx++;
                        }
                        ByteBuffer pesBuf2 = Stream.this.readPes(ch, curOff2 + posShift + Stream.this.leadingSize(MPSTrackFactory.this.pesTokens[idx]), Stream.this.pesLen(MPSTrackFactory.this.pesTokens[idx]), Stream.this.payloadLen(MPSTrackFactory.this.pesTokens[idx]), idx);
                        curOff2 += Stream.this.leadingSize(MPSTrackFactory.this.pesTokens[idx]) + posShift + Stream.this.pesLen(MPSTrackFactory.this.pesTokens[idx]);
                        result.put(NIOUtils.read(pesBuf2, Math.min(pesBuf2.remaining(), result.remaining())));
                    }
                    result.flip();
                    return result;
                } finally {
                    NIOUtils.closeQuietly(ch);
                }
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public int getDataLen() throws IOException {
                return Stream.this.siLen + Stream.this.fsizes[this.curFrame];
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public double getPts() {
                return (Stream.this.fpts[this.curFrame] - Stream.this.fpts[0]) / 90000.0d;
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public double getDuration() {
                return Stream.this.duration / 90000.0d;
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public boolean isKeyframe() {
                return Stream.this.sync.length == 0 || Arrays.binarySearch(Stream.this.sync, this.curFrame) >= 0;
            }

            @Override // org.jcodec.movtool.streaming.VirtualPacket
            public int getFrameNo() {
                return this.curFrame;
            }
        }

        @Override // org.jcodec.movtool.streaming.VirtualTrack
        public CodecMeta getCodecMeta() {
            return new VideoCodecMeta("m2v1", ByteBuffer.allocate(0), new Size(1920, 1080), new Rational(1, 1));
        }

        @Override // org.jcodec.movtool.streaming.VirtualTrack
        public VirtualTrack.VirtualEdit[] getEdits() {
            return null;
        }

        @Override // org.jcodec.movtool.streaming.VirtualTrack
        public int getPreferredTimescale() {
            return 90000;
        }

        @Override // org.jcodec.movtool.streaming.VirtualTrack
        public void close() throws IOException {
            MPSTrackFactory.this.f1565fp.close();
        }
    }

    public List<Stream> getVideoStreams() {
        List<Stream> ret = new ArrayList<>();
        Set<Map.Entry<Integer, Stream>> entrySet = this.tracks.entrySet();
        for (Map.Entry<Integer, Stream> entry : entrySet) {
            if (MPSUtils.videoStream(entry.getKey().intValue())) {
                ret.add(entry.getValue());
            }
        }
        return ret;
    }

    public List<Stream> getAudioStreams() {
        List<Stream> ret = new ArrayList<>();
        Set<Map.Entry<Integer, Stream>> entrySet = this.tracks.entrySet();
        for (Map.Entry<Integer, Stream> entry : entrySet) {
            if (MPSUtils.audioStream(entry.getKey().intValue())) {
                ret.add(entry.getValue());
            }
        }
        return ret;
    }

    public List<Stream> getStreams() {
        return new ArrayList(this.tracks.values());
    }

    public static void main(String[] args) throws IOException {
        FilePool fp = new FilePool(new File(args[0]), 10);
        MPSTrackFactory factory = new MPSTrackFactory(NIOUtils.fetchFrom(new File(args[1])), fp);
        Stream stream = factory.getVideoStreams().get(0);
        FileChannelWrapper ch = NIOUtils.writableFileChannel(new File(args[2]));
        List<VirtualPacket> pkt = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            pkt.add(stream.nextPacket());
        }
        for (VirtualPacket virtualPacket : pkt) {
            ch.write(virtualPacket.getData());
        }
        ch.close();
    }
}
