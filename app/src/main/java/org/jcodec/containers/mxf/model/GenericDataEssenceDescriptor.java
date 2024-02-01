package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class GenericDataEssenceDescriptor extends FileDescriptor {
    private C0893UL dataEssenceCoding;

    public GenericDataEssenceDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15873:
                    this.dataEssenceCoding = C0893UL.read(_bb);
                    it.remove();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ FileDescriptor: " + this.f1552ul + "]: %04x", entry.getKey()));
                    break;
            }
        }
    }

    public C0893UL getDataEssenceCoding() {
        return this.dataEssenceCoding;
    }
}
