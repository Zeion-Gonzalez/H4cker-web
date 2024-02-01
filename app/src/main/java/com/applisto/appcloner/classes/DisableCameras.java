package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.util.Properties;

/* loaded from: classes2.dex */
public class DisableCameras extends OnAppExitListener {
    private static final String ACTION_ENABLE_CAMERAS = "com.applisto.appcloner.action.ENABLE_CAMERAS";
    private static final int NOTIFICATION_ID = 556711456;
    private static final String TAG = DisableCameras.class.getSimpleName();
    private static Properties sStringsProperties;

    public void install(Properties properties) {
        Log.m15i(TAG, "install; ");
        sStringsProperties = properties;
        try {
            onCreate();
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        maybeEnableDeviceAdmin(activity);
        disableCameras(activity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        enableCameras(context);
    }

    public void maybeEnableDeviceAdmin(Activity activity) {
        Log.m15i(TAG, "maybeEnableDeviceAdmin; ");
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService("device_policy");
            if (devicePolicyManager != null) {
                ComponentName componentName = new ComponentName(activity, MyDeviceAdminReceiver.class);
                if (devicePolicyManager.isAdminActive(componentName)) {
                    return;
                }
                Intent intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
                intent.putExtra("android.app.extra.DEVICE_ADMIN", componentName);
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disableCameras(Context context) {
        Log.m15i(TAG, "disableCameras; ");
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
            if (devicePolicyManager != null) {
                ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
                if (!devicePolicyManager.isAdminActive(componentName) || devicePolicyManager.getCameraDisabled(componentName)) {
                    return;
                }
                devicePolicyManager.setCameraDisabled(componentName, true);
                Toast.makeText(context, sStringsProperties.getProperty("disable_cameras_cameras_disabled_message"), 1).show();
                showNotification(context);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void enableCameras(Context context) {
        Log.m15i(TAG, "enableCameras; ");
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
            if (devicePolicyManager != null) {
                ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
                if (devicePolicyManager.getCameraDisabled(componentName)) {
                    devicePolicyManager.setCameraDisabled(componentName, false);
                    Toast.makeText(context, sStringsProperties.getProperty("disable_cameras_cameras_enabled_message"), 1).show();
                    hideNotification(context);
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* loaded from: classes2.dex */
    public static class MyDeviceAdminReceiver extends DeviceAdminReceiver {
        @Override // android.app.admin.DeviceAdminReceiver
        public void onEnabled(Context context, Intent intent) {
            Log.m15i(DisableCameras.TAG, "onEnabled; ");
            DisableCameras.disableCameras(context);
        }
    }

    private static void showNotification(Context context) {
        Log.m15i(TAG, "showNotification; ");
        hideNotification(context);
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager != null) {
                String property = sStringsProperties.getProperty("disable_cameras_cameras_disabled_message");
                String property2 = sStringsProperties.getProperty("disable_cameras_touch_to_enable_message");
                Intent intent = new Intent(ACTION_ENABLE_CAMERAS);
                intent.setPackage(context.getPackageName());
                Notification.Builder when = new Notification.Builder(context).setContentTitle(property).setContentText(property2).setContentIntent(PendingIntent.getBroadcast(context, 0, intent, 0)).setWhen(0L);
                Utils.setSmallNotificationIcon(when);
                notificationManager.notify(NOTIFICATION_ID, when.getNotification());
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private static void hideNotification(Context context) {
        Log.m15i(TAG, "hideNotification; ");
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* loaded from: classes2.dex */
    public static class EnableCamerasReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m15i(DisableCameras.TAG, "onReceive; intent: " + intent);
            DisableCameras.enableCameras(context);
        }
    }
}
