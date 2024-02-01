package com.applisto.appcloner.classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class ClearCacheOnExitService extends Service {
    private static final String TAG = ClearCacheOnExitService.class.getSimpleName();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.m15i(TAG, "onStartCommand; ");
        return 2;
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent intent) {
        Log.m15i(TAG, "onTaskRemoved; ");
        ClearCacheOnExitProvider.clearCache(getApplicationContext(), true);
    }
}
