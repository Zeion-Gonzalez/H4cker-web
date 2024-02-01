package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class GenericPackage extends MXFInterchangeObject {
    private String name;
    private Date packageCreationDate;
    private Date packageModifiedDate;
    private C0893UL packageUID;
    private C0893UL[] tracks;

    public GenericPackage(C0893UL ul) {
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
                case 17409:
                    this.packageUID = C0893UL.read(_bb);
                    break;
                case 17410:
                    this.name = readUtf16String(_bb);
                    break;
                case 17411:
                    this.tracks = readULBatch(_bb);
                    break;
                case 17412:
                    this.packageModifiedDate = readDate(_bb);
                    break;
                case 17413:
                    this.packageCreationDate = readDate(_bb);
                    break;
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
            }
            it.remove();
        }
    }

    public C0893UL[] getTracks() {
        return this.tracks;
    }

    public C0893UL getPackageUID() {
        return this.packageUID;
    }

    public String getName() {
        return this.name;
    }

    public Date getPackageModifiedDate() {
        return this.packageModifiedDate;
    }

    public Date getPackageCreationDate() {
        return this.packageCreationDate;
    }
}
