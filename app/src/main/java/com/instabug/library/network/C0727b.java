package com.instabug.library.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.instabug.library.core.eventbus.coreeventbus.C0640a;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.network.worker.fetcher.InstabugFeaturesFetcherService;
import com.instabug.library.network.worker.uploader.InstabugSessionUploaderService;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: InstabugNetworkReceiver.java */
/* renamed from: com.instabug.library.network.b */
/* loaded from: classes.dex */
public class C0727b extends BroadcastReceiver {

    /* renamed from: a */
    private boolean f1182a;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.m1799d(this, "Network state changed");
        if (((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo() != null) {
            InstabugSDKLogger.m1799d(this, "ActiveNetwork not equal null, checking local cache");
            context.startService(new Intent(context, InstabugSessionUploaderService.class));
            context.startService(new Intent(context, InstabugFeaturesFetcherService.class));
            C0640a.m1238a(new SDKCoreEvent(SDKCoreEvent.Network.TYPE_NETWORK, SDKCoreEvent.Network.VALUE_ACTIVATED));
        }
    }

    /* renamed from: a */
    public void m1636a(Context context, IntentFilter intentFilter) {
        context.registerReceiver(this, intentFilter);
        this.f1182a = true;
    }

    /* renamed from: a */
    public void m1635a(Context context) {
        context.unregisterReceiver(this);
        this.f1182a = false;
    }

    /* renamed from: a */
    public boolean m1637a() {
        return this.f1182a;
    }
}
