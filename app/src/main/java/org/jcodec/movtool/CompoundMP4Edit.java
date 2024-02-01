package org.jcodec.movtool;

import java.util.List;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;

/* loaded from: classes.dex */
public class CompoundMP4Edit implements MP4Edit {
    private List<MP4Edit> edits;

    public CompoundMP4Edit(List<MP4Edit> edits) {
        this.edits = edits;
    }

    @Override // org.jcodec.movtool.MP4Edit
    public void apply(MovieBox mov, MovieFragmentBox[] fragmentBox) {
        for (MP4Edit command : this.edits) {
            command.apply(mov, fragmentBox);
        }
    }

    @Override // org.jcodec.movtool.MP4Edit
    public void apply(MovieBox mov) {
        for (MP4Edit command : this.edits) {
            command.apply(mov);
        }
    }
}
