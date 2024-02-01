package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class GamaExtension extends Box {
    private float gamma;

    public GamaExtension(float gamma) {
        super(new Header(fourcc(), 0L));
        this.gamma = gamma;
    }

    public GamaExtension(Header header) {
        super(header);
    }

    public GamaExtension(Box other) {
        super(other);
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        float g = input.getInt();
        this.gamma = g / 65536.0f;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    protected void doWrite(ByteBuffer out) {
        out.putInt((int) (this.gamma * 65536.0f));
    }

    public float getGamma() {
        return this.gamma;
    }

    public static String fourcc() {
        return "gama";
    }
}
