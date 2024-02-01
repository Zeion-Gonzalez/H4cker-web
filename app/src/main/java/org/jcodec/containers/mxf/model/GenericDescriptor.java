package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class GenericDescriptor extends MXFInterchangeObject {
    private C0893UL[] locators;
    private C0893UL[] subDescriptors;

    public GenericDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 12033:
                    this.locators = readULBatch(_bb);
                    break;
                case 16129:
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            this.subDescriptors = readULBatch(_bb);
            it.remove();
        }
    }

    public C0893UL[] getLocators() {
        return this.locators;
    }

    public C0893UL[] getSubDescriptors() {
        return this.subDescriptors;
    }
}
