package org.jcodec.movtool;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jcodec.common.ArrayUtil;
import org.jcodec.common.model.Rational;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.DataRefBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MediaHeaderBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class Util {

    /* loaded from: classes.dex */
    public static class Pair<T> {

        /* renamed from: a */
        private T f1557a;

        /* renamed from: b */
        private T f1558b;

        public Pair(T a, T b) {
            this.f1557a = a;
            this.f1558b = b;
        }

        public T getA() {
            return this.f1557a;
        }

        public T getB() {
            return this.f1558b;
        }
    }

    public static Pair<List<Edit>> split(List<Edit> edits, Rational trackByMv, long tvMv) {
        long total = 0;
        List<Edit> l = new ArrayList<>();
        List<Edit> r = new ArrayList<>();
        ListIterator<Edit> lit = edits.listIterator();
        while (true) {
            if (!lit.hasNext()) {
                break;
            }
            Edit edit = lit.next();
            if (edit.getDuration() + total > tvMv) {
                int leftDurMV = (int) (tvMv - total);
                int leftDurMedia = trackByMv.multiplyS(leftDurMV);
                Edit left = new Edit(leftDurMV, edit.getMediaTime(), 1.0f);
                Edit right = new Edit(edit.getDuration() - leftDurMV, leftDurMedia + edit.getMediaTime(), 1.0f);
                lit.remove();
                if (left.getDuration() > 0) {
                    lit.add(left);
                    l.add(left);
                }
                if (right.getDuration() > 0) {
                    lit.add(right);
                    r.add(right);
                }
            } else {
                l.add(edit);
                total += edit.getDuration();
            }
        }
        while (lit.hasNext()) {
            r.add(lit.next());
        }
        return new Pair<>(l, r);
    }

    public static Pair<List<Edit>> split(MovieBox movie, TrakBox track, long tvMv) {
        return split(track.getEdits(), new Rational(track.getTimescale(), movie.getTimescale()), tvMv);
    }

    public static void spread(MovieBox movie, TrakBox track, long tvMv, long durationMv) {
        Pair<List<Edit>> split = split(movie, track, tvMv);
        track.getEdits().add(split.getA().size(), new Edit(durationMv, -1L, 1.0f));
    }

    public static void shift(MovieBox movie, TrakBox track, long tvMv) {
        track.getEdits().add(0, new Edit(tvMv, -1L, 1.0f));
    }

    public static long[] getTimevalues(TrakBox track) {
        TimeToSampleBox stts = (TimeToSampleBox) Box.findFirst(track, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        int count = 0;
        TimeToSampleBox.TimeToSampleEntry[] tts = stts.getEntries();
        for (TimeToSampleBox.TimeToSampleEntry timeToSampleEntry : tts) {
            count += timeToSampleEntry.getSampleCount();
        }
        long[] tv = new long[count + 1];
        int k = 0;
        for (int i = 0; i < tts.length; i++) {
            int j = 0;
            while (j < tts[i].getSampleCount()) {
                tv[k + 1] = tv[k] + tts[i].getSampleDuration();
                j++;
                k++;
            }
        }
        return tv;
    }

    private static void appendToInternal(MovieBox movie, TrakBox dest, TrakBox src) {
        int off = appendEntries(dest, src);
        appendChunkOffsets(dest, src);
        appendTimeToSamples(dest, src);
        appendSampleToChunk(dest, src, off);
        appendSampleSizes(dest, src);
    }

    private static void updateDuration(TrakBox dest, TrakBox src) {
        MediaHeaderBox mdhd1 = (MediaHeaderBox) NodeBox.findFirst(dest, MediaHeaderBox.class, "mdia", "mdhd");
        MediaHeaderBox mdhd2 = (MediaHeaderBox) NodeBox.findFirst(src, MediaHeaderBox.class, "mdia", "mdhd");
        mdhd1.setDuration(mdhd1.getDuration() + mdhd2.getDuration());
    }

    public static void appendTo(MovieBox movie, TrakBox dest, TrakBox src) {
        appendToInternal(movie, dest, src);
        appendEdits(dest, src, dest.getEdits().size());
        updateDuration(dest, src);
    }

    public static void insertTo(MovieBox movie, TrakBox dest, TrakBox src, long tvMv) {
        appendToInternal(movie, dest, src);
        insertEdits(movie, dest, src, tvMv);
        updateDuration(dest, src);
    }

    private static void insertEdits(MovieBox movie, TrakBox dest, TrakBox src, long tvMv) {
        Pair<List<Edit>> split = split(movie, dest, tvMv);
        appendEdits(dest, src, split.getA().size());
    }

    private static void appendEdits(TrakBox dest, TrakBox src, int ind) {
        for (Edit edit : src.getEdits()) {
            edit.shift(dest.getMediaDuration());
        }
        dest.getEdits().addAll(ind, src.getEdits());
        dest.setEdits(dest.getEdits());
    }

    private static void appendSampleSizes(TrakBox trakBox1, TrakBox trakBox2) {
        SampleSizesBox stszr;
        SampleSizesBox stsz1 = (SampleSizesBox) NodeBox.findFirst(trakBox1, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        SampleSizesBox stsz2 = (SampleSizesBox) NodeBox.findFirst(trakBox2, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        if (stsz1.getDefaultSize() != stsz2.getDefaultSize()) {
            throw new IllegalArgumentException("Can't append to track that has different default sample size");
        }
        if (stsz1.getDefaultSize() > 0) {
            stszr = new SampleSizesBox(stsz1.getDefaultSize(), stsz1.getCount() + stsz2.getCount());
        } else {
            stszr = new SampleSizesBox(ArrayUtil.addAll(stsz1.getSizes(), stsz2.getSizes()));
        }
        ((NodeBox) NodeBox.findFirst(trakBox1, NodeBox.class, "mdia", "minf", "stbl")).replace("stsz", stszr);
    }

    private static void appendSampleToChunk(TrakBox trakBox1, TrakBox trakBox2, int off) {
        SampleToChunkBox stsc1 = (SampleToChunkBox) NodeBox.findFirst(trakBox1, SampleToChunkBox.class, "mdia", "minf", "stbl", "stsc");
        SampleToChunkBox stsc2 = (SampleToChunkBox) NodeBox.findFirst(trakBox2, SampleToChunkBox.class, "mdia", "minf", "stbl", "stsc");
        SampleToChunkBox.SampleToChunkEntry[] orig = stsc2.getSampleToChunk();
        SampleToChunkBox.SampleToChunkEntry[] shifted = new SampleToChunkBox.SampleToChunkEntry[orig.length];
        for (int i = 0; i < orig.length; i++) {
            shifted[i] = new SampleToChunkBox.SampleToChunkEntry(orig[i].getFirst() + stsc1.getSampleToChunk().length, orig[i].getCount(), orig[i].getEntry() + off);
        }
        ((NodeBox) NodeBox.findFirst(trakBox1, NodeBox.class, "mdia", "minf", "stbl")).replace("stsc", new SampleToChunkBox((SampleToChunkBox.SampleToChunkEntry[]) ArrayUtil.addAll(stsc1.getSampleToChunk(), shifted)));
    }

    private static int appendEntries(TrakBox trakBox1, TrakBox trakBox2) {
        appendDrefs(trakBox1, trakBox2);
        SampleEntry[] ent1 = (SampleEntry[]) NodeBox.findAll(trakBox1, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
        SampleEntry[] ent2 = (SampleEntry[]) NodeBox.findAll(trakBox2, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
        SampleDescriptionBox stsd = new SampleDescriptionBox(ent1);
        for (SampleEntry se : ent2) {
            se.setDrefInd((short) (se.getDrefInd() + ent1.length));
            stsd.add(se);
        }
        ((NodeBox) NodeBox.findFirst(trakBox1, NodeBox.class, "mdia", "minf", "stbl")).replace("stsd", stsd);
        return ent1.length;
    }

    private static void appendDrefs(TrakBox trakBox1, TrakBox trakBox2) {
        DataRefBox dref1 = (DataRefBox) NodeBox.findFirst(trakBox1, DataRefBox.class, "mdia", "minf", "dinf", "dref");
        DataRefBox dref2 = (DataRefBox) NodeBox.findFirst(trakBox2, DataRefBox.class, "mdia", "minf", "dinf", "dref");
        dref1.getBoxes().addAll(dref2.getBoxes());
    }

    private static void appendTimeToSamples(TrakBox trakBox1, TrakBox trakBox2) {
        TimeToSampleBox stts1 = (TimeToSampleBox) NodeBox.findFirst(trakBox1, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        TimeToSampleBox stts2 = (TimeToSampleBox) NodeBox.findFirst(trakBox2, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        TimeToSampleBox sttsNew = new TimeToSampleBox((TimeToSampleBox.TimeToSampleEntry[]) ArrayUtil.addAll(stts1.getEntries(), stts2.getEntries()));
        ((NodeBox) NodeBox.findFirst(trakBox1, NodeBox.class, "mdia", "minf", "stbl")).replace("stts", sttsNew);
    }

    private static void appendChunkOffsets(TrakBox trakBox1, TrakBox trakBox2) {
        ChunkOffsetsBox stco1 = (ChunkOffsetsBox) NodeBox.findFirst(trakBox1, ChunkOffsetsBox.class, "mdia", "minf", "stbl", "stco");
        ChunkOffsets64Box co641 = (ChunkOffsets64Box) NodeBox.findFirst(trakBox1, ChunkOffsets64Box.class, "mdia", "minf", "stbl", "co64");
        ChunkOffsetsBox stco2 = (ChunkOffsetsBox) NodeBox.findFirst(trakBox2, ChunkOffsetsBox.class, "mdia", "minf", "stbl", "stco");
        ChunkOffsets64Box co642 = (ChunkOffsets64Box) NodeBox.findFirst(trakBox2, ChunkOffsets64Box.class, "mdia", "minf", "stbl", "co64");
        long[] off1 = stco1 == null ? co641.getChunkOffsets() : stco1.getChunkOffsets();
        long[] off2 = stco2 == null ? co642.getChunkOffsets() : stco2.getChunkOffsets();
        NodeBox stbl1 = (NodeBox) NodeBox.findFirst(trakBox1, NodeBox.class, "mdia", "minf", "stbl");
        stbl1.removeChildren("stco", "co64");
        stbl1.add((co641 == null && co642 == null) ? new ChunkOffsetsBox(ArrayUtil.addAll(off1, off2)) : new ChunkOffsets64Box(ArrayUtil.addAll(off1, off2)));
    }

    public static void forceEditList(MovieBox movie, TrakBox trakBox) {
        List<Edit> edits = trakBox.getEdits();
        if (edits == null || edits.size() == 0) {
            MovieHeaderBox mvhd = (MovieHeaderBox) Box.findFirst(movie, MovieHeaderBox.class, "mvhd");
            List<Edit> edits2 = new ArrayList<>();
            trakBox.setEdits(edits2);
            edits2.add(new Edit((int) mvhd.getDuration(), 0L, 1.0f));
            trakBox.setEdits(edits2);
        }
    }

    public static void forceEditList(MovieBox movie) {
        TrakBox[] arr$ = movie.getTracks();
        for (TrakBox trakBox : arr$) {
            forceEditList(movie, trakBox);
        }
    }

    public static List<Edit> editsOnEdits(Rational mvByTrack, List<Edit> lower, List<Edit> higher) {
        List<Edit> result = new ArrayList<>();
        List<Edit> next = new ArrayList<>(lower);
        for (Edit edit : higher) {
            long startMv = mvByTrack.multiply(edit.getMediaTime());
            Pair<List<Edit>> split = split(next, mvByTrack.flip(), startMv);
            Pair<List<Edit>> split2 = split(split.getB(), mvByTrack.flip(), edit.getDuration() + startMv);
            result.addAll(split2.getA());
            List<Edit> next2 = split2.getB();
            next = next2;
        }
        return result;
    }
}
