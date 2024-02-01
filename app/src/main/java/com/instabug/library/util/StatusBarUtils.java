package com.instabug.library.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public class StatusBarUtils {
    public static void setStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(SettingsManager.getInstance().getStatusBarColor());
        }
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = activity.getWindow().getDecorView();
            if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeLight) {
                decorView.setSystemUiVisibility(8192);
            } else {
                decorView.setSystemUiVisibility(0);
            }
        }
    }
}
