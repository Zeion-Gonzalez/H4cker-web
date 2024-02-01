package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class ContentStorage extends MXFInterchangeObject {
    private C0893UL[] essenceContainerData;
    private C0893UL[] packageRefs;

    public ContentStorage(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    protected void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 6401:
                    this.packageRefs = readULBatch(_bb);
                    break;
                case 6402:
                    this.essenceContainerData = readULBatch(_bb);
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ ContentStorage: " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public C0893UL[] getPackageRefs() {
        return this.packageRefs;
    }

    public C0893UL[] getEssenceContainerData() {
        return this.essenceContainerData;
    }
}
