package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.TapeTimecode;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.QTTimeUtil;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TimecodeSampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class TimecodeMP4DemuxerTrack {
    private TrakBox box;
    private long[] chunkOffsets;
    private SeekableByteChannel input;
    private MovieBox movie;
    private int[] sampleCache;
    private SampleToChunkBox.SampleToChunkEntry[] sampleToChunks;
    private TimeToSampleBox.TimeToSampleEntry[] timeToSamples;
    private TimecodeSampleEntry tse;

    public TimecodeMP4DemuxerTrack(MovieBox movie, TrakBox trak, SeekableByteChannel input) throws IOException {
        this.box = trak;
        this.input = input;
        this.movie = movie;
        NodeBox stbl = trak.getMdia().getMinf().getStbl();
        TimeToSampleBox stts = (TimeToSampleBox) Box.findFirst(stbl, TimeToSampleBox.class, "stts");
        SampleToChunkBox stsc = (SampleToChunkBox) Box.findFirst(stbl, SampleToChunkBox.class, "stsc");
        ChunkOffsetsBox stco = (ChunkOffsetsBox) Box.findFirst(stbl, ChunkOffsetsBox.class, "stco");
        ChunkOffsets64Box co64 = (ChunkOffsets64Box) Box.findFirst(stbl, ChunkOffsets64Box.class, "co64");
        this.timeToSamples = stts.getEntries();
        this.chunkOffsets = stco != null ? stco.getChunkOffsets() : co64.getChunkOffsets();
        this.sampleToChunks = stsc.getSampleToChunk();
        if (this.chunkOffsets.length == 1) {
            cacheSamples(this.sampleToChunks, this.chunkOffsets);
        }
        this.tse = (TimecodeSampleEntry) this.box.getSampleEntries()[0];
    }

    public MP4Packet getTimecode(MP4Packet pkt) throws IOException {
        long tv = QTTimeUtil.editedToMedia(this.box, this.box.rescale(pkt.getPts(), pkt.getTimescale()), this.movie.getTimescale());
        int ttsInd = 0;
        int ttsSubInd = 0;
        int sample = 0;
        while (sample < this.sampleCache.length - 1) {
            int dur = this.timeToSamples[ttsInd].getSampleDuration();
            if (tv < dur) {
                break;
            }
            tv -= dur;
            ttsSubInd++;
            if (ttsInd < this.timeToSamples.length - 1 && ttsSubInd >= this.timeToSamples[ttsInd].getSampleCount()) {
                ttsInd++;
            }
            sample++;
        }
        int frameNo = ((int) (((((2 * tv) * this.tse.getTimescale()) / this.box.getTimescale()) / this.tse.getFrameDuration()) + 1)) / 2;
        return new MP4Packet(pkt, getTimecode(getTimecodeSample(sample), frameNo, this.tse));
    }

    private int getTimecodeSample(int sample) throws IOException {
        int i;
        if (this.sampleCache != null) {
            return this.sampleCache[sample];
        }
        synchronized (this.input) {
            int stscInd = 0;
            int stscSubInd = sample;
            while (stscInd < this.sampleToChunks.length && stscSubInd >= this.sampleToChunks[stscInd].getCount()) {
                stscSubInd -= this.sampleToChunks[stscInd].getCount();
                stscInd++;
            }
            long offset = this.chunkOffsets[stscInd] + (Math.min(stscSubInd, this.sampleToChunks[stscInd].getCount() - 1) << 2);
            if (this.input.position() != offset) {
                this.input.position(offset);
            }
            ByteBuffer buf = NIOUtils.fetchFrom(this.input, 4);
            i = buf.getInt();
        }
        return i;
    }

    private TapeTimecode getTimecode(int startCounter, int frameNo, TimecodeSampleEntry entry) {
        int frame = dropFrameAdjust(frameNo + startCounter, entry);
        int sec = frame / entry.getNumFrames();
        return new TapeTimecode((short) (sec / 3600), (byte) ((sec / 60) % 60), (byte) (sec % 60), (byte) (frame % entry.getNumFrames()), entry.isDropFrame());
    }

    private int dropFrameAdjust(int frame, TimecodeSampleEntry entry) {
        if (entry.isDropFrame()) {
            long D = frame / 17982;
            long M = frame % 17982;
            return (int) (frame + (18 * D) + (2 * ((M - 2) / 1798)));
        }
        return frame;
    }

    private void cacheSamples(SampleToChunkBox.SampleToChunkEntry[] sampleToChunks, long[] chunkOffsets) throws IOException {
        synchronized (this.input) {
            int stscInd = 0;
            IntArrayList ss = new IntArrayList();
            for (int chunkNo = 0; chunkNo < chunkOffsets.length; chunkNo++) {
                int nSamples = sampleToChunks[stscInd].getCount();
                if (stscInd < sampleToChunks.length - 1 && chunkNo + 1 >= sampleToChunks[stscInd + 1].getFirst()) {
                    stscInd++;
                }
                long offset = chunkOffsets[chunkNo];
                this.input.position(offset);
                ByteBuffer buf = NIOUtils.fetchFrom(this.input, nSamples * 4);
                for (int i = 0; i < nSamples; i++) {
                    ss.add(buf.getInt());
                }
            }
            this.sampleCache = ss.toArray();
        }
    }

    public int getStartTimecode() throws IOException {
        return getTimecodeSample(0);
    }

    public TrakBox getBox() {
        return this.box;
    }

    public int parseTimecode(String tc) {
        String[] split = tc.split(":");
        TimecodeSampleEntry tmcd = (TimecodeSampleEntry) Box.findFirst(this.box, TimecodeSampleEntry.class, "mdia", "minf", "stbl", "stsd", "tmcd");
        byte nf = tmcd.getNumFrames();
        return Integer.parseInt(split[3]) + (Integer.parseInt(split[2]) * nf) + (Integer.parseInt(split[1]) * 60 * nf) + (Integer.parseInt(split[0]) * 3600 * nf);
    }

    public int timeCodeToFrameNo(String timeCode) throws Exception {
        if (!isValidTimeCode(timeCode)) {
            return -1;
        }
        int movieFrame = parseTimecode(timeCode.trim()) - this.sampleCache[0];
        int frameRate = this.tse.getNumFrames();
        long framesInTimescale = this.tse.getTimescale() * movieFrame;
        long mediaToEdited = QTTimeUtil.mediaToEdited(this.box, framesInTimescale / frameRate, this.movie.getTimescale()) * frameRate;
        return (int) (mediaToEdited / this.box.getTimescale());
    }

    private static boolean isValidTimeCode(String input) {
        Pattern p = Pattern.compile("[0-9][0-9]:[0-5][0-9]:[0-5][0-9]:[0-2][0-9]");
        Matcher m = p.matcher(input);
        return (input == null || input.trim().equals("") || !m.matches()) ? false : true;
    }
}
