package org.jcodec.audio;

/* loaded from: classes.dex */
public class SincLowPassFilter extends ConvolutionFilter {
    private double cutoffFreq;
    private int kernelSize;

    public SincLowPassFilter(int cutoffFreq, int samplingRate) {
        this(cutoffFreq / samplingRate);
    }

    public SincLowPassFilter(double cutoffFreq) {
        this(40, cutoffFreq);
    }

    public SincLowPassFilter(int kernelSize, double cutoffFreq) {
        this.kernelSize = kernelSize;
        this.cutoffFreq = cutoffFreq;
    }

    @Override // org.jcodec.audio.ConvolutionFilter
    protected double[] buildKernel() {
        double[] kernel = new double[this.kernelSize];
        double sum = 0.0d;
        for (int i = 0; i < this.kernelSize; i++) {
            int a = i - (this.kernelSize / 2);
            if (a != 0) {
                kernel[i] = (Math.sin((6.283185307179586d * this.cutoffFreq) * (i - (this.kernelSize / 2))) / (i - (this.kernelSize / 2))) * (0.54d - (0.46d * Math.cos((6.283185307179586d * i) / this.kernelSize)));
            } else {
                kernel[i] = 6.283185307179586d * this.cutoffFreq;
            }
            sum += kernel[i];
        }
        for (int i2 = 0; i2 < this.kernelSize; i2++) {
            kernel[i2] = kernel[i2] / sum;
        }
        return kernel;
    }
}
