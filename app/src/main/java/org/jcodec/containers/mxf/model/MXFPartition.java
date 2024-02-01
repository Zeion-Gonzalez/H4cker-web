package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MXFPartition {
    private boolean closed;
    private boolean complete;
    private long essenceFilePos;
    private long essenceLength;
    private MXFPartitionPack pack;

    public MXFPartition(MXFPartitionPack pack, long essenceFilePos, boolean closed, boolean complete, long essenceLength) {
        this.pack = pack;
        this.essenceFilePos = essenceFilePos;
        this.closed = closed;
        this.complete = complete;
        this.essenceLength = essenceLength;
    }

    public static MXFPartition read(C0893UL ul, ByteBuffer bb, long packSize, long nextPartition) {
        boolean closed = (ul.get(14) & 1) == 0;
        boolean complete = ul.get(14) > 2;
        MXFPartitionPack pp = new MXFPartitionPack(ul);
        pp.read(bb);
        long essenceFilePos = roundToKag(pp.getThisPartition() + packSize, pp.getKagSize()) + roundToKag(pp.getHeaderByteCount(), pp.getKagSize()) + roundToKag(pp.getIndexByteCount(), pp.getKagSize());
        return new MXFPartition(pp, essenceFilePos, closed, complete, nextPartition - essenceFilePos);
    }

    static long roundToKag(long position, int kag_size) {
        long ret = (position / kag_size) * kag_size;
        return ret == position ? ret : ret + kag_size;
    }

    public MXFPartitionPack getPack() {
        return this.pack;
    }

    public long getEssenceFilePos() {
        return this.essenceFilePos;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public long getEssenceLength() {
        return this.essenceLength;
    }
}
