package com.instabug.library.annotation.p019a;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/* compiled from: Utility.java */
/* renamed from: com.instabug.library.annotation.a.f */
/* loaded from: classes.dex */
public class C0614f {

    /* renamed from: a */
    private static final RectF f714a = new RectF(0.0f, 0.0f, 28.0f, 28.0f);

    /* renamed from: a */
    public static Path m1081a(Path path) {
        Path path2 = new Path(path);
        RectF rectF = new RectF();
        path2.computeBounds(rectF, true);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(rectF, f714a, Matrix.ScaleToFit.CENTER);
        path2.transform(matrix);
        return path2;
    }

    /* renamed from: a */
    public static Path m1082a(Path path, int i) {
        Path path2 = new Path(path);
        Matrix matrix = new Matrix();
        matrix.postRotate(i, 14.0f, 14.0f);
        path2.transform(matrix);
        return m1081a(path2);
    }
}
