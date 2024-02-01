package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv420jToYuv420 implements Transform {
    public static int Y_COEFF = 7168;

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int[] sy = src.getPlaneData(0);
        int[] dy = dst.getPlaneData(0);
        for (int i = 0; i < src.getPlaneWidth(0) * src.getPlaneHeight(0); i++) {
            dy[i] = ((sy[i] * Y_COEFF) >> 13) + 16;
        }
        int[] su = src.getPlaneData(1);
        int[] du = dst.getPlaneData(1);
        for (int i2 = 0; i2 < src.getPlaneWidth(1) * src.getPlaneHeight(1); i2++) {
            du[i2] = (((su[i2] - 128) * Y_COEFF) >> 13) + 128;
        }
        int[] sv = src.getPlaneData(2);
        int[] dv = dst.getPlaneData(2);
        for (int i3 = 0; i3 < src.getPlaneWidth(2) * src.getPlaneHeight(2); i3++) {
            dv[i3] = (((sv[i3] - 128) * Y_COEFF) >> 13) + 128;
        }
    }
}
