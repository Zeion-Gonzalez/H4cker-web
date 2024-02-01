package org.jcodec.movtool.streaming;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;

/* loaded from: classes.dex */
public class VideoCodecMeta extends CodecMeta {
    private boolean interlaced;
    private Rational pasp;
    private Size size;
    private boolean topFieldFirst;

    public VideoCodecMeta(String fourcc, ByteBuffer codecPrivate, Size size, Rational pasp) {
        super(fourcc, codecPrivate);
        this.size = size;
        this.pasp = pasp;
    }

    public VideoCodecMeta(String fourcc, ByteBuffer codecPrivate, Size size, Rational pasp, boolean interlaced, boolean topFieldFirst) {
        super(fourcc, codecPrivate);
        this.size = size;
        this.pasp = pasp;
        this.interlaced = interlaced;
        this.topFieldFirst = topFieldFirst;
    }

    public Size getSize() {
        return this.size;
    }

    public Rational getPasp() {
        return this.pasp;
    }

    public boolean isInterlaced() {
        return this.interlaced;
    }

    public boolean isTopFieldFirst() {
        return this.topFieldFirst;
    }
}
