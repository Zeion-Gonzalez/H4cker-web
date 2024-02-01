package org.jcodec.containers.mkv.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mkv.MKVType;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public abstract class EbmlBase {
    public long dataOffset;
    public long offset;
    protected EbmlMaster parent;
    public MKVType type;
    public int typeSizeLength;

    /* renamed from: id */
    public byte[] f1540id = {0};
    public int dataLen = 0;

    public abstract ByteBuffer getData();

    public boolean equalId(byte[] typeId) {
        return Arrays.equals(this.f1540id, typeId);
    }

    public long size() {
        return this.dataLen + EbmlUtil.ebmlLength(this.dataLen) + this.f1540id.length;
    }

    public long mux(SeekableByteChannel os) throws IOException {
        ByteBuffer bb = getData();
        return os.write(bb);
    }
}
