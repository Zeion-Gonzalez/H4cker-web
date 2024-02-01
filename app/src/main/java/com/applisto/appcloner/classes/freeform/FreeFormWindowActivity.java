package com.applisto.appcloner.classes.freeform;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.applisto.appcloner.classes.Utils;
import com.applisto.appcloner.classes.util.Log;

/* loaded from: classes2.dex */
public class FreeFormWindowActivity extends Activity {
    private static final String TAG = FreeFormWindowActivity.class.getSimpleName();
    private static FreeFormWindow sFreeFormWindow;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (sFreeFormWindow == null) {
            sFreeFormWindow = new FreeFormWindow();
        }
        try {
            Bundle bundle2 = getPackageManager().getActivityInfo(getComponentName(), 129).metaData;
            if (bundle2 != null) {
                String string = bundle2.getString("com.applisto.appcloner.originalActivityName");
                if (string != null && string.startsWith(".")) {
                    string = getPackageName() + string;
                }
                Log.m15i(TAG, "onCreate; originalActivityName: " + string);
                Intent intent = new Intent(this, Class.forName(string));
                intent.addFlags(268435456);
                intent.addFlags(4096);
                intent.addFlags(65536);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtras(extras);
                }
                Rect loadActivityBounds = loadActivityBounds();
                if (loadActivityBounds == null) {
                    loadActivityBounds = getDefaultActivityBounds();
                }
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                Log.m15i(TAG, "onCreate; activityBounds: " + loadActivityBounds);
                Bundle bundle3 = ActivityOptions.makeBasic().setLaunchBounds(loadActivityBounds).toBundle();
                startActivity(intent, bundle3);
                Log.m15i(TAG, "onCreate; intent: " + intent + ", options: " + bundle3);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public Rect loadActivityBounds() {
        try {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int i = defaultSharedPreferences.getInt(FreeFormWindow.PREF_KEY_FREE_FORM_WINDOW_LEFT, 0);
            int i2 = defaultSharedPreferences.getInt(FreeFormWindow.PREF_KEY_FREE_FORM_WINDOW_TOP, 0);
            int i3 = defaultSharedPreferences.getInt(FreeFormWindow.PREF_KEY_FREE_FORM_WINDOW_RIGHT, 0);
            int i4 = defaultSharedPreferences.getInt(FreeFormWindow.PREF_KEY_FREE_FORM_WINDOW_BOTTOM, 0);
            Log.m15i(TAG, "loadActivityBounds; left: " + i + ", top: " + i2 + ", right: " + i3 + ", bottom: " + i4);
            Rect rect = new Rect(i, i2, i3, i4);
            if (rect.isEmpty()) {
                return null;
            }
            Point realScreenSize = Utils.getRealScreenSize(getWindowManager().getDefaultDisplay());
            if (rect.left == 0 && rect.width() == realScreenSize.x && rect.top == 0) {
                if (rect.height() == realScreenSize.y) {
                    return null;
                }
            }
            return rect;
        } catch (Exception e) {
            Log.m21w(TAG, e);
            return null;
        }
    }

    public Rect getDefaultActivityBounds() {
        Point realScreenSize = Utils.getRealScreenSize(getWindowManager().getDefaultDisplay());
        Log.m15i(TAG, "getDefaultActivityBounds; realScreenSize: " + realScreenSize);
        int round = Math.round(((float) realScreenSize.x) * 0.8f);
        int round2 = Math.round(((float) realScreenSize.y) * 0.8f);
        int i = (realScreenSize.x - round) / 2;
        int i2 = (realScreenSize.y - round2) / 2;
        return new Rect(i, i2, round + i, round2 + i2);
    }
}
