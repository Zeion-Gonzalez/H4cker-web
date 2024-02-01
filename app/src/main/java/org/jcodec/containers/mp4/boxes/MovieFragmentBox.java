package org.jcodec.containers.mp4.boxes;

import java.util.List;

/* loaded from: classes.dex */
public class MovieFragmentBox extends NodeBox {
    private MovieBox moov;

    public MovieFragmentBox() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "moof";
    }

    public MovieBox getMovie() {
        return this.moov;
    }

    public void setMovie(MovieBox moov) {
        this.moov = moov;
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox
    protected void getModelFields(List<String> model) {
    }

    public TrackFragmentBox[] getTracks() {
        return (TrackFragmentBox[]) Box.findAll(this, TrackFragmentBox.class, TrackFragmentBox.fourcc());
    }

    public int getSequenceNumber() {
        MovieFragmentHeaderBox mfhd = (MovieFragmentHeaderBox) Box.findFirst(this, MovieFragmentHeaderBox.class, MovieFragmentHeaderBox.fourcc());
        if (mfhd == null) {
            throw new RuntimeException("Corrupt movie fragment, no header atom found");
        }
        return mfhd.getSequenceNumber();
    }
}
