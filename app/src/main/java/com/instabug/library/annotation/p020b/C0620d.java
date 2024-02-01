package com.instabug.library.annotation.p020b;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import com.instabug.library.annotation.C0616b;
import com.instabug.library.annotation.p021c.C0627b;

/* compiled from: OvalShape.java */
/* renamed from: com.instabug.library.annotation.b.d */
/* loaded from: classes.dex */
public class C0620d extends C0622f {
    public C0620d(@ColorInt int i, float f, int i2) {
        super(i, f, i2);
    }

    @Override // com.instabug.library.annotation.p020b.C0622f
    /* renamed from: a */
    protected void mo1107a(Canvas canvas, C0616b c0616b) {
        m1106b(canvas, c0616b);
    }

    /* renamed from: b */
    private void m1106b(Canvas canvas, C0616b c0616b) {
        canvas.drawPath(mo1095a(c0616b), this.f761d);
    }

    @Override // com.instabug.library.annotation.p020b.C0622f, com.instabug.library.annotation.p020b.AbstractC0623g
    /* renamed from: a */
    public void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
        C0627b.m1148a(canvas, pointF, pointF2, this.f760c);
        C0627b.m1148a(canvas, pointF, pointF4, this.f760c);
        C0627b.m1148a(canvas, pointF2, pointF3, this.f760c);
        C0627b.m1148a(canvas, pointF3, pointF4, this.f760c);
    }

    @Override // com.instabug.library.annotation.p020b.C0622f
    /* renamed from: b */
    protected void mo1108b(C0616b c0616b) {
        this.f759b.reset();
        if (this.f758a == 0 || this.f758a == 180) {
            this.f759b.addOval(c0616b, Path.Direction.CW);
            return;
        }
        PointF m1145a = C0627b.m1145a(c0616b.f736e, c0616b.f737f);
        PointF m1145a2 = C0627b.m1145a(c0616b.f736e, m1145a);
        PointF m1145a3 = C0627b.m1145a(c0616b.f737f, m1145a);
        PointF m1145a4 = C0627b.m1145a(c0616b.f737f, c0616b.f738g);
        PointF m1145a5 = C0627b.m1145a(c0616b.f737f, m1145a4);
        PointF m1145a6 = C0627b.m1145a(c0616b.f738g, m1145a4);
        PointF m1145a7 = C0627b.m1145a(c0616b.f738g, c0616b.f739h);
        PointF m1145a8 = C0627b.m1145a(c0616b.f738g, m1145a7);
        PointF m1145a9 = C0627b.m1145a(c0616b.f739h, m1145a7);
        PointF m1145a10 = C0627b.m1145a(c0616b.f739h, c0616b.f736e);
        PointF m1145a11 = C0627b.m1145a(c0616b.f739h, m1145a10);
        PointF m1145a12 = C0627b.m1145a(c0616b.f736e, m1145a10);
        this.f759b.moveTo(m1145a.x, m1145a.y);
        this.f759b.cubicTo(m1145a3.x, m1145a3.y, m1145a5.x, m1145a5.y, m1145a4.x, m1145a4.y);
        this.f759b.cubicTo(m1145a6.x, m1145a6.y, m1145a8.x, m1145a8.y, m1145a7.x, m1145a7.y);
        this.f759b.cubicTo(m1145a9.x, m1145a9.y, m1145a11.x, m1145a11.y, m1145a10.x, m1145a10.y);
        this.f759b.cubicTo(m1145a12.x, m1145a12.y, m1145a2.x, m1145a2.y, m1145a.x, m1145a.y);
        this.f759b.close();
    }
}
