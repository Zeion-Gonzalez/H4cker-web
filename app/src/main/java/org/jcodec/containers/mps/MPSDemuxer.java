package org.jcodec.containers.mps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcodec.codecs.mpeg12.MPEGES;
import org.jcodec.codecs.mpeg12.SegmentReader;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mps.MPEGDemuxer;

/* loaded from: classes.dex */
public class MPSDemuxer extends SegmentReader implements MPEGDemuxer {
    private static final int BUFFER_SIZE = 1048576;
    private List<ByteBuffer> bufPool;
    private SeekableByteChannel channel;
    private Map<Integer, BaseTrack> streams;

    public MPSDemuxer(SeekableByteChannel channel) throws IOException {
        super(channel);
        this.streams = new HashMap();
        this.bufPool = new ArrayList();
        this.channel = channel;
        findStreams();
    }

    protected void findStreams() throws IOException {
        int i = 0;
        while (true) {
            if (i == 0 || (i < this.streams.size() * 5 && this.streams.size() < 2)) {
                PESPacket nextPacket = nextPacket(getBuffer());
                if (nextPacket != null) {
                    addToStream(nextPacket);
                    i++;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class PESPacket {
        public ByteBuffer data;
        public long dts;
        public int length;
        public long pos;
        public long pts;
        public int streamId;

        public PESPacket(ByteBuffer data, long pts, int streamId, int length, long pos, long dts) {
            this.data = data;
            this.pts = pts;
            this.streamId = streamId;
            this.length = length;
            this.pos = pos;
            this.dts = dts;
        }
    }

    public ByteBuffer getBuffer() {
        synchronized (this.bufPool) {
            if (this.bufPool.size() > 0) {
                return this.bufPool.remove(0);
            }
            return ByteBuffer.allocate(1048576);
        }
    }

    public void putBack(ByteBuffer buffer) {
        buffer.clear();
        synchronized (this.bufPool) {
            this.bufPool.add(buffer);
        }
    }

    /* loaded from: classes.dex */
    public abstract class BaseTrack implements MPEGDemuxer.MPEGDemuxerTrack {
        protected List<PESPacket> pending = new ArrayList();
        protected int streamId;

        public BaseTrack(int streamId, PESPacket pkt) throws IOException {
            this.streamId = streamId;
            this.pending.add(pkt);
        }

        public int getSid() {
            return this.streamId;
        }

        public void pending(PESPacket pkt) {
            if (this.pending != null) {
                this.pending.add(pkt);
            } else {
                MPSDemuxer.this.putBack(pkt.data);
            }
        }

        public List<PESPacket> getPending() {
            return this.pending;
        }

        @Override // org.jcodec.containers.mps.MPEGDemuxer.MPEGDemuxerTrack
        public void ignore() {
            if (this.pending != null) {
                for (PESPacket pesPacket : this.pending) {
                    MPSDemuxer.this.putBack(pesPacket.data);
                }
                this.pending = null;
            }
        }
    }

    /* loaded from: classes.dex */
    public class MPEGTrack extends BaseTrack implements ReadableByteChannel {

        /* renamed from: es */
        private MPEGES f1546es;

        public MPEGTrack(int streamId, PESPacket pkt) throws IOException {
            super(streamId, pkt);
            this.f1546es = new MPEGES(this);
        }

        @Override // java.nio.channels.Channel
        public boolean isOpen() {
            return true;
        }

        public MPEGES getES() {
            return this.f1546es;
        }

        @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // java.nio.channels.ReadableByteChannel
        public int read(ByteBuffer arg0) throws IOException {
            PESPacket pes = this.pending.size() > 0 ? this.pending.remove(0) : getPacket();
            if (pes == null || !pes.data.hasRemaining()) {
                return -1;
            }
            int toRead = Math.min(arg0.remaining(), pes.data.remaining());
            arg0.put(NIOUtils.read(pes.data, toRead));
            if (pes.data.hasRemaining()) {
                this.pending.add(0, pes);
                return toRead;
            }
            MPSDemuxer.this.putBack(pes.data);
            return toRead;
        }

        private PESPacket getPacket() throws IOException {
            if (this.pending.size() > 0) {
                return this.pending.remove(0);
            }
            while (true) {
                PESPacket pkt = MPSDemuxer.this.nextPacket(MPSDemuxer.this.getBuffer());
                if (pkt != null) {
                    if (pkt.streamId == this.streamId) {
                        if (pkt.pts != -1) {
                            this.f1546es.curPts = pkt.pts;
                        }
                        return pkt;
                    }
                    MPSDemuxer.this.addToStream(pkt);
                } else {
                    return null;
                }
            }
        }

        @Override // org.jcodec.containers.mps.MPEGDemuxer.MPEGDemuxerTrack
        public Packet nextFrame(ByteBuffer buf) throws IOException {
            return this.f1546es.getFrame(buf);
        }

        @Override // org.jcodec.containers.mps.MPEGDemuxer.MPEGDemuxerTrack
        public DemuxerTrackMeta getMeta() {
            return new DemuxerTrackMeta(MPSUtils.videoStream(this.streamId) ? DemuxerTrackMeta.Type.VIDEO : MPSUtils.audioStream(this.streamId) ? DemuxerTrackMeta.Type.AUDIO : DemuxerTrackMeta.Type.OTHER, null, 0, 0.0d, null);
        }
    }

    /* loaded from: classes.dex */
    public class PlainTrack extends BaseTrack {
        private int frameNo;

        public PlainTrack(int streamId, PESPacket pkt) throws IOException {
            super(streamId, pkt);
        }

        public boolean isOpen() {
            return true;
        }

        public void close() throws IOException {
        }

        @Override // org.jcodec.containers.mps.MPEGDemuxer.MPEGDemuxerTrack
        public Packet nextFrame(ByteBuffer buf) throws IOException {
            PESPacket pkt;
            if (this.pending.size() <= 0) {
                while (true) {
                    pkt = MPSDemuxer.this.nextPacket(MPSDemuxer.this.getBuffer());
                    if (pkt == null || pkt.streamId == this.streamId) {
                        break;
                    }
                    MPSDemuxer.this.addToStream(pkt);
                }
            } else {
                pkt = this.pending.remove(0);
            }
            if (pkt == null) {
                return null;
            }
            ByteBuffer byteBuffer = pkt.data;
            long j = pkt.pts;
            int i = this.frameNo;
            this.frameNo = i + 1;
            return new Packet(byteBuffer, j, 90000L, 0L, i, true, null);
        }

        @Override // org.jcodec.containers.mps.MPEGDemuxer.MPEGDemuxerTrack
        public DemuxerTrackMeta getMeta() {
            return new DemuxerTrackMeta(MPSUtils.videoStream(this.streamId) ? DemuxerTrackMeta.Type.VIDEO : MPSUtils.audioStream(this.streamId) ? DemuxerTrackMeta.Type.AUDIO : DemuxerTrackMeta.Type.OTHER, null, 0, 0.0d, null);
        }
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public void seekByte(long offset) throws IOException {
        this.channel.position(offset);
        reset();
    }

    public void reset() {
        for (BaseTrack track : this.streams.values()) {
            track.pending.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToStream(PESPacket pkt) throws IOException {
        BaseTrack pes;
        BaseTrack pes2 = this.streams.get(Integer.valueOf(pkt.streamId));
        if (pes2 == null) {
            if (isMPEG(pkt.data)) {
                pes = new MPEGTrack(pkt.streamId, pkt);
            } else {
                pes = new PlainTrack(pkt.streamId, pkt);
            }
            this.streams.put(Integer.valueOf(pkt.streamId), pes);
            return;
        }
        pes2.pending(pkt);
    }

    public PESPacket nextPacket(ByteBuffer out) throws IOException {
        ByteBuffer dup = out.duplicate();
        while (!MPSUtils.psMarker(this.curMarker)) {
            if (!skipToMarker()) {
                return null;
            }
        }
        ByteBuffer fork = dup.duplicate();
        readToNextMarker(dup);
        PESPacket pkt = MPSUtils.readPESHeader(fork, curPos());
        if (pkt.length != 0) {
            read(dup, (pkt.length - dup.position()) + 6);
            fork.limit(dup.position());
            pkt.data = fork;
            return pkt;
        }
        while (!MPSUtils.psMarker(this.curMarker) && readToNextMarker(dup)) {
        }
        fork.limit(dup.position());
        pkt.data = fork;
        return pkt;
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<MPEGDemuxer.MPEGDemuxerTrack> getTracks() {
        return new ArrayList(this.streams.values());
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<MPEGDemuxer.MPEGDemuxerTrack> getVideoTracks() {
        List<MPEGDemuxer.MPEGDemuxerTrack> result = new ArrayList<>();
        for (BaseTrack p : this.streams.values()) {
            if (MPSUtils.videoStream(p.streamId)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override // org.jcodec.containers.mps.MPEGDemuxer
    public List<MPEGDemuxer.MPEGDemuxerTrack> getAudioTracks() {
        List<MPEGDemuxer.MPEGDemuxerTrack> result = new ArrayList<>();
        for (BaseTrack p : this.streams.values()) {
            if (MPSUtils.audioStream(p.streamId)) {
                result.add(p);
            }
        }
        return result;
    }

    private boolean isMPEG(ByteBuffer _data) {
        ByteBuffer b = _data.duplicate();
        int marker = -1;
        int score = 0;
        boolean hasHeader = false;
        boolean slicesStarted = false;
        while (b.hasRemaining()) {
            int code = b.get() & 255;
            marker = (marker << 8) | code;
            if (marker >= 256 && marker <= 440) {
                if (marker >= 432 && marker <= 440) {
                    if ((hasHeader && marker != 437 && marker != 434) || slicesStarted) {
                        break;
                    }
                    score += 5;
                } else if (marker == 256) {
                    if (slicesStarted) {
                        break;
                    }
                    hasHeader = true;
                } else if (marker > 256 && marker < 432) {
                    if (!hasHeader) {
                        break;
                    }
                    if (!slicesStarted) {
                        score += 50;
                        slicesStarted = true;
                    }
                    score++;
                }
            }
        }
        return score > 50;
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0043, code lost:
    
        if (r2 == false) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0045, code lost:
    
        if (r5 != false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0047, code lost:
    
        r1 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int probe(java.nio.ByteBuffer r9) {
        /*
            r8 = 432(0x1b0, float:6.05E-43)
            r7 = 256(0x100, float:3.59E-43)
            r3 = -1
            r4 = 0
            r2 = 0
            r1 = 0
            r5 = 0
        L9:
            boolean r6 = r9.hasRemaining()
            if (r6 == 0) goto L27
            byte r6 = r9.get()
            r0 = r6 & 255(0xff, float:3.57E-43)
            int r6 = r3 << 8
            r3 = r6 | r0
            if (r3 < r7) goto L9
            r6 = 511(0x1ff, float:7.16E-43)
            if (r3 > r6) goto L9
            boolean r6 = org.jcodec.containers.mps.MPSUtils.videoMarker(r3)
            if (r6 == 0) goto L2a
            if (r2 == 0) goto L28
        L27:
            return r4
        L28:
            r2 = 1
            goto L9
        L2a:
            if (r3 < r8) goto L41
            r6 = 440(0x1b8, float:6.17E-43)
            if (r3 > r6) goto L41
            if (r2 == 0) goto L41
            if (r1 == 0) goto L3c
            r6 = 437(0x1b5, float:6.12E-43)
            if (r3 == r6) goto L3c
            r6 = 434(0x1b2, float:6.08E-43)
            if (r3 != r6) goto L27
        L3c:
            if (r5 != 0) goto L27
            int r4 = r4 + 5
            goto L9
        L41:
            if (r3 != r7) goto L49
            if (r2 == 0) goto L49
            if (r5 != 0) goto L27
            r1 = 1
            goto L9
        L49:
            if (r3 <= r7) goto L9
            if (r3 >= r8) goto L9
            if (r1 == 0) goto L27
            if (r5 != 0) goto L54
            int r4 = r4 + 50
            r5 = 1
        L54:
            int r4 = r4 + 1
            goto L9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.containers.mps.MPSDemuxer.probe(java.nio.ByteBuffer):int");
    }

    /* loaded from: classes.dex */
    public static class MPEGPacket extends Packet {
        private int gop;
        private long offset;
        private ByteBuffer seq;
        private int timecode;

        public MPEGPacket(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean keyFrame, TapeTimecode tapeTimecode) {
            super(data, pts, timescale, duration, frameNo, keyFrame, tapeTimecode);
        }

        public long getOffset() {
            return this.offset;
        }

        public ByteBuffer getSeq() {
            return this.seq;
        }

        public int getGOP() {
            return this.gop;
        }

        public int getTimecode() {
            return this.timecode;
        }
    }
}
