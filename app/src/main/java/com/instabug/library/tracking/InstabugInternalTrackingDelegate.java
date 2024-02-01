package com.instabug.library.tracking;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.model.C0719d;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.visualusersteps.C0763d;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class InstabugInternalTrackingDelegate {
    private static InstabugInternalTrackingDelegate INSTANCE;
    private WeakReference<Activity> currentActivity;

    public static void init(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new InstabugInternalTrackingDelegate(application);
        }
    }

    public static InstabugInternalTrackingDelegate getInstance() {
        return INSTANCE;
    }

    private InstabugInternalTrackingDelegate(Application application) {
        if (application != null) {
            registerActivityLifecycleListener(application);
        }
    }

    public void onApplicationCreated(Application application) {
        if (isUserTrackingStepsEnable()) {
            InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, application.getClass().getSimpleName() + " created");
            C0741d.m1746a().m1751a(application.getClass().getName(), C0719d.a.APPLICATION_CREATED);
        }
    }

    public void onActivityCreated(Activity activity) {
        if (beforeICS()) {
            handleActivityCreatedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityCreatedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " created");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_CREATED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_created", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
            CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.CREATED);
        }
    }

    public void onActivityStarted(Activity activity) {
        if (beforeICS()) {
            handleActivityStartedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityStartedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " started");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_STARTED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_started", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
        }
        CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.STARTED);
    }

    public void onActivityResumed(Activity activity) {
        if (beforeICS()) {
            handleActivityResumedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityResumedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            this.currentActivity = new WeakReference<>(activity);
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " resumed");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_RESUMED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_resumed", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
            CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.RESUMED);
        }
    }

    public void onActivityPaused(Activity activity) {
        if (beforeICS()) {
            handleActivityPausedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityPausedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            if (this.currentActivity == null) {
                InstabugSDKLogger.m1804w(this, "No activity was set earlier than this call. Doing nothing");
                return;
            }
            if (!activity.equals(this.currentActivity.get())) {
                InstabugSDKLogger.m1804w(this, "You're trying to pause an activity that is not the current activity! Please make sure you're calling onCurrentActivityPaused and onCurrentActivityResumed on every activity");
                return;
            }
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " paused");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_PAUSED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_paused", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
            CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.PAUSED);
        }
    }

    public void onActivityStopped(Activity activity) {
        if (beforeICS()) {
            handleActivityStoppedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityStoppedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " stopped");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_STOPPED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_stopped", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
        }
        CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.STOPPED);
    }

    public void onActivityDestroyed(Activity activity) {
        if (beforeICS()) {
            handleActivityDestroyedEvent(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleActivityDestroyedEvent(Activity activity) {
        if (isNotInstabugActivity(activity)) {
            if (isUserTrackingStepsEnable()) {
                InstabugSDKLogger.m1803v(InstabugInternalTrackingDelegate.class, activity.getClass().getSimpleName() + " destroyed");
                C0741d.m1746a().m1751a(activity.getClass().getName(), C0719d.a.ACTIVITY_DESTROYED);
            }
            if (isReproStepsEnable()) {
                C0763d.m1884a().m1896a("activity_destroyed", activity.getClass().getSimpleName(), activity.getClass().getName());
            }
            CurrentActivityLifeCycleEventBus.getInstance().post(ActivityLifeCycleEvent.DESTROYED);
        }
    }

    public void onFragmentAttached(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_ATTACHED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_attached", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentViewCreated(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_VIEW_CREATED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_view_created", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentStarted(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_STARTED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_started", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentResumed(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_RESUMED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_resumed", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentPaused(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_PAUSED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_paused", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentStopped(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_STOPPED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_stopped", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentDetached(Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_DETACHED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_detached", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentVisibilityChanged(boolean z, Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1754a(fragment.getClass().getName(), activity.getClass().getName(), "Fragment visibility: " + z, C0719d.a.FRAGMENT_VISIBILITY_CHANGED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_visibility_changed", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentAttached(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_ATTACHED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_attached", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentViewCreated(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_VIEW_CREATED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_view_created", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentStarted(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_STARTED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_started", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentResumed(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_RESUMED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_resumed", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentPaused(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_PAUSED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_paused", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentStopped(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_STOPPED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_stopped", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentDetached(android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1752a(fragment.getClass().getName(), activity.getClass().getName(), C0719d.a.FRAGMENT_DETACHED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_detached", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void onFragmentVisibilityChanged(boolean z, android.support.v4.app.Fragment fragment, Activity activity) {
        if (isUserTrackingStepsEnable()) {
            C0741d.m1746a().m1754a(fragment.getClass().getName(), activity.getClass().getName(), "Fragment visibility: " + z, C0719d.a.FRAGMENT_VISIBILITY_CHANGED);
        }
        if (isReproStepsEnable()) {
            C0763d.m1884a().m1896a("fragment_visibility_changed", fragment.getClass().getSimpleName(), fragment.getClass().getName());
        }
    }

    public void trackTouchEvent(MotionEvent motionEvent, Activity activity) {
        C0740c.m1740a().m1745a(activity, motionEvent);
    }

    public Activity getCurrentActivity() {
        if (this.currentActivity == null || this.currentActivity.get() == null) {
            return null;
        }
        return this.currentActivity.get();
    }

    @Nullable
    public Activity getTargetActivity() {
        if (this.currentActivity != null && this.currentActivity.get() != null && this.currentActivity.get().getParent() != null) {
            Activity parent = this.currentActivity.get().getParent();
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            return parent;
        }
        if (this.currentActivity == null) {
            return null;
        }
        return this.currentActivity.get();
    }

    private void registerActivityLifecycleListener(Application application) {
        if (Build.VERSION.SDK_INT >= 14) {
            InstabugSDKLogger.m1803v(this, "Registering activity lifecycle listener");
            application.registerActivityLifecycleCallbacks(new C0738a());
        }
    }

    private boolean beforeICS() {
        return Build.VERSION.SDK_INT < 14;
    }

    private boolean isUserTrackingStepsEnable() {
        return C0629b.m1160a().m1170b(Feature.TRACK_USER_STEPS) == Feature.State.ENABLED;
    }

    private boolean isReproStepsEnable() {
        return C0629b.m1160a().m1170b(Feature.REPRO_STEPS) == Feature.State.ENABLED;
    }

    private boolean isNotInstabugActivity(Activity activity) {
        return !(activity instanceof _InstabugActivity);
    }
}
