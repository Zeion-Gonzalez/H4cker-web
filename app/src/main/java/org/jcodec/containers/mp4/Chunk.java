package org.jcodec.containers.mp4;

/* loaded from: classes.dex */
public class Chunk {
    private int entry;
    private long offset;
    private int sampleCount;
    private int sampleDur;
    private int[] sampleDurs;
    private int sampleSize;
    private int[] sampleSizes;
    private long startTv;

    public Chunk(long offset, long startTv, int sampleCount, int sampleSize, int[] sampleSizes, int sampleDur, int[] sampleDurs, int entry) {
        this.offset = offset;
        this.startTv = startTv;
        this.sampleCount = sampleCount;
        this.sampleSize = sampleSize;
        this.sampleSizes = sampleSizes;
        this.sampleDur = sampleDur;
        this.sampleDurs = sampleDurs;
        this.entry = entry;
    }

    public long getOffset() {
        return this.offset;
    }

    public long getStartTv() {
        return this.startTv;
    }

    public int getSampleCount() {
        return this.sampleCount;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    public int[] getSampleSizes() {
        return this.sampleSizes;
    }

    public int getSampleDur() {
        return this.sampleDur;
    }

    public int[] getSampleDurs() {
        return this.sampleDurs;
    }

    public int getEntry() {
        return this.entry;
    }

    public int getDuration() {
        if (this.sampleDur > 0) {
            return this.sampleDur * this.sampleCount;
        }
        int sum = 0;
        int[] arr$ = this.sampleDurs;
        for (int i : arr$) {
            sum += i;
        }
        return sum;
    }

    public long getSize() {
        if (this.sampleSize > 0) {
            return this.sampleSize * this.sampleCount;
        }
        long sum = 0;
        int[] arr$ = this.sampleSizes;
        for (int i : arr$) {
            sum += i;
        }
        return sum;
    }
}
