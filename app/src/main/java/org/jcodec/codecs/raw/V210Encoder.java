package org.jcodec.codecs.raw;

import android.support.v4.view.PointerIconCompat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class V210Encoder {
    public ByteBuffer encodeFrame(ByteBuffer _out, Picture frame) throws IOException {
        ByteBuffer out = _out.duplicate();
        out.order(ByteOrder.LITTLE_ENDIAN);
        int tgtStride = ((frame.getPlaneWidth(0) + 47) / 48) * 48;
        int[][] data = frame.getData();
        int[] tmpY = new int[tgtStride];
        int[] tmpCb = new int[tgtStride >> 1];
        int[] tmpCr = new int[tgtStride >> 1];
        int yOff = 0;
        int cbOff = 0;
        int crOff = 0;
        for (int yy = 0; yy < frame.getHeight(); yy++) {
            System.arraycopy(data[0], yOff, tmpY, 0, frame.getPlaneWidth(0));
            System.arraycopy(data[1], cbOff, tmpCb, 0, frame.getPlaneWidth(1));
            System.arraycopy(data[2], crOff, tmpCr, 0, frame.getPlaneWidth(2));
            int cri = 0;
            int cbi = 0;
            int yi = 0;
            while (yi < tgtStride) {
                int cri2 = cri + 1;
                int i = 0 | (clip(tmpCr[cri]) << 20);
                int yi2 = yi + 1;
                int cbi2 = cbi + 1;
                out.putInt(i | (clip(tmpY[yi]) << 10) | clip(tmpCb[cbi]));
                int yi3 = yi2 + 1;
                int i2 = 0 | clip(tmpY[yi2]);
                int yi4 = yi3 + 1;
                int cbi3 = cbi2 + 1;
                out.putInt(i2 | (clip(tmpY[yi3]) << 20) | (clip(tmpCb[cbi2]) << 10));
                int i3 = 0 | (clip(tmpCb[cbi3]) << 20);
                int yi5 = yi4 + 1;
                int cri3 = cri2 + 1;
                out.putInt(i3 | (clip(tmpY[yi4]) << 10) | clip(tmpCr[cri2]));
                int yi6 = yi5 + 1;
                int i4 = 0 | clip(tmpY[yi5]);
                yi = yi6 + 1;
                out.putInt(i4 | (clip(tmpY[yi6]) << 20) | (clip(tmpCr[cri3]) << 10));
                cri = cri3 + 1;
                cbi = cbi3 + 1;
            }
            yOff += frame.getPlaneWidth(0);
            cbOff += frame.getPlaneWidth(1);
            crOff += frame.getPlaneWidth(2);
        }
        out.flip();
        return out;
    }

    static final int clip(int val) {
        return MathUtil.clip(val, 8, PointerIconCompat.TYPE_ZOOM_OUT);
    }
}
