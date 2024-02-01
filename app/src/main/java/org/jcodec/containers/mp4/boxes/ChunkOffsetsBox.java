package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ChunkOffsetsBox extends FullBox {
    private long[] chunkOffsets;

    public static String fourcc() {
        return "stco";
    }

    public ChunkOffsetsBox(long[] chunkOffsets) {
        super(new Header(fourcc()));
        this.chunkOffsets = chunkOffsets;
    }

    public ChunkOffsetsBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int length = input.getInt();
        this.chunkOffsets = new long[length];
        for (int i = 0; i < length; i++) {
            this.chunkOffsets[i] = input.getInt() & 4294967295L;
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.chunkOffsets.length);
        long[] arr$ = this.chunkOffsets;
        for (long offset : arr$) {
            out.putInt((int) offset);
        }
    }

    public long[] getChunkOffsets() {
        return this.chunkOffsets;
    }

    public void setChunkOffsets(long[] chunkOffsets) {
        this.chunkOffsets = chunkOffsets;
    }
}
