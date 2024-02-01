package com.instabug.library.settings;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.OnSdkInvokedCallback;
import com.instabug.library.internal.storage.AttachmentsUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

/* loaded from: classes.dex */
public class SettingsManager {
    public static final String INSTABUG_SHARED_PREF_NAME = "instabug";
    public static final boolean VERBOSE = false;
    private static SettingsManager settingsManager;
    private boolean DEBUG = false;
    private boolean reproStepsScreenshotEnable;

    private SettingsManager() {
    }

    public static void init(Context context) {
        settingsManager = new SettingsManager();
        C0737b.m1693a(context);
        C0736a.m1648a();
    }

    public static SettingsManager getInstance() {
        if (settingsManager == null) {
            settingsManager = new SettingsManager();
        }
        return settingsManager;
    }

    public boolean isDebugEnabled() {
        return this.DEBUG;
    }

    public void setDebugEnabled(boolean z) {
        this.DEBUG = z;
    }

    public String getAppToken() {
        return C0737b.m1692a().m1699b();
    }

    public void setAppToken(String str) {
        C0737b.m1692a().m1697a(str);
    }

    public Runnable getPreInvocationRunnable() {
        return C0736a.m1649b().m1663c();
    }

    public void setPreInvocationRunnable(Runnable runnable) {
        C0736a.m1649b().m1657a(runnable);
    }

    public Runnable getPreReportRunnable() {
        return C0736a.m1649b().m1666d();
    }

    public OnSdkInvokedCallback getOnSdkInvokedCallback() {
        return C0736a.m1649b().m1669e();
    }

    public void setOnSdkInvokedCallback(OnSdkInvokedCallback onSdkInvokedCallback) {
        C0736a.m1649b().m1656a(onSdkInvokedCallback);
    }

    public String getUserData() {
        return C0629b.m1160a().m1170b(Feature.USER_DATA) == Feature.State.ENABLED ? C0737b.m1692a().m1727p() : "";
    }

    public void setUserData(String str) {
        C0737b.m1692a().m1716g(str);
    }

    public Locale getInstabugLocale(Context context) {
        return C0736a.m1649b().m1650a(context);
    }

    public void setInstabugLocale(Locale locale) {
        C0736a.m1649b().m1658a(locale);
    }

    public boolean isIntroMessageEnabled() {
        return C0737b.m1692a().m1728q();
    }

    public void setIntroMessageEnabled(boolean z) {
        C0737b.m1692a().m1710e(z);
    }

    public void addExtraAttachmentFile(Uri uri, String str) {
        C0736a.m1649b().m1653a(uri, str);
    }

    public void addExtraAttachmentFile(@NonNull byte[] bArr, String str) {
        C0736a.m1649b().m1653a(AttachmentsUtility.getUriFromBytes(Instabug.getApplicationContext(), bArr, str), str);
    }

    public void clearExtraAttachmentFiles() {
        C0736a.m1649b().m1673g();
    }

    public LinkedHashMap<Uri, String> getExtraAttachmentFiles() {
        return C0736a.m1649b().m1671f();
    }

    @NonNull
    public String getUserEmail() {
        return C0737b.m1692a().m1703c();
    }

    public void setUserEmail(String str) {
        C0737b.m1692a().m1701b(str);
    }

    @NonNull
    public String getIdentifiedUserEmail() {
        return C0737b.m1692a().m1706d();
    }

    public void setIdentifiedUserEmail(String str) {
        C0737b.m1692a().m1704c(str);
    }

    public boolean isDeviceRegistered() {
        return C0737b.m1692a().m1711e();
    }

    public void setIsDeviceRegistered(boolean z) {
        C0737b.m1692a().m1698a(z);
    }

    public boolean isFirstRun() {
        return C0737b.m1692a().m1714f();
    }

    public void setIsFirstRun(boolean z) {
        C0737b.m1692a().m1702b(z);
    }

    public Date getFirstRunAt() {
        return new Date(C0737b.m1692a().m1715g());
    }

    public void setFirstRunAt(long j) {
        C0737b.m1692a().m1695a(j);
    }

    @Deprecated
    public long getLastContactedAt() {
        return C0737b.m1692a().m1718h();
    }

    @Deprecated
    public void setLastContactedAt(long j) {
        C0737b.m1692a().m1700b(j);
    }

    public boolean isAppOnForeground() {
        return C0737b.m1692a().m1720i();
    }

    public void setIsAppOnForeground(boolean z) {
        C0737b.m1692a().m1705c(z);
    }

    public int getLastMigrationVersion() {
        return C0737b.m1692a().m1721j();
    }

    public void setLastMigrationVersion(int i) {
        C0737b.m1692a().m1694a(i);
    }

    public boolean isFirstDismiss() {
        return C0737b.m1692a().m1722k();
    }

    public void setIsFirstDismiss(boolean z) {
        C0737b.m1692a().m1708d(z);
    }

    public int getPrimaryColor() {
        return C0736a.m1649b().m1678k();
    }

    public void setPrimaryColor(@ColorInt int i) {
        C0736a.m1649b().m1651a(i);
    }

    public InstabugColorTheme getTheme() {
        return C0737b.m1692a().m1723l();
    }

