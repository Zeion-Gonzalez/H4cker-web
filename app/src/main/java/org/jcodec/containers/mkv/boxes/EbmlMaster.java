package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class EbmlMaster extends EbmlBase {
    public final ArrayList<EbmlBase> children = new ArrayList<>();
    protected long usedSize;

    public EbmlMaster(byte[] id) {
        this.f1540id = id;
    }

    public void add(EbmlBase elem) {
        if (elem != null) {
            elem.parent = this;
            this.children.add(elem);
        }
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBase
    public ByteBuffer getData() {
        long size = getDataLen();
        if (size > 2147483647L) {
            System.out.println("EbmlMaster.getData: id.length " + this.f1540id.length + "  EbmlUtil.ebmlLength(" + size + "): " + EbmlUtil.ebmlLength(size) + " size: " + size);
        }
        ByteBuffer bb = ByteBuffer.allocate((int) (this.f1540id.length + EbmlUtil.ebmlLength(size) + size));
        bb.put(this.f1540id);
        bb.put(EbmlUtil.ebmlEncode(size));
        for (int i = 0; i < this.children.size(); i++) {
            bb.put(this.children.get(i).getData());
        }
        bb.flip();
        return bb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getDataLen() {
        if (this.children == null || this.children.isEmpty()) {
            return this.dataLen;
        }
        long dataLength = 0;
        Iterator i$ = this.children.iterator();
        while (i$.hasNext()) {
            EbmlBase e = i$.next();
            dataLength += e.size();
        }
        return dataLength;
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBase
    public long size() {
        long size = getDataLen();
        return size + EbmlUtil.ebmlLength(size) + this.f1540id.length;
    }
}
