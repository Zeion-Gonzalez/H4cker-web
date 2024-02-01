package com.instabug.library.invocation.p029a;

import android.content.Context;
import com.instabug.library.invocation.InterfaceC0697a;
import com.instabug.library.util.C0756d;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: ShakeInvoker.java */
/* renamed from: com.instabug.library.invocation.a.e */
/* loaded from: classes.dex */
public class C0702e implements InterfaceC0698a<Void>, C0756d.a {

    /* renamed from: a */
    private C0756d f1062a;

    /* renamed from: b */
    private InterfaceC0697a f1063b;

    public C0702e(Context context, InterfaceC0697a interfaceC0697a) {
        this.f1063b = interfaceC0697a;
        this.f1062a = new C0756d(context, this);
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: a */
    public void mo1407a() {
        this.f1062a.m1811a();
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: b */
    public void mo1408b() {
        this.f1062a.m1814b();
    }

    @Override // com.instabug.library.util.C0756d.a
    /* renamed from: c */
    public void mo1499c() {
        InstabugSDKLogger.m1799d(this, "Shake detected, invoking SDK");
        this.f1063b.mo1405a();
    }

    /* renamed from: a */
    public void m1498a(int i) {
        this.f1062a.m1812a(i);
    }
}
