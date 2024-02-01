package org.jcodec.containers.mp4.muxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import org.jcodec.common.Assert;
import org.jcodec.common.LongArrayList;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.common.model.Unit;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
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
import org.jcodec.containers.mp4.boxes.TimeToSampleBox;
import org.jcodec.containers.mp4.boxes.TrackHeaderBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class PCMMP4MuxerTrack extends AbstractMP4MuxerTrack {
    private LongArrayList chunkOffsets;
    private int frameDuration;
    private int frameSize;
    private int framesInCurChunk;
    private SeekableByteChannel out;
    private int totalFrames;

    public PCMMP4MuxerTrack(SeekableByteChannel out, int trackId, TrackType type, int timescale, int frameDuration, int frameSize, SampleEntry se) {
        super(trackId, type, timescale);
        this.chunkOffsets = new LongArrayList();
        this.out = out;
        this.frameDuration = frameDuration;
        this.frameSize = frameSize;
        addSampleEntry(se);
        setTgtChunkDuration(new Rational(1, 2), Unit.SEC);
    }

    public void addSamples(ByteBuffer buffer) throws IOException {
        this.curChunk.add(buffer);
        int frames = buffer.remaining() / this.frameSize;
        this.totalFrames += frames;
        this.framesInCurChunk += frames;
        this.chunkDuration += this.frameDuration * frames;
        outChunkIfNeeded();
    }

    private void outChunkIfNeeded() throws IOException {
        Assert.assertTrue(this.tgtChunkDurationUnit == Unit.FRAME || this.tgtChunkDurationUnit == Unit.SEC);
        if (this.tgtChunkDurationUnit == Unit.FRAME && this.framesInCurChunk * this.tgtChunkDuration.getDen() == this.tgtChunkDuration.getNum()) {
            outChunk();
        } else if (this.tgtChunkDurationUnit == Unit.SEC && this.chunkDuration > 0 && this.chunkDuration * this.tgtChunkDuration.getDen() >= this.tgtChunkDuration.getNum() * this.timescale) {
            outChunk();
        }
    }

    private void outChunk() throws IOException {
        if (this.framesInCurChunk != 0) {
            this.chunkOffsets.add(this.out.position());
            for (ByteBuffer b : this.curChunk) {
                this.out.write(b);
            }
            this.curChunk.clear();
            if (this.samplesInLastChunk == -1 || this.framesInCurChunk != this.samplesInLastChunk) {
                this.samplesInChunks.add(new SampleToChunkBox.SampleToChunkEntry(this.chunkNo + 1, this.framesInCurChunk, 1));
            }
            this.samplesInLastChunk = this.framesInCurChunk;
            this.chunkNo++;
            this.framesInCurChunk = 0;
            this.chunkDuration = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack
    public Box finish(MovieHeaderBox mvhd) throws IOException {
        if (this.finished) {
            throw new IllegalStateException("The muxer track has finished muxing");
        }
        outChunk();
        this.finished = true;
        TrakBox trak = new TrakBox();
        Size dd = getDisplayDimensions();
        TrackHeaderBox tkhd = new TrackHeaderBox(this.trackId, ((mvhd.getTimescale() * this.totalFrames) * this.frameDuration) / this.timescale, dd.getWidth(), dd.getHeight(), new Date().getTime(), new Date().getTime(), 1.0f, (short) 0, 0L, new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824});
        tkhd.setFlags(15);
        trak.add(tkhd);
        tapt(trak);
        MediaBox media = new MediaBox();
        trak.add(media);
        media.add(new MediaHeaderBox(this.timescale, this.totalFrames * this.frameDuration, 0, new Date().getTime(), new Date().getTime(), 0));
        HandlerBox hdlr = new HandlerBox("mhlr", this.type.getHandler(), "appl", 0, 0);
        media.add(hdlr);
        MediaInfoBox minf = new MediaInfoBox();
        media.add(minf);
        mediaHeader(minf, this.type);
        minf.add(new HandlerBox("dhlr", "url ", "appl", 0, 0));
        addDref(minf);
        NodeBox stbl = new NodeBox(new Header("stbl"));
        minf.add(stbl);
        putEdits(trak);
        putName(trak);
        stbl.add(new SampleDescriptionBox((SampleEntry[]) this.sampleEntries.toArray(new SampleEntry[0])));
        stbl.add(new SampleToChunkBox((SampleToChunkBox.SampleToChunkEntry[]) this.samplesInChunks.toArray(new SampleToChunkBox.SampleToChunkEntry[0])));
        stbl.add(new SampleSizesBox(this.frameSize, this.totalFrames));
        stbl.add(new TimeToSampleBox(new TimeToSampleBox.TimeToSampleEntry[]{new TimeToSampleBox.TimeToSampleEntry(this.totalFrames, this.frameDuration)}));
        stbl.add(new ChunkOffsets64Box(this.chunkOffsets.toArray()));
        return trak;
    }

    @Override // org.jcodec.containers.mp4.muxer.AbstractMP4MuxerTrack
    public long getTrackTotalDuration() {
        return this.totalFrames * this.frameDuration;
    }
}
