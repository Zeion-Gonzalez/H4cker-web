package com.instabug.chat.settings;

import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;

/* compiled from: PersistableSettings.java */
/* renamed from: com.instabug.chat.settings.c */
/* loaded from: classes.dex */
public class C0539c {

    /* renamed from: a */
    private static C0539c f422a;

    /* renamed from: b */
    private SharedPreferences f423b;

    private C0539c(SharedPreferences sharedPreferences) {
        this.f423b = sharedPreferences;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m760a(SharedPreferences sharedPreferences) {
        f422a = new C0539c(sharedPreferences);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static C0539c m759a() {
        return f422a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public long m765b() {
        return this.f423b.getLong("ibc_last_chat_time", System.currentTimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m762a(long j) {
        this.f423b.edit().putLong("ibc_last_chat_time", j).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public long m768c() {
        return this.f423b.getLong("ibc_ttl", 60L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m766b(long j) {
        this.f423b.edit().putLong("ibc_ttl", j).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m764a(boolean z) {
        this.f423b.edit().putBoolean("ibc_conversation_sounds", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public boolean m771d() {
        return this.f423b.getBoolean("ibc_notification_sound", false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m767b(boolean z) {
        this.f423b.edit().putBoolean("ibc_notification_sound", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
    public boolean m773e() {
        return this.f423b.getBoolean("ibc_in_app_notification_sound", false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public void m769c(boolean z) {
        this.f423b.edit().putBoolean("ibc_in_app_notification_sound", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @DrawableRes
    /* renamed from: f */
    public int m774f() {
        return this.f423b.getInt("ibc_push_notification_icon", -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m761a(@DrawableRes int i) {
        this.f423b.edit().putInt("ibc_push_notification_icon", i).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: g */
    public boolean m775g() {
        return this.f423b.getBoolean("ibc__notifications_state", true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public void m770d(boolean z) {
        this.f423b.edit().putBoolean("ibc__notifications_state", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m763a(String str) {
        this.f423b.edit().putString("ibc_push_notification_token", str).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: h */
    public String m776h() {
        return this.f423b.getString("ibc_push_notification_token", "");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
    public void m772e(boolean z) {
        this.f423b.edit().putBoolean("ibc_is_push_notification_token_sent", z).apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: i */
    public boolean m777i() {
        return this.f423b.getBoolean("ibc_is_push_notification_token_sent", false);
    }
}
