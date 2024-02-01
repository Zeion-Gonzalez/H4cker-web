package org.jcodec.codecs.aac.blocks;

import android.support.v4.view.InputDeviceCompat;
import java.lang.reflect.Array;
import org.jcodec.codecs.aac.Profile;
import org.jcodec.codecs.prores.ProresDecoder;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;
import org.jcodec.common.io.VLCBuilder;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class BlockICS extends Block {
    private static final int MAX_LTP_LONG_SFB = 40;
    private static final int POW_SF2_ZERO = 200;
    private boolean commonWindow;
    private int globalGain;
    int maxSfb;
    private int numSwb;
    private int numWindows;
    int num_window_groups;
    private Profile profile;
    private int samplingIndex;
    private boolean scaleFlag;
    private double[] sfs;
    private int[] swbOffset;
    private int windowSequence;
    private static VLC vlc = new VLC(AACTab.ff_aac_scalefactor_code, AACTab.ff_aac_scalefactor_bits);
    private static VLC[] spectral = {new VLCBuilder(AACTab.codes1, AACTab.bits1, AACTab.codebook_vector02_idx).getVLC(), new VLCBuilder(AACTab.codes2, AACTab.bits2, AACTab.codebook_vector02_idx).getVLC(), new VLCBuilder(AACTab.codes3, AACTab.bits3, AACTab.codebook_vector02_idx).getVLC(), new VLCBuilder(AACTab.codes4, AACTab.bits4, AACTab.codebook_vector02_idx).getVLC(), new VLCBuilder(AACTab.codes5, AACTab.bits5, AACTab.codebook_vector4_idx).getVLC(), new VLCBuilder(AACTab.codes6, AACTab.bits6, AACTab.codebook_vector4_idx).getVLC(), new VLCBuilder(AACTab.codes7, AACTab.bits7, AACTab.codebook_vector6_idx).getVLC(), new VLCBuilder(AACTab.codes8, AACTab.bits8, AACTab.codebook_vector6_idx).getVLC(), new VLCBuilder(AACTab.codes9, AACTab.bits9, AACTab.codebook_vector8_idx).getVLC(), new VLCBuilder(AACTab.codes10, AACTab.bits10, AACTab.codebook_vector8_idx).getVLC(), new VLCBuilder(AACTab.codes11, AACTab.bits11, AACTab.codebook_vector10_idx).getVLC()};
    static float[] ff_aac_pow2sf_tab = new float[428];
    float[][] ff_aac_codebook_vector_vals = {AACTab.codebook_vector0_vals, AACTab.codebook_vector0_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector4_vals, AACTab.codebook_vector4_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector10_vals, AACTab.codebook_vector10_vals};
    private int[] group_len = new int[8];
    private int[] band_type = new int[120];
    private int[] band_type_run_end = new int[120];

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum BandType {
        ZERO_BT,
        BT_1,
        BT_2,
        BT_3,
        BT_4,
        FIRST_PAIR_BT,
        BT_6,
        BT_7,
        BT_8,
        BT_9,
        BT_10,
        ESC_BT,
        BT_12,
        NOISE_BT,
        INTENSITY_BT2,
        INTENSITY_BT
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum WindowSequence {
        ONLY_LONG_SEQUENCE,
        LONG_START_SEQUENCE,
        EIGHT_SHORT_SEQUENCE,
        LONG_STOP_SEQUENCE
    }

    static {
        for (int i = 0; i < 428; i++) {
            ff_aac_pow2sf_tab[i] = (float) Math.pow(2.0d, ((double) (i - 200)) / 4.0d);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int parseICSInfo(BitReader in) {
        in.read1Bit();
        this.windowSequence = in.readNBit(2);
        in.read1Bit();
        this.num_window_groups = 1;
        this.group_len[0] = 1;
        if (this.windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE.ordinal()) {
            in.readNBit(4);
            for (int i = 0; i < 7; i++) {
                if (in.read1Bit() != 0) {
                    int[] iArr = this.group_len;
                    int i2 = this.num_window_groups - 1;
                    iArr[i2] = iArr[i2] + 1;
                } else {
                    this.num_window_groups++;
                    this.group_len[this.num_window_groups - 1] = 1;
                }
            }
            this.numSwb = AACTab.ff_aac_num_swb_128[this.samplingIndex];
            this.swbOffset = AACTab.ff_swb_offset_128[this.samplingIndex];
            this.numWindows = 8;
        } else {
            this.maxSfb = in.readNBit(6);
            this.numSwb = AACTab.ff_aac_num_swb_1024[this.samplingIndex];
            this.swbOffset = AACTab.ff_swb_offset_1024[this.samplingIndex];
            this.numWindows = 1;
            int predictor_present = in.read1Bit();
            if (predictor_present != 0) {
                if (this.profile == Profile.MAIN) {
                    decodePrediction(in, this.maxSfb);
                } else {
                    if (this.profile == Profile.LC) {
                        throw new RuntimeException("Prediction is not allowed in AAC-LC.\n");
                    }
                    int ltpPresent = in.read1Bit();
                    if (ltpPresent != 0) {
                        decodeLtp(in, this.maxSfb);
                    }
                }
            }
        }
        return 0;
    }

    private void decodePrediction(BitReader in, int maxSfb) {
        if (in.read1Bit() != 0) {
            in.readNBit(5);
        }
        for (int sfb = 0; sfb < Math.min(maxSfb, AACTab.maxSfbTab[this.samplingIndex]); sfb++) {
            in.read1Bit();
        }
    }

    private void decodeLtp(BitReader in, int maxSfb) {
        in.readNBit(11);
        float f = AACTab.ltpCoefTab[in.readNBit(3)];
        for (int sfb = 0; sfb < Math.min(maxSfb, 40); sfb++) {
            in.read1Bit();
        }
    }

    private void decodeBandTypes(BitReader in) {
        int sect_len_incr;
        int idx = 0;
        int bits = this.windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE.ordinal() ? 3 : 5;
        for (int g = 0; g < this.num_window_groups; g++) {
            int k = 0;
            while (k < this.maxSfb) {
                int sect_end = k;
                int sect_band_type = in.readNBit(4);
                if (sect_band_type == 12) {
                    throw new RuntimeException("invalid band type");
                }
                while (true) {
                    sect_len_incr = in.readNBit(bits);
                    if (sect_len_incr != (1 << bits) - 1) {
                        break;
                    } else {
                        sect_end += sect_len_incr;
                    }
                }
                int sect_end2 = sect_end + sect_len_incr;
                if (!in.moreData() || sect_len_incr == (1 << bits) - 1) {
                    throw new RuntimeException("Overread");
                }
                if (sect_end2 > this.maxSfb) {
                    throw new RuntimeException(String.format("Number of bands (%d) exceeds limit (%d).\n", Integer.valueOf(sect_end2), Integer.valueOf(this.maxSfb)));
                }
                int idx2 = idx;
                while (k < sect_end2) {
                    this.band_type[idx2] = sect_band_type;
                    this.band_type_run_end[idx2] = sect_end2;
                    k++;
                    idx2++;
                }
                idx = idx2;
            }
        }
    }

    private void decodeScalefactors(BitReader in) {
        int[] offset = {this.globalGain, this.globalGain - 90, 0};
        int noise_flag = 1;
        String[] sf_str = {"Global gain", "Noise gain", "Intensity stereo position"};
        int idx = 0;
        for (int g = 0; g < this.num_window_groups; g++) {
            int i = 0;
            while (i < this.maxSfb) {
                int run_end = this.band_type_run_end[idx];
                if (this.band_type[idx] == BandType.ZERO_BT.ordinal()) {
                    while (i < run_end) {
                        this.sfs[idx] = 0.0d;
                        i++;
                        idx++;
                    }
                } else if (this.band_type[idx] == BandType.INTENSITY_BT.ordinal() || this.band_type[idx] == BandType.INTENSITY_BT2.ordinal()) {
                    while (i < run_end) {
                        offset[2] = offset[2] + (vlc.readVLC(in) - 60);
                        int clipped_offset = MathUtil.clip(offset[2], -155, 100);
                        if (offset[2] != clipped_offset) {
                            System.out.println(String.format("Intensity stereo position clipped (%d -> %d).\nIf you heard an audible artifact, there may be a bug in the decoder. ", Integer.valueOf(offset[2]), Integer.valueOf(clipped_offset)));
                        }
                        this.sfs[idx] = ff_aac_pow2sf_tab[(-clipped_offset) + 200];
                        i++;
                        idx++;
                    }
                } else if (this.band_type[idx] == BandType.NOISE_BT.ordinal()) {
                    int noise_flag2 = noise_flag;
                    while (i < run_end) {
                        int noise_flag3 = noise_flag2 - 1;
                        if (noise_flag2 > 0) {
                            offset[1] = offset[1] + in.readNBit(9) + InputDeviceCompat.SOURCE_ANY;
                        } else {
                            offset[1] = offset[1] + (vlc.readVLC(in) - 60);
                        }
                        int clipped_offset2 = MathUtil.clip(offset[1], -100, 155);
                        if (offset[1] != clipped_offset2) {
                            System.out.println(String.format("Noise gain clipped (%d -> %d).\nIf you heard an audible artifact, there may be a bug in the decoder. ", Integer.valueOf(offset[1]), Integer.valueOf(clipped_offset2)));
                        }
                        this.sfs[idx] = -ff_aac_pow2sf_tab[clipped_offset2 + 200];
                        i++;
                        idx++;
                        noise_flag2 = noise_flag3;
                    }
                    noise_flag = noise_flag2;
                } else {
                    while (i < run_end) {
                        offset[0] = offset[0] + (vlc.readVLC(in) - 60);
                        if (offset[0] > 255) {
                            throw new RuntimeException(String.format("%s (%d) out of range.\n", sf_str[0], Integer.valueOf(offset[0])));
                        }
                        this.sfs[idx] = -ff_aac_pow2sf_tab[(offset[0] - 100) + 200];
                        i++;
                        idx++;
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Pulse {
        private int[] amp;
        private int numPulse;
        private int[] pos;

        public Pulse(int numPulse, int[] pos, int[] amp) {
            this.numPulse = numPulse;
            this.pos = pos;
            this.amp = amp;
        }

        public int getNumPulse() {
            return this.numPulse;
        }

        public int[] getPos() {
            return this.pos;
        }

        public int[] getAmp() {
            return this.amp;
        }
    }

    private Pulse decodePulses(BitReader in) {
        int[] pos = new int[4];
        int[] amp = new int[4];
        int numPulse = in.readNBit(2) + 1;
        int pulseSwb = in.readNBit(6);
        if (pulseSwb >= this.numSwb) {
            throw new RuntimeException("pulseSwb >= numSwb");
        }
        pos[0] = this.swbOffset[pulseSwb];
        pos[0] = pos[0] + in.readNBit(5);
        if (pos[0] > 1023) {
            throw new RuntimeException("pos[0] > 1023");
        }
        amp[0] = in.readNBit(4);
        for (int i = 1; i < numPulse; i++) {
            pos[i] = in.readNBit(5) + pos[i - 1];
            if (pos[i] > 1023) {
                throw new RuntimeException("pos[" + i + "] > 1023");
            }
            amp[i] = in.readNBit(5);
        }
        return new Pulse(numPulse, pos, amp);
    }

    /* loaded from: classes.dex */
    public static class Tns {
        private float[][][] coeff;
        private int[][] direction;
        private int[][] length;
        private int[] nFilt;
        private int[][] order;

        public Tns(int[] nFilt, int[][] length, int[][] order, int[][] direction, float[][][] coeff) {
            this.nFilt = nFilt;
            this.length = length;
            this.order = order;
            this.direction = direction;
            this.coeff = coeff;
        }
    }

    private Tns decodeTns(BitReader in) {
        int tns_max_order;
        int is8 = this.windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE.ordinal() ? 1 : 0;
        if (is8 != 0) {
            tns_max_order = 7;
        } else {
            tns_max_order = this.profile == Profile.MAIN ? 20 : 12;
        }
        int[] nFilt = new int[this.numWindows];
        int[][] length = (int[][]) Array.newInstance(Integer.TYPE, this.numWindows, 2);
        int[][] order = (int[][]) Array.newInstance(Integer.TYPE, this.numWindows, 2);
        int[][] direction = (int[][]) Array.newInstance(Integer.TYPE, this.numWindows, 2);
        float[][][] coeff = (float[][][]) Array.newInstance(Float.TYPE, this.numWindows, 2, 1 << (5 - (is8 * 2)));
        for (int w = 0; w < this.numWindows; w++) {
            int readNBit = in.readNBit(2 - is8);
            nFilt[w] = readNBit;
            if (readNBit != 0) {
                int coefRes = in.read1Bit();
                for (int filt = 0; filt < nFilt[w]; filt++) {
                    length[w][filt] = in.readNBit(6 - (is8 * 2));
                    int[] iArr = order[w];
                    int readNBit2 = in.readNBit(5 - (is8 * 2));
                    iArr[filt] = readNBit2;
                    if (readNBit2 > tns_max_order) {
                        throw new RuntimeException(String.format("TNS filter order %d is greater than maximum %d.\n", Integer.valueOf(order[w][filt]), Integer.valueOf(tns_max_order)));
                    }
                    if (order[w][filt] != 0) {
                        direction[w][filt] = in.read1Bit();
                        int coefCompress = in.read1Bit();
                        int coefLen = (coefRes + 3) - coefCompress;
                        int tmp2_idx = (coefCompress * 2) + coefRes;
                        for (int i = 0; i < order[w][filt]; i++) {
                            coeff[w][filt][i] = AACTab.tns_tmp2_map[tmp2_idx][in.readNBit(coefLen)];
                        }
                    }
                }
            }
        }
        return new Tns(nFilt, length, order, direction, coeff);
    }

    void VMUL4(float[] result, int idx, float[] v, int code, float scale) {
        result[idx] = v[code & 3] * scale;
        result[idx + 1] = v[(code >> 2) & 3] * scale;
        result[idx + 2] = v[(code >> 4) & 3] * scale;
        result[idx + 3] = v[(code >> 6) & 3] * scale;
    }

    void VMUL4S(float[] result, int idx, float[] v, int code, int sign, float scale) {
        int nz = code >> 12;
        result[idx + 0] = v[idx & 3] * scale;
        int sign2 = sign << (nz & 1);
        int nz2 = nz >> 1;
        result[idx + 1] = v[(idx >> 2) & 3] * scale;
        int sign3 = sign2 << (nz2 & 1);
        int nz3 = nz2 >> 1;
        result[idx + 2] = v[(idx >> 4) & 3] * scale;
        int i = sign3 << (nz3 & 1);
        int i2 = nz3 >> 1;
        result[idx + 3] = v[(idx >> 6) & 3] * scale;
    }

    void VMUL2(float[] result, int idx, float[] v, int code, float scale) {
        result[idx] = v[code & 15] * scale;
        result[idx + 1] = v[(code >> 4) & 15] * scale;
    }

    void VMUL2S(float[] result, int idx, float[] v, int code, int sign, float scale) {
        result[idx] = v[code & 15] * scale;
        result[idx + 1] = v[(code >> 4) & 15] * scale;
    }

    private void decodeSpectrum(BitReader in) {
        float[] coef = new float[1024];
        int idx = 0;
        for (int g = 0; g < this.num_window_groups; g++) {
            int i = 0;
            while (i < this.maxSfb) {
                int cbt_m1 = this.band_type[idx] - 1;
                if (cbt_m1 < BandType.INTENSITY_BT2.ordinal() - 1 && cbt_m1 != BandType.NOISE_BT.ordinal() - 1) {
                    float[] vq = this.ff_aac_codebook_vector_vals[cbt_m1];
                    VLC vlc2 = spectral[cbt_m1];
                    switch (cbt_m1 >> 1) {
                        case 0:
                            readBandType1And2(in, coef, idx, g, i, vq, vlc2);
                            break;
                        case 1:
                            readBandType3And4(in, coef, idx, g, i, vq, vlc2);
                            break;
                        case 2:
                            readBandType5And6(in, coef, idx, g, i, vq, vlc2);
                            break;
                        case 3:
                        case 4:
                            readBandType7Through10(in, coef, idx, g, i, vq, vlc2);
                            break;
                        default:
                            readOther(in, coef, idx, g, i, vq, vlc2);
                            break;
                    }
                }
                i++;
                idx++;
            }
        }
    }

    private void readBandType3And4(BitReader in, float[] coef, int idx, int g, int sfb, float[] vq, VLC vlc2) {
        int g_len = this.group_len[g];
        int cfo = this.swbOffset[sfb];
        int off_len = this.swbOffset[sfb + 1] - this.swbOffset[sfb];
        int group = 0;
        while (group < g_len) {
            int cf = cfo;
            int len = off_len;
            do {
                int cb_idx = vlc2.readVLC(in);
                int nnz = (cb_idx >> 8) & 15;
                int bits = nnz == 0 ? 0 : in.readNBit(nnz);
                VMUL4S(coef, cf, vq, cb_idx, bits, (float) this.sfs[idx]);
                cf += 4;
                len -= 4;
            } while (len > 0);
            group++;
            cfo += 128;
        }
    }

    private void readBandType7Through10(BitReader in, float[] coef, int idx, int g, int sfb, float[] vq, VLC vlc2) {
        int g_len = this.group_len[g];
        int cfo = this.swbOffset[sfb];
        int off_len = this.swbOffset[sfb + 1] - this.swbOffset[sfb];
        int group = 0;
        while (group < g_len) {
            int cf = cfo;
            int len = off_len;
            do {
                int cb_idx = vlc2.readVLC(in);
                int nnz = (cb_idx >> 8) & 15;
                int bits = nnz == 0 ? 0 : in.readNBit(nnz) << (cb_idx >> 12);
                VMUL2S(coef, cf, vq, cb_idx, bits, (float) this.sfs[idx]);
                cf += 2;
                len -= 2;
            } while (len > 0);
            group++;
            cfo += 128;
        }
    }

    private void readOther(BitReader in, float[] coef, int idx, int g, int sfb, float[] vq, VLC vlc2) {
        int g_len = this.group_len[g];
        int cfo = this.swbOffset[sfb];
        int off_len = this.swbOffset[sfb + 1] - this.swbOffset[sfb];
        int group = 0;
        while (group < g_len) {
            int cf = cfo;
            int len = off_len;
            do {
                int cb_idx = vlc2.readVLC(in);
                if (cb_idx != 0) {
                    int nnz = cb_idx >> 12;
                    int nzt = cb_idx >> 8;
                    int bits = in.readNBit(nnz) << (32 - nnz);
                    int j = 0;
                    while (true) {
                        int cf2 = cf;
                        if (j < 2) {
                            if (((1 << j) & nzt) != 0) {
                                int b = ProresDecoder.nZeros(in.checkNBit(14) ^ (-1));
                                if (b > 8) {
                                    throw new RuntimeException("error in spectral data, ESC overflow\n");
                                }
                                in.skip(b + 1);
                                int b2 = b + 4;
                                int n = (1 << b2) + in.readNBit(b2);
                                cf = cf2 + 1;
                                coef[cf2] = MathUtil.cubeRoot(n) | (Integer.MIN_VALUE & bits);
                                bits <<= 1;
                            } else {
                                int v = (int) vq[cb_idx & 15];
                                cf = cf2 + 1;
                                coef[cf2] = (Integer.MIN_VALUE & bits) | v;
                            }
                            cb_idx >>= 4;
                            j++;
                        } else {
                            cf = cf2 + 2;
                            len += 2;
                            break;
                        }
                    }
                }
            } while (len > 0);
            group++;
            cfo += 128;
        }
    }

    private void readBandType1And2(BitReader in, float[] coef, int idx, int g, int sfb, float[] vq, VLC vlc2) {
        int g_len = this.group_len[g];
        int cfo = this.swbOffset[sfb];
        int off_len = this.swbOffset[sfb + 1] - this.swbOffset[sfb];
        int group = 0;
        while (group < g_len) {
            int cf = cfo;
            int len = off_len;
            do {
                int cb_idx = vlc2.readVLC(in);
                VMUL4(coef, cf, vq, cb_idx, (float) this.sfs[idx]);
                cf += 4;
                len -= 4;
            } while (len > 0);
            group++;
            cfo += 128;
        }
    }

    private void readBandType5And6(BitReader in, float[] coef, int idx, int g, int sfb, float[] vq, VLC vlc2) {
        int g_len = this.group_len[g];
        int cfo = this.swbOffset[sfb];
        int off_len = this.swbOffset[sfb + 1] - this.swbOffset[sfb];
        int group = 0;
        while (group < g_len) {
            int cf = cfo;
            int len = off_len;
            do {
                int cb_idx = vlc2.readVLC(in);
                VMUL2(coef, cf, vq, cb_idx, (float) this.sfs[idx]);
                cf += 2;
                len -= 2;
            } while (len > 0);
            group++;
            cfo += 128;
        }
    }

    @Override // org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        this.globalGain = in.readNBit(8);
        if (!this.commonWindow && !this.scaleFlag) {
            parseICSInfo(in);
        }
        decodeBandTypes(in);
        decodeScalefactors(in);
        if (!this.scaleFlag) {
            int pulse_present = in.read1Bit();
            if (pulse_present != 0) {
                if (this.windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE.ordinal()) {
                    throw new RuntimeException("Pulse tool not allowed in eight short sequence.");
                }
                decodePulses(in);
            }
            int tns_present = in.read1Bit();
            if (tns_present != 0) {
                decodeTns(in);
            }
            if (in.read1Bit() != 0) {
                throw new RuntimeException("SSR is not supported");
            }
        }
        decodeSpectrum(in);
    }
}
