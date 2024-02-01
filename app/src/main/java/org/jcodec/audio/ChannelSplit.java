package org.jcodec.audio;

import java.nio.FloatBuffer;
import org.jcodec.common.AudioFormat;

/* loaded from: classes.dex */
public class ChannelSplit implements AudioFilter {
    private AudioFormat format;

    public ChannelSplit(AudioFormat format) {
        this.format = format;
    }

    @Override // org.jcodec.audio.AudioFilter
    public void filter(FloatBuffer[] in, long[] inPos, FloatBuffer[] out) {
        if (in.length != 1) {
            throw new IllegalArgumentException("Channel split invoked on more then one input");
        }
        if (out.length != this.format.getChannels()) {
            throw new IllegalArgumentException("Channel split must be supplied with " + this.format.getChannels() + " output buffers to hold the channels.");
        }
        FloatBuffer in0 = in[0];
        int outSampleCount = in0.remaining() / out.length;
        for (int i = 0; i < out.length; i++) {
            if (out[i].remaining() < outSampleCount) {
                throw new IllegalArgumentException("Supplied buffer for " + i + "th channel doesn't have sufficient space to put the samples ( required: " + outSampleCount + ", actual: " + out[i].remaining() + ")");
            }
        }
        while (in0.remaining() >= this.format.getChannels()) {
            for (FloatBuffer floatBuffer : out) {
                floatBuffer.put(in0.get());
            }
        }
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getDelay() {
        return 0;
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNInputs() {
        return 1;
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNOutputs() {
        return this.format.getChannels();
    }
}
