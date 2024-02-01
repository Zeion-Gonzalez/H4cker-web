package org.jcodec.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.IOUtils;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.LeafBox;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;

/* loaded from: classes.dex */
public class TestTool {
    private File errs;

    /* renamed from: jm */
    private String f1578jm;
    private File coded = File.createTempFile("seq", ".264");
    private File decoded = File.createTempFile("seq_dec", ".yuv");
    private File jmconf = File.createTempFile("ldecod", ".conf");

    public TestTool(String jm, String errs) throws IOException {
        this.f1578jm = jm;
        this.errs = new File(errs);
        prepareJMConf();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("JCodec h.264 test tool");
            System.out.println("Syntax: <path to ldecod> <movie file> <foder for errors>");
        } else {
            new TestTool(args[0], args[2]).doIt(args[1]);
        }
    }

    private void doIt(String in) throws Exception {
        Packet inFrame;
        SeekableByteChannel raw;
        SeekableByteChannel raw2 = null;
        SeekableByteChannel source = null;
        try {
            SeekableByteChannel source2 = new FileChannelWrapper(new FileInputStream(in).getChannel());
            try {
                MP4Demuxer demux = new MP4Demuxer(source2);
                H264Decoder decoder = new H264Decoder();
                AbstractMP4DemuxerTrack inTrack = demux.getVideoTrack();
                VideoSampleEntry ine = (VideoSampleEntry) inTrack.getSampleEntries()[0];
                AvcCBox avcC = (AvcCBox) Box.m2135as(AvcCBox.class, (LeafBox) Box.findFirst(ine, LeafBox.class, "avcC"));
                ByteBuffer _rawData = ByteBuffer.allocate(12533760);
                decoder.addSps(avcC.getSpsList());
                decoder.addPps(avcC.getPpsList());
                inTrack.gotoFrame(2600);
                do {
                    inFrame = inTrack.nextFrame();
                    if (inFrame == null) {
                        break;
                    }
                } while (!inFrame.isKeyFrame());
                inTrack.gotoFrame(inFrame.getFrameNo());
                List<Picture> decodedPics = new ArrayList<>();
                int totalFrames = (int) inTrack.getFrameCount();
                int seqNo = 0;
                int i = 2600;
                while (true) {
                    try {
                        raw = raw2;
                        Packet inFrame2 = inTrack.nextFrame();
                        if (inFrame2 == null) {
                            break;
                        }
                        ByteBuffer data = inFrame2.getData();
                        List<ByteBuffer> nalUnits = H264Utils.splitMOVPacket(data, avcC);
                        _rawData.clear();
                        H264Utils.joinNALUnits(nalUnits, _rawData);
                        _rawData.flip();
                        if (H264Utils.idrSlice(_rawData)) {
                            if (raw != null) {
                                raw.close();
                                runJMCompareResults(decodedPics, seqNo);
                                decodedPics = new ArrayList<>();
                                seqNo = i;
                            }
                            raw2 = new FileChannelWrapper(new FileOutputStream(this.coded).getChannel());
                            H264Utils.saveStreamParams(avcC, raw2);
                        } else {
                            raw2 = raw;
                        }
                        raw2.write(_rawData);
                        decodedPics.add(decoder.decodeFrame(nalUnits, Picture.create((ine.getWidth() + 15) & (-16), (ine.getHeight() + 15) & (-16), ColorSpace.YUV420).getData()));
                        if (i % 500 == 0) {
                            System.out.println(((i * 100) / totalFrames) + "%");
                        }
                        i++;
                    } catch (Throwable th) {
                        th = th;
                        source = source2;
                        raw2 = raw;
                        if (source != null) {
                            source.close();
                        }
                        if (raw2 != null) {
                            raw2.close();
                        }
                        throw th;
                    }
                }
                if (decodedPics.size() > 0) {
                    runJMCompareResults(decodedPics, seqNo);
                }
                if (source2 != null) {
                    source2.close();
                }
                if (raw != null) {
                    raw.close();
                }
            } catch (Throwable th2) {
                th = th2;
                source = source2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void runJMCompareResults(List<Picture> decodedPics, int seqNo) throws Exception {
        try {
            Process process = Runtime.getRuntime().exec(this.f1578jm + " -d " + this.jmconf.getAbsolutePath());
            process.waitFor();
            ByteBuffer yuv = NIOUtils.fetchFrom(this.decoded);
            Iterator i$ = decodedPics.iterator();
            while (i$.hasNext()) {
                Picture pic = i$.next().cropped();
                boolean equals = Arrays.equals(JCodecUtil.getAsIntArray(yuv, pic.getPlaneWidth(0) * pic.getPlaneHeight(0)), pic.getPlaneData(0));
                if (!(equals & Arrays.equals(JCodecUtil.getAsIntArray(yuv, pic.getPlaneWidth(1) * pic.getPlaneHeight(1)), pic.getPlaneData(1)) & Arrays.equals(JCodecUtil.getAsIntArray(yuv, pic.getPlaneWidth(2) * pic.getPlaneHeight(2)), pic.getPlaneData(2)))) {
                    diff(seqNo);
                }
            }
        } catch (Exception e) {
            diff(seqNo);
        }
    }

    private void diff(int seqNo) {
        System.out.println(seqNo + ": DIFF!!!");
        this.coded.renameTo(new File(this.errs, String.format("seq%08d.264", Integer.valueOf(seqNo))));
        this.decoded.renameTo(new File(this.errs, String.format("seq%08d_dec.yuv", Integer.valueOf(seqNo))));
    }

    private void prepareJMConf() throws IOException {
        InputStream cool = null;
        try {
            cool = getClass().getClassLoader().getResourceAsStream("org/jcodec/testing/jm.conf");
            String str = IOUtils.toString(cool);
            IOUtils.writeStringToFile(this.jmconf, str.replace("%input_file%", this.coded.getAbsolutePath()).replace("%output_file%", this.decoded.getAbsolutePath()));
        } finally {
            IOUtils.closeQuietly(cool);
        }
    }
}
