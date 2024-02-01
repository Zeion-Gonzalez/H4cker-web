package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class MovieRange extends InputStream {
    private ByteBuffer chunkData;
    private int chunkNo;
    private VirtualMovie movie;
    private long remaining;

    public MovieRange(VirtualMovie movie, long from, long to) throws IOException {
        if (to < from) {
            throw new IllegalArgumentException("from < to");
        }
        this.movie = movie;
        MovieSegment chunk = movie.getPacketAt(from);
        this.remaining = (to - from) + 1;
        if (chunk != null) {
            this.chunkData = checkDataLen(chunk.getData(), chunk.getDataLen());
            this.chunkNo = chunk.getNo();
            NIOUtils.skip(this.chunkData, (int) (from - chunk.getPos()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteBuffer checkDataLen(ByteBuffer chunkData, int chunkDataLen) throws IOException {
        if (chunkData == null) {
            System.err.println("WARN: packet expected data len != actual data len " + chunkDataLen + " != 0");
            return ByteBuffer.allocate(chunkDataLen);
        }
        if (chunkData.remaining() != chunkDataLen) {
            System.err.println("WARN: packet expected data len != actual data len " + chunkDataLen + " != " + chunkData.remaining());
            int chunkDataLen2 = Math.max(0, chunkDataLen);
            if (chunkDataLen2 < chunkData.remaining() || chunkData.capacity() - chunkData.position() >= chunkDataLen2) {
                chunkData.limit(chunkData.position() + chunkDataLen2);
            } else {
                ByteBuffer correct = ByteBuffer.allocate(chunkDataLen2);
                correct.put(chunkData);
                correct.clear();
                return correct;
            }
        }
        return chunkData;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int from, int len) throws IOException {
        tryFetch();
        if (this.chunkData == null || this.remaining == 0) {
            return -1;
        }
        int len2 = (int) Math.min(this.remaining, len);
        int totalRead = 0;
        while (len2 > 0) {
            int toRead = Math.min(this.chunkData.remaining(), len2);
            this.chunkData.get(b, from, toRead);
            totalRead += toRead;
            len2 -= toRead;
            from += toRead;
            tryFetch();
            if (this.chunkData == null) {
                break;
            }
        }
        this.remaining -= totalRead;
        return totalRead;
    }

    private void tryFetch() throws IOException {
        if (this.chunkData == null || !this.chunkData.hasRemaining()) {
            MovieSegment chunk = this.movie.getPacketByNo(this.chunkNo + 1);
            if (chunk != null) {
                this.chunkData = checkDataLen(chunk.getData(), chunk.getDataLen());
                this.chunkNo = chunk.getNo();
            } else {
                this.chunkData = null;
            }
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        tryFetch();
        if (this.chunkData == null || this.remaining == 0) {
            return -1;
        }
        this.remaining--;
        return this.chunkData.get() & 255;
    }
}
