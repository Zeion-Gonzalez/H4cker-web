package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.WebOptimizedMP4Muxer;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.containers.mp4.demuxer.TimecodeMP4DemuxerTrack;
import org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.containers.mp4.muxer.PCMMP4MuxerTrack;

/* loaded from: classes.dex */
public class Remux {

    /* loaded from: classes.dex */
    public interface Handler {
        void handle(MovieBox movieBox) throws IOException;
    }

    public static void main1(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("remux <movie>");
            return;
        }
        File tgt = new File(args[0]);
        File src = hidFile(tgt);
        tgt.renameTo(src);
        try {
            new Remux().remux(tgt, src, null, null);
        } catch (Throwable th) {
            tgt.renameTo(new File(tgt.getParentFile(), tgt.getName() + ".error"));
            src.renameTo(tgt);
        }
    }

    public void remux(File tgt, File src, File timecode, Handler handler) throws IOException {
        SeekableByteChannel input = null;
        SeekableByteChannel output = null;
        SeekableByteChannel tci = null;
        try {
            input = NIOUtils.readableFileChannel(src);
            output = NIOUtils.writableFileChannel(tgt);
            MP4Demuxer demuxer = new MP4Demuxer(input);
            TimecodeMP4DemuxerTrack tt = null;
            if (timecode != null) {
                tci = NIOUtils.readableFileChannel(src);
                MP4Demuxer tcd = new MP4Demuxer(tci);
                tt = tcd.getTimecodeTrack();
            }
            MP4Muxer muxer = WebOptimizedMP4Muxer.withOldHeader(output, Brand.MOV, demuxer.getMovie());
            List<AbstractMP4DemuxerTrack> at = demuxer.getAudioTracks();
            List<PCMMP4MuxerTrack> audioTracks = new ArrayList<>();
            for (AbstractMP4DemuxerTrack demuxerTrack : at) {
                PCMMP4MuxerTrack att = muxer.addPCMAudioTrack(((AudioSampleEntry) demuxerTrack.getSampleEntries()[0]).getFormat());
                audioTracks.add(att);
                att.setEdits(demuxerTrack.getEdits());
                att.setName(demuxerTrack.getName());
            }
            AbstractMP4DemuxerTrack vt = demuxer.getVideoTrack();
            FramesMP4MuxerTrack video = muxer.addTrack(TrackType.VIDEO, (int) vt.getTimescale());
            video.setTimecode(muxer.addTimecodeTrack((int) vt.getTimescale()));
            copyEdits(vt, video, new Rational((int) vt.getTimescale(), demuxer.getMovie().getTimescale()));
            video.addSampleEntries(vt.getSampleEntries());
            while (true) {
                MP4Packet pkt = (MP4Packet) vt.nextFrame();
                if (pkt == null) {
                    break;
                }
                if (tt != null) {
                    pkt = tt.getTimecode(pkt);
                }
                MP4Packet pkt2 = processFrame(pkt);
                video.addFrame(pkt2);
                for (int i = 0; i < at.size(); i++) {
                    AudioSampleEntry ase = (AudioSampleEntry) at.get(i).getSampleEntries()[0];
                    int sampleRate = (int) ((ase.getSampleRate() * ((float) pkt2.getDuration())) / ((float) vt.getTimescale()));
                    MP4Packet apkt = (MP4Packet) at.get(i).nextFrame();
                    audioTracks.get(i).addSamples(apkt.getData());
                }
            }
            MovieBox movie = muxer.finalizeHeader();
            if (handler != null) {
                handler.handle(movie);
            }
            muxer.storeHeader(movie);
        } finally {
            NIOUtils.closeQuietly(input);
            NIOUtils.closeQuietly(output);
            NIOUtils.closeQuietly(tci);
        }
    }

    private void copyEdits(AbstractMP4DemuxerTrack from, AbstractMP4MuxerTrack two, Rational tsRatio) {
        List<Edit> edits = from.getEdits();
        List<Edit> result = new ArrayList<>();
        if (edits != null) {
            for (Edit edit : edits) {
                result.add(new Edit(tsRatio.multiply(edit.getDuration()), edit.getMediaTime(), edit.getRate()));
            }
            two.setEdits(result);
        }
    }

    protected MP4Packet processFrame(MP4Packet pkt) {
        return pkt;
    }

    public static File hidFile(File tgt) {
        File src = new File(tgt.getParentFile(), "." + tgt.getName());
        if (src.exists()) {
            int i = 1;
            while (true) {
                int i2 = i + 1;
                src = new File(tgt.getParentFile(), "." + tgt.getName() + "." + i);
                if (!src.exists()) {
                    break;
                }
                i = i2;
            }
        }
        return src;
    }
}
