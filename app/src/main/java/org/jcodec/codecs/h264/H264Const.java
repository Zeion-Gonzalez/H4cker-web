package org.jcodec.codecs.h264;

import android.support.v7.widget.helper.ItemTouchHelper;
import org.jcodec.codecs.h264.io.CAVLC;
import org.jcodec.codecs.h264.io.model.MBType;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.io.VLC;
import org.jcodec.common.io.VLCBuilder;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class H264Const {
    public static final int[][] ARRAY;
    public static final int[][] BLK8x8_BLOCKS;
    public static final int[] BLK_4x4_MB_OFF_LUMA;
    public static final int[] BLK_8x8_IND;
    public static final int[] BLK_8x8_MB_OFF_CHROMA;
    public static final int[] BLK_8x8_MB_OFF_LUMA;
    public static int[] BLK_INV_MAP;
    public static int[] BLK_X;
    public static int[] BLK_Y;
    public static int[] CODED_BLOCK_PATTERN_INTER_COLOR;
    public static int[] CODED_BLOCK_PATTERN_INTRA_COLOR;
    public static int[] MB_BLK_OFF_LEFT;
    public static int[] MB_BLK_OFF_TOP;
    public static final Picture NO_PIC;
    public static final int[] QP_SCALE_CR;
    public static MBType[] bMbTypes;
    public static int[] bPartH;
    public static PartPred[] bPartPredModes;
    public static int[] bPartW;
    public static PartPred[][] bPredModes;
    public static int[] bSubMbTypes;
    public static int[] coded_block_pattern_inter_monochrome;
    public static int[] coded_block_pattern_intra_monochrome;
    public static VLC[] coeffToken = new VLC[10];
    public static VLC coeffTokenChromaDCY420;
    public static VLC coeffTokenChromaDCY422;
    public static int[] identityMapping16;
    public static int[] identityMapping4;
    public static int[] last_sig_coeff_map_8x8;
    public static VLC[] run;
    public static int[] sig_coeff_map_8x8;
    public static int[] sig_coeff_map_8x8_mbaff;
    public static VLC[] totalZeros16;
    public static VLC[] totalZeros4;
    public static VLC[] totalZeros8;

    static {
        VLCBuilder vbl = new VLCBuilder();
        vbl.set(0, "1");
        vbl.set(CAVLC.coeffToken(1, 0), "000101");
        vbl.set(CAVLC.coeffToken(1, 1), "01");
        vbl.set(CAVLC.coeffToken(2, 0), "00000111");
        vbl.set(CAVLC.coeffToken(2, 1), "000100");
        vbl.set(CAVLC.coeffToken(2, 2), "001");
        vbl.set(CAVLC.coeffToken(3, 0), "000000111");
        vbl.set(CAVLC.coeffToken(3, 1), "00000110");
        vbl.set(CAVLC.coeffToken(3, 2), "0000101");
        vbl.set(CAVLC.coeffToken(3, 3), "00011");
        vbl.set(CAVLC.coeffToken(4, 0), "0000000111");
        vbl.set(CAVLC.coeffToken(4, 1), "000000110");
        vbl.set(CAVLC.coeffToken(4, 2), "00000101");
        vbl.set(CAVLC.coeffToken(4, 3), "000011");
        vbl.set(CAVLC.coeffToken(5, 0), "00000000111");
        vbl.set(CAVLC.coeffToken(5, 1), "0000000110");
        vbl.set(CAVLC.coeffToken(5, 2), "000000101");
        vbl.set(CAVLC.coeffToken(5, 3), "0000100");
        vbl.set(CAVLC.coeffToken(6, 0), "0000000001111");
        vbl.set(CAVLC.coeffToken(6, 1), "00000000110");
        vbl.set(CAVLC.coeffToken(6, 2), "0000000101");
        vbl.set(CAVLC.coeffToken(6, 3), "00000100");
        vbl.set(CAVLC.coeffToken(7, 0), "0000000001011");
        vbl.set(CAVLC.coeffToken(7, 1), "0000000001110");
        vbl.set(CAVLC.coeffToken(7, 2), "00000000101");
        vbl.set(CAVLC.coeffToken(7, 3), "000000100");
        vbl.set(CAVLC.coeffToken(8, 0), "0000000001000");
        vbl.set(CAVLC.coeffToken(8, 1), "0000000001010");
        vbl.set(CAVLC.coeffToken(8, 2), "0000000001101");
        vbl.set(CAVLC.coeffToken(8, 3), "0000000100");
        vbl.set(CAVLC.coeffToken(9, 0), "00000000001111");
        vbl.set(CAVLC.coeffToken(9, 1), "00000000001110");
        vbl.set(CAVLC.coeffToken(9, 2), "0000000001001");
        vbl.set(CAVLC.coeffToken(9, 3), "00000000100");
        vbl.set(CAVLC.coeffToken(10, 0), "00000000001011");
        vbl.set(CAVLC.coeffToken(10, 1), "00000000001010");
        vbl.set(CAVLC.coeffToken(10, 2), "00000000001101");
        vbl.set(CAVLC.coeffToken(10, 3), "0000000001100");
        vbl.set(CAVLC.coeffToken(11, 0), "000000000001111");
        vbl.set(CAVLC.coeffToken(11, 1), "000000000001110");
        vbl.set(CAVLC.coeffToken(11, 2), "00000000001001");
        vbl.set(CAVLC.coeffToken(11, 3), "00000000001100");
        vbl.set(CAVLC.coeffToken(12, 0), "000000000001011");
        vbl.set(CAVLC.coeffToken(12, 1), "000000000001010");
        vbl.set(CAVLC.coeffToken(12, 2), "000000000001101");
        vbl.set(CAVLC.coeffToken(12, 3), "00000000001000");
        vbl.set(CAVLC.coeffToken(13, 0), "0000000000001111");
        vbl.set(CAVLC.coeffToken(13, 1), "000000000000001");
        vbl.set(CAVLC.coeffToken(13, 2), "000000000001001");
        vbl.set(CAVLC.coeffToken(13, 3), "000000000001100");
        vbl.set(CAVLC.coeffToken(14, 0), "0000000000001011");
        vbl.set(CAVLC.coeffToken(14, 1), "0000000000001110");
        vbl.set(CAVLC.coeffToken(14, 2), "0000000000001101");
        vbl.set(CAVLC.coeffToken(14, 3), "000000000001000");
        vbl.set(CAVLC.coeffToken(15, 0), "0000000000000111");
        vbl.set(CAVLC.coeffToken(15, 1), "0000000000001010");
        vbl.set(CAVLC.coeffToken(15, 2), "0000000000001001");
        vbl.set(CAVLC.coeffToken(15, 3), "0000000000001100");
        vbl.set(CAVLC.coeffToken(16, 0), "0000000000000100");
        vbl.set(CAVLC.coeffToken(16, 1), "0000000000000110");
        vbl.set(CAVLC.coeffToken(16, 2), "0000000000000101");
        vbl.set(CAVLC.coeffToken(16, 3), "0000000000001000");
        VLC[] vlcArr = coeffToken;
        VLC[] vlcArr2 = coeffToken;
        VLC vlc = vbl.getVLC();
        vlcArr2[1] = vlc;
        vlcArr[0] = vlc;
        VLCBuilder vbl2 = new VLCBuilder();
        vbl2.set(CAVLC.coeffToken(0, 0), "11");
        vbl2.set(CAVLC.coeffToken(1, 0), "001011");
        vbl2.set(CAVLC.coeffToken(1, 1), "10");
        vbl2.set(CAVLC.coeffToken(2, 0), "000111");
        vbl2.set(CAVLC.coeffToken(2, 1), "00111");
        vbl2.set(CAVLC.coeffToken(2, 2), "011");
        vbl2.set(CAVLC.coeffToken(3, 0), "0000111");
        vbl2.set(CAVLC.coeffToken(3, 1), "001010");
        vbl2.set(CAVLC.coeffToken(3, 2), "001001");
        vbl2.set(CAVLC.coeffToken(3, 3), "0101");
        vbl2.set(CAVLC.coeffToken(4, 0), "00000111");
        vbl2.set(CAVLC.coeffToken(4, 1), "000110");
        vbl2.set(CAVLC.coeffToken(4, 2), "000101");
        vbl2.set(CAVLC.coeffToken(4, 3), "0100");
        vbl2.set(CAVLC.coeffToken(5, 0), "00000100");
        vbl2.set(CAVLC.coeffToken(5, 1), "0000110");
        vbl2.set(CAVLC.coeffToken(5, 2), "0000101");
        vbl2.set(CAVLC.coeffToken(5, 3), "00110");
        vbl2.set(CAVLC.coeffToken(6, 0), "000000111");
        vbl2.set(CAVLC.coeffToken(6, 1), "00000110");
        vbl2.set(CAVLC.coeffToken(6, 2), "00000101");
        vbl2.set(CAVLC.coeffToken(6, 3), "001000");
        vbl2.set(CAVLC.coeffToken(7, 0), "00000001111");
        vbl2.set(CAVLC.coeffToken(7, 1), "000000110");
        vbl2.set(CAVLC.coeffToken(7, 2), "000000101");
        vbl2.set(CAVLC.coeffToken(7, 3), "000100");
        vbl2.set(CAVLC.coeffToken(8, 0), "00000001011");
        vbl2.set(CAVLC.coeffToken(8, 1), "00000001110");
        vbl2.set(CAVLC.coeffToken(8, 2), "00000001101");
        vbl2.set(CAVLC.coeffToken(8, 3), "0000100");
        vbl2.set(CAVLC.coeffToken(9, 0), "000000001111");
        vbl2.set(CAVLC.coeffToken(9, 1), "00000001010");
        vbl2.set(CAVLC.coeffToken(9, 2), "00000001001");
        vbl2.set(CAVLC.coeffToken(9, 3), "000000100");
        vbl2.set(CAVLC.coeffToken(10, 0), "000000001011");
        vbl2.set(CAVLC.coeffToken(10, 1), "000000001110");
        vbl2.set(CAVLC.coeffToken(10, 2), "000000001101");
        vbl2.set(CAVLC.coeffToken(10, 3), "00000001100");
        vbl2.set(CAVLC.coeffToken(11, 0), "000000001000");
        vbl2.set(CAVLC.coeffToken(11, 1), "000000001010");
        vbl2.set(CAVLC.coeffToken(11, 2), "000000001001");
        vbl2.set(CAVLC.coeffToken(11, 3), "00000001000");
        vbl2.set(CAVLC.coeffToken(12, 0), "0000000001111");
        vbl2.set(CAVLC.coeffToken(12, 1), "0000000001110");
        vbl2.set(CAVLC.coeffToken(12, 2), "0000000001101");
        vbl2.set(CAVLC.coeffToken(12, 3), "000000001100");
        vbl2.set(CAVLC.coeffToken(13, 0), "0000000001011");
        vbl2.set(CAVLC.coeffToken(13, 1), "0000000001010");
        vbl2.set(CAVLC.coeffToken(13, 2), "0000000001001");
        vbl2.set(CAVLC.coeffToken(13, 3), "0000000001100");
        vbl2.set(CAVLC.coeffToken(14, 0), "0000000000111");
        vbl2.set(CAVLC.coeffToken(14, 1), "00000000001011");
        vbl2.set(CAVLC.coeffToken(14, 2), "0000000000110");
        vbl2.set(CAVLC.coeffToken(14, 3), "0000000001000");
        vbl2.set(CAVLC.coeffToken(15, 0), "00000000001001");
        vbl2.set(CAVLC.coeffToken(15, 1), "00000000001000");
        vbl2.set(CAVLC.coeffToken(15, 2), "00000000001010");
        vbl2.set(CAVLC.coeffToken(15, 3), "0000000000001");
        vbl2.set(CAVLC.coeffToken(16, 0), "00000000000111");
        vbl2.set(CAVLC.coeffToken(16, 1), "00000000000110");
        vbl2.set(CAVLC.coeffToken(16, 2), "00000000000101");
        vbl2.set(CAVLC.coeffToken(16, 3), "00000000000100");
        VLC[] vlcArr3 = coeffToken;
        VLC[] vlcArr4 = coeffToken;
        VLC vlc2 = vbl2.getVLC();
        vlcArr4[3] = vlc2;
        vlcArr3[2] = vlc2;
        VLCBuilder vbl3 = new VLCBuilder();
        vbl3.set(CAVLC.coeffToken(0, 0), "1111");
        vbl3.set(CAVLC.coeffToken(1, 0), "001111");
        vbl3.set(CAVLC.coeffToken(1, 1), "1110");
        vbl3.set(CAVLC.coeffToken(2, 0), "001011");
        vbl3.set(CAVLC.coeffToken(2, 1), "01111");
        vbl3.set(CAVLC.coeffToken(2, 2), "1101");
        vbl3.set(CAVLC.coeffToken(3, 0), "001000");
        vbl3.set(CAVLC.coeffToken(3, 1), "01100");
        vbl3.set(CAVLC.coeffToken(3, 2), "01110");
        vbl3.set(CAVLC.coeffToken(3, 3), "1100");
        vbl3.set(CAVLC.coeffToken(4, 0), "0001111");
        vbl3.set(CAVLC.coeffToken(4, 1), "01010");
        vbl3.set(CAVLC.coeffToken(4, 2), "01011");
        vbl3.set(CAVLC.coeffToken(4, 3), "1011");
        vbl3.set(CAVLC.coeffToken(5, 0), "0001011");
        vbl3.set(CAVLC.coeffToken(5, 1), "01000");
        vbl3.set(CAVLC.coeffToken(5, 2), "01001");
        vbl3.set(CAVLC.coeffToken(5, 3), "1010");
        vbl3.set(CAVLC.coeffToken(6, 0), "0001001");
        vbl3.set(CAVLC.coeffToken(6, 1), "001110");
        vbl3.set(CAVLC.coeffToken(6, 2), "001101");
        vbl3.set(CAVLC.coeffToken(6, 3), "1001");
        vbl3.set(CAVLC.coeffToken(7, 0), "0001000");
        vbl3.set(CAVLC.coeffToken(7, 1), "001010");
        vbl3.set(CAVLC.coeffToken(7, 2), "001001");
        vbl3.set(CAVLC.coeffToken(7, 3), "1000");
        vbl3.set(CAVLC.coeffToken(8, 0), "00001111");
        vbl3.set(CAVLC.coeffToken(8, 1), "0001110");
        vbl3.set(CAVLC.coeffToken(8, 2), "0001101");
        vbl3.set(CAVLC.coeffToken(8, 3), "01101");
        vbl3.set(CAVLC.coeffToken(9, 0), "00001011");
        vbl3.set(CAVLC.coeffToken(9, 1), "00001110");
        vbl3.set(CAVLC.coeffToken(9, 2), "0001010");
        vbl3.set(CAVLC.coeffToken(9, 3), "001100");
        vbl3.set(CAVLC.coeffToken(10, 0), "000001111");
        vbl3.set(CAVLC.coeffToken(10, 1), "00001010");
        vbl3.set(CAVLC.coeffToken(10, 2), "00001101");
        vbl3.set(CAVLC.coeffToken(10, 3), "0001100");
        vbl3.set(CAVLC.coeffToken(11, 0), "000001011");
        vbl3.set(CAVLC.coeffToken(11, 1), "000001110");
        vbl3.set(CAVLC.coeffToken(11, 2), "00001001");
        vbl3.set(CAVLC.coeffToken(11, 3), "00001100");
        vbl3.set(CAVLC.coeffToken(12, 0), "000001000");
        vbl3.set(CAVLC.coeffToken(12, 1), "000001010");
        vbl3.set(CAVLC.coeffToken(12, 2), "000001101");
        vbl3.set(CAVLC.coeffToken(12, 3), "00001000");
        vbl3.set(CAVLC.coeffToken(13, 0), "0000001101");
        vbl3.set(CAVLC.coeffToken(13, 1), "000000111");
        vbl3.set(CAVLC.coeffToken(13, 2), "000001001");
        vbl3.set(CAVLC.coeffToken(13, 3), "000001100");
        vbl3.set(CAVLC.coeffToken(14, 0), "0000001001");
        vbl3.set(CAVLC.coeffToken(14, 1), "0000001100");
        vbl3.set(CAVLC.coeffToken(14, 2), "0000001011");
        vbl3.set(CAVLC.coeffToken(14, 3), "0000001010");
        vbl3.set(CAVLC.coeffToken(15, 0), "0000000101");
        vbl3.set(CAVLC.coeffToken(15, 1), "0000001000");
        vbl3.set(CAVLC.coeffToken(15, 2), "0000000111");
        vbl3.set(CAVLC.coeffToken(15, 3), "0000000110");
        vbl3.set(CAVLC.coeffToken(16, 0), "0000000001");
        vbl3.set(CAVLC.coeffToken(16, 1), "0000000100");
        vbl3.set(CAVLC.coeffToken(16, 2), "0000000011");
        vbl3.set(CAVLC.coeffToken(16, 3), "0000000010");
        VLC[] vlcArr5 = coeffToken;
        VLC[] vlcArr6 = coeffToken;
        VLC[] vlcArr7 = coeffToken;
        VLC[] vlcArr8 = coeffToken;
        VLC vlc3 = vbl3.getVLC();
        vlcArr8[7] = vlc3;
        vlcArr7[6] = vlc3;
        vlcArr6[5] = vlc3;
        vlcArr5[4] = vlc3;
        VLCBuilder vbl4 = new VLCBuilder();
        vbl4.set(CAVLC.coeffToken(0, 0), "000011");
        vbl4.set(CAVLC.coeffToken(1, 0), "000000");
        vbl4.set(CAVLC.coeffToken(1, 1), "000001");
        vbl4.set(CAVLC.coeffToken(2, 0), "000100");
        vbl4.set(CAVLC.coeffToken(2, 1), "000101");
        vbl4.set(CAVLC.coeffToken(2, 2), "000110");
        vbl4.set(CAVLC.coeffToken(3, 0), "001000");
        vbl4.set(CAVLC.coeffToken(3, 1), "001001");
        vbl4.set(CAVLC.coeffToken(3, 2), "001010");
        vbl4.set(CAVLC.coeffToken(3, 3), "001011");
        vbl4.set(CAVLC.coeffToken(4, 0), "001100");
        vbl4.set(CAVLC.coeffToken(4, 1), "001101");
        vbl4.set(CAVLC.coeffToken(4, 2), "001110");
        vbl4.set(CAVLC.coeffToken(4, 3), "001111");
        vbl4.set(CAVLC.coeffToken(5, 0), "010000");
        vbl4.set(CAVLC.coeffToken(5, 1), "010001");
        vbl4.set(CAVLC.coeffToken(5, 2), "010010");
        vbl4.set(CAVLC.coeffToken(5, 3), "010011");
        vbl4.set(CAVLC.coeffToken(6, 0), "010100");
        vbl4.set(CAVLC.coeffToken(6, 1), "010101");
        vbl4.set(CAVLC.coeffToken(6, 2), "010110");
        vbl4.set(CAVLC.coeffToken(6, 3), "010111");
        vbl4.set(CAVLC.coeffToken(7, 0), "011000");
        vbl4.set(CAVLC.coeffToken(7, 1), "011001");
        vbl4.set(CAVLC.coeffToken(7, 2), "011010");
        vbl4.set(CAVLC.coeffToken(7, 3), "011011");
        vbl4.set(CAVLC.coeffToken(8, 0), "011100");
        vbl4.set(CAVLC.coeffToken(8, 1), "011101");
        vbl4.set(CAVLC.coeffToken(8, 2), "011110");
        vbl4.set(CAVLC.coeffToken(8, 3), "011111");
        vbl4.set(CAVLC.coeffToken(9, 0), "100000");
        vbl4.set(CAVLC.coeffToken(9, 1), "100001");
        vbl4.set(CAVLC.coeffToken(9, 2), "100010");
        vbl4.set(CAVLC.coeffToken(9, 3), "100011");
        vbl4.set(CAVLC.coeffToken(10, 0), "100100");
        vbl4.set(CAVLC.coeffToken(10, 1), "100101");
        vbl4.set(CAVLC.coeffToken(10, 2), "100110");
        vbl4.set(CAVLC.coeffToken(10, 3), "100111");
        vbl4.set(CAVLC.coeffToken(11, 0), "101000");
        vbl4.set(CAVLC.coeffToken(11, 1), "101001");
        vbl4.set(CAVLC.coeffToken(11, 2), "101010");
        vbl4.set(CAVLC.coeffToken(11, 3), "101011");
        vbl4.set(CAVLC.coeffToken(12, 0), "101100");
        vbl4.set(CAVLC.coeffToken(12, 1), "101101");
        vbl4.set(CAVLC.coeffToken(12, 2), "101110");
        vbl4.set(CAVLC.coeffToken(12, 3), "101111");
        vbl4.set(CAVLC.coeffToken(13, 0), "110000");
        vbl4.set(CAVLC.coeffToken(13, 1), "110001");
        vbl4.set(CAVLC.coeffToken(13, 2), "110010");
        vbl4.set(CAVLC.coeffToken(13, 3), "110011");
        vbl4.set(CAVLC.coeffToken(14, 0), "110100");
        vbl4.set(CAVLC.coeffToken(14, 1), "110101");
        vbl4.set(CAVLC.coeffToken(14, 2), "110110");
        vbl4.set(CAVLC.coeffToken(14, 3), "110111");
        vbl4.set(CAVLC.coeffToken(15, 0), "111000");
        vbl4.set(CAVLC.coeffToken(15, 1), "111001");
        vbl4.set(CAVLC.coeffToken(15, 2), "111010");
        vbl4.set(CAVLC.coeffToken(15, 3), "111011");
        vbl4.set(CAVLC.coeffToken(16, 0), "111100");
        vbl4.set(CAVLC.coeffToken(16, 1), "111101");
        vbl4.set(CAVLC.coeffToken(16, 2), "111110");
        vbl4.set(CAVLC.coeffToken(16, 3), "111111");
        coeffToken[8] = vbl4.getVLC();
        VLCBuilder vbl5 = new VLCBuilder();
        vbl5.set(CAVLC.coeffToken(0, 0), "01");
        vbl5.set(CAVLC.coeffToken(1, 0), "000111");
        vbl5.set(CAVLC.coeffToken(1, 1), "1");
        vbl5.set(CAVLC.coeffToken(2, 0), "000100");
        vbl5.set(CAVLC.coeffToken(2, 1), "000110");
        vbl5.set(CAVLC.coeffToken(2, 2), "001");
        vbl5.set(CAVLC.coeffToken(3, 0), "000011");
        vbl5.set(CAVLC.coeffToken(3, 1), "0000011");
        vbl5.set(CAVLC.coeffToken(3, 2), "0000010");
        vbl5.set(CAVLC.coeffToken(3, 3), "000101");
        vbl5.set(CAVLC.coeffToken(4, 0), "000010");
        vbl5.set(CAVLC.coeffToken(4, 1), "00000011");
        vbl5.set(CAVLC.coeffToken(4, 2), "00000010");
        vbl5.set(CAVLC.coeffToken(4, 3), "0000000");
        coeffTokenChromaDCY420 = vbl5.getVLC();
        VLCBuilder vbl6 = new VLCBuilder();
        vbl6.set(CAVLC.coeffToken(0, 0), "1");
        vbl6.set(CAVLC.coeffToken(1, 0), "0001111");
        vbl6.set(CAVLC.coeffToken(1, 1), "01");
        vbl6.set(CAVLC.coeffToken(2, 0), "0001110");
        vbl6.set(CAVLC.coeffToken(2, 1), "0001101");
        vbl6.set(CAVLC.coeffToken(2, 2), "001");
        vbl6.set(CAVLC.coeffToken(3, 0), "000000111");
        vbl6.set(CAVLC.coeffToken(3, 1), "0001100");
        vbl6.set(CAVLC.coeffToken(3, 2), "0001011");
        vbl6.set(CAVLC.coeffToken(3, 3), "00001");
        vbl6.set(CAVLC.coeffToken(4, 0), "000000110");
        vbl6.set(CAVLC.coeffToken(4, 1), "000000101");
        vbl6.set(CAVLC.coeffToken(4, 2), "0001010");
        vbl6.set(CAVLC.coeffToken(4, 3), "000001");
        vbl6.set(CAVLC.coeffToken(5, 0), "0000000111");
        vbl6.set(CAVLC.coeffToken(5, 1), "0000000110");
        vbl6.set(CAVLC.coeffToken(5, 2), "000000100");
        vbl6.set(CAVLC.coeffToken(5, 3), "0001001");
        vbl6.set(CAVLC.coeffToken(6, 0), "00000000111");
        vbl6.set(CAVLC.coeffToken(6, 1), "00000000110");
        vbl6.set(CAVLC.coeffToken(6, 2), "0000000101");
        vbl6.set(CAVLC.coeffToken(6, 3), "0001000");
        vbl6.set(CAVLC.coeffToken(7, 0), "000000000111");
        vbl6.set(CAVLC.coeffToken(7, 1), "000000000110");
        vbl6.set(CAVLC.coeffToken(7, 2), "00000000101");
        vbl6.set(CAVLC.coeffToken(7, 3), "0000000100");
        vbl6.set(CAVLC.coeffToken(8, 0), "0000000000111");
        vbl6.set(CAVLC.coeffToken(8, 1), "000000000101");
        vbl6.set(CAVLC.coeffToken(8, 2), "000000000100");
        vbl6.set(CAVLC.coeffToken(8, 3), "00000000100");
        coeffTokenChromaDCY422 = vbl6.getVLC();
        run = new VLC[]{new VLCBuilder().set(0, "1").set(1, "0").getVLC(), new VLCBuilder().set(0, "1").set(1, "01").set(2, "00").getVLC(), new VLCBuilder().set(0, "11").set(1, "10").set(2, "01").set(3, "00").getVLC(), new VLCBuilder().set(0, "11").set(1, "10").set(2, "01").set(3, "001").set(4, "000").getVLC(), new VLCBuilder().set(0, "11").set(1, "10").set(2, "011").set(3, "010").set(4, "001").set(5, "000").getVLC(), new VLCBuilder().set(0, "11").set(1, "000").set(2, "001").set(3, "011").set(4, "010").set(5, "101").set(6, "100").getVLC(), new VLCBuilder().set(0, "111").set(1, "110").set(2, "101").set(3, "100").set(4, "011").set(5, "010").set(6, "001").set(7, "0001").set(8, "00001").set(9, "000001").set(10, "0000001").set(11, "00000001").set(12, "000000001").set(13, "0000000001").set(14, "00000000001").getVLC()};
        totalZeros16 = new VLC[]{new VLCBuilder().set(0, "1").set(1, "011").set(2, "010").set(3, "0011").set(4, "0010").set(5, "00011").set(6, "00010").set(7, "000011").set(8, "000010").set(9, "0000011").set(10, "0000010").set(11, "00000011").set(12, "00000010").set(13, "000000011").set(14, "000000010").set(15, "000000001").getVLC(), new VLCBuilder().set(0, "111").set(1, "110").set(2, "101").set(3, "100").set(4, "011").set(5, "0101").set(6, "0100").set(7, "0011").set(8, "0010").set(9, "00011").set(10, "00010").set(11, "000011").set(12, "000010").set(13, "000001").set(14, "000000").getVLC(), new VLCBuilder().set(0, "0101").set(1, "111").set(2, "110").set(3, "101").set(4, "0100").set(5, "0011").set(6, "100").set(7, "011").set(8, "0010").set(9, "00011").set(10, "00010").set(11, "000001").set(12, "00001").set(13, "000000").getVLC(), new VLCBuilder().set(0, "00011").set(1, "111").set(2, "0101").set(3, "0100").set(4, "110").set(5, "101").set(6, "100").set(7, "0011").set(8, "011").set(9, "0010").set(10, "00010").set(11, "00001").set(12, "00000").getVLC(), new VLCBuilder().set(0, "0101").set(1, "0100").set(2, "0011").set(3, "111").set(4, "110").set(5, "101").set(6, "100").set(7, "011").set(8, "0010").set(9, "00001").set(10, "0001").set(11, "00000").getVLC(), new VLCBuilder().set(0, "000001").set(1, "00001").set(2, "111").set(3, "110").set(4, "101").set(5, "100").set(6, "011").set(7, "010").set(8, "0001").set(9, "001").set(10, "000000").getVLC(), new VLCBuilder().set(0, "000001").set(1, "00001").set(2, "101").set(3, "100").set(4, "011").set(5, "11").set(6, "010").set(7, "0001").set(8, "001").set(9, "000000").getVLC(), new VLCBuilder().set(0, "000001").set(1, "0001").set(2, "00001").set(3, "011").set(4, "11").set(5, "10").set(6, "010").set(7, "001").set(8, "000000").getVLC(), new VLCBuilder().set(0, "000001").set(1, "000000").set(2, "0001").set(3, "11").set(4, "10").set(5, "001").set(6, "01").set(7, "00001").getVLC(), new VLCBuilder().set(0, "00001").set(1, "00000").set(2, "001").set(3, "11").set(4, "10").set(5, "01").set(6, "0001").getVLC(), new VLCBuilder().set(0, "0000").set(1, "0001").set(2, "001").set(3, "010").set(4, "1").set(5, "011").getVLC(), new VLCBuilder().set(0, "0000").set(1, "0001").set(2, "01").set(3, "1").set(4, "001").getVLC(), new VLCBuilder().set(0, "000").set(1, "001").set(2, "1").set(3, "01").getVLC(), new VLCBuilder().set(0, "00").set(1, "01").set(2, "1").getVLC(), new VLCBuilder().set(0, "0").set(1, "1").getVLC()};
        totalZeros4 = new VLC[]{new VLCBuilder().set(0, "1").set(1, "01").set(2, "001").set(3, "000").getVLC(), new VLCBuilder().set(0, "1").set(1, "01").set(2, "00").getVLC(), new VLCBuilder().set(0, "1").set(1, "0").getVLC()};
        totalZeros8 = new VLC[]{new VLCBuilder().set(0, "1").set(1, "010").set(2, "011").set(3, "0010").set(4, "0011").set(5, "0001").set(6, "00001").set(7, "00000").getVLC(), new VLCBuilder().set(0, "000").set(1, "01").set(2, "001").set(3, "100").set(4, "101").set(5, "110").set(6, "111").getVLC(), new VLCBuilder().set(0, "000").set(1, "001").set(2, "01").set(3, "10").set(4, "110").set(5, "111").getVLC(), new VLCBuilder().set(0, "110").set(1, "00").set(2, "01").set(3, "10").set(4, "111").getVLC(), new VLCBuilder().set(0, "00").set(1, "01").set(2, "10").set(3, "11").getVLC(), new VLCBuilder().set(0, "00").set(1, "01").set(2, "1").getVLC(), new VLCBuilder().set(0, "0").set(1, "1").getVLC()};
        bPredModes = new PartPred[][]{null, new PartPred[]{PartPred.L0}, new PartPred[]{PartPred.L1}, new PartPred[]{PartPred.Bi}, new PartPred[]{PartPred.L0, PartPred.L0}, new PartPred[]{PartPred.L0, PartPred.L0}, new PartPred[]{PartPred.L1, PartPred.L1}, new PartPred[]{PartPred.L1, PartPred.L1}, new PartPred[]{PartPred.L0, PartPred.L1}, new PartPred[]{PartPred.L0, PartPred.L1}, new PartPred[]{PartPred.L1, PartPred.L0}, new PartPred[]{PartPred.L1, PartPred.L0}, new PartPred[]{PartPred.L0, PartPred.Bi}, new PartPred[]{PartPred.L0, PartPred.Bi}, new PartPred[]{PartPred.L1, PartPred.Bi}, new PartPred[]{PartPred.L1, PartPred.Bi}, new PartPred[]{PartPred.Bi, PartPred.L0}, new PartPred[]{PartPred.Bi, PartPred.L0}, new PartPred[]{PartPred.Bi, PartPred.L1}, new PartPred[]{PartPred.Bi, PartPred.L1}, new PartPred[]{PartPred.Bi, PartPred.Bi}, new PartPred[]{PartPred.Bi, PartPred.Bi}};
        bMbTypes = new MBType[]{MBType.B_Direct_16x16, MBType.B_L0_16x16, MBType.B_L1_16x16, MBType.B_Bi_16x16, MBType.B_L0_L0_16x8, MBType.B_L0_L0_8x16, MBType.B_L1_L1_16x8, MBType.B_L1_L1_8x16, MBType.B_L0_L1_16x8, MBType.B_L0_L1_8x16, MBType.B_L1_L0_16x8, MBType.B_L1_L0_8x16, MBType.B_L0_Bi_16x8, MBType.B_L0_Bi_8x16, MBType.B_L1_Bi_16x8, MBType.B_L1_Bi_8x16, MBType.B_Bi_L0_16x8, MBType.B_Bi_L0_8x16, MBType.B_Bi_L1_16x8, MBType.B_Bi_L1_8x16, MBType.B_Bi_Bi_16x8, MBType.B_Bi_Bi_8x16, MBType.B_8x8};
        bPartW = new int[]{0, 16, 16, 16, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8};
        bPartH = new int[]{0, 16, 16, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16, 8, 16};
        BLK_X = new int[]{0, 4, 0, 4, 8, 12, 8, 12, 0, 4, 0, 4, 8, 12, 8, 12};
        BLK_Y = new int[]{0, 0, 4, 4, 0, 0, 4, 4, 8, 8, 12, 12, 8, 8, 12, 12};
        BLK_INV_MAP = new int[]{0, 1, 4, 5, 2, 3, 6, 7, 8, 9, 12, 13, 10, 11, 14, 15};
        MB_BLK_OFF_LEFT = new int[]{0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3};
        MB_BLK_OFF_TOP = new int[]{0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3};
        QP_SCALE_CR = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 29, 30, 31, 32, 32, 33, 34, 34, 35, 35, 36, 36, 37, 37, 37, 38, 38, 38, 39, 39, 39, 39};
        NO_PIC = new Picture(0, 0, null, null);
        BLK_8x8_MB_OFF_LUMA = new int[]{0, 8, 128, 136};
        BLK_8x8_MB_OFF_CHROMA = new int[]{0, 4, 32, 36};
        BLK_4x4_MB_OFF_LUMA = new int[]{0, 4, 8, 12, 64, 68, 72, 76, 128, 132, 136, 140, JpegConst.SOF0, JpegConst.DHT, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 204};
        BLK_8x8_IND = new int[]{0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3};
        BLK8x8_BLOCKS = new int[][]{new int[]{0, 1, 4, 5}, new int[]{2, 3, 6, 7}, new int[]{8, 9, 12, 13}, new int[]{10, 11, 14, 15}};
        ARRAY = new int[][]{new int[]{0}, new int[]{1}, new int[]{2}, new int[]{3}};
        CODED_BLOCK_PATTERN_INTRA_COLOR = new int[]{47, 31, 15, 0, 23, 27, 29, 30, 7, 11, 13, 14, 39, 43, 45, 46, 16, 3, 5, 10, 12, 19, 21, 26, 28, 35, 37, 42, 44, 1, 2, 4, 8, 17, 18, 20, 24, 6, 9, 22, 25, 32, 33, 34, 36, 40, 38, 41};
        coded_block_pattern_intra_monochrome = new int[]{15, 0, 7, 11, 13, 14, 3, 5, 10, 12, 1, 2, 4, 8, 6, 9};
        CODED_BLOCK_PATTERN_INTER_COLOR = new int[]{0, 16, 1, 2, 4, 8, 32, 3, 5, 10, 12, 15, 47, 7, 11, 13, 14, 6, 9, 31, 35, 37, 42, 44, 33, 34, 36, 40, 39, 43, 45, 46, 17, 18, 20, 24, 19, 21, 26, 28, 23, 27, 29, 30, 22, 25, 38, 41};
        coded_block_pattern_inter_monochrome = new int[]{0, 1, 2, 4, 8, 3, 5, 10, 12, 15, 7, 11, 13, 14, 6, 9};
        sig_coeff_map_8x8 = new int[]{0, 1, 2, 3, 4, 5, 5, 4, 4, 3, 3, 4, 4, 4, 5, 5, 4, 4, 4, 4, 3, 3, 6, 7, 7, 7, 8, 9, 10, 9, 8, 7, 7, 6, 11, 12, 13, 11, 6, 7, 8, 9, 14, 10, 9, 8, 6, 11, 12, 13, 11, 6, 9, 14, 10, 9, 11, 12, 13, 11, 14, 10, 12};
        sig_coeff_map_8x8_mbaff = new int[]{0, 1, 1, 2, 2, 3, 3, 4, 5, 6, 7, 7, 7, 8, 4, 5, 6, 9, 10, 10, 8, 11, 12, 11, 9, 9, 10, 10, 8, 11, 12, 11, 9, 9, 10, 10, 8, 11, 12, 11, 9, 9, 10, 10, 8, 13, 13, 9, 9, 10, 10, 8, 13, 13, 9, 9, 10, 10, 14, 14, 14, 14, 14};
        last_sig_coeff_map_8x8 = new int[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8};
        identityMapping16 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        identityMapping4 = new int[]{0, 1, 2, 3};
        bPartPredModes = new PartPred[]{PartPred.Direct, PartPred.L0, PartPred.L1, PartPred.Bi, PartPred.L0, PartPred.L0, PartPred.L1, PartPred.L1, PartPred.Bi, PartPred.Bi, PartPred.L0, PartPred.L1, PartPred.Bi};
        bSubMbTypes = new int[]{0, 0, 0, 0, 1, 2, 1, 2, 1, 2, 3, 3, 3};
    }

    /* loaded from: classes.dex */
    public enum PartPred {
        L0,
        L1,
        Bi,
        Direct;

        public boolean usesList(int l) {
            if (this == Bi) {
                return true;
            }
            if (this == L0 && l == 0) {
                return true;
            }
            return this == L1 && l == 1;
        }
    }
}