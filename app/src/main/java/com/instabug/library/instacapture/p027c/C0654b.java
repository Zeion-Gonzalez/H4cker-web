package com.instabug.library.instacapture.p027c;

import android.app.ActivityManager;
import android.content.Context;

/* compiled from: Memory.java */
/* renamed from: com.instabug.library.instacapture.c.b */
/* loaded from: classes.dex */
public class C0654b {
    /* renamed from: a */
    public static long m1285a(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
}
