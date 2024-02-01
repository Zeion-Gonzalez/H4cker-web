package com.instabug.library.internal.device;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public class InstabugDeviceProperties {
    public static String getAppVersion(Context context) {
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            InstabugSDKLogger.m1801e(InstabugDeviceProperties.class, "failed to get app version", e);
        }
        return String.format("%s (%s)", packageInfo.versionName, Integer.valueOf(packageInfo.versionCode));
    }

    public static String getAppVersionName(Context context) {
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            InstabugSDKLogger.m1801e(InstabugDeviceProperties.class, "failed to get app version", e);
        }
        return packageInfo.versionName;
    }

    public static String getPackageName(Context context) {
        try {
            return context.getApplicationInfo().packageName;
        } catch (Exception e) {
            InstabugSDKLogger.m1801e(InstabugDeviceProperties.class, "failed to get package name", e);
            return "Could not get information";
        }
    }

    public static String getDeviceType() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public static int getCurrentOSLevel() {
        return Build.VERSION.SDK_INT;
    }
}
