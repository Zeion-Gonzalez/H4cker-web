package com.instabug.library.instacapture;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;

/* compiled from: ActivityReferenceManager.java */
/* renamed from: com.instabug.library.instacapture.a */
/* loaded from: classes.dex */
public final class C0647a {
    @Nullable

    /* renamed from: a */
    private WeakReference<Activity> f820a;

    /* renamed from: a */
    public void m1272a(@NonNull Activity activity) {
        this.f820a = new WeakReference<>(activity);
    }

    @Nullable
    /* renamed from: a */
    public Activity m1271a() {
        if (this.f820a == null) {
            return null;
        }
        Activity activity = this.f820a.get();
        if (m1270b(activity)) {
            return activity;
        }
        return null;
    }

    /* renamed from: b */
    private boolean m1270b(@Nullable Activity activity) {
        if (activity == null) {
            return false;
        }
        return Build.VERSION.SDK_INT >= 17 ? (activity.isFinishing() || activity.isDestroyed()) ? false : true : !activity.isFinishing();
    }
}
