package org.jcodec.movtool.streaming.tracks;

import android.support.v4.internal.view.SupportMenu;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.IntArrayList;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.logging.Logger;
import org.jcodec.containers.mp4.boxes.EndianBox;
import org.jcodec.containers.mp4.boxes.channel.Label;
import org.jcodec.movtool.streaming.AudioCodecMeta;

/* loaded from: classes.dex */
public class DownmixHelper {
    private static float rev = 4.656613E-10f;
    private int[][] channels;
    private int[][] counts;
    private ThreadLocal<float[][]> fltBuf = new ThreadLocal<>();
    private float[][] matrix;
    private int nSamples;

    /* renamed from: se */
    private AudioCodecMeta[] f1563se;

    public DownmixHelper(AudioCodecMeta[] se, int nSamples, boolean[][] solo) {
        this.nSamples = nSamples;
        this.f1563se = se;
        List<float[]> matrixBuilder = new ArrayList<>();
        List<int[]> countsBuilder = new ArrayList<>();
        List<int[]> channelsBuilder = new ArrayList<>();
        for (int tr = 0; tr < se.length; tr++) {
            Label[] channels = se[tr].getChannelLabels();
            IntArrayList tmp = new IntArrayList();
            for (int ch = 0; ch < channels.length; ch++) {
                if (solo == null || solo[tr][ch]) {
                    tmp.add(ch);
                    switch (channels[ch]) {
                        case Left:
                        case LeftTotal:
                        case LeftCenter:
                            matrixBuilder.add(new float[]{1.0f, 0.0f});
                            countsBuilder.add(new int[]{1, 0});
                            break;
                        case LeftSurround:
                        case RearSurroundLeft:
                            matrixBuilder.add(new float[]{0.7f, 0.0f});
                            countsBuilder.add(new int[]{1, 0});
                            break;
                        case Right:
                        case RightTotal:
                        case RightCenter:
                            matrixBuilder.add(new float[]{0.0f, 1.0f});
                            countsBuilder.add(new int[]{0, 1});
                            break;
                        case RightSurround:
                        case RearSurroundRight:
                            matrixBuilder.add(new float[]{0.0f, 0.7f});
                            countsBuilder.add(new int[]{0, 1});
                            break;
                        case Mono:
                        case LFEScreen:
                        case Center:
                        case LFE2:
                        case Discrete:
                            matrixBuilder.add(new float[]{0.7f, 0.7f});
                            countsBuilder.add(new int[]{1, 1});
                            break;
                        case Unused:
                            break;
                        default:
                            if ((channels[ch].getVal() >>> 16) == 1) {
                                matrixBuilder.add(new float[]{0.7f, 0.7f});
                                countsBuilder.add(new int[]{1, 1});
                                Logger.info("Discrete" + (channels[ch].getVal() & SupportMenu.USER_MASK));
                                break;
                            } else {
                                break;
                            }
                    }
                }
            }
            channelsBuilder.add(tmp.toArray());
        }
        this.matrix = (float[][]) matrixBuilder.toArray(new float[0]);
        this.counts = (int[][]) countsBuilder.toArray(new int[0]);
        this.channels = (int[][]) channelsBuilder.toArray(new int[0]);
    }

