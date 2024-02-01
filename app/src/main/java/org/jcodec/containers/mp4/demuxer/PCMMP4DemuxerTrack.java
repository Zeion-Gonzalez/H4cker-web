package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.QTTimeUtil;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class PCMMP4DemuxerTrack extends AbstractMP4DemuxerTrack {
    private int defaultSampleSize;
    private SeekableByteChannel input;
    private MovieBox movie;
    private int posShift;
    protected int totalFrames;

    public PCMMP4DemuxerTrack(MovieBox movie, TrakBox trak, SeekableByteChannel input) {
        super(trak);
        this.movie = movie;
        this.input = input;
        SampleSizesBox stsz = (SampleSizesBox) Box.findFirst(trak, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        this.defaultSampleSize = stsz.getDefaultSize();
        int chunks = 0;
        for (int i = 1; i < this.sampleToChunks.length; i++) {
            int ch = (int) (this.sampleToChunks[i].getFirst() - this.sampleToChunks[i - 1].getFirst());
            this.totalFrames += this.sampleToChunks[i - 1].getCount() * ch;
            chunks += ch;
        }
        this.totalFrames += this.sampleToChunks[this.sampleToChunks.length - 1].getCount() * (this.chunkOffsets.length - chunks);
    }

    @Override // org.jcodec.common.DemuxerTrack
    public Packet nextFrame() throws IOException {
        int frameSize = getFrameSize();
        int chSize = (this.sampleToChunks[this.stscInd].getCount() * frameSize) - this.posShift;
        return nextFrame(ByteBuffer.allocate(chSize));
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    public synchronized MP4Packet nextFrame(ByteBuffer buffer) throws IOException {
        MP4Packet mP4Packet;
        if (this.stcoInd >= this.chunkOffsets.length) {
            mP4Packet = null;
        } else {
            int frameSize = getFrameSize();
            int se = this.sampleToChunks[this.stscInd].getEntry();
            int chSize = this.sampleToChunks[this.stscInd].getCount() * frameSize;
            long pktOff = this.chunkOffsets[this.stcoInd] + this.posShift;
            int pktSize = chSize - this.posShift;
            ByteBuffer result = readPacketData(this.input, buffer, pktOff, pktSize);
            long ptsRem = this.pts;
            int doneFrames = pktSize / frameSize;
            shiftPts(doneFrames);
            mP4Packet = new MP4Packet(result, QTTimeUtil.mediaToEdited(this.box, ptsRem, this.movie.getTimescale()), this.timescale, (int) (this.pts - ptsRem), this.curFrame, true, null, ptsRem, se - 1, pktOff, pktSize, true);
            this.curFrame += doneFrames;
            this.posShift = 0;
            this.stcoInd++;
            if (this.stscInd < this.sampleToChunks.length - 1 && this.stcoInd + 1 == this.sampleToChunks[this.stscInd + 1].getFirst()) {
                this.stscInd++;
            }
        }
        return mP4Packet;
    }

    @Override // org.jcodec.common.SeekableDemuxerTrack
    public boolean gotoSyncFrame(long frameNo) {
        return gotoFrame(frameNo);
    }

    public int getFrameSize() {
        SampleEntry entry = this.sampleEntries[this.sampleToChunks[this.stscInd].getEntry() - 1];
        return entry instanceof AudioSampleEntry ? ((AudioSampleEntry) entry).calcFrameSize() : this.defaultSampleSize;
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    protected void seekPointer(long frameNo) {
        this.stcoInd = 0;
        this.stscInd = 0;
        this.curFrame = 0L;
        while (true) {
            long nextFrame = this.curFrame + this.sampleToChunks[this.stscInd].getCount();
            if (nextFrame <= frameNo) {
                this.curFrame = nextFrame;
                nextChunk();
            } else {
                this.posShift = (int) ((frameNo - this.curFrame) * getFrameSize());
                this.curFrame = frameNo;
                return;
            }
        }
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    public long getFrameCount() {
        return this.totalFrames;
    }

    @Override // org.jcodec.common.DemuxerTrack
    public DemuxerTrackMeta getMeta() {
        return new DemuxerTrackMeta(DemuxerTrackMeta.Type.AUDIO, null, this.totalFrames, this.duration / this.timescale, null);
    }
}
