package org.jcodec.containers.mp4.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jcodec.common.Assert;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.LongArrayList;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.common.model.Unit;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.CompositionOffsetsBox;
import org.jcodec.containers.mp4.boxes.Edit;
import org.jcodec.containers.mp4.boxes.HandlerBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MediaBox;
import org.jcodec.containers.mp4.boxes.MediaHeaderBox;
import org.jcodec.containers.mp4.boxes.MediaInfoBox;
import org.jcodec.containers.mp4.boxes.MovieHeaderBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.SyncSamplesBox;
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrackHeaderBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class FramesMP4MuxerTrack extends AbstractMP4MuxerTrack {
    private boolean allIframes;
    private LongArrayList chunkOffsets;
    private List<CompositionOffsetsBox.Entry> compositionOffsets;
    private long curDuration;
    private int curFrame;
    private IntArrayList iframes;
    private int lastCompositionOffset;
    private int lastCompositionSamples;
    private int lastEntry;
    private SeekableByteChannel out;
    private long ptsEstimate;
    private long sameDurCount;
    private List<TimeToSampleBox.TimeToSampleEntry> sampleDurations;
    private IntArrayList sampleSizes;
    private TimecodeMP4MuxerTrack timecodeTrack;
    private long trackTotalDuration;

    public FramesMP4MuxerTrack(SeekableByteChannel out, int trackId, TrackType type, int timescale) {
        super(trackId, type, timescale);
        this.sampleDurations = new ArrayList();
        this.sameDurCount = 0L;
        this.curDuration = -1L;
        this.chunkOffsets = new LongArrayList();
        this.sampleSizes = new IntArrayList();
        this.iframes = new IntArrayList();
        this.compositionOffsets = new ArrayList();
        this.lastCompositionOffset = 0;
        this.lastCompositionSamples = 0;
        this.ptsEstimate = 0L;
        this.lastEntry = -1;
        this.allIframes = true;
        this.out = out;
        setTgtChunkDuration(new Rational(1, 1), Unit.FRAME);
    }

    public void addFrame(MP4Packet pkt) throws IOException {
        if (this.finished) {
            throw new IllegalStateException("The muxer track has finished muxing");
        }
        int entryNo = pkt.getEntryNo() + 1;
        int compositionOffset = (int) (pkt.getPts() - this.ptsEstimate);
        if (compositionOffset != this.lastCompositionOffset) {
            if (this.lastCompositionSamples > 0) {
                this.compositionOffsets.add(new CompositionOffsetsBox.Entry(this.lastCompositionSamples, this.lastCompositionOffset));
            }
            this.lastCompositionOffset = compositionOffset;
            this.lastCompositionSamples = 0;
        }
        this.lastCompositionSamples++;
        this.ptsEstimate += pkt.getDuration();
        if (this.lastEntry != -1 && this.lastEntry != entryNo) {
            outChunk(this.lastEntry);
            this.samplesInLastChunk = -1;
        }
        this.curChunk.add(pkt.getData());
        if (pkt.isKeyFrame()) {
            this.iframes.add(this.curFrame + 1);
        } else {
            this.allIframes = false;
        }
        this.curFrame++;
        this.chunkDuration += pkt.getDuration();
        if (this.curDuration != -1 && pkt.getDuration() != this.curDuration) {
            this.sampleDurations.add(new TimeToSampleBox.TimeToSampleEntry((int) this.sameDurCount, (int) this.curDuration));
            this.sameDurCount = 0L;
        }
        this.curDuration = pkt.getDuration();
        this.sameDurCount++;
        this.trackTotalDuration += pkt.getDuration();
        outChunkIfNeeded(entryNo);
        processTimecode(pkt);
        this.lastEntry = entryNo;
    }

    private void processTimecode(MP4Packet pkt) throws IOException {
        if (this.timecodeTrack != null) {
            this.timecodeTrack.addTimecode(pkt);
        }
    }

    private void outChunkIfNeeded(int entryNo) throws IOException {
        Assert.assertTrue(this.tgtChunkDurationUnit == Unit.FRAME || this.tgtChunkDurationUnit == Unit.SEC);
        if (this.tgtChunkDurationUnit == Unit.FRAME && this.curChunk.size() * this.tgtChunkDuration.getDen() == this.tgtChunkDuration.getNum()) {
            outChunk(entryNo);
        } else if (this.tgtChunkDurationUnit == Unit.SEC && this.chunkDuration > 0 && this.chunkDuration * this.tgtChunkDuration.getDen() >= this.tgtChunkDuration.getNum() * this.timescale) {
            outChunk(entryNo);
        }
    }

    void outChunk(int entryNo) throws IOException {
        if (this.curChunk.size() != 0) {
            this.chunkOffsets.add(this.out.position());
            for (ByteBuffer bs : this.curChunk) {
                this.sampleSizes.add(bs.remaining());
                this.out.write(bs);
            }
            if (this.samplesInLastChunk == -1 || this.samplesInLastChunk != this.curChunk.size()) {
                this.samplesInChunks.add(new SampleToChunkBox.SampleToChunkEntry(this.chunkNo + 1, this.curChunk.size(), entryNo));
            }
            this.samplesInLastChunk = this.curChunk.size();
            this.chunkNo++;
            this.chunkDuration = 0L;
            this.curChunk.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack
    public Box finish(MovieHeaderBox mvhd) throws IOException {
        if (this.finished) {
            throw new IllegalStateException("The muxer track has finished muxing");
        }
        outChunk(this.lastEntry);
        if (this.sameDurCount > 0) {
            this.sampleDurations.add(new TimeToSampleBox.TimeToSampleEntry((int) this.sameDurCount, (int) this.curDuration));
        }
        this.finished = true;
        TrakBox trak = new TrakBox();
        Size dd = getDisplayDimensions();
        TrackHeaderBox tkhd = new TrackHeaderBox(this.trackId, (mvhd.getTimescale() * this.trackTotalDuration) / this.timescale, dd.getWidth(), dd.getHeight(), new Date().getTime(), new Date().getTime(), 1.0f, (short) 0, 0L, new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824});
        tkhd.setFlags(15);
        trak.add(tkhd);
        tapt(trak);
        MediaBox media = new MediaBox();
        trak.add(media);
        media.add(new MediaHeaderBox(this.timescale, this.trackTotalDuration, 0, new Date().getTime(), new Date().getTime(), 0));
        HandlerBox hdlr = new HandlerBox("mhlr", this.type.getHandler(), "appl", 0, 0);
        media.add(hdlr);
        MediaInfoBox minf = new MediaInfoBox();
        media.add(minf);
        mediaHeader(minf, this.type);
        minf.add(new HandlerBox("dhlr", "url ", "appl", 0, 0));
        addDref(minf);
        NodeBox stbl = new NodeBox(new Header("stbl"));
        minf.add(stbl);
        putCompositionOffsets(stbl);
        putEdits(trak);
        putName(trak);
        stbl.add(new SampleDescriptionBox((SampleEntry[]) this.sampleEntries.toArray(new SampleEntry[0])));
        stbl.add(new SampleToChunkBox((SampleToChunkBox.SampleToChunkEntry[]) this.samplesInChunks.toArray(new SampleToChunkBox.SampleToChunkEntry[0])));
        stbl.add(new SampleSizesBox(this.sampleSizes.toArray()));
        stbl.add(new TimeToSampleBox((TimeToSampleBox.TimeToSampleEntry[]) this.sampleDurations.toArray(new TimeToSampleBox.TimeToSampleEntry[0])));
        stbl.add(new ChunkOffsets64Box(this.chunkOffsets.toArray()));
        if (!this.allIframes && this.iframes.size() > 0) {
            stbl.add(new SyncSamplesBox(this.iframes.toArray()));
        }
        return trak;
    }

    private void putCompositionOffsets(NodeBox stbl) {
        if (this.compositionOffsets.size() > 0) {
            this.compositionOffsets.add(new CompositionOffsetsBox.Entry(this.lastCompositionSamples, this.lastCompositionOffset));
            int min = minOffset(this.compositionOffsets);
            if (min > 0) {
                for (CompositionOffsetsBox.Entry entry : this.compositionOffsets) {
                    entry.offset -= min;
                }
            }
            CompositionOffsetsBox.Entry first = this.compositionOffsets.get(0);
            if (first.getOffset() > 0) {
                if (this.edits == null) {
                    this.edits = new ArrayList();
                    this.edits.add(new Edit(this.trackTotalDuration, first.getOffset(), 1.0f));
                } else {
                    for (Edit edit : this.edits) {
                        edit.setMediaTime(edit.getMediaTime() + first.getOffset());
                    }
                }
            }
            stbl.add(new CompositionOffsetsBox((CompositionOffsetsBox.Entry[]) this.compositionOffsets.toArray(new CompositionOffsetsBox.Entry[0])));
        }
    }

    public static int minOffset(List<CompositionOffsetsBox.Entry> offs) {
        int min = Integer.MAX_VALUE;
        for (CompositionOffsetsBox.Entry entry : offs) {
            if (entry.getOffset() < min) {
                min = entry.getOffset();
            }
        }
        return min;
    }

    @Override // org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack
    public long getTrackTotalDuration() {
        return this.trackTotalDuration;
    }

    public void addSampleEntries(SampleEntry[] sampleEntries) {
        for (SampleEntry se : sampleEntries) {
            addSampleEntry(se);
        }
    }

    public TimecodeMP4MuxerTrack getTimecodeTrack() {
        return this.timecodeTrack;
    }

    public void setTimecode(TimecodeMP4MuxerTrack timecodeTrack) {
        this.timecodeTrack = timecodeTrack;
    }
}
