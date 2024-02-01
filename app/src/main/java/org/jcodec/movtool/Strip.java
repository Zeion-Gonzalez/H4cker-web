package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.Chunk;
import org.jcodec.containers.mp4.ChunkReader;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.MediaHeaderBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class Strip {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Syntax: strip <ref movie> <out movie>");
            System.exit(-1);
        }
        SeekableByteChannel input = null;
        SeekableByteChannel out = null;
        try {
            input = NIOUtils.readableFileChannel(new File(args[0]));
            File file = new File(args[1]);
            file.delete();
            out = NIOUtils.writableFileChannel(file);
            MovieBox movie = MP4Util.createRefMovie(input, "file://" + new File(args[0]).getAbsolutePath());
            new Strip().strip(movie);
            MP4Util.writeMovie(out, movie);
        } finally {
            if (input != null) {
                input.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public void strip(MovieBox movie) throws IOException {
        TrakBox[] arr$ = movie.getTracks();
        for (TrakBox track : arr$) {
            stripTrack(movie, track);
        }
    }

    public void stripTrack(MovieBox movie, TrakBox track) {
        ChunkReader chunks = new ChunkReader(track);
        List<Edit> edits = track.getEdits();
        List<Edit> oldEdits = deepCopy(edits);
        List<Chunk> result = new ArrayList<>();
        while (true) {
            Chunk chunk = chunks.next();
            if (chunk != null) {
                boolean intersects = false;
                for (Edit edit : oldEdits) {
                    if (edit.getMediaTime() != -1) {
                        long editS = edit.getMediaTime();
                        long editE = edit.getMediaTime() + track.rescale(edit.getDuration(), movie.getTimescale());
                        long chunkS = chunk.getStartTv();
                        long chunkE = chunk.getStartTv() + chunk.getDuration();
                        intersects = intersects(editS, editE, chunkS, chunkE);
                        if (intersects) {
                            break;
                        }
                    }
                }
                if (!intersects) {
                    for (int i = 0; i < oldEdits.size(); i++) {
                        if (oldEdits.get(i).getMediaTime() >= chunk.getStartTv() + chunk.getDuration()) {
                            edits.get(i).shift(-chunk.getDuration());
                        }
                    }
                } else {
                    result.add(chunk);
                }
            } else {
                NodeBox stbl = (NodeBox) Box.findFirst(track, NodeBox.class, "mdia", "minf", "stbl");
                stbl.replace("stts", getTimeToSamples(result));
                stbl.replace("stsz", getSampleSizes(result));
                stbl.replace("stsc", getSamplesToChunk(result));
                stbl.removeChildren("stco", "co64");
                stbl.add(getChunkOffsets(result));
                ((MediaHeaderBox) Box.findFirst(track, MediaHeaderBox.class, "mdia", "mdhd")).setDuration(totalDuration(result));
                return;
            }
        }
    }

    private long totalDuration(List<Chunk> result) {
        long duration = 0;
        for (Chunk chunk : result) {
            duration += chunk.getDuration();
        }
        return duration;
    }

    private List<Edit> deepCopy(List<Edit> edits) {
        ArrayList<Edit> newList = new ArrayList<>();
        for (Edit edit : edits) {
            newList.add(new Edit(edit));
        }
        return newList;
    }

    public Box getChunkOffsets(List<Chunk> chunks) {
        long[] result = new long[chunks.size()];
        boolean longBox = false;
        int i = 0;
        for (Chunk chunk : chunks) {
            if (chunk.getOffset() >= 4294967296L) {
                longBox = true;
            }
            result[i] = chunk.getOffset();
            i++;
        }
        return longBox ? new ChunkOffsets64Box(result) : new ChunkOffsetsBox(result);
    }

    public TimeToSampleBox getTimeToSamples(List<Chunk> chunks) {
        ArrayList<TimeToSampleBox.TimeToSampleEntry> tts = new ArrayList<>();
        int curTts = -1;
        int cnt = 0;
        for (Chunk chunk : chunks) {
            if (chunk.getSampleDur() > 0) {
                if (curTts == -1 || curTts != chunk.getSampleDur()) {
                    if (curTts != -1) {
                        tts.add(new TimeToSampleBox.TimeToSampleEntry(cnt, curTts));
                    }
                    cnt = 0;
                    curTts = chunk.getSampleDur();
                }
                cnt += chunk.getSampleCount();
            } else {
                int[] arr$ = chunk.getSampleDurs();
                for (int dur : arr$) {
                    if (curTts == -1 || curTts != dur) {
                        if (curTts != -1) {
                            tts.add(new TimeToSampleBox.TimeToSampleEntry(cnt, curTts));
                        }
                        cnt = 0;
                        curTts = dur;
                    }
                    cnt++;
                }
            }
        }
        if (cnt > 0) {
            tts.add(new TimeToSampleBox.TimeToSampleEntry(cnt, curTts));
        }
        return new TimeToSampleBox((TimeToSampleBox.TimeToSampleEntry[]) tts.toArray(new TimeToSampleBox.TimeToSampleEntry[0]));
    }

    public SampleSizesBox getSampleSizes(List<Chunk> chunks) {
        int nSamples = 0;
        int prevSize = chunks.get(0).getSampleSize();
        for (Chunk chunk : chunks) {
            nSamples += chunk.getSampleCount();
            if (prevSize == 0 && chunk.getSampleSize() != 0) {
                throw new RuntimeException("Mixed sample sizes not supported");
            }
        }
        if (prevSize > 0) {
            return new SampleSizesBox(prevSize, nSamples);
        }
        int[] sizes = new int[nSamples];
        int startSample = 0;
        for (Chunk chunk2 : chunks) {
            System.arraycopy(chunk2.getSampleSizes(), 0, sizes, startSample, chunk2.getSampleCount());
            startSample += chunk2.getSampleCount();
        }
        return new SampleSizesBox(sizes);
    }

    public SampleToChunkBox getSamplesToChunk(List<Chunk> chunks) {
        ArrayList<SampleToChunkBox.SampleToChunkEntry> result = new ArrayList<>();
        Iterator<Chunk> it = chunks.iterator();
        Chunk chunk = it.next();
        int curSz = chunk.getSampleCount();
        int curEntry = chunk.getEntry();
        int first = 1;
        int cnt = 1;
        while (it.hasNext()) {
            Chunk chunk2 = it.next();
            int newSz = chunk2.getSampleCount();
            int newEntry = chunk2.getEntry();
            if (curSz != newSz || curEntry != newEntry) {
                result.add(new SampleToChunkBox.SampleToChunkEntry(first, curSz, curEntry));
                curSz = newSz;
                curEntry = newEntry;
                first += cnt;
                cnt = 0;
            }
            cnt++;
        }
        if (cnt > 0) {
            result.add(new SampleToChunkBox.SampleToChunkEntry(first, curSz, curEntry));
        }
        return new SampleToChunkBox((SampleToChunkBox.SampleToChunkEntry[]) result.toArray(new SampleToChunkBox.SampleToChunkEntry[0]));
    }

    private boolean intersects(long a, long b, long c, long d) {
        return (a >= c && a < d) || (b >= c && b < d) || ((c >= a && c < b) || (d >= a && d < b));
    }
}
