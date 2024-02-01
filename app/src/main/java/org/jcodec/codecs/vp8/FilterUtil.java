package org.jcodec.codecs.vp8;

import org.jcodec.codecs.vp8.Macroblock;

/* loaded from: classes.dex */
public class FilterUtil {
    /* JADX INFO: Access modifiers changed from: private */
    public static int clipPlus128(int v) {
        return clipSigned(v) + 128;
    }

    /* loaded from: classes.dex */
    public static class Segment {

        /* renamed from: p0 */
        int f1478p0;

        /* renamed from: p1 */
        int f1479p1;

        /* renamed from: p2 */
        int f1480p2;

        /* renamed from: p3 */
        int f1481p3;

        /* renamed from: q0 */
        int f1482q0;

        /* renamed from: q1 */
        int f1483q1;

        /* renamed from: q2 */
        int f1484q2;

        /* renamed from: q3 */
        int f1485q3;

        public boolean isFilterRequired(int interior, int edge) {
            return (Math.abs(this.f1478p0 - this.f1482q0) << 2) + (Math.abs(this.f1479p1 - this.f1483q1) >> 2) <= edge && Math.abs(this.f1481p3 - this.f1480p2) <= interior && Math.abs(this.f1480p2 - this.f1479p1) <= interior && Math.abs(this.f1479p1 - this.f1478p0) <= interior && Math.abs(this.f1485q3 - this.f1484q2) <= interior && Math.abs(this.f1484q2 - this.f1483q1) <= interior && Math.abs(this.f1483q1 - this.f1482q0) <= interior;
        }

        public boolean isHighVariance(int threshold) {
            return Math.abs(this.f1479p1 - this.f1478p0) > threshold || Math.abs(this.f1483q1 - this.f1482q0) > threshold;
        }

        public Segment getSigned() {
            Segment seg = new Segment();
            seg.f1481p3 = FilterUtil.minus128(this.f1481p3);
            seg.f1480p2 = FilterUtil.minus128(this.f1480p2);
            seg.f1479p1 = FilterUtil.minus128(this.f1479p1);
            seg.f1478p0 = FilterUtil.minus128(this.f1478p0);
            seg.f1482q0 = FilterUtil.minus128(this.f1482q0);
            seg.f1483q1 = FilterUtil.minus128(this.f1483q1);
            seg.f1484q2 = FilterUtil.minus128(this.f1484q2);
            seg.f1485q3 = FilterUtil.minus128(this.f1485q3);
            return seg;
        }

        public static Segment horizontal(Macroblock.Subblock right, Macroblock.Subblock left, int a) {
            Segment seg = new Segment();
            seg.f1478p0 = left.val[a + 12];
            seg.f1479p1 = left.val[a + 8];
            seg.f1480p2 = left.val[a + 4];
            seg.f1481p3 = left.val[a + 0];
            seg.f1482q0 = right.val[a + 0];
            seg.f1483q1 = right.val[a + 4];
            seg.f1484q2 = right.val[a + 8];
            seg.f1485q3 = right.val[a + 12];
            return seg;
        }

        public static Segment vertical(Macroblock.Subblock lower, Macroblock.Subblock upper, int a) {
            Segment seg = new Segment();
            seg.f1478p0 = upper.val[(a * 4) + 3];
            seg.f1479p1 = upper.val[(a * 4) + 2];
            seg.f1480p2 = upper.val[(a * 4) + 1];
            seg.f1481p3 = upper.val[(a * 4) + 0];
            seg.f1482q0 = lower.val[(a * 4) + 0];
            seg.f1483q1 = lower.val[(a * 4) + 1];
            seg.f1484q2 = lower.val[(a * 4) + 2];
            seg.f1485q3 = lower.val[(a * 4) + 3];
            return seg;
        }

        public void applyHorizontally(Macroblock.Subblock right, Macroblock.Subblock left, int a) {
            left.val[a + 12] = this.f1478p0;
            left.val[a + 8] = this.f1479p1;
            left.val[a + 4] = this.f1480p2;
            left.val[a + 0] = this.f1481p3;
            right.val[a + 0] = this.f1482q0;
            right.val[a + 4] = this.f1483q1;
            right.val[a + 8] = this.f1484q2;
            right.val[a + 12] = this.f1485q3;
        }

