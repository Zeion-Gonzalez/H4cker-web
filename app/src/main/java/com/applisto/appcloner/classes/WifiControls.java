package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;

/* loaded from: classes2.dex */
public class WifiControls extends OnAppExitListener {
    private static final String TAG = WifiControls.class.getSimpleName();
    private Boolean mOldWifiState;
    private boolean mRestoreWifiStateOnExit;
    private Boolean mWifiState;
    private boolean mWifiStateSet;

    public WifiControls(CloneSettings cloneSettings) {
        this.mWifiState = cloneSettings.getBoolean("wifiState", null);
        this.mRestoreWifiStateOnExit = cloneSettings.getBoolean("restoreWifiStateOnExit", false).booleanValue();
        Log.m15i(TAG, "WifiControls; mWifiState: " + this.mWifiState);
        Log.m15i(TAG, "WifiControls; mRestoreWifiStateOnExit: " + this.mRestoreWifiStateOnExit);
    }

    public void install(Context context) {
        Log.m15i(TAG, "install; ");
        if (this.mWifiState != null) {
            onCreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        if (this.mWifiStateSet) {
            return;
        }
        try {
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService("wifi");
            this.mOldWifiState = Boolean.valueOf(wifiManager.isWifiEnabled());
            if (this.mWifiState.booleanValue()) {
                wifiManager.setWifiEnabled(true);
                Log.m15i(TAG, "onActivityCreated; enabled Wi-Fi");
            } else {
                wifiManager.setWifiEnabled(false);
                Log.m15i(TAG, "onActivityCreated; disabled Wi-Fi");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        Log.m15i(TAG, "onActivityCreated; mOldWifiState: " + this.mOldWifiState);
        this.mWifiStateSet = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        Log.m15i(TAG, "onAppExit; mRestoreWifiStateOnExit: " + this.mRestoreWifiStateOnExit + ", mOldWifiState: " + this.mOldWifiState);
        if (!this.mRestoreWifiStateOnExit || this.mOldWifiState == null) {
            return;
        }
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
            if (this.mOldWifiState.booleanValue()) {
                wifiManager.setWifiEnabled(true);
                Log.m15i(TAG, "onAppExit; enabled Wi-Fi");
            } else {
                wifiManager.setWifiEnabled(false);
                Log.m15i(TAG, "onAppExit; disabled Wi-Fi");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        this.mWifiStateSet = false;
    }
}
