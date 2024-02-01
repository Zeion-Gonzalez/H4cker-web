package com.instabug.crash.p016b;

import android.content.Context;

/* compiled from: CrashSettings.java */
/* renamed from: com.instabug.crash.b.a */
/* loaded from: classes.dex */
public class C0561a {

    /* renamed from: a */
    private static C0561a f534a;

    private C0561a() {
    }

    /* renamed from: a */
    public static void m920a(Context context) {
        f534a = new C0561a();
        C0563c.m930a(context);
        C0562b.m925a();
    }

    /* renamed from: a */
    public static C0561a m919a() {
        if (f534a == null) {
            f534a = new C0561a();
        }
        return f534a;
    }

    /* renamed from: b */
    public Runnable m923b() {
        return C0562b.m926b().m928c();
    }

    /* renamed from: a */
    public void m922a(Runnable runnable) {
        C0562b.m926b().m927a(runnable);
    }

    /* renamed from: a */
    public void m921a(long j) {
        C0563c.m929a().m931a(j);
    }

    /* renamed from: c */
    public long m924c() {
        return C0563c.m929a().m932b();
    }
}
