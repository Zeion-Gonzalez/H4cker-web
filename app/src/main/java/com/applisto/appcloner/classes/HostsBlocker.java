package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.applisto.appcloner.classes.HostsBlocker;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcodec.codecs.common.biari.MQEncoder;

/* loaded from: classes2.dex */
public class HostsBlocker {
    private static final String PREF_KEY_PREFIX = "com.applisto.appcloner.host_";
    private static boolean sAllowAllOtherHosts;
    private static String sAllowLabel;
    private static boolean sBlockByDefault;
    private static String sBlockLabel;
    private static String sContentText;
    private static String sContentTitle;
    private static Context sContext;
    private static String sHostBlockedMessage;
    private static String sHostsBlockerTitle;
    private static String sIgnoreLabel;
    private static NotificationManager sNotificationManager;
    private static SharedPreferences sPreferences;
    private static String sSocksProxyHost;
    private static final String TAG = HostsBlocker.class.getSimpleName();
    private static Map<String, Boolean> sHostsFileHosts = new HashMap();
    private static final Map<String, Boolean> sHosts = Collections.synchronizedMap(new HashMap());
    private static final Map<Integer, String> sNotifications = Collections.synchronizedMap(new HashMap());
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void install(Context context, boolean z, String str, boolean z2, Properties properties) {
        sContext = context;
        sBlockByDefault = z;
        sSocksProxyHost = str;
        sAllowAllOtherHosts = z2;
        sNotificationManager = (NotificationManager) context.getSystemService("notification");
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        synchronized (sHosts) {
            try {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(".blockedHostsFile")));
                    try {
                        Pattern compile = Pattern.compile("(0.0.0.0|127.0.0.1|# x.x.x.x)\\s+(\\S*)");
                        while (true) {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            String trim = readLine.trim();
                            if (trim.startsWith("# x.x.x.x") || !trim.startsWith("#")) {
                                Matcher matcher = compile.matcher(trim);
                                if (matcher.find()) {
                                    String group = matcher.group(2);
                                    boolean equals = "# x.x.x.x".equals(matcher.group(1));
                                    Log.m15i(TAG, "install; host: " + group + ", allowedBlocked: " + equals);
                                    sHostsFileHosts.put(group, Boolean.valueOf(equals));
                                }
                            }
                        }
                        bufferedReader.close();
                    } finally {
                    }
                } catch (FileNotFoundException | Exception unused) {
                }
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
            for (String str2 : sPreferences.getAll().keySet()) {
                if (str2 != null && str2.startsWith(PREF_KEY_PREFIX)) {
                    sHosts.put(str2.substring(28), Boolean.valueOf(sPreferences.getBoolean(str2, false)));
                }
            }
            Log.m15i(TAG, "install; sHosts: " + sHosts);
        }
        Hooking.initHooking(context);
        Hooking.addHookClass(Hook.class);
        Log.m15i(TAG, "install; hooks installed");
        sHostsBlockerTitle = properties.getProperty("hosts_blocker_title");
        sContentTitle = properties.getProperty("hosts_blocker_app_is_accessing_title");
        sContentText = properties.getProperty("hosts_blocker_touch_to_block_title");
        sAllowLabel = properties.getProperty("label_allow");
        sBlockLabel = properties.getProperty("label_block");
        sIgnoreLabel = properties.getProperty("label_ignore");
        sHostBlockedMessage = properties.getProperty("hosts_blocker_host_blocked_message");
    }

    @HookReflectClass("java.net.PlainSocketImpl")
    /* loaded from: classes2.dex */
    public static class Hook {
        @HookMethodBackup("socketConnect")
        @MethodParams({InetAddress.class, int.class, int.class})
        static Method socketConnectBackup;

        @MethodParams({InetAddress.class, int.class, int.class})
        @HookMethod("socketConnect")
        public static void socketConnectHook(Object obj, InetAddress inetAddress, int i, int i2) throws Throwable {
            Log.m15i(HostsBlocker.TAG, "socketConnectHook; address: " + inetAddress + ", port: " + i + ", timeout: " + i2);
            HostsBlocker.checkHost(inetAddress);
            Hooking.callInstanceOrigin(socketConnectBackup, obj, inetAddress, Integer.valueOf(i), Integer.valueOf(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkHost(InetAddress inetAddress) throws UnknownHostException {
        if (inetAddress == null) {
            return;
        }
        checkHost(inetAddress.getHostName());
    }

    private static void checkHost(final String str) throws UnknownHostException {
        if (str == null || str.length() <= 0) {
            return;
        }
        String str2 = sSocksProxyHost;
        if ((str2 != null && str2.equals(str)) || "127.0.0.1".equals(str) || "localhost".equals(str) || str.startsWith("192.168.")) {
            return;
        }
        for (String str3 : splitHost(str)) {
            if (sHosts.containsKey(str3)) {
                Boolean bool = sHosts.get(str3);
                if (bool == null || bool.booleanValue()) {
                    Log.m15i(TAG, "checkHost; ignored/allowed; host: " + str);
                    return;
                }
                Log.m15i(TAG, "checkHost; blocked; host: " + str);
                throw new UnknownHostException("Blocked");
            }
            if (sHostsFileHosts.containsKey(str3)) {
                if (sHostsFileHosts.get(str3).booleanValue()) {
                    addAllowedHost(str);
                    return;
                }
                addBlockedHost(str);
                sHandler.post(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$_USfTmCXNR_6TuWfuzd6lXwjP6c
                    @Override // java.lang.Runnable
                    public final void run() {
                        Toast.makeText(HostsBlocker.sContext, HostsBlocker.sHostBlockedMessage.replace("%s", str), 0).show();
                    }
                });
                Log.m15i(TAG, "checkHost; blocked; host: " + str);
                throw new UnknownHostException("Blocked");
            }
        }
        if (sAllowAllOtherHosts) {
            return;
        }
        showNotification(str);
        if (sBlockByDefault) {
            throw new UnknownHostException("Blocked by default");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<String> splitHost(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        int length = str.length();
        boolean z = false;
        int i = 0;
        while (true) {
            if (i < length) {
                char charAt = str.charAt(i);
                if (charAt != '.' && !Character.isDigit(charAt)) {
                    z = true;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (z) {
            while (true) {
                int indexOf = str.indexOf(46);
                if (indexOf == -1) {
                    break;
                }
                str = str.substring(indexOf + 1);
                if (str.indexOf(46) == -1) {
                    break;
                }
                arrayList.add("*." + str);
            }
        }
        return arrayList;
    }

    private static void showNotification(String str) {
        int hashCode = str.hashCode();
        synchronized (sNotifications) {
            if (sNotifications.containsKey(Integer.valueOf(hashCode))) {
                return;
            }
            sNotifications.put(Integer.valueOf(hashCode), str);
            Log.m15i(TAG, "showNotification; host: " + str);
            int hashCode2 = str.hashCode();
            Context context = sContext;
            PendingIntent broadcast = PendingIntent.getBroadcast(context, hashCode2, new Intent(str, null, context, ContentReceiver.class), MQEncoder.CARRY_MASK);
            Context context2 = sContext;
            PendingIntent broadcast2 = PendingIntent.getBroadcast(context2, hashCode2 + 1, new Intent(str, null, context2, AllowReceiver.class), 1073741824);
            Context context3 = sContext;
            PendingIntent broadcast3 = PendingIntent.getBroadcast(context3, hashCode2 + 2, new Intent(str, null, context3, BlockReceiver.class), 1073741824);
            Context context4 = sContext;
            PendingIntent broadcast4 = PendingIntent.getBroadcast(context4, hashCode2 + 3, new Intent(str, null, context4, IgnoreReceiver.class), 1073741824);
            Notification.Builder autoCancel = new Notification.Builder(sContext).setSmallIcon(17301543).setContentTitle(sContentTitle.replace("%s", Utils.getAppName(sContext))).setContentIntent(broadcast).setDeleteIntent(PendingIntent.getBroadcast(sContext, hashCode2 + 4, new Intent(Integer.toString(hashCode), null, sContext, CancelReceiver.class), 1073741824)).setAutoCancel(false);
            if (Build.VERSION.SDK_INT >= 16) {
                autoCancel.setContentText(str).addAction(hashCode2, sAllowLabel, broadcast2).addAction(hashCode2, sBlockLabel, broadcast3).addAction(hashCode2, sIgnoreLabel, broadcast4);
            } else {
                autoCancel.setContentText(sContentText.replace("%s", str)).setContentIntent(broadcast3);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                autoCancel.setVisibility(-1);
            }
            Utils.setSmallNotificationIcon(autoCancel);
            Notification notification = autoCancel.getNotification();
            notification.sound = null;
            notification.defaults &= -2;
            sNotificationManager.notify(hashCode, notification);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addAllowedHost(String str) {
        Log.m15i(TAG, "addAllowedHost; host: " + str);
        synchronized (sPreferences) {
            sHosts.put(str, true);
            sPreferences.edit().putBoolean(PREF_KEY_PREFIX + str, true).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addBlockedHost(String str) {
        Log.m15i(TAG, "addBlockedHost; host: " + str);
        synchronized (sPreferences) {
            sHosts.put(str, false);
            sPreferences.edit().putBoolean(PREF_KEY_PREFIX + str, false).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addIgnoredHost(String str) {
        Log.m15i(TAG, "addIgnoredHost; host: " + str);
        synchronized (sPreferences) {
            sHosts.put(str, null);
            sPreferences.edit().remove(PREF_KEY_PREFIX + str).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void whois(Context context, String str) {
        Log.m15i(TAG, "whois; host: " + str);
        if (str.startsWith("*.")) {
            str = str.substring(2);
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(268435456);
            intent.setData(Uri.parse("http://whois.domaintools.com/" + str));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
            Toast.makeText(context, "Failed to open browser.", 1).show();
        }
    }

    public static Map<String, Boolean> getAllowedBlockedHosts() {
        if (sPreferences == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        synchronized (sPreferences) {
            for (String str : sPreferences.getAll().keySet()) {
                if (str != null && str.startsWith(PREF_KEY_PREFIX)) {
                    hashMap.put(str.substring(28), Boolean.valueOf(sPreferences.getBoolean(str, false)));
                }
            }
        }
        return hashMap;
    }

    public static void setAllowedBlockedHosts(Map<String, Boolean> map) {
        for (String str : map.keySet()) {
            Boolean bool = map.get(str);
            if (bool == null) {
                addIgnoredHost(str);
            } else if (bool.booleanValue()) {
                addAllowedHost(str);
            } else {
                addBlockedHost(str);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class HostReceiver extends BroadcastReceiver {
        protected abstract void handleHost(Context context, String str);

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }
                handleHost(context, action);
            } catch (Exception e) {
                Log.m21w(HostsBlocker.TAG, e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ContentReceiver extends HostReceiver {
        @Override // com.applisto.appcloner.classes.HostsBlocker.HostReceiver
        protected void handleHost(Context context, String str) {
            Log.m15i(HostsBlocker.TAG, "ContentReceiver; handleHost; host: " + str);
            Intent intent = new Intent(context, HostsBlockerActivity.class);
            intent.setFlags(268435456);
            intent.putExtra("host", str);
            context.startActivity(intent);
        }
    }

    /* loaded from: classes2.dex */
    public static class AllowReceiver extends HostReceiver {
        @Override // com.applisto.appcloner.classes.HostsBlocker.HostReceiver
        protected void handleHost(Context context, String str) {
            Log.m15i(HostsBlocker.TAG, "AllowReceiver; handleHost; host: " + str);
            HostsBlocker.addAllowedHost(str);
            HostsBlocker.hideNotification(str);
        }
    }

    /* loaded from: classes2.dex */
    public static class BlockReceiver extends HostReceiver {
        @Override // com.applisto.appcloner.classes.HostsBlocker.HostReceiver
        protected void handleHost(Context context, String str) {
            Log.m15i(HostsBlocker.TAG, "BlockReceiver; handleHost; host: " + str);
            HostsBlocker.addBlockedHost(str);
            HostsBlocker.hideNotification(str);
        }
    }

    /* loaded from: classes2.dex */
    public static class IgnoreReceiver extends HostReceiver {
        @Override // com.applisto.appcloner.classes.HostsBlocker.HostReceiver
        protected void handleHost(Context context, String str) {
            Log.m15i(HostsBlocker.TAG, "IgnoreReceiver; handleHost; host: " + str);
            HostsBlocker.addIgnoredHost(str);
            HostsBlocker.hideNotification(str);
        }
    }

    /* loaded from: classes2.dex */
    public static class CancelReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.m15i(HostsBlocker.TAG, "CancelReceiver; onReceive; notificationId: " + action);
                HostsBlocker.sNotifications.remove(Integer.valueOf(Integer.parseInt(action)));
            } catch (Exception e) {
                Log.m21w(HostsBlocker.TAG, e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class HostsBlockerActivity extends Activity {
        @Override // android.app.Activity
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            String stringExtra = getIntent().getStringExtra("host");
            Log.m15i(HostsBlocker.TAG, "onCreate; host: " + stringExtra);
            final String[] strArr = (String[]) HostsBlocker.splitHost(stringExtra).toArray(new String[0]);
            final AtomicReference atomicReference = new AtomicReference(strArr[0]);
            AlertDialog.Builder singleChoiceItems = Utils.getDialogBuilder(this).setTitle(HostsBlocker.sHostsBlockerTitle).setSingleChoiceItems(strArr, 0, new DialogInterface.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$O0vfs8oynLufkBJUVRjJyjUliYY
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    atomicReference.set(strArr[i]);
                }
            });
            if (Build.VERSION.SDK_INT > 21) {
                singleChoiceItems.setNeutralButton("Whois", (DialogInterface.OnClickListener) null).setNegativeButton(HostsBlocker.sAllowLabel, new DialogInterface.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$xwX4UbsB94guR3MUPHjKfxODyBg
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        HostsBlocker.HostsBlockerActivity.lambda$onCreate$1(atomicReference, dialogInterface, i);
                    }
                }).setPositiveButton(HostsBlocker.sBlockLabel, new DialogInterface.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$GhW8LIlPObOrgtqj-t2hesBvbBY
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        HostsBlocker.HostsBlockerActivity.lambda$onCreate$2(atomicReference, dialogInterface, i);
                    }
                });
            } else {
                singleChoiceItems.setNegativeButton("Whois", (DialogInterface.OnClickListener) null).setNeutralButton(HostsBlocker.sAllowLabel, new DialogInterface.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$MvDXewRy7CjKdu39aJdYCOiUFT4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        HostsBlocker.HostsBlockerActivity.lambda$onCreate$3(atomicReference, dialogInterface, i);
                    }
                }).setPositiveButton(HostsBlocker.sBlockLabel, new DialogInterface.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$vGwmOuSw8UKQpS1A8MTvSP9oeZI
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        HostsBlocker.HostsBlockerActivity.lambda$onCreate$4(atomicReference, dialogInterface, i);
                    }
                });
            }
            AlertDialog create = singleChoiceItems.create();
            create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$MDtJtxB3eBafPfMMhbREgZlk7qs
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    HostsBlocker.HostsBlockerActivity.this.lambda$onCreate$5$HostsBlocker$HostsBlockerActivity(dialogInterface);
                }
            });
            create.show();
            if (Build.VERSION.SDK_INT > 21) {
                create.getButton(-3).setOnClickListener(new View.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$G504BLbUiHhV46dpbVKtBe20M6s
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        HostsBlocker.HostsBlockerActivity.this.lambda$onCreate$6$HostsBlocker$HostsBlockerActivity(atomicReference, view);
                    }
                });
            } else {
                create.getButton(-2).setOnClickListener(new View.OnClickListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$HostsBlocker$HostsBlockerActivity$RmDpLjz8j3kWM6u_GoiKg9gtl8w
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        HostsBlocker.HostsBlockerActivity.this.lambda$onCreate$7$HostsBlocker$HostsBlockerActivity(atomicReference, view);
                    }
                });
            }
            Utils.keepDialogOpenOnOrientationChange(create);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onCreate$1(AtomicReference atomicReference, DialogInterface dialogInterface, int i) {
            HostsBlocker.addAllowedHost((String) atomicReference.get());
            HostsBlocker.hideNotification((String) atomicReference.get());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onCreate$2(AtomicReference atomicReference, DialogInterface dialogInterface, int i) {
            HostsBlocker.addBlockedHost((String) atomicReference.get());
            HostsBlocker.hideNotification((String) atomicReference.get());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onCreate$3(AtomicReference atomicReference, DialogInterface dialogInterface, int i) {
            HostsBlocker.addAllowedHost((String) atomicReference.get());
            HostsBlocker.hideNotification((String) atomicReference.get());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onCreate$4(AtomicReference atomicReference, DialogInterface dialogInterface, int i) {
            HostsBlocker.addBlockedHost((String) atomicReference.get());
            HostsBlocker.hideNotification((String) atomicReference.get());
        }

        public /* synthetic */ void lambda$onCreate$5$HostsBlocker$HostsBlockerActivity(DialogInterface dialogInterface) {
            finish();
        }

        public /* synthetic */ void lambda$onCreate$6$HostsBlocker$HostsBlockerActivity(AtomicReference atomicReference, View view) {
            HostsBlocker.whois(this, (String) atomicReference.get());
        }

        public /* synthetic */ void lambda$onCreate$7$HostsBlocker$HostsBlockerActivity(AtomicReference atomicReference, View view) {
            HostsBlocker.whois(this, (String) atomicReference.get());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void hideNotification(String str) {
        Log.m15i(TAG, "hideNotification; host: " + str);
        try {
            sNotificationManager.cancel(str.hashCode());
            if (str.startsWith("*.")) {
                str = str.substring(2);
            }
            synchronized (sNotifications) {
                for (Integer num : sNotifications.keySet()) {
                    if (sNotifications.get(num).endsWith(str)) {
                        sNotificationManager.cancel(num.intValue());
                    }
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
