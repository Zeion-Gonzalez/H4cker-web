package com.instabug.library.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.view.WindowManager;
import com.instabug.library.Instabug;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public class OrientationUtils {
    private OrientationUtils() {
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            InstabugSDKLogger.m1799d(OrientationUtils.class, "Unlocking orientation for activity " + activity.toString());
            try {
                activity.setRequestedOrientation(activity.getPackageManager().getActivityInfo(new ComponentName(activity, activity.getClass()), 128).screenOrientation);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                activity.setRequestedOrientation(-1);
            } catch (Exception e2) {
                activity.setRequestedOrientation(-1);
            }
        }
    }

    public static void lockScreenOrientation(Activity activity) {
        InstabugSDKLogger.m1799d(OrientationUtils.class, "Locking orientation for activity " + activity.toString());
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (activity.getResources().getConfiguration().orientation) {
            case 1:
                if (rotation == 1 || rotation == 2) {
                    activity.setRequestedOrientation(9);
                    return;
                } else {
                    activity.setRequestedOrientation(1);
                    return;
                }
            case 2:
                if (rotation == 0 || rotation == 1) {
                    activity.setRequestedOrientation(0);
                    return;
                } else {
                    activity.setRequestedOrientation(8);
                    return;
                }
            default:
                return;
        }
    }

    public static void handelOrientation(Activity activity) {
        if (SettingsManager.getInstance().getRequestedOrientation() == -2) {
            lockScreenOrientation(activity);
        } else {
            activity.setRequestedOrientation(Instabug.getRequestedOrientation());
        }
    }

    public static int getOrientation(int i) {
        switch (i) {
            case -1:
            case 7:
            default:
                return -1;
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
        }
    }

    public static boolean isInLandscape(Activity activity) {
        if (activity == null) {
            return false;
        }
        switch (((WindowManager) activity.getSystemService("window")).getDefaultDisplay().getOrientation()) {
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                return false;
            default:
                return true;
        }
    }
}
