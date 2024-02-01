package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class EssenceContainerData extends MXFInterchangeObject {
    private int bodySID;
    private int indexSID;
    private C0893UL linkedPackageUID;

    public EssenceContainerData(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    protected void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 9985:
                    this.linkedPackageUID = C0893UL.read(_bb);
                    break;
                case 16134:
                    this.indexSID = _bb.getInt();
                    break;
                case 16135:
                    this.bodySID = _bb.getInt();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ EssenceContainerData: " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public C0893UL getLinkedPackageUID() {
        return this.linkedPackageUID;
    }

    public int getIndexSID() {
        return this.indexSID;
    }

    public int getBodySID() {
        return this.bodySID;
    }
}
