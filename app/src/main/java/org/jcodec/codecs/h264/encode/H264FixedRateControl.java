package org.jcodec.codecs.h264.encode;

import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class H264FixedRateControl implements RateControl {
    private static final int INIT_QP = 26;
    private int balance;
    private int curQp = 26;
    private int perMb;

    public H264FixedRateControl(int bitsPer256) {
        this.perMb = bitsPer256;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public int getInitQp() {
        return 26;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public int getQpDelta() {
        int qpDelta;
        if (this.balance < 0) {
            qpDelta = this.balance < (-(this.perMb >> 1)) ? 2 : 1;
        } else {
            qpDelta = this.balance > this.perMb ? this.balance > (this.perMb << 2) ? -2 : -1 : 0;
        }
        int prevQp = this.curQp;
        this.curQp = MathUtil.clip(this.curQp + qpDelta, 12, 30);
        return this.curQp - prevQp;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public boolean accept(int bits) {
        this.balance += this.perMb - bits;
        return true;
    }

    @Override // org.jcodec.codecs.h264.encode.RateControl
    public void reset() {
        this.balance = 0;
        this.curQp = 26;
    }

    public int calcFrameSize(int nMB) {
        return ((((this.perMb + 9) * nMB) + 256) >> 3) + (nMB >> 6);
    }

    public void setRate(int rate) {
        this.perMb = rate;
    }
}
