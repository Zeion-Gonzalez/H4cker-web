package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.AudioFormat;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.channel.Label;
import org.jcodec.movtool.streaming.AudioCodecMeta;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class StereoDownmixTrack implements VirtualTrack {
    private static final int FRAMES_IN_OUT_PACKET = 20480;
    private DownmixHelper downmix;
    private int frameNo;
    private int rate;
    private AudioCodecMeta[] sampleEntries;
    private boolean[][] solo;
    private VirtualTrack[] sources;

    public StereoDownmixTrack(VirtualTrack... tracks) {
        this.rate = -1;
        this.sources = new VirtualTrack[tracks.length];
        this.sampleEntries = new AudioCodecMeta[this.sources.length];
        this.solo = new boolean[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            CodecMeta se = tracks[i].getCodecMeta();
            if (!(se instanceof AudioCodecMeta)) {
                throw new IllegalArgumentException("Non audio track");
            }
            AudioCodecMeta ase = (AudioCodecMeta) se;
            if (!ase.isPCM()) {
                throw new IllegalArgumentException("Non PCM audio track.");
            }
            AudioFormat format = ase.getFormat();
            if (this.rate != -1 && this.rate != format.getFrameRate()) {
                throw new IllegalArgumentException("Can not downmix tracks of different rate.");
            }
            this.rate = format.getFrameRate();
            this.sampleEntries[i] = ase;
            this.sources[i] = new PCMFlatternTrack(tracks[i], FRAMES_IN_OUT_PACKET);
            this.solo[i] = new boolean[format.getChannels()];
        }
        this.downmix = new DownmixHelper(this.sampleEntries, FRAMES_IN_OUT_PACKET, null);
    }

    public void soloTrack(int track, boolean s) {
        for (int ch = 0; ch < this.solo[track].length; ch++) {
            this.solo[track][ch] = s;
        }
        this.downmix = new DownmixHelper(this.sampleEntries, FRAMES_IN_OUT_PACKET, this.solo);
    }

    public void soloChannel(int track, int channel, boolean s) {
        this.solo[track][channel] = s;
        this.downmix = new DownmixHelper(this.sampleEntries, FRAMES_IN_OUT_PACKET, this.solo);
    }

    public boolean isChannelMute(int track, int channel) {
        return this.solo[track][channel];
    }

    public boolean[][] bulkGetSolo() {
        return this.solo;
    }

    public void soloAll() {
        for (int i = 0; i < this.solo.length; i++) {
            for (int j = 0; j < this.solo[i].length; j++) {
                this.solo[i][j] = true;
            }
        }
    }

    public void muteAll() {
        for (int i = 0; i < this.solo.length; i++) {
            for (int j = 0; j < this.solo[i].length; j++) {
                this.solo[i][j] = false;
            }
        }
    }

    public void bulkSetSolo(boolean[][] solo) {
        this.solo = solo;
        this.downmix = new DownmixHelper(this.sampleEntries, FRAMES_IN_OUT_PACKET, solo);
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        VirtualPacket[] packets = new VirtualPacket[this.sources.length];
        boolean allNull = true;
        for (int i = 0; i < packets.length; i++) {
            packets[i] = this.sources[i].nextPacket();
            allNull &= packets[i] == null;
        }
        if (allNull) {
            return null;
        }
        DownmixVirtualPacket downmixVirtualPacket = new DownmixVirtualPacket(packets, this.frameNo);
        this.frameNo += FRAMES_IN_OUT_PACKET;
        return downmixVirtualPacket;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return new AudioCodecMeta("sowt", 2, 2, this.rate, EndianBox.Endian.LITTLE_ENDIAN, true, new Label[]{Label.Left, Label.Right}, null);
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        VirtualTrack[] arr$ = this.sources;
        for (VirtualTrack virtualTrack : arr$) {
            virtualTrack.close();
        }
    }

    /* loaded from: classes.dex */
    protected class DownmixVirtualPacket implements VirtualPacket {
        private int frameNo;
        private VirtualPacket[] packets;

        public DownmixVirtualPacket(VirtualPacket[] packets, int pktNo) {
            this.packets = packets;
            this.frameNo = pktNo;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            ByteBuffer[] data = new ByteBuffer[this.packets.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = this.packets[i] == null ? null : this.packets[i].getData();
            }
            ByteBuffer out = ByteBuffer.allocate(81920);
            StereoDownmixTrack.this.downmix.downmix(data, out);
            return out;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return 81920;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getPts() {
            return this.frameNo / StereoDownmixTrack.this.rate;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public double getDuration() {
            return 20480.0d / StereoDownmixTrack.this.rate;
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
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.rate;
    }
}
