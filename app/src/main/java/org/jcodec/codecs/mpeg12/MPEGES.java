package org.jcodec.codecs.mpeg12;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.jcodec.containers.mps.MPSDemuxer;

/* loaded from: classes.dex */
public class MPEGES extends SegmentReader {
    public long curPts;
    private int frameNo;

    public MPEGES(ReadableByteChannel channel) throws IOException {
        super(channel, 4096);
    }

    public MPEGES(ReadableByteChannel channel, int fetchSize) throws IOException {
        super(channel, fetchSize);
    }

    public MPSDemuxer.MPEGPacket getFrame(ByteBuffer buffer) throws IOException {
        ByteBuffer dup = buffer.duplicate();
        while (this.curMarker != 256 && this.curMarker != 435 && skipToMarker()) {
        }
        while (this.curMarker != 256 && readToNextMarker(dup)) {
        }
        readToNextMarker(dup);
        while (this.curMarker != 256 && this.curMarker != 435 && readToNextMarker(dup)) {
        }
        dup.flip();
        if (!dup.hasRemaining()) {
            return null;
        }
        long j = this.curPts;
        int i = this.frameNo;
        this.frameNo = i + 1;
        return new MPSDemuxer.MPEGPacket(dup, j, 90000L, 0L, i, true, null);
    }
}
