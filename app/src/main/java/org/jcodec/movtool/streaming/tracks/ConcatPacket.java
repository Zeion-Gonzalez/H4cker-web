package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.movtool.streaming.VirtualPacket;

/* loaded from: classes.dex */
public class ConcatPacket implements VirtualPacket {
    private int fnOffset;
    private VirtualPacket packet;
    private double ptsOffset;

    public ConcatPacket(VirtualPacket packet, double ptsOffset, int fnOffset) {
        this.packet = packet;
        this.ptsOffset = ptsOffset;
        this.fnOffset = fnOffset;
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public ByteBuffer getData() throws IOException {
        return this.packet.getData();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public int getDataLen() throws IOException {
        return this.packet.getDataLen();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public double getPts() {
        return this.ptsOffset + this.packet.getPts();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public double getDuration() {
        return this.packet.getDuration();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public boolean isKeyframe() {
        return this.packet.isKeyframe();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public int getFrameNo() {
        return this.fnOffset + this.packet.getFrameNo();
    }
}
