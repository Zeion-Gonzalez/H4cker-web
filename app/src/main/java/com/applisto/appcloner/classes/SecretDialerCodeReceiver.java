package com.applisto.appcloner.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class SecretDialerCodeReceiver extends BroadcastReceiver {
    private static final String TAG = SecretDialerCodeReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; context: " + context);
        try {
            if (TextUtils.isEmpty(CloneSettings.getInstance(context).getString("secretDialerCode", null))) {
                return;
            }
            Intent launchIntent = Utils.getLaunchIntent(context, context.getPackageName());
            launchIntent.addFlags(268435456);
            context.startActivity(launchIntent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
            Toast.makeText(context, "Failed to launch app.", 0).show();
        }
    }
}
