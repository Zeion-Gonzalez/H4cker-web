package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;

/* renamed from: org.jcodec.containers.mxf.model.UL */
/* loaded from: classes.dex */
public class C0893UL {
    private byte[] bytes;

    public C0893UL(byte... bytes) {
        this.bytes = bytes;
    }

    public C0893UL(int... bytes) {
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            this.bytes[i] = (byte) bytes[i];
        }
    }

    public int hashCode() {
        return ((this.bytes[4] & 255) << 24) | ((this.bytes[5] & 255) << 16) | ((this.bytes[6] & 255) << 8) | (this.bytes[7] & 255);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof C0893UL)) {
            return false;
        }
        byte[] other = ((C0893UL) obj).bytes;
        for (int i = 4; i < Math.min(this.bytes.length, other.length); i++) {
            if (this.bytes[i] != other[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(C0893UL o, int mask) {
        if (o == null) {
            return false;
        }
        byte[] other = o.bytes;
        int mask2 = mask >> 4;
        int i = 4;
        while (i < Math.min(this.bytes.length, other.length)) {
            if ((mask2 & 1) == 1 && this.bytes[i] != other[i]) {
                return false;
            }
            i++;
            mask2 >>= 1;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("06:0E:2B:34:");
        for (int i = 4; i < this.bytes.length; i++) {
            sb.append(hex((this.bytes[i] >> 4) & 15));
            sb.append(hex(this.bytes[i] & 15));
            if (i < this.bytes.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    private char hex(int i) {
        return (char) (i < 10 ? i + 48 : (i - 10) + 65);
    }

    public int get(int i) {
        return this.bytes[i];
    }

    public static C0893UL read(ByteBuffer _bb) {
        byte[] umid = new byte[16];
        _bb.get(umid);
        return new C0893UL(umid);
    }
}
