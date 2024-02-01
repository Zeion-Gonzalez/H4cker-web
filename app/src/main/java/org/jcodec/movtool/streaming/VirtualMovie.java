package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class VirtualMovie {
    public MovieSegment[] chunks;
    public MovieSegment headerChunk;
    protected long size;
    protected VirtualTrack[] tracks;

    protected abstract MovieSegment headerChunk(List<MovieSegment> list, VirtualTrack[] virtualTrackArr, long j) throws IOException;

    protected abstract MovieSegment packetChunk(VirtualTrack virtualTrack, VirtualPacket virtualPacket, int i, int i2, long j);

    public VirtualMovie(VirtualTrack... tracks) throws IOException {
        this.tracks = tracks;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void muxTracks() throws IOException {
        int i;
        List<MovieSegment> chch = new ArrayList<>();
        VirtualPacket[] heads = new VirtualPacket[this.tracks.length];
        VirtualPacket[] tails = new VirtualPacket[this.tracks.length];
        int curChunk = 1;
        while (true) {
            int min = -1;
            while (i < heads.length) {
                if (heads[i] == null) {
                    heads[i] = this.tracks[i].nextPacket();
                    i = heads[i] == null ? i + 1 : 0;
                }
                if (min == -1 || heads[i].getPts() < heads[min].getPts()) {
                    min = i;
                }
            }
            if (min != -1) {
                chch.add(packetChunk(this.tracks[min], heads[min], curChunk, min, this.size));
                if (heads[min].getDataLen() >= 0) {
                    this.size += heads[min].getDataLen();
                } else {
                    System.err.println("WARN: Negative frame data len!!!");
                }
                tails[min] = heads[min];
                heads[min] = this.tracks[min].nextPacket();
                curChunk++;
            } else {
                this.headerChunk = headerChunk(chch, this.tracks, this.size);
                this.size += this.headerChunk.getDataLen();
                this.chunks = (MovieSegment[]) chch.toArray(new MovieSegment[0]);
                return;
            }
        }
    }

    public void close() throws IOException {
        VirtualTrack[] arr$ = this.tracks;
        for (VirtualTrack virtualTrack : arr$) {
            virtualTrack.close();
        }
    }

    public MovieSegment getPacketAt(long position) throws IOException {
        if (position >= 0 && position < this.headerChunk.getDataLen()) {
            return this.headerChunk;
        }
        for (int i = 0; i < this.chunks.length - 1; i++) {
            if (this.chunks[i + 1].getPos() > position) {
                return this.chunks[i];
            }
        }
        if (position < this.size) {
            return this.chunks[this.chunks.length - 1];
        }
        return null;
    }

    public MovieSegment getPacketByNo(int no) {
        if (no > this.chunks.length) {
            return null;
        }
        if (no == 0) {
            return this.headerChunk;
        }
        return this.chunks[no - 1];
    }

    public long size() {
        return this.size;
    }
}
