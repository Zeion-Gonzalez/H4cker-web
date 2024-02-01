package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class CompositionOffsetsBox extends FullBox {
    private Entry[] entries;

    /* loaded from: classes.dex */
    public static class Entry {
        public int count;
        public int offset;

        public Entry(int count, int offset) {
            this.count = count;
            this.offset = offset;
        }

        public int getCount() {
            return this.count;
        }

        public int getOffset() {
            return this.offset;
        }
    }

    public CompositionOffsetsBox() {
        super(new Header(fourcc()));
    }

    public CompositionOffsetsBox(Entry[] entries) {
        super(new Header(fourcc()));
        this.entries = entries;
    }

    public static String fourcc() {
        return "ctts";
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int num = input.getInt();
        this.entries = new Entry[num];
        for (int i = 0; i < num; i++) {
            this.entries[i] = new Entry(input.getInt(), input.getInt());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.entries.length);
        for (int i = 0; i < this.entries.length; i++) {
            out.putInt(this.entries[i].count);
            out.putInt(this.entries[i].offset);
        }
    }

    public Entry[] getEntries() {
        return this.entries;
    }
}
