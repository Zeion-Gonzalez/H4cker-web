package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Rational;

/* loaded from: classes.dex */
public class FileDescriptor extends GenericDescriptor {
    private C0893UL codec;
    private long containerDuration;
    private C0893UL essenceContainer;
    private int linkedTrackId;
    private Rational sampleRate;

    public FileDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 12289:
                    this.sampleRate = new Rational(_bb.getInt(), _bb.getInt());
                    break;
                case 12290:
                    this.containerDuration = _bb.getLong();
                    break;
                case 12291:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 12292:
                    this.essenceContainer = C0893UL.read(_bb);
                    break;
                case 12293:
                    this.codec = C0893UL.read(_bb);
                    break;
                case 12294:
                    this.linkedTrackId = _bb.getInt();
                    break;
            }
            it.remove();
        }
    }

    public int getLinkedTrackId() {
        return this.linkedTrackId;
    }

    public Rational getSampleRate() {
        return this.sampleRate;
    }

    public long getContainerDuration() {
        return this.containerDuration;
    }

    public C0893UL getEssenceContainer() {
        return this.essenceContainer;
    }

    public C0893UL getCodec() {
        return this.codec;
    }
}
