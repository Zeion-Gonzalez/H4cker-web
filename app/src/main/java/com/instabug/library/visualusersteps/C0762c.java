package com.instabug.library.visualusersteps;

import android.support.annotation.Nullable;
import com.instabug.library.Instabug;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.visualusersteps.C0760a;
import java.io.File;
import java.util.LinkedList;
import p045rx.Observable;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.schedulers.Schedulers;

/* compiled from: VisualUserSteps.java */
/* renamed from: com.instabug.library.visualusersteps.c */
/* loaded from: classes.dex */
public class C0762c {

    /* renamed from: a */
    private LinkedList<C0760a> f1295a = new LinkedList<>();

    /* renamed from: b */
    private int f1296b;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public LinkedList<C0760a> m1874a() {
        return this.f1295a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    /* renamed from: b */
    public C0760a m1878b() {
        if (this.f1295a.isEmpty()) {
            return null;
        }
        return this.f1295a.getLast();
    }

    /* renamed from: f */
    private C0760a m1872f() {
        return this.f1295a.getFirst();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1877a(C0761b c0761b) {
        m1878b().m1821a(c0761b);
        this.f1296b++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public void m1879c() {
        if (m1872f().m1823c() > 1) {
            this.f1296b--;
            m1872f().m1824d();
        } else {
            m1873g();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public int m1880d() {
        return this.f1296b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1876a(C0760a c0760a) {
        this.f1295a.add(c0760a);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
    public int m1881e() {
        return this.f1295a.size();
    }

    /* renamed from: g */
    private void m1873g() {
        C0760a.a m1826f = this.f1295a.getFirst().m1826f();
        if (m1826f != null) {
            m1870b(m1826f.m1828a());
        }
        this.f1296b -= this.f1295a.getFirst().m1823c();
        this.f1295a.removeFirst();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1875a(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            m1873g();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public boolean m1869a(String str) {
        File file = new File(VisualUserStepsHelper.getVisualUserStepsDirectory(Instabug.getApplicationContext()) + File.separator + str);
        if (file.exists() && file.delete()) {
            InstabugSDKLogger.m1803v(C0763d.class, "VisualUserStep screenshot deleted! filename= " + str);
            return true;
        }
        InstabugSDKLogger.m1803v(C0763d.class, "VisualUserStep screenshot doesn't deleted! filename= " + str + "\n Something went wrong");
        return false;
    }

    /* renamed from: b */
    private void m1870b(String str) {
        m1871c(str).subscribeOn(Schedulers.m2140io()).subscribe(new Action1<Boolean>() { // from class: com.instabug.library.visualusersteps.c.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Boolean bool) {
            }
        });
    }

    /* renamed from: c */
    private Observable<Boolean> m1871c(final String str) {
        return Observable.defer(new Func0<Observable<Boolean>>() { // from class: com.instabug.library.visualusersteps.c.2
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<Boolean> call() {
                return Observable.just(Boolean.valueOf(C0762c.this.m1869a(str)));
            }
        });
    }
}
