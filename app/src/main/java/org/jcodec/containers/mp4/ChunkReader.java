package org.jcodec.containers.mp4;

import java.util.Arrays;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class ChunkReader {
    private long[] chunkOffsets;
    private int curChunk;
    private int s2cIndex;
    private int sampleNo;
    private SampleToChunkBox.SampleToChunkEntry[] sampleToChunk;
    private SampleDescriptionBox stsd;
    private SampleSizesBox stsz;
    private TimeToSampleBox.TimeToSampleEntry[] tts;
    private int ttsInd = 0;
    private int ttsSubInd = 0;
    private long chunkTv = 0;

    public ChunkReader(TrakBox trakBox) {
        TimeToSampleBox stts = (TimeToSampleBox) NodeBox.findFirst(trakBox, TimeToSampleBox.class, "mdia", "minf", "stbl", "stts");
        this.tts = stts.getEntries();
        ChunkOffsetsBox stco = (ChunkOffsetsBox) NodeBox.findFirst(trakBox, ChunkOffsetsBox.class, "mdia", "minf", "stbl", "stco");
        ChunkOffsets64Box co64 = (ChunkOffsets64Box) NodeBox.findFirst(trakBox, ChunkOffsets64Box.class, "mdia", "minf", "stbl", "co64");
        this.stsz = (SampleSizesBox) NodeBox.findFirst(trakBox, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        SampleToChunkBox stsc = (SampleToChunkBox) NodeBox.findFirst(trakBox, SampleToChunkBox.class, "mdia", "minf", "stbl", "stsc");
        if (stco != null) {
            this.chunkOffsets = stco.getChunkOffsets();
        } else {
            this.chunkOffsets = co64.getChunkOffsets();
        }
        this.sampleToChunk = stsc.getSampleToChunk();
        this.stsd = (SampleDescriptionBox) NodeBox.findFirst(trakBox, SampleDescriptionBox.class, "mdia", "minf", "stbl", "stsd");
    }

    public boolean hasNext() {
        return this.curChunk < this.chunkOffsets.length;
    }

    public Chunk next() {
        if (this.curChunk >= this.chunkOffsets.length) {
            return null;
        }
        if (this.s2cIndex + 1 < this.sampleToChunk.length && this.curChunk + 1 == this.sampleToChunk[this.s2cIndex + 1].getFirst()) {
            this.s2cIndex++;
        }
        int sampleCount = this.sampleToChunk[this.s2cIndex].getCount();
        int[] samplesDur = null;
        int sampleDur = 0;
        if (this.ttsSubInd + sampleCount <= this.tts[this.ttsInd].getSampleCount()) {
            sampleDur = this.tts[this.ttsInd].getSampleDuration();
            this.ttsSubInd += sampleCount;
        } else {
            samplesDur = new int[sampleCount];
            for (int i = 0; i < sampleCount; i++) {
                if (this.ttsSubInd >= this.tts[this.ttsInd].getSampleCount() && this.ttsInd < this.tts.length - 1) {
                    this.ttsSubInd = 0;
                    this.ttsInd++;
                }
                samplesDur[i] = this.tts[this.ttsInd].getSampleDuration();
                this.ttsSubInd++;
            }
        }
        int size = 0;
        int[] sizes = null;
        if (this.stsz.getDefaultSize() > 0) {
            size = getFrameSize();
        } else {
            sizes = Arrays.copyOfRange(this.stsz.getSizes(), this.sampleNo, this.sampleNo + sampleCount);
        }
        int dref = this.sampleToChunk[this.s2cIndex].getEntry();
        Chunk chunk = new Chunk(this.chunkOffsets[this.curChunk], this.chunkTv, sampleCount, size, sizes, sampleDur, samplesDur, dref);
        this.chunkTv += chunk.getDuration();
        this.sampleNo += sampleCount;
        this.curChunk++;
        return chunk;
    }

    private int getFrameSize() {
        int size = this.stsz.getDefaultSize();
        Box box = this.stsd.getBoxes().get(this.sampleToChunk[this.s2cIndex].getEntry() - 1);
        if (box instanceof AudioSampleEntry) {
            int size2 = ((AudioSampleEntry) box).calcFrameSize();
            return size2;
        }
        return size;
    }

    public int size() {
        return this.chunkOffsets.length;
    }
}
