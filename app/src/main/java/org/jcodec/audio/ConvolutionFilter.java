package org.jcodec.audio;

import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public abstract class ConvolutionFilter implements AudioFilter {
    private double[] kernel;

    protected abstract double[] buildKernel();

    @Override // org.jcodec.audio.AudioFilter
    public void filter(FloatBuffer[] in, long[] pos, FloatBuffer[] out) {
        if (in.length != 1) {
            throw new IllegalArgumentException(getClass().getName() + " filter is designed to work only on one input");
        }
        if (out.length != 1) {
            throw new IllegalArgumentException(getClass().getName() + " filter is designed to work only on one output");
        }
        FloatBuffer in0 = in[0];
        FloatBuffer out0 = out[0];
        if (this.kernel == null) {
            this.kernel = buildKernel();
        }
        if (out0.remaining() < in0.remaining() - this.kernel.length) {
            throw new IllegalArgumentException("Output buffer is too small");
        }
        if (in0.remaining() <= this.kernel.length) {
            throw new IllegalArgumentException("Input buffer should contain > kernel lenght (" + this.kernel.length + ") samples.");
        }
        int halfKernel = this.kernel.length / 2;
        int i = in0.position() + halfKernel;
        while (i < in0.limit() - halfKernel) {
            double result = 0.0d;
            for (int j = 0; j < this.kernel.length; j++) {
                result += this.kernel[j] * in0.get((i + j) - halfKernel);
            }
            out0.put((float) result);
            i++;
        }
        in0.position(i - halfKernel);
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getDelay() {
        if (this.kernel == null) {
            this.kernel = buildKernel();
        }
        return this.kernel.length / 2;
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNInputs() {
        return 1;
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNOutputs() {
        return 1;
    }
}
