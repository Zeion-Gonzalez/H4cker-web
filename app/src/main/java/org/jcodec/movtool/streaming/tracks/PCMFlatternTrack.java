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
public class PCMFlatternTrack implements VirtualTrack {
    private static final VirtualPacket[] EMPTY = new VirtualPacket[0];
    private int dataLen;
    private int frameNo;
    private int framesPerPkt;
    private VirtualPacket leftover;
    private int leftoverOffset;
    private double packetDur;
    private List<VirtualPacket> pktBuffer = new ArrayList();

    /* renamed from: se */
    private AudioCodecMeta f1568se;
    private VirtualTrack src;

    public PCMFlatternTrack(VirtualTrack src, int samplesPerPkt) {
        this.framesPerPkt = samplesPerPkt;
        this.src = src;
        this.f1568se = (AudioCodecMeta) src.getCodecMeta();
        this.dataLen = this.f1568se.getFrameSize() * this.framesPerPkt;
        this.packetDur = this.framesPerPkt / this.f1568se.getSampleRate();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        this.pktBuffer.clear();
        VirtualPacket pkt = this.leftover == null ? this.src.nextPacket() : this.leftover;
        if (pkt == null) {
            return null;
        }
        int rem = this.dataLen + this.leftoverOffset;
        do {
            this.pktBuffer.add(pkt);
            rem -= pkt.getDataLen();
            if (rem > 0) {
                pkt = this.src.nextPacket();
            }
            if (rem <= 0) {
                break;
            }
        } while (pkt != null);
        FlatternPacket flatternPacket = new FlatternPacket(this.frameNo, (VirtualPacket[]) this.pktBuffer.toArray(EMPTY), this.leftoverOffset, this.dataLen - Math.max(rem, 0));
        this.frameNo += this.framesPerPkt;
        if (rem < 0) {
            this.leftover = this.pktBuffer.get(this.pktBuffer.size() - 1);
            this.leftoverOffset = this.leftover.getDataLen() + rem;
            return flatternPacket;
        }
        this.leftover = null;
        this.leftoverOffset = 0;
        return flatternPacket;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1568se;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
    }

    /* loaded from: classes.dex */
    private class FlatternPacket implements VirtualPacket {
        private int dataLen;
        private int frameNo;
        private int leading;

        /* renamed from: pN */
        private VirtualPacket[] f1569pN;

        public FlatternPacket(int frameNo, VirtualPacket[] pN, int lead, int dataLen) {
            this.frameNo = frameNo;
            this.leading = lead;
            this.f1569pN = pN;
            this.dataLen = dataLen;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            ByteBuffer result = ByteBuffer.allocate(this.dataLen);
            ByteBuffer d0 = this.f1569pN[0].getData();
            NIOUtils.skip(d0, this.leading);
            NIOUtils.write(result, d0);
            for (int i = 1; i < this.f1569pN.length && result.hasRemaining(); i++) {
                ByteBuffer dN = this.f1569pN[i].getData();
                int toWrite = Math.min(dN.remaining(), result.remaining());
                NIOUtils.write(result, dN, toWrite);
            }
            result.flip();
            return result;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return this.dataLen;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return (this.frameNo * PCMFlatternTrack.this.framesPerPkt) / PCMFlatternTrack.this.f1568se.getSampleRate();
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return PCMFlatternTrack.this.packetDur;
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
    public VirtualTrack.VirtualEdit[] getEdits() {
        return this.src.getEdits();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }
}
