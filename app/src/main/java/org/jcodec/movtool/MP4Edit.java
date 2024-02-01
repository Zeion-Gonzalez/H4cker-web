package org.jcodec.movtool;

import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;

/* loaded from: classes.dex */
public interface MP4Edit {
    void apply(MovieBox movieBox);

    void apply(MovieBox movieBox, MovieFragmentBox[] movieFragmentBoxArr);
}
