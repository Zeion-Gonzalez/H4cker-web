package org.jcodec.api.specific;

import org.jcodec.api.FrameGrab;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.PixelAspectExt;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;

/* loaded from: classes.dex */
public class AVCMP4Adaptor implements ContainerAdaptor {
    private AvcCBox avcCBox;
    private int curENo;
    private H264Decoder decoder;
    private SampleEntry[] ses;
    private Size size;

    public AVCMP4Adaptor(SampleEntry[] ses) {
        this.ses = ses;
        this.curENo = -1;
        calcBufferSize();
    }

    private void calcBufferSize() {
        int w = Integer.MIN_VALUE;
        int h = Integer.MIN_VALUE;
        SampleEntry[] arr$ = this.ses;
        for (SampleEntry se : arr$) {
            if ("avc1".equals(se.getFourcc())) {
                AvcCBox avcC = H264Utils.parseAVCC((VideoSampleEntry) se);
                for (SeqParameterSet sps : H264Utils.readSPS(avcC.getSpsList())) {
                    int ww = sps.pic_width_in_mbs_minus1 + 1;
                    if (ww > w) {
                        w = ww;
                    }
                    int hh = H264Utils.getPicHeightInMbs(sps);
                    if (hh > h) {
                        h = hh;
                    }
                }
            }
        }
        this.size = new Size(w << 4, h << 4);
    }

    public AVCMP4Adaptor(AbstractMP4DemuxerTrack vt) {
        this(vt.getSampleEntries());
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public Picture decodeFrame(Packet packet, int[][] data) {
        updateState(packet);
        Picture pic = this.decoder.decodeFrame(H264Utils.splitMOVPacket(packet.getData(), this.avcCBox), data);
        PixelAspectExt pasp = (PixelAspectExt) Box.findFirst(this.ses[this.curENo], PixelAspectExt.class, "pasp");
        if (pasp != null) {
        }
        return pic;
    }

    private void updateState(Packet packet) {
        int eNo = ((MP4Packet) packet).getEntryNo();
        if (eNo != this.curENo) {
            this.curENo = eNo;
            this.avcCBox = H264Utils.parseAVCC((VideoSampleEntry) this.ses[this.curENo]);
            this.decoder = new H264Decoder();
            this.decoder.addSps(this.avcCBox.getSpsList());
            this.decoder.addPps(this.avcCBox.getPpsList());
        }
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public boolean canSeek(Packet pkt) {
        updateState(pkt);
        return H264Utils.idrSlice(H264Utils.splitMOVPacket(pkt.getData(), this.avcCBox));
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public int[][] allocatePicture() {
        return Picture.create(this.size.getWidth(), this.size.getHeight(), ColorSpace.YUV444).getData();
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public FrameGrab.MediaInfo getMediaInfo() {
        return new FrameGrab.MediaInfo(this.size);
    }
}
