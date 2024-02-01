package com.applisto.appcloner.classes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;

/* loaded from: classes2.dex */
public class ClearCacheOnExitProvider extends OnAppExitListener {
    private static final String TAG = ClearCacheOnExitProvider.class.getSimpleName();

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected boolean onInit(Application application) {
        Log.m15i(TAG, "onInit; application: " + application);
        try {
            application.startService(new Intent(application, ClearCacheOnExitService.class));
            return true;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        clearCache(context, true);
    }

    public static synchronized void clearCache(Context context, boolean z) {
        synchronized (ClearCacheOnExitProvider.class) {
            try {
                DefaultProvider.invokeSecondaryStatic("util.Utils", "clearCache", context);
                if (z) {
                    Toast.makeText(context, Utils.getStringsProperties().getProperty("cache_cleared_message"), 0).show();
                }
            } finally {
            }
        }
    }
}
