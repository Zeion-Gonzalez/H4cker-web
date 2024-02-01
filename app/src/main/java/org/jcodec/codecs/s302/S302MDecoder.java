package org.jcodec.codecs.s302;

import android.support.v4.internal.view.SupportMenu;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.common.AudioDecoder;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.model.AudioBuffer;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class S302MDecoder implements AudioDecoder {
    public static int SAMPLE_RATE = 48000;

    @Override // org.jcodec.common.AudioDecoder
    public AudioBuffer decodeFrame(ByteBuffer frame, ByteBuffer dst) {
        frame.order(ByteOrder.BIG_ENDIAN);
        ByteBuffer dup = dst.duplicate();
        int h = frame.getInt();
        int frameSize = (h >> 16) & SupportMenu.USER_MASK;
        if (frame.remaining() != frameSize) {
            throw new IllegalArgumentException("Wrong s302m frame");
        }
        int channels = (((h >> 14) & 3) * 2) + 2;
        int sampleSizeInBits = (((h >> 4) & 3) * 4) + 16;
        if (sampleSizeInBits == 24) {
            int nSamples = (frame.remaining() / 7) * 2;
            while (frame.remaining() > 6) {
                byte c = (byte) MathUtil.reverse(frame.get() & 255);
                byte b = (byte) MathUtil.reverse(frame.get() & 255);
                byte a = (byte) MathUtil.reverse(frame.get() & 255);
                int g = MathUtil.reverse(frame.get() & 15);
                int f = MathUtil.reverse(frame.get() & 255);
                int e = MathUtil.reverse(frame.get() & 255);
                int d = MathUtil.reverse(frame.get() & 240);
                dup.put(a);
                dup.put(b);
                dup.put(c);
                dup.put((byte) ((d << 4) | (e >> 4)));
                dup.put((byte) ((e << 4) | (f >> 4)));
                dup.put((byte) ((f << 4) | (g >> 4)));
            }
            dup.flip();
            return new AudioBuffer(dup, new AudioFormat(SAMPLE_RATE, 24, channels, true, true), nSamples / channels);
        }
        if (sampleSizeInBits == 20) {
            int nSamples2 = (frame.remaining() / 6) * 2;
            while (frame.remaining() > 5) {
                int c2 = MathUtil.reverse(frame.get() & 255);
                int b2 = MathUtil.reverse(frame.get() & 255);
                int a2 = MathUtil.reverse(frame.get() & 240);
                dup.put((byte) ((a2 << 4) | (b2 >> 4)));
                dup.put((byte) ((b2 << 4) | (c2 >> 4)));
                dup.put((byte) (c2 << 4));
                int cc = MathUtil.reverse(frame.get() & 255);
                int bb = MathUtil.reverse(frame.get() & 255);
                int aa = MathUtil.reverse(frame.get() & 240);
                dup.put((byte) ((aa << 4) | (bb >> 4)));
                dup.put((byte) ((bb << 4) | (cc >> 4)));
                dup.put((byte) (cc << 4));
            }
            dup.flip();
            return new AudioBuffer(dup, new AudioFormat(SAMPLE_RATE, 24, channels, true, true), nSamples2 / channels);
        }
        int nSamples3 = (frame.remaining() / 5) * 2;
        while (frame.remaining() > 4) {
            byte bb2 = (byte) MathUtil.reverse(frame.get() & 255);
            byte aa2 = (byte) MathUtil.reverse(frame.get() & 255);
            int c3 = MathUtil.reverse(frame.get() & 255);
            int b3 = MathUtil.reverse(frame.get() & 255);
            int a3 = MathUtil.reverse(frame.get() & 240);
            dst.put(aa2);
            dst.put(bb2);
            dst.put((byte) ((a3 << 4) | (b3 >> 4)));
            dst.put((byte) ((b3 << 4) | (c3 >> 4)));
        }
        dup.flip();
        return new AudioBuffer(dup, new AudioFormat(SAMPLE_RATE, 16, channels, true, true), nSamples3 / channels);
    }
}
