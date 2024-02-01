package org.jcodec.common.dct;

/* loaded from: classes.dex */
public abstract class DCT {
    public abstract int[] decode(int[] iArr);

    public short[] encode(byte[] orig) {
        throw new UnsupportedOperationException();
    }

    public void decodeAll(int[][] src) {
        for (int i = 0; i < src.length; i++) {
            src[i] = decode(src[i]);
        }
    }
}
