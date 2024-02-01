package com.instabug.library.internal.storage.cache.p028a;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.concurrent.atomic.AtomicInteger;

/* compiled from: SQLiteDatabaseWrapper.java */
/* renamed from: com.instabug.library.internal.storage.cache.a.c */
/* loaded from: classes.dex */
public class C0674c {

    /* renamed from: a */
    private AtomicInteger f853a = new AtomicInteger();

    /* renamed from: b */
    private SQLiteDatabase f854b;

    /* renamed from: c */
    private SQLiteOpenHelper f855c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0674c(SQLiteOpenHelper sQLiteOpenHelper) {
        this.f855c = sQLiteOpenHelper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public synchronized void m1318a() {
        if (this.f853a.incrementAndGet() == 1) {
            this.f854b = this.f855c.getWritableDatabase();
        }
    }

    /* renamed from: b */
    public synchronized void m1321b() {
        if (this.f853a.decrementAndGet() == 0) {
            this.f854b.close();
        }
    }

    /* renamed from: c */
    public void m1322c() {
        this.f854b.beginTransaction();
    }

    /* renamed from: a */
    public long m1314a(String str, String str2, ContentValues contentValues) {
        return this.f854b.insert(str, str2, contentValues);
    }

    /* renamed from: a */
    public void m1319a(String str) {
        this.f854b.execSQL(str);
    }

    /* renamed from: d */
    public void m1323d() {
        this.f854b.setTransactionSuccessful();
    }

    /* renamed from: e */
    public void m1324e() {
        this.f854b.endTransaction();
    }

    /* renamed from: a */
    public Cursor m1315a(String str, String[] strArr) {
        return this.f854b.rawQuery(str, strArr);
    }

    /* renamed from: a */
    public void m1320a(String str, String str2, String[] strArr) {
        this.f854b.delete(str, str2, strArr);
    }

    /* renamed from: a */
    public int m1313a(String str, ContentValues contentValues, String str2, String[] strArr) {
        return this.f854b.update(str, contentValues, str2, strArr);
    }

    /* renamed from: a */
    public Cursor m1316a(String str, String[] strArr, String str2, String[] strArr2, String str3, String str4, String str5) {
        return this.f854b.query(str, strArr, str2, strArr2, str3, str4, str5);
    }

    /* renamed from: a */
    public Cursor m1317a(String str, String[] strArr, String str2, String[] strArr2, String str3, String str4, String str5, String str6) {
        return this.f854b.query(str, strArr, str2, strArr2, str3, str4, str5, str6);
    }
}
