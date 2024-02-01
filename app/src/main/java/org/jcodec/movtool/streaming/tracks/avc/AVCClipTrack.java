package org.jcodec.movtool.streaming.tracks.avc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.encode.H264FixedRateControl;
import org.jcodec.codecs.h264.io.model.Frame;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.PictureParameterSet;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.io.model.SliceHeader;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VideoCodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;
import org.jcodec.movtool.streaming.tracks.ClipTrack;
import org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper;

/* loaded from: classes.dex */
public class AVCClipTrack extends ClipTrack {
    private AvcCBox avcC;
    private PictureParameterSet encPPS;
    private SeqParameterSet encSPS;
    private int frameSize;
    private int mbH;
    private int mbW;

    /* renamed from: rc */
    private H264FixedRateControl f1574rc;

    /* renamed from: se */
    private VideoCodecMeta f1575se;

    public AVCClipTrack(VirtualTrack src, int frameFrom, int frameTo) {
        super(src, frameFrom, frameTo);
        VideoCodecMeta codecMeta = (VideoCodecMeta) src.getCodecMeta();
        if (!"avc1".equals(codecMeta.getFourcc())) {
            throw new RuntimeException("Not an AVC source track");
        }
        this.f1574rc = new H264FixedRateControl(1024);
        H264Encoder encoder = new H264Encoder(this.f1574rc);
        this.avcC = H264Utils.parseAVCC(codecMeta.getCodecPrivate());
        SeqParameterSet sps = H264Utils.readSPS(NIOUtils.duplicate(this.avcC.getSpsList().get(0)));
        this.mbW = sps.pic_width_in_mbs_minus1 + 1;
        this.mbH = H264Utils.getPicHeightInMbs(sps);
        this.encSPS = encoder.initSPS(H264Utils.getPicSize(sps));
        this.encSPS.seq_parameter_set_id = 1;
        this.encPPS = encoder.initPPS();
        this.encPPS.seq_parameter_set_id = 1;
        this.encPPS.pic_parameter_set_id = 1;
        this.encSPS.profile_idc = sps.profile_idc;
        this.encSPS.level_idc = sps.level_idc;
        this.encSPS.frame_mbs_only_flag = sps.frame_mbs_only_flag;
        this.encSPS.frame_crop_bottom_offset = sps.frame_crop_bottom_offset;
        this.encSPS.frame_crop_left_offset = sps.frame_crop_left_offset;
        this.encSPS.frame_crop_right_offset = sps.frame_crop_right_offset;
        this.encSPS.frame_crop_top_offset = sps.frame_crop_top_offset;
        this.encSPS.vuiParams = sps.vuiParams;
        this.avcC.getSpsList().add(H264Utils.writeSPS(this.encSPS, 128));
        this.avcC.getPpsList().add(H264Utils.writePPS(this.encPPS, 20));
        this.f1575se = new VideoCodecMeta("avc1", H264Utils.getAvcCData(this.avcC), codecMeta.getSize(), codecMeta.getPasp());
        this.frameSize = this.f1574rc.calcFrameSize(this.mbW * this.mbH);
        this.frameSize += this.frameSize >> 4;
    }

    @Override // org.jcodec.movtool.streaming.tracks.ClipTrack
    protected List<VirtualPacket> getGop(VirtualTrack src, int from) throws IOException {
        VirtualPacket packet = src.nextPacket();
        List<VirtualPacket> head = new ArrayList<>();
        while (packet != null && packet.getFrameNo() < from) {
            if (packet.isKeyframe()) {
                head.clear();
            }
            head.add(packet);
            packet = src.nextPacket();
        }
        List<VirtualPacket> tail = new ArrayList<>();
        while (packet != null && !packet.isKeyframe()) {
            tail.add(packet);
            packet = src.nextPacket();
        }
        List<VirtualPacket> gop = new ArrayList<>();
        GopTranscoder tr = new GopTranscoder(head, tail);
        for (int i = 0; i < tail.size(); i++) {
            gop.add(new TranscodePacket(tail.get(i), tr, i));
        }
        gop.add(packet);
        return gop;
    }

