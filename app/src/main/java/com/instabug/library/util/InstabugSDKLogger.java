package com.instabug.library.util;

import android.support.annotation.NonNull;
import android.util.Log;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public final class InstabugSDKLogger {
    private static final String LOG_TAG = "INSTABUG - ";

    private InstabugSDKLogger() {
    }

    public static String logTag(@NonNull Object obj) {
        String simpleName;
        if (obj instanceof Class) {
            simpleName = ((Class) obj).getSimpleName();
        } else {
            simpleName = obj.getClass().getSimpleName();
        }
        return LOG_TAG + simpleName;
    }

    /* renamed from: v */
    public static void m1803v(@NonNull Object obj, @NonNull String str) {
        String substring;
        if (SettingsManager.getInstance().isDebugEnabled()) {
            String logTag = logTag(obj);
            if (str.length() > 4000) {
                int length = str.length() / 4000;
                Log.v(logTag, "logMessage length = " + str.length() + " divided to " + (length + 1) + " chunks");
                for (int i = 0; i <= length; i++) {
                    int i2 = (i + 1) * 4000;
                    if (i2 >= str.length()) {
                        substring = str.substring(i * 4000);
                    } else {
                        substring = str.substring(i * 4000, i2);
                    }
                    Log.v(logTag, "chunk " + (i + 1) + " of " + (length + 1) + ":\n" + substring);
                }
                return;
            }
            Log.v(logTag, str);
        }
    }

    /* renamed from: d */
    public static void m1799d(@NonNull Object obj, @NonNull String str) {
        String substring;
        if (SettingsManager.getInstance().isDebugEnabled()) {
            String logTag = logTag(obj);
            if (str.length() > 4000) {
                int length = str.length() / 4000;
                Log.d(logTag, "logMessage length = " + str.length() + " divided to " + (length + 1) + " chunks");
                for (int i = 0; i <= length; i++) {
                    int i2 = (i + 1) * 4000;
                    if (i2 >= str.length()) {
                        substring = str.substring(i * 4000);
                    } else {
                        substring = str.substring(i * 4000, i2);
                    }
                    Log.d(logTag, "chunk " + (i + 1) + " of " + (length + 1) + ":\n" + substring);
                }
                return;
            }
            Log.d(logTag, str);
        }
    }

    /* renamed from: i */
    public static void m1802i(@NonNull Object obj, @NonNull String str) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.i(logTag(obj), str);
        }
    }

    /* renamed from: w */
    public static void m1804w(@NonNull Object obj, @NonNull String str) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.w(logTag(obj), str);
        }
    }

    /* renamed from: e */
    public static void m1800e(@NonNull Object obj, @NonNull String str) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.e(logTag(obj), str);
        }
    }

    /* renamed from: e */
    public static void m1801e(@NonNull Object obj, @NonNull String str, @NonNull Throwable th) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.e(logTag(obj), str, th);
        }
    }

    public static void wtf(@NonNull Object obj, @NonNull String str) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.wtf(logTag(obj), str);
        }
    }

    public static void wtf(@NonNull Object obj, @NonNull String str, @NonNull Throwable th) {
        if (SettingsManager.getInstance().isDebugEnabled()) {
            Log.wtf(logTag(obj), str, th);
        }
    }
}
