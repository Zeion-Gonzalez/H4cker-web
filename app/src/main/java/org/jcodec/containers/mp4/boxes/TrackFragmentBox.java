package org.jcodec.containers.mp4.boxes;

import java.util.List;

/* loaded from: classes.dex */
public class TrackFragmentBox extends NodeBox {
    public TrackFragmentBox() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "traf";
    }

    @Override // org.jcodec.containers.mp4.boxes.NodeBox
    protected void getModelFields(List<String> model) {
    }

    public int getTrackId() {
        TrackFragmentHeaderBox tfhd = (TrackFragmentHeaderBox) Box.findFirst(this, TrackFragmentHeaderBox.class, TrackFragmentHeaderBox.fourcc());
        if (tfhd == null) {
            throw new RuntimeException("Corrupt track fragment, no header atom found");
        }
        return tfhd.getTrackId();
    }
}
