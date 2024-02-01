package org.jcodec.codecs.wav;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.ReadableByteChannel;
import org.jcodec.audio.AudioSource;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.AudioUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class WavInput implements Closeable {
    protected AudioFormat format;
    protected WavHeader header;

    /* renamed from: in */
    protected ReadableByteChannel f1494in;
    protected byte[] prevBuf;

    public WavInput(ReadableByteChannel in) throws IOException {
        this.header = WavHeader.read(in);
        this.format = this.header.getFormat();
        this.f1494in = in;
    }

    public int read(ByteBuffer buf) throws IOException {
        int maxRead = this.format.framesToBytes(this.format.bytesToFrames(buf.remaining()));
        return NIOUtils.read(this.f1494in, buf, maxRead);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.f1494in.close();
    }

    public WavHeader getHeader() {
        return this.header;
    }

    public AudioFormat getFormat() {
        return this.format;
    }

    /* loaded from: classes.dex */
    public static class File extends WavInput {
        public File(java.io.File f) throws IOException {
            super(NIOUtils.readableFileChannel(f));
        }

        @Override // org.jcodec.codecs.wav.WavInput, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            this.f1494in.close();
        }
    }

    /* loaded from: classes.dex */
    public static class Source implements AudioSource, Closeable {
        private AudioFormat format;
        private int pos;
        private WavInput src;

        public Source(WavInput src) {
            this.src = src;
            this.format = src.getFormat();
        }

        public Source(ReadableByteChannel ch) throws IOException {
            this(new WavInput(ch));
        }

        public Source(java.io.File file) throws IOException {
            this(new File(file));
        }

        @Override // org.jcodec.audio.AudioSource
        public AudioFormat getFormat() {
            return this.src.getFormat();
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.src.close();
        }

        public int read(int[] samples, int max) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(this.format.samplesToBytes(Math.min(max, samples.length)));
            int read = this.src.read(bb);
            bb.flip();
            AudioUtil.toInt(this.format, bb, samples);
            return this.format.bytesToFrames(read);
        }

        @Override // org.jcodec.audio.AudioSource
        public int read(FloatBuffer samples) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(this.format.samplesToBytes(samples.remaining()));
            int i = this.src.read(bb);
            if (i == -1) {
                return -1;
            }
            bb.flip();
            AudioUtil.toFloat(this.format, bb, samples);
            int read = this.format.bytesToFrames(i);
            this.pos += read;
            return read;
        }
    }
}
