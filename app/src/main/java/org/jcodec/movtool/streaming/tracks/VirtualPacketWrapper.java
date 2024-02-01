package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.movtool.streaming.VirtualPacket;

/* loaded from: classes.dex */
public class VirtualPacketWrapper implements VirtualPacket {
    protected VirtualPacket src;

    public VirtualPacketWrapper(VirtualPacket src) {
        this.src = src;
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public ByteBuffer getData() throws IOException {
        return this.src.getData();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public int getDataLen() throws IOException {
        return this.src.getDataLen();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public double getPts() {
        return this.src.getPts();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public double getDuration() {
        return this.src.getDuration();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public boolean isKeyframe() {
        return this.src.isKeyframe();
    }

    @Override // org.jcodec.movtool.streaming.VirtualPacket
    public int getFrameNo() {
        return this.src.getFrameNo();
    }
}
