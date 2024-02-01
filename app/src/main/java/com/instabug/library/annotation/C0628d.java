package com.instabug.library.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/* compiled from: MarkUpStack.java */
/* renamed from: com.instabug.library.annotation.d */
/* loaded from: classes.dex */
public class C0628d {

    /* renamed from: a */
    private List<C0625c> f771a = new ArrayList();

    /* renamed from: b */
    private List<C0625c> f772b = new ArrayList();

    /* renamed from: c */
    private List<C0625c> f773c = new ArrayList();

    /* renamed from: d */
    private Stack<C0625c> f774d = new Stack<>();

    /* renamed from: a */
    public void m1153a(C0625c c0625c) {
        this.f772b.add(c0625c);
        m1150d();
        this.f774d.add(c0625c);
    }

    /* renamed from: b */
    public void m1155b(C0625c c0625c) {
        this.f773c.add(c0625c);
        m1150d();
        this.f774d.add(c0625c);
    }

    /* renamed from: d */
    private void m1150d() {
        this.f771a.clear();
        this.f771a.addAll(this.f773c);
        this.f771a.addAll(this.f772b);
    }

    /* renamed from: a */
    public List<C0625c> m1152a() {
        return this.f771a;
    }

    /* renamed from: c */
    public void m1157c(C0625c c0625c) {
        if (!this.f772b.remove(c0625c)) {
            this.f773c.remove(c0625c);
        }
        this.f771a.remove(c0625c);
        while (true) {
            int indexOf = this.f774d.indexOf(c0625c);
            if (indexOf != -1) {
                this.f774d.remove(indexOf);
            } else {
                return;
            }
        }
    }

    /* renamed from: a */
    public C0625c m1151a(int i) {
        return this.f771a.get(i);
    }

    /* renamed from: b */
    public int m1154b() {
        return this.f771a.size();
    }

    /* renamed from: d */
    public int m1158d(C0625c c0625c) {
        return this.f771a.indexOf(c0625c);
    }

    /* renamed from: c */
    public C0625c m1156c() {
        if (this.f774d.size() > 0) {
            C0625c pop = this.f774d.pop();
            if (!pop.m1133b()) {
                m1157c(pop);
                return pop;
            }
        }
        return null;
    }

    /* renamed from: e */
    public void m1159e(C0625c c0625c) {
        this.f774d.push(c0625c);
    }
}
