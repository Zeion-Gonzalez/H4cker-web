package com.instabug.crash.p016b;

import android.content.Context;
import android.content.SharedPreferences;

/* compiled from: PersistableSettings.java */
/* renamed from: com.instabug.crash.b.c */
/* loaded from: classes.dex */
public class C0563c {

    /* renamed from: b */
    private static C0563c f537b;

    /* renamed from: a */
    private SharedPreferences f538a;

    private C0563c(Context context) {
        this.f538a = context.getSharedPreferences("instabug_crash", 0);
    }

    /* renamed from: a */
    public static void m930a(Context context) {
        f537b = new C0563c(context);
    }

    /* renamed from: a */
    public static C0563c m929a() {
        return f537b;
    }

    /* renamed from: a */
    public void m931a(long j) {
        this.f538a.edit().putLong("last_crash_time", j).apply();
    }

    /* renamed from: b */
    public long m932b() {
        return this.f538a.getLong("last_crash_time", 0L);
    }
}
