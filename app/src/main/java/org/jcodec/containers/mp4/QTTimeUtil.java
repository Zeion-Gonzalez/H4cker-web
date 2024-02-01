package org.jcodec.containers.mp4;

import java.io.IOException;
import java.util.List;
import org.jcodec.common.model.RationalLarge;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TimecodeSampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.demuxer.TimecodeMP4DemuxerTrack;

/* loaded from: classes.dex */
public class QTTimeUtil {
    public static long getEditedDuration(TrakBox track) {
        List<Edit> edits = track.getEdits();
        if (edits == null) {
            return track.getDuration();
        }
        long duration = 0;
        for (Edit edit : edits) {
            duration += edit.getDuration();
        }
        return duration;
    }

    public static long frameToTimevalue(TrakBox trak, int frameNumber) {
        TimeToSampleBox stts = (TimeToSampleBox) Box.findFirst(trak, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        TimeToSampleBox.TimeToSampleEntry[] timeToSamples = stts.getEntries();
        long pts = 0;
        int sttsInd = 0;
        int sttsSubInd = frameNumber;
        while (sttsSubInd >= timeToSamples[sttsInd].getSampleCount()) {
            sttsSubInd -= timeToSamples[sttsInd].getSampleCount();
            pts += timeToSamples[sttsInd].getSampleCount() * timeToSamples[sttsInd].getSampleDuration();
            sttsInd++;
        }
        return (timeToSamples[sttsInd].getSampleDuration() * sttsSubInd) + pts;
    }

    public static int timevalueToFrame(TrakBox trak, long tv) {
        TimeToSampleBox.TimeToSampleEntry[] tts = ((TimeToSampleBox) Box.findFirst(trak, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts")).getEntries();
        int frame = 0;
        for (int i = 0; tv > 0 && i < tts.length; i++) {
            long rem = tv / tts[i].getSampleDuration();
            tv -= tts[i].getSampleCount() * tts[i].getSampleDuration();
            long j = frame;
            if (tv > 0) {
                rem = tts[i].getSampleCount();
            }
            frame = (int) (j + rem);
        }
        return frame;
    }

    public static long mediaToEdited(TrakBox trak, long mediaTv, int movieTimescale) {
        if (trak.getEdits() == null) {
            return mediaTv;
        }
        long accum = 0;
        for (Edit edit : trak.getEdits()) {
            if (mediaTv >= edit.getMediaTime()) {
                long duration = trak.rescale(edit.getDuration(), movieTimescale);
                if (edit.getMediaTime() != -1 && mediaTv >= edit.getMediaTime() && mediaTv < edit.getMediaTime() + duration) {
                    return accum + (mediaTv - edit.getMediaTime());
                }
                accum += duration;
            } else {
                return accum;
            }
        }
        return accum;
    }

    public static long editedToMedia(TrakBox trak, long editedTv, int movieTimescale) {
        if (trak.getEdits() != null) {
            long accum = 0;
            for (Edit edit : trak.getEdits()) {
                long duration = trak.rescale(edit.getDuration(), movieTimescale);
                if (accum + duration > editedTv) {
                    return (edit.getMediaTime() + editedTv) - accum;
                }
                accum += duration;
            }
            return accum;
        }
        return editedTv;
    }

    public static int qtPlayerFrameNo(MovieBox movie, int mediaFrameNo) {
        TrakBox videoTrack = movie.getVideoTrack();
        long editedTv = mediaToEdited(videoTrack, frameToTimevalue(videoTrack, mediaFrameNo), movie.getTimescale());
        return tv2QTFrameNo(movie, editedTv);
    }

    public static int tv2QTFrameNo(MovieBox movie, long tv) {
        TrakBox videoTrack = movie.getVideoTrack();
        TrakBox timecodeTrack = movie.getTimecodeTrack();
        return (timecodeTrack == null || Box.findFirst(videoTrack, "tref", "tmcd") == null) ? timevalueToFrame(videoTrack, tv) : timevalueToTimecodeFrame(timecodeTrack, new RationalLarge(tv, videoTrack.getTimescale()), movie.getTimescale());
    }

    public static String qtPlayerTime(MovieBox movie, int mediaFrameNo) {
        TrakBox videoTrack = movie.getVideoTrack();
        long editedTv = mediaToEdited(videoTrack, frameToTimevalue(videoTrack, mediaFrameNo), movie.getTimescale());
        int sec = (int) (editedTv / videoTrack.getTimescale());
        return String.format("%02d", Integer.valueOf(sec / 3600)) + "_" + String.format("%02d", Integer.valueOf((sec % 3600) / 60)) + "_" + String.format("%02d", Integer.valueOf(sec % 60));
    }

    public static String qtPlayerTimecode(MovieBox movie, TimecodeMP4DemuxerTrack timecodeTrack, int mediaFrameNo) throws IOException {
        TrakBox videoTrack = movie.getVideoTrack();
        long editedTv = mediaToEdited(videoTrack, frameToTimevalue(videoTrack, mediaFrameNo), movie.getTimescale());
        TrakBox tt = timecodeTrack.getBox();
        int ttTimescale = tt.getTimescale();
        long ttTv = editedToMedia(tt, (ttTimescale * editedTv) / videoTrack.getTimescale(), movie.getTimescale());
        return formatTimecode(timecodeTrack.getBox(), timecodeTrack.getStartTimecode() + timevalueToTimecodeFrame(timecodeTrack.getBox(), new RationalLarge(ttTv, ttTimescale), movie.getTimescale()));
    }

    public static String qtPlayerTimecode(TimecodeMP4DemuxerTrack timecodeTrack, RationalLarge tv, int movieTimescale) throws IOException {
        TrakBox tt = timecodeTrack.getBox();
        int ttTimescale = tt.getTimescale();
        long ttTv = editedToMedia(tt, tv.multiplyS(ttTimescale), movieTimescale);
        return formatTimecode(timecodeTrack.getBox(), timecodeTrack.getStartTimecode() + timevalueToTimecodeFrame(timecodeTrack.getBox(), new RationalLarge(ttTv, ttTimescale), movieTimescale));
    }

    public static int timevalueToTimecodeFrame(TrakBox timecodeTrack, RationalLarge tv, int movieTimescale) {
        TimecodeSampleEntry se = (TimecodeSampleEntry) timecodeTrack.getSampleEntries()[0];
        return ((int) (((2 * tv.multiplyS(se.getTimescale())) / se.getFrameDuration()) + 1)) / 2;
    }

    public static String formatTimecode(TrakBox timecodeTrack, int counter) {
        TimecodeSampleEntry tmcd = (TimecodeSampleEntry) Box.findFirst(timecodeTrack, TimecodeSampleEntry.class, "mdia", "minf", "stbl", "stsd", "tmcd");
        byte nf = tmcd.getNumFrames();
        String tc = String.format("%02d", Integer.valueOf(counter % nf));
        int counter2 = counter / nf;
        String tc2 = String.format("%02d", Integer.valueOf(counter2 % 60)) + ":" + tc;
        int counter3 = counter2 / 60;
        return String.format("%02d", Integer.valueOf(counter3 / 60)) + ":" + (String.format("%02d", Integer.valueOf(counter3 % 60)) + ":" + tc2);
    }
}
