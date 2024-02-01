package org.jcodec.common.dct;

import java.nio.ShortBuffer;

/* loaded from: classes.dex */
public class FfmpegIntDct {
    private static final int CONST_BITS = 13;

    /* renamed from: D1 */
    private static final int f1509D1 = 11;

    /* renamed from: D2 */
    private static final int f1510D2 = 18;
    private static final int DCTSIZE = 8;
    private static final int DCTSIZE_0 = 0;
    private static final int DCTSIZE_1 = 8;
    private static final int DCTSIZE_2 = 16;
    private static final int DCTSIZE_3 = 24;
    private static final int DCTSIZE_4 = 32;
    private static final int DCTSIZE_5 = 40;
    private static final int DCTSIZE_6 = 48;
    private static final int DCTSIZE_7 = 56;
    private static final short FIX_0_211164243 = 1730;
    private static final short FIX_0_275899380 = 2260;
    private static final short FIX_0_298631336 = 2446;
    private static final short FIX_0_390180644 = 3196;
    private static final short FIX_0_509795579 = 4176;
    private static final short FIX_0_541196100 = 4433;
    private static final short FIX_0_601344887 = 4926;
    private static final short FIX_0_765366865 = 6270;
    private static final short FIX_0_785694958 = 6436;
    private static final short FIX_0_899976223 = 7373;
    private static final short FIX_1_061594337 = 8697;
    private static final short FIX_1_111140466 = 9102;
    private static final short FIX_1_175875602 = 9633;
    private static final short FIX_1_306562965 = 10703;
    private static final short FIX_1_387039845 = 11363;
    private static final short FIX_1_451774981 = 11893;
    private static final short FIX_1_501321110 = 12299;
    private static final short FIX_1_662939225 = 13623;
    private static final short FIX_1_847759065 = 15137;
    private static final short FIX_1_961570560 = 16069;
    private static final short FIX_2_053119869 = 16819;
    private static final short FIX_2_172734803 = 17799;
    private static final short FIX_2_562915447 = 20995;
    private static final short FIX_3_072711026 = 25172;
    private static final int ONEHALF_11 = 1024;
    private static final int ONEHALF_18 = 131072;
    private static final int PASS1_BITS = 2;

    public short[] decode(short[] orig) {
        ShortBuffer data = ShortBuffer.wrap(orig);
        pass1(data);
        pass2(data);
        return orig;
    }

    private static ShortBuffer advance(ShortBuffer dataptr, int size) {
        return ((ShortBuffer) dataptr.position(dataptr.position() + size)).slice();
    }

