package org.jcodec.audio;

import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public interface AudioFilter {
    void filter(FloatBuffer[] floatBufferArr, long[] jArr, FloatBuffer[] floatBufferArr2);

    int getDelay();

    int getNInputs();

    int getNOutputs();
}
