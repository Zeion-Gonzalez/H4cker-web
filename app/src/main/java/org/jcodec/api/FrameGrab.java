package org.jcodec.api;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.api.specific.AVCMP4Adaptor;
import org.jcodec.api.specific.ContainerAdaptor;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.prores.ProresDecoder;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;

/* loaded from: classes.dex */
public class FrameGrab {
    private ThreadLocal<int[][]> buffers = new ThreadLocal<>();
    private ContainerAdaptor decoder;
    private DemuxerTrack videoTrack;

    /* loaded from: classes.dex */
    public static class MediaInfo {
        private Size dim;

        public MediaInfo(Size dim) {
            this.dim = dim;
        }

        public Size getDim() {
            return this.dim;
        }

        public void setDim(Size dim) {
            this.dim = dim;
        }
    }

    public FrameGrab(SeekableByteChannel in) throws IOException, JCodecException {
        ByteBuffer header = ByteBuffer.allocate(65536);
        in.read(header);
        header.flip();
        JCodecUtil.Format detectFormat = JCodecUtil.detectFormat(header);
        switch (detectFormat) {
            case MOV:
                MP4Demuxer d1 = new MP4Demuxer(in);
                this.videoTrack = d1.getVideoTrack();
                decodeLeadingFrames();
                return;
            case MPEG_PS:
                throw new UnsupportedFormatException("MPEG PS is temporarily unsupported.");
            case MPEG_TS:
                throw new UnsupportedFormatException("MPEG TS is temporarily unsupported.");
            default:
                throw new UnsupportedFormatException("Container format is not supported by JCodec");
        }
    }

    public FrameGrab(SeekableDemuxerTrack videoTrack, ContainerAdaptor decoder) {
        this.videoTrack = videoTrack;
        this.decoder = decoder;
    }

    private SeekableDemuxerTrack sdt() throws JCodecException {
        if (!(this.videoTrack instanceof SeekableDemuxerTrack)) {
            throw new JCodecException("Not a seekable track");
        }
        return (SeekableDemuxerTrack) this.videoTrack;
    }

    public FrameGrab seekToSecondPrecise(double second) throws IOException, JCodecException {
        sdt().seek(second);
        decodeLeadingFrames();
        return this;
    }

    public FrameGrab seekToFramePrecise(int frameNumber) throws IOException, JCodecException {
        sdt().gotoFrame(frameNumber);
        decodeLeadingFrames();
        return this;
    }

    public FrameGrab seekToSecondSloppy(double second) throws IOException, JCodecException {
        sdt().seek(second);
        goToPrevKeyframe();
        return this;
    }

    public FrameGrab seekToFrameSloppy(int frameNumber) throws IOException, JCodecException {
        sdt().gotoFrame(frameNumber);
        goToPrevKeyframe();
        return this;
    }

    private void goToPrevKeyframe() throws IOException, JCodecException {
        sdt().gotoFrame(detectKeyFrame((int) sdt().getCurFrame()));
    }

    private void decodeLeadingFrames() throws IOException, JCodecException {
        SeekableDemuxerTrack sdt = sdt();
        int curFrame = (int) sdt.getCurFrame();
        int keyFrame = detectKeyFrame(curFrame);
        sdt.gotoFrame(keyFrame);
        Packet frame = sdt.nextFrame();
        this.decoder = detectDecoder(sdt, frame);
        while (frame.getFrameNo() < curFrame) {
            this.decoder.decodeFrame(frame, getBuffer());
            frame = sdt.nextFrame();
        }
        sdt.gotoFrame(curFrame);
    }

    private int[][] getBuffer() {
        int[][] buf = this.buffers.get();
        if (buf == null) {
            int[][] buf2 = this.decoder.allocatePicture();
            this.buffers.set(buf2);
            return buf2;
        }
        return buf;
    }

    private int detectKeyFrame(int start) throws IOException {
        int[] seekFrames = this.videoTrack.getMeta().getSeekFrames();
        if (seekFrames != null) {
            int prev = seekFrames[0];
            for (int i = 1; i < seekFrames.length && seekFrames[i] <= start; i++) {
                prev = seekFrames[i];
            }
            return prev;
        }
        return start;
    }

    private ContainerAdaptor detectDecoder(SeekableDemuxerTrack videoTrack, Packet frame) throws JCodecException {
        if (videoTrack instanceof AbstractMP4DemuxerTrack) {
            SampleEntry se = ((AbstractMP4DemuxerTrack) videoTrack).getSampleEntries()[((MP4Packet) frame).getEntryNo()];
            VideoDecoder byFourcc = byFourcc(se.getHeader().getFourcc());
            if (byFourcc instanceof H264Decoder) {
                return new AVCMP4Adaptor(((AbstractMP4DemuxerTrack) videoTrack).getSampleEntries());
            }
        }
        throw new UnsupportedFormatException("Codec is not supported");
    }

    private VideoDecoder byFourcc(String fourcc) {
        if (fourcc.equals("avc1")) {
            return new H264Decoder();
        }
        if (fourcc.equals("m1v1") || fourcc.equals("m2v1")) {
            return new MPEGDecoder();
        }
        if (fourcc.equals("apco") || fourcc.equals("apcs") || fourcc.equals("apcn") || fourcc.equals("apch") || fourcc.equals("ap4h")) {
            return new ProresDecoder();
        }
        return null;
    }

    public Picture getNativeFrame() throws IOException {
        Packet frame = this.videoTrack.nextFrame();
        if (frame == null) {
            return null;
        }
        return this.decoder.decodeFrame(frame, getBuffer());
    }

    public static Picture getNativeFrame(File file, double second) throws IOException, JCodecException {
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(file);
            return new FrameGrab(ch).seekToSecondPrecise(second).getNativeFrame();
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static Picture getNativeFrame(SeekableByteChannel file, double second) throws JCodecException, IOException {
        return new FrameGrab(file).seekToSecondPrecise(second).getNativeFrame();
    }

    public static Picture getNativeFrame(File file, int frameNumber) throws IOException, JCodecException {
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(file);
            return new FrameGrab(ch).seekToFramePrecise(frameNumber).getNativeFrame();
        } finally {
            NIOUtils.closeQuietly(ch);
        }
    }

    public static Picture getNativeFrame(SeekableByteChannel file, int frameNumber) throws JCodecException, IOException {
        return new FrameGrab(file).seekToFramePrecise(frameNumber).getNativeFrame();
    }

    public static Picture getNativeFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber) throws IOException, JCodecException {
        return new FrameGrab(vt, decoder).seekToFramePrecise(frameNumber).getNativeFrame();
    }

    public static Picture getNativeFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second) throws IOException, JCodecException {
        return new FrameGrab(vt, decoder).seekToSecondPrecise(second).getNativeFrame();
    }

    public static Picture getNativeFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber) throws IOException, JCodecException {
        return new FrameGrab(vt, decoder).seekToFrameSloppy(frameNumber).getNativeFrame();
    }

    public static Picture getNativeFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second) throws IOException, JCodecException {
        return new FrameGrab(vt, decoder).seekToSecondSloppy(second).getNativeFrame();
    }

    public MediaInfo getMediaInfo() {
        return this.decoder.getMediaInfo();
    }
}
