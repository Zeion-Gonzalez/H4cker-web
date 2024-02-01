package com.instabug.bug.model;

/* compiled from: ExtraReportField.java */
/* renamed from: com.instabug.bug.model.a */
/* loaded from: classes.dex */
public class C0471a {

    /* renamed from: a */
    private String f91a;

    /* renamed from: b */
    private CharSequence f92b;

    /* renamed from: c */
    private CharSequence f93c;

    /* renamed from: d */
    private String f94d;

    /* renamed from: e */
    private boolean f95e;

    public C0471a(CharSequence charSequence, boolean z) {
        this.f92b = charSequence;
        this.f95e = z;
    }

    public C0471a(CharSequence charSequence, CharSequence charSequence2, boolean z, String str) {
        this.f92b = charSequence;
        this.f93c = charSequence2;
        this.f95e = z;
        this.f91a = str;
    }

    /* renamed from: a */
    public String m144a() {
        return this.f91a;
    }

    /* renamed from: a */
    public void m145a(String str) {
        this.f94d = str;
    }

    /* renamed from: b */
    public String m146b() {
        return this.f94d;
    }

    /* renamed from: c */
    public CharSequence m147c() {
        return this.f92b;
    }

    /* renamed from: d */
    public CharSequence m148d() {
        return this.f93c;
    }

    /* renamed from: e */
    public boolean m149e() {
        return this.f95e;
    }
}
