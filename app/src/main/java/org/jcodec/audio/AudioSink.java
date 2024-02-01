package org.jcodec.audio;

import java.io.IOException;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public interface AudioSink {
    void write(FloatBuffer floatBuffer) throws IOException;
}
