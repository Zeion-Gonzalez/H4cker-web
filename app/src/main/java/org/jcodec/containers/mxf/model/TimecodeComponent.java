package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class TimecodeComponent extends MXFStructuralComponent {
    private int base;
    private int dropFrame;
    private long start;

    public TimecodeComponent(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.MXFStructuralComponent, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 5377:
                    this.start = _bb.getLong();
                    break;
                case 5378:
                    this.base = _bb.getShort();
                    break;
                case 5379:
                    this.dropFrame = _bb.get();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public long getStart() {
        return this.start;
    }

    public int getBase() {
        return this.base;
    }

    public int getDropFrame() {
        return this.dropFrame;
    }
}
