package com.applisto.appcloner.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class MediaMountEjectReceiver extends BroadcastReceiver {
    private static final String TAG = MediaMountEjectReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; intent: " + intent);
        try {
            String action = intent.getAction();
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                Intent launchIntent = Utils.getLaunchIntent(context, context.getPackageName());
                launchIntent.addFlags(268435456);
                context.startActivity(launchIntent);
            } else if ("android.intent.action.MEDIA_EJECT".equals(action)) {
                DefaultProvider.invokeSecondaryInstance("util.Utils", "killAppProcesses", context);
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }
}
