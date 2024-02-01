package com.applisto.appcloner.classes.util;

import com.applisto.appcloner.classes.Utils;

/* loaded from: classes2.dex */
public class Log {
    public static boolean sEnabled = Utils.isDevDevice();

    private Log() {
    }

    /* renamed from: v */
    public static int m17v(String str, String str2) {
        if (sEnabled) {
            return android.util.Log.v(str, str2);
        }
        return 0;
    }

    /* renamed from: v */
    public static int m18v(String str, String str2, Throwable th) {
        if (sEnabled) {
            return android.util.Log.v(str, str2, th);
        }
        return 0;
    }

    /* renamed from: d */
    public static int m11d(String str, String str2) {
        if (sEnabled) {
            return android.util.Log.d(str, str2);
        }
        return 0;
    }

    /* renamed from: d */
    public static int m12d(String str, String str2, Throwable th) {
        if (sEnabled) {
            return android.util.Log.d(str, str2, th);
        }
        return 0;
    }

    /* renamed from: i */
    public static int m15i(String str, String str2) {
        if (sEnabled) {
            return android.util.Log.i(str, str2);
        }
        return 0;
    }

    /* renamed from: i */
    public static int m16i(String str, String str2, Throwable th) {
        if (sEnabled) {
            return android.util.Log.i(str, str2, th);
        }
        return 0;
    }

    /* renamed from: w */
    public static int m19w(String str, String str2) {
        if (sEnabled) {
            return android.util.Log.w(str, str2);
        }
        return 0;
    }

    /* renamed from: w */
    public static int m20w(String str, String str2, Throwable th) {
        if (sEnabled) {
            return android.util.Log.w(str, str2, th);
        }
        return 0;
    }

    /* renamed from: w */
    public static int m21w(String str, Throwable th) {
        if (sEnabled) {
            return android.util.Log.w(str, th);
        }
        return 0;
    }

    /* renamed from: e */
    public static int m13e(String str, String str2) {
        if (sEnabled) {
            return android.util.Log.e(str, str2);
        }
        return 0;
    }

    /* renamed from: e */
    public static int m14e(String str, String str2, Throwable th) {
        if (sEnabled) {
            return android.util.Log.e(str, str2, th);
        }
        return 0;
    }

    public static String getStackTraceString(Throwable th) {
        return android.util.Log.getStackTraceString(th);
    }
}