        public void applyVertically(Macroblock.Subblock lower, Macroblock.Subblock upper, int a) {
            upper.val[(a * 4) + 3] = this.f1478p0;
            upper.val[(a * 4) + 2] = this.f1479p1;
            upper.val[(a * 4) + 1] = this.f1480p2;
            upper.val[(a * 4) + 0] = this.f1481p3;
            lower.val[(a * 4) + 0] = this.f1482q0;
            lower.val[(a * 4) + 1] = this.f1483q1;
            lower.val[(a * 4) + 2] = this.f1484q2;
            lower.val[(a * 4) + 3] = this.f1485q3;
        }

        void filterMb(int hevThreshold, int interiorLimit, int edgeLimit) {
            Segment signedSeg = getSigned();
            if (signedSeg.isFilterRequired(interiorLimit, edgeLimit)) {
                if (!signedSeg.isHighVariance(hevThreshold)) {
                    int w = FilterUtil.clipSigned(FilterUtil.clipSigned(signedSeg.f1479p1 - signedSeg.f1483q1) + ((signedSeg.f1482q0 - signedSeg.f1478p0) * 3));
                    int a = ((w * 27) + 63) >> 7;
                    this.f1482q0 = FilterUtil.clipPlus128(signedSeg.f1482q0 - a);
                    this.f1478p0 = FilterUtil.clipPlus128(signedSeg.f1478p0 + a);
                    int a2 = ((w * 18) + 63) >> 7;
                    this.f1483q1 = FilterUtil.clipPlus128(signedSeg.f1483q1 - a2);
                    this.f1479p1 = FilterUtil.clipPlus128(signedSeg.f1479p1 + a2);
                    int a3 = ((w * 9) + 63) >> 7;
                    this.f1484q2 = FilterUtil.clipPlus128(signedSeg.f1484q2 - a3);
                    this.f1480p2 = FilterUtil.clipPlus128(signedSeg.f1480p2 + a3);
                    return;
                }
                adjust(true);
            }
        }

        public void filterSb(int hev_threshold, int interior_limit, int edge_limit) {
            Segment signedSeg = getSigned();
            if (signedSeg.isFilterRequired(interior_limit, edge_limit)) {
                boolean hv = signedSeg.isHighVariance(hev_threshold);
                int a = (adjust(hv) + 1) >> 1;
                if (!hv) {
                    this.f1483q1 = FilterUtil.clipPlus128(signedSeg.f1483q1 - a);
                    this.f1479p1 = FilterUtil.clipPlus128(signedSeg.f1479p1 + a);
                }
            }
        }