    public void downmix(ByteBuffer[] data, ByteBuffer out) {
        out.order(ByteOrder.LITTLE_ENDIAN);
        if (this.matrix.length == 0) {
            out.limit(this.nSamples << 2);
            return;
        }
        float[][] flt = this.fltBuf.get();
        if (flt == null) {
            flt = (float[][]) Array.newInstance(Float.TYPE, this.matrix.length, this.nSamples);
            this.fltBuf.set(flt);
        }
        int i = 0;
        for (int tr = 0; tr < this.f1563se.length; tr++) {
            int ch = 0;
            while (ch < this.channels[tr].length) {
                toFloat(flt[i], this.f1563se[tr], data[tr], this.channels[tr][ch], this.f1563se[tr].getChannelCount());
                ch++;
                i++;
            }
        }
        for (int s = 0; s < this.nSamples; s++) {
            int lcount = 0;
            int rcount = 0;
            float lSum = 0.0f;
            float lMul = 1.0f;
            float rSum = 0.0f;
            float rMul = 1.0f;
            for (int inp = 0; inp < this.matrix.length; inp++) {
                float sample = flt[inp][s];
                float l = this.matrix[inp][0] * sample;
                float r = this.matrix[inp][1] * sample;
                lSum += l;
                lMul *= l;
                rSum += r;
                rMul *= r;
                lcount += this.counts[inp][0];
                rcount += this.counts[inp][1];
            }
            float outLeft = lcount > 1 ? clamp1f(lSum - lMul) : lSum;
            float outRight = rcount > 1 ? clamp1f(rSum - rMul) : rSum;
            short left = (short) (32767.0f * outLeft);
            short right = (short) (32767.0f * outRight);
            out.putShort(left);
            out.putShort(right);
        }
        out.flip();
    }

    private void toFloat(float[] fSamples, AudioCodecMeta se, ByteBuffer bb, int ch, int nCh) {
        byte[] ba;
        int off;
        int len;
        int maxSamples;
        if (bb.hasArray()) {
            ba = bb.array();
            off = bb.arrayOffset() + bb.position();
            len = bb.remaining();
        } else {
            ba = NIOUtils.toArray(bb);
            off = 0;
            len = ba.length;
        }
        if (se.getSampleSize() == 3) {
            int step = nCh * 3;
            maxSamples = Math.min(this.nSamples, len / step);
            if (se.getEndian() == EndianBox.Endian.BIG_ENDIAN) {
                int s = 0;
                int bi = off + (ch * 3);
                while (s < maxSamples) {
                    fSamples[s] = nextSample24BE(ba, bi);
                    s++;
                    bi += step;
                }
            } else {
                int s2 = 0;
                int bi2 = off + (ch * 3);
                while (s2 < maxSamples) {
                    fSamples[s2] = nextSample24LE(ba, bi2);
                    s2++;
                    bi2 += step;
                }
            }
        } else {
            int step2 = nCh * 2;
            maxSamples = Math.min(this.nSamples, len / step2);
            if (se.getEndian() == EndianBox.Endian.BIG_ENDIAN) {
                int s3 = 0;
                int bi3 = off + (ch * 2);
                while (s3 < maxSamples) {
                    fSamples[s3] = nextSample16BE(ba, bi3);
                    s3++;
                    bi3 += step2;
                }
            } else {
                int s4 = 0;
                int bi4 = off + (ch * 2);
                while (s4 < maxSamples) {
                    fSamples[s4] = nextSample16LE(ba, bi4);
                    s4++;
                    bi4 += step2;
                }
            }
        }
        for (int s5 = maxSamples; s5 < this.nSamples; s5++) {
            fSamples[s5] = 0.0f;
        }
    }

    public static final float clamp1f(float f) {
        if (f > 1.0f) {
            return 1.0f;
        }
        if (f < -1.0f) {
            return -1.0f;
        }
        return f;
    }

    private static final float nextSample24BE(byte[] ba, int bi) {
        return rev * (((ba[bi] & 255) << 24) | ((ba[bi + 1] & 255) << 16) | ((ba[bi + 2] & 255) << 8));
    }

    private static final float nextSample24LE(byte[] ba, int bi) {
        return rev * (((ba[bi] & 255) << 8) | ((ba[bi + 1] & 255) << 16) | ((ba[bi + 2] & 255) << 24));
    }

    private static final float nextSample16BE(byte[] ba, int bi) {
        return rev * (((ba[bi] & 255) << 24) | ((ba[bi + 1] & 255) << 16));
    }

    private static final float nextSample16LE(byte[] ba, int bi) {
        return rev * (((ba[bi] & 255) << 16) | ((ba[bi + 1] & 255) << 24));
    }
}
