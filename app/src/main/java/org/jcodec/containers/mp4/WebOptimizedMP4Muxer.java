package org.jcodec.containers.mp4;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.logging.Logger;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

/* loaded from: classes.dex */
public class WebOptimizedMP4Muxer extends MP4Muxer {
    private ByteBuffer header;
    private long headerPos;

    public static WebOptimizedMP4Muxer withOldHeader(SeekableByteChannel output, Brand brand, MovieBox oldHeader) throws IOException {
        int size;
        int size2 = (int) oldHeader.getHeader().getSize();
        TrakBox vt = oldHeader.getVideoTrack();
        SampleToChunkBox stsc = (SampleToChunkBox) Box.findFirst(vt, SampleToChunkBox.class, "mdia", "minf", "stbl", "stsc");
        int size3 = (size2 - (stsc.getSampleToChunk().length * 12)) + 12;
        ChunkOffsetsBox stco = (ChunkOffsetsBox) Box.findFirst(vt, ChunkOffsetsBox.class, "mdia", "minf", "stbl", "stco");
        if (stco != null) {
            size = (size3 - (stco.getChunkOffsets().length << 2)) + (vt.getFrameCount() << 3);
        } else {
            ChunkOffsets64Box co64 = (ChunkOffsets64Box) Box.findFirst(vt, ChunkOffsets64Box.class, "mdia", "minf", "stbl", "co64");
            size = (size3 - (co64.getChunkOffsets().length << 3)) + (vt.getFrameCount() << 3);
        }
        return new WebOptimizedMP4Muxer(output, brand, (size >> 1) + size);
    }

    public WebOptimizedMP4Muxer(SeekableByteChannel output, Brand brand, int headerSize) throws IOException {
        super(output, brand);
        this.headerPos = output.position() - 24;
        output.position(this.headerPos);
        this.header = ByteBuffer.allocate(headerSize);
        output.write(this.header);
        this.header.clear();
        new Header("wide", 8L).write(output);
        new Header("mdat", 1L).write(output);
        this.mdatOffset = output.position();
        NIOUtils.writeLong(output, 0L);
    }

    @Override // org.jcodec.containers.mp4.muxer.MP4Muxer
    public void storeHeader(MovieBox movie) throws IOException {
        long mdatEnd = this.out.position();
        long mdatSize = (mdatEnd - this.mdatOffset) + 8;
        this.out.position(this.mdatOffset);
        NIOUtils.writeLong(this.out, mdatSize);
        this.out.position(this.headerPos);
        try {
            movie.write(this.header);
            this.header.flip();
            int rem = this.header.capacity() - this.header.limit();
            if (rem < 8) {
                this.header.duplicate().putInt(this.header.capacity());
            }
            this.out.write(this.header);
            if (rem >= 8) {
                new Header("free", rem).write(this.out);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.warn("Could not web-optimize, header is bigger then allocated space.");
            new Header("free", this.header.remaining()).write(this.out);
            this.out.position(mdatEnd);
            MP4Util.writeMovie(this.out, movie);
        }
    }
}
