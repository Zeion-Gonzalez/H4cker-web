package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.MovieBox;

/* loaded from: classes.dex */
public class ReplaceMP4Editor {
    public void modifyOrReplace(File src, MP4Edit edit) throws IOException {
        boolean modify = new InplaceMP4Editor().modify(src, edit);
        if (!modify) {
            replace(src, edit);
        }
    }

    public void replace(File src, MP4Edit edit) throws IOException {
        File tmp = new File(src.getParentFile(), "." + src.getName());
        copy(src, tmp, edit);
        tmp.renameTo(src);
    }

    public void copy(File src, File dst, MP4Edit edit) throws IOException {
        MovieBox movie = MP4Util.createRefMovie(src);
        edit.apply(movie);
        Flattern fl = new Flattern();
        fl.flattern(movie, dst);
    }
}
