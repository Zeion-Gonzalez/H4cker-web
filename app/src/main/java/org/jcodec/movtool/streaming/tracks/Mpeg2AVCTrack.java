package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.encode.H264FixedRateControl;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.mpeg12.MPEGUtil;
import org.jcodec.codecs.mpeg12.bitstream.PictureHeader;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.Size;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class Mpeg2AVCTrack implements VirtualTrack {
    public static final int TARGET_RATE = 1024;
    private int frameSize;
    private GOP gop;
    int mbH;
    int mbW;
    private VirtualPacket nextPacket;
    private GOP prevGop;
    int scaleFactor;

    /* renamed from: se */
    private CodecMeta f1567se;
    protected VirtualTrack src;
    int thumbHeight;
    int thumbWidth;
    private ThreadLocal<MPEGToAVCTranscoder> transcoders = new ThreadLocal<>();

    protected void checkFourCC(VirtualTrack srcTrack) {
        String fourcc = srcTrack.getCodecMeta().getFourcc();
        if (!"m2v1".equals(fourcc)) {
            throw new IllegalArgumentException("Input track is not ProRes");
        }
    }

    protected int selectScaleFactor(Size frameDim) {
        if (frameDim.getWidth() >= 960) {
            return 2;
        }
        return frameDim.getWidth() > 480 ? 1 : 0;
    }

    public Mpeg2AVCTrack(VirtualTrack src) throws IOException {
        checkFourCC(src);
        this.src = src;
        H264FixedRateControl rc = new H264FixedRateControl(1024);
        H264Encoder encoder = new H264Encoder(rc);
        this.nextPacket = src.nextPacket();
        Size frameDim = MPEGDecoder.getSize(this.nextPacket.getData());
        this.scaleFactor = selectScaleFactor(frameDim);
        this.thumbWidth = frameDim.getWidth() >> this.scaleFactor;
        this.thumbHeight = (frameDim.getHeight() >> this.scaleFactor) & (-2);
        this.mbW = (this.thumbWidth + 15) >> 4;
        this.mbH = (this.thumbHeight + 15) >> 4;
        this.f1567se = Transcode2AVCTrack.createCodecMeta(src, encoder, this.thumbWidth, this.thumbHeight);
        this.frameSize = rc.calcFrameSize(this.mbW * this.mbH);
        this.frameSize += this.frameSize >> 4;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.f1567se;
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        if (this.nextPacket == null) {
            return null;
        }
        if (this.nextPacket.isKeyframe()) {
            this.prevGop = this.gop;
            this.gop = new GOP(this.nextPacket.getFrameNo(), this.prevGop);
            if (this.prevGop != null) {
                this.prevGop.setNextGop(this.gop);
            }
        }
        VirtualPacket addPacket = this.gop.addPacket(this.nextPacket);
        this.nextPacket = this.src.nextPacket();
        return addPacket;
    }

    /* loaded from: classes.dex */
    private class GOP {
        private ByteBuffer[] data;
        private int frameNo;
        private List<ByteBuffer> leadingB;
        private GOP nextGop;
        private List<VirtualPacket> packets = new ArrayList();
        private GOP prevGop;

        public GOP(int frameNo, GOP prevGop) {
            this.frameNo = frameNo;
            this.prevGop = prevGop;
        }

        public void setNextGop(GOP gop) {
            this.nextGop = gop;
        }

        public VirtualPacket addPacket(VirtualPacket pkt) {
            this.packets.add(pkt);
            return new TranscodePacket(pkt, this, this.packets.size() - 1);
        }

        private synchronized void transcode() throws IOException {
            int i;
            if (this.data == null) {
                this.data = new ByteBuffer[this.packets.size()];
                loop0: for (int tr = 0; tr < 2; tr++) {
                    try {
                        MPEGToAVCTranscoder t = (MPEGToAVCTranscoder) Mpeg2AVCTrack.this.transcoders.get();
                        if (t == null) {
                            t = Mpeg2AVCTrack.this.createTranscoder(Mpeg2AVCTrack.this.scaleFactor);
                            Mpeg2AVCTrack.this.transcoders.set(t);
                        }
                        carryLeadingBOver();
                        double[] pts = collectPts(this.packets);
                        int numRefs = 0;
                        while (i < this.packets.size()) {
                            VirtualPacket pkt = this.packets.get(i);
                            ByteBuffer pktData = pkt.getData();
                            int picType = Mpeg2AVCTrack.getPicType(pktData.duplicate());
                            if (picType == 3) {
                                i = numRefs < 2 ? i + 1 : 0;
                            } else {
                                numRefs++;
                            }
                            ByteBuffer buf = ByteBuffer.allocate(Mpeg2AVCTrack.this.frameSize);
                            this.data[i] = t.transcodeFrame(pktData, buf, i == 0, Arrays.binarySearch(pts, pkt.getPts()));
                        }
                        if (this.nextGop == null) {
                            break;
                        }
                        this.nextGop.leadingB = new ArrayList();
                        double[] pts2 = collectPts(this.nextGop.packets);
                        int numRefs2 = 0;
                        int i2 = 0;
                        while (i2 < this.nextGop.packets.size()) {
                            VirtualPacket pkt2 = this.nextGop.packets.get(i2);
                            ByteBuffer pktData2 = pkt2.getData();
                            int picType2 = Mpeg2AVCTrack.getPicType(pktData2.duplicate());
                            if (picType2 != 3) {
                                numRefs2++;
                            }
                            if (numRefs2 >= 2) {
                                break loop0;
                            }
                            ByteBuffer buf2 = ByteBuffer.allocate(Mpeg2AVCTrack.this.frameSize);
                            this.nextGop.leadingB.add(t.transcodeFrame(pktData2, buf2, i2 == 0, Arrays.binarySearch(pts2, pkt2.getPts())));
                            i2++;
                        }
                        break loop0;
                    } catch (Throwable t2) {
                        Logger.error("Error transcoding gop: " + t2.getMessage() + ", retrying.");
                    }
                }
            }
        }

        private double[] collectPts(List<VirtualPacket> packets2) {
            double[] pts = new double[packets2.size()];
            for (int i = 0; i < pts.length; i++) {
                pts[i] = packets2.get(i).getPts();
            }
            Arrays.sort(pts);
            return pts;
        }

        private synchronized void carryLeadingBOver() {
            if (this.leadingB != null) {
                for (int i = 0; i < this.leadingB.size(); i++) {
                    this.data[i] = this.leadingB.get(i);
                }
            }
        }

        public ByteBuffer getData(int i) throws IOException {
            transcode();
            if (this.data[i] == null && this.prevGop != null) {
                this.prevGop.transcode();
                carryLeadingBOver();
            }
            return this.data[i];
        }
    }

    protected MPEGToAVCTranscoder createTranscoder(int scaleFactor) {
        return new MPEGToAVCTranscoder(scaleFactor);
    }

    public static int getPicType(ByteBuffer buf) {
        ByteBuffer segment;
        int code;
        do {
            segment = MPEGUtil.nextSegment(buf);
            if (segment == null) {
                return -1;
            }
            code = segment.getInt() & 255;
        } while (code != 0);
        PictureHeader ph = PictureHeader.read(segment);
        return ph.picture_coding_type;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TranscodePacket extends VirtualPacketWrapper {
        private GOP gop;
        private int index;

        public TranscodePacket(VirtualPacket nextPacket, GOP gop, int index) {
            super(nextPacket);
            this.gop = gop;
            this.index = index;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public int getDataLen() {
            return Mpeg2AVCTrack.this.frameSize;
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public ByteBuffer getData() throws IOException {
            return this.gop.getData(this.index);
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        this.src.close();
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
