package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class ConcatTrack implements VirtualTrack {
    private VirtualPacket lastPacket;
    private VirtualTrack[] tracks;
    private int idx = 0;
    private double offsetPts = 0.0d;
    private int offsetFn = 0;

    public ConcatTrack(VirtualTrack[] tracks) {
        this.tracks = tracks;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        while (this.idx < this.tracks.length) {
            VirtualTrack track = this.tracks[this.idx];
            VirtualPacket nextPacket = track.nextPacket();
            if (nextPacket == null) {
                this.idx++;
                this.offsetPts += this.lastPacket.getPts() + this.lastPacket.getDuration();
                this.offsetFn += this.lastPacket.getFrameNo() + 1;
            } else {
                this.lastPacket = nextPacket;
                return new ConcatPacket(nextPacket, this.offsetPts, this.offsetFn);
            }
        }
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.tracks[0].getCodecMeta();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.tracks[0].getPreferredTimescale();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        for (int i = 0; i < this.tracks.length; i++) {
            this.tracks[i].close();
        }
    }
}
