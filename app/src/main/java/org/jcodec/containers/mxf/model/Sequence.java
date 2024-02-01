package org.jcodec.containers.mxf.model;

import android.support.v4.app.FragmentTransaction;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class Sequence extends MXFStructuralComponent {
    private C0893UL[] structuralComponentsRefs;

    public Sequence(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.MXFStructuralComponent, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            switch (entry.getKey().intValue()) {
                case FragmentTransaction.TRANSIT_FRAGMENT_OPEN /* 4097 */:
                    this.structuralComponentsRefs = readULBatch(entry.getValue());
                    it.remove();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    break;
            }
        }
    }

    public C0893UL[] getStructuralComponentsRefs() {
        return this.structuralComponentsRefs;
    }
}
