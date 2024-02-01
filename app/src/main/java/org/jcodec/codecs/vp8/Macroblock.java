package org.jcodec.codecs.vp8;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.jcodec.codecs.vp8.VP8Util;

/* loaded from: classes.dex */
public class Macroblock {
    public final int Rrow;
    public int chromaMode;
    public final int column;
    public int filterLevel;
    public int lumaMode;
    public int skipCoeff;
    boolean skipFilter;
    public final Subblock[][] ySubblocks = (Subblock[][]) Array.newInstance(Subblock.class, 4, 4);

    /* renamed from: y2 */
    public final Subblock f1486y2 = new Subblock(0, 0, VP8Util.PLANE.Y2);
    public final Subblock[][] uSubblocks = (Subblock[][]) Array.newInstance(Subblock.class, 2, 2);
    public final Subblock[][] vSubblocks = (Subblock[][]) Array.newInstance(Subblock.class, 2, 2);
    public int segment = 0;
    public boolean debug = true;

    public Macroblock(int y, int x) {
        this.Rrow = y;
        this.column = x;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.ySubblocks[row][col] = new Subblock(row, col, VP8Util.PLANE.Y1);
            }
        }
        for (int row2 = 0; row2 < 2; row2++) {
            for (int col2 = 0; col2 < 2; col2++) {
                this.uSubblocks[row2][col2] = new Subblock(row2, col2, VP8Util.PLANE.U);
                this.vSubblocks[row2][col2] = new Subblock(row2, col2, VP8Util.PLANE.V);
            }
        }
    }

    public void dequantMacroBlock(Macroblock[][] mbs, VP8Util.QuantizationParams p) {
        if (this.lumaMode != 4) {
            int acQValue = p.y2AC;
            int dcQValue = p.y2DC;
            int[] input = new int[16];
            input[0] = this.f1486y2.tokens[0] * dcQValue;
            for (int x = 1; x < 16; x++) {
                input[x] = this.f1486y2.tokens[x] * acQValue;
            }
            this.f1486y2.residue = VP8DCT.decodeWHT(input);
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    this.ySubblocks[row][col].dequantSubblock(p.yDC, p.yAC, Integer.valueOf(this.f1486y2.residue[(row * 4) + col]));
                }
            }
            predictY(mbs);
            predictUV(mbs);
            for (int row2 = 0; row2 < 2; row2++) {
                for (int col2 = 0; col2 < 2; col2++) {
                    this.uSubblocks[row2][col2].dequantSubblock(p.chromaDC, p.chromaAC, null);
                    this.vSubblocks[row2][col2].dequantSubblock(p.chromaDC, p.chromaAC, null);
                }
            }
            reconstruct();
            return;
        }
        for (int row3 = 0; row3 < 4; row3++) {
            for (int col3 = 0; col3 < 4; col3++) {
                Subblock sb = this.ySubblocks[row3][col3];
                sb.dequantSubblock(p.yDC, p.yAC, null);
                sb.predict(mbs);
                sb.reconstruct();
            }
        }
        predictUV(mbs);
        for (int row4 = 0; row4 < 2; row4++) {
            for (int col4 = 0; col4 < 2; col4++) {
                Subblock sb2 = this.uSubblocks[row4][col4];
                sb2.dequantSubblock(p.chromaDC, p.chromaAC, null);
                sb2.reconstruct();
            }
        }
        for (int row5 = 0; row5 < 2; row5++) {
            for (int col5 = 0; col5 < 2; col5++) {
                Subblock sb3 = this.vSubblocks[row5][col5];
                sb3.dequantSubblock(p.chromaDC, p.chromaAC, null);
                sb3.reconstruct();
            }
        }
    }

    public void reconstruct() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.ySubblocks[row][col].reconstruct();
            }
        }
        for (int row2 = 0; row2 < 2; row2++) {
            for (int col2 = 0; col2 < 2; col2++) {
                this.uSubblocks[row2][col2].reconstruct();
            }
        }
        for (int row3 = 0; row3 < 2; row3++) {
            for (int col3 = 0; col3 < 2; col3++) {
                this.vSubblocks[row3][col3].reconstruct();
            }
        }
    }

    public void predictUV(Macroblock[][] mbs) {
        Macroblock aboveMb = mbs[this.Rrow - 1][this.column];
        Macroblock leftMb = mbs[this.Rrow][this.column - 1];
        switch (this.chromaMode) {
            case 0:
                boolean up_available = false;
                boolean left_available = false;
                int uAvg = 0;
                int vAvg = 0;
                int expected_udc = 128;
                int expected_vdc = 128;
                if (this.column > 1) {
                    left_available = true;
                }
                if (this.Rrow > 1) {
                    up_available = true;
                }
                if (up_available || left_available) {
                    if (up_available) {
                        for (int j = 0; j < 2; j++) {
                            Subblock usb = aboveMb.uSubblocks[1][j];
                            Subblock vsb = aboveMb.vSubblocks[1][j];
                            for (int i = 0; i < 4; i++) {
                                uAvg += usb.val[i + 12];
                                vAvg += vsb.val[i + 12];
                            }
                        }
                    }
                    if (left_available) {
                        for (int j2 = 0; j2 < 2; j2++) {
                            Subblock usb2 = leftMb.uSubblocks[j2][1];
                            Subblock vsb2 = leftMb.vSubblocks[j2][1];
                            for (int i2 = 0; i2 < 4; i2++) {
                                uAvg += usb2.val[(i2 * 4) + 3];
                                vAvg += vsb2.val[(i2 * 4) + 3];
                            }
                        }
                    }
                    int shift = 2;
                    if (up_available) {
                        shift = 2 + 1;
                    }
                    if (left_available) {
                        shift++;
                    }
                    expected_udc = ((1 << (shift - 1)) + uAvg) >> shift;
                    expected_vdc = ((1 << (shift - 1)) + vAvg) >> shift;
                }
                int[] ufill = new int[16];
                for (int aRow = 0; aRow < 4; aRow++) {
                    for (int aCol = 0; aCol < 4; aCol++) {
                        ufill[(aRow * 4) + aCol] = expected_udc;
                    }
                }
                int[] vfill = new int[16];
                for (int aRow2 = 0; aRow2 < 4; aRow2++) {
                    for (int aCol2 = 0; aCol2 < 4; aCol2++) {
                        vfill[(aRow2 * 4) + aCol2] = expected_vdc;
                    }
                }
                for (int aRow3 = 0; aRow3 < 2; aRow3++) {
                    for (int aCol3 = 0; aCol3 < 2; aCol3++) {
                        Subblock usb3 = this.uSubblocks[aRow3][aCol3];
                        Subblock vsb3 = this.vSubblocks[aRow3][aCol3];
                        usb3.predict = ufill;
                        vsb3.predict = vfill;
                    }
                }
                return;
            case 1:
                Subblock[] aboveUSb = new Subblock[2];
                Subblock[] aboveVSb = new Subblock[2];
                for (int aCol4 = 0; aCol4 < 2; aCol4++) {
                    aboveUSb[aCol4] = aboveMb.uSubblocks[1][aCol4];
                    aboveVSb[aCol4] = aboveMb.vSubblocks[1][aCol4];
                }
                for (int aRow4 = 0; aRow4 < 2; aRow4++) {
                    for (int aCol5 = 0; aCol5 < 2; aCol5++) {
                        Subblock usb4 = this.uSubblocks[aRow4][aCol5];
                        Subblock vsb4 = this.vSubblocks[aRow4][aCol5];
                        int[] ublock = new int[16];
                        int[] vblock = new int[16];
                        for (int pRow = 0; pRow < 4; pRow++) {
                            for (int pCol = 0; pCol < 4; pCol++) {
                                ublock[(pRow * 4) + pCol] = aboveUSb[aCol5].val != null ? aboveUSb[aCol5].val[pCol + 12] : 127;
                                vblock[(pRow * 4) + pCol] = aboveVSb[aCol5].val != null ? aboveVSb[aCol5].val[pCol + 12] : 127;
                            }
                        }
                        usb4.predict = ublock;
                        vsb4.predict = vblock;
                    }
                }
                return;
            case 2:
                Subblock[] leftUSb = new Subblock[2];
                Subblock[] leftVSb = new Subblock[2];
                for (int aCol6 = 0; aCol6 < 2; aCol6++) {
                    leftUSb[aCol6] = leftMb.uSubblocks[aCol6][1];
                    leftVSb[aCol6] = leftMb.vSubblocks[aCol6][1];
                }
                for (int aRow5 = 0; aRow5 < 2; aRow5++) {
                    for (int aCol7 = 0; aCol7 < 2; aCol7++) {
                        Subblock usb5 = this.uSubblocks[aRow5][aCol7];
                        Subblock vsb5 = this.vSubblocks[aRow5][aCol7];
                        int[] ublock2 = new int[16];
                        int[] vblock2 = new int[16];
                        for (int pRow2 = 0; pRow2 < 4; pRow2++) {
                            for (int pCol2 = 0; pCol2 < 4; pCol2++) {
                                ublock2[(pRow2 * 4) + pCol2] = leftUSb[aRow5].val != null ? leftUSb[aRow5].val[(pRow2 * 4) + 3] : 129;
                                vblock2[(pRow2 * 4) + pCol2] = leftVSb[aRow5].val != null ? leftVSb[aRow5].val[(pRow2 * 4) + 3] : 129;
                            }
                        }
                        usb5.predict = ublock2;
                        vsb5.predict = vblock2;
                    }
                }
                return;
            case 3:
                Macroblock ALMb = mbs[this.Rrow - 1][this.column - 1];
                Subblock ALUSb = ALMb.uSubblocks[1][1];
                int alu = ALUSb.val[15];
                Subblock ALVSb = ALMb.vSubblocks[1][1];
                int alv = ALVSb.val[15];
                Subblock[] aboveUSb2 = new Subblock[2];
                Subblock[] leftUSb2 = new Subblock[2];
                Subblock[] aboveVSb2 = new Subblock[2];
                Subblock[] leftVSb2 = new Subblock[2];
                for (int x = 0; x < 2; x++) {
                    aboveUSb2[x] = aboveMb.uSubblocks[1][x];
                    leftUSb2[x] = leftMb.uSubblocks[x][1];
                    aboveVSb2[x] = aboveMb.vSubblocks[1][x];
                    leftVSb2[x] = leftMb.vSubblocks[x][1];
                }
                for (int sbRow = 0; sbRow < 2; sbRow++) {
                    for (int pRow3 = 0; pRow3 < 4; pRow3++) {
                        for (int sbCol = 0; sbCol < 2; sbCol++) {
                            if (this.uSubblocks[sbRow][sbCol].val == null) {
                                this.uSubblocks[sbRow][sbCol].val = new int[16];
                            }
                            if (this.vSubblocks[sbRow][sbCol].val == null) {
                                this.vSubblocks[sbRow][sbCol].val = new int[16];
                            }
                            for (int pCol3 = 0; pCol3 < 4; pCol3++) {
                                int upred = (leftUSb2[sbRow].val[(pRow3 * 4) + 3] + aboveUSb2[sbCol].val[pCol3 + 12]) - alu;
                                this.uSubblocks[sbRow][sbCol].val[(pRow3 * 4) + pCol3] = VP8Util.QuantizationParams.clip255(upred);
                                int vpred = (leftVSb2[sbRow].val[(pRow3 * 4) + 3] + aboveVSb2[sbCol].val[pCol3 + 12]) - alv;
                                this.vSubblocks[sbRow][sbCol].val[(pRow3 * 4) + pCol3] = VP8Util.QuantizationParams.clip255(vpred);
                            }
                        }
                    }
                }
                return;
            default:
                System.err.println("TODO predict_mb_uv: " + this.lumaMode);
                System.exit(0);
                return;
        }
    }

    private void predictY(Macroblock[][] mbs) {
        Macroblock aboveMb = mbs[this.Rrow - 1][this.column];
        Macroblock leftMb = mbs[this.Rrow][this.column - 1];
        switch (this.lumaMode) {
            case 0:
                predictLumaDC(aboveMb, leftMb);
                return;
            case 1:
                predictLumaV(aboveMb);
                return;
            case 2:
                predictLumaH(leftMb);
                return;
            case 3:
                Macroblock upperLeft = mbs[this.Rrow - 1][this.column - 1];
                Subblock ALSb = upperLeft.ySubblocks[3][3];
                int aboveLeft = ALSb.val[15];
                predictLumaTM(aboveMb, leftMb, aboveLeft);
                return;
            default:
                System.err.println("TODO predict_mb_y: " + this.lumaMode);
                System.exit(0);
                return;
        }
    }

    private void predictLumaDC(Macroblock above, Macroblock left) {
        boolean hasAbove = this.Rrow > 1;
        boolean hasLeft = this.column > 1;
        int expected_dc = 128;
        if (hasAbove || hasLeft) {
            int average = 0;
            if (hasAbove) {
                for (int j = 0; j < 4; j++) {
                    Subblock sb = above.ySubblocks[3][j];
                    for (int i = 0; i < 4; i++) {
                        average += sb.val[i + 12];
                    }
                }
            }
            if (hasLeft) {
                for (int j2 = 0; j2 < 4; j2++) {
                    Subblock sb2 = left.ySubblocks[j2][3];
                    for (int i2 = 0; i2 < 4; i2++) {
                        average += sb2.val[(i2 * 4) + 3];
                    }
                }
            }
            int shift = 3;
            if (hasAbove) {
                shift = 3 + 1;
            }
            if (hasLeft) {
                shift++;
            }
            expected_dc = ((1 << (shift - 1)) + average) >> shift;
        }
        int[] fill = new int[16];
        for (int i3 = 0; i3 < 16; i3++) {
            fill[i3] = expected_dc;
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                this.ySubblocks[y][x].predict = fill;
            }
        }
    }

    private void predictLumaH(Macroblock leftMb) {
        Subblock[] leftYSb = new Subblock[4];
        for (int row = 0; row < 4; row++) {
            leftYSb[row] = leftMb.ySubblocks[row][3];
        }
        for (int row2 = 0; row2 < 4; row2++) {
            for (int col = 0; col < 4; col++) {
                Subblock sb = this.ySubblocks[row2][col];
                int[] block = new int[16];
                for (int bRow = 0; bRow < 4; bRow++) {
                    for (int bCol = 0; bCol < 4; bCol++) {
                        block[(bRow * 4) + bCol] = leftYSb[row2].val != null ? leftYSb[row2].val[(bRow * 4) + 3] : 129;
                    }
                }
                sb.predict = block;
            }
        }
    }

    private void predictLumaTM(Macroblock above, Macroblock left, int aboveLeft) {
        Subblock[] aboveYSb = new Subblock[4];
        Subblock[] leftYSb = new Subblock[4];
        for (int col = 0; col < 4; col++) {
            aboveYSb[col] = above.ySubblocks[3][col];
        }
        for (int row = 0; row < 4; row++) {
            leftYSb[row] = left.ySubblocks[row][3];
        }
        for (int row2 = 0; row2 < 4; row2++) {
            for (int pRow = 0; pRow < 4; pRow++) {
                for (int col2 = 0; col2 < 4; col2++) {
                    if (this.ySubblocks[row2][col2].val == null) {
                        this.ySubblocks[row2][col2].val = new int[16];
                    }
                    for (int pCol = 0; pCol < 4; pCol++) {
                        int pred = (leftYSb[row2].val[(pRow * 4) + 3] + aboveYSb[col2].val[pCol + 12]) - aboveLeft;
                        this.ySubblocks[row2][col2].val[(pRow * 4) + pCol] = VP8Util.QuantizationParams.clip255(pred);
                    }
                }
            }
        }
    }

    private void predictLumaV(Macroblock above) {
        Subblock[] aboveYSb = new Subblock[4];
        for (int col = 0; col < 4; col++) {
            aboveYSb[col] = above.ySubblocks[3][col];
        }
        for (int row = 0; row < 4; row++) {
            for (int col2 = 0; col2 < 4; col2++) {
                Subblock sb = this.ySubblocks[row][col2];
                int[] block = new int[16];
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++) {
                        block[(j * 4) + i] = aboveYSb[col2].val != null ? aboveYSb[col2].val[i + 12] : 127;
                    }
                }
                sb.predict = block;
            }
        }
    }

    public Subblock getBottomSubblock(int x, VP8Util.PLANE plane) {
        if (plane == VP8Util.PLANE.Y1) {
            return this.ySubblocks[3][x];
        }
        if (plane == VP8Util.PLANE.U) {
            return this.uSubblocks[1][x];
        }
        if (plane == VP8Util.PLANE.V) {
            return this.vSubblocks[1][x];
        }
        if (plane == VP8Util.PLANE.Y2) {
            return this.f1486y2;
        }
        return null;
    }

    public Subblock getRightSubBlock(int y, VP8Util.PLANE plane) {
        if (plane == VP8Util.PLANE.Y1) {
            return this.ySubblocks[y][3];
        }
        if (plane == VP8Util.PLANE.U) {
            return this.uSubblocks[y][1];
        }
        if (plane == VP8Util.PLANE.V) {
            return this.vSubblocks[y][1];
        }
        if (plane == VP8Util.PLANE.Y2) {
            return this.f1486y2;
        }
        return null;
    }

    public void decodeMacroBlock(Macroblock[][] mbs, BooleanArithmeticDecoder tockenDecoder, int[][][][] coefProbs) {
        if (this.skipCoeff > 0) {
            this.skipFilter = this.lumaMode != 4;
        } else if (this.lumaMode != 4) {
            decodeMacroBlockTokens(true, mbs, tockenDecoder, coefProbs);
        } else {
            decodeMacroBlockTokens(false, mbs, tockenDecoder, coefProbs);
        }
    }

    private void decodeMacroBlockTokens(boolean withY2, Macroblock[][] mbs, BooleanArithmeticDecoder decoder, int[][][][] coefProbs) {
        this.skipFilter = false;
        if (withY2) {
            this.skipFilter = decodePlaneTokens(1, VP8Util.PLANE.Y2, false, mbs, decoder, coefProbs) | this.skipFilter;
        }
        this.skipFilter = decodePlaneTokens(4, VP8Util.PLANE.Y1, withY2, mbs, decoder, coefProbs) | this.skipFilter;
        this.skipFilter = decodePlaneTokens(2, VP8Util.PLANE.U, false, mbs, decoder, coefProbs) | this.skipFilter;
        this.skipFilter = decodePlaneTokens(2, VP8Util.PLANE.V, false, mbs, decoder, coefProbs) | this.skipFilter;
        this.skipFilter = !this.skipFilter;
    }

    private boolean decodePlaneTokens(int dimentions, VP8Util.PLANE plane, boolean withY2, Macroblock[][] mbs, BooleanArithmeticDecoder decoder, int[][][][] coefProbs) {
        boolean r = false;
        for (int row = 0; row < dimentions; row++) {
            for (int col = 0; col < dimentions; col++) {
                Subblock sb = null;
                if (VP8Util.PLANE.Y1.equals(plane)) {
                    sb = this.ySubblocks[row][col];
                } else if (VP8Util.PLANE.U.equals(plane)) {
                    sb = this.uSubblocks[row][col];
                } else if (VP8Util.PLANE.V.equals(plane)) {
                    sb = this.vSubblocks[row][col];
                } else if (VP8Util.PLANE.Y2.equals(plane)) {
                    sb = this.f1486y2;
                }
                Subblock l = sb.getLeft(plane, mbs);
                Subblock a = sb.getAbove(plane, mbs);
                int lc = (l.someValuePresent ? 1 : 0) + (a.someValuePresent ? 1 : 0);
                sb.decodeSubBlock(decoder, coefProbs, lc, VP8Util.planeToType(plane, Boolean.valueOf(withY2)), withY2);
                r |= sb.someValuePresent;
            }
        }
        return r;
    }

    /* loaded from: classes.dex */
    public class Subblock {
        private int col;
        public int mode;
        private VP8Util.PLANE plane;
        public int[] predict;
        public int[] residue;
        private int row;
        public boolean someValuePresent;
        private int[] tokens = new int[16];
        public int[] val;

        public Subblock(int row, int col, VP8Util.PLANE plane) {
            this.row = row;
            this.col = col;
            this.plane = plane;
        }

        public void predict(Macroblock[][] mbs) {
            int aboveLeft;
            Subblock aboveSb = getAbove(this.plane, mbs);
            Subblock leftSb = getLeft(this.plane, mbs);
            int[] above = new int[4];
            int[] left = new int[4];
            int[] aboveValues = aboveSb.val != null ? aboveSb.val : VP8Util.PRED_BLOCK_127;
            above[0] = aboveValues[12];
            above[1] = aboveValues[13];
            above[2] = aboveValues[14];
            above[3] = aboveValues[15];
            int[] leftValues = leftSb.val != null ? leftSb.val : VP8Util.pickDefaultPrediction(this.mode);
            left[0] = leftValues[3];
            left[1] = leftValues[7];
            left[2] = leftValues[11];
            left[3] = leftValues[15];
            Subblock aboveLeftSb = aboveSb.getLeft(this.plane, mbs);
            if (leftSb.val == null && aboveSb.val == null) {
                aboveLeft = 127;
            } else if (aboveSb.val == null) {
                aboveLeft = 127;
            } else {
                aboveLeft = aboveLeftSb.val != null ? aboveLeftSb.val[15] : VP8Util.pickDefaultPrediction(this.mode)[15];
            }
            int[] ar = getAboveRightLowestRow(mbs);
            switch (this.mode) {
                case 0:
                    this.predict = VP8Util.predictDC(above, left);
                    return;
                case 1:
                    this.predict = VP8Util.predictTM(above, left, aboveLeft);
                    return;
                case 2:
                    this.predict = VP8Util.predictVE(above, aboveLeft, ar);
                    return;
                case 3:
                    this.predict = VP8Util.predictHE(left, aboveLeft);
                    return;
                case 4:
                    this.predict = VP8Util.predictLD(above, ar);
                    return;
                case 5:
                    this.predict = VP8Util.predictRD(above, left, aboveLeft);
                    return;
                case 6:
                    this.predict = VP8Util.predictVR(above, left, aboveLeft);
                    return;
                case 7:
                    this.predict = VP8Util.predictVL(above, ar);
                    return;
                case 8:
                    this.predict = VP8Util.predictHD(above, left, aboveLeft);
                    return;
                case 9:
                    this.predict = VP8Util.predictHU(left);
                    return;
                default:
                    throw new UnsupportedOperationException("TODO: unknowwn mode: " + this.mode);
            }
        }

        public void reconstruct() {
            int[] p = this.val != null ? this.val : this.predict;
            int[] dest = new int[16];
            for (int aRow = 0; aRow < 4; aRow++) {
                for (int aCol = 0; aCol < 4; aCol++) {
                    int a = VP8Util.QuantizationParams.clip255(this.residue[(aRow * 4) + aCol] + p[(aRow * 4) + aCol]);
                    dest[(aRow * 4) + aCol] = a;
                }
            }
            this.val = dest;
        }

        public Subblock getAbove(VP8Util.PLANE plane, Macroblock[][] mbs) {
            if (this.row > 0) {
                if (VP8Util.PLANE.Y1.equals(this.plane)) {
                    return Macroblock.this.ySubblocks[this.row - 1][this.col];
                }
                if (VP8Util.PLANE.U.equals(this.plane)) {
                    return Macroblock.this.uSubblocks[this.row - 1][this.col];
                }
                if (VP8Util.PLANE.V.equals(this.plane)) {
                    return Macroblock.this.vSubblocks[this.row - 1][this.col];
                }
            }
            int x = this.col;
            Macroblock mb2 = mbs[Macroblock.this.Rrow - 1][Macroblock.this.column];
            if (plane == VP8Util.PLANE.Y2) {
                while (mb2.lumaMode == 4) {
                    mb2 = mbs[mb2.Rrow - 1][mb2.column];
                }
            }
            return mb2.getBottomSubblock(x, plane);
        }

        public Subblock getLeft(VP8Util.PLANE p, Macroblock[][] mbs) {
            if (this.col > 0) {
                if (VP8Util.PLANE.Y1.equals(this.plane)) {
                    return Macroblock.this.ySubblocks[this.row][this.col - 1];
                }
                if (VP8Util.PLANE.U.equals(this.plane)) {
                    return Macroblock.this.uSubblocks[this.row][this.col - 1];
                }
                if (VP8Util.PLANE.V.equals(this.plane)) {
                    return Macroblock.this.vSubblocks[this.row][this.col - 1];
                }
            }
            int y = this.row;
            Macroblock mb2 = mbs[Macroblock.this.Rrow][Macroblock.this.column - 1];
            if (p == VP8Util.PLANE.Y2) {
                while (mb2.lumaMode == 4) {
                    mb2 = mbs[mb2.Rrow][mb2.column - 1];
                }
            }
            return mb2.getRightSubBlock(y, p);
        }

        private int[] getAboveRightLowestRow(Macroblock[][] mbs) {
            int[] aboveRightDistValues;
            if (!VP8Util.PLANE.Y1.equals(this.plane)) {
                throw new IllegalArgumentException("Decoder.getAboveRight: not implemented for Y2 and chroma planes");
            }
            if (this.row == 0 && this.col < 3) {
                Macroblock mb2 = mbs[Macroblock.this.Rrow - 1][Macroblock.this.column];
                Subblock aboveRight = mb2.ySubblocks[3][this.col + 1];
                aboveRightDistValues = aboveRight.val;
            } else if (this.row > 0 && this.col < 3) {
                Subblock aboveRight2 = Macroblock.this.ySubblocks[this.row - 1][this.col + 1];
                aboveRightDistValues = aboveRight2.val;
            } else if (this.row == 0 && this.col == 3) {
                Macroblock aboveRightMb = mbs[Macroblock.this.Rrow - 1][Macroblock.this.column + 1];
                if (aboveRightMb.column < mbs[0].length - 1) {
                    Subblock aboveRightSb = aboveRightMb.ySubblocks[3][0];
                    aboveRightDistValues = aboveRightSb.val;
                } else {
                    aboveRightDistValues = new int[16];
                    int fillVal = aboveRightMb.Rrow == 0 ? 127 : mbs[Macroblock.this.Rrow - 1][Macroblock.this.column].ySubblocks[3][3].val[15];
                    Arrays.fill(aboveRightDistValues, fillVal);
                }
            } else {
                Subblock sb2 = Macroblock.this.ySubblocks[0][3];
                return sb2.getAboveRightLowestRow(mbs);
            }
            if (aboveRightDistValues == null) {
                aboveRightDistValues = VP8Util.PRED_BLOCK_127;
            }
            int[] ar = {aboveRightDistValues[12], aboveRightDistValues[13], aboveRightDistValues[14], aboveRightDistValues[15]};
            return ar;
        }

        public void decodeSubBlock(BooleanArithmeticDecoder decoder, int[][][][] allProbs, int ilc, int type, boolean withY2) {
            int startAt = 0;
            if (withY2) {
                startAt = 1;
            }
            int lc = ilc;
            int v = 1;
            boolean skip = false;
            this.someValuePresent = false;
            for (int count = 0; v != 11 && count + startAt < 16; count++) {
                int[] probs = allProbs[type][VP8Util.SubblockConstants.vp8CoefBands[count + startAt]][lc];
                if (!skip) {
                    v = decoder.readTree(VP8Util.SubblockConstants.vp8CoefTree, probs);
                } else {
                    v = decoder.readTreeSkip(VP8Util.SubblockConstants.vp8CoefTree, probs, 1);
                }
                int dv = decodeToken(decoder, v);
                lc = 0;
                skip = false;
                if (dv == 1 || dv == -1) {
                    lc = 1;
                } else if (dv > 1 || dv < -1) {
                    lc = 2;
                } else if (dv == 0) {
                    skip = true;
                }
                if (v != 11) {
                    this.tokens[VP8Util.SubblockConstants.vp8defaultZigZag1d[count + startAt]] = dv;
                }
            }
            for (int x = 0; x < 16; x++) {
                if (this.tokens[x] != 0) {
                    this.someValuePresent = true;
                }
            }
        }

        private int decodeToken(BooleanArithmeticDecoder decoder, int initialValue) {
            int token = initialValue;
            if (initialValue == 5) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat1) + 5;
            }
            if (initialValue == 6) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat2) + 7;
            }
            if (initialValue == 7) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat3) + 11;
            }
            if (initialValue == 8) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat4) + 19;
            }
            if (initialValue == 9) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat5) + 35;
            }
            if (initialValue == 10) {
                token = DCTextra(decoder, VP8Util.SubblockConstants.Pcat6) + 67;
            }
            if (initialValue != 0 && initialValue != 11 && decoder.decodeBit() > 0) {
                return -token;
            }
            return token;
        }

        private int DCTextra(BooleanArithmeticDecoder decoder, int[] p) {
            int v = 0;
            int offset = 0;
            do {
                v += decoder.decodeBool(p[offset]) + v;
                offset++;
            } while (p[offset] > 0);
            return v;
        }

        public void dequantSubblock(int dc, int ac, Integer Dc) {
            int[] adjustedValues = new int[16];
            adjustedValues[0] = this.tokens[0] * dc;
            for (int i = 1; i < 16; i++) {
                adjustedValues[i] = this.tokens[i] * ac;
            }
            if (Dc != null) {
                adjustedValues[0] = Dc.intValue();
            }
            this.residue = VP8DCT.decodeDCT(adjustedValues);
        }
    }
}
