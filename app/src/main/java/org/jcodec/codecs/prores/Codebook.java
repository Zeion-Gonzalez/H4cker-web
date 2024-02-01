package org.jcodec.codecs.prores;

/* loaded from: classes.dex */
public class Codebook {
    int expOrder;
    int golombBits;
    int golombOffset;
    int riceMask;
    int riceOrder;
    int switchBits;

    public Codebook(int riceOrder, int expOrder, int switchBits) {
        this.riceOrder = riceOrder;
        this.expOrder = expOrder;
        this.switchBits = switchBits;
        this.golombOffset = (1 << expOrder) - ((switchBits + 1) << riceOrder);
        this.golombBits = (expOrder - switchBits) - 1;
        this.riceMask = (1 << riceOrder) - 1;
    }
}
