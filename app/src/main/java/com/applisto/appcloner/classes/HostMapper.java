package com.applisto.appcloner.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.hooking.Hooking;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: classes2.dex */
public class HostMapper {
    private static Context sContext;
    private static Map<String, String> sMappedHosts;
    private static NotificationManager sNotificationManager;
    private static boolean sSilent;
    private static String sSocksProxyHost;
    private static Properties sStringsProperties;
    private static final String TAG = HostMapper.class.getSimpleName();
    private static final Map<Integer, String> sNotifications = Collections.synchronizedMap(new HashMap());

    public static void install(Context context, Map<String, String> map, boolean z, String str, Properties properties) {
        Log.m15i(TAG, "install; mappedHosts: " + map + ", silent: " + z + ", socksProxyHost: " + str);
        sContext = context;
        sMappedHosts = map;
        sSilent = z;
        sSocksProxyHost = str;
        sStringsProperties = properties;
        sNotificationManager = (NotificationManager) context.getSystemService("notification");
        Hooking.initHooking(context);
        Hooking.addHookClass(Hook.class);
        Log.m15i(TAG, "install; hooks installed");
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
            Log.m15i(HostMapper.TAG, "socketConnectHook; address: " + inetAddress + ", port: " + i + ", timeout: " + i2);
            if (inetAddress != null) {
                try {
                    String hostName = inetAddress.getHostName();
                    if (hostName != null && hostName.length() > 0 && (HostMapper.sSocksProxyHost == null || !HostMapper.sSocksProxyHost.equals(hostName))) {
                        String str = null;
                        List<String> splitHost = HostsBlocker.splitHost(hostName);
                        Iterator<String> it = splitHost.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            String next = it.next();
                            str = (String) HostMapper.sMappedHosts.get(next + ':' + i);
                            if (str != null) {
                                Log.m15i(HostMapper.TAG, "socketConnectHook; mappedHost: " + str);
                                break;
                            }
                        }
                        if (str == null) {
                            Iterator<String> it2 = splitHost.iterator();
                            while (true) {
                                if (!it2.hasNext()) {
                                    break;
                                }
                                str = (String) HostMapper.sMappedHosts.get(it2.next());
                                if (str != null) {
                                    Log.m15i(HostMapper.TAG, "socketConnectHook; mappedHost: " + str);
                                    break;
                                }
                            }
                        }
                        if (!HostMapper.sSilent) {
                            HostMapper.showNotification(hostName + ':' + i, str);
                        }
                        if (str != null) {
                            int indexOf = str.indexOf(58);
                            if (indexOf != -1) {
                                try {
                                    i = Integer.parseInt(str.substring(indexOf + 1));
                                } catch (Exception e) {
                                    Log.m21w(HostMapper.TAG, e);
                                }
                                str = str.substring(0, indexOf);
                            }
                            inetAddress = InetAddress.getByName(str);
                            Log.m15i(HostMapper.TAG, "socketConnectHook; address: " + inetAddress + ", port: " + i);
                        }
                    }
                } catch (Exception e2) {
                    Log.m21w(HostMapper.TAG, e2);
                }
            }
            Hooking.callInstanceOrigin(socketConnectBackup, obj, inetAddress, Integer.valueOf(i), Integer.valueOf(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showNotification(String str, String str2) {
        String property;
        int hashCode = str.hashCode();
        synchronized (sNotifications) {
            if (sNotifications.containsKey(Integer.valueOf(hashCode))) {
                return;
            }
            sNotifications.put(Integer.valueOf(hashCode), str);
            Log.m15i(TAG, "showNotification; host: " + str + ", mappedHost: " + str2);
            str.hashCode();
            if (TextUtils.isEmpty(str2)) {
                property = sStringsProperties.getProperty("hosts_mapper_not_mapped_notification_title");
            } else {
                property = sStringsProperties.getProperty("hosts_mapper_mapped_notification_title");
            }
            if (!TextUtils.isEmpty(str2)) {
                str = str + " → " + str2;
            }
            Notification.Builder contentText = new Notification.Builder(sContext).setSmallIcon(17301543).setContentTitle(property).setContentText(str);
            if (Build.VERSION.SDK_INT >= 21) {
                contentText.setVisibility(-1);
            }
            Utils.setSmallNotificationIcon(contentText);
            Notification notification = contentText.getNotification();
            notification.sound = null;
            notification.defaults &= -2;
            sNotificationManager.notify(hashCode, notification);
        }
    }
}
