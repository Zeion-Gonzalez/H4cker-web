package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SampleToChunkBox extends FullBox {
    private SampleToChunkEntry[] sampleToChunk;

    /* loaded from: classes.dex */
    public static class SampleToChunkEntry {
        private int count;
        private int entry;
        private long first;

        public SampleToChunkEntry(long first, int count, int entry) {
            this.first = first;
            this.count = count;
            this.entry = entry;
        }

        public long getFirst() {
            return this.first;
        }

        public void setFirst(long first) {
            this.first = first;
        }

        public int getCount() {
            return this.count;
        }

        public int getEntry() {
            return this.entry;
        }

        public void setEntry(int entry) {
            this.entry = entry;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static String fourcc() {
        return "stsc";
    }

    public SampleToChunkBox(SampleToChunkEntry[] sampleToChunk) {
        super(new Header(fourcc()));
        this.sampleToChunk = sampleToChunk;
    }

    public SampleToChunkBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int size = input.getInt();
        this.sampleToChunk = new SampleToChunkEntry[size];
        for (int i = 0; i < size; i++) {
            this.sampleToChunk[i] = new SampleToChunkEntry(input.getInt(), input.getInt(), input.getInt());
        }
    }

    public SampleToChunkEntry[] getSampleToChunk() {
        return this.sampleToChunk;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.sampleToChunk.length);
        SampleToChunkEntry[] arr$ = this.sampleToChunk;
        for (SampleToChunkEntry stc : arr$) {
            out.putInt((int) stc.getFirst());
            out.putInt(stc.getCount());
            out.putInt(stc.getEntry());
        }
    }

    public void setSampleToChunk(SampleToChunkEntry[] sampleToChunk) {
        this.sampleToChunk = sampleToChunk;
    }
}