    private static final void pass1(ShortBuffer data) {
        int tmp13;
        int tmp10;
        int tmp12;
        int tmp11;
        int tmp3;
        int tmp2;
        int tmp1;
        int tmp0;
        ShortBuffer dataptr = data.duplicate();
        for (int rowctr = 7; rowctr >= 0; rowctr--) {
            int d0 = dataptr.get(0);
            int d2 = dataptr.get(1);
            int d4 = dataptr.get(2);
            int d6 = dataptr.get(3);
            int d1 = dataptr.get(4);
            int d3 = dataptr.get(5);
            int d5 = dataptr.get(6);
            int d7 = dataptr.get(7);
            if ((d1 | d2 | d3 | d4 | d5 | d6 | d7) == 0) {
                if (d0 != 0) {
                    int dcval = d0 << 2;
                    for (int i = 0; i < 8; i++) {
                        dataptr.put(i, (short) dcval);
                    }
                }
                dataptr = advance(dataptr, 8);
            } else {
                if (d6 != 0) {
                    if (d2 != 0) {
                        int z1 = MULTIPLY(d2 + d6, FIX_0_541196100);
                        int tmp22 = z1 + MULTIPLY(-d6, FIX_1_847759065);
                        int tmp32 = z1 + MULTIPLY(d2, FIX_0_765366865);
                        int tmp02 = (d0 + d4) << 13;
                        int tmp14 = (d0 - d4) << 13;
                        tmp10 = tmp02 + tmp32;
                        tmp13 = tmp02 - tmp32;
                        tmp11 = tmp14 + tmp22;
                        tmp12 = tmp14 - tmp22;
                    } else {
                        int tmp23 = MULTIPLY(-d6, FIX_1_306562965);
                        int tmp33 = MULTIPLY(d6, FIX_0_541196100);
                        int tmp03 = (d0 + d4) << 13;
                        int tmp15 = (d0 - d4) << 13;
                        tmp10 = tmp03 + tmp33;
                        tmp13 = tmp03 - tmp33;
                        tmp11 = tmp15 + tmp23;
                        tmp12 = tmp15 - tmp23;
                    }
                } else if (d2 != 0) {
                    int tmp24 = MULTIPLY(d2, FIX_0_541196100);
                    int tmp34 = MULTIPLY(d2, FIX_1_306562965);
                    int tmp04 = (d0 + d4) << 13;
                    int tmp16 = (d0 - d4) << 13;
                    tmp10 = tmp04 + tmp34;
                    tmp13 = tmp04 - tmp34;
                    tmp11 = tmp16 + tmp24;
                    tmp12 = tmp16 - tmp24;
                } else {
                    tmp13 = (d0 + d4) << 13;
                    tmp10 = tmp13;
                    tmp12 = (d0 - d4) << 13;
                    tmp11 = tmp12;
                }
                if (d7 != 0) {
                    if (d5 != 0) {
                        if (d3 != 0) {
                            if (d1 != 0) {
                                int z12 = d7 + d1;
                                int z2 = d5 + d3;
                                int z3 = d7 + d3;
                                int z4 = d5 + d1;
                                int z5 = MULTIPLY(z3 + z4, FIX_1_175875602);
                                int tmp05 = MULTIPLY(d7, FIX_0_298631336);
                                int tmp17 = MULTIPLY(d5, FIX_2_053119869);
                                int tmp25 = MULTIPLY(d3, FIX_3_072711026);
                                int tmp35 = MULTIPLY(d1, FIX_1_501321110);
                                int z13 = MULTIPLY(-z12, FIX_0_899976223);
                                int z22 = MULTIPLY(-z2, FIX_2_562915447);
                                int z32 = MULTIPLY(-z3, FIX_1_961570560) + z5;
                                int z42 = MULTIPLY(-z4, FIX_0_390180644) + z5;
                                tmp0 = tmp05 + z13 + z32;
                                tmp1 = tmp17 + z22 + z42;
                                tmp2 = tmp25 + z22 + z32;
                                tmp3 = tmp35 + z13 + z42;
                            } else {
                                int z23 = d5 + d3;
                                int z33 = d7 + d3;
                                int z52 = MULTIPLY(z33 + d5, FIX_1_175875602);
                                int tmp06 = MULTIPLY(d7, FIX_0_298631336);
                                int tmp18 = MULTIPLY(d5, FIX_2_053119869);
                                int tmp26 = MULTIPLY(d3, FIX_3_072711026);
                                int z14 = MULTIPLY(-d7, FIX_0_899976223);
                                int z24 = MULTIPLY(-z23, FIX_2_562915447);
                                int z34 = MULTIPLY(-z33, FIX_1_961570560) + z52;
                                int z43 = MULTIPLY(-d5, FIX_0_390180644) + z52;
                                tmp0 = tmp06 + z14 + z34;
                                tmp1 = tmp18 + z24 + z43;
                                tmp2 = tmp26 + z24 + z34;
                                tmp3 = z14 + z43;
                            }
                        } else if (d1 != 0) {
                            int z15 = d7 + d1;
                            int z44 = d5 + d1;
                            int z53 = MULTIPLY(d7 + z44, FIX_1_175875602);
                            int tmp07 = MULTIPLY(d7, FIX_0_298631336);
                            int tmp19 = MULTIPLY(d5, FIX_2_053119869);
                            int tmp36 = MULTIPLY(d1, FIX_1_501321110);
                            int z16 = MULTIPLY(-z15, FIX_0_899976223);
                            int z25 = MULTIPLY(-d5, FIX_2_562915447);
                            int z35 = MULTIPLY(-d7, FIX_1_961570560);
                            int z36 = z35 + z53;
                            int z45 = MULTIPLY(-z44, FIX_0_390180644) + z53;
                            tmp0 = tmp07 + z16 + z36;
                            tmp1 = tmp19 + z25 + z45;
                            tmp2 = z25 + z36;
                            tmp3 = tmp36 + z16 + z45;
                        } else {
                            int tmp08 = MULTIPLY(-d7, FIX_0_601344887);
                            int z17 = MULTIPLY(-d7, FIX_0_899976223);
                            int z37 = MULTIPLY(-d7, FIX_1_961570560);
                            int tmp110 = MULTIPLY(-d5, FIX_0_509795579);
                            int z26 = MULTIPLY(-d5, FIX_2_562915447);
                            int z46 = MULTIPLY(-d5, FIX_0_390180644);
                            int z54 = MULTIPLY(d5 + d7, FIX_1_175875602);
                            int z38 = z37 + z54;
                            int z47 = z46 + z54;
                            tmp0 = tmp08 + z38;
                            tmp1 = tmp110 + z47;
                            tmp2 = z26 + z38;
                            tmp3 = z17 + z47;
                        }
                    } else if (d3 != 0) {
                        if (d1 != 0) {
                            int z18 = d7 + d1;
                            int z39 = d7 + d3;
                            int z55 = MULTIPLY(z39 + d1, FIX_1_175875602);
                            int tmp09 = MULTIPLY(d7, FIX_0_298631336);
                            int tmp27 = MULTIPLY(d3, FIX_3_072711026);
                            int tmp37 = MULTIPLY(d1, FIX_1_501321110);
                            int z19 = MULTIPLY(-z18, FIX_0_899976223);
                            int z27 = MULTIPLY(-d3, FIX_2_562915447);
                            int z310 = MULTIPLY(-z39, FIX_1_961570560) + z55;
                            int z48 = MULTIPLY(-d1, FIX_0_390180644) + z55;
                            tmp0 = tmp09 + z19 + z310;
                            tmp1 = z27 + z48;
                            tmp2 = tmp27 + z27 + z310;
                            tmp3 = tmp37 + z19 + z48;
                        } else {
                            int z311 = d7 + d3;
                            int tmp010 = MULTIPLY(-d7, FIX_0_601344887);
                            int z110 = MULTIPLY(-d7, FIX_0_899976223);
                            int tmp28 = MULTIPLY(d3, FIX_0_509795579);
                            int z28 = MULTIPLY(-d3, FIX_2_562915447);
                            int z56 = MULTIPLY(z311, FIX_1_175875602);
                            int z312 = MULTIPLY(-z311, FIX_0_785694958);
                            tmp0 = tmp010 + z312;
                            tmp1 = z28 + z56;
                            tmp2 = tmp28 + z312;
                            tmp3 = z110 + z56;
                        }
                    } else if (d1 != 0) {
                        int z111 = d7 + d1;
                        int z57 = MULTIPLY(z111, FIX_1_175875602);
                        int z112 = MULTIPLY(z111, FIX_0_275899380);
                        int z313 = MULTIPLY(-d7, FIX_1_961570560);
                        tmp0 = MULTIPLY(-d7, FIX_1_662939225) + z112;
                        tmp1 = MULTIPLY(-d1, FIX_0_390180644) + z57;
                        tmp2 = z313 + z57;
                        tmp3 = MULTIPLY(d1, FIX_1_111140466) + z112;
                    } else {
                        tmp0 = MULTIPLY(-d7, FIX_1_387039845);
                        tmp1 = MULTIPLY(d7, FIX_1_175875602);
                        tmp2 = MULTIPLY(-d7, FIX_0_785694958);
                        tmp3 = MULTIPLY(d7, FIX_0_275899380);
                    }
                } else if (d5 != 0) {
                    if (d3 != 0) {
                        if (d1 != 0) {
                            int z29 = d5 + d3;
                            int z49 = d5 + d1;
                            int z58 = MULTIPLY(d3 + z49, FIX_1_175875602);
                            int tmp111 = MULTIPLY(d5, FIX_2_053119869);
                            int tmp29 = MULTIPLY(d3, FIX_3_072711026);
                            int tmp38 = MULTIPLY(d1, FIX_1_501321110);
                            int z113 = MULTIPLY(-d1, FIX_0_899976223);
                            int z210 = MULTIPLY(-z29, FIX_2_562915447);
                            int z314 = MULTIPLY(-d3, FIX_1_961570560);
                            int z315 = z314 + z58;
                            int z410 = MULTIPLY(-z49, FIX_0_390180644) + z58;
                            tmp0 = z113 + z315;
                            tmp1 = tmp111 + z210 + z410;
                            tmp2 = tmp29 + z210 + z315;
                            tmp3 = tmp38 + z113 + z410;
                        } else {
                            int z211 = d5 + d3;
                            int z59 = MULTIPLY(z211, FIX_1_175875602);
                            int tmp112 = MULTIPLY(d5, FIX_1_662939225);
                            int z411 = MULTIPLY(-d5, FIX_0_390180644);
                            int z212 = MULTIPLY(-z211, FIX_1_387039845);
                            int tmp210 = MULTIPLY(d3, FIX_1_111140466);
                            int z316 = MULTIPLY(-d3, FIX_1_961570560);
                            tmp0 = z316 + z59;
                            tmp1 = tmp112 + z212;
                            tmp2 = tmp210 + z212;
                            tmp3 = z411 + z59;
                        }
                    } else if (d1 != 0) {
                        int z412 = d5 + d1;
                        int z510 = MULTIPLY(z412, FIX_1_175875602);
                        int z114 = MULTIPLY(-d1, FIX_0_899976223);
                        int tmp39 = MULTIPLY(d1, FIX_0_601344887);
                        int tmp113 = MULTIPLY(-d5, FIX_0_509795579);
                        int z213 = MULTIPLY(-d5, FIX_2_562915447);
                        int z413 = MULTIPLY(z412, FIX_0_785694958);
                        tmp0 = z114 + z510;
                        tmp1 = tmp113 + z413;
                        tmp2 = z213 + z510;
                        tmp3 = tmp39 + z413;
                    } else {
                        tmp0 = MULTIPLY(d5, FIX_1_175875602);
                        tmp1 = MULTIPLY(d5, FIX_0_275899380);
                        tmp2 = MULTIPLY(-d5, FIX_1_387039845);
                        tmp3 = MULTIPLY(d5, FIX_0_785694958);
                    }
                } else if (d3 != 0) {
                    if (d1 != 0) {
                        int z511 = d1 + d3;
                        int tmp310 = MULTIPLY(d1, FIX_0_211164243);
                        int tmp211 = MULTIPLY(-d3, FIX_1_451774981);
                        int z115 = MULTIPLY(d1, FIX_1_061594337);
                        int z214 = MULTIPLY(-d3, FIX_2_172734803);
                        int z414 = MULTIPLY(z511, FIX_0_785694958);
                        int z512 = MULTIPLY(z511, FIX_1_175875602);
                        tmp0 = z115 - z414;
                        tmp1 = z214 + z414;
                        tmp2 = tmp211 + z512;
                        tmp3 = tmp310 + z512;
                    } else {
                        tmp0 = MULTIPLY(-d3, FIX_0_785694958);
                        tmp1 = MULTIPLY(-d3, FIX_1_387039845);
                        tmp2 = MULTIPLY(-d3, FIX_0_275899380);
                        tmp3 = MULTIPLY(d3, FIX_1_175875602);
                    }
                } else if (d1 != 0) {
                    tmp0 = MULTIPLY(d1, FIX_0_275899380);
                    tmp1 = MULTIPLY(d1, FIX_0_785694958);
                    tmp2 = MULTIPLY(d1, FIX_1_175875602);
                    tmp3 = MULTIPLY(d1, FIX_1_387039845);
                } else {
                    tmp3 = 0;
                    tmp2 = 0;
                    tmp1 = 0;
                    tmp0 = 0;
                }
                dataptr.put(0, DESCALE11(tmp10 + tmp3));
                dataptr.put(7, DESCALE11(tmp10 - tmp3));
                dataptr.put(1, DESCALE11(tmp11 + tmp2));
                dataptr.put(6, DESCALE11(tmp11 - tmp2));
                dataptr.put(2, DESCALE11(tmp12 + tmp1));
                dataptr.put(5, DESCALE11(tmp12 - tmp1));
                dataptr.put(3, DESCALE11(tmp13 + tmp0));
                dataptr.put(4, DESCALE11(tmp13 - tmp0));
                dataptr = advance(dataptr, 8);
            }
        }
    }

