package org.jcodec.audio;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jcodec.audio.Audio;

/* loaded from: classes.dex */
public class FilterGraph implements AudioFilter {
    private FilterSocket[] sockets;

    public static Factory addLevel(AudioFilter first) {
        return new Factory(first);
    }

    /* loaded from: classes.dex */
    public static class Factory {
        private List<FilterSocket> sockets = new ArrayList();

        protected Factory(AudioFilter firstFilter) {
            if (firstFilter.getDelay() != 0) {
                this.sockets.add(new FilterSocket(new Audio.DummyFilter(firstFilter.getNInputs())));
                addLevel(firstFilter);
            } else {
                this.sockets.add(new FilterSocket(firstFilter));
            }
        }

        public Factory addLevel(AudioFilter... filters) {
            FilterSocket socket = new FilterSocket(filters);
            socket.allocateBuffers(4096);
            this.sockets.add(socket);
            return this;
        }

        public Factory addLevel(AudioFilter filter, int n) {
            AudioFilter[] filters = new AudioFilter[n];
            Arrays.fill(filters, filter);
            return addLevel(filters);
        }

        public Factory addLevelSpan(AudioFilter filter) {
            int prevLevelOuts = this.sockets.get(this.sockets.size() - 1).getTotalOutputs();
            if (prevLevelOuts % filter.getNInputs() != 0) {
                throw new IllegalArgumentException("Can't fill " + prevLevelOuts + " with multiple of " + filter.getNInputs());
            }
            return addLevel(filter, prevLevelOuts / filter.getNInputs());
        }

        public FilterGraph create() {
            return new FilterGraph((FilterSocket[]) this.sockets.toArray(new FilterSocket[0]));
        }
    }

    private FilterGraph(FilterSocket[] sockets) {
        this.sockets = sockets;
    }

    @Override // org.jcodec.audio.AudioFilter
    public void filter(FloatBuffer[] ins, long[] pos, FloatBuffer[] outs) {
        this.sockets[0].setBuffers(ins, pos);
        int i = 0;
        while (i < this.sockets.length) {
            FloatBuffer[] curOut = i < this.sockets.length + (-1) ? this.sockets[i + 1].getBuffers() : outs;
            this.sockets[i].filter(curOut);
            if (i > 0) {
                this.sockets[i].rotate();
            }
            if (i < this.sockets.length - 1) {
                FloatBuffer[] arr$ = curOut;
                for (FloatBuffer b : arr$) {
                    b.flip();
                }
            }
            i++;
        }
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getDelay() {
        return this.sockets[0].getFilters()[0].getDelay();
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNInputs() {
        return this.sockets[0].getTotalInputs();
    }

    @Override // org.jcodec.audio.AudioFilter
    public int getNOutputs() {
        return this.sockets[this.sockets.length - 1].getTotalOutputs();
    }
}
