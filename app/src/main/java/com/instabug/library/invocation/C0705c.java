package com.instabug.library.invocation;

import com.instabug.library.invocation.p029a.C0702e;
import com.instabug.library.invocation.p029a.ViewOnClickListenerC0699b;
import com.instabug.library.invocation.util.InstabugFloatingButtonEdge;
import com.instabug.library.invocation.util.InstabugVideoRecordingButtonCorner;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/* compiled from: InvocationSettings.java */
/* renamed from: com.instabug.library.invocation.c */
/* loaded from: classes.dex */
public class C0705c {

    /* renamed from: a */
    private final boolean[] f1082a = new boolean[5];

    /* renamed from: b */
    private int f1083b = 0;

    /* renamed from: c */
    private InstabugVideoRecordingButtonCorner f1084c = InstabugVideoRecordingButtonCorner.BOTTOM_RIGHT;

    /* renamed from: e */
    private int f1086e = 350;

    /* renamed from: d */
    private ViewOnClickListenerC0699b.d f1085d = new ViewOnClickListenerC0699b.d();

    /* renamed from: a */
    public int m1532a() {
        return this.f1083b;
    }

    /* renamed from: a */
    public void m1533a(int i) {
        this.f1083b = i;
    }

    /* renamed from: b */
    public void m1536b() {
        this.f1083b = 0;
    }

    /* renamed from: a */
    public void m1534a(InstabugFloatingButtonEdge instabugFloatingButtonEdge) {
        this.f1085d.f998a = instabugFloatingButtonEdge;
    }

    /* renamed from: b */
    public void m1537b(int i) {
        this.f1085d.f999b = i;
    }

    /* renamed from: c */
    public ViewOnClickListenerC0699b.d m1538c() {
        return this.f1085d;
    }

    /* renamed from: c */
    public void m1539c(int i) {
        if (i > 0) {
            this.f1086e = i;
            if (C0704b.m1513c().m1525f() instanceof C0702e) {
                ((C0702e) C0704b.m1513c().m1525f()).m1498a(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public int m1540d() {
        return this.f1086e;
    }

    /* renamed from: d */
    public void m1541d(int i) {
        this.f1082a[i] = true;
    }

    /* renamed from: e */
    public void m1542e(int i) {
        this.f1082a[i] = false;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP"})
    /* renamed from: e */
    public boolean[] m1543e() {
        return this.f1082a;
    }

    /* renamed from: f */
    public InstabugVideoRecordingButtonCorner m1544f() {
        return this.f1084c;
    }

    /* renamed from: a */
    public void m1535a(InstabugVideoRecordingButtonCorner instabugVideoRecordingButtonCorner) {
        this.f1084c = instabugVideoRecordingButtonCorner;
    }
}
