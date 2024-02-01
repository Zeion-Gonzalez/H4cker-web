package com.instabug.chat.settings;

import com.instabug.library.OnSdkDismissedCallback;

/* compiled from: PerSessionSettings.java */
/* renamed from: com.instabug.chat.settings.b */
/* loaded from: classes.dex */
public class C0538b {

    /* renamed from: a */
    private static C0538b f417a;

    /* renamed from: c */
    private Runnable f419c;

    /* renamed from: e */
    private OnSdkDismissedCallback f421e;

    /* renamed from: d */
    private boolean f420d = false;

    /* renamed from: b */
    private AttachmentTypesState f418b = new AttachmentTypesState();

    private C0538b() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m749a() {
        f417a = new C0538b();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static C0538b m750b() {
        return f417a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public Runnable m755c() {
        return this.f419c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m753a(Runnable runnable) {
        this.f419c = runnable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public C0538b m751a(AttachmentTypesState attachmentTypesState) {
        this.f418b = attachmentTypesState;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public AttachmentTypesState m756d() {
        return this.f418b;
    }

    /* renamed from: e */
    public boolean m757e() {
        return this.f420d;
    }

    /* renamed from: a */
    public void m754a(boolean z) {
        this.f420d = z;
    }

    /* renamed from: f */
    public OnSdkDismissedCallback m758f() {
        return this.f421e;
    }

    /* renamed from: a */
    public void m752a(OnSdkDismissedCallback onSdkDismissedCallback) {
        this.f421e = onSdkDismissedCallback;
    }
}
