package com.instabug.library.internal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.TypedValue;
import android.widget.TextView;
import com.instabug.library.annotation.p021c.C0627b;

/* compiled from: BubbleTextView.java */
/* renamed from: com.instabug.library.internal.view.a */
/* loaded from: classes.dex */
public class C0692a extends TextView {

    /* renamed from: a */
    private RectF f925a;

    /* renamed from: b */
    private Paint f926b;

    /* renamed from: c */
    private float f927c;

    /* renamed from: d */
    private PointF f928d;

    /* renamed from: e */
    private PointF f929e;

    /* renamed from: f */
    private PointF f930f;

    /* renamed from: g */
    private Path f931g;

    /* renamed from: h */
    private float f932h;

    public C0692a(Context context) {
        super(context);
        m1383a();
    }

    /* renamed from: a */
    private void m1383a() {
        setTextColor(-1);
        this.f925a = new RectF();
        this.f926b = new Paint(1);
        this.f926b.setColor(-12303292);
        this.f926b.setStyle(Paint.Style.FILL);
        this.f927c = TypedValue.applyDimension(1, 7.0f, getContext().getResources().getDisplayMetrics());
        this.f932h = this.f927c / 2.0f;
        int i = (int) this.f927c;
        setPadding((int) (this.f927c * 1.5d), i, (int) ((this.f927c * 1.5d) + this.f927c), i);
        this.f928d = new PointF();
        this.f929e = new PointF();
        this.f930f = new PointF();
        this.f931g = new Path();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, measuredHeight);
        this.f925a.right = measuredWidth - this.f927c;
        this.f925a.bottom = measuredHeight;
        this.f928d.x = measuredWidth;
        this.f928d.y = this.f925a.centerY();
        float cos = (float) (this.f927c / Math.cos(45.0d));
        C0627b.m1147a(cos, 225.0f, this.f928d, this.f929e);
        C0627b.m1147a(cos, 135.0f, this.f928d, this.f930f);
        this.f931g.moveTo(this.f929e.x, this.f929e.y);
        this.f931g.lineTo(this.f928d.x, this.f928d.y);
        this.f931g.lineTo(this.f930f.x, this.f930f.y);
        this.f931g.close();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(this.f925a, this.f932h, this.f932h, this.f926b);
        canvas.drawPath(this.f931g, this.f926b);
        super.onDraw(canvas);
    }
}
