package org.jcodec.containers.mxf.model;

import android.support.v4.view.InputDeviceCompat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public abstract class MXFInterchangeObject extends MXFMetadata {
    private C0893UL generationUID;
    private C0893UL objectClass;

    protected abstract void read(Map<Integer, ByteBuffer> map);

    public MXFInterchangeObject(C0893UL ul) {
        super(ul);
    }

    @Override // org.jcodec.containers.mxf.model.MXFMetadata
    public void read(ByteBuffer bb) {
        bb.order(ByteOrder.BIG_ENDIAN);
        Map<Integer, ByteBuffer> tags = new HashMap<>();
        while (bb.hasRemaining()) {
            int tag = bb.getShort() & 65535;
            int size = bb.getShort() & 65535;
            ByteBuffer _bb = NIOUtils.read(bb, size);
            switch (tag) {
                case InputDeviceCompat.SOURCE_KEYBOARD /* 257 */:
                    this.objectClass = C0893UL.read(_bb);
                    break;
                case 258:
                    this.generationUID = C0893UL.read(_bb);
                    break;
                case 15370:
                    this.uid = C0893UL.read(_bb);
                    break;
                default:
                    tags.put(Integer.valueOf(tag), _bb);
                    break;
            }
        }
        if (tags.size() > 0) {
            read(tags);
        }
    }

    public C0893UL getGenerationUID() {
        return this.generationUID;
    }

    public C0893UL getObjectClass() {
        return this.objectClass;
    }
}
