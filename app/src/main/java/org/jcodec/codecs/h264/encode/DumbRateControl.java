package org.jcodec.codecs.h264.encode;

/* loaded from: classes.dex */
public class DumbRateControl implements RateControl {

    /* renamed from: QP */
    private static final int f1456QP = 20;

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public int getInitQp() {
        return 20;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public int getQpDelta() {
        return 0;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public boolean accept(int bits) {
        return true;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public void reset() {
    }
}
