package org.jcodec.containers.mkv.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class EbmlBin extends EbmlBase {
    public ByteBuffer data;
    protected boolean dataRead = false;

    public EbmlBin(byte[] id) {
        this.f1540id = id;
    }

    public void read(SeekableByteChannel is) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(this.dataLen);
        is.read(bb);
        bb.flip();
        read(bb);
    }

    public void read(ByteBuffer source) {
        this.data = source.slice();
        this.data.limit(this.dataLen);
        this.dataRead = true;
    }

    public void skip(ByteBuffer source) {
        if (!this.dataRead) {
            source.position((int) (this.dataOffset + this.dataLen));
            this.dataRead = true;
        }
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBase
    public long size() {
        if (this.data == null || this.data.limit() == 0) {
            return super.size();
        }
        long totalSize = this.data.limit();
        return totalSize + EbmlUtil.ebmlLength(this.data.limit()) + this.f1540id.length;
    }

    public void set(ByteBuffer data) {
        this.data = data.slice();
        this.dataLen = this.data.limit();
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBase
    public ByteBuffer getData() {
        int sizeSize = EbmlUtil.ebmlLength(this.data.limit());
        byte[] size = EbmlUtil.ebmlEncode(this.data.limit(), sizeSize);
        ByteBuffer bb = ByteBuffer.allocate(this.f1540id.length + sizeSize + this.data.limit());
        bb.put(this.f1540id);
        bb.put(size);
        bb.put(this.data);
        bb.flip();
        this.data.flip();
        return bb;
    }
}
