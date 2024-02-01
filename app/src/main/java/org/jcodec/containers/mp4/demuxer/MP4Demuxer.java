package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.HandlerBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class MP4Demuxer {
    private SeekableByteChannel input;
    MovieBox movie;
    private TimecodeMP4DemuxerTrack timecodeTrack;
    private List<AbstractMP4DemuxerTrack> tracks = new LinkedList();
    private static int ftyp = 1718909296;
    private static int free = 1718773093;
    private static int moov = 1836019574;
    private static int mdat = 1835295092;
    private static int wide = 2003395685;

    public AbstractMP4DemuxerTrack create(TrakBox trak) {
        SampleSizesBox stsz = (SampleSizesBox) Box.findFirst(trak, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        return stsz.getDefaultSize() == 0 ? new FramesMP4DemuxerTrack(this.movie, trak, this.input) : new PCMMP4DemuxerTrack(this.movie, trak, this.input);
    }

    public MP4Demuxer(SeekableByteChannel input) throws IOException {
        this.input = input;
        findMovieBox(input);
    }

    public AbstractMP4DemuxerTrack[] getTracks() {
        return (AbstractMP4DemuxerTrack[]) this.tracks.toArray(new AbstractMP4DemuxerTrack[0]);
    }

    private void findMovieBox(SeekableByteChannel input) throws IOException {
        this.movie = MP4Util.parseMovie(input);
        if (this.movie == null) {
            throw new IOException("Could not find movie meta information box");
        }
        processHeader(this.movie);
    }

    private void processHeader(NodeBox moov2) throws IOException {
        TrakBox tt = null;
        TrakBox[] arr$ = (TrakBox[]) Box.findAll(moov2, TrakBox.class, "trak");
        for (TrakBox trak : arr$) {
            SampleEntry se = (SampleEntry) Box.findFirst(trak, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
            if ("tmcd".equals(se.getFourcc())) {
                tt = trak;
            } else {
                this.tracks.add(create(trak));
            }
        }
        if (tt != null) {
            AbstractMP4DemuxerTrack video = getVideoTrack();
            if (video != null) {
                this.timecodeTrack = new TimecodeMP4DemuxerTrack(this.movie, tt, this.input);
            }
        }
    }

    public static TrackType getTrackType(TrakBox trak) {
        HandlerBox handler = (HandlerBox) Box.findFirst(trak, HandlerBox.class, "mdia", "hdlr");
        return TrackType.fromHandler(handler.getComponentSubType());
    }

    public AbstractMP4DemuxerTrack getVideoTrack() {
        for (AbstractMP4DemuxerTrack demuxerTrack : this.tracks) {
            if (demuxerTrack.box.isVideo()) {
                return demuxerTrack;
            }
        }
        return null;
    }

    public MovieBox getMovie() {
        return this.movie;
    }

    public AbstractMP4DemuxerTrack getTrack(int no) {
        for (AbstractMP4DemuxerTrack track : this.tracks) {
            if (track.getNo() == no) {
                return track;
            }
        }
        return null;
    }

    public List<AbstractMP4DemuxerTrack> getAudioTracks() {
        ArrayList<AbstractMP4DemuxerTrack> result = new ArrayList<>();
        for (AbstractMP4DemuxerTrack demuxerTrack : this.tracks) {
            if (demuxerTrack.box.isAudio()) {
                result.add(demuxerTrack);
            }
        }
        return result;
    }

    public TimecodeMP4DemuxerTrack getTimecodeTrack() {
        return this.timecodeTrack;
    }

    public static int probe(ByteBuffer b) {
        ByteBuffer fork = b.duplicate();
        int success = 0;
        int total = 0;
        while (fork.remaining() >= 8) {
            long len = fork.getInt() & 4294967295L;
            int fcc = fork.getInt();
            int hdrLen = 8;
            if (len != 1) {
                if (len < 8) {
                    break;
                }
            } else {
                len = fork.getLong();
                hdrLen = 16;
            }
            if ((fcc == ftyp && len < 64) || ((fcc == moov && len < 104857600) || fcc == free || fcc == mdat || fcc == wide)) {
                success++;
            }
            total++;
            if (len >= 2147483647L) {
                break;
            }
            NIOUtils.skip(fork, (int) (len - hdrLen));
        }
        if (total == 0) {
            return 0;
        }
        return (success * 100) / total;
    }
}
