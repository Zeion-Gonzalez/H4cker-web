package org.jcodec.containers.mxf.model;

import android.support.v4.view.InputDeviceCompat;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class MXFStructuralComponent extends MXFInterchangeObject {
    private C0893UL dataDefinitionUL;
    private long duration;

    public MXFStructuralComponent(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            switch (entry.getKey().intValue()) {
                case InputDeviceCompat.SOURCE_DPAD /* 513 */:
                    this.dataDefinitionUL = C0893UL.read(entry.getValue());
                    break;
                case 514:
                    this.duration = entry.getValue().getLong();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public long getDuration() {
        return this.duration;
    }

    public C0893UL getDataDefinitionUL() {
        return this.dataDefinitionUL;
    }
}
