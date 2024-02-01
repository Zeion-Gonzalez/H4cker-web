package org.jcodec.containers.mps.index;

import android.support.v4.view.InputDeviceCompat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcodec.common.ArrayUtil;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.LongArrayList;
import org.jcodec.common.RunLength;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.tools.MathUtil;
import org.jcodec.containers.mps.MPSDemuxer;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mps.index.MPSIndex;

/* loaded from: classes.dex */
public abstract class BaseIndexer extends MPSUtils.PESReader {
    private Map<Integer, BaseAnalyser> analyzers = new HashMap();
    private LongArrayList tokens = new LongArrayList();
    private RunLength.Integer streams = new RunLength.Integer();

    public int estimateSize() {
        int sizeEstimate = (this.tokens.size() << 3) + this.streams.estimateSize() + 128;
        for (Integer stream : this.analyzers.keySet()) {
            sizeEstimate += this.analyzers.get(stream).estimateSize();
        }
        return sizeEstimate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public abstract class BaseAnalyser {
        protected IntArrayList pts = new IntArrayList(250000);
        protected IntArrayList dur = new IntArrayList(250000);

        public abstract void finishAnalyse();

        public abstract void pkt(ByteBuffer byteBuffer, MPSDemuxer.PESPacket pESPacket);

        public abstract MPSIndex.MPSStreamIndex serialize(int i);

        protected BaseAnalyser() {
        }

        public int estimateSize() {
            return (this.pts.size() << 2) + 4;
        }
    }

    /* loaded from: classes.dex */
    private class GenericAnalyser extends BaseAnalyser {
        private int knownDuration;
        private long lastPts;
        private IntArrayList sizes;

        private GenericAnalyser() {
            super();
            this.sizes = new IntArrayList(250000);
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public void pkt(ByteBuffer pkt, MPSDemuxer.PESPacket pesHeader) {
            this.sizes.add(pkt.remaining());
            if (pesHeader.pts == -1) {
                pesHeader.pts = this.lastPts + this.knownDuration;
            } else {
                this.knownDuration = (int) (pesHeader.pts - this.lastPts);
                this.lastPts = pesHeader.pts;
            }
            this.pts.add((int) pesHeader.pts);
            this.dur.add(this.knownDuration);
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public MPSIndex.MPSStreamIndex serialize(int streamId) {
            return new MPSIndex.MPSStreamIndex(streamId, this.sizes.toArray(), this.pts.toArray(), this.dur.toArray(), new int[0]);
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public int estimateSize() {
            return super.estimateSize() + (this.sizes.size() << 2) + 32;
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public void finishAnalyse() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MPEGVideoAnalyser extends BaseAnalyser {
        private List<Frame> curGop;
        private int frameNo;
        private boolean inFrameData;
        private IntArrayList keyFrames;
        private Frame lastFrame;
        private Frame lastFrameOfLastGop;
        private int marker;
        private long phPos;
        private long position;
        private IntArrayList sizes;

        private MPEGVideoAnalyser() {
            super();
            this.marker = -1;
            this.sizes = new IntArrayList(250000);
            this.keyFrames = new IntArrayList(20000);
            this.curGop = new ArrayList();
            this.phPos = -1L;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class Frame {
            long offset;
            int pts;
            int size;
            int tempRef;

            private Frame() {
            }
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public void pkt(ByteBuffer pkt, MPSDemuxer.PESPacket pesHeader) {
            while (pkt.hasRemaining()) {
                int b = pkt.get() & 255;
                this.position++;
                this.marker = (this.marker << 8) | b;
                if (this.phPos != -1) {
                    long phOffset = this.position - this.phPos;
                    if (phOffset == 5) {
                        this.lastFrame.tempRef = b << 2;
                    } else if (phOffset == 6) {
                        int picCodingType = (b >> 3) & 7;
                        this.lastFrame.tempRef |= b >> 6;
                        if (picCodingType == 1) {
                            this.keyFrames.add(this.frameNo - 1);
                            if (this.curGop.size() > 0) {
                                outGop();
                            }
                        }
                    }
                }
                if ((this.marker & InputDeviceCompat.SOURCE_ANY) == 256) {
                    if (this.inFrameData && (this.marker == 256 || this.marker > 431)) {
                        this.lastFrame.size = (int) ((this.position - 4) - this.lastFrame.offset);
                        this.curGop.add(this.lastFrame);
                        this.lastFrame = null;
                        this.inFrameData = false;
                    } else if (!this.inFrameData && this.marker > 256 && this.marker <= 431) {
                        this.inFrameData = true;
                    }
                    if (this.lastFrame == null && (this.marker == 435 || this.marker == 440 || this.marker == 256)) {
                        Frame frame = new Frame();
                        frame.pts = (int) pesHeader.pts;
                        frame.offset = this.position - 4;
                        Logger.info(String.format("FRAME[%d]: %012x, %d", Integer.valueOf(this.frameNo), Long.valueOf((pesHeader.pos + pkt.position()) - 4), Long.valueOf(pesHeader.pts)));
                        this.frameNo++;
                        this.lastFrame = frame;
                    }
                    if (this.lastFrame != null && this.lastFrame.pts == -1 && this.marker == 256) {
                        this.lastFrame.pts = (int) pesHeader.pts;
                    }
                    this.phPos = this.marker == 256 ? this.position - 4 : -1L;
                }
            }
        }

        private void outGop() {
            fixPts(this.curGop);
            for (Frame frame : this.curGop) {
                this.sizes.add(frame.size);
                this.pts.add(frame.pts);
            }
            this.curGop.clear();
        }

        private void fixPts(List<Frame> curGop) {
            Frame[] frames = (Frame[]) curGop.toArray(new Frame[0]);
            Arrays.sort(frames, new Comparator<Frame>() { // from class: org.jcodec.containers.mps.index.BaseIndexer.MPEGVideoAnalyser.1
                @Override // java.util.Comparator
                public int compare(Frame o1, Frame o2) {
                    if (o1.tempRef > o2.tempRef) {
                        return 1;
                    }
                    return o1.tempRef == o2.tempRef ? 0 : -1;
                }
            });
            for (int dir = 0; dir < 3; dir++) {
                int lastPts = -1;
                int secondLastPts = -1;
                int lastTref = -1;
                int secondLastTref = -1;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i].pts == -1 && lastPts != -1 && secondLastPts != -1) {
                        frames[i].pts = ((lastPts - secondLastPts) / MathUtil.abs(lastTref - secondLastTref)) + lastPts;
                    }
                    if (frames[i].pts != -1) {
                        secondLastPts = lastPts;
                        secondLastTref = lastTref;
                        lastPts = frames[i].pts;
                        lastTref = frames[i].tempRef;
                    }
                }
                ArrayUtil.reverse(frames);
            }
            if (this.lastFrameOfLastGop != null) {
                this.dur.add(frames[0].pts - this.lastFrameOfLastGop.pts);
            }
            for (int i2 = 1; i2 < frames.length; i2++) {
                this.dur.add(frames[i2].pts - frames[i2 - 1].pts);
            }
            this.lastFrameOfLastGop = frames[frames.length - 1];
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public void finishAnalyse() {
            if (this.lastFrame != null) {
                this.lastFrame.size = (int) (this.position - this.lastFrame.offset);
                this.curGop.add(this.lastFrame);
                outGop();
            }
        }

        @Override // org.jcodec.containers.mps.index.BaseIndexer.BaseAnalyser
        public MPSIndex.MPSStreamIndex serialize(int streamId) {
            return new MPSIndex.MPSStreamIndex(streamId, this.sizes.toArray(), this.pts.toArray(), this.dur.toArray(), this.keyFrames.toArray());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseAnalyser getAnalyser(int stream) {
        BaseAnalyser analizer = this.analyzers.get(Integer.valueOf(stream));
        if (analizer == null) {
            BaseAnalyser analizer2 = (stream < 224 || stream > 239) ? new GenericAnalyser() : new MPEGVideoAnalyser();
            this.analyzers.put(Integer.valueOf(stream), analizer2);
        }
        return this.analyzers.get(Integer.valueOf(stream));
    }

    public MPSIndex serialize() {
        List<MPSIndex.MPSStreamIndex> streamsIndices = new ArrayList<>();
        Set<Map.Entry<Integer, BaseAnalyser>> entrySet = this.analyzers.entrySet();
        for (Map.Entry<Integer, BaseAnalyser> entry : entrySet) {
            streamsIndices.add(entry.getValue().serialize(entry.getKey().intValue()));
        }
        return new MPSIndex(this.tokens.toArray(), this.streams, (MPSIndex.MPSStreamIndex[]) streamsIndices.toArray(new MPSIndex.MPSStreamIndex[0]));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void savePESMeta(int stream, long token) {
        this.tokens.add(token);
        this.streams.add(stream);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishAnalyse() {
        super.finishRead();
        for (BaseAnalyser baseAnalyser : this.analyzers.values()) {
            baseAnalyser.finishAnalyse();
        }
    }
}
