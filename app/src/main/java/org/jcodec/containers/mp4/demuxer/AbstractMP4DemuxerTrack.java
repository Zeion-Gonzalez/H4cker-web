package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.model.RationalLarge;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.EditListBox;
import org.jcodec.containers.mp4.boxes.NameBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public abstract class AbstractMP4DemuxerTrack implements SeekableDemuxerTrack {
    protected TrakBox box;
    protected long[] chunkOffsets;
    protected long curFrame;
    protected long duration;

    /* renamed from: no */
    private int f1545no;
    protected long pts;
    protected SampleEntry[] sampleEntries;
    protected SampleToChunkBox.SampleToChunkEntry[] sampleToChunks;
    protected int stcoInd;
    protected int stscInd;
    protected int sttsInd;
    protected int sttsSubInd;
    protected TimeToSampleBox.TimeToSampleEntry[] timeToSamples;
    protected int timescale;
    private TrackType type;

    public abstract long getFrameCount();

    public abstract MP4Packet nextFrame(ByteBuffer byteBuffer) throws IOException;

    protected abstract void seekPointer(long j);

    public AbstractMP4DemuxerTrack(TrakBox trak) {
        this.f1545no = trak.getTrackHeader().getNo();
        this.type = MP4Demuxer.getTrackType(trak);
        this.sampleEntries = (SampleEntry[]) Box.findAll(trak, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
        NodeBox stbl = trak.getMdia().getMinf().getStbl();
        TimeToSampleBox stts = (TimeToSampleBox) Box.findFirst(stbl, TimeToSampleBox.class, "stts");
        SampleToChunkBox stsc = (SampleToChunkBox) Box.findFirst(stbl, SampleToChunkBox.class, "stsc");
        ChunkOffsetsBox stco = (ChunkOffsetsBox) Box.findFirst(stbl, ChunkOffsetsBox.class, "stco");
        ChunkOffsets64Box co64 = (ChunkOffsets64Box) Box.findFirst(stbl, ChunkOffsets64Box.class, "co64");
        this.timeToSamples = stts.getEntries();
        this.sampleToChunks = stsc.getSampleToChunk();
        this.chunkOffsets = stco != null ? stco.getChunkOffsets() : co64.getChunkOffsets();
        TimeToSampleBox.TimeToSampleEntry[] arr$ = this.timeToSamples;
        for (TimeToSampleBox.TimeToSampleEntry ttse : arr$) {
            this.duration += ttse.getSampleCount() * ttse.getSampleDuration();
        }
        this.box = trak;
        this.timescale = trak.getTimescale();
    }

    public int pts2Sample(long _tv, int _timescale) {
        long tv = (this.timescale * _tv) / _timescale;
        int sample = 0;
        int ttsInd = 0;
        while (ttsInd < this.timeToSamples.length - 1) {
            int a = this.timeToSamples[ttsInd].getSampleCount() * this.timeToSamples[ttsInd].getSampleDuration();
            if (tv < a) {
                break;
            }
            tv -= a;
            sample += this.timeToSamples[ttsInd].getSampleCount();
            ttsInd++;
        }
        return ((int) (tv / this.timeToSamples[ttsInd].getSampleDuration())) + sample;
    }

    public TrackType getType() {
        return this.type;
    }

    public int getNo() {
        return this.f1545no;
    }

    public SampleEntry[] getSampleEntries() {
        return this.sampleEntries;
    }

    public TrakBox getBox() {
        return this.box;
    }

    public long getTimescale() {
        return this.timescale;
    }

    public boolean canSeek(long pts) {
        return pts >= 0 && pts < this.duration;
    }

    public synchronized boolean seek(long pts) {
        boolean z = false;
        synchronized (this) {
            if (pts < 0) {
                throw new IllegalArgumentException("Seeking to negative pts");
            }
            if (pts < this.duration) {
                long prevDur = 0;
                int frameNo = 0;
                this.sttsInd = 0;
                while (pts > (this.timeToSamples[this.sttsInd].getSampleCount() * this.timeToSamples[this.sttsInd].getSampleDuration()) + prevDur && this.sttsInd < this.timeToSamples.length - 1) {
                    prevDur += this.timeToSamples[this.sttsInd].getSampleCount() * this.timeToSamples[this.sttsInd].getSampleDuration();
                    frameNo += this.timeToSamples[this.sttsInd].getSampleCount();
                    this.sttsInd++;
                }
                this.sttsSubInd = (int) ((pts - prevDur) / this.timeToSamples[this.sttsInd].getSampleDuration());
                int frameNo2 = frameNo + this.sttsSubInd;
                this.pts = (this.timeToSamples[this.sttsInd].getSampleDuration() * this.sttsSubInd) + prevDur;
                seekPointer(frameNo2);
                z = true;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shiftPts(long frames) {
        this.pts -= this.sttsSubInd * this.timeToSamples[this.sttsInd].getSampleDuration();
        this.sttsSubInd = (int) (this.sttsSubInd + frames);
        while (this.sttsInd < this.timeToSamples.length - 1 && this.sttsSubInd >= this.timeToSamples[this.sttsInd].getSampleCount()) {
            this.pts += this.timeToSamples[this.sttsInd].getSegmentDuration();
            this.sttsSubInd -= this.timeToSamples[this.sttsInd].getSampleCount();
            this.sttsInd++;
        }
        this.pts += this.sttsSubInd * this.timeToSamples[this.sttsInd].getSampleDuration();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void nextChunk() {
        if (this.stcoInd < this.chunkOffsets.length) {
            this.stcoInd++;
            if (this.stscInd + 1 < this.sampleToChunks.length && this.stcoInd + 1 == this.sampleToChunks[this.stscInd + 1].getFirst()) {
                this.stscInd++;
            }
        }
    }

    @Override // org.jcodec.common.SeekableDemuxerTrack
    public synchronized boolean gotoFrame(long frameNo) {
        boolean z = true;
        synchronized (this) {
            if (frameNo < 0) {
                throw new IllegalArgumentException("negative frame number");
            }
            if (frameNo >= getFrameCount()) {
                z = false;
            } else if (frameNo != this.curFrame) {
                seekPointer(frameNo);
                seekPts(frameNo);
            }
        }
        return z;
    }

    @Override // org.jcodec.common.SeekableDemuxerTrack
    public void seek(double second) {
        seek((long) (this.timescale * second));
    }

    private void seekPts(long frameNo) {
        this.sttsSubInd = 0;
        this.sttsInd = 0;
        this.pts = 0;
        shiftPts(frameNo);
    }

    public RationalLarge getDuration() {
        return new RationalLarge(this.box.getMediaDuration(), this.box.getTimescale());
    }

    @Override // org.jcodec.common.SeekableDemuxerTrack
    public long getCurFrame() {
        return this.curFrame;
    }

    public List<Edit> getEdits() {
        EditListBox editListBox = (EditListBox) Box.findFirst(this.box, EditListBox.class, "edts", "elst");
        if (editListBox != null) {
            return editListBox.getEdits();
        }
        return null;
    }

    public String getName() {
        NameBox nameBox = (NameBox) Box.findFirst(this.box, NameBox.class, "udta", "name");
        if (nameBox != null) {
            return nameBox.getName();
        }
        return null;
    }

    public String getFourcc() {
        return getSampleEntries()[0].getFourcc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ByteBuffer readPacketData(SeekableByteChannel input, ByteBuffer buffer, long offset, int size) throws IOException {
        ByteBuffer result = buffer.duplicate();
        synchronized (input) {
            input.position(offset);
            NIOUtils.read(input, result, size);
        }
        result.flip();
        return result;
    }
}
