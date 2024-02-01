package com.instabug.chat.model;

import java.util.ArrayList;

/* compiled from: FlatMessage.java */
/* renamed from: com.instabug.chat.model.a */
/* loaded from: classes.dex */
public class C0527a {

    /* renamed from: a */
    private String f361a;

    /* renamed from: b */
    private String f362b;

    /* renamed from: c */
    private long f363c;

    /* renamed from: d */
    private String f364d;

    /* renamed from: e */
    private String f365e;

    /* renamed from: f */
    private b f366f;

    /* renamed from: g */
    private a f367g;

    /* renamed from: h */
    private boolean f368h;

    /* renamed from: i */
    private boolean f369i = false;

    /* renamed from: j */
    private ArrayList<C0528b> f370j;

    /* compiled from: FlatMessage.java */
    /* renamed from: com.instabug.chat.model.a$a */
    /* loaded from: classes.dex */
    public enum a {
        NONE,
        PLAYING
    }

    /* compiled from: FlatMessage.java */
    /* renamed from: com.instabug.chat.model.a$b */
    /* loaded from: classes.dex */
    public enum b {
        MESSAGE,
        IMAGE,
        AUDIO,
        VIDEO
    }

    /* renamed from: a */
    public String m655a() {
        return this.f361a;
    }

    /* renamed from: a */
    public C0527a m653a(String str) {
        this.f361a = str;
        return this;
    }

    /* renamed from: b */
    public String m659b() {
        return this.f362b;
    }

    /* renamed from: b */
    public C0527a m657b(String str) {
        this.f362b = str;
        return this;
    }

    /* renamed from: c */
    public long m660c() {
        return this.f363c;
    }

    /* renamed from: a */
    public C0527a m650a(long j) {
        this.f363c = j;
        return this;
    }

    /* renamed from: d */
    public String m663d() {
        return this.f364d;
    }

    /* renamed from: c */
    public C0527a m661c(String str) {
        this.f364d = str;
        return this;
    }

    /* renamed from: e */
    public b m664e() {
        return this.f366f;
    }

    /* renamed from: a */
    public C0527a m652a(b bVar) {
        this.f366f = bVar;
        return this;
    }

    /* renamed from: f */
    public a m665f() {
        return this.f367g;
    }

    /* renamed from: a */
    public C0527a m651a(a aVar) {
        this.f367g = aVar;
        return this;
    }

    /* renamed from: g */
    public boolean m666g() {
        return this.f368h;
    }

    /* renamed from: a */
    public C0527a m654a(boolean z) {
        this.f368h = z;
        return this;
    }

    /* renamed from: h */
    public String m667h() {
        return this.f365e;
    }

    /* renamed from: d */
    public C0527a m662d(String str) {
        this.f365e = str;
        return this;
    }

    /* renamed from: i */
    public boolean m668i() {
        return this.f370j != null && this.f370j.size() > 0;
    }

    /* renamed from: j */
    public ArrayList<C0528b> m669j() {
        return this.f370j;
    }

    /* renamed from: a */
    public void m656a(ArrayList<C0528b> arrayList) {
        this.f370j = arrayList;
    }

    /* renamed from: k */
    public boolean m670k() {
        return this.f369i;
    }

    /* renamed from: b */
    public C0527a m658b(boolean z) {
        this.f369i = z;
        return this;
    }

    public String toString() {
        return "Body: " + m655a() + "URL: " + m663d() + "has actions: " + m668i() + "type: " + m664e() + "actions: " + m669j();
    }
}
