package org.jcodec.containers.mp4.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.TimecodeSampleEntry;
import org.jcodec.movtool.Util;

/* loaded from: classes.dex */
public class TimecodeMP4MuxerTrack extends FramesMP4MuxerTrack {
    private TapeTimecode firstTimecode;
    private int fpsEstimate;
    private List<Packet> gop;
    private List<Edit> lower;
    private TapeTimecode prevTimecode;
    private long sampleDuration;
    private long samplePts;
    private int tcFrames;

    public TimecodeMP4MuxerTrack(SeekableByteChannel out, int trackId, int timescale) {
        super(out, trackId, TrackType.TIMECODE, timescale);
        this.lower = new ArrayList();
        this.gop = new ArrayList();
    }

    public void addTimecode(Packet packet) throws IOException {
        if (packet.isKeyFrame()) {
            processGop();
        }
        this.gop.add(new Packet(packet, (ByteBuffer) null));
    }

    private void processGop() throws IOException {
        if (this.gop.size() > 0) {
            for (Packet pkt : sortByDisplay(this.gop)) {
                addTimecodeInt(pkt);
            }
            this.gop.clear();
        }
    }

    private List<Packet> sortByDisplay(List<Packet> gop) {
        ArrayList<Packet> result = new ArrayList<>(gop);
        Collections.sort(result, new Comparator<Packet>() { // from class: org.jcodec.containers.mp4.muxer.TimecodeMP4MuxerTrack.1
            @Override // java.util.Comparator
            public int compare(Packet o1, Packet o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null || o1.getDisplayOrder() > o2.getDisplayOrder()) {
                    return 1;
                }
                return o1.getDisplayOrder() == o2.getDisplayOrder() ? 0 : -1;
            }
        });
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack, org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack
    public Box finish(MovieHeaderBox mvhd) throws IOException {
        processGop();
        outTimecodeSample();
        if (this.sampleEntries.size() == 0) {
            return null;
        }
        if (this.edits != null) {
            this.edits = Util.editsOnEdits(new Rational(1, 1), this.lower, this.edits);
        } else {
            this.edits = this.lower;
        }
        return super.finish(mvhd);
    }

    private void addTimecodeInt(Packet packet) throws IOException {
        TapeTimecode tapeTimecode = packet.getTapeTimecode();
        boolean gap = isGap(this.prevTimecode, tapeTimecode);
        this.prevTimecode = tapeTimecode;
        if (gap) {
            outTimecodeSample();
            this.firstTimecode = tapeTimecode;
            this.fpsEstimate = tapeTimecode.isDropFrame() ? 30 : -1;
            this.samplePts += this.sampleDuration;
            this.sampleDuration = 0L;
            this.tcFrames = 0;
        }
        this.sampleDuration += packet.getDuration();
        this.tcFrames++;
    }

    private boolean isGap(TapeTimecode prevTimecode, TapeTimecode tapeTimecode) {
        if (prevTimecode == null && tapeTimecode != null) {
            return true;
        }
        if (prevTimecode == null) {
            return false;
        }
        if (tapeTimecode == null || prevTimecode.isDropFrame() != tapeTimecode.isDropFrame()) {
            return true;
        }
        boolean gap = isTimeGap(prevTimecode, tapeTimecode);
        return gap;
    }

    private boolean isTimeGap(TapeTimecode prevTimecode, TapeTimecode tapeTimecode) {
        int sec = toSec(tapeTimecode);
        int secDiff = sec - toSec(prevTimecode);
        if (secDiff == 0) {
            int frameDiff = tapeTimecode.getFrame() - prevTimecode.getFrame();
            if (this.fpsEstimate != -1) {
                frameDiff = (this.fpsEstimate + frameDiff) % this.fpsEstimate;
            }
            return frameDiff != 1;
        }
        if (secDiff == 1) {
            if (this.fpsEstimate == -1) {
                if (tapeTimecode.getFrame() == 0) {
                    this.fpsEstimate = prevTimecode.getFrame() + 1;
                    return false;
                }
                return true;
            }
            int firstFrame = (tapeTimecode.isDropFrame() && sec % 60 == 0 && sec % 600 != 0) ? 2 : 0;
            if (tapeTimecode.getFrame() == firstFrame && prevTimecode.getFrame() == this.fpsEstimate - 1) {
                return false;
            }
            return true;
        }
        return true;
    }

    private void outTimecodeSample() throws IOException {
        if (this.sampleDuration > 0) {
            if (this.firstTimecode != null) {
                if (this.fpsEstimate == -1) {
                    this.fpsEstimate = this.prevTimecode.getFrame() + 1;
                }
                TimecodeSampleEntry tmcd = new TimecodeSampleEntry(this.firstTimecode.isDropFrame() ? 1 : 0, this.timescale, (int) (this.sampleDuration / this.tcFrames), this.fpsEstimate);
                this.sampleEntries.add(tmcd);
                ByteBuffer sample = ByteBuffer.allocate(4);
                sample.putInt(toCounter(this.firstTimecode, this.fpsEstimate));
                sample.flip();
                addFrame(new MP4Packet(sample, this.samplePts, this.timescale, this.sampleDuration, 0L, true, null, this.samplePts, this.sampleEntries.size() - 1));
                this.lower.add(new Edit(this.sampleDuration, this.samplePts, 1.0f));
                return;
            }
            this.lower.add(new Edit(this.sampleDuration, -1L, 1.0f));
        }
    }

    private int toCounter(TapeTimecode tc, int fps) {
        int frames = (toSec(tc) * fps) + tc.getFrame();
        if (tc.isDropFrame()) {
            long D = frames / 18000;
            long M = frames % 18000;
            return (int) (frames - ((18 * D) + (2 * ((M - 2) / 1800))));
        }
        return frames;
    }

    private int toSec(TapeTimecode tc) {
        return (tc.getHour() * 3600) + (tc.getMinute() * 60) + tc.getSecond();
    }
}
