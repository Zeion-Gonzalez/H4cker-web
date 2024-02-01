package com.instabug.library.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* compiled from: MD5Generator.java */
/* renamed from: com.instabug.library.util.b */
/* loaded from: classes.dex */
public class C0754b {
    /* renamed from: a */
    public static String m1807a(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                if ((b & 255) < 16) {
                    sb.append("0");
                    sb.append(Integer.toHexString(b & 255));
                } else {
                    sb.append(Integer.toHexString(b & 255));
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
