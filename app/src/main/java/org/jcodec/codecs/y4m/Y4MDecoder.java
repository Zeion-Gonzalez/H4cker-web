package org.jcodec.codecs.y4m;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.StringUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;

/* loaded from: classes.dex */
public class Y4MDecoder {
    private int bufSize;
    private Rational fps;
    private int height;
    private String invalidFormat;

    /* renamed from: is */
    private FileChannel f1495is;
    private int width;

    public Y4MDecoder(SeekableByteChannel is) throws IOException {
        ByteBuffer buf = NIOUtils.fetchFrom(is, 2048);
        String[] header = StringUtils.split(readLine(buf), ' ');
        if (!"YUV4MPEG2".equals(header[0])) {
            this.invalidFormat = "Not yuv4mpeg stream";
            return;
        }
        String chroma = find(header, 'C');
        if (chroma != null && !chroma.startsWith("420")) {
            this.invalidFormat = "Only yuv420p is supported";
            return;
        }
        this.width = Integer.parseInt(find(header, 'W'));
        this.height = Integer.parseInt(find(header, 'H'));
        String fpsStr = find(header, 'F');
        if (fpsStr != null) {
            String[] numden = StringUtils.split(fpsStr, ':');
            this.fps = new Rational(Integer.parseInt(numden[0]), Integer.parseInt(numden[1]));
        }
        is.position(buf.position());
        this.bufSize = this.width * this.height * 2;
    }

    public Picture nextFrame(int[][] buffer) throws IOException {
        if (this.invalidFormat != null) {
            throw new RuntimeException("Invalid input: " + this.invalidFormat);
        }
        long pos = this.f1495is.position();
        ByteBuffer buf = NIOUtils.fetchFrom(this.f1495is, 2048);
        String frame = readLine(buf);
        if (frame == null || !frame.startsWith("FRAME")) {
            return null;
        }
        MappedByteBuffer pix = this.f1495is.map(FileChannel.MapMode.READ_ONLY, buf.position() + pos, this.bufSize);
        this.f1495is.position(buf.position() + pos + this.bufSize);
        Picture create = Picture.create(this.width, this.height, ColorSpace.YUV420);
        copy(pix, create.getPlaneData(0));
        copy(pix, create.getPlaneData(1));
        copy(pix, create.getPlaneData(2));
        return create;
    }

    void copy(ByteBuffer b, int[] ii) {
        int i = 0;
        while (b.hasRemaining()) {
            ii[i] = b.get() & 255;
            i++;
        }
    }

    private static String find(String[] header, char c) {
        for (String string : header) {
            if (string.charAt(0) == c) {
                return string.substring(1);
            }
        }
        return null;
    }

    private static String readLine(ByteBuffer y4m) {
        ByteBuffer duplicate = y4m.duplicate();
        while (y4m.hasRemaining() && y4m.get() != 10) {
        }
        if (y4m.hasRemaining()) {
            duplicate.limit(y4m.position() - 1);
        }
        return new String(NIOUtils.toArray(duplicate));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Rational getFps() {
        return this.fps;
    }

    public Size getSize() {
        return new Size(this.width, this.height);
    }
}
