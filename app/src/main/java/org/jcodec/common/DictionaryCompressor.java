package org.jcodec.common;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.common.RunLength;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.io.VLC;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class DictionaryCompressor {
    protected VLC buildCodes(int[] counts, int esc) {
        int[] codes = new int[counts.length];
        int[] codeSizes = new int[counts.length];
        for (int code = 0; code < Math.min(codes.length, esc); code++) {
            int max = 0;
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] > counts[max]) {
                    max = i;
                }
            }
            codes[max] = code;
            codeSizes[max] = Math.max(1, MathUtil.log2(code));
            counts[max] = Integer.MIN_VALUE;
        }
        int escSize = MathUtil.log2(esc);
        for (int i2 = 0; i2 < counts.length; i2++) {
            if (counts[i2] >= 0) {
                codes[i2] = esc;
                codeSizes[i2] = escSize;
            }
        }
        return new VLC(codes, codeSizes);
    }

    /* loaded from: classes.dex */
    public static class Long extends DictionaryCompressor {
        public void compress(long[] values, ByteBuffer bb) {
            RunLength.Long rl = getValueStats(values);
            int[] counts = rl.getCounts();
            long[] keys = rl.getValues();
            VLC vlc = buildCodes(counts, values.length / 10);
            int[] codes = vlc.getCodes();
            int[] codeSizes = vlc.getCodeSizes();
            bb.putInt(codes.length);
            for (int i = 0; i < codes.length; i++) {
                bb.put((byte) codeSizes[i]);
                bb.putShort((short) (codes[i] >>> 16));
                bb.putLong(keys[i]);
            }
            BitWriter br = new BitWriter(bb);
            for (long l : values) {
                for (int i2 = 0; i2 < keys.length; i2++) {
                    if (keys[i2] == l) {
                        vlc.writeVLC(br, i2);
                        if (codes[i2] == 15) {
                            br.writeNBit(16, i2);
                        }
                    }
                }
            }
            br.flush();
        }

        private RunLength.Long getValueStats(long[] values) {
            long[] copy = Arrays.copyOf(values, values.length);
            Arrays.sort(copy);
            RunLength.Long rl = new RunLength.Long();
            for (long l : copy) {
                rl.add(l);
            }
            return rl;
        }
    }

    /* loaded from: classes.dex */
    public static class Int extends DictionaryCompressor {
        public void compress(int[] values, ByteBuffer bb) {
            RunLength.Integer rl = getValueStats(values);
            int[] counts = rl.getCounts();
            int[] keys = rl.getValues();
            int esc = Math.max(1, (1 << (MathUtil.log2(counts.length) - 2)) - 1);
            VLC vlc = buildCodes(counts, esc);
            int[] codes = vlc.getCodes();
            int[] codeSizes = vlc.getCodeSizes();
            bb.putInt(codes.length);
            for (int i = 0; i < codes.length; i++) {
                bb.put((byte) codeSizes[i]);
                bb.putShort((short) (codes[i] >>> 16));
                bb.putInt(keys[i]);
            }
            BitWriter br = new BitWriter(bb);
            for (int l : values) {
                for (int i2 = 0; i2 < keys.length; i2++) {
                    if (keys[i2] == l) {
                        vlc.writeVLC(br, i2);
                        if (codes[i2] == esc) {
                            br.writeNBit(i2, 16);
                        }
                    }
                }
            }
            br.flush();
        }

        private RunLength.Integer getValueStats(int[] values) {
            int[] copy = Arrays.copyOf(values, values.length);
            Arrays.sort(copy);
            RunLength.Integer rl = new RunLength.Integer();
            for (int l : copy) {
                rl.add(l);
            }
            return rl;
        }
    }
}
