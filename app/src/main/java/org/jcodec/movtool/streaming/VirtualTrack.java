package org.jcodec.movtool.streaming;

import java.io.IOException;

/* loaded from: classes.dex */
public interface VirtualTrack {
    void close() throws IOException;

    CodecMeta getCodecMeta();

    VirtualEdit[] getEdits();

    int getPreferredTimescale();

    VirtualPacket nextPacket() throws IOException;

    /* loaded from: classes.dex */
    public static class VirtualEdit {
        private double duration;

        /* renamed from: in */
        private double f1561in;

        public VirtualEdit(double in, double duration) {
            this.f1561in = in;
            this.duration = duration;
        }

        public double getIn() {
            return this.f1561in;
        }

        public double getDuration() {
            return this.duration;
        }
    }
}
