package org.jcodec.codecs.h264.io.model;

/* loaded from: classes.dex */
public enum SliceType {
    P,
    B,
    I,
    SP,
    SI;

    public boolean isIntra() {
        return this == I || this == SI;
    }

    public boolean isInter() {
        return (this == I || this == SI) ? false : true;
    }

    public static SliceType fromValue(int j) {
        SliceType[] arr$ = values();
        for (SliceType sliceType : arr$) {
            if (sliceType.ordinal() == j) {
                return sliceType;
            }
        }
        return null;
    }
}
