package com.applisto.appcloner.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class ClearCacheReceiver extends BroadcastReceiver {
    private static final String TAG = ClearCacheReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.m15i(TAG, "onReceive; intent: " + intent);
        ClearCacheOnExitProvider.clearCache(context, false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            Notification.Builder when = new Notification.Builder(context).setContentTitle(Utils.getAppName(context)).setContentText(Utils.getStringsProperties().getProperty("cache_cleared_message")).setWhen(System.currentTimeMillis());
            Utils.setSmallNotificationIcon(when);
            notificationManager.notify(474329107, when.getNotification());
        }
    }
}
