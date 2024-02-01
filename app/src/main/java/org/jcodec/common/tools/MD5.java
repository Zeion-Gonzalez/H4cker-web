package org.jcodec.common.tools;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public class MD5 {
    public static String md5sum(byte[] bytes) {
        MessageDigest md5 = getDigest();
        md5.update(bytes);
        return digestToString(md5.digest());
    }

    private static String digestToString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte item : digest) {
            int b = item & 255;
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    public static String md5sum(ByteBuffer bytes) {
        MessageDigest md5 = getDigest();
        md5.update(bytes);
        byte[] digest = md5.digest();
        return digestToString(digest);
    }

    public static MessageDigest getDigest() {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
