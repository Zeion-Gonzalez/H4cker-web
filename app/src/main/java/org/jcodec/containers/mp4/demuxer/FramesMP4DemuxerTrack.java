package org.jcodec.containers.mp4.demuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.QTTimeUtil;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.CompositionOffsetsBox;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.SampleSizesBox;
import org.jcodec.containers.mp4.boxes.SyncSamplesBox;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class FramesMP4DemuxerTrack extends AbstractMP4DemuxerTrack {
    private CompositionOffsetsBox.Entry[] compOffsets;
    private int cttsInd;
    private int cttsSubInd;
    private SeekableByteChannel input;
    private MovieBox movie;
    private int noInChunk;
    private long offInChunk;
    private int[] partialSync;
    private int psOff;
    private int[] sizes;
    private int ssOff;
    private int[] syncSamples;

    public FramesMP4DemuxerTrack(MovieBox mov, TrakBox trak, SeekableByteChannel input) {
        super(trak);
        this.input = input;
        this.movie = mov;
        SampleSizesBox stsz = (SampleSizesBox) Box.findFirst(trak, SampleSizesBox.class, "mdia", "minf", "stbl", "stsz");
        SyncSamplesBox stss = (SyncSamplesBox) Box.findFirst(trak, SyncSamplesBox.class, "mdia", "minf", "stbl", "stss");
        SyncSamplesBox stps = (SyncSamplesBox) Box.findFirst(trak, SyncSamplesBox.class, "mdia", "minf", "stbl", "stps");
        CompositionOffsetsBox ctts = (CompositionOffsetsBox) Box.findFirst(trak, CompositionOffsetsBox.class, "mdia", "minf", "stbl", "ctts");
        this.compOffsets = ctts == null ? null : ctts.getEntries();
        if (stss != null) {
            this.syncSamples = stss.getSyncSamples();
        }
        if (stps != null) {
            this.partialSync = stps.getSyncSamples();
        }
        this.sizes = stsz.getSizes();
    }

    @Override // org.jcodec.common.DemuxerTrack
    public synchronized MP4Packet nextFrame() throws IOException {
        MP4Packet nextFrame;
        if (this.curFrame >= this.sizes.length) {
            nextFrame = null;
        } else {
            int size = this.sizes[(int) this.curFrame];
            nextFrame = nextFrame(ByteBuffer.allocate(size));
        }
        return nextFrame;
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    public synchronized MP4Packet nextFrame(ByteBuffer storage) throws IOException {
        MP4Packet mP4Packet;
        if (this.curFrame >= this.sizes.length) {
            mP4Packet = null;
        } else {
            int size = this.sizes[(int) this.curFrame];
            if (storage != null && storage.remaining() < size) {
                throw new IllegalArgumentException("Buffer size is not enough to fit a packet");
            }
            long pktPos = this.chunkOffsets[this.stcoInd] + this.offInChunk;
            ByteBuffer result = readPacketData(this.input, storage, pktPos, size);
            if (result != null && result.remaining() < size) {
                mP4Packet = null;
            } else {
                int duration = this.timeToSamples[this.sttsInd].getSampleDuration();
                boolean sync = this.syncSamples == null;
                if (this.syncSamples != null && this.ssOff < this.syncSamples.length && this.curFrame + 1 == this.syncSamples[this.ssOff]) {
                    sync = true;
                    this.ssOff++;
                }
                boolean psync = false;
                if (this.partialSync != null && this.psOff < this.partialSync.length && this.curFrame + 1 == this.partialSync[this.psOff]) {
                    psync = true;
                    this.psOff++;
                }
                long realPts = this.pts;
                if (this.compOffsets != null) {
                    realPts = this.pts + this.compOffsets[this.cttsInd].getOffset();
                    this.cttsSubInd++;
                    if (this.cttsInd < this.compOffsets.length - 1 && this.cttsSubInd == this.compOffsets[this.cttsInd].getCount()) {
                        this.cttsInd++;
                        this.cttsSubInd = 0;
                    }
                }
                mP4Packet = new MP4Packet(result, QTTimeUtil.mediaToEdited(this.box, realPts, this.movie.getTimescale()), this.timescale, duration, this.curFrame, sync, null, realPts, this.sampleToChunks[this.stscInd].getEntry() - 1, pktPos, size, psync);
                this.offInChunk += size;
                this.curFrame++;
                this.noInChunk++;
                if (this.noInChunk >= this.sampleToChunks[this.stscInd].getCount()) {
                    this.noInChunk = 0;
                    this.offInChunk = 0L;
                    nextChunk();
                }
                shiftPts(1L);
            }
        }
        return mP4Packet;
    }

    @Override // org.jcodec.common.SeekableDemuxerTrack
    public boolean gotoSyncFrame(long frameNo) {
        if (this.syncSamples == null) {
            return gotoFrame(frameNo);
        }
        if (frameNo < 0) {
            throw new IllegalArgumentException("negative frame number");
        }
        if (frameNo >= getFrameCount()) {
            return false;
        }
        if (frameNo == this.curFrame) {
            return true;
        }
        for (int i = 0; i < this.syncSamples.length; i++) {
            if (((long) (this.syncSamples[i] - 1)) > frameNo) {
                return gotoFrame((long) (this.syncSamples[i - 1] - 1));
            }
        }
        return gotoFrame((long) (this.syncSamples[this.syncSamples.length - 1] - 1));
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    protected void seekPointer(long frameNo) {
        if (this.compOffsets != null) {
            this.cttsSubInd = (int) frameNo;
            this.cttsInd = 0;
            while (this.cttsSubInd >= this.compOffsets[this.cttsInd].getCount()) {
                this.cttsSubInd -= this.compOffsets[this.cttsInd].getCount();
                this.cttsInd++;
            }
        }
        this.curFrame = (int) frameNo;
        this.stcoInd = 0;
        this.stscInd = 0;
        this.noInChunk = (int) frameNo;
        this.offInChunk = 0L;
        while (this.noInChunk >= this.sampleToChunks[this.stscInd].getCount()) {
            this.noInChunk -= this.sampleToChunks[this.stscInd].getCount();
            nextChunk();
        }
        for (int i = 0; i < this.noInChunk; i++) {
            this.offInChunk += this.sizes[(((int) frameNo) - this.noInChunk) + i];
        }
        if (this.syncSamples != null) {
            this.ssOff = 0;
            while (this.ssOff < this.syncSamples.length && this.syncSamples[this.ssOff] < this.curFrame + 1) {
                this.ssOff++;
            }
        }
        if (this.partialSync != null) {
            this.psOff = 0;
            while (this.psOff < this.partialSync.length && this.partialSync[this.psOff] < this.curFrame + 1) {
                this.psOff++;
            }
        }
    }

    @Override // org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack
    public long getFrameCount() {
        return this.sizes.length;
    }

    @Override // org.jcodec.common.DemuxerTrack
    public DemuxerTrackMeta getMeta() {
        int[] copyOf = Arrays.copyOf(this.syncSamples, this.syncSamples.length);
        for (int i = 0; i < copyOf.length; i++) {
            copyOf[i] = copyOf[i] - 1;
        }
        TrackType type = getType();
        return new DemuxerTrackMeta(type == TrackType.VIDEO ? DemuxerTrackMeta.Type.VIDEO : type == TrackType.SOUND ? DemuxerTrackMeta.Type.AUDIO : DemuxerTrackMeta.Type.OTHER, copyOf, this.sizes.length, this.duration / this.timescale, this.box.getCodedSize());
    }
}
