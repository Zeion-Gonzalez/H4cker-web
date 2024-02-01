package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class Preface extends MXFInterchangeObject {
    private C0893UL[] dmSchemes;
    private C0893UL[] essenceContainers;
    private Date lastModifiedDate;
    private int objectModelVersion;

    /* renamed from: op */
    private C0893UL f1554op;

    public Preface(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    protected void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15106:
                    this.lastModifiedDate = readDate(_bb);
                    break;
                case 15107:
                case 15108:
                case 15109:
                case 15110:
                case 15112:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 15111:
                    this.objectModelVersion = _bb.getInt();
                    break;
                case 15113:
                    this.f1554op = C0893UL.read(_bb);
                    break;
                case 15114:
                    this.essenceContainers = readULBatch(_bb);
                    break;
                case 15115:
                    this.dmSchemes = readULBatch(_bb);
                    break;
            }
            it.remove();
        }
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public int getObjectModelVersion() {
        return this.objectModelVersion;
    }

    public C0893UL getOp() {
        return this.f1554op;
    }

    public C0893UL[] getEssenceContainers() {
        return this.essenceContainers;
    }

    public C0893UL[] getDmSchemes() {
        return this.dmSchemes;
    }
}
