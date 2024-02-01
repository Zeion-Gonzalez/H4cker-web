package org.jcodec.audio;

import java.nio.FloatBuffer;
import org.jcodec.common.Assert;
import org.jcodec.common.AudioFormat;

/* loaded from: classes.dex */
public class ChannelMerge implements AudioFilter {
    private AudioFormat format;

    public ChannelMerge(AudioFormat format) {
        this.format = format;
    }

    @Override // org.jcodec.audio.AudioFilter
    public void filter(FloatBuffer[] in, long[] inPos, FloatBuffer[] out) {
        if (in.length != this.format.getChannels()) {
            throw new IllegalArgumentException("Channel merge must be supplied with " + this.format.getChannels() + " input buffers to hold the channels.");
        }
        if (out.length != 1) {
            throw new IllegalArgumentException("Channel merget invoked on more then one output");
        }
        FloatBuffer out0 = out[0];
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            if (in[i].remaining() < min) {
                min = in[i].remaining();
            }
        }
        for (FloatBuffer floatBuffer : in) {
            Assert.assertEquals(floatBuffer.remaining(), min);
        }
        if (out0.remaining() < in.length * min) {
            throw new IllegalArgumentException("Supplied output buffer is not big enough to hold " + min + " * " + in.length + " = " + (in.length * min) + " output samples.");
        }
        for (int i2 = 0; i2 < min; i2++) {
            for (FloatBuffer floatBuffer2 : in) {
                out0.put(floatBuffer2.get());
            }
        }
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getDelay() {
        return 0;
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNInputs() {
        return this.format.getChannels();
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNOutputs() {
        return 1;
    }
}
