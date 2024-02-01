package com.instabug.library.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public class LastContactedChangedBroadcast extends BroadcastReceiver {
    public static final String LAST_CONTACTED_AT = "last_contacted_at";
    public static final String LAST_CONTACTED_CHANGED = "User last contact at changed";
    private InterfaceC0632a mLastContactedMonitor;

    /* renamed from: com.instabug.library.broadcast.LastContactedChangedBroadcast$a */
    /* loaded from: classes.dex */
    public interface InterfaceC0632a {
        /* renamed from: a */
        void m1185a();
    }

    public LastContactedChangedBroadcast(InterfaceC0632a interfaceC0632a) {
        this.mLastContactedMonitor = interfaceC0632a;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.m1803v(this, " - onReceive");
        this.mLastContactedMonitor.m1185a();
    }
}
