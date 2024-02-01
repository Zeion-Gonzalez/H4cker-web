package org.jcodec.movtool.streaming;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class CodecMeta {
    private ByteBuffer codecPrivate;
    private String fourcc;

    public CodecMeta(String fourcc, ByteBuffer codecPrivate) {
        this.fourcc = fourcc;
        this.codecPrivate = codecPrivate;
    }

    public String getFourcc() {
        return this.fourcc;
    }

    public ByteBuffer getCodecPrivate() {
        return this.codecPrivate;
    }
}
