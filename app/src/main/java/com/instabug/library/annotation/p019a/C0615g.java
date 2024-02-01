package com.instabug.library.annotation.p019a;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/* compiled from: Weight.java */
@SuppressFBWarnings({"EQ_COMPARETO_USE_OBJECT_EQUALS", "UUF_UNUSED_FIELD", "URF_UNREAD_FIELD"})
/* renamed from: com.instabug.library.annotation.a.g */
/* loaded from: classes.dex */
public class C0615g implements Comparable<C0615g> {

    /* renamed from: a */
    int f715a;

    /* renamed from: b */
    int f716b;

    /* renamed from: c */
    float f717c;

    /* renamed from: d */
    float f718d;

    /* renamed from: e */
    float f719e;

    /* renamed from: f */
    float f720f;

    /* renamed from: g */
    float f721g;

    /* renamed from: h */
    int f722h;

    /* renamed from: i */
    int f723i;

    /* renamed from: j */
    int f724j;

    /* renamed from: k */
    int f725k;

    /* renamed from: l */
    float f726l;

    /* compiled from: Weight.java */
    /* renamed from: com.instabug.library.annotation.a.g$a */
    /* loaded from: classes.dex */
    public enum a {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    /* renamed from: a */
    public a m1084a() {
        return this.f725k > this.f723i ? a.BOTTOM : a.TOP;
    }

    @Override // java.lang.Comparable
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public int compareTo(C0615g c0615g) {
        if (c0615g.f715a > this.f715a) {
            return -1;
        }
        if (c0615g.f715a < this.f715a) {
            return 1;
        }
        return 0;
    }
}
