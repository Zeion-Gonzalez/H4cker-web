package com.instabug.library.visualusersteps;

import java.util.LinkedList;

/* compiled from: Parent.java */
/* renamed from: com.instabug.library.visualusersteps.a */
/* loaded from: classes.dex */
class C0760a {

    /* renamed from: a */
    private String f1272a;

    /* renamed from: b */
    private String f1273b;

    /* renamed from: c */
    private a f1274c;

    /* renamed from: d */
    private LinkedList<C0761b> f1275d = new LinkedList<>();

    /* renamed from: e */
    private boolean f1276e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0760a(String str, String str2) {
        this.f1272a = str;
        this.f1273b = str2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public String m1819a() {
        return this.f1273b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1821a(C0761b c0761b) {
        this.f1275d.add(c0761b);
        if (c0761b.m1847f().equals("activity_resumed") || c0761b.m1847f().equals("fragment_resumed")) {
            this.f1276e = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public LinkedList<C0761b> m1822b() {
        return this.f1275d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public int m1823c() {
        return this.f1275d.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public void m1824d() {
        this.f1275d.removeFirst();
    }

    /* renamed from: e */
    public String m1825e() {
        return this.f1272a;
    }

    /* renamed from: a */
    public void m1820a(a aVar) {
        this.f1274c = aVar;
    }

    /* renamed from: f */
    public a m1826f() {
        return this.f1274c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: g */
    public boolean m1827g() {
        return this.f1276e;
    }

    /* compiled from: Parent.java */
    /* renamed from: com.instabug.library.visualusersteps.a$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        private String f1277a;

        /* renamed from: b */
        private String f1278b;

        public a(String str) {
            this.f1277a = str;
        }

        /* renamed from: a */
        public String m1828a() {
            return this.f1277a;
        }

        /* renamed from: a */
        public void m1829a(String str) {
            this.f1278b = str;
        }

        /* renamed from: b */
        public String m1830b() {
            return this.f1278b;
        }
    }
}
