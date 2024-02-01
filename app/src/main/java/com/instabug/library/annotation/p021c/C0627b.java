package com.instabug.library.annotation.p021c;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

/* compiled from: DrawingUtility.java */
/* renamed from: com.instabug.library.annotation.c.b */
/* loaded from: classes.dex */
public class C0627b {
    /* renamed from: a */
    public static void m1148a(Canvas canvas, PointF pointF, PointF pointF2, Paint paint) {
        canvas.drawLine(pointF.x, pointF.y, pointF2.x, pointF2.y, paint);
    }

    /* renamed from: a */
    public static float m1142a(float f, float f2, float f3, float f4) {
        return (float) Math.toDegrees(Math.atan2(f2 - f4, f - f3));
    }

    /* renamed from: a */
    public static void m1147a(float f, float f2, PointF pointF, PointF pointF2) {
        pointF2.x = ((float) (f * Math.cos((f2 * 3.141592653589793d) / 180.0d))) + pointF.x;
        pointF2.y = ((float) (f * Math.sin((f2 * 3.141592653589793d) / 180.0d))) + pointF.y;
    }

    /* renamed from: a */
    public static PointF m1144a(float f, float f2, PointF pointF) {
        PointF pointF2 = new PointF();
        m1147a(f, f2, pointF, pointF2);
        return pointF2;
    }

    /* renamed from: a */
    public static PointF m1143a(float f, float f2, float f3, PointF pointF) {
        double sin = Math.sin((f3 * 3.141592653589793d) / 180.0d);
        double cos = Math.cos((f3 * 3.141592653589793d) / 180.0d);
        pointF.x -= f;
        pointF.y -= f2;
        double d = (pointF.x * cos) - (pointF.y * sin);
        pointF.x = ((float) d) + f;
        pointF.y = ((float) ((sin * pointF.x) + (cos * pointF.y))) + f2;
        return pointF;
    }

    /* renamed from: a */
    public static double m1141a(double[] dArr, double[] dArr2, double[] dArr3) {
        double[] dArr4 = {dArr2[0] - dArr[0], dArr2[1] - dArr[1]};
        double[] dArr5 = {dArr3[0] - dArr[0], dArr3[1] - dArr[1]};
        return (dArr4[0] * dArr5[1]) - (dArr5[0] * dArr4[1]);
    }

    /* renamed from: a */
    public static double m1140a(double[] dArr, double[] dArr2) {
        double d = dArr[0] - dArr2[0];
        double d2 = dArr[1] - dArr2[1];
        return Math.sqrt((d * d) + (d2 * d2));
    }

    /* renamed from: b */
    public static double m1149b(double[] dArr, double[] dArr2, double[] dArr3) {
        return Math.abs(m1141a(dArr, dArr2, dArr3) / m1140a(dArr, dArr2));
    }

    /* renamed from: a */
    public static double m1139a(PointF pointF, PointF pointF2, PointF pointF3) {
        return m1149b(new double[]{pointF.x, pointF.y}, new double[]{pointF2.x, pointF2.y}, new double[]{pointF3.x, pointF3.y});
    }

    /* renamed from: a */
    public static PointF m1145a(PointF pointF, PointF pointF2) {
        PointF pointF3 = new PointF();
        pointF3.x = (pointF.x + pointF2.x) / 2.0f;
        pointF3.y = (pointF.y + pointF2.y) / 2.0f;
        return pointF3;
    }

    /* renamed from: a */
    public static List<PointF> m1146a(Path path) {
        ArrayList arrayList = new ArrayList();
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        float[] fArr = new float[2];
        for (int i = 0; i < length; i++) {
            pathMeasure.getPosTan(i, fArr, null);
            arrayList.add(new PointF(fArr[0], fArr[1]));
        }
        return arrayList;
    }
}
