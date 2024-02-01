package org.jcodec.containers.mkv.boxes;

import java.nio.ByteBuffer;
import java.util.Date;

/* loaded from: classes.dex */
public class EbmlDate extends EbmlSint {
    private static final int MILISECONDS_IN_A_SECOND = 1000;
    public static long MILISECONDS_SINCE_UNIX_EPOCH_START = 978307200;
    private static final int NANOSECONDS_IN_A_MILISECOND = 1000000;
    private static final int NANOSECONDS_IN_A_SECOND = 1000000000;

    public EbmlDate(byte[] id) {
        super(id);
    }

    public void setDate(Date value) {
        setMiliseconds(value.getTime());
    }

    public Date getDate() {
        long val = get();
        return new Date((val / 1000000) + MILISECONDS_SINCE_UNIX_EPOCH_START);
    }

    private void setMiliseconds(long milliseconds) {
        set((milliseconds - MILISECONDS_SINCE_UNIX_EPOCH_START) * 1000000);
    }

    @Override // org.jcodec.containers.mkv.boxes.EbmlSint
    public void set(long value) {
        this.data = ByteBuffer.allocate(8);
        this.data.putLong(value);
        this.data.flip();
    }
}
