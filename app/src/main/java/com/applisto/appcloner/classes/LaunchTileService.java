package com.applisto.appcloner.classes;

import android.content.Context;
import android.content.Intent;
import android.service.quicksettings.TileService;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class LaunchTileService extends TileService {
    private static final String TAG = LaunchTileService.class.getSimpleName();

    @Override // android.service.quicksettings.TileService
    public void onStartListening() {
        Log.m15i(TAG, "onStartListening; ");
    }

    @Override // android.service.quicksettings.TileService
    public void onClick() {
        Log.m15i(TAG, "onClick; ");
        Context applicationContext = getApplicationContext();
        try {
            Intent launchIntent = Utils.getLaunchIntent(applicationContext, applicationContext.getPackageName());
            launchIntent.addFlags(268435456);
            startActivity(launchIntent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
            Toast.makeText(applicationContext, "Failed to launch app.", 0).show();
        }
    }
}
