package com.applisto.appcloner.classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class PersistentAppService extends Service {
    private static final String TAG = PersistentAppService.class.getSimpleName();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.m15i(TAG, "onStartCommand; ");
        return 1;
    }
}
