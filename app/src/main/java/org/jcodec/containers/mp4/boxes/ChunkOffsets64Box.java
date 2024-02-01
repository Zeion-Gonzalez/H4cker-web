package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ChunkOffsets64Box extends FullBox {
    private long[] chunkOffsets;

    public static String fourcc() {
        return "co64";
    }

    public ChunkOffsets64Box(Header atom) {
        super(atom);
    }

    public ChunkOffsets64Box() {
        super(new Header(fourcc(), 0L));
    }

    public ChunkOffsets64Box(long[] offsets) {
        this();
        this.chunkOffsets = offsets;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        int length = input.getInt();
        this.chunkOffsets = new long[length];
        for (int i = 0; i < length; i++) {
            this.chunkOffsets[i] = input.getLong();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.chunkOffsets.length);
        long[] arr$ = this.chunkOffsets;
        for (long offset : arr$) {
            out.putLong(offset);
        }
    }

    public long[] getChunkOffsets() {
        return this.chunkOffsets;
    }

    public void setChunkOffsets(long[] chunkOffsets) {
        this.chunkOffsets = chunkOffsets;
    }
}
