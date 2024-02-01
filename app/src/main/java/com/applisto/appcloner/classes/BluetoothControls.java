package com.applisto.appcloner.classes;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;

/* loaded from: classes2.dex */
public class BluetoothControls extends OnAppExitListener {
    private static final String TAG = BluetoothControls.class.getSimpleName();
    private Boolean mBluetoothState;
    private boolean mBluetoothStateSet;
    private Boolean mOldBluetoothState;
    private boolean mRestoreBluetoothStateOnExit;

    public BluetoothControls(CloneSettings cloneSettings) {
        this.mBluetoothState = cloneSettings.getBoolean("bluetoothState", null);
        this.mRestoreBluetoothStateOnExit = cloneSettings.getBoolean("restoreBluetoothStateOnExit", false).booleanValue();
        Log.m15i(TAG, "BluetoothControls; mBluetoothState: " + this.mBluetoothState);
        Log.m15i(TAG, "BluetoothControls; mRestoreBluetoothStateOnExit: " + this.mRestoreBluetoothStateOnExit);
    }

    public void install() {
        if (this.mBluetoothState != null) {
            onCreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(Activity activity) {
        super.onActivityCreated(activity);
        if (this.mBluetoothStateSet) {
            return;
        }
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mOldBluetoothState = Boolean.valueOf(defaultAdapter.isEnabled());
            if (this.mBluetoothState.booleanValue()) {
                defaultAdapter.enable();
                Log.m15i(TAG, "onActivityCreated; enabled bluetooth");
            } else {
                defaultAdapter.disable();
                Log.m15i(TAG, "onActivityCreated; disabled bluetooth");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        Log.m15i(TAG, "onActivityCreated; mOldBluetoothState: " + this.mOldBluetoothState);
        this.mBluetoothStateSet = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        Log.m15i(TAG, "onAppExit; mRestoreBluetoothStateOnExit: " + this.mRestoreBluetoothStateOnExit + ", mOldBluetoothState: " + this.mOldBluetoothState);
        if (!this.mRestoreBluetoothStateOnExit || this.mOldBluetoothState == null) {
            return;
        }
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mOldBluetoothState.booleanValue()) {
                defaultAdapter.enable();
                Log.m15i(TAG, "onAppExit; enabled bluetooth");
            } else {
                defaultAdapter.disable();
                Log.m15i(TAG, "onAppExit; disabled bluetooth");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        this.mBluetoothStateSet = false;
    }
}
