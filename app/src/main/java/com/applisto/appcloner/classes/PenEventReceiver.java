package com.applisto.appcloner.classes;

import android.content.Context;
import android.content.Intent;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.StartExitAppEventReceiver;

/* loaded from: classes2.dex */
public class PenEventReceiver extends StartExitAppEventReceiver {
    private static final String TAG = PenEventReceiver.class.getSimpleName();

    public static void install() {
        Log.m15i(TAG, "install; ");
        init();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; intent: " + intent);
        try {
            String action = intent.getAction();
            if ("com.samsung.pen.INSERT".equals(action)) {
                boolean booleanExtra = intent.getBooleanExtra("penInsert", false);
                Log.m15i(TAG, "onReceive; penInsert: " + booleanExtra);
                if (booleanExtra) {
                    handleEventAction(context, CloneSettings.getInstance(context).getString("penInsertedEventAction", "NONE"));
                } else {
                    handleEventAction(context, CloneSettings.getInstance(context).getString("penDetachedEventAction", "NONE"));
                }
            } else if ("com.sec.android.intent.action.AIR_BUTTON".equals(action) && intent.getBooleanExtra("isShow", false)) {
                handleEventAction(context, CloneSettings.getInstance(context).getString("penButtonPressedEventAction", "NONE"));
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
