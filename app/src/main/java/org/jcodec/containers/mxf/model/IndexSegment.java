package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class IndexSegment extends MXFInterchangeObject {
    private int bodySID;
    private DeltaEntries deltaEntries;
    private int editUnitByteCount;

    /* renamed from: ie */
    private IndexEntries f1551ie;
    private long indexDuration;
    private int indexEditRateDen;
    private int indexEditRateNum;
    private int indexSID;
    private long indexStartPosition;
    private C0893UL instanceUID;
    private int posTableCount;
    private int sliceCount;

    public IndexSegment(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    protected void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15370:
                    this.instanceUID = C0893UL.read(_bb);
                    break;
                case 16133:
                    this.editUnitByteCount = _bb.getInt();
                    break;
                case 16134:
                    this.indexSID = _bb.getInt();
                    break;
                case 16135:
                    this.bodySID = _bb.getInt();
                    break;
                case 16136:
                    this.sliceCount = _bb.get() & 255;
                    break;
                case 16137:
                    this.deltaEntries = DeltaEntries.read(_bb);
                    break;
                case 16138:
                    this.f1551ie = IndexEntries.read(_bb);
                    break;
                case 16139:
                    this.indexEditRateNum = _bb.getInt();
                    this.indexEditRateDen = _bb.getInt();
                    break;
                case 16140:
                    this.indexStartPosition = _bb.getLong();
                    break;
                case 16141:
                    this.indexDuration = _bb.getLong();
                    break;
                case 16142:
                    this.posTableCount = _bb.get() & 255;
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [" + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public IndexEntries getIe() {
        return this.f1551ie;
    }

    public int getEditUnitByteCount() {
        return this.editUnitByteCount;
    }

    public DeltaEntries getDeltaEntries() {
        return this.deltaEntries;
    }

    public int getIndexSID() {
        return this.indexSID;
    }

    public int getBodySID() {
        return this.bodySID;
    }

    public int getIndexEditRateNum() {
        return this.indexEditRateNum;
    }

    public int getIndexEditRateDen() {
        return this.indexEditRateDen;
    }

    public long getIndexStartPosition() {
        return this.indexStartPosition;
    }

    public long getIndexDuration() {
        return this.indexDuration;
    }

    public C0893UL getInstanceUID() {
        return this.instanceUID;
    }

    public int getSliceCount() {
        return this.sliceCount;
    }

    public int getPosTableCount() {
        return this.posTableCount;
    }
}
