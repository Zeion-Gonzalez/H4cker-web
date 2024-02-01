package com.instabug.library.internal.storage.cache.p028a;

import android.database.sqlite.SQLiteOpenHelper;

/* compiled from: DatabaseManager.java */
/* renamed from: com.instabug.library.internal.storage.cache.a.a */
/* loaded from: classes.dex */
public class C0672a {

    /* renamed from: a */
    private static C0672a f851a;

    /* renamed from: b */
    private static C0674c f852b;

    /* renamed from: a */
    public static synchronized void m1310a(SQLiteOpenHelper sQLiteOpenHelper) {
        synchronized (C0672a.class) {
            if (f851a == null) {
                f851a = new C0672a();
                f852b = new C0674c(sQLiteOpenHelper);
            }
        }
    }

    /* renamed from: a */
    public static synchronized C0672a m1309a() {
        C0672a c0672a;
        synchronized (C0672a.class) {
            if (f851a == null) {
                throw new IllegalStateException(C0672a.class.getSimpleName() + " is not initialized, call init(..) method first.");
            }
            c0672a = f851a;
        }
        return c0672a;
    }

    /* renamed from: b */
    public C0674c m1311b() {
        f852b.m1318a();
        return f852b;
    }
}
