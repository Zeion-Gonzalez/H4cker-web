package org.jcodec.containers.mkv.boxes;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class EbmlVoid extends EbmlBase {
    public EbmlVoid(byte[] id) {
        this.f1540id = id;
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlBase
    public ByteBuffer getData() {
        return null;
    }

    public void skip(SeekableByteChannel is) throws IOException {
        is.position(this.dataOffset + this.dataLen);
    }
}
