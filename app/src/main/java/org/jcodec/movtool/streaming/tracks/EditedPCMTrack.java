package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class EditedPCMTrack implements VirtualTrack {
    private List<VirtualPacket>[] buckets;
    private int curEdit;
    private int curPkt;
    private VirtualTrack.VirtualEdit[] edits;
    private int frameNo;
    private int frameSize;
    private double pts;
    private float sampleRate;
    private VirtualTrack src;

    public EditedPCMTrack(VirtualTrack src) throws IOException {
        this.src = src;
        this.edits = src.getEdits();
        this.buckets = new List[this.edits.length];
        for (int i = 0; i < this.edits.length; i++) {
            this.buckets[i] = new ArrayList();
        }
        while (true) {
            VirtualPacket pkt = src.nextPacket();
            if (pkt != null) {
                for (int e = 0; e < this.edits.length; e++) {
                    VirtualTrack.VirtualEdit ed = this.edits[e];
                    if (pkt.getPts() < ed.getIn() + ed.getDuration() && pkt.getPts() + pkt.getDuration() > ed.getIn()) {
                        this.buckets[e].add(pkt);
                    }
                }
            } else {
                AudioCodecMeta ase = (AudioCodecMeta) src.getCodecMeta();
                this.frameSize = ase.getFrameSize();
                this.sampleRate = ase.getSampleRate();
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
        double start = Math.max(pkt.getPts(), edt.getIn());
        double end = Math.min(pkt.getPts() + pkt.getDuration(), edt.getIn() + edt.getDuration());
        double duration = end - start;
        double lead = start - pkt.getPts();
        EditedPCMPacket editedPCMPacket = new EditedPCMPacket(pkt, (int) (Math.round(this.sampleRate * lead) * this.frameSize), (int) (Math.round(this.sampleRate * duration) * this.frameSize), this.pts, duration, this.frameNo);
        this.curPkt++;
        if (this.curPkt >= this.buckets[this.curEdit].size()) {
            this.curEdit++;
            this.curPkt = 0;
        }
        this.frameNo++;
        this.pts += duration;
        return editedPCMPacket;
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

    /* loaded from: classes.dex */
    public static class EditedPCMPacket implements VirtualPacket {
        private int dataLen;
        private double duration;
        private int frameNo;
        private int inBytes;
        private double pts;
        private VirtualPacket src;

        public EditedPCMPacket(VirtualPacket src, int inBytes, int dataLen, double pts, double duration, int frameNo) {
            this.src = src;
            this.inBytes = inBytes;
            this.dataLen = dataLen;
            this.pts = pts;
            this.duration = duration;
            this.frameNo = frameNo;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            ByteBuffer data = this.src.getData();
            NIOUtils.skip(data, this.inBytes);
            return NIOUtils.read(data, this.dataLen);
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return this.dataLen;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.pts;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return this.duration;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return true;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return this.frameNo;
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }
}
