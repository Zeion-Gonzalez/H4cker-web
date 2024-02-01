package org.jcodec.audio;

import java.io.IOException;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class Audio {
    public static void transfer(AudioSource src, AudioSink sink) throws IOException {
        transfer(src, new DummyFilter(1), sink);
    }

    public static void transfer(AudioSource src, AudioFilter filter, AudioSink sink) throws IOException {
        if (filter.getNInputs() != 1) {
            throw new IllegalArgumentException("Audio filter has # inputs != 1");
        }
        if (filter.getNOutputs() != 1) {
            throw new IllegalArgumentException("Audio filter has # outputs != 1");
        }
        if (filter.getDelay() != 0) {
            throw new IllegalArgumentException("Audio filter has delay");
        }
        FloatBuffer[] ins = {FloatBuffer.allocate(4096)};
        FloatBuffer[] outs = {FloatBuffer.allocate(8192)};
        long[] pos = new long[1];
        while (src.read(ins[0]) != -1) {
            ins[0].flip();
            filter.filter(ins, pos, outs);
            pos[0] = pos[0] + ins[0].position();
            rotate(ins[0]);
            outs[0].flip();
            sink.write(outs[0]);
            outs[0].clear();
        }
    }

    public static void print(FloatBuffer buf) {
        FloatBuffer dup = buf.duplicate();
        while (dup.hasRemaining()) {
            System.out.print(String.format("%.3f,", Float.valueOf(dup.get())));
        }
        System.out.println();
    }

    public static void rotate(FloatBuffer buf) {
        int pos = 0;
        while (buf.hasRemaining()) {
            buf.put(pos, buf.get());
            pos++;
        }
        buf.position(pos);
        buf.limit(buf.capacity());
    }

    /* loaded from: classes.dex */
    public static class DummyFilter implements AudioFilter {
        private int nInputs;

        public DummyFilter(int nInputs) {
            this.nInputs = nInputs;
        }

        @Override // org.jcodec.audio.AudioFilter
        public void filter(FloatBuffer[] in, long[] inPos, FloatBuffer[] out) {
            for (int i = 0; i < in.length; i++) {
                if (out[i].remaining() >= in[i].remaining()) {
                    out[i].put(in[i]);
                } else {
                    out[i].put((FloatBuffer) in[i].duplicate().limit(in[i].position() + out[i].remaining()));
                }
            }
        }

        @Override // org.jcodec.audio.AudioFilter
        public int getDelay() {
            return 0;
        }

        @Override // org.jcodec.audio.AudioFilter
        public int getNInputs() {
            return this.nInputs;
        }

        @Override // org.jcodec.audio.AudioFilter
        public int getNOutputs() {
            return this.nInputs;
        }
    }
}
