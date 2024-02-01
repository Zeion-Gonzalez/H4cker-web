package org.jcodec.codecs.h264.io.model;

import java.util.Comparator;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;

/* loaded from: classes.dex */
public class Frame extends Picture {
    public static Comparator<Frame> POCAsc = new Comparator<Frame>() { // from class: org.jcodec.codecs.h264.io.model.Frame.1
        @Override // java.util.Comparator
        public int compare(Frame o1, Frame o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1.poc <= o2.poc) {
                return o1.poc == o2.poc ? 0 : -1;
            }
            return 1;
        }
    };
    public static Comparator<Frame> POCDesc = new Comparator<Frame>() { // from class: org.jcodec.codecs.h264.io.model.Frame.2
        @Override // java.util.Comparator
        public int compare(Frame o1, Frame o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1.poc >= o2.poc) {
                return o1.poc == o2.poc ? 0 : -1;
            }
            return 1;
        }
    };
    private int frameNo;
    private int[][][][] mvs;
    private int poc;
    private Frame[][][] refsUsed;
    private boolean shortTerm;

    public Frame(int width, int height, int[][] data, ColorSpace color, Rect crop, int frameNo, int[][][][] mvs, Frame[][][] refsUsed, int poc) {
        super(width, height, data, color, crop);
        this.frameNo = frameNo;
        this.mvs = mvs;
        this.refsUsed = refsUsed;
        this.poc = poc;
        this.shortTerm = true;
    }

    public static Frame createFrame(Frame pic) {
        Picture comp = pic.createCompatible();
        return new Frame(comp.getWidth(), comp.getHeight(), comp.getData(), comp.getColor(), pic.getCrop(), pic.frameNo, pic.mvs, pic.refsUsed, pic.poc);
    }

    @Override // org.jcodec.common.model.Picture
    public Frame cropped() {
        Picture cropped = super.cropped();
        return new Frame(cropped.getWidth(), cropped.getHeight(), cropped.getData(), cropped.getColor(), null, this.frameNo, this.mvs, this.refsUsed, this.poc);
    }

    public void copyFrom(Frame src) {
        super.copyFrom((Picture) src);
        this.frameNo = src.frameNo;
        this.mvs = src.mvs;
        this.shortTerm = src.shortTerm;
        this.refsUsed = src.refsUsed;
        this.poc = src.poc;
    }

    public int getFrameNo() {
        return this.frameNo;
    }

    public int[][][][] getMvs() {
        return this.mvs;
    }

    public boolean isShortTerm() {
        return this.shortTerm;
    }

    public void setShortTerm(boolean shortTerm) {
        this.shortTerm = shortTerm;
    }

    public int getPOC() {
        return this.poc;
    }

    public Frame[][][] getRefsUsed() {
        return this.refsUsed;
    }
}
