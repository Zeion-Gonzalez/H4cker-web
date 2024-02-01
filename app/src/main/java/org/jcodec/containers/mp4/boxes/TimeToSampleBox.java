package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class TimeToSampleBox extends FullBox {
    private TimeToSampleEntry[] entries;

    /* loaded from: classes.dex */
    public static class TimeToSampleEntry {
        int sampleCount;
        int sampleDuration;

        public TimeToSampleEntry(int sampleCount, int sampleDuration) {
            this.sampleCount = sampleCount;
            this.sampleDuration = sampleDuration;
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int getSampleDuration() {
            return this.sampleDuration;
        }

        public void setSampleDuration(int sampleDuration) {
            this.sampleDuration = sampleDuration;
        }

        public void setSampleCount(int sampleCount) {
            this.sampleCount = sampleCount;
        }

        public long getSegmentDuration() {
            return this.sampleCount * this.sampleDuration;
        }
    }

    public static String fourcc() {
        return "stts";
    }

    public TimeToSampleBox(TimeToSampleEntry[] timeToSamples) {
        super(new Header(fourcc()));
        this.entries = timeToSamples;
    }

    public TimeToSampleBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int foo = input.getInt();
        this.entries = new TimeToSampleEntry[foo];
        for (int i = 0; i < foo; i++) {
            this.entries[i] = new TimeToSampleEntry(input.getInt(), input.getInt());
        }
    }

    public TimeToSampleEntry[] getEntries() {
        return this.entries;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.entries.length);
        TimeToSampleEntry[] arr$ = this.entries;
        for (TimeToSampleEntry timeToSampleEntry : arr$) {
            out.putInt(timeToSampleEntry.getSampleCount());
            out.putInt(timeToSampleEntry.getSampleDuration());
        }
    }

    public void setEntries(TimeToSampleEntry[] entries) {
        this.entries = entries;
    }
}
