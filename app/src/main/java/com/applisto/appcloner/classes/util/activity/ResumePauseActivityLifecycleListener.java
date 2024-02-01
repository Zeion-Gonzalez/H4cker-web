package com.applisto.appcloner.classes.util.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import com.applisto.appcloner.classes.util.Log;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public abstract class ResumePauseActivityLifecycleListener extends ActivityLifecycleListener {
    private static final String TAG = ResumePauseActivityLifecycleListener.class.getSimpleName();
    private Set<Activity> mActivities = new HashSet();
    private Handler mHandler = new Handler();
    private boolean mResumed;

    protected abstract void onPaused(Context context);

    protected abstract void onResumed(Activity activity);

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityResumed(Activity activity) {
        Log.m15i(TAG, "onActivityResumed; activity: " + activity);
        if (!this.mResumed && !activity.getClass().getName().startsWith("com.applisto.appcloner.classes.PasswordActivity")) {
            onResumed(activity);
            this.mResumed = true;
        }
        this.mHandler.removeCallbacksAndMessages(null);
        this.mActivities.add(activity);
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityPaused(Activity activity) {
        Log.m15i(TAG, "onActivityPaused; activity: " + activity);
        this.mActivities.remove(activity);
        if (this.mActivities.isEmpty()) {
            final Context applicationContext = activity.getApplicationContext();
            this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.util.activity.-$$Lambda$ResumePauseActivityLifecycleListener$7tqTc6qJhzTG1_84iLl08h9pnaM
                @Override // java.lang.Runnable
                public final void run() {
                    ResumePauseActivityLifecycleListener.this.lambda$onActivityPaused$0$ResumePauseActivityLifecycleListener(applicationContext);
                }
            }, 1000L);
        }
    }

    public /* synthetic */ void lambda$onActivityPaused$0$ResumePauseActivityLifecycleListener(Context context) {
        onPaused(context);
        this.mResumed = false;
    }
}
