package org.jcodec.codecs.h264.encode;

/* loaded from: classes.dex */
public interface RateControl {
    boolean accept(int i);

    int getInitQp();

    int getQpDelta();

    void reset();
}
