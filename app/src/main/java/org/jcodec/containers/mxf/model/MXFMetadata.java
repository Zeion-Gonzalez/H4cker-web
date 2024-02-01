package org.jcodec.containers.mxf.model;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public abstract class MXFMetadata {
    protected C0893UL uid;

    /* renamed from: ul */
    protected C0893UL f1552ul;

    public abstract void read(ByteBuffer byteBuffer);

    public MXFMetadata(C0893UL ul) {
        this.f1552ul = ul;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static C0893UL[] readULBatch(ByteBuffer _bb) {
        int count = _bb.getInt();
        _bb.getInt();
        C0893UL[] result = new C0893UL[count];
        for (int i = 0; i < count; i++) {
            result[i] = C0893UL.read(_bb);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int[] readInt32Batch(ByteBuffer _bb) {
        int count = _bb.getInt();
        _bb.getInt();
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = _bb.getInt();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Date readDate(ByteBuffer _bb) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, _bb.getShort());
        calendar.set(2, _bb.get());
        calendar.set(5, _bb.get());
        calendar.set(10, _bb.get());
        calendar.set(12, _bb.get());
        calendar.set(13, _bb.get());
        calendar.set(14, (_bb.get() & 255) << 2);
        return calendar.getTime();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String readUtf16String(ByteBuffer _bb) {
        try {
            return new String(NIOUtils.toArray(_bb), "utf-16");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public C0893UL getUl() {
        return this.f1552ul;
    }

    public C0893UL getUid() {
        return this.uid;
    }
}
