package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.util.Properties;

/* loaded from: classes2.dex */
public class InterruptionFilterControls extends OnAppExitListener {
    private static final String TAG = InterruptionFilterControls.class.getSimpleName();
    private Integer mInterruptionFilter;
    private boolean mInterruptionFilterSet;
    private int mOldInterruptionFilter;
    private boolean mRestoreInterruptionFilterOnExit;
    private Properties mStringsProperties;

    public InterruptionFilterControls(CloneSettings cloneSettings) {
        this.mInterruptionFilter = cloneSettings.getInteger("interruptionFilter", null);
        this.mRestoreInterruptionFilterOnExit = cloneSettings.getBoolean("restoreInterruptionFilterOnExit", false).booleanValue();
        Log.m15i(TAG, "WifiControls; mInterruptionFilter: " + this.mInterruptionFilter);
        Log.m15i(TAG, "WifiControls; mRestoreInterruptionFilterOnExit: " + this.mRestoreInterruptionFilterOnExit);
    }

    public void install(Properties properties) {
        Log.m15i(TAG, "install; ");
        this.mStringsProperties = properties;
        if (this.mInterruptionFilter != null) {
            onCreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                NotificationManager notificationManager = (NotificationManager) activity.getSystemService("notification");
                if (notificationManager != null) {
                    boolean isNotificationPolicyAccessGranted = notificationManager.isNotificationPolicyAccessGranted();
                    Log.m15i(TAG, "onActivityCreated; accessGranted: " + isNotificationPolicyAccessGranted);
                    if (!isNotificationPolicyAccessGranted) {
                        activity.startActivity(new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"));
                        Toast.makeText(activity, this.mStringsProperties.getProperty("do_not_disturb_enable_access_message").replace("%s", Utils.getAppName(activity)), 1).show();
                    } else if (!this.mInterruptionFilterSet) {
                        this.mOldInterruptionFilter = notificationManager.getCurrentInterruptionFilter();
                        notificationManager.setInterruptionFilter(this.mInterruptionFilter.intValue());
                        Log.m15i(TAG, "onActivityCreated: interruption filter set: " + this.mInterruptionFilter);
                        Log.m15i(TAG, "onActivityCreated; mOldInterruptionFilter: " + this.mOldInterruptionFilter);
                        this.mInterruptionFilterSet = true;
                    }
                }
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.m15i(TAG, "onAppExit; mRestoreInterruptionFilterOnExit: " + this.mRestoreInterruptionFilterOnExit + ", mOldInterruptionFilter: " + this.mOldInterruptionFilter);
            if (this.mRestoreInterruptionFilterOnExit) {
                try {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                    if (notificationManager != null) {
                        notificationManager.setInterruptionFilter(this.mOldInterruptionFilter);
                        Log.m15i(TAG, "onAppExit: interruption filter set: " + this.mOldInterruptionFilter);
                    }
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                }
                this.mInterruptionFilterSet = false;
            }
        }
    }
}
