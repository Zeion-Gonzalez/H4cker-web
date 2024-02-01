package org.jcodec.codecs.common.biari;

/* loaded from: classes.dex */
public class Context {
    private int mps;
    private int stateIdx;

    public Context(int state, int mps) {
        this.stateIdx = state;
        this.mps = mps;
    }

    public int getState() {
        return this.stateIdx;
    }

    public int getMps() {
        return this.mps;
    }

    public void setMps(int mps) {
        this.mps = mps;
    }

    public void setState(int state) {
        this.stateIdx = state;
    }
}
