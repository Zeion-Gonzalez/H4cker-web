package org.jcodec.codecs.wav;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.jcodec.audio.AudioSink;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.AudioUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public class WavOutput implements Closeable {
    protected AudioFormat format;
    protected WavHeader header;
    protected SeekableByteChannel out;
    protected int written;

    public WavOutput(SeekableByteChannel out, AudioFormat format) throws IOException {
        this.out = out;
        this.format = format;
        this.header = new WavHeader(format, 0);
        this.header.write(out);
    }

    public void write(ByteBuffer samples) throws IOException {
        this.written += this.out.write(samples);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.position(0L);
        new WavHeader(this.format, this.format.bytesToFrames(this.written)).write(this.out);
        NIOUtils.closeQuietly(this.out);
    }

    /* loaded from: classes.dex */
    public static class File extends WavOutput {
        public File(java.io.File f, AudioFormat format) throws IOException {
            super(NIOUtils.writableFileChannel(f), format);
        }

        @Override // org.jcodec.codecs.wav.WavOutput, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            NIOUtils.closeQuietly(this.out);
        }
    }

    /* loaded from: classes.dex */
    public static class Sink implements AudioSink, Closeable {
        private WavOutput out;

        public Sink(WavOutput out) {
            this.out = out;
        }

        public Sink(java.io.File f, AudioFormat format) throws IOException {
            this(new File(f, format));
        }

        public Sink(SeekableByteChannel ch, AudioFormat format) throws IOException {
            this(new WavOutput(ch, format));
        }

        @Override // org.jcodec.audio.AudioSink
        public void write(FloatBuffer data) throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(this.out.format.samplesToBytes(data.remaining()));
            AudioUtil.fromFloat(data, this.out.format, buf);
            buf.flip();
            this.out.write(buf);
        }

        public void write(int[] data, int len) throws IOException {
            int len2 = Math.min(data.length, len);
            ByteBuffer buf = ByteBuffer.allocate(this.out.format.samplesToBytes(len2));
            AudioUtil.fromInt(data, len2, this.out.format, buf);
            buf.flip();
            this.out.write(buf);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.out.close();
        }
    }
}
