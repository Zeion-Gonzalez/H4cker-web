package com.applisto.appcloner.classes.util.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.applisto.appcloner.classes.Utils;
import com.applisto.appcloner.classes.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes2.dex */
public abstract class StartExitAppEventReceiver extends BroadcastReceiver {
    private static final String TAG = StartExitAppEventReceiver.class.getSimpleName();
    private static final Set<Activity> sActivities = new HashSet();
    private static boolean sInited;

    /* JADX INFO: Access modifiers changed from: protected */
    public static void init() {
        try {
            if (sInited) {
                return;
            }
            Log.m15i(TAG, "init; ");
            new ActivityLifecycleListener() { // from class: com.applisto.appcloner.classes.util.activity.StartExitAppEventReceiver.1
                @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
                protected void onActivityCreated(Activity activity) {
                    StartExitAppEventReceiver.sActivities.add(activity);
                }

                @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
                protected void onActivityDestroyed(Activity activity) {
                    StartExitAppEventReceiver.sActivities.remove(activity);
                }
            }.onCreate();
            sInited = true;
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleEventAction(Context context, String str) {
        Log.m15i(TAG, "handleAction; eventAction: " + str);
        if ("START_APP".equals(str)) {
            startApp(context);
        } else if ("EXIT_APP".equals(str)) {
            exitApp();
        }
    }

    private void startApp(Context context) {
        Log.m15i(TAG, "startApp; ");
        try {
            Intent launchIntent = Utils.getLaunchIntent(context, context.getPackageName());
            if (launchIntent != null) {
                launchIntent.setFlags(335544320);
                context.startActivity(launchIntent);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private void exitApp() {
        Log.m15i(TAG, "exitApp; ");
        Iterator<Activity> it = sActivities.iterator();
        while (it.hasNext()) {
            try {
                it.next().finish();
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
    }
}
