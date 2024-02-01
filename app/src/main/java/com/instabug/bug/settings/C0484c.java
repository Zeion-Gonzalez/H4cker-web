package com.instabug.bug.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/* compiled from: PersistableSettings.java */
/* renamed from: com.instabug.bug.settings.c */
/* loaded from: classes.dex */
public class C0484c {

    /* renamed from: c */
    private static C0484c f164c;

    /* renamed from: a */
    private SharedPreferences f165a;

    /* renamed from: b */
    private SharedPreferences.Editor f166b;

    private C0484c(Context context) {
        this.f165a = context.getSharedPreferences("instabug_bug_reporting", 0);
        this.f166b = this.f165a.edit();
    }

    /* renamed from: a */
    public static void m293a(Context context) {
        f164c = new C0484c(context);
    }

    /* renamed from: a */
    public static C0484c m292a() {
        return f164c;
    }

    /* renamed from: b */
    public boolean m299b() {
        return this.f165a.getBoolean("ib_bugreporting_is_email_required", true);
    }

    /* renamed from: a */
    public void m296a(boolean z) {
        this.f166b.putBoolean("ib_bugreporting_is_email_required", z);
        this.f166b.apply();
    }

    /* renamed from: c */
    public boolean m301c() {
        return this.f165a.getBoolean("ib_bugreporting_is_email_enabled", true);
    }

    /* renamed from: b */
    public void m298b(boolean z) {
        this.f166b.putBoolean("ib_bugreporting_is_email_enabled", z);
        this.f166b.apply();
    }

    /* renamed from: d */
    public long m302d() {
        return this.f165a.getLong("last_bug_time", 0L);
    }

    /* renamed from: a */
    public void m294a(long j) {
        this.f165a.edit().putLong("last_bug_time", j).apply();
    }

    /* renamed from: e */
    public boolean m303e() {
        return this.f165a.getBoolean("ib_bugreporting_success_dialog_enabled", true);
    }

    /* renamed from: c */
    public void m300c(boolean z) {
        this.f165a.edit().putBoolean("ib_bugreporting_success_dialog_enabled", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    /* renamed from: f */
    public String m304f() {
        return this.f165a.getString("ib_remote_report_categories", null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m295a(String str) {
        this.f165a.edit().putString("ib_remote_report_categories", str).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: g */
    public long m305g() {
        return this.f165a.getLong("report_categories_fetched_time", 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m297b(long j) {
        this.f165a.edit().putLong("report_categories_fetched_time", j).apply();
    }
}
