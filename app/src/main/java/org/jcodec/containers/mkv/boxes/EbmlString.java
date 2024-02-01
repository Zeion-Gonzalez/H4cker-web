package org.jcodec.containers.mkv.boxes;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class EbmlString extends EbmlBin {
    public String charset;

    public EbmlString(byte[] id) {
        super(id);
        this.charset = "UTF-8";
    }

    public EbmlString(byte[] id, String value) {
        super(id);
        this.charset = "UTF-8";
        set(value);
    }

    public String get() {
        try {
            return new String(this.data.array(), this.charset);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void set(String value) {
        try {
            this.data = ByteBuffer.wrap(value.getBytes(this.charset));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}
