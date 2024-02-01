package com.instabug.library.annotation.p019a;

import android.graphics.Path;
import com.instabug.library.annotation.p019a.C0613e;

/* compiled from: PathRecognizer.java */
/* renamed from: com.instabug.library.annotation.a.c */
/* loaded from: classes.dex */
public class C0611c {

    /* compiled from: PathRecognizer.java */
    /* renamed from: com.instabug.library.annotation.a.c$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        public C0613e.a f703a;

        /* renamed from: b */
        public int f704b;

        /* renamed from: c */
        public float f705c;
    }

    /* renamed from: a */
    public a m1080a(Path path) {
        a aVar = new a();
        C0609a c0609a = new C0609a(path);
        C0615g m1077a = c0609a.m1077a(C0613e.a.OVAL);
        C0615g m1076a = c0609a.m1076a();
        C0615g m1077a2 = c0609a.m1077a(C0613e.a.RECT);
        if (m1076a.f717c <= m1077a2.f717c || m1076a.f717c <= m1077a.f717c) {
            if (m1077a2.f717c > m1077a.f717c) {
                if (m1077a2.f720f > 0.5f || m1077a2.f721g > 0.5f) {
                    aVar.f703a = C0613e.a.NONE;
                } else {
                    aVar.f703a = C0613e.a.RECT;
                    aVar.f704b = m1077a2.f716b;
                    aVar.f705c = m1077a2.f719e;
                }
            } else if (m1077a.f720f > 0.5f || m1077a.f721g > 0.5f) {
                aVar.f703a = C0613e.a.NONE;
            } else {
                aVar.f703a = C0613e.a.OVAL;
                aVar.f704b = m1077a.f716b;
                aVar.f705c = m1077a.f719e;
            }
        } else if (m1076a.f720f > 0.5f || m1076a.f721g > 0.5f) {
            aVar.f703a = C0613e.a.NONE;
        } else if (m1076a.f726l < 100.0f) {
            if (Math.abs(m1076a.f723i - m1076a.f725k) < 10 && Math.abs(m1076a.f724j - m1076a.f722h) < 10) {
                aVar.f703a = C0613e.a.LINE;
            } else {
                aVar.f703a = C0613e.a.ARROW;
            }
            aVar.f704b = m1076a.f716b;
        } else {
            aVar.f703a = C0613e.a.NONE;
        }
        return aVar;
    }
}
