package org.jcodec.movtool;

import java.io.File;
import java.util.Arrays;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.ClipRegionBox;
import org.jcodec.containers.mp4.boxes.LoadSettingsBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SoundMediaHeaderBox;
import org.jcodec.containers.mp4.boxes.TrackHeaderBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoMediaHeaderBox;

/* loaded from: classes.dex */
public class Paste {

    /* renamed from: tv */
    long[] f1556tv;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Syntax: paste <to movie> <from movie> [second]");
            System.exit(-1);
        }
        File toFile = new File(args[0]);
        SeekableByteChannel to = null;
        SeekableByteChannel from = null;
        SeekableByteChannel out = null;
        try {
            File outFile = new File(toFile.getParentFile(), toFile.getName().replaceAll("\\.mov$", "") + ".paste.mov");
            outFile.delete();
            out = NIOUtils.writableFileChannel(outFile);
            to = NIOUtils.writableFileChannel(toFile);
            File fromFile = new File(args[1]);
            from = NIOUtils.readableFileChannel(fromFile);
            MovieBox toMov = MP4Util.createRefMovie(to, "file://" + toFile.getCanonicalPath());
            MovieBox fromMov = MP4Util.createRefMovie(from, "file://" + fromFile.getCanonicalPath());
            new Strip().strip(fromMov);
            if (args.length > 2) {
                new Paste().paste(toMov, fromMov, Double.parseDouble(args[2]));
            } else {
                new Paste().addToMovie(toMov, fromMov);
            }
            MP4Util.writeMovie(out, toMov);
        } finally {
            if (to != null) {
                to.close();
            }
            if (from != null) {
                from.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public void paste(MovieBox to, MovieBox from, double sec) {
        TrakBox videoTrack = to.getVideoTrack();
        if (videoTrack != null && videoTrack.getTimescale() != to.getTimescale()) {
            to.fixTimescale(videoTrack.getTimescale());
        }
        long displayTv = (long) (to.getTimescale() * sec);
        Util.forceEditList(to);
        Util.forceEditList(from);
        TrakBox[] fromTracks = from.getTracks();
        TrakBox[] toTracks = to.getTracks();
        int[][] matches = findMatches(fromTracks, toTracks);
        for (int i = 0; i < matches[0].length; i++) {
            TrakBox localTrack = to.importTrack(from, fromTracks[i]);
            if (matches[0][i] != -1) {
                Util.insertTo(to, toTracks[matches[0][i]], localTrack, displayTv);
            } else {
                to.appendTrack(localTrack);
                Util.shift(to, localTrack, displayTv);
            }
        }
        for (int i2 = 0; i2 < matches[1].length; i2++) {
            if (matches[1][i2] == -1) {
                Util.spread(to, toTracks[i2], displayTv, to.rescale(from.getDuration(), from.getTimescale()));
            }
        }
        to.updateDuration();
    }

    public void addToMovie(MovieBox to, MovieBox from) {
        TrakBox[] arr$ = from.getTracks();
        for (TrakBox track : arr$) {
            to.appendTrack(to.importTrack(from, track));
        }
    }

    private long getFrameTv(TrakBox videoTrack, int frame) {
        if (this.f1556tv == null) {
            this.f1556tv = Util.getTimevalues(videoTrack);
        }
        return this.f1556tv[frame];
    }

    private int[][] findMatches(TrakBox[] fromTracks, TrakBox[] toTracks) {
        int[] f2t = new int[fromTracks.length];
        int[] t2f = new int[toTracks.length];
        Arrays.fill(f2t, -1);
        Arrays.fill(t2f, -1);
        for (int i = 0; i < fromTracks.length; i++) {
            if (f2t[i] == -1) {
                int j = 0;
                while (true) {
                    if (j >= toTracks.length) {
                        break;
                    }
                    if (t2f[j] != -1 || !matches(fromTracks[i], toTracks[j])) {
                        j++;
                    } else {
                        f2t[i] = j;
                        t2f[j] = i;
                        break;
                    }
                }
            }
        }
        return new int[][]{f2t, t2f};
    }

    private boolean matches(TrakBox trakBox1, TrakBox trakBox2) {
        return trakBox1.getHandlerType().equals(trakBox2.getHandlerType()) && matchHeaders(trakBox1, trakBox2) && matchSampleSizes(trakBox1, trakBox2) && matchMediaHeader(trakBox1, trakBox2) && matchClip(trakBox1, trakBox2) && matchLoad(trakBox1, trakBox2);
    }

    private boolean matchSampleSizes(TrakBox trakBox1, TrakBox trakBox2) {
        SampleSizesBox stsz1 = (SampleSizesBox) NodeBox.findFirst(trakBox1, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        SampleSizesBox stsz2 = (SampleSizesBox) NodeBox.findFirst(trakBox1, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        return stsz1.getDefaultSize() == stsz2.getDefaultSize();
    }

    private boolean matchMediaHeader(TrakBox trakBox1, TrakBox trakBox2) {
        VideoMediaHeaderBox vmhd1 = (VideoMediaHeaderBox) NodeBox.findFirst(trakBox1, VideoMediaHeaderBox.class, "mdia", "minf", "vmhd");
        VideoMediaHeaderBox vmhd2 = (VideoMediaHeaderBox) NodeBox.findFirst(trakBox2, VideoMediaHeaderBox.class, "mdia", "minf", "vmhd");
        if ((vmhd1 != null && vmhd2 == null) || (vmhd1 == null && vmhd2 != null)) {
            return false;
        }
        if (vmhd1 != null && vmhd2 != null) {
            return vmhd1.getGraphicsMode() == vmhd2.getGraphicsMode() && vmhd1.getbOpColor() == vmhd2.getbOpColor() && vmhd1.getgOpColor() == vmhd2.getgOpColor() && vmhd1.getrOpColor() == vmhd2.getrOpColor();
        }
        SoundMediaHeaderBox smhd1 = (SoundMediaHeaderBox) NodeBox.findFirst(trakBox1, SoundMediaHeaderBox.class, "mdia", "minf", "smhd");
        SoundMediaHeaderBox smhd2 = (SoundMediaHeaderBox) NodeBox.findFirst(trakBox2, SoundMediaHeaderBox.class, "mdia", "minf", "smhd");
        if ((smhd1 != null || smhd2 == null) && (smhd1 == null || smhd2 != null)) {
            return smhd1 == null || smhd2 == null || smhd1.getBalance() == smhd1.getBalance();
        }
        return false;
    }

    private boolean matchHeaders(TrakBox trakBox1, TrakBox trakBox2) {
        TrackHeaderBox th1 = trakBox1.getTrackHeader();
        TrackHeaderBox th2 = trakBox2.getTrackHeader();
        return ("vide".equals(trakBox1.getHandlerType()) && Arrays.equals(th1.getMatrix(), th2.getMatrix()) && th1.getLayer() == th2.getLayer() && th1.getWidth() == th2.getWidth() && th1.getHeight() == th2.getHeight()) || ("soun".equals(trakBox1.getHandlerType()) && th1.getVolume() == th2.getVolume()) || "tmcd".equals(trakBox1.getHandlerType());
    }

    private boolean matchLoad(TrakBox trakBox1, TrakBox trakBox2) {
        LoadSettingsBox load1 = (LoadSettingsBox) NodeBox.findFirst(trakBox1, LoadSettingsBox.class, "load");
        LoadSettingsBox load2 = (LoadSettingsBox) NodeBox.findFirst(trakBox2, LoadSettingsBox.class, "load");
        if (load1 == null && load2 == null) {
            return true;
        }
        if ((load1 != null || load2 == null) && (load1 == null || load2 != null)) {
            return load1.getPreloadStartTime() == load2.getPreloadStartTime() && load1.getPreloadDuration() == load2.getPreloadDuration() && load1.getPreloadFlags() == load2.getPreloadFlags() && load1.getDefaultHints() == load2.getDefaultHints();
        }
        return false;
    }

    private boolean matchClip(TrakBox trakBox1, TrakBox trakBox2) {
        ClipRegionBox crgn1 = (ClipRegionBox) NodeBox.findFirst(trakBox1, ClipRegionBox.class, "clip", "crgn");
        ClipRegionBox crgn2 = (ClipRegionBox) NodeBox.findFirst(trakBox2, ClipRegionBox.class, "clip", "crgn");
        if ((crgn1 == null && crgn2 != null) || (crgn1 != null && crgn2 == null)) {
            return false;
        }
        if (crgn1 == null && crgn2 == null) {
            return true;
        }
        return crgn1.getRgnSize() == crgn2.getRgnSize() && crgn1.getX() == crgn2.getX() && crgn1.getY() == crgn2.getY() && crgn1.getWidth() == crgn2.getWidth() && crgn1.getHeight() == crgn2.getHeight();
    }
}
