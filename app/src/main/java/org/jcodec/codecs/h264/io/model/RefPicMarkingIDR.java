package org.jcodec.codecs.h264.io.model;

/* loaded from: classes.dex */
public class RefPicMarkingIDR {
    boolean discardDecodedPics;
    boolean useForlongTerm;

    public RefPicMarkingIDR(boolean discardDecodedPics, boolean useForlongTerm) {
        this.discardDecodedPics = discardDecodedPics;
        this.useForlongTerm = useForlongTerm;
    }

    public boolean isDiscardDecodedPics() {
        return this.discardDecodedPics;
    }

    public boolean isUseForlongTerm() {
        return this.useForlongTerm;
    }
}
