package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv422jToRgb implements Transform {
    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] y = src.getPlaneData(0);
        int[] u = src.getPlaneData(1);
        int[] v = src.getPlaneData(2);
        int[] data = dst.getPlaneData(0);
        int offLuma = 0;
        int offChroma = 0;
        for (int i = 0; i < dst.getHeight(); i++) {
            for (int j = 0; j < dst.getWidth(); j += 2) {
                Yuv420jToRgb.YUVJtoRGB(y[offLuma], u[offChroma], v[offChroma], data, offLuma * 3);
                Yuv420jToRgb.YUVJtoRGB(y[offLuma + 1], u[offChroma], v[offChroma], data, (offLuma + 1) * 3);
                offLuma += 2;
                offChroma++;
            }
        }
    }
}
