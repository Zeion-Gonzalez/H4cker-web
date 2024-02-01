package com.instabug.library.instacapture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.instacapture.p025a.C0648a;
import com.instabug.library.instacapture.p026b.InterfaceC0651a;
import com.instabug.library.instacapture.p027c.C0653a;
import com.instabug.library.instacapture.screenshot.C0656a;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.android.schedulers.AndroidSchedulers;

/* compiled from: InstaCapture.java */
/* renamed from: com.instabug.library.instacapture.b */
/* loaded from: classes.dex */
public final class C0650b {

    /* renamed from: a */
    private static C0650b f821a;
    @NonNull

    /* renamed from: b */
    private C0647a f822b = new C0647a();

    /* renamed from: c */
    private C0656a f823c;

    private C0650b(@NonNull Activity activity) {
        this.f822b.m1272a(activity);
        this.f823c = m1274a();
    }

    /* renamed from: a */
    public static C0650b m1273a(@NonNull Activity activity) {
        synchronized (C0650b.class) {
            if (f821a == null) {
                f821a = new C0650b(activity);
            } else {
                f821a.m1276b(activity);
            }
        }
        return f821a;
    }

    /* renamed from: b */
    private void m1276b(@NonNull Activity activity) {
        this.f822b.m1272a(activity);
    }

    /* renamed from: a */
    public void m1277a(final InterfaceC0651a interfaceC0651a, @IdRes @Nullable int... iArr) {
        if (this.f823c != null) {
            m1275b(interfaceC0651a, iArr).subscribe((Subscriber<? super Bitmap>) new Subscriber<Bitmap>() { // from class: com.instabug.library.instacapture.b.1
                @Override // p045rx.Observer
                public void onCompleted() {
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    C0653a.m1284b("Screenshot capture failed");
                    C0653a.m1283a(th);
                    if (interfaceC0651a != null) {
                        interfaceC0651a.mo1281a(th);
                    }
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(Bitmap bitmap) {
                    if (interfaceC0651a != null) {
                        interfaceC0651a.mo1280a(bitmap);
                    }
                }
            });
        }
    }

    /* renamed from: b */
    private Observable<Bitmap> m1275b(InterfaceC0651a interfaceC0651a, @IdRes @Nullable int... iArr) {
        Activity m1271a = this.f822b.m1271a();
        if (m1271a == null) {
            return Observable.error(new C0648a("Is your activity running?"));
        }
        if (interfaceC0651a != null) {
            interfaceC0651a.mo1279a();
        }
        return this.f823c.m1286a(m1271a, iArr).observeOn(AndroidSchedulers.mainThread());
    }

    /* renamed from: a */
    private C0656a m1274a() {
        if (this.f822b.m1271a() != null) {
            return new C0656a();
        }
        C0653a.m1284b("Is your activity running?");
        return null;
    }
}
