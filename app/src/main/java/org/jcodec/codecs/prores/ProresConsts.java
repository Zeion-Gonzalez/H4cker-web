package org.jcodec.codecs.prores;

/* loaded from: classes.dex */
public class ProresConsts {
    public static Codebook firstDCCodebook = new Codebook(5, 6, 0);
    public static final Codebook[] dcCodebooks = {new Codebook(0, 1, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(2, 3, 1), new Codebook(2, 3, 1), new Codebook(3, 4, 0), new Codebook(3, 4, 0)};
    public static final Codebook[] runCodebooks = {new Codebook(0, 1, 2), new Codebook(0, 1, 2), new Codebook(0, 1, 1), new Codebook(0, 1, 1), new Codebook(0, 1, 0), new Codebook(1, 2, 1), new Codebook(1, 2, 1), new Codebook(1, 2, 1), new Codebook(1, 2, 1), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(2, 3, 0)};
    public static final Codebook[] levCodebooks = {new Codebook(0, 1, 0), new Codebook(0, 2, 2), new Codebook(0, 1, 1), new Codebook(0, 1, 2), new Codebook(0, 1, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(1, 2, 0), new Codebook(2, 3, 0)};
    public static int[] progressive_scan = {0, 1, 8, 9, 2, 3, 10, 11, 16, 17, 24, 25, 18, 19, 26, 27, 4, 5, 12, 20, 13, 6, 7, 14, 21, 28, 29, 22, 15, 23, 30, 31, 32, 33, 40, 48, 41, 34, 35, 42, 49, 56, 57, 50, 43, 36, 37, 44, 51, 58, 59, 52, 45, 38, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63};
    public static int[] interlaced_scan = {0, 8, 1, 9, 16, 24, 17, 25, 2, 10, 3, 11, 18, 26, 19, 27, 32, 40, 33, 34, 41, 48, 56, 49, 42, 35, 43, 50, 57, 58, 51, 59, 4, 12, 5, 6, 13, 20, 28, 21, 14, 7, 15, 22, 29, 36, 44, 37, 30, 23, 31, 38, 45, 52, 60, 53, 46, 39, 47, 54, 61, 62, 55, 63};
    public static final int[] QMAT_LUMA_APCH = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 5, 5, 6, 4, 4, 4, 4, 5, 5, 6, 7, 4, 4, 4, 4, 5, 6, 7, 7};
    public static final int[] QMAT_CHROMA_APCH = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 5, 5, 6, 4, 4, 4, 4, 5, 5, 6, 7, 4, 4, 4, 4, 5, 6, 7, 7};
    public static final int[] QMAT_LUMA_APCO = {4, 7, 9, 11, 13, 14, 15, 63, 7, 7, 11, 12, 14, 15, 63, 63, 9, 11, 13, 14, 15, 63, 63, 63, 11, 11, 13, 14, 63, 63, 63, 63, 11, 13, 14, 63, 63, 63, 63, 63, 13, 14, 63, 63, 63, 63, 63, 63, 13, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63};
    public static final int[] QMAT_CHROMA_APCO = {4, 7, 9, 11, 13, 14, 63, 63, 7, 7, 11, 12, 14, 63, 63, 63, 9, 11, 13, 14, 63, 63, 63, 63, 11, 11, 13, 14, 63, 63, 63, 63, 11, 13, 14, 63, 63, 63, 63, 63, 13, 14, 63, 63, 63, 63, 63, 63, 13, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63};
    public static final int[] QMAT_LUMA_APCN = {4, 4, 5, 5, 6, 7, 7, 9, 4, 4, 5, 6, 7, 7, 9, 9, 5, 5, 6, 7, 7, 9, 9, 10, 5, 5, 6, 7, 7, 9, 9, 10, 5, 6, 7, 7, 8, 9, 10, 12, 6, 7, 7, 8, 9, 10, 12, 15, 6, 7, 7, 9, 10, 11, 14, 17, 7, 7, 9, 10, 11, 14, 17, 21};
    public static final int[] QMAT_CHROMA_APCN = {4, 4, 5, 5, 6, 7, 7, 9, 4, 4, 5, 6, 7, 7, 9, 9, 5, 5, 6, 7, 7, 9, 9, 10, 5, 5, 6, 7, 7, 9, 9, 10, 5, 6, 7, 7, 8, 9, 10, 12, 6, 7, 7, 8, 9, 10, 12, 15, 6, 7, 7, 9, 10, 11, 14, 17, 7, 7, 9, 10, 11, 14, 17, 21};
    public static final int[] QMAT_LUMA_APCS = {4, 5, 6, 7, 9, 11, 13, 15, 5, 5, 7, 8, 11, 13, 15, 17, 6, 7, 9, 11, 13, 15, 15, 17, 7, 7, 9, 11, 13, 15, 17, 19, 7, 9, 11, 13, 14, 16, 19, 23, 9, 11, 13, 14, 16, 19, 23, 29, 9, 11, 13, 15, 17, 21, 28, 35, 11, 13, 16, 17, 21, 28, 35, 41};
    public static final int[] QMAT_CHROMA_APCS = {4, 5, 6, 7, 9, 11, 13, 15, 5, 5, 7, 8, 11, 13, 15, 17, 6, 7, 9, 11, 13, 15, 15, 17, 7, 7, 9, 11, 13, 15, 17, 19, 7, 9, 11, 13, 14, 16, 19, 23, 9, 11, 13, 14, 16, 19, 23, 29, 9, 11, 13, 15, 17, 21, 28, 35, 11, 13, 16, 17, 21, 28, 35, 41};

    /* loaded from: classes.dex */
    public static class FrameHeader {
        public int chromaType;
        public int frameType;
        public int height;
        public int payloadSize;
        public int[] qMatChroma;
        public int[] qMatLuma;
        public int[] scan;
        public boolean topFieldFirst;
        public int width;

        public FrameHeader(int frameSize, int width, int height, int frameType, boolean topFieldFirst, int[] scan, int[] qMatLuma, int[] qMatChroma, int chromaType) {
            this.payloadSize = frameSize;
            this.width = width;
            this.height = height;
            this.frameType = frameType;
            this.topFieldFirst = topFieldFirst;
            this.scan = scan;
            this.qMatChroma = qMatChroma;
            this.qMatLuma = qMatLuma;
            this.chromaType = chromaType;
        }
    }

    /* loaded from: classes.dex */
    public static class PictureHeader {
        public int log2SliceMbWidth;
        public short[] sliceSizes;

        public PictureHeader(int log2SliceMbWidth, short[] sliceSizes) {
            this.log2SliceMbWidth = log2SliceMbWidth;
            this.sliceSizes = sliceSizes;
        }
    }
}
