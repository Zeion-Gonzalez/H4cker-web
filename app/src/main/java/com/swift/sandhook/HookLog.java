package com.swift.sandhook;

import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class HookLog {
    public static boolean DEBUG = SandHookConfig.DEBUG;
    public static final String TAG = "SandHook";

    /* renamed from: v */
    public static int m2123v(String str) {
        return Log.m17v(TAG, str);
    }

    /* renamed from: i */
    public static int m2122i(String str) {
        return Log.m15i(TAG, str);
    }

    /* renamed from: d */
    public static int m2119d(String str) {
        return Log.m11d(TAG, str);
    }

    /* renamed from: w */
    public static int m2124w(String str) {
        return Log.m19w(TAG, str);
    }

    /* renamed from: e */
    public static int m2120e(String str) {
        return Log.m13e(TAG, str);
    }

    /* renamed from: e */
    public static int m2121e(String str, Throwable th) {
        return Log.m14e(TAG, str, th);
    }
}
