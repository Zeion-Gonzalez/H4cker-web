package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class SourcePackage extends GenericPackage {
    private C0893UL descriptorRef;
    private C0893UL packageUid;
    private C0893UL[] trackRefs;

    public SourcePackage(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.GenericPackage, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 18177:
                    this.descriptorRef = C0893UL.read(_bb);
                    it.remove();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    break;
            }
        }
    }

    public C0893UL[] getTrackRefs() {
        return this.trackRefs;
    }

    public C0893UL getDescriptorRef() {
        return this.descriptorRef;
    }

    public C0893UL getPackageUid() {
        return this.packageUid;
    }
}
