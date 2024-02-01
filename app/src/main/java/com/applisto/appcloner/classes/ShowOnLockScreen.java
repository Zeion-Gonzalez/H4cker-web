package com.applisto.appcloner.classes;

import android.app.Activity;
import android.view.Window;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener;

/* loaded from: classes2.dex */
public class ShowOnLockScreen extends ActivityLifecycleListener {
    private static final String TAG = ShowOnLockScreen.class.getSimpleName();
    private boolean mShowOnLockScreen;

    public ShowOnLockScreen(CloneSettings cloneSettings) {
        this.mShowOnLockScreen = cloneSettings.getBoolean("showOnLockScreen", false).booleanValue();
        Log.m15i(TAG, "ShowOnLockScreen; mShowOnLockScreen: " + this.mShowOnLockScreen);
    }

    public void install() {
        Log.m15i(TAG, "install; ");
        if (this.mShowOnLockScreen) {
            onCreate();
        }
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityCreated(Activity activity) {
        Log.m15i(TAG, "onActivityCreated; activity: " + activity);
        Window window = activity.getWindow();
        window.addFlags(4194304);
        window.addFlags(524288);
        window.addFlags(2097152);
    }
}
