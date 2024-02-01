package org.jcodec.codecs.h264.io.model;

import android.support.v4.view.InputDeviceCompat;
import org.jcodec.codecs.h264.decode.CAVLCReader;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class ScalingList {
    public int[] scalingList;
    public boolean useDefaultScalingMatrixFlag;

    public void write(BitWriter out) {
        if (this.useDefaultScalingMatrixFlag) {
            CAVLCWriter.writeSE(out, 0, "SPS: ");
            return;
        }
        int lastScale = 8;
        for (int j = 0; j < this.scalingList.length; j++) {
            if (8 != 0) {
                int deltaScale = (this.scalingList[j] - lastScale) + InputDeviceCompat.SOURCE_ANY;
                CAVLCWriter.writeSE(out, deltaScale, "SPS: ");
            }
            lastScale = this.scalingList[j];
        }
    }

    public static ScalingList read(BitReader in, int sizeOfScalingList) {
        ScalingList sl = new ScalingList();
        sl.scalingList = new int[sizeOfScalingList];
        int lastScale = 8;
        int nextScale = 8;
        int j = 0;
        while (j < sizeOfScalingList) {
            if (nextScale != 0) {
                int deltaScale = CAVLCReader.readSE(in, "deltaScale");
                nextScale = ((lastScale + deltaScale) + 256) % 256;
                sl.useDefaultScalingMatrixFlag = j == 0 && nextScale == 0;
            }
            int[] iArr = sl.scalingList;
            if (nextScale != 0) {
                lastScale = nextScale;
            }
            iArr[j] = lastScale;
            lastScale = sl.scalingList[j];
            j++;
        }
        return sl;
    }
}
