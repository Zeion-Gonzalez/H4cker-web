package org.jcodec.common;

import org.jcodec.common.model.Size;

/* loaded from: classes.dex */
public class DemuxerTrackMeta {
    private Size dimensions;
    private int[] seekFrames;
    private double totalDuration;
    private int totalFrames;
    private Type type;

    /* loaded from: classes.dex */
    public enum Type {
        VIDEO,
        AUDIO,
        OTHER
    }

    public DemuxerTrackMeta(Type type, int[] seekFrames, int totalFrames, double totalDuration, Size dimensions) {
        this.type = type;
        this.seekFrames = seekFrames;
        this.totalFrames = totalFrames;
        this.totalDuration = totalDuration;
        this.dimensions = dimensions;
    }

    public Type getType() {
        return this.type;
    }

    public int[] getSeekFrames() {
        return this.seekFrames;
    }

    public int getTotalFrames() {
        return this.totalFrames;
    }

    public double getTotalDuration() {
        return this.totalDuration;
    }

    public Size getDimensions() {
        return this.dimensions;
    }
}
