package com.applisto.appcloner.classes;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.StartExitAppEventReceiver;

/* loaded from: classes2.dex */
public class HeadphonesEventReceiver extends StartExitAppEventReceiver {
    private static final String TAG = HeadphonesEventReceiver.class.getSimpleName();

    public static void install(Context context) {
        Log.m15i(TAG, "install; ");
        init();
        try {
            context.registerReceiver(new HeadphonesEventReceiver(), new IntentFilter("android.intent.action.HEADSET_PLUG"));
            context.startService(new Intent(context, PersistentAppService.class));
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; intent: " + intent);
        try {
            if (!isInitialStickyBroadcast() && "android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("state", -1);
                boolean z = intExtra != 0;
                Log.m15i(TAG, "onReceive; state: " + intExtra + ", plugged: " + z);
                if (z) {
                    handleEventAction(context, CloneSettings.getInstance(context).getString("headphonesPluggedEventAction", "NONE"));
                } else {
                    handleEventAction(context, CloneSettings.getInstance(context).getString("headphonesUnpluggedEventAction", "NONE"));
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
