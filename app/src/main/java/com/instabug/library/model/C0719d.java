package com.instabug.library.model;

/* compiled from: UserTrackingStep.java */
/* renamed from: com.instabug.library.model.d */
/* loaded from: classes.dex */
public class C0719d {

    /* renamed from: a */
    private long f1132a;

    /* renamed from: b */
    private String f1133b;

    /* renamed from: c */
    private String f1134c;

    /* renamed from: d */
    private String f1135d;

    /* renamed from: e */
    private String f1136e;

    /* renamed from: f */
    private a f1137f;

    /* renamed from: g */
    private String f1138g;

    /* compiled from: UserTrackingStep.java */
    /* renamed from: com.instabug.library.model.d$a */
    /* loaded from: classes.dex */
    public enum a {
        TAP,
        SHAKE,
        APPLICATION_CREATED,
        ACTIVITY_CREATED,
        ACTIVITY_STARTED,
        ACTIVITY_RESUMED,
        ACTIVITY_PAUSED,
        ACTIVITY_STOPPED,
        ACTIVITY_DESTROYED,
        OPEN_DIALOG,
        FRAGMENT_ATTACHED,
        FRAGMENT_VIEW_CREATED,
        FRAGMENT_STARTED,
        FRAGMENT_RESUMED,
        FRAGMENT_PAUSED,
        FRAGMENT_STOPPED,
        FRAGMENT_DETACHED,
        FRAGMENT_VISIBILITY_CHANGED
    }

    /* renamed from: a */
    public long m1605a() {
        return this.f1132a;
    }

    /* renamed from: a */
    public C0719d m1606a(long j) {
        this.f1132a = j;
        return this;
    }

    /* renamed from: b */
    public String m1610b() {
        return this.f1133b;
    }

    /* renamed from: a */
    public C0719d m1608a(String str) {
        this.f1133b = str;
        return this;
    }

    /* renamed from: c */
    public String m1612c() {
        return this.f1134c;
    }

    /* renamed from: b */
    public C0719d m1609b(String str) {
        this.f1134c = str;
        return this;
    }

    /* renamed from: d */
    public String m1614d() {
        return this.f1135d;
    }

    /* renamed from: c */
    public C0719d m1611c(String str) {
        this.f1135d = str;
        return this;
    }

    /* renamed from: e */
    public String m1616e() {
        return this.f1136e;
    }

    /* renamed from: d */
    public C0719d m1613d(String str) {
        this.f1136e = str;
        return this;
    }

    /* renamed from: f */
    public a m1617f() {
        return this.f1137f;
    }

    /* renamed from: a */
    public C0719d m1607a(a aVar) {
        this.f1137f = aVar;
        return this;
    }

    /* renamed from: g */
    public String m1618g() {
        return this.f1138g;
    }

    /* renamed from: e */
    public C0719d m1615e(String str) {
        this.f1138g = str;
        return this;
    }
}
