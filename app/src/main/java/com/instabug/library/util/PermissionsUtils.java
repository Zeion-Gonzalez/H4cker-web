package com.instabug.library.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/* loaded from: classes.dex */
public class PermissionsUtils {
    public static boolean isPermissionGranted(@NonNull Context context, @NonNull String str) {
        boolean z;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                z = ContextCompat.checkSelfPermission(context, str) == 0;
                InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " state is " + (z ? "" : "NOT ") + "granted");
            } else {
                z = context.checkCallingOrSelfPermission(str) == 0;
                InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " state is " + (z ? "" : "NOT ") + "granted");
            }
            return z;
        } catch (Error | Exception e) {
            return true;
        }
    }

    public static void requestPermission(@NonNull Activity activity, @NonNull String str, int i, @Nullable Runnable runnable, @Nullable Runnable runnable2) {
        if (!isPermissionGranted(activity, str)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
                runIfValid(runnable);
            }
            InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " not granted, requesting it");
            ActivityCompat.requestPermissions(activity, new String[]{str}, i);
            return;
        }
        InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " already granted, running after permission granted runnable");
        runIfValid(runnable2);
    }

    public static void requestPermission(@NonNull Fragment fragment, @NonNull String str, int i, @Nullable Runnable runnable, @Nullable Runnable runnable2) {
        if (!isPermissionGranted(fragment.getContext(), str)) {
            if (!fragment.shouldShowRequestPermissionRationale(str)) {
                runIfValid(runnable);
            }
            InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " not granted, requesting it");
            fragment.requestPermissions(new String[]{str}, i);
            return;
        }
        InstabugSDKLogger.m1799d(PermissionsUtils.class, "Permission " + str + " already granted, running after permission granted runnable");
        runIfValid(runnable2);
    }

    private static void runIfValid(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
}
