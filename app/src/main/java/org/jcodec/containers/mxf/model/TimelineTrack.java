package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Rational;

/* loaded from: classes.dex */
public class TimelineTrack extends GenericTrack {
    private Rational editRate;
    private long origin;

    public TimelineTrack(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.GenericTrack, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 19201:
                    this.editRate = new Rational(_bb.getInt(), _bb.getInt());
                    break;
                case 19202:
                    this.origin = _bb.getLong();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public Rational getEditRate() {
        return this.editRate;
    }

    public long getOrigin() {
        return this.origin;
    }
}