    public void setTheme(InstabugColorTheme instabugColorTheme) {
        C0737b.m1692a().m1696a(instabugColorTheme);
    }

    public String getUsername() {
        return C0737b.m1692a().m1724m();
    }

    public void setUsername(String str) {
        C0737b.m1692a().m1707d(str);
    }

    public String getUuid() {
        return C0737b.m1692a().m1725n();
    }

    public void setUuid(String str) {
        C0737b.m1692a().m1709e(str);
    }

    public String getMD5Uuid() {
        return C0737b.m1692a().m1726o();
    }

    public void setMD5Uuid(String str) {
        C0737b.m1692a().m1712f(str);
    }

    public ArrayList<String> getTags() {
        return C0736a.m1649b().m1675h();
    }

    public void addTags(String... strArr) {
        C0736a.m1649b().m1660a(strArr);
    }

    public void resetTags() {
        C0736a.m1649b().m1676i();
    }

    public String getTagsAsString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> m1675h = C0736a.m1649b().m1675h();
        if (m1675h != null && m1675h.size() > 0) {
            int size = m1675h.size();
            for (int i = 0; i < size; i++) {
                sb.append(m1675h.get(i));
                if (i != size - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    public long getSessionStartedAt() {
        return C0736a.m1649b().m1677j();
    }

    public void setSessionStartedAt(long j) {
        C0736a.m1649b().m1652a(j);
    }

    public boolean isVideoProcessorBusy() {
        return C0736a.m1649b().m1679l();
    }

    public void setVideoProcessorBusy(boolean z) {
        C0736a.m1649b().m1659a(z);
    }

    public InstabugCustomTextPlaceHolder getCustomPlaceHolders() {
        return C0736a.m1649b().m1680m();
    }

    public void setCustomPlaceHolders(InstabugCustomTextPlaceHolder instabugCustomTextPlaceHolder) {
        C0736a.m1649b().m1654a(instabugCustomTextPlaceHolder);
    }

    public boolean isUserLoggedOut() {
        return C0737b.m1692a().m1729r();
    }

    public void setUserLoggedOut(boolean z) {
        C0737b.m1692a().m1713f(z);
    }

    public boolean shouldMakeUUIDMigrationRequest() {
        return C0737b.m1692a().m1730s();
    }

    public void setShouldMakeUUIDMigrationRequest(boolean z) {
        C0737b.m1692a().m1717g(z);
    }

    public void setCurrentSDKVersion(String str) {
        C0737b.m1692a().m1719h(str);
    }

    public String getLastSDKVersion() {
        return C0737b.m1692a().m1731t();
    }

    public boolean isSDKVersionSet() {
        return C0737b.m1692a().m1732u();
    }

    public int getStatusBarColor() {
        return C0736a.m1649b().m1681n();
    }

    public void setStatusBarColor(int i) {
        C0736a.m1649b().m1661b(i);
    }

    public void setRequestedOrientation(int i) {
        C0736a.m1649b().m1664c(i);
    }

    public int getRequestedOrientation() {
        return C0736a.m1649b().m1682o();
    }

    public void resetRequestedOrientation() {
        C0736a.m1649b().m1683p();
    }

    public int getSessionsCount() {
        return C0737b.m1692a().m1733v();
    }

    public void incrementSessionsCount() {
        C0737b.m1692a().m1734w();
    }

    public boolean isPromptOptionsScreenShown() {
        return C0736a.m1649b().m1684q();
    }

    public void setPromptOptionsScreenShown(boolean z) {
        C0736a.m1649b().m1662b(z);
    }

    public boolean isRequestPermissionScreenShown() {
        return C0736a.m1649b().m1685r();
    }

    public void setRequestPermissionScreenShown(boolean z) {
        C0736a.m1649b().m1665c(z);
    }

    public OnSdkDismissedCallback getOnSdkDismissedCallback() {
        return C0736a.m1649b().m1686s();
    }

    public void setOnSdkDismissedCallback(OnSdkDismissedCallback onSdkDismissedCallback) {
        C0736a.m1649b().m1655a(onSdkDismissedCallback);
    }

    public void setAutoScreenRecordingEnabled(boolean z) {
        C0736a.m1649b().m1674g(z);
    }

    public boolean autoScreenRecordingEnabled() {
        return C0736a.m1649b().m1690w();
    }

    public void setAutoScreenRecordingMaxDuration(int i) {
        C0736a m1649b = C0736a.m1649b();
        if (i > 30000) {
            i = 30000;
        }
        m1649b.m1667d(i);
    }

    public int autoScreenRecordingMaxDuration() {
        return C0736a.m1649b().m1691x();
    }

    public boolean isScreenCurrentlyRecorded() {
        return C0736a.m1649b().m1688u();
    }

    public void setScreenCurrentlyRecorded(boolean z) {
        C0736a.m1649b().m1670e(z);
    }

    public boolean isAutoScreenRecordingDenied() {
        return C0736a.m1649b().m1689v();
    }

    public void setAutoScreenRecordingDenied(boolean z) {
        C0736a.m1649b().m1672f(z);
    }

    public void setReproStepsScreenshotEnabled(boolean z) {
        C0736a.m1649b().m1668d(z);
    }

    public boolean isReproStepsScreenshotEnabled() {
        return C0736a.m1649b().m1687t();
    }
}
