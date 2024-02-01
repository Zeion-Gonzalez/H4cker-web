package org.jcodec.codecs.wav;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public abstract class WriterLE {
    public static void writeShort(OutputStream out, short s) throws IOException {
        out.write(s & 255);
        out.write((s >> 8) & 255);
    }

    public static void writeInt(OutputStream out, int i) throws IOException {
        out.write(i & 255);
        out.write((i >> 8) & 255);
        out.write((i >> 16) & 255);
        out.write((i >> 24) & 255);
    }

    public static void writeLong(OutputStream out, long l) throws IOException {
        out.write((int) (l & 255));
        out.write((int) ((l >> 8) & 255));
        out.write((int) ((l >> 16) & 255));
        out.write((int) ((l >> 24) & 255));
        out.write((int) ((l >> 32) & 255));
        out.write((int) ((l >> 40) & 255));
        out.write((int) ((l >> 48) & 255));
        out.write((int) ((l >> 56) & 255));
    }
}
