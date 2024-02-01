package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.Properties;

/* loaded from: classes2.dex */
public class DataDirectoryFtpServer extends OnAppExitListener {
    private static final int NOTIFICATION_ID = 516311456;
    private static final String PASSWORD = "appcloner";
    private static final String TAG = DataDirectoryFtpServer.class.getSimpleName();
    private static final String USER_NAME = "appcloner";
    private static int sPort;
    private static Boolean sStarted;
    private static Properties sStringsProperties;

    public void install(Properties properties) {
        Log.m15i(TAG, "install; ");
        sStringsProperties = properties;
        onCreate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        startFtpServer(activity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        stopFtpServer(context);
        sStarted = null;
    }

    /* loaded from: classes2.dex */
    public static class StopFtpServerReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m15i(DataDirectoryFtpServer.TAG, "onReceive; intent: " + intent);
            DataDirectoryFtpServer.stopFtpServer(context);
        }
    }

    private static void startFtpServer(final Context context) {
        Log.m15i(TAG, "startFtpServer; sStarted: " + sStarted);
        if (sStarted == null) {
            try {
                if (sPort == 0) {
                    sPort = getFreePort();
                    Log.m15i(TAG, "startFtpServer; generated new port; sPort: " + sPort);
                }
                String parent = context.getFilesDir().getParent();
                Method method = Class.forName("com.applisto.appcloner.ftpserver.AppClonerFtpServer").getMethod("startFtpServer", Integer.TYPE, String.class, String.class, String.class);
                try {
                    method.invoke(null, Integer.valueOf(sPort), "appcloner", "appcloner", parent);
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                    sPort = getFreePort();
                    Log.m15i(TAG, "startFtpServer; generated new port; sPort: " + sPort);
                    method.invoke(null, Integer.valueOf(sPort), "appcloner", "appcloner", parent);
                }
                sStarted = true;
                showNotification(context, true);
                new Handler().postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$DataDirectoryFtpServer$UpJdbCtrYxLd2xRLShZFusSzt_o
                    @Override // java.lang.Runnable
                    public final void run() {
                        DataDirectoryFtpServer.showNotification(context, false);
                    }
                }, 1000L);
            } catch (Throwable th) {
                Log.m21w(TAG, th);
                Toast.makeText(context, sStringsProperties.getProperty("data_directory_ftp_server_started_error_message"), 0).show();
                sStarted = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showNotification(Context context, boolean z) {
        Log.m15i(TAG, "showNotification; ");
        try {
            if (sStarted == null || !sStarted.booleanValue()) {
                return;
            }
            String property = sStringsProperties.getProperty("data_directory_ftp_server_started_message");
            Inet4Address inet4Address = (Inet4Address) Utils.getWifiInetAddress(Inet4Address.class);
            StringBuilder sb = new StringBuilder();
            sb.append("ftp://appcloner:appcloner@");
            sb.append(inet4Address != null ? inet4Address.getHostAddress() : "localhost");
            sb.append(":");
            sb.append(sPort);
            String sb2 = sb.toString();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager != null) {
                Notification.Builder ongoing = new Notification.Builder(context).setContentTitle(property).setContentText(sb2).setOngoing(true);
                if (Build.VERSION.SDK_INT >= 16) {
                    ongoing.setStyle(new Notification.BigTextStyle().setBigContentTitle(property));
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    ongoing.addAction(0, "Stop FTP server", PendingIntent.getBroadcast(context, 0, new Intent(null, null, context, StopFtpServerReceiver.class), 1073741824));
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    ongoing.setVisibility(-1);
                }
                Utils.setSmallNotificationIcon(ongoing, true);
                Notification notification = ongoing.getNotification();
                notification.sound = null;
                notification.defaults &= -2;
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
            if (z) {
                Toast.makeText(context, property + " " + sb2, 1).show();
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private static int getFreePort() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        try {
            return serverSocket.getLocalPort();
        } finally {
            serverSocket.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void stopFtpServer(Context context) {
        Log.m15i(TAG, "stopFtpServer; sStarted: " + sStarted);
        Boolean bool = sStarted;
        if (bool != null && bool.booleanValue()) {
            try {
                Class.forName("com.applisto.appcloner.ftpserver.AppClonerFtpServer").getMethod("stopFtpServer", new Class[0]).invoke(null, new Object[0]);
                sStarted = false;
            } catch (Exception e) {
                Log.m21w(TAG, e);
                Toast.makeText(context, "Failed to stop FTP server.", 0).show();
            }
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
