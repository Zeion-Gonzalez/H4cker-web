package com.instabug.library.annotation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/* compiled from: ControlButton.java */
/* renamed from: com.instabug.library.annotation.a */
/* loaded from: classes.dex */
public class C0608a {

    /* renamed from: c */
    private int f692c;

    /* renamed from: b */
    private PointF f691b = new PointF();

    /* renamed from: a */
    private final Paint f690a = new Paint(1);

    /* renamed from: a */
    public void m1067a(int i) {
        this.f692c = i;
    }

    /* renamed from: a */
    public void m1069a(PointF pointF) {
        this.f691b = pointF;
    }

    /* renamed from: a */
    public void m1066a(float f, float f2) {
        this.f691b = new PointF(f, f2);
    }

    /* renamed from: b */
    public boolean m1070b(PointF pointF) {
        float f = this.f691b.x - pointF.x;
        float f2 = this.f691b.y - pointF.y;
        return (f * f) + (f2 * f2) <= 1764.0f;
    }

    /* renamed from: a */
    public void m1068a(Canvas canvas) {
        this.f690a.setColor(this.f692c);
        this.f690a.setStyle(Paint.Style.FILL);
        canvas.drawCircle(this.f691b.x, this.f691b.y, 12.0f, this.f690a);
        this.f690a.setColor(-1);
        this.f690a.setStyle(Paint.Style.STROKE);
        this.f690a.setStrokeWidth(2.0f);
        canvas.drawCircle(this.f691b.x, this.f691b.y, 12.0f, this.f690a);
    }
}
