package com.instabug.library.network;

import android.app.IntentService;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: InstabugBackgroundService.java */
/* renamed from: com.instabug.library.network.a */
/* loaded from: classes.dex */
public abstract class AbstractIntentServiceC0722a extends IntentService {
    protected abstract boolean mustHaveNetworkConnection();

    protected abstract void runBackgroundTask() throws Exception;

    public AbstractIntentServiceC0722a() {
        super(AbstractIntentServiceC0722a.class.getSimpleName());
        setIntentRedelivery(true);
    }

    @Override // android.app.IntentService, android.app.Service
    public void onCreate() {
        super.onCreate();
        InstabugSDKLogger.m1803v(this, "New " + getClass().getSimpleName() + " created");
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        InstabugSDKLogger.m1803v(this, getClass().getSimpleName() + " started with intent " + intent);
        if (mustHaveNetworkConnection() && NetworkManager.isOnline(this)) {
            InstabugSDKLogger.m1803v(this, "Internet is good to go");
            try {
                InstabugSDKLogger.m1803v(this, "Starting " + getClass().getSimpleName() + " task");
                runBackgroundTask();
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "An error occurred while doing " + getClass().getSimpleName() + "'s required task " + e.getMessage(), e);
            }
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        InstabugSDKLogger.m1803v(this, getClass().getSimpleName() + " destroyed");
    }
}
