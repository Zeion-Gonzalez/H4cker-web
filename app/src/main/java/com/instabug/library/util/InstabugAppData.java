package com.instabug.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/* loaded from: classes.dex */
public class InstabugAppData {
    private ApplicationInfo applicationInfo;
    private Context context;
    private String packageName;

    /* renamed from: pm */
    private PackageManager f1250pm;

    public InstabugAppData(Context context) {
        this.context = context;
        this.packageName = context.getPackageName();
        this.f1250pm = context.getPackageManager();
        try {
            this.applicationInfo = this.f1250pm.getApplicationInfo(this.packageName, 128);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAppName() {
        return (String) (this.applicationInfo != null ? this.f1250pm.getApplicationLabel(this.applicationInfo) : "(unknown)");
    }

    public int getAppIcon() {
        return this.applicationInfo.icon;
    }

    public Intent getMainIntent() {
        return this.f1250pm.getLaunchIntentForPackage(this.context.getPackageName());
    }
}
