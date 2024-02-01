package org.jcodec.common;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class AudioUtil {
    private static final float f16 = 32767.0f;
    private static final float f24 = 8388607.0f;
    public static final float r16 = 3.0517578E-5f;
    public static final float r24 = 1.1920929E-7f;

    public static void toFloat(AudioFormat format, ByteBuffer buf, FloatBuffer floatBuf) {
        if (!format.isSigned()) {
            throw new IllegalArgumentException("Unsigned PCM is not supported ( yet? ).");
        }
        if (format.getSampleSizeInBits() != 16 && format.getSampleSizeInBits() != 24) {
            throw new IllegalArgumentException(format.getSampleSizeInBits() + " bit PCM is not supported ( yet? ).");
        }
        if (format.isBigEndian()) {
            if (format.getSampleSizeInBits() == 16) {
                toFloat16BE(buf, floatBuf);
                return;
            } else {
                toFloat24BE(buf, floatBuf);
                return;
            }
        }
        if (format.getSampleSizeInBits() == 16) {
            toFloat16LE(buf, floatBuf);
        } else {
            toFloat24LE(buf, floatBuf);
        }
    }

    public static void fromFloat(FloatBuffer floatBuf, AudioFormat format, ByteBuffer buf) {
        if (!format.isSigned()) {
            throw new IllegalArgumentException("Unsigned PCM is not supported ( yet? ).");
        }
        if (format.getSampleSizeInBits() != 16 && format.getSampleSizeInBits() != 24) {
            throw new IllegalArgumentException(format.getSampleSizeInBits() + " bit PCM is not supported ( yet? ).");
        }
        if (format.isBigEndian()) {
            if (format.getSampleSizeInBits() == 16) {
                fromFloat16BE(buf, floatBuf);
                return;
            } else {
                fromFloat24BE(buf, floatBuf);
                return;
            }
        }
        if (format.getSampleSizeInBits() == 16) {
            fromFloat16LE(buf, floatBuf);
        } else {
            fromFloat24LE(buf, floatBuf);
        }
    }

    private static void toFloat24LE(ByteBuffer buf, FloatBuffer out) {
        while (buf.remaining() >= 3 && out.hasRemaining()) {
            out.put(1.1920929E-7f * (((((buf.get() & 255) << 8) | ((buf.get() & 255) << 16)) | ((buf.get() & 255) << 24)) >> 8));
        }
    }

    private static void toFloat16LE(ByteBuffer buf, FloatBuffer out) {
        while (buf.remaining() >= 2 && out.hasRemaining()) {
            out.put(3.0517578E-5f * ((short) ((buf.get() & 255) | ((buf.get() & 255) << 8))));
        }
    }

    private static void toFloat24BE(ByteBuffer buf, FloatBuffer out) {
        while (buf.remaining() >= 3 && out.hasRemaining()) {
            out.put(1.1920929E-7f * (((((buf.get() & 255) << 24) | ((buf.get() & 255) << 16)) | ((buf.get() & 255) << 8)) >> 8));
        }
    }

    private static void toFloat16BE(ByteBuffer buf, FloatBuffer out) {
        while (buf.remaining() >= 2 && out.hasRemaining()) {
            out.put(3.0517578E-5f * ((short) (((buf.get() & 255) << 8) | (buf.get() & 255))));
        }
    }

    private static void fromFloat24LE(ByteBuffer buf, FloatBuffer in) {
        while (buf.remaining() >= 3 && in.hasRemaining()) {
            int val = MathUtil.clip((int) (in.get() * f24), -8388608, 8388607) & ViewCompat.MEASURED_SIZE_MASK;
            buf.put((byte) val);
            buf.put((byte) (val >> 8));
            buf.put((byte) (val >> 16));
        }
    }

    private static void fromFloat16LE(ByteBuffer buf, FloatBuffer in) {
        while (buf.remaining() >= 2 && in.hasRemaining()) {
            int val = MathUtil.clip((int) (in.get() * f16), -32768, 32767) & SupportMenu.USER_MASK;
            buf.put((byte) val);
            buf.put((byte) (val >> 8));
        }
    }

    private static void fromFloat24BE(ByteBuffer buf, FloatBuffer in) {
        while (buf.remaining() >= 3 && in.hasRemaining()) {
            int val = MathUtil.clip((int) (in.get() * f24), -8388608, 8388607) & ViewCompat.MEASURED_SIZE_MASK;
            buf.put((byte) (val >> 16));
            buf.put((byte) (val >> 8));
            buf.put((byte) val);
        }
    }

    private static void fromFloat16BE(ByteBuffer buf, FloatBuffer in) {
        while (buf.remaining() >= 2 && in.hasRemaining()) {
            int val = MathUtil.clip((int) (in.get() * f16), -32768, 32767) & SupportMenu.USER_MASK;
            buf.put((byte) (val >> 8));
            buf.put((byte) val);
        }
    }

    public static int fromInt(int[] data, int len, AudioFormat format, ByteBuffer buf) {
        if (!format.isSigned()) {
            throw new IllegalArgumentException("Unsigned PCM is not supported ( yet? ).");
        }
        if (format.getSampleSizeInBits() != 16 && format.getSampleSizeInBits() != 24) {
            throw new IllegalArgumentException(format.getSampleSizeInBits() + " bit PCM is not supported ( yet? ).");
        }
        if (format.isBigEndian()) {
            if (format.getSampleSizeInBits() == 16) {
                return fromInt16BE(buf, data, len);
            }
            return fromInt24BE(buf, data, len);
        }
        if (format.getSampleSizeInBits() == 16) {
            return fromInt16LE(buf, data, len);
        }
        return fromInt24LE(buf, data, len);
    }

    private static int fromInt24LE(ByteBuffer buf, int[] out, int len) {
        int samples = 0;
        while (buf.remaining() >= 3 && samples < len) {
            int val = out[samples];
            buf.put((byte) val);
            buf.put((byte) (val >> 8));
            buf.put((byte) (val >> 16));
            samples++;
        }
        return samples;
    }

    private static int fromInt16LE(ByteBuffer buf, int[] out, int len) {
        int samples = 0;
        while (buf.remaining() >= 2 && samples < len) {
            int val = out[samples];
            buf.put((byte) val);
            buf.put((byte) (val >> 8));
            samples++;
        }
        return samples;
    }

    private static int fromInt24BE(ByteBuffer buf, int[] out, int len) {
        int samples = 0;
        while (buf.remaining() >= 3 && samples < len) {
            int val = out[samples];
            buf.put((byte) (val >> 16));
            buf.put((byte) (val >> 8));
            buf.put((byte) val);
            samples++;
        }
        return samples;
    }

    private static int fromInt16BE(ByteBuffer buf, int[] out, int len) {
        int samples = 0;
        while (buf.remaining() >= 2 && samples < len) {
            int val = out[samples];
            buf.put((byte) (val >> 8));
            buf.put((byte) val);
            samples++;
        }
        return samples;
    }

    public static int toInt(AudioFormat format, ByteBuffer buf, int[] samples) {
        if (!format.isSigned()) {
            throw new IllegalArgumentException("Unsigned PCM is not supported ( yet? ).");
        }
        if (format.getSampleSizeInBits() != 16 && format.getSampleSizeInBits() != 24) {
            throw new IllegalArgumentException(format.getSampleSizeInBits() + " bit PCM is not supported ( yet? ).");
        }
        if (format.isBigEndian()) {
            if (format.getSampleSizeInBits() == 16) {
                return toInt16BE(buf, samples);
            }
            return toInt24BE(buf, samples);
        }
        if (format.getSampleSizeInBits() == 16) {
            return toInt16LE(buf, samples);
        }
        return toInt24LE(buf, samples);
    }

    private static int toInt24LE(ByteBuffer buf, int[] out) {
        int samples = 0;
        while (buf.remaining() >= 3 && samples < out.length) {
            out[samples] = ((((buf.get() & 255) << 8) | ((buf.get() & 255) << 16)) | ((buf.get() & 255) << 24)) >> 8;
            samples++;
        }
        return samples;
    }

    private static int toInt16LE(ByteBuffer buf, int[] out) {
        int samples = 0;
        while (buf.remaining() >= 2 && samples < out.length) {
            out[samples] = (short) ((buf.get() & 255) | ((buf.get() & 255) << 8));
            samples++;
        }
        return samples;
    }

    private static int toInt24BE(ByteBuffer buf, int[] out) {
        int samples = 0;
        while (buf.remaining() >= 3 && samples < out.length) {
            out[samples] = ((((buf.get() & 255) << 24) | ((buf.get() & 255) << 16)) | ((buf.get() & 255) << 8)) >> 8;
            samples++;
        }
        return samples;
    }

    private static int toInt16BE(ByteBuffer buf, int[] out) {
        int samples = 0;
        while (buf.remaining() >= 2 && samples < out.length) {
            out[samples] = (short) (((buf.get() & 255) << 8) | (buf.get() & 255));
            samples++;
        }
        return samples;
    }

    public static void interleave(AudioFormat format, ByteBuffer[] ins, ByteBuffer outb) {
        int bytesPerSample = format.getSampleSizeInBits() >> 3;
        int bytesPerFrame = bytesPerSample * ins.length;
        int max = 0;
        for (int i = 0; i < ins.length; i++) {
            if (ins[i].remaining() > max) {
                max = ins[i].remaining();
            }
        }
        for (int frames = 0; frames < max && outb.remaining() >= bytesPerFrame; frames++) {
            for (int j = 0; j < ins.length; j++) {
                if (ins[j].remaining() < bytesPerSample) {
                    for (int i2 = 0; i2 < bytesPerSample; i2++) {
                        outb.put((byte) 0);
                    }
                } else {
                    for (int i3 = 0; i3 < bytesPerSample; i3++) {
                        outb.put(ins[j].get());
                    }
                }
            }
        }
    }

    public static void deinterleave(AudioFormat format, ByteBuffer inb, ByteBuffer[] outs) {
        int bytesPerSample = format.getSampleSizeInBits() >> 3;
        int bytesPerFrame = bytesPerSample * outs.length;
        while (inb.remaining() >= bytesPerFrame) {
            for (ByteBuffer byteBuffer : outs) {
                for (int i = 0; i < bytesPerSample; i++) {
                    byteBuffer.put(inb.get());
                }
            }
        }
    }
}
