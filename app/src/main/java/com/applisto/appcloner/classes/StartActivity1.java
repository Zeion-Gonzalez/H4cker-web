package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class StartActivity1 extends Activity {
    private static final String TAG = StartActivity1.class.getSimpleName();

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            Bundle bundle2 = getPackageManager().getActivityInfo(getComponentName(), 129).metaData;
            if (bundle2 != null) {
                String string = bundle2.getString("com.applisto.appcloner.originalActivityName");
                Log.m15i(TAG, "onCreate; originalActivityName: " + string);
                if (string != null) {
                    if (string.startsWith(".")) {
                        string = getPackageName() + string;
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                    Intent intent = new Intent(getIntent());
                    intent.setComponent(new ComponentName(this, Class.forName(string)));
                    intent.setFlags(268435456);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }
}
