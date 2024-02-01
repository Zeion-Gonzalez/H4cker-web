package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.Properties;
import org.jcodec.codecs.common.biari.MQEncoder;

/* loaded from: classes2.dex */
public class DatabaseEditor extends OnAppExitListener {
    private static final int NOTIFICATION_ID = 516311457;
    private static final String TAG = DatabaseEditor.class.getSimpleName();
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
        startDatabaseEditor(activity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        stopDatabaseEditor(context);
        sStarted = null;
    }

    /* loaded from: classes2.dex */
    public static class StopDatabaseEditorReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m15i(DatabaseEditor.TAG, "onReceive; intent: " + intent);
            DatabaseEditor.stopDatabaseEditor(context);
        }
    }

    private static void startDatabaseEditor(Context context) {
        Log.m15i(TAG, "startDatabaseEditor; sStarted: " + sStarted);
        if (sStarted == null) {
            try {
                if (sPort == 0) {
                    sPort = getFreePort();
                    Log.m15i(TAG, "startDatabaseEditor; generated new port; sPort: " + sPort);
                }
                Class.forName("com.amitshekhar.DebugDB").getMethod("initialize", Context.class, Class.forName("com.amitshekhar.sqlite.DBFactory"), Integer.TYPE).invoke(null, context, Class.forName("com.amitshekhar.debug.sqlite.DebugDBFactory").getConstructor(new Class[0]).newInstance(new Object[0]), Integer.valueOf(sPort));
                sStarted = true;
                String property = sStringsProperties.getProperty("database_editor_started_message");
                Inet4Address inet4Address = (Inet4Address) Utils.getWifiInetAddress(Inet4Address.class);
                StringBuilder sb = new StringBuilder();
                sb.append("http://");
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
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb2));
                    intent.setFlags(268435456);
                    ongoing.setContentIntent(PendingIntent.getActivity(context, 0, intent, MQEncoder.CARRY_MASK));
                    if (Build.VERSION.SDK_INT >= 16) {
                        ongoing.addAction(0, "Stop database editor", PendingIntent.getBroadcast(context, 0, new Intent(null, null, context, StopDatabaseEditorReceiver.class), 1073741824));
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        ongoing.setVisibility(-1);
                    }
                    Utils.setSmallNotificationIcon(ongoing, true);
                    Notification notification = ongoing.getNotification();
                    notification.sound = null;
                    notification.defaults &= -2;
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    return;
                }
                Toast.makeText(context, property + " " + sb2, 1).show();
            } catch (Throwable th) {
                Log.m21w(TAG, th);
                Toast.makeText(context, sStringsProperties.getProperty("database_editor_started_error_message"), 0).show();
                sStarted = false;
            }
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
    public static void stopDatabaseEditor(Context context) {
        Log.m15i(TAG, "stopDatabaseEditor; sStarted: " + sStarted);
        Boolean bool = sStarted;
        if (bool != null && bool.booleanValue()) {
            try {
                Class.forName("com.amitshekhar.DebugDB").getMethod("shutDown", new Class[0]).invoke(null, new Object[0]);
                sStarted = false;
            } catch (Exception e) {
                Log.m21w(TAG, e);
                Toast.makeText(context, "Failed to stop database editor.", 0).show();
            }
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
