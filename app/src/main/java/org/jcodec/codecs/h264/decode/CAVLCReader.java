package org.jcodec.codecs.h264.decode;

import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.tools.Debug;

/* loaded from: classes.dex */
public class CAVLCReader {
    private CAVLCReader() {
    }

    public static int readNBit(BitReader bits, int n, String message) {
        int val = bits.readNBit(n);
        Debug.trace(message, Integer.valueOf(val));
        return val;
    }

    public static int readUE(BitReader bits) {
        int cnt = 0;
        while (bits.read1Bit() == 0 && cnt < 31) {
            cnt++;
        }
        if (cnt <= 0) {
            return 0;
        }
        long val = bits.readNBit(cnt);
        int res = (int) (((long) ((1 << cnt) - 1)) + val);
        return res;
    }

    public static int readUE(BitReader bits, String message) {
        int res = readUE(bits);
        Debug.trace(message, Integer.valueOf(res));
        return res;
    }

    public static int readSE(BitReader bits, String message) {
        int val = H264Utils.golomb2Signed(readUE(bits));
        Debug.trace(message, Integer.valueOf(val));
        return val;
    }

    public static boolean readBool(BitReader bits, String message) {
        boolean res = bits.read1Bit() != 0;
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(res ? 1 : 0);
        Debug.trace(message, objArr);
        return res;
    }

    public static int readU(BitReader bits, int i, String string) {
        return readNBit(bits, i, string);
    }

    public static int readTE(BitReader bits, int max) {
        return max > 1 ? readUE(bits) : (bits.read1Bit() ^ (-1)) & 1;
    }

    public static int readME(BitReader bits, String string) {
        return readUE(bits, string);
    }

    public static int readZeroBitCount(BitReader bits, String message) {
        int count = 0;
        while (bits.read1Bit() == 0 && count < 32) {
            count++;
        }
        Debug.trace(message, String.valueOf(count));
        return count;
    }

    public static boolean moreRBSPData(BitReader bits) {
        return (bits.remaining() < 32 && bits.checkNBit(1) == 1 && (bits.checkNBit(24) << 9) == 0) ? false : true;
    }
}
