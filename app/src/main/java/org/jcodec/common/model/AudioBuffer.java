package org.jcodec.common.model;

import java.nio.ByteBuffer;
import org.jcodec.common.AudioFormat;

/* loaded from: classes.dex */
public class AudioBuffer {
    private ByteBuffer data;
    private AudioFormat format;
    private int nFrames;

    public AudioBuffer(ByteBuffer data, AudioFormat format, int nFrames) {
        this.data = data;
        this.format = format;
        this.nFrames = nFrames;
    }

    public AudioBuffer(AudioBuffer other) {
        this.data = other.data;
        this.format = other.format;
        this.nFrames = other.nFrames;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public AudioFormat getFormat() {
        return this.format;
    }

    public int getNFrames() {
        return this.nFrames;
    }
}
