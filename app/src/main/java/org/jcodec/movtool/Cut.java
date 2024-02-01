package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.StringUtils;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class Cut {
    public static void main(String[] args) throws Exception {
        List<MovieBox> slicesMovs;
        if (args.length < 1) {
            System.out.println("Syntax: cut [-command arg]...[-command arg] [-self] <movie file>\n\tCreates a reference movie out of the file and applies a set of changes specified by the commands to it.");
            System.exit(-1);
        }
        List<Slice> slices = new ArrayList<>();
        List<String> sliceNames = new ArrayList<>();
        boolean selfContained = false;
        int shift = 0;
        while (true) {
            if ("-cut".equals(args[shift])) {
                String[] pt = StringUtils.split(args[shift + 1], ":");
                slices.add(new Slice(Integer.parseInt(pt[0]), Integer.parseInt(pt[1])));
                if (pt.length > 2) {
                    sliceNames.add(pt[2]);
                } else {
                    sliceNames.add(null);
                }
                shift += 2;
            } else {
                if (!"-self".equals(args[shift])) {
                    break;
                }
                shift++;
                selfContained = true;
            }
        }
        File source = new File(args[shift]);
        SeekableByteChannel input = null;
        SeekableByteChannel out = null;
        List<SeekableByteChannel> outs = new ArrayList<>();
        try {
            input = NIOUtils.readableFileChannel(source);
            MovieBox movie = MP4Util.createRefMovie(input, "file://" + source.getCanonicalPath());
            if (!selfContained) {
                out = NIOUtils.writableFileChannel(new File(source.getParentFile(), JCodecUtil.removeExtension(source.getName()) + ".ref.mov"));
                slicesMovs = new Cut().cut(movie, slices);
                MP4Util.writeMovie(out, movie);
            } else {
                out = NIOUtils.writableFileChannel(new File(source.getParentFile(), JCodecUtil.removeExtension(source.getName()) + ".self.mov"));
                slicesMovs = new Cut().cut(movie, slices);
                new Strip().strip(movie);
                new Flattern().flattern(movie, out);
            }
            saveSlices(slicesMovs, sliceNames, source.getParentFile());
        } finally {
            if (input != null) {
                input.close();
            }
            if (out != null) {
                out.close();
            }
            for (SeekableByteChannel o : outs) {
                o.close();
            }
        }
    }

    private static void saveSlices(List<MovieBox> slices, List<String> names, File parentFile) throws IOException {
        for (int i = 0; i < slices.size(); i++) {
            if (names.get(i) != null) {
                SeekableByteChannel out = null;
                try {
                    out = NIOUtils.writableFileChannel(new File(parentFile, names.get(i)));
                    MP4Util.writeMovie(out, slices.get(i));
                } finally {
                    NIOUtils.closeQuietly(out);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Slice {
        private double inSec;
        private double outSec;

        public Slice(double in, double out) {
            this.inSec = in;
            this.outSec = out;
        }
    }

    public List<MovieBox> cut(MovieBox movie, List<Slice> commands) {
        TrakBox videoTrack = movie.getVideoTrack();
        if (videoTrack != null && videoTrack.getTimescale() != movie.getTimescale()) {
            movie.fixTimescale(videoTrack.getTimescale());
        }
        TrakBox[] tracks = movie.getTracks();
        for (TrakBox trakBox : tracks) {
            Util.forceEditList(movie, trakBox);
            List<Edit> edits = trakBox.getEdits();
            for (Slice cut : commands) {
                split(edits, cut.inSec, movie, trakBox);
                split(edits, cut.outSec, movie, trakBox);
            }
        }
        ArrayList<MovieBox> result = new ArrayList<>();
        for (Slice cut2 : commands) {
            MovieBox clone = (MovieBox) MP4Util.cloneBox(movie, 16777216);
            TrakBox[] arr$ = clone.getTracks();
            for (TrakBox trakBox2 : arr$) {
                selectInner(trakBox2.getEdits(), cut2, movie, trakBox2);
            }
            result.add(clone);
        }
        long movDuration = 0;
        TrakBox[] arr$2 = movie.getTracks();
        for (TrakBox trakBox3 : arr$2) {
            selectOuter(trakBox3.getEdits(), commands, movie, trakBox3);
            trakBox3.setEdits(trakBox3.getEdits());
            movDuration = Math.max(movDuration, trakBox3.getDuration());
        }
        movie.setDuration(movDuration);
        return result;
    }

    private void selectOuter(List<Edit> edits, List<Slice> commands, MovieBox movie, TrakBox trakBox) {
        long[] inMv = new long[commands.size()];
        long[] outMv = new long[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            inMv[i] = (long) (commands.get(i).inSec * movie.getTimescale());
            outMv[i] = (long) (commands.get(i).outSec * movie.getTimescale());
        }
        long editStartMv = 0;
        ListIterator<Edit> lit = edits.listIterator();
        while (lit.hasNext()) {
            Edit edit = lit.next();
            for (int i2 = 0; i2 < inMv.length; i2++) {
                if (edit.getDuration() + editStartMv > inMv[i2] && editStartMv < outMv[i2]) {
                    lit.remove();
                }
            }
            editStartMv += edit.getDuration();
        }
    }

    private void selectInner(List<Edit> edits, Slice cut, MovieBox movie, TrakBox trakBox) {
        long inMv = (long) (movie.getTimescale() * cut.inSec);
        long outMv = (long) (movie.getTimescale() * cut.outSec);
        long editStart = 0;
        ListIterator<Edit> lit = edits.listIterator();
        while (lit.hasNext()) {
            Edit edit = lit.next();
            if (edit.getDuration() + editStart <= inMv || editStart >= outMv) {
                lit.remove();
            }
            editStart += edit.getDuration();
        }
    }

    private void split(List<Edit> edits, double sec, MovieBox movie, TrakBox trakBox) {
        Util.split(movie, trakBox, (long) (movie.getTimescale() * sec));
    }
}
