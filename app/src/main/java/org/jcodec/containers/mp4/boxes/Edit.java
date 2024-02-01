package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class Edit {
    private long duration;
    private long mediaTime;
    private float rate;

    public Edit(long duration, long mediaTime, float rate) {
        this.duration = duration;
        this.mediaTime = mediaTime;
        this.rate = rate;
    }

    public Edit(Edit edit) {
        this.duration = edit.duration;
        this.mediaTime = edit.mediaTime;
        this.rate = edit.rate;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getMediaTime() {
        return this.mediaTime;
    }

    public float getRate() {
        return this.rate;
    }

    public void shift(long shift) {
        this.mediaTime += shift;
    }

    public void setMediaTime(long l) {
        this.mediaTime = l;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
