package com.instabug.library.annotation;

import android.graphics.Canvas;
import android.graphics.PointF;
import com.instabug.library.annotation.p020b.AbstractC0623g;
import java.util.Stack;

/* compiled from: MarkUpDrawable.java */
/* renamed from: com.instabug.library.annotation.c */
/* loaded from: classes.dex */
public class C0625c {

    /* renamed from: a */
    protected AbstractC0623g f766a;

    /* renamed from: b */
    protected AbstractC0623g f767b;

    /* renamed from: c */
    protected C0616b f768c = new C0616b();

    /* renamed from: d */
    protected C0616b f769d = new C0616b();

    /* renamed from: e */
    private Stack<C0616b> f770e = new Stack<>();

    public C0625c(AbstractC0623g abstractC0623g) {
        this.f766a = abstractC0623g;
        this.f767b = abstractC0623g;
    }

    /* renamed from: a */
    public void m1128a(C0616b c0616b) {
        this.f768c = c0616b;
        this.f769d.m1087a(c0616b);
    }

    /* renamed from: a */
    public boolean m1130a(PointF pointF) {
        if (this.f769d.m1093f()) {
            return this.f766a.mo1103a(pointF, this.f768c);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: a */
    public void m1125a(Canvas canvas) {
        if (this.f769d.m1093f()) {
            canvas.save();
            this.f766a.mo1098a(canvas, this.f768c, this.f769d);
            canvas.restore();
        }
    }

    /* renamed from: c */
    private void m1122c(C0616b c0616b) {
        if (this.f769d != null) {
            this.f770e.push(new C0616b(this.f769d));
        }
        this.f769d = c0616b;
    }

    /* renamed from: a */
    public void m1123a() {
        m1122c(new C0616b(this.f768c));
    }

    /* renamed from: a */
    public void m1129a(boolean z) {
        C0616b c0616b = new C0616b(this.f768c);
        c0616b.m1088a(z);
        m1122c(c0616b);
    }

    /* renamed from: b */
    public boolean m1133b() {
        if (this.f770e.size() <= 0) {
            return false;
        }
        this.f769d = this.f770e.pop();
        if (this.f770e.size() == 0) {
            this.f766a = this.f767b;
        }
        this.f766a.mo1101a(this.f769d, this.f768c, true);
        return true;
    }

    /* renamed from: a */
    public void m1124a(int i, int i2) {
        this.f766a.mo1100a(this.f768c, this.f769d, i, i2);
    }

    /* renamed from: c */
    public AbstractC0623g m1134c() {
        return this.f766a;
    }

    /* renamed from: b */
    public void m1132b(C0616b c0616b) {
        this.f766a.mo1101a(c0616b, this.f768c, false);
    }

    /* renamed from: a */
    public void m1126a(Canvas canvas, C0608a... c0608aArr) {
        this.f766a.mo1099a(canvas, this.f768c, c0608aArr);
    }

    /* renamed from: b */
    public void m1131b(Canvas canvas) {
        this.f766a.mo1097a(canvas, this.f768c.m1086a(), this.f768c.m1089b(), this.f768c.m1090c(), this.f768c.m1091d());
    }

    /* renamed from: a */
    public void m1127a(AbstractC0623g abstractC0623g, C0616b c0616b) {
        m1122c(new C0616b(c0616b));
        this.f766a = abstractC0623g;
    }

    /* renamed from: d */
    public boolean m1135d() {
        return this.f769d.m1093f();
    }
}
