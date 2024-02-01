package com.instabug.chat.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;
import com.instabug.library.OnSdkDismissedCallback;

/* compiled from: ChatSettings.java */
/* renamed from: com.instabug.chat.settings.a */
/* loaded from: classes.dex */
public class C0537a {
    /* renamed from: a */
    public static void m724a(Context context) {
        C0538b.m749a();
        C0539c.m760a(m730b(context));
    }

    /* renamed from: b */
    private static SharedPreferences m730b(Context context) {
        return context.getSharedPreferences("com.instabug.chat", 0);
    }

    /* renamed from: a */
    public static Runnable m721a() {
        return C0538b.m750b().m755c();
    }

    /* renamed from: a */
    public static void m727a(Runnable runnable) {
        C0538b.m750b().m753a(runnable);
    }

    /* renamed from: b */
    public static AttachmentTypesState m731b() {
        return C0538b.m750b().m756d();
    }

    /* renamed from: a */
    public static void m725a(AttachmentTypesState attachmentTypesState) {
        C0538b.m750b().m751a(attachmentTypesState);
    }

    /* renamed from: c */
    public static boolean m735c() {
        AttachmentTypesState m731b = m731b();
        return m731b.isScreenshotEnabled() || m731b.isImageFromGalleryEnabled() || m731b.isScreenRecordingEnabled();
    }

    /* renamed from: d */
    public static long m736d() {
        return C0539c.m759a().m765b();
    }

    /* renamed from: a */
    public static void m723a(long j) {
        C0539c.m759a().m762a(j);
    }

    /* renamed from: e */
    public static long m738e() {
        return C0539c.m759a().m768c();
    }

    /* renamed from: b */
    public static void m732b(long j) {
        C0539c.m759a().m766b(j);
    }

    /* renamed from: f */
    public static boolean m741f() {
        return C0539c.m759a().m775g();
    }

    /* renamed from: a */
    public static void m729a(boolean z) {
        C0539c.m759a().m770d(z);
    }

    /* renamed from: g */
    public static boolean m742g() {
        return C0539c.m759a().m771d();
    }

    /* renamed from: b */
    public static void m733b(boolean z) {
        C0539c.m759a().m767b(z);
    }

    /* renamed from: h */
    public static boolean m743h() {
        return C0539c.m759a().m773e();
    }

    /* renamed from: c */
    public static void m734c(boolean z) {
        C0539c.m759a().m769c(z);
    }

    /* renamed from: d */
    public static void m737d(boolean z) {
        C0539c.m759a().m764a(z);
    }

    @DrawableRes
    /* renamed from: i */
    public static int m744i() {
        return C0539c.m759a().m774f();
    }

    /* renamed from: a */
    public static void m722a(@DrawableRes int i) {
        C0539c.m759a().m761a(i);
    }

    /* renamed from: j */
    public static boolean m745j() {
        return C0538b.m750b().m757e();
    }

    /* renamed from: e */
    public static void m739e(boolean z) {
        C0538b.m750b().m754a(z);
    }

    /* renamed from: k */
    public static OnSdkDismissedCallback m746k() {
        return C0538b.m750b().m758f();
    }

    /* renamed from: a */
    public static void m726a(OnSdkDismissedCallback onSdkDismissedCallback) {
        C0538b.m750b().m752a(onSdkDismissedCallback);
    }

    /* renamed from: a */
    public static void m728a(String str) {
        C0539c.m759a().m763a(str);
    }

    /* renamed from: l */
    public static String m747l() {
        return C0539c.m759a().m776h();
    }

    /* renamed from: f */
    public static void m740f(boolean z) {
        C0539c.m759a().m772e(z);
    }

    /* renamed from: m */
    public static boolean m748m() {
        return C0539c.m759a().m777i();
    }
}
