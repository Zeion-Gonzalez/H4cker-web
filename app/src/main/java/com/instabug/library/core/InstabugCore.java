package com.instabug.library.core;

import android.app.Activity;
import android.net.Uri;
import com.instabug.library.C0629b;
import com.instabug.library.C0646e;
import com.instabug.library.Feature;
import com.instabug.library.OnSdkInvokedCallback;
import com.instabug.library.core.plugin.C0642a;
import com.instabug.library.core.plugin.Plugin;
import com.instabug.library.p023c.C0635a;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.user.C0750a;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class InstabugCore {
    public static boolean isFeaturesFetchedBefore() {
        return C0629b.m1160a().m1172b();
    }

    public static Feature.State getFeatureState(Feature feature) {
        return C0629b.m1160a().m1170b(feature);
    }

    public static boolean isFeatureAvailable(Feature feature) {
        return C0629b.m1160a().m1169a(feature);
    }

    public static LinkedHashMap<Uri, String> getExtraAttachmentFiles() {
        return SettingsManager.getInstance().getExtraAttachmentFiles();
    }

    public static Runnable getPreReportRunnable() {
        return C0635a.m1191a();
    }

    public static String getSDKVersion() {
        return "4.11.2";
    }

    public static int getStartedActivitiesCount() {
        return C0646e.m1249a().m1268d();
    }

    public static int getPrimaryColor() {
        return SettingsManager.getInstance().getPrimaryColor();
    }

    public static String getUserEmail() {
        return C0750a.m1784b();
    }

    public static String getUsername() {
        return C0750a.m1787c();
    }

    public static String getUserData() {
        return SettingsManager.getInstance().getUserData();
    }

    public static void setUserEmail(String str) {
        C0750a.m1785b(str);
    }

    public static OnSdkInvokedCallback getOnSdkInvokedCallback() {
        return SettingsManager.getInstance().getOnSdkInvokedCallback();
    }

    public static String getTagsAsString() {
        return SettingsManager.getInstance().getTagsAsString();
    }

    public static void setLastContactedAt(long j) {
        SettingsManager.getInstance().setLastContactedAt(j);
    }

    public static boolean isUserLoggedOut() {
        return SettingsManager.getInstance().isUserLoggedOut();
    }

    public static boolean isForegroundBusy() {
        return C0642a.m1246d();
    }

    public static void setFeatureState(Feature feature, Feature.State state) {
        C0629b.m1160a().m1167a(feature, state);
    }

    public static int getSessionCount() {
        return SettingsManager.getInstance().getSessionsCount();
    }

    public static long getFirstRunAt() {
        return SettingsManager.getInstance().getFirstRunAt().getTime();
    }

    public static Plugin getXPlugin(Class cls) {
        return C0642a.m1240a(cls);
    }

    public static boolean isReproStepsScreenshotEnabled() {
        return SettingsManager.getInstance().isReproStepsScreenshotEnabled();
    }

    public static boolean isAppOnForeground() {
        return SettingsManager.getInstance().isAppOnForeground();
    }

    public static Activity getTargetActivity() {
        return InstabugInternalTrackingDelegate.getInstance().getTargetActivity();
    }
}
