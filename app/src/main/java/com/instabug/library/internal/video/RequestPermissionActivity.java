package com.instabug.library.internal.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library._InstabugActivity;
import com.instabug.library.broadcast.C0633a;
import com.instabug.library.settings.SettingsManager;

@TargetApi(21)
/* loaded from: classes.dex */
public class RequestPermissionActivity extends Activity implements _InstabugActivity, C0633a.a {

    /* renamed from: a */
    private C0633a f868a = new C0633a(this);

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            startActivityForResult(((MediaProjectionManager) getSystemService("media_projection")).createScreenCaptureIntent(), 2020);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2020) {
            try {
                if (i2 == -1) {
                    startService(AutoScreenRecordingService.m1325a(this, i2, intent));
                } else if (i2 == 0) {
                    SettingsManager.getInstance().setAutoScreenRecordingDenied(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                finish();
            }
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // com.instabug.library.broadcast.C0633a.a
    /* renamed from: a */
    public void mo999a(boolean z) {
        if (z) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.f868a, new IntentFilter("SDK invoked"));
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.f868a);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        SettingsManager.getInstance().setRequestPermissionScreenShown(true);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        SettingsManager.getInstance().setRequestPermissionScreenShown(false);
        finish();
    }
}
