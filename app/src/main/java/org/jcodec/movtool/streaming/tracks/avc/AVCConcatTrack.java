package org.jcodec.movtool.streaming.tracks.avc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Rational;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class AVCConcatTrack implements VirtualTrack {
    private AvcCBox[] avcCs;
    private VirtualPacket lastPacket;
    private Map<Integer, Integer> map;

    /* renamed from: se */
    private CodecMeta f1577se;
    private VirtualTrack[] tracks;
    private H264Utils.SliceHeaderTweaker[] tweakers;
    private int idx = 0;
    private double offsetPts = 0.0d;
    private int offsetFn = 0;
    private List<PictureParameterSet> allPps = new ArrayList();
    private List<SeqParameterSet> allSps = new ArrayList();

    public AVCConcatTrack(VirtualTrack... tracks) {
        this.tracks = tracks;
        this.avcCs = new AvcCBox[tracks.length];
        Rational pasp = null;
        this.tweakers = new H264Utils.SliceHeaderTweaker[tracks.length];
        int nalLengthSize = 0;
        for (int i = 0; i < tracks.length; i++) {
            CodecMeta se = tracks[i].getCodecMeta();
            if (!(se instanceof VideoCodecMeta)) {
                throw new RuntimeException("Not a video track.");
            }
            if (!"avc1".equals(se.getFourcc())) {
                throw new RuntimeException("Not an AVC track.");
            }
            VideoCodecMeta vcm = (VideoCodecMeta) se;
            Rational paspL = vcm.getPasp();
            if (pasp != null && paspL != null && !pasp.equals(paspL)) {
                throw new RuntimeException("Can not concat video tracks with different Pixel Aspect Ratio.");
            }
            pasp = paspL;
            AvcCBox avcC = H264Utils.parseAVCC(vcm.getCodecPrivate());
            if (nalLengthSize == 0) {
                nalLengthSize = avcC.getNalLengthSize();
            } else if (nalLengthSize != avcC.getNalLengthSize()) {
                throw new RuntimeException("Unable to concat AVC tracks with different NAL length size in AvcC box");
            }
            for (ByteBuffer ppsBuffer : avcC.getPpsList()) {
                PictureParameterSet pps = H264Utils.readPPS(NIOUtils.duplicate(ppsBuffer));
                pps.pic_parameter_set_id |= i << 8;
                pps.seq_parameter_set_id |= i << 8;
                this.allPps.add(pps);
            }
            for (ByteBuffer spsBuffer : avcC.getSpsList()) {
                SeqParameterSet sps = H264Utils.readSPS(NIOUtils.duplicate(spsBuffer));
                sps.seq_parameter_set_id |= i << 8;
                this.allSps.add(sps);
            }
            final int idx2 = i;
            this.tweakers[i] = new H264Utils.SliceHeaderTweaker(avcC.getSpsList(), avcC.getPpsList()) { // from class: org.jcodec.movtool.streaming.tracks.avc.AVCConcatTrack.1
                @Override // org.jcodec.codecs.h264.H264Utils.SliceHeaderTweaker
                protected void tweak(SliceHeader sh) {
                    sh.pic_parameter_set_id = ((Integer) AVCConcatTrack.this.map.get(Integer.valueOf((idx2 << 8) | sh.pic_parameter_set_id))).intValue();
                }
            };
            this.avcCs[i] = avcC;
        }
        this.map = mergePS(this.allSps, this.allPps);
        VideoCodecMeta codecMeta = (VideoCodecMeta) tracks[0].getCodecMeta();
        AvcCBox createAvcC = H264Utils.createAvcC(this.allSps, this.allPps, nalLengthSize);
        this.f1577se = new VideoCodecMeta("avc1", H264Utils.getAvcCData(createAvcC), codecMeta.getSize(), codecMeta.getPasp());
    }

    private Map<Integer, Integer> mergePS(List<SeqParameterSet> allSps, List<PictureParameterSet> allPps) {
        List<ByteBuffer> spsRef = new ArrayList<>();
        for (SeqParameterSet sps : allSps) {
            int spsId = sps.seq_parameter_set_id;
            sps.seq_parameter_set_id = 0;
            ByteBuffer serial = H264Utils.writeSPS(sps, 32);
            int idx = NIOUtils.find(spsRef, serial);
            if (idx == -1) {
                idx = spsRef.size();
                spsRef.add(serial);
            }
            for (PictureParameterSet pps : allPps) {
                if (pps.seq_parameter_set_id == spsId) {
                    pps.seq_parameter_set_id = idx;
                }
            }
        }
        Map<Integer, Integer> map = new HashMap<>();
        List<ByteBuffer> ppsRef = new ArrayList<>();
        for (PictureParameterSet pps2 : allPps) {
            int ppsId = pps2.pic_parameter_set_id;
            pps2.pic_parameter_set_id = 0;
            ByteBuffer serial2 = H264Utils.writePPS(pps2, 128);
            int idx2 = NIOUtils.find(ppsRef, serial2);
            if (idx2 == -1) {
                idx2 = ppsRef.size();
                ppsRef.add(serial2);
            }
            map.put(Integer.valueOf(ppsId), Integer.valueOf(idx2));
        }
        allSps.clear();
        for (int i = 0; i < spsRef.size(); i++) {
            SeqParameterSet sps2 = H264Utils.readSPS(spsRef.get(i));
            sps2.seq_parameter_set_id = i;
            allSps.add(sps2);
        }
        allPps.clear();
        for (int i2 = 0; i2 < ppsRef.size(); i2++) {
            PictureParameterSet pps3 = H264Utils.readPPS(ppsRef.get(i2));
            pps3.pic_parameter_set_id = i2;
            allPps.add(pps3);
        }
        return map;
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
                return new AVCConcatPacket(nextPacket, this.offsetPts, this.offsetFn, this.idx);
            }
        }
        return null;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1577se;
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

    /* JADX INFO: Access modifiers changed from: private */
    public ByteBuffer patchPacket(int idx2, ByteBuffer data) {
        ByteBuffer out = ByteBuffer.allocate(data.remaining() + 8);
        for (ByteBuffer nal : H264Utils.splitMOVPacket(data, this.avcCs[idx2])) {
            NALUnit nu = NALUnit.read(nal);
            if (nu.type == NALUnitType.IDR_SLICE || nu.type == NALUnitType.NON_IDR_SLICE) {
                ByteBuffer nalSizePosition = out.duplicate();
                out.putInt(0);
                nu.write(out);
                this.tweakers[idx2].run(nal, out, nu);
                nalSizePosition.putInt((out.position() - nalSizePosition.position()) - 4);
            } else {
                Logger.warn("Skipping NAL unit: " + nu.type);
            }
        }
        if (out.remaining() >= 5) {
            int nalLengthSize = this.avcCs[idx2].getNalLengthSize();
            int size = out.remaining() - nalLengthSize;
            if (nalLengthSize == 4) {
                out.putInt(size);
            } else if (nalLengthSize == 2) {
                out.putShort((short) size);
            } else if (nalLengthSize == 3) {
                out.put((byte) (size >> 16));
                out.putShort((short) (65535 & size));
            }
            new NALUnit(NALUnitType.FILLER_DATA, 0).write(out);
        }
        out.clear();
        return out;
    }

    /* loaded from: classes.dex */
    public class AVCConcatPacket implements VirtualPacket {
        private int fnOffset;
        private int idx;
        private VirtualPacket packet;
        private double ptsOffset;

        public AVCConcatPacket(VirtualPacket packet, double ptsOffset, int fnOffset, int idx) {
            this.packet = packet;
            this.ptsOffset = ptsOffset;
            this.fnOffset = fnOffset;
            this.idx = idx;
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            return AVCConcatTrack.this.patchPacket(this.idx, this.packet.getData());
        }

        @Override // org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() throws IOException {
            return this.packet.getDataLen() + 8;
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
}
