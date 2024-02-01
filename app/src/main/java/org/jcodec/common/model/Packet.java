package org.jcodec.common.model;

import java.nio.ByteBuffer;
import java.util.Comparator;

/* loaded from: classes.dex */
public class Packet {
    public static final Comparator<Packet> FRAME_ASC = new Comparator<Packet>() { // from class: org.jcodec.common.model.Packet.1
        @Override // java.util.Comparator
        public int compare(Packet o1, Packet o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.frameNo >= o2.frameNo) {
                return o1.frameNo == o2.frameNo ? 0 : 1;
            }
            return -1;
        }
    };
    private ByteBuffer data;
    private int displayOrder;
    private long duration;
    private long frameNo;
    private boolean keyFrame;
    private long pts;
    private TapeTimecode tapeTimecode;
    private long timescale;

    public Packet(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean keyFrame, TapeTimecode tapeTimecode) {
        this(data, pts, timescale, duration, frameNo, keyFrame, tapeTimecode, 0);
    }

    public Packet(Packet other) {
        this(other.data, other.pts, other.timescale, other.duration, other.frameNo, other.keyFrame, other.tapeTimecode);
        this.displayOrder = other.displayOrder;
    }

    public Packet(Packet other, ByteBuffer data) {
        this(data, other.pts, other.timescale, other.duration, other.frameNo, other.keyFrame, other.tapeTimecode);
        this.displayOrder = other.displayOrder;
    }

    public Packet(Packet other, TapeTimecode timecode) {
        this(other.data, other.pts, other.timescale, other.duration, other.frameNo, other.keyFrame, timecode);
        this.displayOrder = other.displayOrder;
    }

    public Packet(ByteBuffer data, long pts, long timescale, long duration, long frameNo, boolean keyFrame, TapeTimecode tapeTimecode, int displayOrder) {
        this.data = data;
        this.pts = pts;
        this.timescale = timescale;
        this.duration = duration;
        this.frameNo = frameNo;
        this.keyFrame = keyFrame;
        this.tapeTimecode = tapeTimecode;
        this.displayOrder = displayOrder;
    }

    public ByteBuffer getData() {
        return this.data.duplicate();
    }

    public long getPts() {
        return this.pts;
    }

    public long getTimescale() {
        return this.timescale;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getFrameNo() {
        return this.frameNo;
    }

    public void setTimescale(int timescale) {
        this.timescale = timescale;
    }

    public TapeTimecode getTapeTimecode() {
        return this.tapeTimecode;
    }

    public void setTapeTimecode(TapeTimecode tapeTimecode) {
        this.tapeTimecode = tapeTimecode;
    }

    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isKeyFrame() {
        return this.keyFrame;
    }

    public RationalLarge getPtsR() {
        return RationalLarge.m2134R(this.pts, this.timescale);
    }

    public double getPtsD() {
        return this.pts / this.timescale;
    }

    public double getDurationD() {
        return this.duration / this.timescale;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }
}
