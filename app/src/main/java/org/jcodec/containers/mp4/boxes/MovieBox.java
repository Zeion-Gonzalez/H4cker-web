package org.jcodec.containers.mp4.boxes;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Util;

/* loaded from: classes.dex */
public class MovieBox extends NodeBox {
    public MovieBox(Header atom) {
        super(atom);
    }

    public static String fourcc() {
        return "moov";
    }

    public MovieBox() {
        super(new Header(fourcc()));
    }

    public MovieBox(MovieBox movie) {
        super(movie);
    }

    public TrakBox[] getTracks() {
        return (TrakBox[]) findAll(this, TrakBox.class, "trak");
    }

    public TrakBox getVideoTrack() {
        TrakBox[] arr$ = getTracks();
        for (TrakBox trakBox : arr$) {
            if (trakBox.isVideo()) {
                return trakBox;
            }
        }
        return null;
    }

    public TrakBox getTimecodeTrack() {
        TrakBox[] arr$ = getTracks();
        for (TrakBox trakBox : arr$) {
            if (trakBox.isTimecode()) {
                return trakBox;
            }
        }
        return null;
    }

    public int getTimescale() {
        return getMovieHeader().getTimescale();
    }

    public long rescale(long tv, long ts) {
        return (getTimescale() * tv) / ts;
    }

    public void fixTimescale(int newTs) {
        int oldTs = getTimescale();
        setTimescale(newTs);
        TrakBox[] arr$ = getTracks();
        for (TrakBox trakBox : arr$) {
            trakBox.setDuration(rescale(trakBox.getDuration(), oldTs));
            List<Edit> edits = trakBox.getEdits();
            if (edits != null) {
                ListIterator<Edit> lit = edits.listIterator();
                while (lit.hasNext()) {
                    Edit edit = lit.next();
                    lit.set(new Edit(rescale(edit.getDuration(), oldTs), edit.getMediaTime(), edit.getRate()));
                }
            }
        }
        setDuration(rescale(getDuration(), oldTs));
    }

    private void setTimescale(int newTs) {
        ((MovieHeaderBox) findFirst(this, MovieHeaderBox.class, "mvhd")).setTimescale(newTs);
    }

    public void setDuration(long movDuration) {
        getMovieHeader().setDuration(movDuration);
    }

    private MovieHeaderBox getMovieHeader() {
        return (MovieHeaderBox) findFirst(this, MovieHeaderBox.class, "mvhd");
    }

    public List<TrakBox> getAudioTracks() {
        ArrayList<TrakBox> result = new ArrayList<>();
        TrakBox[] arr$ = getTracks();
        for (TrakBox trakBox : arr$) {
            if (trakBox.isAudio()) {
                result.add(trakBox);
            }
        }
        return result;
    }

    public long getDuration() {
        return getMovieHeader().getDuration();
    }

    public TrakBox importTrack(MovieBox movie, TrakBox track) {
        TrakBox newTrack = (TrakBox) MP4Util.cloneBox(track, 1048576);
        List<Edit> edits = newTrack.getEdits();
        ArrayList<Edit> result = new ArrayList<>();
        if (edits != null) {
            for (Edit edit : edits) {
                result.add(new Edit(rescale(edit.getDuration(), movie.getTimescale()), edit.getMediaTime(), edit.getRate()));
            }
        }
        newTrack.setEdits(result);
        return newTrack;
    }

    public void appendTrack(TrakBox newTrack) {
        newTrack.getTrackHeader().setNo(getMovieHeader().getNextTrackId());
        getMovieHeader().setNextTrackId(getMovieHeader().getNextTrackId() + 1);
        this.boxes.add(newTrack);
    }

    public boolean isPureRefMovie(MovieBox movie) {
        boolean pureRef = true;
        TrakBox[] arr$ = movie.getTracks();
        for (TrakBox trakBox : arr$) {
            pureRef &= trakBox.isPureRef();
        }
        return pureRef;
    }

    public void updateDuration() {
        TrakBox[] tracks = getTracks();
        long min = 2147483647L;
        for (TrakBox trakBox : tracks) {
            if (trakBox.getDuration() < min) {
                min = trakBox.getDuration();
            }
        }
        getMovieHeader().setDuration(min);
    }

    public Size getDisplaySize() {
        TrakBox videoTrack = getVideoTrack();
        if (videoTrack == null) {
            return null;
        }
        ClearApertureBox clef = (ClearApertureBox) NodeBox.findFirst(videoTrack, ClearApertureBox.class, "tapt", "clef");
        if (clef != null) {
            return applyMatrix(videoTrack, new Size((int) clef.getWidth(), (int) clef.getHeight()));
        }
        Box box = ((SampleDescriptionBox) NodeBox.findFirst(videoTrack, SampleDescriptionBox.class, "mdia", "minf", "stbl", "stsd")).getBoxes().get(0);
        if (box == null || !(box instanceof VideoSampleEntry)) {
            return null;
        }
        VideoSampleEntry vs = (VideoSampleEntry) box;
        Rational par = videoTrack.getPAR();
        return applyMatrix(videoTrack, new Size((vs.getWidth() * par.getNum()) / par.getDen(), vs.getHeight()));
    }

    private Size applyMatrix(TrakBox videoTrack, Size size) {
        int[] matrix = videoTrack.getTrackHeader().getMatrix();
        return new Size((int) ((size.getWidth() * matrix[0]) / 65536.0d), (int) ((size.getHeight() * matrix[4]) / 65536.0d));
    }

    public Size getStoredSize() {
        TrakBox videoTrack = getVideoTrack();
        if (videoTrack == null) {
            return null;
        }
        EncodedPixelBox enof = (EncodedPixelBox) NodeBox.findFirst(videoTrack, EncodedPixelBox.class, "tapt", "enof");
        if (enof != null) {
            return new Size((int) enof.getWidth(), (int) enof.getHeight());
        }
        Box box = ((SampleDescriptionBox) NodeBox.findFirst(videoTrack, SampleDescriptionBox.class, "mdia", "minf", "stbl", "stsd")).getBoxes().get(0);
        if (box == null || !(box instanceof VideoSampleEntry)) {
            return null;
        }
        VideoSampleEntry vs = (VideoSampleEntry) box;
        return new Size(vs.getWidth(), vs.getHeight());
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox
    protected void getModelFields(List<String> model) {
    }
}
