package com.instabug.library.annotation.p020b;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0627b;
import java.util.List;

/* compiled from: PathShape.java */
/* renamed from: com.instabug.library.annotation.b.e */
/* loaded from: classes.dex */
public class C0621e extends C0622f {

    /* renamed from: e */
    private final RectF f753e;

    /* renamed from: f */
    private final Paint f754f;

    /* renamed from: g */
    private Path f755g;

    /* renamed from: h */
    private Matrix f756h;

    /* renamed from: i */
    private List<PointF> f757i;

    public C0621e(Path path, float f, Paint paint, List<PointF> list) {
        super(paint.getColor(), f, 0);
        this.f755g = path;
        this.f754f = new Paint(paint);
        this.f757i = list;
        this.f753e = new RectF();
        path.computeBounds(this.f753e, true);
        this.f756h = new Matrix();
    }

    @Override // com.instabug.library.annotation.p020b.C0622f, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1098a(Canvas canvas, C0616b c0616b, C0616b c0616b2) {
        this.f756h.reset();
        Path path = new Path(this.f755g);
        this.f756h.setRectToRect(this.f753e, new RectF(c0616b), Matrix.ScaleToFit.FILL);
        path.transform(this.f756h);
        canvas.drawPath(path, this.f754f);
    }

    @Override // com.instabug.library.annotation.p020b.C0622f, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public boolean mo1103a(PointF pointF, C0616b c0616b) {
        RectF rectF = new RectF(pointF.x - 50.0f, pointF.y - 50.0f, pointF.x + 50.0f, pointF.y + 50.0f);
        for (PointF pointF2 : this.f757i) {
            float[] fArr = {pointF2.x, pointF2.y};
            float[] fArr2 = new float[2];
            this.f756h.mapPoints(fArr2, fArr);
            if (rectF.contains(fArr2[0], fArr2[1])) {
                return true;
            }
        }
        return false;
    }

    @Override // com.instabug.library.annotation.p020b.C0622f, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z) {
        if (Math.abs(c0616b2.width() - c0616b.width()) < 1.0f && Math.abs(c0616b2.height() - c0616b.height()) < 1.0f) {
            c0616b2.m1087a(c0616b);
            return;
        }
        float max = Math.max(c0616b.width() / c0616b2.width(), c0616b.height() / c0616b2.height());
        Matrix matrix = new Matrix();
        matrix.postScale(max, max, c0616b2.centerX(), c0616b2.centerY());
        matrix.mapRect(c0616b2);
    }

    @Override // com.instabug.library.annotation.p020b.C0622f, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
        C0627b.m1148a(canvas, pointF, pointF2, this.f760c);
        C0627b.m1148a(canvas, pointF, pointF4, this.f760c);
        C0627b.m1148a(canvas, pointF2, pointF3, this.f760c);
        C0627b.m1148a(canvas, pointF3, pointF4, this.f760c);
    }
}
