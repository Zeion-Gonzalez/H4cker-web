package org.jcodec.containers.mkv.muxer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mkv.boxes.MkvBlock;

/* loaded from: classes.dex */
public class MKVMuxerTrack {
    static final int DEFAULT_TIMESCALE = 1000000000;
    static final int MULTIPLIER = 1000;
    static final int NANOSECONDS_IN_A_MILISECOND = 1000000;
    public String codecId;
    public Size frameDimentions;
    private int frameDuration;
    public int trackNo;
    public MKVMuxerTrackType type = MKVMuxerTrackType.VIDEO;
    List<MkvBlock> trackBlocks = new ArrayList();

    /* loaded from: classes.dex */
    public enum MKVMuxerTrackType {
        VIDEO
    }

    public int getTimescale() {
        return NANOSECONDS_IN_A_MILISECOND;
    }

    public void addSampleEntry(ByteBuffer frameData, int pts) {
        MkvBlock frame = MkvBlock.keyFrame(this.trackNo, 0, frameData);
        frame.absoluteTimecode = (long) (pts - 1);
        this.trackBlocks.add(frame);
    }

    public long getTrackNo() {
        return this.trackNo;
    }
}
