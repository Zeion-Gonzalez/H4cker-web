package com.instabug.library.annotation.p020b;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import com.instabug.library.annotation.C0608a;
import com.instabug.library.annotation.C0616b;

/* compiled from: Shape.java */
/* renamed from: com.instabug.library.annotation.b.g */
/* loaded from: classes.dex */
public abstract class AbstractC0623g {

    /* renamed from: c */
    protected final Paint f760c;

    /* renamed from: d */
    protected Paint f761d = new Paint(1);

    /* renamed from: a */
    public abstract Path mo1095a(C0616b c0616b);

    /* renamed from: a */
    public abstract void mo1097a(Canvas canvas, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4);

    /* renamed from: a */
    public abstract void mo1098a(Canvas canvas, C0616b c0616b, C0616b c0616b2);

    /* renamed from: a */
    public abstract void mo1100a(C0616b c0616b, C0616b c0616b2, int i, int i2);

    /* renamed from: a */
    public abstract void mo1101a(C0616b c0616b, C0616b c0616b2, boolean z);

    /* renamed from: a */
    public abstract boolean mo1103a(PointF pointF, C0616b c0616b);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0623g(@ColorInt int i, float f) {
        this.f761d.setColor(i);
        this.f761d.setStyle(Paint.Style.STROKE);
        this.f761d.setStrokeWidth(f);
        this.f761d.setStrokeCap(Paint.Cap.ROUND);
        this.f760c = new Paint(1);
        this.f760c.setColor(Integer.MIN_VALUE);
    }

    /* renamed from: a */
    public float m1120a() {
        return this.f761d.getStrokeWidth();
    }

    /* renamed from: a */
    public void mo1099a(Canvas canvas, C0616b c0616b, C0608a[] c0608aArr) {
        int color = this.f761d.getColor();
        PointF[] m1092e = c0616b.m1092e();
        for (int i = 0; i < c0608aArr.length; i++) {
            c0608aArr[i].m1069a(m1092e[i]);
            c0608aArr[i].m1067a(color);
            c0608aArr[i].m1068a(canvas);
        }
    }
}
