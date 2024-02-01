package com.instabug.survey.p033b;

/* compiled from: PerSessionSettings.java */
/* renamed from: com.instabug.survey.b.a */
/* loaded from: classes.dex */
public class C0770a {

    /* renamed from: a */
    private static C0770a f1349a;

    /* renamed from: d */
    private Runnable f1352d;

    /* renamed from: e */
    private Runnable f1353e;

    /* renamed from: c */
    private boolean f1351c = true;

    /* renamed from: b */
    private StringBuilder f1350b = new StringBuilder();

    private C0770a() {
    }

    /* renamed from: a */
    public static void m1966a() {
        f1349a = new C0770a();
    }

    /* renamed from: b */
    public static C0770a m1967b() {
        return f1349a;
    }

    /* renamed from: a */
    public void m1969a(boolean z) {
        this.f1351c = z;
    }

    /* renamed from: c */
    public boolean m1971c() {
        return this.f1351c;
    }

    /* renamed from: a */
    public void m1968a(Runnable runnable) {
        this.f1352d = runnable;
    }

    /* renamed from: d */
    public Runnable m1972d() {
        return this.f1352d;
    }

    /* renamed from: b */
    public void m1970b(Runnable runnable) {
        this.f1353e = runnable;
    }

    /* renamed from: e */
    public Runnable m1973e() {
        return this.f1353e;
    }
}
