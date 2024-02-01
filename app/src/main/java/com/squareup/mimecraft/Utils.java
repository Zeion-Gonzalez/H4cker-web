package com.squareup.mimecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
final class Utils {
    private Utils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void copyStream(InputStream in, OutputStream out, byte[] buffer) throws IOException {
        while (true) {
            int count = in.read(buffer);
            if (count != -1) {
                out.write(buffer, 0, count);
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void isNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void isNull(Object obj, String message) {
        if (obj != null) {
            throw new IllegalStateException(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void isNotEmpty(String thing, String message) {
        isNotNull(thing, message);
        if ("".equals(thing.trim())) {
            throw new IllegalStateException(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void isNotZero(int value, String message) {
        if (value != 0) {
            throw new IllegalStateException(message);
        }
    }
}
