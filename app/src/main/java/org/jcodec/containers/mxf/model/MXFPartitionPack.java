package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class MXFPartitionPack extends MXFMetadata {
    private int bodySid;
    private long footerPartition;
    private long headerByteCount;
    private long indexByteCount;
    private int indexSid;
    private int kagSize;
    private int nbEssenceContainers;

    /* renamed from: op */
    private C0893UL f1553op;
    private long prevPartition;
    private long thisPartition;

    public MXFPartitionPack(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFMetadata
    public void read(ByteBuffer bb) {
        bb.order(ByteOrder.BIG_ENDIAN);
        NIOUtils.skip(bb, 4);
        this.kagSize = bb.getInt();
        this.thisPartition = bb.getLong();
        this.prevPartition = bb.getLong();
        this.footerPartition = bb.getLong();
        this.headerByteCount = bb.getLong();
        this.indexByteCount = bb.getLong();
        this.indexSid = bb.getInt();
        NIOUtils.skip(bb, 8);
        this.bodySid = bb.getInt();
        this.f1553op = C0893UL.read(bb);
        this.nbEssenceContainers = bb.getInt();
    }

    public int getKagSize() {
        return this.kagSize;
    }

    public long getThisPartition() {
        return this.thisPartition;
    }

    public long getPrevPartition() {
        return this.prevPartition;
    }

    public long getFooterPartition() {
        return this.footerPartition;
    }

    public long getHeaderByteCount() {
        return this.headerByteCount;
    }

    public long getIndexByteCount() {
        return this.indexByteCount;
    }

    public int getIndexSid() {
        return this.indexSid;
    }

    public int getBodySid() {
        return this.bodySid;
    }

    public C0893UL getOp() {
        return this.f1553op;
    }

    public int getNbEssenceContainers() {
        return this.nbEssenceContainers;
    }
}
