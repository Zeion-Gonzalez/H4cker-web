package com.applisto.appcloner.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class UsbDeviceAttachedReceiver extends BroadcastReceiver {
    private static final String TAG = UsbDeviceAttachedReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; intent: " + intent);
        try {
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                Intent launchIntent = Utils.getLaunchIntent(context, context.getPackageName());
                launchIntent.addFlags(268435456);
                context.startActivity(launchIntent);
            }
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }
}
