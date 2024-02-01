package com.instabug.library.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: SDKInvokedBroadcast.java */
/* renamed from: com.instabug.library.broadcast.a */
/* loaded from: classes.dex */
public class C0633a extends BroadcastReceiver {

    /* renamed from: a */
    private a f802a;

    /* compiled from: SDKInvokedBroadcast.java */
    /* renamed from: com.instabug.library.broadcast.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo999a(boolean z);
    }

    public C0633a(a aVar) {
        this.f802a = aVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.m1803v(this, " - onReceive");
        this.f802a.mo999a(intent.getExtras().getBoolean("SDK invoking state"));
    }
}
