package com.applisto.appcloner.classes.util.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.view.View;
import com.applisto.appcloner.classes.Utils;
import com.applisto.appcloner.classes.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes2.dex */
public abstract class ActivityLifecycleListener extends AbstractContentProvider {
    private static final String TAG = ActivityLifecycleListener.class.getSimpleName();
    private Set<Activity> mActivities = new HashSet();
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() { // from class: com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (ActivityLifecycleListener.this.mActivities.isEmpty()) {
                    return;
                }
                Iterator it = ActivityLifecycleListener.this.mActivities.iterator();
                while (it.hasNext()) {
                    try {
                        ActivityLifecycleListener.this.onActivityTimer((Activity) it.next());
                    } catch (Exception e) {
                        Log.m21w(ActivityLifecycleListener.TAG, e);
                    }
                }
                ActivityLifecycleListener.this.mHandler.postDelayed(this, ActivityLifecycleListener.this.getActivityTimerDelayMillis());
            } catch (Exception e2) {
                Log.m21w(ActivityLifecycleListener.TAG, e2);
            }
        }
    };

    protected int getActivityTimerDelayMillis() {
        return PathInterpolatorCompat.MAX_NUM_POINTS;
    }

    protected int getActivityTimerInitialDelayMillis() {
        return 0;
    }

    protected void onActivityCreated(Activity activity) {
    }

    protected void onActivityDestroyed(Activity activity) {
    }

    protected void onActivityPaused(Activity activity) {
    }

    protected void onActivityResumed(Activity activity) {
    }

    protected void onActivityStarted(Activity activity) {
    }

    protected void onActivityStopped(Activity activity) {
    }

    protected void onActivityTimer(Activity activity) {
    }

    protected boolean onInit(Application application) {
        return true;
    }

    @Override // com.applisto.appcloner.classes.util.activity.AbstractContentProvider, android.content.ContentProvider
    public boolean onCreate() {
        Application application = Utils.getApplication();
        if (application == null || !onInit(application)) {
            return true;
        }
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener.2
            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityCreated(Activity activity, Bundle bundle) {
                ActivityLifecycleListener.this.onActivityCreated(activity);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStarted(Activity activity) {
                ActivityLifecycleListener.this.onActivityStarted(activity);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityResumed(Activity activity) {
                ActivityLifecycleListener.this.onActivityResumed(activity);
                ActivityLifecycleListener.this.mActivities.add(activity);
                ActivityLifecycleListener.this.mHandler.removeCallbacksAndMessages(null);
                ActivityLifecycleListener.this.mHandler.postDelayed(ActivityLifecycleListener.this.mRunnable, ActivityLifecycleListener.this.getActivityTimerInitialDelayMillis());
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityPaused(Activity activity) {
                ActivityLifecycleListener.this.onActivityPaused(activity);
                ActivityLifecycleListener.this.mActivities.remove(activity);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStopped(Activity activity) {
                ActivityLifecycleListener.this.onActivityStopped(activity);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityDestroyed(Activity activity) {
                ActivityLifecycleListener.this.onActivityDestroyed(activity);
            }
        });
        return true;
    }

    protected View getRootView(Activity activity) {
        return activity.getWindow().getDecorView().getRootView();
    }
}
