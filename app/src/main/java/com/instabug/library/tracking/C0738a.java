package com.instabug.library.tracking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: InstabugActivityLifecycleListener.java */
@TargetApi(14)
/* renamed from: com.instabug.library.tracking.a */
/* loaded from: classes.dex */
public class C0738a implements Application.ActivityLifecycleCallbacks {
    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityCreatedEvent(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityStartedEvent(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityResumedEvent(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityPausedEvent(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityStoppedEvent(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        InstabugSDKLogger.m1799d(this, activity.getClass().getSimpleName() + " SaveInstanceState");
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        InstabugInternalTrackingDelegate.getInstance().handleActivityDestroyedEvent(activity);
    }
}
