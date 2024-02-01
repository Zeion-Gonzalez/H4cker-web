package org.jcodec.common.dct;

import java.nio.IntBuffer;

/* loaded from: classes.dex */
public class IntDCT extends DCT {
    private static final int CENTERJSAMPLE = 128;
    private static final int CONST_BITS = 13;
    private static final int DCTSIZE = 8;
    private static final int FIX_0_298631336;
    private static final int FIX_0_390180644;
    private static final int FIX_0_541196100;
    private static final int FIX_0_765366865;
    private static final int FIX_0_899976223;
    private static final int FIX_1_175875602;
    private static final int FIX_1_501321110;
    private static final int FIX_1_847759065;
    private static final int FIX_1_961570560;
    private static final int FIX_2_053119869;
    private static final int FIX_2_562915447;
    private static final int FIX_3_072711026;
    private static final int MAXJSAMPLE = 255;
    private static final int ONE_HALF = 4096;
    private static final int PASS1_BITS = 2;
    private static final int RANGE_MASK = 1023;
    public static final IntDCT INSTANCE = new IntDCT();
    private static final IntBuffer sample_range_limit = IntBuffer.allocate(1408);
    private static final IntBuffer idct_sample_range_limit = IntBuffer.allocate(sample_range_limit.capacity() - 128);

    static {
        prepare_range_limit_table();
        FIX_0_298631336 = FIX(0.298631336d);
        FIX_0_390180644 = FIX(0.390180644d);
        FIX_0_541196100 = FIX(0.5411961d);
        FIX_0_765366865 = FIX(0.765366865d);
        FIX_0_899976223 = FIX(0.899976223d);
        FIX_1_175875602 = FIX(1.175875602d);
        FIX_1_501321110 = FIX(1.50132111d);
        FIX_1_847759065 = FIX(1.847759065d);
        FIX_1_961570560 = FIX(1.96157056d);
        FIX_2_053119869 = FIX(2.053119869d);
        FIX_2_562915447 = FIX(2.562915447d);
        FIX_3_072711026 = FIX(3.072711026d);
    }

    @Override // org.jcodec.common.dct.DCT
    public int[] decode(int[] orig) {
        IntBuffer inptr = IntBuffer.wrap(orig);
        IntBuffer workspace = IntBuffer.allocate(64);
        IntBuffer outptr = IntBuffer.allocate(64);
        decode(inptr, workspace, outptr);
        return outptr.array();
    }

    protected IntBuffer decode(IntBuffer inptr, IntBuffer workspace, IntBuffer outptr) {
        pass1(inptr, workspace.duplicate());
        pass2(outptr, workspace.duplicate());
        return outptr;
    }

    private static void pass2(IntBuffer outptr, IntBuffer wsptr) {
        for (int ctr = 0; ctr < 8; ctr++) {
            int z2 = wsptr.get(2);
            int z3 = wsptr.get(6);
            int z1 = MULTIPLY(z2 + z3, FIX_0_541196100);
            int tmp2 = z1 + MULTIPLY(z3, -FIX_1_847759065);
            int tmp3 = z1 + MULTIPLY(z2, FIX_0_765366865);
            int tmp0 = (wsptr.get(0) + wsptr.get(4)) << 13;
            int tmp1 = (wsptr.get(0) - wsptr.get(4)) << 13;
            int tmp10 = tmp0 + tmp3;
            int tmp13 = tmp0 - tmp3;
            int tmp11 = tmp1 + tmp2;
            int tmp12 = tmp1 - tmp2;
            int tmp02 = wsptr.get(7);
            int tmp14 = wsptr.get(5);
            int tmp22 = wsptr.get(3);
            int tmp32 = wsptr.get(1);
            int z12 = tmp02 + tmp32;
            int z22 = tmp14 + tmp22;
            int z32 = tmp02 + tmp22;
            int z4 = tmp14 + tmp32;
            int z5 = MULTIPLY(z32 + z4, FIX_1_175875602);
            int tmp03 = MULTIPLY(tmp02, FIX_0_298631336);
            int tmp15 = MULTIPLY(tmp14, FIX_2_053119869);
            int tmp23 = MULTIPLY(tmp22, FIX_3_072711026);
            int tmp33 = MULTIPLY(tmp32, FIX_1_501321110);
            int z13 = MULTIPLY(z12, -FIX_0_899976223);
            int z23 = MULTIPLY(z22, -FIX_2_562915447);
            int z33 = MULTIPLY(z32, -FIX_1_961570560) + z5;
            int z42 = MULTIPLY(z4, -FIX_0_390180644) + z5;
            int tmp04 = tmp03 + z13 + z33;
            int tmp16 = tmp15 + z23 + z42;
            int tmp24 = tmp23 + z23 + z33;
            int tmp34 = tmp33 + z13 + z42;
            outptr.put(range_limit(DESCALE(tmp10 + tmp34, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp11 + tmp24, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp12 + tmp16, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp13 + tmp04, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp13 - tmp04, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp12 - tmp16, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp11 - tmp24, 18) & RANGE_MASK));
            outptr.put(range_limit(DESCALE(tmp10 - tmp34, 18) & RANGE_MASK));
            wsptr = advance(wsptr, 8);
        }
    }

