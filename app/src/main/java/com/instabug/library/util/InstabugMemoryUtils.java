package com.instabug.library.util;

import android.app.Activity;
import android.app.ActivityManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;

/* loaded from: classes.dex */
public class InstabugMemoryUtils {
    public static boolean isLowMemory() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            ActivityManager activityManager = (ActivityManager) currentActivity.getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                return memoryInfo.lowMemory;
            }
        }
        return true;
    }
}
