package org.jcodec.codecs.vpx;

/* loaded from: classes.dex */
public class NopRateControl implements RateControl {

    /* renamed from: qp */
    private int f1492qp;

    public NopRateControl(int qp) {
        this.f1492qp = qp;
    }

    @Override // org.jcodec.codecs.vpx.RateControl
    public int[] getSegmentQps() {
        return new int[]{this.f1492qp};
    }

    @Override // org.jcodec.codecs.vpx.RateControl
    public int getSegment() {
        return 0;
    }

    @Override // org.jcodec.codecs.vpx.RateControl
    public void report(int bits) {
    }

    @Override // org.jcodec.codecs.vpx.RateControl
    public void reset() {
    }
}
