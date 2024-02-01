package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.codecs.pcmdvd.PCMDVDDecoder;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.model.AudioBuffer;
import org.jcodec.containers.mp4.boxes.channel.Label;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class PCMDVDTrack implements VirtualTrack {
    private PCMDVDDecoder decoder = new PCMDVDDecoder();
    private AudioFormat format;
    private int nFrames;
    private VirtualPacket prevPkt;
    private VirtualTrack src;

    public PCMDVDTrack(VirtualTrack src) throws IOException {
        this.src = src;
        this.prevPkt = src.nextPacket();
        if (this.prevPkt != null) {
            AudioBuffer decodeFrame = this.decoder.decodeFrame(this.prevPkt.getData(), ByteBuffer.allocate(this.prevPkt.getData().remaining()));
            this.format = decodeFrame.getFormat();
            this.nFrames = decodeFrame.getNFrames();
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        if (this.prevPkt == null) {
            return null;
        }
        VirtualPacket ret = this.prevPkt;
        this.prevPkt = this.src.nextPacket();
        return new PCMDVDPkt(ret);
    }

    /* loaded from: classes.dex */
    private class PCMDVDPkt extends VirtualPacketWrapper {
        public PCMDVDPkt(VirtualPacket src) {
            super(src);
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            ByteBuffer data = super.getData();
            AudioBuffer decodeFrame = PCMDVDTrack.this.decoder.decodeFrame(data, data);
            return decodeFrame.getData();
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() throws IOException {
            return (PCMDVDTrack.this.nFrames * PCMDVDTrack.this.format.getChannels()) << 1;
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return new AudioCodecMeta(MP4Muxer.lookupFourcc(this.format), ByteBuffer.allocate(0), this.format, true, new Label[]{Label.Left, Label.Right});
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.format.getSampleRate();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
    }
}
