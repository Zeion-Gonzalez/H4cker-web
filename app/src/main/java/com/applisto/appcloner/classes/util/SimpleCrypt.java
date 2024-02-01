package com.applisto.appcloner.classes.util;

import android.util.Base64;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public class SimpleCrypt {
    private String mKey;

    public SimpleCrypt(String str) {
        this.mKey = str;
    }

    public String encrypt(String str) {
        try {
            return Base64.encodeToString(getCipher(1).doFinal(str.getBytes()), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] bArr) {
        try {
            return getCipher(1).doFinal(bArr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String str) throws IOException {
        try {
            return new String(getCipher(2).doFinal(Base64.decode(str, 0)));
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    public byte[] decrypt(byte[] bArr) throws IOException {
        try {
            return getCipher(2).doFinal(bArr);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    private Cipher getCipher(int i) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.mKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(i, secretKeySpec);
        return cipher;
    }
}
