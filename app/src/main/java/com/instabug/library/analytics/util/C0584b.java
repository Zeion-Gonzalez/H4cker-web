package com.instabug.library.analytics.util;

import android.content.Intent;
import com.instabug.library.Instabug;
import com.instabug.library.analytics.network.InstabugAnalyticsUploaderService;

/* compiled from: UploaderServiceLauncher.java */
/* renamed from: com.instabug.library.analytics.util.b */
/* loaded from: classes.dex */
public class C0584b {
    /* renamed from: a */
    public static void m1021a() {
        if (Instabug.getApplicationContext() != null) {
            Instabug.getApplicationContext().startService(new Intent(Instabug.getApplicationContext(), InstabugAnalyticsUploaderService.class));
        }
    }
}