    private static int MULTIPLY(int x, short y) {
        return ((short) x) * y;
    }

    private static final void pass2(ShortBuffer data) {
        int tmp13;
        int tmp10;
        int tmp12;
        int tmp11;
        int tmp3;
        int tmp2;
        int tmp1;
        int tmp0;
        ShortBuffer dataptr = data.duplicate();
        for (int rowctr = 7; rowctr >= 0; rowctr--) {
            int d0 = dataptr.get(0);
            int d1 = dataptr.get(8);
            int d2 = dataptr.get(16);
            int d3 = dataptr.get(24);
            int d4 = dataptr.get(32);
            int d5 = dataptr.get(40);
            int d6 = dataptr.get(48);
            int d7 = dataptr.get(56);
            if (d6 != 0) {
                if (d2 != 0) {
                    int z1 = MULTIPLY(d2 + d6, FIX_0_541196100);
                    int tmp22 = z1 + MULTIPLY(-d6, FIX_1_847759065);
                    int tmp32 = z1 + MULTIPLY(d2, FIX_0_765366865);
                    int tmp02 = (d0 + d4) << 13;
                    int tmp14 = (d0 - d4) << 13;
                    tmp10 = tmp02 + tmp32;
                    tmp13 = tmp02 - tmp32;
                    tmp11 = tmp14 + tmp22;
                    tmp12 = tmp14 - tmp22;
                } else {
                    int tmp23 = MULTIPLY(-d6, FIX_1_306562965);
                    int tmp33 = MULTIPLY(d6, FIX_0_541196100);
                    int tmp03 = (d0 + d4) << 13;
                    int tmp15 = (d0 - d4) << 13;
                    tmp10 = tmp03 + tmp33;
                    tmp13 = tmp03 - tmp33;
                    tmp11 = tmp15 + tmp23;
                    tmp12 = tmp15 - tmp23;
                }
            } else if (d2 != 0) {
                int tmp24 = MULTIPLY(d2, FIX_0_541196100);
                int tmp34 = MULTIPLY(d2, FIX_1_306562965);
                int tmp04 = (d0 + d4) << 13;
                int tmp16 = (d0 - d4) << 13;
                tmp10 = tmp04 + tmp34;
                tmp13 = tmp04 - tmp34;
                tmp11 = tmp16 + tmp24;
                tmp12 = tmp16 - tmp24;
            } else {
                tmp13 = (d0 + d4) << 13;
                tmp10 = tmp13;
                tmp12 = (d0 - d4) << 13;
                tmp11 = tmp12;
            }
            if (d7 != 0) {
                if (d5 != 0) {
                    if (d3 != 0) {
                        if (d1 != 0) {
                            int z12 = d7 + d1;
                            int z2 = d5 + d3;
                            int z3 = d7 + d3;
                            int z4 = d5 + d1;
                            int z5 = MULTIPLY(z3 + z4, FIX_1_175875602);
                            int tmp05 = MULTIPLY(d7, FIX_0_298631336);
                            int tmp17 = MULTIPLY(d5, FIX_2_053119869);
                            int tmp25 = MULTIPLY(d3, FIX_3_072711026);
                            int tmp35 = MULTIPLY(d1, FIX_1_501321110);
                            int z13 = MULTIPLY(-z12, FIX_0_899976223);
                            int z22 = MULTIPLY(-z2, FIX_2_562915447);
                            int z32 = MULTIPLY(-z3, FIX_1_961570560) + z5;
                            int z42 = MULTIPLY(-z4, FIX_0_390180644) + z5;
                            tmp0 = tmp05 + z13 + z32;
                            tmp1 = tmp17 + z22 + z42;
                            tmp2 = tmp25 + z22 + z32;
                            tmp3 = tmp35 + z13 + z42;
                        } else {
                            int z23 = d5 + d3;
                            int z33 = d7 + d3;
                            int z52 = MULTIPLY(z33 + d5, FIX_1_175875602);
                            int tmp06 = MULTIPLY(d7, FIX_0_298631336);
                            int tmp18 = MULTIPLY(d5, FIX_2_053119869);
                            int tmp26 = MULTIPLY(d3, FIX_3_072711026);
                            int z14 = MULTIPLY(-d7, FIX_0_899976223);
                            int z24 = MULTIPLY(-z23, FIX_2_562915447);
                            int z34 = MULTIPLY(-z33, FIX_1_961570560) + z52;
                            int z43 = MULTIPLY(-d5, FIX_0_390180644) + z52;
                            tmp0 = tmp06 + z14 + z34;
                            tmp1 = tmp18 + z24 + z43;
                            tmp2 = tmp26 + z24 + z34;
                            tmp3 = z14 + z43;
                        }
                    } else if (d1 != 0) {
                        int z15 = d7 + d1;
                        int z44 = d5 + d1;
                        int z53 = MULTIPLY(d7 + z44, FIX_1_175875602);
                        int tmp07 = MULTIPLY(d7, FIX_0_298631336);
                        int tmp19 = MULTIPLY(d5, FIX_2_053119869);
                        int tmp36 = MULTIPLY(d1, FIX_1_501321110);
                        int z16 = MULTIPLY(-z15, FIX_0_899976223);
                        int z25 = MULTIPLY(-d5, FIX_2_562915447);
                        int z35 = MULTIPLY(-d7, FIX_1_961570560);
                        int z36 = z35 + z53;
                        int z45 = MULTIPLY(-z44, FIX_0_390180644) + z53;
                        tmp0 = tmp07 + z16 + z36;
                        tmp1 = tmp19 + z25 + z45;
                        tmp2 = z25 + z36;
                        tmp3 = tmp36 + z16 + z45;
                    } else {
                        int tmp08 = MULTIPLY(-d7, FIX_0_601344887);
                        int z17 = MULTIPLY(-d7, FIX_0_899976223);
                        int z37 = MULTIPLY(-d7, FIX_1_961570560);
                        int tmp110 = MULTIPLY(-d5, FIX_0_509795579);
                        int z26 = MULTIPLY(-d5, FIX_2_562915447);
                        int z46 = MULTIPLY(-d5, FIX_0_390180644);
                        int z54 = MULTIPLY(d5 + d7, FIX_1_175875602);
                        int z38 = z37 + z54;
                        int z47 = z46 + z54;
                        tmp0 = tmp08 + z38;
                        tmp1 = tmp110 + z47;
                        tmp2 = z26 + z38;
                        tmp3 = z17 + z47;
                    }
                } else if (d3 != 0) {
                    if (d1 != 0) {
                        int z18 = d7 + d1;
                        int z39 = d7 + d3;
                        int z55 = MULTIPLY(z39 + d1, FIX_1_175875602);
                        int tmp09 = MULTIPLY(d7, FIX_0_298631336);
                        int tmp27 = MULTIPLY(d3, FIX_3_072711026);
                        int tmp37 = MULTIPLY(d1, FIX_1_501321110);
                        int z19 = MULTIPLY(-z18, FIX_0_899976223);
                        int z27 = MULTIPLY(-d3, FIX_2_562915447);
                        int z310 = MULTIPLY(-z39, FIX_1_961570560) + z55;
                        int z48 = MULTIPLY(-d1, FIX_0_390180644) + z55;
                        tmp0 = tmp09 + z19 + z310;
                        tmp1 = z27 + z48;
                        tmp2 = tmp27 + z27 + z310;
                        tmp3 = tmp37 + z19 + z48;
                    } else {
                        int z311 = d7 + d3;
                        int tmp010 = MULTIPLY(-d7, FIX_0_601344887);
                        int z110 = MULTIPLY(-d7, FIX_0_899976223);
                        int tmp28 = MULTIPLY(d3, FIX_0_509795579);
                        int z28 = MULTIPLY(-d3, FIX_2_562915447);
                        int z56 = MULTIPLY(z311, FIX_1_175875602);
                        int z312 = MULTIPLY(-z311, FIX_0_785694958);
                        tmp0 = tmp010 + z312;
                        tmp1 = z28 + z56;
                        tmp2 = tmp28 + z312;
                        tmp3 = z110 + z56;
                    }
                } else if (d1 != 0) {
                    int z111 = d7 + d1;
                    int z57 = MULTIPLY(z111, FIX_1_175875602);
                    int z112 = MULTIPLY(z111, FIX_0_275899380);
                    int z313 = MULTIPLY(-d7, FIX_1_961570560);
                    tmp0 = MULTIPLY(-d7, FIX_1_662939225) + z112;
                    tmp1 = MULTIPLY(-d1, FIX_0_390180644) + z57;
                    tmp2 = z313 + z57;
                    tmp3 = MULTIPLY(d1, FIX_1_111140466) + z112;
                } else {
                    tmp0 = MULTIPLY(-d7, FIX_1_387039845);
                    tmp1 = MULTIPLY(d7, FIX_1_175875602);
                    tmp2 = MULTIPLY(-d7, FIX_0_785694958);
                    tmp3 = MULTIPLY(d7, FIX_0_275899380);
                }
            } else if (d5 != 0) {
                if (d3 != 0) {
                    if (d1 != 0) {
                        int z29 = d5 + d3;
                        int z49 = d5 + d1;
                        int z58 = MULTIPLY(d3 + z49, FIX_1_175875602);
                        int tmp111 = MULTIPLY(d5, FIX_2_053119869);
                        int tmp29 = MULTIPLY(d3, FIX_3_072711026);
                        int tmp38 = MULTIPLY(d1, FIX_1_501321110);
                        int z113 = MULTIPLY(-d1, FIX_0_899976223);
                        int z210 = MULTIPLY(-z29, FIX_2_562915447);
                        int z314 = MULTIPLY(-d3, FIX_1_961570560);
                        int z315 = z314 + z58;
                        int z410 = MULTIPLY(-z49, FIX_0_390180644) + z58;
                        tmp0 = z113 + z315;
                        tmp1 = tmp111 + z210 + z410;
                        tmp2 = tmp29 + z210 + z315;
                        tmp3 = tmp38 + z113 + z410;
                    } else {
                        int z211 = d5 + d3;
                        int z59 = MULTIPLY(z211, FIX_1_175875602);
                        int tmp112 = MULTIPLY(d5, FIX_1_662939225);
                        int z411 = MULTIPLY(-d5, FIX_0_390180644);
                        int z212 = MULTIPLY(-z211, FIX_1_387039845);
                        int tmp210 = MULTIPLY(d3, FIX_1_111140466);
                        int z316 = MULTIPLY(-d3, FIX_1_961570560);
                        tmp0 = z316 + z59;
                        tmp1 = tmp112 + z212;
                        tmp2 = tmp210 + z212;
                        tmp3 = z411 + z59;
                    }
                } else if (d1 != 0) {
                    int z412 = d5 + d1;
                    int z510 = MULTIPLY(z412, FIX_1_175875602);
                    int z114 = MULTIPLY(-d1, FIX_0_899976223);
                    int tmp39 = MULTIPLY(d1, FIX_0_601344887);
                    int tmp113 = MULTIPLY(-d5, FIX_0_509795579);
                    int z213 = MULTIPLY(-d5, FIX_2_562915447);
                    int z413 = MULTIPLY(z412, FIX_0_785694958);
                    tmp0 = z114 + z510;
                    tmp1 = tmp113 + z413;
                    tmp2 = z213 + z510;
                    tmp3 = tmp39 + z413;
                } else {
                    tmp0 = MULTIPLY(d5, FIX_1_175875602);
                    tmp1 = MULTIPLY(d5, FIX_0_275899380);
                    tmp2 = MULTIPLY(-d5, FIX_1_387039845);
                    tmp3 = MULTIPLY(d5, FIX_0_785694958);
                }
            } else if (d3 != 0) {
                if (d1 != 0) {
                    int z511 = d1 + d3;
                    int tmp310 = MULTIPLY(d1, FIX_0_211164243);
                    int tmp211 = MULTIPLY(-d3, FIX_1_451774981);
                    int z115 = MULTIPLY(d1, FIX_1_061594337);
                    int z214 = MULTIPLY(-d3, FIX_2_172734803);
                    int z414 = MULTIPLY(z511, FIX_0_785694958);
                    int z512 = MULTIPLY(z511, FIX_1_175875602);
                    tmp0 = z115 - z414;
                    tmp1 = z214 + z414;
                    tmp2 = tmp211 + z512;
                    tmp3 = tmp310 + z512;
                } else {
                    tmp0 = MULTIPLY(-d3, FIX_0_785694958);
                    tmp1 = MULTIPLY(-d3, FIX_1_387039845);
                    tmp2 = MULTIPLY(-d3, FIX_0_275899380);
                    tmp3 = MULTIPLY(d3, FIX_1_175875602);
                }
            } else if (d1 != 0) {
                tmp0 = MULTIPLY(d1, FIX_0_275899380);
                tmp1 = MULTIPLY(d1, FIX_0_785694958);
                tmp2 = MULTIPLY(d1, FIX_1_175875602);
                tmp3 = MULTIPLY(d1, FIX_1_387039845);
            } else {
                tmp3 = 0;
                tmp2 = 0;
                tmp1 = 0;
                tmp0 = 0;
            }
            dataptr.put(0, DESCALE18(tmp10 + tmp3));
            dataptr.put(56, DESCALE18(tmp10 - tmp3));
            dataptr.put(8, DESCALE18(tmp11 + tmp2));
            dataptr.put(48, DESCALE18(tmp11 - tmp2));
            dataptr.put(16, DESCALE18(tmp12 + tmp1));
            dataptr.put(40, DESCALE18(tmp12 - tmp1));
            dataptr.put(24, DESCALE18(tmp13 + tmp0));
            dataptr.put(32, DESCALE18(tmp13 - tmp0));
            dataptr = advance(dataptr, 1);
        }
    }

    private static final int DESCALE(int x, int n) {
        return ((1 << (n - 1)) + x) >> n;
    }

    private static final short DESCALE11(int x) {
        return (short) ((x + 1024) >> 11);
    }

    private static final short DESCALE18(int x) {
        return (short) ((131072 + x) >> 18);
    }
}
