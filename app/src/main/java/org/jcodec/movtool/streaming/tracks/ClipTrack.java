package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class ClipTrack implements VirtualTrack {
    private boolean eof;
    private int from;
    private List<VirtualPacket> gop;
    private VirtualTrack src;
    private int startFrame;
    private double startPts;

    /* renamed from: to */
    private int f1562to;

    public ClipTrack(VirtualTrack src, int frameFrom, int frameTo) {
        if (frameTo <= frameFrom) {
            throw new IllegalArgumentException("Clipping negative or zero frames.");
        }
        this.src = src;
        this.from = frameFrom;
        this.f1562to = frameTo;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        if (this.eof) {
            return null;
        }
        if (this.gop == null) {
            this.gop = getGop(this.src, this.from);
            this.startPts = this.gop.get(0).getPts();
            this.startFrame = this.gop.get(0).getFrameNo();
        }
        VirtualPacket nextPacket = this.gop.size() > 0 ? this.gop.remove(0) : this.src.nextPacket();
        if (nextPacket == null || nextPacket.getFrameNo() >= this.f1562to) {
            this.eof = true;
            return null;
        }
        return new ClipPacket(nextPacket);
    }

    protected List<VirtualPacket> getGop(VirtualTrack src, int from) throws IOException {
        VirtualPacket nextPacket;
        List<VirtualPacket> result = new ArrayList<>();
        do {
            nextPacket = src.nextPacket();
            if (nextPacket != null) {
                if (nextPacket.isKeyframe()) {
                    result.clear();
                }
                result.add(nextPacket);
            }
            if (nextPacket == null) {
                break;
            }
        } while (nextPacket.getFrameNo() < from);
        return result;
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
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
    }

    /* loaded from: classes.dex */
    public class ClipPacket extends VirtualPacketWrapper {
        public ClipPacket(VirtualPacket src) {
            super(src);
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return super.getPts() - ClipTrack.this.startPts;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getFrameNo() {
            return super.getFrameNo() - ClipTrack.this.startFrame;
        }
    }
}
