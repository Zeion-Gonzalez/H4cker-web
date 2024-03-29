package org.jcodec.codecs.wav;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public abstract class StringReader {
    public static String readString(InputStream input, int len) throws IOException {
        byte[] bs = sureRead(input, len);
        if (bs == null) {
            return null;
        }
        return new String(bs);
    }

    public static byte[] sureRead(InputStream input, int len) throws IOException {
        byte[] res = new byte[len];
        if (sureRead(input, res, res.length) == len) {
            return res;
        }
        return null;
    }

    public static int sureRead(InputStream input, byte[] buf, int len) throws IOException {
        int read = 0;
        while (read < len) {
            int tmp = input.read(buf, read, len - read);
            if (tmp == -1) {
                break;
            }
            read += tmp;
        }
        return read;
    }

    public static void sureSkip(InputStream is, long l) throws IOException {
        while (l > 0) {
            l -= is.skip(l);
        }
    }
}
