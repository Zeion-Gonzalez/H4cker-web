package com.applisto.appcloner.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.applisto.appcloner.classes.util.Log;
import java.util.List;

/* loaded from: classes2.dex */
public class StartForOutgoingCallReceiver extends BroadcastReceiver {
    private static final String TAG = StartForOutgoingCallReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        List<String> stringList;
        Intent launchIntent;
        try {
            String stringExtra = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
            Log.m15i(TAG, "onReceive; phoneNumber: " + stringExtra);
            if (TextUtils.isEmpty(stringExtra) || (stringList = CloneSettings.getInstance(context).getStringList("startForOutgoingCall")) == null || !stringList.contains(stringExtra) || (launchIntent = Utils.getLaunchIntent(context, context.getPackageName())) == null) {
                return;
            }
            launchIntent.setFlags(268435456);
            context.startActivity(launchIntent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
