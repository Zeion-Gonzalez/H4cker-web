package com.applisto.appcloner.classes.freeform;

import android.app.Activity;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.View;
import com.applisto.appcloner.classes.BuildConfig;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener;

/* loaded from: classes2.dex */
public class FreeFormWindow extends ActivityLifecycleListener {
    public static final String PREF_KEY_FREE_FORM_WINDOW_BOTTOM = "free_form_window_bottom";
    public static final String PREF_KEY_FREE_FORM_WINDOW_LEFT = "free_form_window_left";
    public static final String PREF_KEY_FREE_FORM_WINDOW_RIGHT = "free_form_window_right";
    public static final String PREF_KEY_FREE_FORM_WINDOW_TOP = "free_form_window_top";
    private static final String TAG = FreeFormWindow.class.getSimpleName();

    public FreeFormWindow() {
        onCreate();
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityPaused(Activity activity) {
        saveActivityBounds(activity);
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityStopped(Activity activity) {
        saveActivityBounds(activity);
    }

    private void saveActivityBounds(Activity activity) {
        Rect activityBounds;
        Log.m15i(TAG, "saveActivityBounds; activity: " + activity);
        try {
            if (!activity.getClass().getName().startsWith(BuildConfig.APPLICATION_ID) && (activityBounds = getActivityBounds(activity)) != null && !activityBounds.isEmpty()) {
                Log.m15i(TAG, "saveActivityBounds; rect: " + activityBounds);
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt(PREF_KEY_FREE_FORM_WINDOW_LEFT, activityBounds.left).putInt(PREF_KEY_FREE_FORM_WINDOW_TOP, activityBounds.top).putInt(PREF_KEY_FREE_FORM_WINDOW_RIGHT, activityBounds.right).putInt(PREF_KEY_FREE_FORM_WINDOW_BOTTOM, activityBounds.bottom).apply();
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private Rect getActivityBounds(Activity activity) {
        try {
            View decorView = activity.getWindow().getDecorView();
            int[] iArr = new int[2];
            decorView.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            return new Rect(i, i2, decorView.getWidth() + i, decorView.getHeight() + i2);
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return null;
        }
    }
}
