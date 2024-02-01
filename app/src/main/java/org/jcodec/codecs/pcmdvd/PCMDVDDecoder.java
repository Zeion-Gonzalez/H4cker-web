package org.jcodec.codecs.pcmdvd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.AudioDecoder;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.AudioBuffer;

/* loaded from: classes.dex */
public class PCMDVDDecoder implements AudioDecoder {
    private static final int[] lpcm_freq_tab = {48000, 96000, 44100, 32000};

    @Override // org.jcodec.common.AudioDecoder
    public AudioBuffer decodeFrame(ByteBuffer _frame, ByteBuffer _dst) throws IOException {
        ByteBuffer dst = _dst.duplicate();
        ByteBuffer frame = _frame.duplicate();
        frame.order(ByteOrder.BIG_ENDIAN);
        dst.order(ByteOrder.LITTLE_ENDIAN);
        int dvdaudioSubstreamType = frame.get() & 255;
        NIOUtils.skip(frame, 3);
        if ((dvdaudioSubstreamType & JpegConst.APP0) != 160) {
            if ((dvdaudioSubstreamType & JpegConst.APP0) == 128) {
                if ((dvdaudioSubstreamType & 248) == 136) {
                    throw new RuntimeException("CODEC_ID_DTS");
                }
                throw new RuntimeException("CODEC_ID_AC3");
            }
            throw new RuntimeException("MPEG DVD unknown coded");
        }
        int i = frame.get() & 255;
        int b1 = frame.get() & 255;
        int i2 = frame.get() & 255;
        int freq = (b1 >> 4) & 3;
        int sampleRate = lpcm_freq_tab[freq];
        int channelCount = (b1 & 7) + 1;
        int sampleSizeInBits = (((b1 >> 6) & 3) * 4) + 16;
        int nFrames = frame.remaining() / ((sampleSizeInBits >> 3) * channelCount);
        switch (sampleSizeInBits) {
            case 20:
                for (int n = 0; n < (nFrames >> 1); n++) {
                    for (int c = 0; c < channelCount; c++) {
                        short s0 = frame.getShort();
                        dst.putShort(s0);
                        short s1 = frame.getShort();
                        dst.putShort(s1);
                    }
                    NIOUtils.skip(frame, channelCount);
                }
                break;
            case 24:
                for (int n2 = 0; n2 < (nFrames >> 1); n2++) {
                    for (int c2 = 0; c2 < channelCount; c2++) {
                        short s02 = frame.getShort();
                        dst.putShort(s02);
                        short s12 = frame.getShort();
                        dst.putShort(s12);
                    }
                    NIOUtils.skip(frame, channelCount << 1);
                }
                break;
        }
        dst.flip();
        return new AudioBuffer(dst, new AudioFormat(sampleRate, 16, channelCount, true, false), nFrames);
    }
}
