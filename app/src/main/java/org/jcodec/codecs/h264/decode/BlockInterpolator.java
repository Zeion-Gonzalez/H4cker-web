package org.jcodec.codecs.h264.decode;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class BlockInterpolator {
    private static int[] tmp1 = new int[1024];
    private static LumaInterpolator[] safe = {new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.1
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma00(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.2
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma10(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.3
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma20(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.4
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma30(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.5
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma01(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.6
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma11(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.7
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma21(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.8
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma31(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.9
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma02(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.10
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma12(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.11
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma22(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.12
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma32(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.13
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma03(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.14
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma13(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.15
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma23(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.16
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma33(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }};
    private static LumaInterpolator[] unsafe = {new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.17
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma00Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.18
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma10Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.19
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma20Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.20
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma30Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.21
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma01Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.22
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma11Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.23
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma21Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.24
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma31Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.25
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma02Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.26
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma12Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.27
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma22Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.28
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma32Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.29
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma03Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.30
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma13Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.31
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma23Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }, new LumaInterpolator() { // from class: org.jcodec.codecs.h264.decode.BlockInterpolator.32
        @Override // org.jcodec.codecs.h264.decode.BlockInterpolator.LumaInterpolator
        public void getLuma(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
            BlockInterpolator.getLuma33Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        }
    }};

    /* loaded from: classes.dex */
    private interface LumaInterpolator {
        void getLuma(int[] iArr, int i, int i2, int[] iArr2, int i3, int i4, int i5, int i6, int i7, int i8);
    }

    public static void getBlockLuma(Picture pic, Picture out, int off, int x, int y, int w, int h) {
        int xInd = x & 3;
        int yInd = y & 3;
        int xFp = x >> 2;
        int yFp = y >> 2;
        if (xFp < 2 || yFp < 2 || xFp > (pic.getWidth() - w) - 5 || yFp > (pic.getHeight() - h) - 5) {
            unsafe[(yInd << 2) + xInd].getLuma(pic.getData()[0], pic.getWidth(), pic.getHeight(), out.getPlaneData(0), off, out.getPlaneWidth(0), xFp, yFp, w, h);
        } else {
            safe[(yInd << 2) + xInd].getLuma(pic.getData()[0], pic.getWidth(), pic.getHeight(), out.getPlaneData(0), off, out.getPlaneWidth(0), xFp, yFp, w, h);
        }
    }

    public static void getBlockChroma(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int xInd = x & 7;
        int yInd = y & 7;
        int xFull = x >> 3;
        int yFull = y >> 3;
        if (xFull < 0 || xFull > (picW - blkW) - 1 || yFull < 0 || yFull > (picH - blkH) - 1) {
            if (xInd == 0 && yInd == 0) {
                getChroma00Unsafe(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, blkW, blkH);
                return;
            }
            if (yInd == 0) {
                getChromaX0Unsafe(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, xInd, blkW, blkH);
                return;
            } else if (xInd == 0) {
                getChroma0XUnsafe(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, yInd, blkW, blkH);
                return;
            } else {
                getChromaXXUnsafe(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, xInd, yInd, blkW, blkH);
                return;
            }
        }
        if (xInd == 0 && yInd == 0) {
            getChroma00(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, blkW, blkH);
            return;
        }
        if (yInd == 0) {
            getChromaX0(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, xInd, blkW, blkH);
        } else if (xInd == 0) {
            getChroma0X(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, yInd, blkW, blkH);
        } else {
            getChromaXX(pels, picW, picH, blk, blkOff, blkStride, xFull, yFull, xInd, yInd, blkW, blkH);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma00(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            System.arraycopy(pic, off, blk, blkOff, blkW);
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma00Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, j + y) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = pic[iClip3(0, maxW, x + i) + lineStart];
            }
            blkOff += blkStride;
        }
    }

    private static void getLuma20NoRound(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            int off1 = -2;
            for (int i = 0; i < blkW; i++) {
                int val = ((pic[off + off1] + pic[(off + off1) + 5]) - ((pic[(off + off1) + 1] + pic[(off + off1) + 4]) * 5)) + ((pic[off + off1 + 2] + pic[off + off1 + 3]) * 20);
                off1++;
                blk[blkOff + i] = val;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma20(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20NoRound(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = iClip3(0, 255, (blk[blkOff + i] + 16) >> 5);
            }
            blkOff += blkStride;
        }
    }

    private static void getLuma20UnsafeNoRound(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxW = picW - 1;
        int maxH = picH - 1;
        for (int i = 0; i < blkW; i++) {
            int ipos_m2 = iClip3(0, maxW, (x + i) - 2);
            int ipos_m1 = iClip3(0, maxW, (x + i) - 1);
            int ipos = iClip3(0, maxW, x + i);
            int ipos_p1 = iClip3(0, maxW, x + i + 1);
            int ipos_p2 = iClip3(0, maxW, x + i + 2);
            int ipos_p3 = iClip3(0, maxW, x + i + 3);
            int boff = blkOff;
            for (int j = 0; j < blkH; j++) {
                int lineStart = iClip3(0, maxH, j + y) * picW;
                int result = ((pic[lineStart + ipos_m2] + pic[lineStart + ipos_p3]) - ((pic[lineStart + ipos_m1] + pic[lineStart + ipos_p2]) * 5)) + ((pic[lineStart + ipos] + pic[lineStart + ipos_p1]) * 20);
                blk[boff + i] = result;
                boff += blkStride;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma20Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20UnsafeNoRound(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int i = 0; i < blkW; i++) {
            int boff = blkOff;
            for (int j = 0; j < blkH; j++) {
                blk[boff + i] = iClip3(0, 255, (blk[boff + i] + 16) >> 5);
                boff += blkStride;
            }
        }
    }

    private static void getLuma02NoRound(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int off = ((y - 2) * picW) + x;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int val = ((pic[off + i] + pic[(off + i) + (picW * 5)]) - ((pic[(off + i) + picW] + pic[(off + i) + (picW * 4)]) * 5)) + ((pic[off + i + (picW * 2)] + pic[off + i + (picW * 3)]) * 20);
                blk[blkOff + i] = val;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma02(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma02NoRound(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = iClip3(0, 255, (blk[blkOff + i] + 16) >> 5);
            }
            blkOff += blkStride;
        }
    }

    private static void getLuma02UnsafeNoRound(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        for (int j = 0; j < blkH; j++) {
            int offP0 = iClip3(0, maxH, (y + j) - 2) * picW;
            int offP1 = iClip3(0, maxH, (y + j) - 1) * picW;
            int offP2 = iClip3(0, maxH, y + j) * picW;
            int offP3 = iClip3(0, maxH, y + j + 1) * picW;
            int offP4 = iClip3(0, maxH, y + j + 2) * picW;
            int offP5 = iClip3(0, maxH, y + j + 3) * picW;
            for (int i = 0; i < blkW; i++) {
                int pres_x = iClip3(0, maxW, x + i);
                int result = ((pic[pres_x + offP0] + pic[pres_x + offP5]) - ((pic[pres_x + offP1] + pic[pres_x + offP4]) * 5)) + ((pic[pres_x + offP2] + pic[pres_x + offP3]) * 20);
                blk[blkOff + i] = result;
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma02Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma02UnsafeNoRound(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = iClip3(0, 255, (blk[blkOff + i] + 16) >> 5);
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma10(int[] oic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(oic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + oic[off + i]) + 1) >> 1;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma10Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        getLuma20Unsafe(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, j + y) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[iClip3(0, maxW, x + i) + lineStart]) + 1) >> 1;
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma30(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((pic[(off + i) + 1] + blk[blkOff + i]) + 1) >> 1;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma30Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        getLuma20Unsafe(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, j + y) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[iClip3(0, maxW, (x + i) + 1) + lineStart]) + 1) >> 1;
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma01(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma02(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[off + i]) + 1) >> 1;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma01Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        getLuma02Unsafe(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, y + j) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[iClip3(0, maxW, x + i) + lineStart]) + 1) >> 1;
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma03(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma02(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[(off + i) + picW]) + 1) >> 1;
            }
            off += picW;
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma03Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        getLuma02Unsafe(pic, picW, picH, blk, blkOff, blkStride, x, y, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, y + j + 1) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + pic[iClip3(0, maxW, x + i) + lineStart]) + 1) >> 1;
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma21(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20NoRound(pic, picW, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        int off = blkW << 1;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[off + i] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += blkW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma21Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20UnsafeNoRound(pic, picW, imgH, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        int off = blkW << 1;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[off + i] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += blkW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma22(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20NoRound(pic, picW, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma22Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20UnsafeNoRound(pic, picW, imgH, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
            }
            blkOff += blkStride;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma23(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20NoRound(pic, picW, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        int off = blkW << 1;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[(off + i) + blkW] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += blkW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma23Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20UnsafeNoRound(pic, picW, imgH, tmp1, 0, blkW, x, y - 2, blkW, blkH + 7);
        getLuma02NoRound(tmp1, blkW, blk, blkOff, blkStride, 0, 2, blkW, blkH);
        int off = blkW << 1;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[(off + i) + blkW] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += blkW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma12(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int tmpW = blkW + 7;
        getLuma02NoRound(pic, picW, tmp1, 0, tmpW, x - 2, y, tmpW, blkH);
        getLuma20NoRound(tmp1, tmpW, blk, blkOff, blkStride, 2, 0, blkW, blkH);
        int off = 2;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[off + i] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += tmpW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma12Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int tmpW = blkW + 7;
        getLuma02UnsafeNoRound(pic, picW, imgH, tmp1, 0, tmpW, x - 2, y, tmpW, blkH);
        getLuma20NoRound(tmp1, tmpW, blk, blkOff, blkStride, 2, 0, blkW, blkH);
        int off = 2;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[off + i] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += tmpW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma32(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int tmpW = blkW + 7;
        getLuma02NoRound(pic, picW, tmp1, 0, tmpW, x - 2, y, tmpW, blkH);
        getLuma20NoRound(tmp1, tmpW, blk, blkOff, blkStride, 2, 0, blkW, blkH);
        int off = 2;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[(off + i) + 1] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += tmpW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma32Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int tmpW = blkW + 7;
        getLuma02UnsafeNoRound(pic, picW, imgH, tmp1, 0, tmpW, x - 2, y, tmpW, blkH);
        getLuma20NoRound(tmp1, tmpW, blk, blkOff, blkStride, 2, 0, blkW, blkH);
        int off = 2;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int rounded = iClip3(0, 255, (blk[blkOff + i] + 512) >> 10);
                int rounded2 = iClip3(0, 255, (tmp1[(off + i) + 1] + 16) >> 5);
                blk[blkOff + i] = ((rounded + rounded2) + 1) >> 1;
            }
            blkOff += blkStride;
            off += tmpW;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma33(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(pic, picW, blk, blkOff, blkStride, x, y + 1, blkW, blkH);
        getLuma02(pic, picW, tmp1, 0, blkW, x + 1, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma33Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20Unsafe(pic, picW, imgH, blk, blkOff, blkStride, x, y + 1, blkW, blkH);
        getLuma02Unsafe(pic, picW, imgH, tmp1, 0, blkW, x + 1, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma11(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(pic, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        getLuma02(pic, picW, tmp1, 0, blkW, x, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma11Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20Unsafe(pic, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        getLuma02Unsafe(pic, picW, imgH, tmp1, 0, blkW, x, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma13(int[] pic, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(pic, picW, blk, blkOff, blkStride, x, y + 1, blkW, blkH);
        getLuma02(pic, picW, tmp1, 0, blkW, x, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma13Unsafe(int[] pic, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20Unsafe(pic, picW, imgH, blk, blkOff, blkStride, x, y + 1, blkW, blkH);
        getLuma02Unsafe(pic, picW, imgH, tmp1, 0, blkW, x, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma31(int[] pels, int picW, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20(pels, picW, blk, blkOff, blkStride, x, y, blkW, blkH);
        getLuma02(pels, picW, tmp1, 0, blkW, x + 1, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void getLuma31Unsafe(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        getLuma20Unsafe(pels, picW, imgH, blk, blkOff, blkStride, x, y, blkW, blkH);
        getLuma02Unsafe(pels, picW, imgH, tmp1, 0, blkW, x + 1, y, blkW, blkH);
        mergeCrap(blk, blkOff, blkStride, blkW, blkH);
    }

    private static int iClip3(int min, int max, int val) {
        return val < min ? min : val > max ? max : val;
    }

    private static void mergeCrap(int[] blk, int blkOff, int blkStride, int blkW, int blkH) {
        int tOff = 0;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((blk[blkOff + i] + tmp1[tOff + i]) + 1) >> 1;
            }
            blkOff += blkStride;
            tOff += blkW;
        }
    }

    private static void getChroma00(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int off = (y * picW) + x;
        for (int j = 0; j < blkH; j++) {
            System.arraycopy(pic, off, blk, blkOff, blkW);
            off += picW;
            blkOff += blkStride;
        }
    }

    private static void getChroma00Unsafe(int[] pic, int picW, int picH, int[] blk, int blkOff, int blkStride, int x, int y, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        for (int j = 0; j < blkH; j++) {
            int lineStart = iClip3(0, maxH, j + y) * picW;
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = pic[iClip3(0, maxW, x + i) + lineStart];
            }
            blkOff += blkStride;
        }
    }

    private static void getChroma0X(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracY, int blkW, int blkH) {
        int w00 = (fullY * picW) + fullX;
        int w01 = w00 + (fullY < picH + (-1) ? picW : 0);
        int eMy = 8 - fracY;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = (((pels[w00 + i] * eMy) + (pels[w01 + i] * fracY)) + 4) >> 3;
            }
            w00 += picW;
            w01 += picW;
            blkOff += blkStride;
        }
    }

    private static void getChroma0XUnsafe(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracY, int blkW, int blkH) {
        int maxW = picW - 1;
        int maxH = picH - 1;
        int eMy = 8 - fracY;
        for (int j = 0; j < blkH; j++) {
            int off00 = iClip3(0, maxH, fullY + j) * picW;
            int off01 = iClip3(0, maxH, fullY + j + 1) * picW;
            for (int i = 0; i < blkW; i++) {
                int w00 = iClip3(0, maxW, fullX + i) + off00;
                int w01 = iClip3(0, maxW, fullX + i) + off01;
                blk[blkOff + i] = (((pels[w00] * eMy) + (pels[w01] * fracY)) + 4) >> 3;
            }
            blkOff += blkStride;
        }
    }

    private static void getChromaX0(int[] pels, int picW, int imgH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracX, int blkW, int blkH) {
        int w00 = (fullY * picW) + fullX;
        int w10 = w00 + (fullX < picW + (-1) ? 1 : 0);
        int eMx = 8 - fracX;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = (((pels[w00 + i] * eMx) + (pels[w10 + i] * fracX)) + 4) >> 3;
            }
            w00 += picW;
            w10 += picW;
            blkOff += blkStride;
        }
    }

    private static void getChromaX0Unsafe(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracX, int blkW, int blkH) {
        int eMx = 8 - fracX;
        int maxW = picW - 1;
        int maxH = picH - 1;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int w00 = (iClip3(0, maxH, fullY + j) * picW) + iClip3(0, maxW, fullX + i);
                int w10 = (iClip3(0, maxH, fullY + j) * picW) + iClip3(0, maxW, fullX + i + 1);
                blk[blkOff + i] = (((pels[w00] * eMx) + (pels[w10] * fracX)) + 4) >> 3;
            }
            blkOff += blkStride;
        }
    }

    private static void getChromaXX(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracX, int fracY, int blkW, int blkH) {
        int w00 = (fullY * picW) + fullX;
        int w01 = w00 + (fullY < picH + (-1) ? picW : 0);
        int w10 = w00 + (fullX < picW + (-1) ? 1 : 0);
        int w11 = (w10 + w01) - w00;
        int eMx = 8 - fracX;
        int eMy = 8 - fracY;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                blk[blkOff + i] = ((((((eMx * eMy) * pels[w00 + i]) + ((fracX * eMy) * pels[w10 + i])) + ((eMx * fracY) * pels[w01 + i])) + ((fracX * fracY) * pels[w11 + i])) + 32) >> 6;
            }
            blkOff += blkStride;
            w00 += picW;
            w01 += picW;
            w10 += picW;
            w11 += picW;
        }
    }

    private static void getChromaXXUnsafe(int[] pels, int picW, int picH, int[] blk, int blkOff, int blkStride, int fullX, int fullY, int fracX, int fracY, int blkW, int blkH) {
        int maxH = picH - 1;
        int maxW = picW - 1;
        int eMx = 8 - fracX;
        int eMy = 8 - fracY;
        for (int j = 0; j < blkH; j++) {
            for (int i = 0; i < blkW; i++) {
                int w00 = (iClip3(0, maxH, fullY + j) * picW) + iClip3(0, maxW, fullX + i);
                int w01 = (iClip3(0, maxH, fullY + j + 1) * picW) + iClip3(0, maxW, fullX + i);
                int w10 = (iClip3(0, maxH, fullY + j) * picW) + iClip3(0, maxW, fullX + i + 1);
                int w11 = (iClip3(0, maxH, fullY + j + 1) * picW) + iClip3(0, maxW, fullX + i + 1);
                blk[blkOff + i] = ((((((eMx * eMy) * pels[w00]) + ((fracX * eMy) * pels[w10])) + ((eMx * fracY) * pels[w01])) + ((fracX * fracY) * pels[w11])) + 32) >> 6;
            }
            blkOff += blkStride;
        }
    }
}
