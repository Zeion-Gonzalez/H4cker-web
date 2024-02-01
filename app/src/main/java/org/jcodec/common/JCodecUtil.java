package org.jcodec.common;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.ppm.PPMEncoder;
import org.jcodec.codecs.prores.ProresDecoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.containers.mps.MPSDemuxer;
import org.jcodec.containers.mps.MTSDemuxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public class JCodecUtil {
    private static final VideoDecoder[] knownDecoders = {new ProresDecoder(), new MPEGDecoder(), new H264Decoder()};

    /* loaded from: classes.dex */
    public enum Format {
        MOV,
        MPEG_PS,
        MPEG_TS
    }

    public static Format detectFormat(File f) throws IOException {
        return detectFormat(NIOUtils.fetchFrom(f, 204800));
    }

    public static Format detectFormat(ReadableByteChannel f) throws IOException {
        return detectFormat(NIOUtils.fetchFrom(f, 204800));
    }

    public static Format detectFormat(ByteBuffer b) {
        int movScore = MP4Demuxer.probe(b.duplicate());
        int psScore = MPSDemuxer.probe(b.duplicate());
        int tsScore = MTSDemuxer.probe(b.duplicate());
        if (movScore == 0 && psScore == 0 && tsScore == 0) {
            return null;
        }
        return movScore > psScore ? movScore > tsScore ? Format.MOV : Format.MPEG_TS : psScore > tsScore ? Format.MPEG_PS : Format.MPEG_TS;
    }

    public static VideoDecoder detectDecoder(ByteBuffer b) {
        int maxProbe = 0;
        VideoDecoder selected = null;
        VideoDecoder[] arr$ = knownDecoders;
        for (VideoDecoder vd : arr$) {
            int probe = vd.probe(b);
            if (probe > maxProbe) {
                selected = vd;
                maxProbe = probe;
            }
        }
        return selected;
    }

    public static VideoDecoder getVideoDecoder(String fourcc) {
        if ("apch".equals(fourcc) || "apcs".equals(fourcc) || "apco".equals(fourcc) || "apcn".equals(fourcc) || "ap4h".equals(fourcc)) {
            return new ProresDecoder();
        }
        if ("m2v1".equals(fourcc)) {
            return new MPEGDecoder();
        }
        return null;
    }

    public static void savePictureAsPPM(Picture pic, File file) throws IOException {
        Transform transform = ColorUtil.getTransform(pic.getColor(), ColorSpace.RGB);
        Picture rgb = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.RGB);
        transform.transform(pic, rgb);
        NIOUtils.writeTo(new PPMEncoder().encodeFrame(rgb), file);
    }

    public static byte[] asciiString(String fourcc) {
        char[] ch = fourcc.toCharArray();
        byte[] result = new byte[ch.length];
        for (int i = 0; i < ch.length; i++) {
            result[i] = (byte) ch[i];
        }
        return result;
    }

    public static void writeBER32(ByteBuffer buffer, int value) {
        buffer.put((byte) ((value >> 21) | 128));
        buffer.put((byte) ((value >> 14) | 128));
        buffer.put((byte) ((value >> 7) | 128));
        buffer.put((byte) (value & 127));
    }

    public static void writeBER32Var(ByteBuffer bb, int value) {
        int bits = MathUtil.log2(value);
        for (int i = 0; i < 4 && bits > 0; i++) {
            bits -= 7;
            int out = value >> bits;
            if (bits > 0) {
                out |= 128;
            }
            bb.put((byte) out);
        }
    }

    public static int readBER32(ByteBuffer input) {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            byte b = input.get();
            size = (size << 7) | (b & Byte.MAX_VALUE);
            if (((b & 255) >> 7) == 0) {
                break;
            }
        }
        return size;
    }

    public static int[] getAsIntArray(ByteBuffer yuv, int size) {
        byte[] b = new byte[size];
        int[] result = new int[size];
        yuv.get(b);
        for (int i = 0; i < b.length; i++) {
            result[i] = b[i] & 255;
        }
        return result;
    }

    public static ThreadPoolExecutor getPriorityExecutor(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue(10, PriorityFuture.COMP)) { // from class: org.jcodec.common.JCodecUtil.1
            @Override // java.util.concurrent.AbstractExecutorService
            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
                return new PriorityFuture(newTaskFor, ((PriorityCallable) callable).getPriority());
            }
        };
    }

    public static String removeExtension(String name) {
        if (name == null) {
            return null;
        }
        return name.replaceAll("\\.[^\\.]+$", "");
    }
}
