package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class EditedFramesTrack implements VirtualTrack {
    private List<VirtualPacket>[] buckets;
    private int curEdit;
    private int curPkt;
    private VirtualTrack.VirtualEdit[] edits;
    private int frameNo;
    private double pts;
    private VirtualTrack src;

    public EditedFramesTrack(VirtualTrack src) throws IOException {
        this.src = src;
        this.edits = src.getEdits();
        this.buckets = new List[this.edits.length];
        for (int i = 0; i < this.edits.length; i++) {
            this.buckets[i] = new ArrayList();
        }
        while (true) {
            VirtualPacket pkt = src.nextPacket();
            if (pkt != null) {
                if (!pkt.isKeyframe()) {
                    throw new IllegalArgumentException("Can not apply edits to a track that has inter frames, this will result in decoding errors.");
                }
                for (int e = 0; e < this.edits.length; e++) {
                    VirtualTrack.VirtualEdit ed = this.edits[e];
                    if (pkt.getPts() < ed.getIn() + ed.getDuration() && pkt.getPts() + pkt.getDuration() > ed.getIn()) {
                        this.buckets[e].add(pkt);
                    }
                }
            } else {
                return;
            }
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        if (this.curEdit >= this.edits.length) {
            return null;
        }
        VirtualPacket pkt = this.buckets[this.curEdit].get(this.curPkt);
        VirtualTrack.VirtualEdit edt = this.edits[this.curEdit];
        double duration = pkt.getDuration();
        double sticksFront = edt.getIn() - pkt.getPts();
        double sticksBack = (pkt.getPts() + pkt.getDuration()) - (edt.getIn() + edt.getDuration());
        double duration2 = duration - (Math.max(sticksFront, 0.0d) + Math.max(sticksBack, 0.0d));
        EditedFramesPacket editedFramesPacket = new EditedFramesPacket(pkt, this.pts, duration2, this.frameNo);
        this.curPkt++;
        if (this.curPkt >= this.buckets[this.curEdit].size()) {
            this.curEdit++;
            this.curPkt = 0;
        }
        this.frameNo++;
        this.pts += duration2;
        return editedFramesPacket;
    }

    /* loaded from: classes.dex */
    public static class EditedFramesPacket extends VirtualPacketWrapper {
        private double duration;
        private int frameNo;
        private double pts;

        public EditedFramesPacket(VirtualPacket src, double pts, double duration, int frameNo) {
            super(src);
            this.pts = pts;
            this.duration = duration;
            this.frameNo = frameNo;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.pts;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return this.duration;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return true;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return this.frameNo;
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.src.getCodecMeta();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }
}
