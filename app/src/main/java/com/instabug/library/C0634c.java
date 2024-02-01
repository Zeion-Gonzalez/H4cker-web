package com.instabug.library;

import java.util.Arrays;

/* compiled from: InstabugTouchesCoordinates.java */
/* renamed from: com.instabug.library.c */
/* loaded from: classes.dex */
public class C0634c {

    /* renamed from: a */
    private static C0634c f803a = new C0634c();

    /* renamed from: b */
    private a[] f804b;

    /* renamed from: a */
    public static C0634c m1186a() {
        return f803a;
    }

    private C0634c() {
    }

    /* renamed from: b */
    public a[] m1188b() {
        if (this.f804b == null) {
            return null;
        }
        return (a[]) Arrays.copyOf(this.f804b, this.f804b.length);
    }

    /* renamed from: a */
    public void m1187a(a[] aVarArr) {
        this.f804b = (a[]) Arrays.copyOf(aVarArr, aVarArr.length);
    }

    /* compiled from: InstabugTouchesCoordinates.java */
    /* renamed from: com.instabug.library.c$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        private int f805a;

        /* renamed from: b */
        private int f806b;

        public a(int i, int i2) {
            this.f805a = -1;
            this.f806b = -1;
            this.f805a = i;
            this.f806b = i2;
        }

        /* renamed from: a */
        public int m1189a() {
            return this.f805a;
        }

        /* renamed from: b */
        public int m1190b() {
            return this.f806b;
        }
    }
}
