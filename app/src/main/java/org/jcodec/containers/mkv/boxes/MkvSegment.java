package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.jcodec.containers.mkv.MKVType;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class MkvSegment extends EbmlMaster {
    int headerSize;

    public MkvSegment(byte[] id) {
        super(id);
        this.headerSize = 0;
    }

    public MkvSegment() {
        super(MKVType.Segment.f1537id);
        this.headerSize = 0;
    }

    public ByteBuffer getHeader() {
        long headerSize = getHeaderSize();
        if (headerSize > 2147483647L) {
            System.out.println("MkvSegment.getHeader: id.length " + this.f1540id.length + "  Element.getEbmlSize(" + this.dataLen + "): " + EbmlUtil.ebmlLength(this.dataLen) + " size: " + this.dataLen);
        }
        ByteBuffer bb = ByteBuffer.allocate((int) headerSize);
        bb.put(this.f1540id);
        bb.put(EbmlUtil.ebmlEncode(getDataLen()));
        if (this.children != null && !this.children.isEmpty()) {
            Iterator i$ = this.children.iterator();
            while (i$.hasNext()) {
                EbmlBase e = i$.next();
                if (!MKVType.Cluster.equals(e.type)) {
                    bb.put(e.getData());
                }
            }
        }
        bb.flip();
        return bb;
    }

    public long getHeaderSize() {
        long returnValue = this.f1540id.length + EbmlUtil.ebmlLength(getDataLen());
        if (this.children != null && !this.children.isEmpty()) {
            Iterator i$ = this.children.iterator();
            while (i$.hasNext()) {
                EbmlBase e = i$.next();
                if (!MKVType.Cluster.equals(e.type)) {
                    returnValue += e.size();
                }
            }
        }
        return returnValue;
    }
}