    public static int range_limit(int i) {
        return idct_sample_range_limit.get(i + 256);
    }

    private static void prepare_range_limit_table() {
        sample_range_limit.position(256);
        for (int i = 0; i < 128; i++) {
            sample_range_limit.put(i);
        }
        for (int i2 = -128; i2 < 0; i2++) {
            sample_range_limit.put(i2);
        }
        for (int i3 = 0; i3 < 384; i3++) {
            sample_range_limit.put(-1);
        }
        for (int i4 = 0; i4 < 384; i4++) {
            sample_range_limit.put(0);
        }
        for (int i5 = 0; i5 < 128; i5++) {
            sample_range_limit.put(i5);
        }
        for (int i6 = 0; i6 < idct_sample_range_limit.capacity(); i6++) {
            idct_sample_range_limit.put(sample_range_limit.get(i6 + 128) & 255);
        }
    }

    private static boolean shortcut(IntBuffer inptr, IntBuffer wsptr) {
        if (inptr.get(8) != 0 || inptr.get(16) != 0 || inptr.get(24) != 0 || inptr.get(32) != 0 || inptr.get(40) != 0 || inptr.get(48) != 0 || inptr.get(56) != 0) {
            return false;
        }
        int dcval = inptr.get(0) << 2;
        wsptr.put(0, dcval);
        wsptr.put(8, dcval);
        wsptr.put(16, dcval);
        wsptr.put(24, dcval);
        wsptr.put(32, dcval);
        wsptr.put(40, dcval);
        wsptr.put(48, dcval);
        wsptr.put(56, dcval);
        advance(inptr);
        advance(wsptr);
        return true;
    }

    private static void pass1(IntBuffer inptr, IntBuffer wsptr) {
        for (int ctr = 8; ctr > 0; ctr--) {
            int z2 = inptr.get(16);
            int z3 = inptr.get(48);
            int z1 = MULTIPLY(z2 + z3, FIX_0_541196100);
            int tmp2 = z1 + MULTIPLY(z3, -FIX_1_847759065);
            int tmp3 = z1 + MULTIPLY(z2, FIX_0_765366865);
            int z22 = inptr.get(0);
            int z32 = inptr.get(32);
            int tmp0 = (z22 + z32) << 13;
            int tmp1 = (z22 - z32) << 13;
            int tmp10 = tmp0 + tmp3;
            int tmp13 = tmp0 - tmp3;
            int tmp11 = tmp1 + tmp2;
            int tmp12 = tmp1 - tmp2;
            int tmp02 = inptr.get(56);
            int tmp14 = inptr.get(40);
            int tmp22 = inptr.get(24);
            int tmp32 = inptr.get(8);
            int z12 = tmp02 + tmp32;
            int z23 = tmp14 + tmp22;
            int z33 = tmp02 + tmp22;
            int z4 = tmp14 + tmp32;
            int z5 = MULTIPLY(z33 + z4, FIX_1_175875602);
            int tmp03 = MULTIPLY(tmp02, FIX_0_298631336);
            int tmp15 = MULTIPLY(tmp14, FIX_2_053119869);
            int tmp23 = MULTIPLY(tmp22, FIX_3_072711026);
            int tmp33 = MULTIPLY(tmp32, FIX_1_501321110);
            int z13 = MULTIPLY(z12, -FIX_0_899976223);
            int z24 = MULTIPLY(z23, -FIX_2_562915447);
            int z34 = MULTIPLY(z33, -FIX_1_961570560) + z5;
            int z42 = MULTIPLY(z4, -FIX_0_390180644) + z5;
            int tmp04 = tmp03 + z13 + z34;
            int tmp16 = tmp15 + z24 + z42;
            int tmp24 = tmp23 + z24 + z34;
            int tmp34 = tmp33 + z13 + z42;
            wsptr.put(0, DESCALE(tmp10 + tmp34, 11));
            wsptr.put(56, DESCALE(tmp10 - tmp34, 11));
            wsptr.put(8, DESCALE(tmp11 + tmp24, 11));
            wsptr.put(48, DESCALE(tmp11 - tmp24, 11));
            wsptr.put(16, DESCALE(tmp12 + tmp16, 11));
            wsptr.put(40, DESCALE(tmp12 - tmp16, 11));
            wsptr.put(24, DESCALE(tmp13 + tmp04, 11));
            wsptr.put(32, DESCALE(tmp13 - tmp04, 11));
            inptr = advance(inptr);
            wsptr = advance(wsptr);
        }
    }

    private static IntBuffer advance(IntBuffer ptr) {
        return advance(ptr, 1);
    }

    private static IntBuffer advance(IntBuffer ptr, int size) {
        return ((IntBuffer) ptr.position(ptr.position() + size)).slice();
    }

    static int DESCALE(int x, int n) {
        return RIGHT_SHIFT((1 << (n - 1)) + x, n);
    }

    private static int RIGHT_SHIFT(int x, int shft) {
        return x >> shft;
    }

    private static final int MULTIPLY(int i, int j) {
        return i * j;
    }

    private static final int FIX(double x) {
        return (int) ((8192.0d * x) + 0.5d);
    }
}
