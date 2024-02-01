package org.jcodec.codecs.h264.io.model;

/* loaded from: classes.dex */
public enum MBType {
    I_NxN(true),
    I_16x16(true),
    I_PCM(true),
    P_16x16,
    P_16x8,
    P_8x16,
    P_8x8,
    P_8x8ref0,
    B_Direct_16x16,
    B_L0_16x16,
    B_L1_16x16,
    B_Bi_16x16,
    B_L0_L0_16x8,
    B_L0_L0_8x16,
    B_L1_L1_16x8,
    B_L1_L1_8x16,
    B_L0_L1_16x8,
    B_L0_L1_8x16,
    B_L1_L0_16x8,
    B_L1_L0_8x16,
    B_L0_Bi_16x8,
    B_L0_Bi_8x16,
    B_L1_Bi_16x8,
    B_L1_Bi_8x16,
    B_Bi_L0_16x8,
    B_Bi_L0_8x16,
    B_Bi_L1_16x8,
    B_Bi_L1_8x16,
    B_Bi_Bi_16x8,
    B_Bi_Bi_8x16,
    B_8x8;

    public boolean intra;

    MBType(boolean intra) {
        this.intra = intra;
    }

    MBType() {
        this(false);
    }

    public boolean isIntra() {
        return this.intra;
    }
}