    /* loaded from: classes.dex */
    public class GopTranscoder {
        private List<VirtualPacket> head;
        private List<ByteBuffer> result;
        private List<VirtualPacket> tail;

        public GopTranscoder(List<VirtualPacket> head, List<VirtualPacket> tail) {
            this.head = head;
            this.tail = tail;
        }

        public List<ByteBuffer> transcode() throws IOException {
            H264Decoder decoder = new H264Decoder();
            decoder.addSps(AVCClipTrack.this.avcC.getSpsList());
            decoder.addPps(AVCClipTrack.this.avcC.getPpsList());
            Picture buf = Picture.create(AVCClipTrack.this.mbW << 4, AVCClipTrack.this.mbH << 4, ColorSpace.YUV420);
            for (VirtualPacket virtualPacket : this.head) {
                decoder.decodeFrame(H264Utils.splitMOVPacket(virtualPacket.getData(), AVCClipTrack.this.avcC), buf.getData());
            }
            H264Encoder encoder = new H264Encoder(AVCClipTrack.this.f1574rc);
            ByteBuffer tmp = ByteBuffer.allocate(AVCClipTrack.this.frameSize);
            List<ByteBuffer> result = new ArrayList<>();
            for (VirtualPacket pkt : this.tail) {
                Frame dec = decoder.decodeFrame(H264Utils.splitMOVPacket(pkt.getData(), AVCClipTrack.this.avcC), buf.getData());
                tmp.clear();
                ByteBuffer res = encoder.encodeFrame(dec, tmp);
                ByteBuffer out = ByteBuffer.allocate(AVCClipTrack.this.frameSize);
                processFrame(res, out);
                result.add(out);
            }
            return result;
        }

        private void processFrame(ByteBuffer in, ByteBuffer out) {
            ByteBuffer buf;
            H264Utils.SliceHeaderTweaker st = new H264Utils.SliceHeaderTweaker() { // from class: org.jcodec.movtool.streaming.tracks.avc.AVCClipTrack.GopTranscoder.1
                @Override // org.jcodec.codecs.h264.H264Utils.SliceHeaderTweaker
                protected void tweak(SliceHeader sh) {
                    sh.pic_parameter_set_id = 1;
                }
            };
            ByteBuffer dup = in.duplicate();
            while (dup.hasRemaining() && (buf = H264Utils.nextNALUnit(dup)) != null) {
                NALUnit nu = NALUnit.read(buf);
                if (nu.type == NALUnitType.IDR_SLICE) {
                    ByteBuffer sp = out.duplicate();
                    out.putInt(0);
                    nu.write(out);
                    st.run(buf, out, nu, AVCClipTrack.this.encSPS, AVCClipTrack.this.encPPS);
                    sp.putInt((out.position() - sp.position()) - 4);
                }
            }
            if (out.remaining() >= 5) {
                out.putInt(out.remaining() - 4);
                new NALUnit(NALUnitType.FILLER_DATA, 0).write(out);
            }
            out.clear();
        }

        public synchronized List<ByteBuffer> getResult() throws IOException {
            if (this.result == null) {
                this.result = transcode();
            }
            return this.result;
        }
    }

    @Override // org.jcodec.movtool.streaming.tracks.ClipTrack, org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1575se;
    }

    /* loaded from: classes.dex */
    public class TranscodePacket extends VirtualPacketWrapper {
        private int off;

        /* renamed from: tr */
        private GopTranscoder f1576tr;

        public TranscodePacket(VirtualPacket src, GopTranscoder tr, int off) {
            super(src);
            this.f1576tr = tr;
            this.off = off;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            return NIOUtils.duplicate(this.f1576tr.getResult().get(this.off));
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() throws IOException {
            return AVCClipTrack.this.frameSize;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public boolean isKeyframe() {
            return true;
        }
    }
}
