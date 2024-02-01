package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class IndexEntries {
    private byte[] displayOff;
    private long[] fileOff;
    private byte[] flags;
    private byte[] keyFrameOff;

    public IndexEntries(byte[] displayOff, byte[] keyFrameOff, byte[] flags, long[] fileOff) {
        this.displayOff = displayOff;
        this.keyFrameOff = keyFrameOff;
        this.flags = flags;
        this.fileOff = fileOff;
    }

    public byte[] getDisplayOff() {
        return this.displayOff;
    }

    public byte[] getFlags() {
        return this.flags;
    }

    public long[] getFileOff() {
        return this.fileOff;
    }

    public byte[] getKeyFrameOff() {
        return this.keyFrameOff;
    }

    public static IndexEntries read(ByteBuffer bb) {
        bb.order(ByteOrder.BIG_ENDIAN);
        int n = bb.getInt();
        int len = bb.getInt();
        int[] temporalOff = new int[n];
        byte[] flags = new byte[n];
        long[] fileOff = new long[n];
        byte[] keyFrameOff = new byte[n];
        for (int i = 0; i < n; i++) {
            temporalOff[i] = bb.get() + i;
            keyFrameOff[i] = bb.get();
            flags[i] = bb.get();
            fileOff[i] = bb.getLong();
            NIOUtils.skip(bb, len - 11);
        }
        byte[] displayOff = new byte[n];
        for (int i2 = 0; i2 < n; i2++) {
            int j = 0;
            while (true) {
                if (j >= n) {
                    break;
                }
                if (temporalOff[j] != i2) {
                    j++;
                } else {
                    displayOff[i2] = (byte) (j - i2);
                    break;
                }
            }
        }
        return new IndexEntries(displayOff, keyFrameOff, flags, fileOff);
    }
}
