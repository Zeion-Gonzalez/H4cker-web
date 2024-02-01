package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class Identification extends MXFInterchangeObject {
    private String companyName;
    private Date modificationDate;
    private String platform;
    private String productName;
    private C0893UL productUID;
    private C0893UL thisGenerationUID;
    private short versionString;

    public Identification(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFInterchangeObject
    protected void read(Map<Integer, ByteBuffer> tags) {
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 15361:
                    this.companyName = readUtf16String(_bb);
                    break;
                case 15362:
                    this.productName = readUtf16String(_bb);
                    break;
                case 15363:
                case 15367:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 15364:
                    this.versionString = _bb.getShort();
                    break;
                case 15365:
                    this.productUID = C0893UL.read(_bb);
                    break;
                case 15366:
                    this.modificationDate = readDate(_bb);
                    break;
                case 15368:
                    this.platform = readUtf16String(_bb);
                    break;
                case 15369:
                    this.thisGenerationUID = C0893UL.read(_bb);
                    break;
            }
            it.remove();
        }
    }

    public C0893UL getThisGenerationUID() {
        return this.thisGenerationUID;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public String getProductName() {
        return this.productName;
    }

    public short getVersionString() {
        return this.versionString;
    }

    public C0893UL getProductUID() {
        return this.productUID;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public String getPlatform() {
        return this.platform;
    }
}
