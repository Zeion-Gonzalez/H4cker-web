package com.instabug.library.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.broadcast.LastContactedChangedBroadcast;

/* compiled from: PersistableSettings.java */
/* renamed from: com.instabug.library.settings.b */
/* loaded from: classes.dex */
public class C0737b {

    /* renamed from: b */
    private static C0737b f1221b;

    /* renamed from: a */
    private SharedPreferences f1222a;

    private C0737b(Context context) {
        this.f1222a = context.getSharedPreferences(SettingsManager.INSTABUG_SHARED_PREF_NAME, 0);
    }

    /* renamed from: a */
    public static void m1693a(Context context) {
        f1221b = new C0737b(context);
    }

    /* renamed from: a */
    public static C0737b m1692a() {
        return f1221b;
    }

    /* renamed from: b */
    public String m1699b() {
        return this.f1222a.getString("ib_app_token", null);
    }

    /* renamed from: a */
    public void m1697a(String str) {
        this.f1222a.edit().putString("ib_app_token", str).apply();
    }

    @NonNull
    /* renamed from: c */
    public String m1703c() {
        return this.f1222a.getString("ib_default_email", "");
    }

    /* renamed from: b */
    public void m1701b(String str) {
        this.f1222a.edit().putString("ib_default_email", str).apply();
    }

    /* renamed from: d */
    public String m1706d() {
        return this.f1222a.getString("ib_identified_email", "");
    }

    /* renamed from: c */
    public void m1704c(String str) {
        this.f1222a.edit().putString("ib_identified_email", str).apply();
    }

    /* renamed from: e */
    public boolean m1711e() {
        return this.f1222a.getBoolean("ib_device_registered", false);
    }

    /* renamed from: a */
    public void m1698a(boolean z) {
        this.f1222a.edit().putBoolean("ib_device_registered", z).apply();
    }

    /* renamed from: f */
    public boolean m1714f() {
        return this.f1222a.getBoolean("ib_first_run", true);
    }

    /* renamed from: b */
    public void m1702b(boolean z) {
        this.f1222a.edit().putBoolean("ib_first_run", z).apply();
        this.f1222a.edit().putLong("ib_first_run_at", System.currentTimeMillis()).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: g */
    public long m1715g() {
        return this.f1222a.getLong("ib_first_run_at", 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1695a(long j) {
        this.f1222a.edit().putLong("ib_first_run_at", j).apply();
    }

    /* renamed from: h */
    public long m1718h() {
        return this.f1222a.getLong(LastContactedChangedBroadcast.LAST_CONTACTED_AT, 0L);
    }

    /* renamed from: b */
    public void m1700b(long j) {
        this.f1222a.edit().putLong(LastContactedChangedBroadcast.LAST_CONTACTED_AT, j).apply();
    }

    /* renamed from: i */
    public boolean m1720i() {
        return this.f1222a.getBoolean("ib_pn", true);
    }

    /* renamed from: c */
    public void m1705c(boolean z) {
        this.f1222a.edit().putBoolean("ib_pn", z).apply();
    }

    /* renamed from: j */
    public int m1721j() {
        return this.f1222a.getInt("last_migration_version", 0);
    }

    /* renamed from: a */
    public void m1694a(int i) {
        this.f1222a.edit().putInt("last_migration_version", i).apply();
    }

    /* renamed from: k */
    public boolean m1722k() {
        return this.f1222a.getBoolean("ib_first_dismiss", true);
    }

    /* renamed from: d */
    public void m1708d(boolean z) {
        this.f1222a.edit().putBoolean("ib_first_dismiss", z).apply();
    }

    /* renamed from: l */
    public InstabugColorTheme m1723l() {
        return InstabugColorTheme.valueOf(this.f1222a.getString("ib_color_theme", InstabugColorTheme.InstabugColorThemeLight.name()));
    }

    /* renamed from: a */
    public void m1696a(InstabugColorTheme instabugColorTheme) {
        this.f1222a.edit().putString("ib_color_theme", instabugColorTheme.name()).apply();
    }

    /* renamed from: m */
    public String m1724m() {
        return this.f1222a.getString("ib_default_username", "");
    }

    /* renamed from: d */
    public void m1707d(String str) {
        this.f1222a.edit().putString("ib_default_username", str).apply();
    }

    /* renamed from: n */
    public String m1725n() {
        return this.f1222a.getString("ib_uuid", null);
    }

    /* renamed from: e */
    public void m1709e(String str) {
        this.f1222a.edit().putString("ib_uuid", str).apply();
    }

    /* renamed from: o */
    public String m1726o() {
        return this.f1222a.getString("ib_md5_uuid", null);
    }

    /* renamed from: f */
    public void m1712f(String str) {
        this.f1222a.edit().putString("ib_md5_uuid", str).apply();
    }

    /* renamed from: p */
    public String m1727p() {
        return this.f1222a.getString("ib_user_data", "");
    }

    /* renamed from: g */
    public void m1716g(String str) {
        this.f1222a.edit().putString("ib_user_data", str).apply();
    }

    /* renamed from: q */
    public boolean m1728q() {
        return this.f1222a.getBoolean("ib_is_intro_message_enabled", true);
    }

    /* renamed from: e */
    public void m1710e(boolean z) {
        this.f1222a.edit().putBoolean("ib_is_intro_message_enabled", z).apply();
    }

    /* renamed from: r */
    public boolean m1729r() {
        return this.f1222a.getBoolean("ib_is_user_logged_out", true);
    }

    /* renamed from: f */
    public void m1713f(boolean z) {
        this.f1222a.edit().putBoolean("ib_is_user_logged_out", z).apply();
    }

    /* renamed from: s */
    public boolean m1730s() {
        return this.f1222a.getBoolean("ib_should_make_uuid_migration_request", false);
    }

    /* renamed from: g */
    public void m1717g(boolean z) {
        this.f1222a.edit().putBoolean("ib_should_make_uuid_migration_request", z).apply();
    }

    /* renamed from: h */
    public void m1719h(String str) {
        this.f1222a.edit().putString("ib_sdk_version", str).apply();
        this.f1222a.edit().putBoolean("ib_is_sdk_version_set", true).apply();
    }

    /* renamed from: t */
    public String m1731t() {
        return this.f1222a.getString("ib_sdk_version", "4.11.2");
    }

    /* renamed from: u */
    public boolean m1732u() {
        return this.f1222a.getBoolean("ib_is_sdk_version_set", false);
    }

    /* renamed from: v */
    public int m1733v() {
        return this.f1222a.getInt("ib_sessions_count", 0);
    }

    /* renamed from: w */
    public void m1734w() {
        this.f1222a.edit().putInt("ib_sessions_count", m1733v() + 1).apply();
    }
}
