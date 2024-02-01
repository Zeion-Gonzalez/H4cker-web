package org.jcodec.codecs.vp8;

import java.lang.reflect.Array;

/* loaded from: classes.dex */
public class VP8DCT {
    private static final int cospi8sqrt2minus1 = 20091;
    private static final int sinpi8sqrt2 = 35468;

    public static int[] decodeDCT(int[] input) {
        int offset = 0;
        int[] output = new int[16];
        for (int i = 0; i < 4; i++) {
            int a1 = input[offset + 0] + input[offset + 8];
            int b1 = input[offset + 0] - input[offset + 8];
            int temp1 = (input[offset + 4] * sinpi8sqrt2) >> 16;
            int temp2 = input[offset + 12] + ((input[offset + 12] * cospi8sqrt2minus1) >> 16);
            int c1 = temp1 - temp2;
            int temp12 = input[offset + 4] + ((input[offset + 4] * cospi8sqrt2minus1) >> 16);
            int temp22 = (input[offset + 12] * sinpi8sqrt2) >> 16;
            int d1 = temp12 + temp22;
            output[offset + 0] = a1 + d1;
            output[offset + 12] = a1 - d1;
            output[offset + 4] = b1 + c1;
            output[offset + 8] = b1 - c1;
            offset++;
        }
        int offset2 = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = output[(offset2 * 4) + 0] + output[(offset2 * 4) + 2];
            int b12 = output[(offset2 * 4) + 0] - output[(offset2 * 4) + 2];
            int temp13 = (output[(offset2 * 4) + 1] * sinpi8sqrt2) >> 16;
            int temp23 = output[(offset2 * 4) + 3] + ((output[(offset2 * 4) + 3] * cospi8sqrt2minus1) >> 16);
            int c12 = temp13 - temp23;
            int temp14 = output[(offset2 * 4) + 1] + ((output[(offset2 * 4) + 1] * cospi8sqrt2minus1) >> 16);
            int temp24 = (output[(offset2 * 4) + 3] * sinpi8sqrt2) >> 16;
            int d12 = temp14 + temp24;
            output[(offset2 * 4) + 0] = ((a12 + d12) + 4) >> 3;
            output[(offset2 * 4) + 3] = ((a12 - d12) + 4) >> 3;
            output[(offset2 * 4) + 1] = ((b12 + c12) + 4) >> 3;
            output[(offset2 * 4) + 2] = ((b12 - c12) + 4) >> 3;
            offset2++;
        }
        return output;
    }

    public static int[] encodeDCT(int[] input) {
        int ip = 0;
        int[] output = new int[input.length];
        int op = 0;
        for (int i = 0; i < 4; i++) {
            int a1 = (input[ip + 0] + input[ip + 3]) << 3;
            int b1 = (input[ip + 1] + input[ip + 2]) << 3;
            int c1 = (input[ip + 1] - input[ip + 2]) << 3;
            int d1 = (input[ip + 0] - input[ip + 3]) << 3;
            output[op + 0] = a1 + b1;
            output[op + 2] = a1 - b1;
            output[op + 1] = (((c1 * 2217) + (d1 * 5352)) + 14500) >> 12;
            output[op + 3] = (((d1 * 2217) - (c1 * 5352)) + 7500) >> 12;
            ip += 4;
            op += 4;
        }
        int ip2 = 0;
        int op2 = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = output[ip2 + 0] + output[ip2 + 12];
            int b12 = output[ip2 + 4] + output[ip2 + 8];
            int c12 = output[ip2 + 4] - output[ip2 + 8];
            int d12 = output[ip2 + 0] - output[ip2 + 12];
            output[op2 + 0] = ((a12 + b12) + 7) >> 4;
            output[op2 + 8] = ((a12 - b12) + 7) >> 4;
            output[op2 + 4] = (d12 != 0 ? 1 : 0) + ((((c12 * 2217) + (d12 * 5352)) + 12000) >> 16);
            output[op2 + 12] = (((d12 * 2217) - (c12 * 5352)) + 51000) >> 16;
            ip2++;
            op2++;
        }
        return output;
    }

    public static int[] decodeWHT(int[] input) {
        int[] output = new int[16];
        int[][] diff = (int[][]) Array.newInstance(Integer.TYPE, 4, 4);
        int offset = 0;
        for (int i = 0; i < 4; i++) {
            int a1 = input[offset + 0] + input[offset + 12];
            int b1 = input[offset + 4] + input[offset + 8];
            int c1 = input[offset + 4] - input[offset + 8];
            int d1 = input[offset + 0] - input[offset + 12];
            output[offset + 0] = a1 + b1;
            output[offset + 4] = c1 + d1;
            output[offset + 8] = a1 - b1;
            output[offset + 12] = d1 - c1;
            offset++;
        }
        int offset2 = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = output[offset2 + 0] + output[offset2 + 3];
            int b12 = output[offset2 + 1] + output[offset2 + 2];
            int c12 = output[offset2 + 1] - output[offset2 + 2];
            int d12 = output[offset2 + 0] - output[offset2 + 3];
            int a2 = a12 + b12;
            int b2 = c12 + d12;
            int c2 = a12 - b12;
            int d2 = d12 - c12;
            output[offset2 + 0] = (a2 + 3) >> 3;
            output[offset2 + 1] = (b2 + 3) >> 3;
            output[offset2 + 2] = (c2 + 3) >> 3;
            output[offset2 + 3] = (d2 + 3) >> 3;
            diff[0][i2] = (a2 + 3) >> 3;
            diff[1][i2] = (b2 + 3) >> 3;
            diff[2][i2] = (c2 + 3) >> 3;
            diff[3][i2] = (d2 + 3) >> 3;
            offset2 += 4;
        }
        return output;
    }

    public static int[] encodeWHT(int[] input) {
        int inputOffset = 0;
        int outputOffset = 0;
        int[] output = new int[input.length];
        for (int i = 0; i < 4; i++) {
            int a1 = (input[inputOffset + 0] + input[inputOffset + 2]) << 2;
            int d1 = (input[inputOffset + 1] + input[inputOffset + 3]) << 2;
            int c1 = (input[inputOffset + 1] - input[inputOffset + 3]) << 2;
            int b1 = (input[inputOffset + 0] - input[inputOffset + 2]) << 2;
            output[outputOffset + 0] = (a1 != 0 ? 1 : 0) + a1 + d1;
            output[outputOffset + 1] = b1 + c1;
            output[outputOffset + 2] = b1 - c1;
            output[outputOffset + 3] = a1 - d1;
            inputOffset += 4;
            outputOffset += 4;
        }
        int inputOffset2 = 0;
        int outputOffset2 = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            int a12 = output[inputOffset2 + 0] + output[inputOffset2 + 8];
            int d12 = output[inputOffset2 + 4] + output[inputOffset2 + 12];
            int c12 = output[inputOffset2 + 4] - output[inputOffset2 + 12];
            int b12 = output[inputOffset2 + 0] - output[inputOffset2 + 8];
            int a2 = a12 + d12;
            int b2 = b12 + c12;
            int c2 = b12 - c12;
            int d2 = a12 - d12;
            int a22 = a2 + (a2 < 0 ? 1 : 0);
            int b22 = b2 + (b2 < 0 ? 1 : 0);
            int c22 = c2 + (c2 < 0 ? 1 : 0);
            int d22 = d2 + (d2 < 0 ? 1 : 0);
            output[outputOffset2 + 0] = (a22 + 3) >> 3;
            output[outputOffset2 + 4] = (b22 + 3) >> 3;
            output[outputOffset2 + 8] = (c22 + 3) >> 3;
            output[outputOffset2 + 12] = (d22 + 3) >> 3;
            inputOffset2++;
            outputOffset2++;
        }
        return output;
    }
}
