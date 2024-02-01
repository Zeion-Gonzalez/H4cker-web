package com.instabug.library;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.tracking.C0739b;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;

/* loaded from: classes.dex */
public final class InstabugTrackingDelegate {
    public static void notifyApplicationCreated(Application application) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("application").setType(Application.class));
        InstabugInternalTrackingDelegate.getInstance().onApplicationCreated(application);
    }

    public static void notifyActivityCreated(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityCreated(activity);
    }

    public static void notifyActivityStarted(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityStarted(activity);
    }

    public static void notifyActivityResumed(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityResumed(activity);
    }

    public static void notifyActivityPaused(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityPaused(activity);
    }

    public static void notifyActivityStopped(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityStopped(activity);
    }

    public static void notifyActivityDestroyed(Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("activity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onActivityDestroyed(activity);
    }

    public static void notifyActivityGotTouchEvent(MotionEvent motionEvent, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName(NotificationCompat.CATEGORY_EVENT).setType(MotionEvent.class), new Api.Parameter().setName("activity").setType(Activity.class));
        C0739b.m1735a(motionEvent, activity);
    }

    public static void notifyFragmentAttached(Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentAttached(fragment, activity);
    }

    public static void notifyFragmentViewCreated(View view, Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragmentView").setType(View.class), new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentViewCreated(fragment, activity);
        C0739b.m1736a(view, activity);
    }

    public static void notifyFragmentStarted(Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentStarted(fragment, activity);
    }

    public static void notifyFragmentResumed(Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentResumed(fragment, activity);
    }

    public static void notifyFragmentPaused(Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentPaused(fragment, activity);
    }

    public static void notifyFragmentStopped(Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentStopped(fragment, activity);
    }

    public static void notifyFragmentDetached(Activity activity, Fragment fragment) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentDetached(fragment, activity);
    }

    public static void notifyFragmentVisibilityChanged(boolean z, Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isVisible").setType(Boolean.TYPE), new Api.Parameter().setName("fragment").setType(Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentVisibilityChanged(z, fragment, activity);
    }

    public static void notifyFragmentAttached(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentAttached(fragment, activity);
    }

    public static void notifyFragmentViewCreated(View view, android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragmentView").setType(View.class), new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentViewCreated(fragment, activity);
        C0739b.m1736a(view, activity);
    }

    public static void notifyFragmentStarted(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentStarted(fragment, activity);
    }

    public static void notifyFragmentResumed(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentResumed(fragment, activity);
    }

    public static void notifyFragmentPaused(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentPaused(fragment, activity);
    }

    public static void notifyFragmentStopped(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentStopped(fragment, activity);
    }

    public static void notifyFragmentDetached(android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentDetached(fragment, activity);
    }

    public static void notifyFragmentVisibilityChanged(boolean z, android.support.v4.app.Fragment fragment, Activity activity) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isVisible").setType(Boolean.TYPE), new Api.Parameter().setName("fragment").setType(android.support.v4.app.Fragment.class), new Api.Parameter().setName("parentActivity").setType(Activity.class));
        InstabugInternalTrackingDelegate.getInstance().onFragmentVisibilityChanged(z, fragment, activity);
    }
}
