package com.instabug.library.annotation;

import android.graphics.PointF;
import android.graphics.RectF;

/* compiled from: DirectionRectF.java */
/* renamed from: com.instabug.library.annotation.b */
/* loaded from: classes.dex */
public class C0616b extends RectF {

    /* renamed from: a */
    public a f732a;

    /* renamed from: b */
    public b f733b;

    /* renamed from: c */
    public a f734c;

    /* renamed from: d */
    public b f735d;

    /* renamed from: e */
    public PointF f736e;

    /* renamed from: f */
    public PointF f737f;

    /* renamed from: g */
    public PointF f738g;

    /* renamed from: h */
    public PointF f739h;

    /* renamed from: i */
    public boolean f740i;

    /* renamed from: j */
    private boolean f741j;

    /* compiled from: DirectionRectF.java */
    /* renamed from: com.instabug.library.annotation.b$a */
    /* loaded from: classes.dex */
    public enum a {
        LEFT,
        RIGHT
    }

    /* compiled from: DirectionRectF.java */
    /* renamed from: com.instabug.library.annotation.b$b */
    /* loaded from: classes.dex */
    public enum b {
        TOP,
        BOTTOM
    }

    public C0616b() {
        this.f736e = new PointF();
        this.f737f = new PointF();
        this.f738g = new PointF();
        this.f739h = new PointF();
        this.f740i = false;
        this.f741j = true;
    }

    public C0616b(C0616b c0616b) {
        this.f736e = new PointF();
        this.f737f = new PointF();
        this.f738g = new PointF();
        this.f739h = new PointF();
        this.f740i = false;
        this.f741j = true;
        m1085b(c0616b);
    }

    public C0616b(float f, float f2, float f3, float f4) {
        super(f, f2, f3, f4);
        this.f736e = new PointF();
        this.f737f = new PointF();
        this.f738g = new PointF();
        this.f739h = new PointF();
        this.f740i = false;
        this.f741j = true;
    }

    /* renamed from: a */
    public void m1087a(C0616b c0616b) {
        m1085b(c0616b);
    }

    /* renamed from: b */
    private void m1085b(C0616b c0616b) {
        if (c0616b == null) {
            this.bottom = 0.0f;
            this.right = 0.0f;
            this.top = 0.0f;
            this.left = 0.0f;
            this.f734c = null;
            this.f732a = null;
            this.f735d = null;
            this.f733b = null;
            this.f736e.set(0.0f, 0.0f);
            this.f737f.set(0.0f, 0.0f);
            this.f738g.set(0.0f, 0.0f);
            this.f739h.set(0.0f, 0.0f);
            this.f740i = false;
            this.f741j = true;
            return;
        }
        this.left = c0616b.left;
        this.top = c0616b.top;
        this.right = c0616b.right;
        this.bottom = c0616b.bottom;
        this.f732a = c0616b.f732a;
        this.f733b = c0616b.f733b;
        this.f734c = c0616b.f734c;
        this.f735d = c0616b.f735d;
        this.f736e.set(c0616b.f736e);
        this.f737f.set(c0616b.f737f);
        this.f738g.set(c0616b.f738g);
        this.f739h.set(c0616b.f739h);
        this.f740i = c0616b.f740i;
        this.f741j = c0616b.m1093f();
    }

    /* renamed from: a */
    public PointF m1086a() {
        return new PointF(this.left, this.top);
    }

    /* renamed from: b */
    public PointF m1089b() {
        return new PointF(this.right, this.top);
    }

    /* renamed from: c */
    public PointF m1090c() {
        return new PointF(this.right, this.bottom);
    }

    /* renamed from: d */
    public PointF m1091d() {
        return new PointF(this.left, this.bottom);
    }

    /* renamed from: e */
    public PointF[] m1092e() {
        return new PointF[]{m1086a(), m1089b(), m1090c(), m1091d()};
    }

    /* renamed from: f */
    public boolean m1093f() {
        return this.f741j;
    }

    /* renamed from: a */
    public void m1088a(boolean z) {
        this.f741j = z;
    }
}
