package org.jcodec.audio;

import java.nio.FloatBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
public class FilterSocket {
    private FloatBuffer[] buffers;
    private int[] delays;
    private AudioFilter[] filters;
    private long[] positions;
    private int totalInputs;
    private int totalOutputs;

    public FilterSocket(AudioFilter... filters) {
        this.totalInputs = 0;
        this.totalOutputs = 0;
        for (int i = 0; i < filters.length; i++) {
            this.totalInputs += filters[i].getNInputs();
            this.totalOutputs += filters[i].getNOutputs();
        }
        this.buffers = new FloatBuffer[this.totalInputs];
        this.positions = new long[this.totalInputs];
        this.delays = new int[this.totalInputs];
        int b = 0;
        for (int i2 = 0; i2 < filters.length; i2++) {
            int j = 0;
            while (j < filters[i2].getNInputs()) {
                this.delays[b] = filters[i2].getDelay();
                j++;
                b++;
            }
        }
        this.filters = filters;
    }

    public void allocateBuffers(int bufferSize) {
        for (int i = 0; i < this.totalInputs; i++) {
            this.buffers[i] = FloatBuffer.allocate((this.delays[i] * 2) + bufferSize);
            this.buffers[i].position(this.delays[i]);
        }
    }

    FilterSocket(AudioFilter filter, FloatBuffer[] buffers, long[] positions) {
        this.filters = new AudioFilter[]{filter};
        this.buffers = buffers;
        this.positions = positions;
        this.delays = new int[]{filter.getDelay()};
        this.totalInputs = filter.getNInputs();
        this.totalOutputs = filter.getNOutputs();
    }

    public void filter(FloatBuffer[] outputs) {
        if (outputs.length != this.totalOutputs) {
            throw new IllegalArgumentException("Can not output to provided filter socket inputs != outputs (" + outputs.length + "!=" + this.totalOutputs + ")");
        }
        int ii = 0;
        int oi = 0;
        for (int i = 0; i < this.filters.length; i++) {
            this.filters[i].filter((FloatBuffer[]) Arrays.copyOfRange(this.buffers, ii, this.filters[i].getNInputs() + ii), Arrays.copyOfRange(this.positions, ii, this.filters[i].getNInputs() + ii), (FloatBuffer[]) Arrays.copyOfRange(outputs, oi, this.filters[i].getNOutputs() + oi));
            ii += this.filters[i].getNInputs();
            oi += this.filters[i].getNOutputs();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatBuffer[] getBuffers() {
        return this.buffers;
    }

    public void rotate() {
        for (int i = 0; i < this.buffers.length; i++) {
            long[] jArr = this.positions;
            jArr[i] = jArr[i] + this.buffers[i].position();
            Audio.rotate(this.buffers[i]);
        }
    }

    public void setBuffers(FloatBuffer[] ins, long[] pos) {
        if (ins.length != this.totalInputs) {
            throw new IllegalArgumentException("Number of input buffers provided is less then the number of filter inputs.");
        }
        if (pos.length != this.totalInputs) {
            throw new IllegalArgumentException("Number of input buffer positions provided is less then the number of filter inputs.");
        }
        this.buffers = ins;
        this.positions = pos;
    }

    public int getTotalInputs() {
        return this.totalInputs;
    }

    public int getTotalOutputs() {
        return this.totalOutputs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioFilter[] getFilters() {
        return this.filters;
    }

    public long[] getPositions() {
        return this.positions;
    }
}
