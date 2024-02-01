package org.jcodec.common.model;

import java.nio.ByteBuffer;
import org.jcodec.common.AudioFormat;

/* loaded from: classes.dex */
public class AudioFrame extends AudioBuffer {
    private long duration;
    private int frameNo;
    private long pts;
    private long timescale;

    public AudioFrame(AudioBuffer other, long pts, long duration, long timescale, int frameNo) {
        super(other);
        this.pts = pts;
        this.duration = duration;
        this.timescale = timescale;
        this.frameNo = frameNo;
    }

    public AudioFrame(ByteBuffer buffer, AudioFormat format, int nFrames, long pts, long duration, long timescale, int frameNo) {
        super(buffer, format, nFrames);
        this.pts = pts;
        this.duration = duration;
        this.timescale = timescale;
        this.frameNo = frameNo;
    }

    public long getPts() {
        return this.pts;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getTimescale() {
        return this.timescale;
    }

    public int getFrameNo() {
        return this.frameNo;
    }
}
