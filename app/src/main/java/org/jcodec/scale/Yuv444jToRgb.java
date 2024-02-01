package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv444jToRgb implements Transform {
    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] y = src.getPlaneData(0);
        int[] u = src.getPlaneData(1);
        int[] v = src.getPlaneData(2);
        int[] data = dst.getPlaneData(0);
        int srcOff = 0;
        int dstOff = 0;
        for (int i = 0; i < dst.getHeight(); i++) {
            int j = 0;
            while (j < dst.getWidth()) {
                Yuv420jToRgb.YUVJtoRGB(y[srcOff], u[srcOff], v[srcOff], data, dstOff);
                j++;
                srcOff++;
                dstOff += 3;
            }
        }
    }
}
