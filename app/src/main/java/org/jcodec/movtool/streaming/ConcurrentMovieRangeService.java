package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class ConcurrentMovieRangeService {
    private ExecutorService exec;
    private VirtualMovie movie;

    public ConcurrentMovieRangeService(VirtualMovie movie, int nThreads) {
        this.exec = Executors.newFixedThreadPool(nThreads, new ThreadFactory() { // from class: org.jcodec.movtool.streaming.ConcurrentMovieRangeService.1
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        });
        this.movie = movie;
    }

    public void shutdown() {
        this.exec.shutdown();
    }

    public InputStream getRange(long from, long to) throws IOException {
        return new ConcurrentMovieRange(from, to);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GetCallable implements Callable<ByteBuffer> {
        private MovieSegment segment;

        public GetCallable(MovieSegment segment) {
            this.segment = segment;
        }

        @Override // java.util.concurrent.Callable
        public ByteBuffer call() throws Exception {
            return MovieRange.checkDataLen(this.segment.getData() == null ? null : this.segment.getData().duplicate(), this.segment.getDataLen());
        }
    }

    /* loaded from: classes.dex */
    public class ConcurrentMovieRange extends InputStream {
        private static final int READ_AHEAD_SEGMENTS = 10;
        private int nextReadAheadNo;
        private long remaining;
        private List<Future<ByteBuffer>> segments = new ArrayList();

        /* renamed from: to */
        private long f1559to;

        public ConcurrentMovieRange(long from, long to) throws IOException {
            if (to < from) {
                throw new IllegalArgumentException("from < to");
            }
            this.remaining = (to - from) + 1;
            this.f1559to = to;
            MovieSegment segment = ConcurrentMovieRangeService.this.movie.getPacketAt(from);
            if (segment != null) {
                this.nextReadAheadNo = segment.getNo();
                scheduleSegmentRetrieve(segment);
                for (int i = 0; i < 10; i++) {
                    tryReadAhead();
                }
                ByteBuffer data = segmentData();
                NIOUtils.skip(data, (int) (from - segment.getPos()));
            }
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int from, int len) throws IOException {
            if (this.segments.size() == 0 || this.remaining == 0) {
                return -1;
            }
            int len2 = (int) Math.min(len, this.remaining);
            int totalRead = 0;
            while (len2 > 0 && this.segments.size() > 0) {
                ByteBuffer segmentData = segmentData();
                int toRead = Math.min(segmentData.remaining(), len2);
                segmentData.get(b, from, toRead);
                totalRead += toRead;
                len2 -= toRead;
                from += toRead;
                disposeReadAhead(segmentData);
            }
            this.remaining -= totalRead;
            return totalRead;
        }

        private void disposeReadAhead(ByteBuffer segmentData) {
            if (!segmentData.hasRemaining()) {
                this.segments.remove(0);
                tryReadAhead();
            }
        }

        private void tryReadAhead() {
            MovieSegment segment = ConcurrentMovieRangeService.this.movie.getPacketByNo(this.nextReadAheadNo);
            if (segment != null && segment.getPos() < this.f1559to) {
                scheduleSegmentRetrieve(segment);
            }
        }

        private void scheduleSegmentRetrieve(MovieSegment segment) {
            Future<ByteBuffer> submit = ConcurrentMovieRangeService.this.exec.submit(new GetCallable(segment));
            this.segments.add(submit);
            this.nextReadAheadNo++;
        }

        private ByteBuffer segmentData() throws IOException {
            try {
                ByteBuffer segmentData = this.segments.get(0).get();
                return segmentData;
            } catch (InterruptedException e) {
                throw new IOException(e);
            } catch (ExecutionException e2) {
                throw new IOException(e2);
            }
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            for (Future<ByteBuffer> future : this.segments) {
                future.cancel(false);
            }
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.segments.size() == 0 || this.remaining == 0) {
                return -1;
            }
            ByteBuffer segmentData = segmentData();
            int i = segmentData.get() & 255;
            disposeReadAhead(segmentData);
            this.remaining--;
            return i;
        }
    }
}
