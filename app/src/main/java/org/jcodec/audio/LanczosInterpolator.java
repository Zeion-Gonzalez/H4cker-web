package org.jcodec.audio;

import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class LanczosInterpolator implements AudioFilter {
    private double rateStep;

    public static double lanczos(double x, int a) {
        if (x >= (-a) && x <= a) {
            return ((a * Math.sin(3.141592653589793d * x)) * Math.sin((3.141592653589793d * x) / a)) / ((9.869604401089358d * x) * x);
        }
        return 0.0d;
    }

    public LanczosInterpolator(int fromRate, int toRate) {
        this.rateStep = fromRate / toRate;
    }

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
        if (out0.remaining() < ((double) (in0.remaining() - 6)) / this.rateStep) {
            throw new IllegalArgumentException("Output buffer is too small");
        }
        if (in0.remaining() <= 6) {
            throw new IllegalArgumentException("Input buffer should contain > 6 samples.");
        }
        int outSample = 0;
        while (true) {
            double inSample = ((3.0d + (outSample * this.rateStep)) + (Math.ceil(pos[0] / this.rateStep) * this.rateStep)) - pos[0];
            int p0i = (int) Math.floor(inSample);
            int q0i = (int) Math.ceil(inSample);
            if (p0i >= in0.limit() - 3) {
                in0.position(p0i - 3);
                return;
            }
            double p0d = p0i - inSample;
            if (p0d < -0.001d) {
                double q0d = q0i - inSample;
                double p0c = lanczos(p0d, 3);
                double q0c = lanczos(q0d, 3);
                double p1c = lanczos(p0d - 1.0d, 3);
                double q1c = lanczos(1.0d + q0d, 3);
                double p2c = lanczos(p0d - 2.0d, 3);
                double q2c = lanczos(2.0d + q0d, 3);
                double factor = 1.0d / (((((p0c + p1c) + p2c) + q0c) + q1c) + q2c);
                out0.put((float) (((in0.get(q0i) * q0c) + (in0.get(q0i + 1) * q1c) + (in0.get(q0i + 2) * q2c) + (in0.get(p0i) * p0c) + (in0.get(p0i - 1) * p1c) + (in0.get(p0i - 2) * p2c)) * factor));
            } else {
                out0.put(in0.get(p0i));
            }
            outSample++;
        }
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getDelay() {
        return 3;
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
