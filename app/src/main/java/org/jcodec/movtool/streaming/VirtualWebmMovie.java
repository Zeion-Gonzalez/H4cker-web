package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.containers.mkv.MKVStreamingMuxer;

/* loaded from: classes.dex */
public class VirtualWebmMovie extends VirtualMovie {
    private MKVStreamingMuxer muxer;

    public VirtualWebmMovie(VirtualTrack... tracks) throws IOException {
        super(tracks);
        this.muxer = null;
        this.muxer = new MKVStreamingMuxer();
        muxTracks();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.movtool.streaming.VirtualMovie
    public void muxTracks() throws IOException {
        int i;
        List<MovieSegment> chch = new ArrayList<>();
        VirtualPacket[] heads = new VirtualPacket[this.tracks.length];
        VirtualPacket[] tails = new VirtualPacket[this.tracks.length];
        long currentlyAddedContentSize = 0;
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
                MovieSegment packetChunk = packetChunk(this.tracks[min], heads[min], curChunk, min, currentlyAddedContentSize);
                chch.add(packetChunk);
                currentlyAddedContentSize += packetChunk.getDataLen();
                tails[min] = heads[min];
                heads[min] = this.tracks[min].nextPacket();
                curChunk++;
            } else {
                this.headerChunk = headerChunk(chch, this.tracks, this.size);
                this.size += this.headerChunk.getDataLen() + currentlyAddedContentSize;
                this.chunks = (MovieSegment[]) chch.toArray(new MovieSegment[0]);
                return;
            }
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualMovie
    protected MovieSegment packetChunk(VirtualTrack track, VirtualPacket pkt, int chunkNo, int trackNo, long previousClustersSize) {
        return this.muxer.preparePacket(track, pkt, chunkNo, trackNo, previousClustersSize);
    }

    @Override // org.jcodec.movtool.streaming.VirtualMovie
    protected MovieSegment headerChunk(List<MovieSegment> chunks, VirtualTrack[] tracks, long dataSize) throws IOException {
        return this.muxer.prepareHeader(chunks, tracks);
    }
}
