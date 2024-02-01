package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class GenericTrack extends MXFInterchangeObject {
    private String name;
    private C0893UL sequenceRef;
    private int trackId;
    private int trackNumber;

    public GenericTrack(C0893UL ul) {
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
                case 18433:
                    this.trackId = _bb.getInt();
                    break;
                case 18434:
                    this.name = readUtf16String(_bb);
                    break;
                case 18435:
                    this.sequenceRef = C0893UL.read(_bb);
                    break;
                case 18436:
                    this.trackNumber = _bb.getInt();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public int getTrackId() {
        return this.trackId;
    }

    public String getName() {
        return this.name;
    }

    public C0893UL getSequenceRef() {
        return this.sequenceRef;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }
}
