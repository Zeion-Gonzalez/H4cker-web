package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class SourceClip extends MXFStructuralComponent {
    private C0893UL sourcePackageUid;
    private int sourceTrackId;
    private long startPosition;

    public SourceClip(C0893UL ul) {
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
                case 4353:
                    this.sourcePackageUid = C0893UL.read(_bb);
                    break;
                case 4354:
                    this.sourceTrackId = _bb.getInt();
                    break;
                case 4609:
                    this.startPosition = _bb.getLong();
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public C0893UL getSourcePackageUid() {
        return this.sourcePackageUid;
    }

    public long getStartPosition() {
        return this.startPosition;
    }

    public int getSourceTrackId() {
        return this.sourceTrackId;
    }
}