        private int adjust(boolean use_outer_taps) {
            int i;
            int p1 = FilterUtil.minus128(this.f1479p1);
            int p0 = FilterUtil.minus128(this.f1478p0);
            int q0 = FilterUtil.minus128(this.f1482q0);
            int q1 = FilterUtil.minus128(this.f1483q1);
            if (use_outer_taps) {
                i = FilterUtil.clipSigned(p1 - q1);
            } else {
                i = 0;
            }
            int a = FilterUtil.clipSigned(i + ((q0 - p0) * 3));
            int b = FilterUtil.clipSigned(a + 3) >> 3;
            int a2 = FilterUtil.clipSigned(a + 4) >> 3;
            this.f1482q0 = FilterUtil.clipPlus128(q0 - a2);
            this.f1478p0 = FilterUtil.clipPlus128(p0 + b);
            return a2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int clipSigned(int v) {
        if (v < -128) {
            return -128;
        }
        if (v > 127) {
            return 127;
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int minus128(int v) {
        return v - 128;
    }

    public static void loopFilterUV(Macroblock[][] mbs, int sharpnessLevel, boolean keyFrame) {
        for (int y = 0; y < mbs.length - 2; y++) {
            for (int x = 0; x < mbs[0].length - 2; x++) {
                Macroblock rmb = mbs[y + 1][x + 1];
                Macroblock bmb = mbs[y + 1][x + 1];
                int loop_filter_level = rmb.filterLevel;
                if (loop_filter_level != 0) {
                    int interior_limit = rmb.filterLevel;
                    if (sharpnessLevel > 0) {
                        interior_limit >>= sharpnessLevel > 4 ? 2 : 1;
                        if (interior_limit > 9 - sharpnessLevel) {
                            interior_limit = 9 - sharpnessLevel;
                        }
                    }
                    if (interior_limit == 0) {
                        interior_limit = 1;
                    }
                    int hev_threshold = 0;
                    if (keyFrame) {
                        if (loop_filter_level >= 40) {
                            hev_threshold = 2;
                        } else if (loop_filter_level >= 15) {
                            hev_threshold = 1;
                        }
                        int mbedge_limit = ((loop_filter_level + 2) * 2) + interior_limit;
                        int sub_bedge_limit = (loop_filter_level * 2) + interior_limit;
                        if (x > 0) {
                            Macroblock lmb = mbs[y + 1][(x + 1) - 1];
                            for (int b = 0; b < 2; b++) {
                                Macroblock.Subblock rsbU = rmb.uSubblocks[b][0];
                                Macroblock.Subblock lsbU = lmb.uSubblocks[b][1];
                                Macroblock.Subblock rsbV = rmb.vSubblocks[b][0];
                                Macroblock.Subblock lsbV = lmb.vSubblocks[b][1];
                                for (int a = 0; a < 4; a++) {
                                    Segment seg = Segment.horizontal(rsbU, lsbU, a);
                                    seg.filterMb(hev_threshold, interior_limit, mbedge_limit);
                                    seg.applyHorizontally(rsbU, lsbU, a);
                                    Segment seg2 = Segment.horizontal(rsbV, lsbV, a);
                                    seg2.filterMb(hev_threshold, interior_limit, mbedge_limit);
                                    seg2.applyHorizontally(rsbV, lsbV, a);
                                }
                            }
                        }
                        if (!rmb.skipFilter) {
                            for (int a2 = 1; a2 < 2; a2++) {
                                for (int b2 = 0; b2 < 2; b2++) {
                                    Macroblock.Subblock lsbU2 = rmb.uSubblocks[b2][a2 - 1];
                                    Macroblock.Subblock rsbU2 = rmb.uSubblocks[b2][a2];
                                    Macroblock.Subblock lsbV2 = rmb.vSubblocks[b2][a2 - 1];
                                    Macroblock.Subblock rsbV2 = rmb.vSubblocks[b2][a2];
                                    for (int c = 0; c < 4; c++) {
                                        Segment seg3 = Segment.horizontal(rsbU2, lsbU2, c);
                                        seg3.filterSb(hev_threshold, interior_limit, sub_bedge_limit);
                                        seg3.applyHorizontally(rsbU2, lsbU2, c);
                                        Segment seg4 = Segment.horizontal(rsbV2, lsbV2, c);
                                        seg4.filterSb(hev_threshold, interior_limit, sub_bedge_limit);
                                        seg4.applyHorizontally(rsbV2, lsbV2, c);
                                    }
                                }
                            }
                        }
                        if (y > 0) {
                            Macroblock tmb = mbs[(y + 1) - 1][x + 1];
                            for (int b3 = 0; b3 < 2; b3++) {
                                Macroblock.Subblock tsbU = tmb.uSubblocks[1][b3];
                                Macroblock.Subblock bsbU = bmb.uSubblocks[0][b3];
                                Macroblock.Subblock tsbV = tmb.vSubblocks[1][b3];
                                Macroblock.Subblock bsbV = bmb.vSubblocks[0][b3];
                                for (int a3 = 0; a3 < 4; a3++) {
                                    Segment seg5 = Segment.vertical(bsbU, tsbU, a3);
                                    seg5.filterMb(hev_threshold, interior_limit, mbedge_limit);
                                    seg5.applyVertically(bsbU, tsbU, a3);
                                    Segment seg6 = Segment.vertical(bsbV, tsbV, a3);
                                    seg6.filterMb(hev_threshold, interior_limit, mbedge_limit);
                                    seg6.applyVertically(bsbV, tsbV, a3);
                                }
                            }
                        }
                        if (!rmb.skipFilter) {
                            for (int a4 = 1; a4 < 2; a4++) {
                                for (int b4 = 0; b4 < 2; b4++) {
                                    Macroblock.Subblock tsbU2 = bmb.uSubblocks[a4 - 1][b4];
                                    Macroblock.Subblock bsbU2 = bmb.uSubblocks[a4][b4];
                                    Macroblock.Subblock tsbV2 = bmb.vSubblocks[a4 - 1][b4];
                                    Macroblock.Subblock bsbV2 = bmb.vSubblocks[a4][b4];
                                    for (int c2 = 0; c2 < 4; c2++) {
                                        Segment seg7 = Segment.vertical(bsbU2, tsbU2, c2);
                                        seg7.filterSb(hev_threshold, interior_limit, sub_bedge_limit);
                                        seg7.applyVertically(bsbU2, tsbU2, c2);
                                        Segment seg8 = Segment.vertical(bsbV2, tsbV2, c2);
                                        seg8.filterSb(hev_threshold, interior_limit, sub_bedge_limit);
                                        seg8.applyVertically(bsbV2, tsbV2, c2);
                                    }
                                }
                            }
                        }
                    } else {
                        throw new UnsupportedOperationException("TODO: non-key frames are not supported yet.");
                    }
                }
            }
        }
    }

    public static void loopFilterY(Macroblock[][] mbs, int sharpnessLevel, boolean keyFrame) {
        for (int y = 0; y < mbs.length - 2; y++) {
            for (int x = 0; x < mbs[0].length - 2; x++) {
                Macroblock rmb = mbs[y + 1][x + 1];
                Macroblock bmb = mbs[y + 1][x + 1];
                int loopFilterLevel = rmb.filterLevel;
                if (loopFilterLevel != 0) {
                    int interiorLimit = rmb.filterLevel;
                    if (sharpnessLevel > 0) {
                        interiorLimit >>= sharpnessLevel > 4 ? 2 : 1;
                        if (interiorLimit > 9 - sharpnessLevel) {
                            interiorLimit = 9 - sharpnessLevel;
                        }
                    }
                    if (interiorLimit == 0) {
                        interiorLimit = 1;
                    }
                    int varianceThreshold = 0;
                    if (keyFrame) {
                        if (loopFilterLevel >= 40) {
                            varianceThreshold = 2;
                        } else if (loopFilterLevel >= 15) {
                            varianceThreshold = 1;
                        }
                        int edgeLimitMb = ((loopFilterLevel + 2) * 2) + interiorLimit;
                        int edgeLimitSb = (loopFilterLevel * 2) + interiorLimit;
                        if (x > 0) {
                            Macroblock lmb = mbs[y + 1][(x - 1) + 1];
                            for (int b = 0; b < 4; b++) {
                                Macroblock.Subblock rsb = rmb.ySubblocks[b][0];
                                Macroblock.Subblock lsb = lmb.ySubblocks[b][3];
                                for (int a = 0; a < 4; a++) {
                                    Segment seg = Segment.horizontal(rsb, lsb, a);
                                    seg.filterMb(varianceThreshold, interiorLimit, edgeLimitMb);
                                    seg.applyHorizontally(rsb, lsb, a);
                                }
                            }
                        }
                        if (!rmb.skipFilter) {
                            for (int a2 = 1; a2 < 4; a2++) {
                                for (int b2 = 0; b2 < 4; b2++) {
                                    Macroblock.Subblock lsb2 = rmb.ySubblocks[b2][a2 - 1];
                                    Macroblock.Subblock rsb2 = rmb.ySubblocks[b2][a2];
                                    for (int c = 0; c < 4; c++) {
                                        Segment seg2 = Segment.horizontal(rsb2, lsb2, c);
                                        seg2.filterSb(varianceThreshold, interiorLimit, edgeLimitSb);
                                        seg2.applyHorizontally(rsb2, lsb2, c);
                                    }
                                }
                            }
                        }
                        if (y > 0) {
                            Macroblock tmb = mbs[(y - 1) + 1][x + 1];
                            for (int b3 = 0; b3 < 4; b3++) {
                                Macroblock.Subblock tsb = tmb.ySubblocks[3][b3];
                                Macroblock.Subblock bsb = bmb.ySubblocks[0][b3];
                                for (int a3 = 0; a3 < 4; a3++) {
                                    Segment seg3 = Segment.vertical(bsb, tsb, a3);
                                    seg3.filterMb(varianceThreshold, interiorLimit, edgeLimitMb);
                                    seg3.applyVertically(bsb, tsb, a3);
                                }
                            }
                        }
                        if (!rmb.skipFilter) {
                            for (int a4 = 1; a4 < 4; a4++) {
                                for (int b4 = 0; b4 < 4; b4++) {
                                    Macroblock.Subblock tsb2 = bmb.ySubblocks[a4 - 1][b4];
                                    Macroblock.Subblock bsb2 = bmb.ySubblocks[a4][b4];
                                    for (int c2 = 0; c2 < 4; c2++) {
                                        Segment seg4 = Segment.vertical(bsb2, tsb2, c2);
                                        seg4.filterSb(varianceThreshold, interiorLimit, edgeLimitSb);
                                        seg4.applyVertically(bsb2, tsb2, c2);
                                    }
                                }
                            }
                        }
                    } else {
                        throw new UnsupportedOperationException("TODO: non-key frames are not supported yet");
                    }
                }
            }
        }
    }
}
