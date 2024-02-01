package com.instabug.library;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.instabug.library.Feature;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.bugreporting.model.ReportCategory;
import com.instabug.library.extendedbugreport.ExtendedBugReport;
import com.instabug.library.internal.layer.CapturableView;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.internal.video.InternalAutoScreenRecorderHelper;
import com.instabug.library.internal.video.InternalScreenRecordHelper;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.C0705c;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.instabug.library.invocation.Invocation;
import com.instabug.library.invocation.util.InstabugFloatingButtonEdge;
import com.instabug.library.invocation.util.InstabugVideoRecordingButtonCorner;
import com.instabug.library.logging.InstabugLog;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.model.BugCategory;
import com.instabug.library.model.State;
import com.instabug.library.p023c.C0635a;
import com.instabug.library.p023c.C0636b;
import com.instabug.library.p023c.C0637c;
import com.instabug.library.p023c.C0638d;
import com.instabug.library.settings.AttachmentsTypesParams;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.user.C0750a;
import com.instabug.library.user.UserEventParam;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;
import com.instabug.library.util.StringUtility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class Instabug {
    private C0578a delegate;
    private static Instabug INSTANCE = null;
    private static InstabugState INSTABUG_STATE = InstabugState.NOT_BUILT;

    private Instabug(@NonNull C0578a c0578a) {
        this.delegate = c0578a;
    }

    static Instabug getInstance() throws IllegalStateException {
        if (INSTANCE == null) {
            throw new IllegalStateException("Instabug getInstance called before Instabug.Builder().build() was called");
        }
        return INSTANCE;
    }

    @Deprecated
    public static void setShouldAudioRecordingOptionAppear(boolean z) throws IllegalStateException {
    }

    @Deprecated
    static boolean shouldAudioRecordingOptionAppear() throws IllegalStateException {
        return false;
    }

    @Nullable
    public static Context getApplicationContext() {
        return getInstance().delegate.m1007i();
    }

    public static void setDebugEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isDebugEnabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        SettingsManager.getInstance().setDebugEnabled(z);
    }

    @Deprecated
    public static void changeInvocationEvent(@NonNull IBGInvocationEvent iBGInvocationEvent) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("invocationEvent").setType(IBGInvocationEvent.class).setValue(iBGInvocationEvent));
        switch (iBGInvocationEvent) {
            case IBGInvocationEventNone:
                changeInvocationEvent(InstabugInvocationEvent.NONE);
                return;
            case IBGInvocationEventShake:
                changeInvocationEvent(InstabugInvocationEvent.SHAKE);
                return;
            case IBGInvocationEventFloatingButton:
                changeInvocationEvent(InstabugInvocationEvent.FLOATING_BUTTON);
                return;
            case IBGInvocationEventTwoFingersSwipeLeft:
                changeInvocationEvent(InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT);
                return;
            case IBGInvocationScreenshotGesture:
                changeInvocationEvent(InstabugInvocationEvent.SCREENSHOT_GESTURE);
                return;
            default:
                return;
        }
    }

    public static void changeInvocationEvent(@NonNull InstabugInvocationEvent instabugInvocationEvent) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugInvocationEvent").setType(InstabugInvocationEvent.class).setValue(instabugInvocationEvent));
        C0704b.m1513c().m1522a(instabugInvocationEvent);
    }

    @Deprecated
    public static void setDialog(@NonNull Dialog dialog) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("dialog").setType(Dialog.class));
    }

    @Deprecated
    public static void addMapView(@NonNull View view, @NonNull Object obj) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("mapView").setType(View.class), new Api.Parameter().setName("googleMap").setType(Object.class));
    }

    @Deprecated
    public static void addCapturableView(@NonNull CapturableView capturableView) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("capturableView").setType(CapturableView.class));
    }

    @Deprecated
    public static void setGLSurfaceView(@NonNull GLSurfaceView gLSurfaceView) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("surfaceView").setType(GLSurfaceView.class));
    }

    public static void invoke() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0704b.m1513c().m1529j();
    }

    @Deprecated
    public static void invokeConversations() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter[0]);
        invoke(InstabugInvocationMode.CHATS_LIST);
    }

    @Deprecated
    public static void invoke(@NonNull IBGInvocationMode iBGInvocationMode) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("invocationMode").setType(IBGInvocationMode.class).setValue(iBGInvocationMode));
        switch (iBGInvocationMode) {
            case IBGInvocationModeNA:
                invoke(InstabugInvocationMode.PROMPT_OPTION);
                return;
            case IBGInvocationModeBugReporter:
                invoke(InstabugInvocationMode.NEW_BUG);
                return;
            case IBGInvocationModeFeedbackSender:
                invoke(InstabugInvocationMode.NEW_FEEDBACK);
                return;
            default:
                return;
        }
    }

    public static void invoke(@NonNull InstabugInvocationMode instabugInvocationMode) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugInvocationMode").setType(InstabugInvocationMode.class).setValue(instabugInvocationMode.toString()));
        switch (instabugInvocationMode) {
            case NEW_BUG:
                C0704b.m1513c().m1520a(1);
                return;
            case NEW_FEEDBACK:
                C0704b.m1513c().m1520a(2);
                return;
            case NEW_CHAT:
                C0704b.m1513c().m1520a(3);
                return;
            case CHATS_LIST:
                C0704b.m1513c().m1520a(4);
                return;
            default:
                C0704b.m1513c().m1520a(0);
                return;
        }
    }

    public static void setDefaultInvocationMode(int i) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("setDefaultInvocationMode").setType(Invocation.class).setValue(Integer.valueOf(i)));
        C0704b.m1513c().m1524e().m1533a(i);
    }

    public static void resetDefaultInvocationMode() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0704b.m1513c().m1524e().m1536b();
    }

    @Deprecated
    public static void log(@NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedLoggingApiUsage(new Api.Parameter().setName("message").setType(String.class));
        if (C0629b.m1160a().m1170b(Feature.INSTABUG_LOGS) == Feature.State.ENABLED) {
            InstabugLog.m1545d(str);
        }
    }

    @Deprecated
    public static void clearLog() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedLoggingApiUsage(new Api.Parameter[0]);
        InstabugLog.clearLogs();
    }

    public static void showIntroMessage() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        getInstance().delegate.m1004f();
    }

    public static void reportException(@NonNull Throwable th) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("throwable").setType(Throwable.class));
        reportException(th, null);
    }

    public static void reportException(@NonNull Throwable th, @Nullable String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("throwable").setType(Throwable.class), new Api.Parameter().setName("exceptionIdentifier").setType(String.class));
        C0637c.m1224a(getApplicationContext(), th, str);
    }

    public static void setPrimaryColor(@ColorInt int i) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("primaryColorValue").setType(Integer.TYPE).setValue(String.valueOf(i)));
        SettingsManager.getInstance().setPrimaryColor(i);
    }

    public static int getPrimaryColor() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return SettingsManager.getInstance().getPrimaryColor();
    }

    @Deprecated
    public static IBGColorTheme getColorTheme() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter[0]);
        switch (SettingsManager.getInstance().getTheme()) {
            case InstabugColorThemeDark:
                return IBGColorTheme.IBGColorThemeDark;
            case InstabugColorThemeLight:
                return IBGColorTheme.IBGColorThemeLight;
            default:
                return IBGColorTheme.IBGColorThemeLight;
        }
    }

    public static InstabugColorTheme getTheme() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return SettingsManager.getInstance().getTheme();
    }

    public static void setUserData(@NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("userData").setType(String.class));
        if (C0629b.m1160a().m1170b(Feature.USER_DATA) == Feature.State.ENABLED) {
            SettingsManager.getInstance().setUserData(StringUtility.trimString(str));
        }
    }

    @Deprecated
    public static void setFileAttachment(Uri uri, @Nullable String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("fileUri").setType(Uri.class), new Api.Parameter().setName("fileNameWithExtension").setType(String.class));
        addFileAttachment(uri, str);
    }

    public static void addFileAttachment(@NonNull Uri uri, @NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("fileUri").setType(Uri.class), new Api.Parameter().setName("fileNameWithExtension").setType(String.class));
        SettingsManager.getInstance().addExtraAttachmentFile(uri, str);
    }

    public static void addFileAttachment(@NonNull byte[] bArr, @NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("data").setType(Uri.class), new Api.Parameter().setName("fileNameWithExtension").setType(String.class));
        SettingsManager.getInstance().addExtraAttachmentFile(bArr, str);
    }

    public static void clearFileAttachment() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter[0]);
        SettingsManager.getInstance().clearExtraAttachmentFiles();
    }

    public static void setPreSendingRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchLoggingApiUsage(new Api.Parameter().setName("preSendingRunnable").setType(Runnable.class));
        C0635a.m1195a(runnable);
        C0637c.m1225a(runnable);
    }

    @Deprecated
    public static void setOnSdkInvokedCallback(OnSdkInvokedCallback onSdkInvokedCallback) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("onSdkInvokedCallback").setType(OnSdkInvokedCallback.class));
        SettingsManager.getInstance().setOnSdkInvokedCallback(onSdkInvokedCallback);
    }

    public static void setPreInvocation(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("preSdkInvocationRunnable").setType(Runnable.class));
        SettingsManager.getInstance().setPreInvocationRunnable(runnable);
    }

    public static void setOnSdkDismissedCallback(OnSdkDismissedCallback onSdkDismissedCallback) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("onSdkDismissedCallback").setType(OnSdkDismissedCallback.class));
        C0635a.m1192a(onSdkDismissedCallback);
        C0636b.m1208a(onSdkDismissedCallback);
        SettingsManager.getInstance().setOnSdkDismissedCallback(onSdkDismissedCallback);
    }

    public static void setNewMessageHandler(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0636b.m1209a(runnable);
    }

    public static String getUserData() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return SettingsManager.getInstance().getUserData();
    }

    public static String getUserEmail() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0750a.m1784b();
    }

    public static String getAppToken() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return SettingsManager.getInstance().getAppToken();
    }

    @Deprecated
    public static void setUserEmail(@NonNull String str) {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("email").setType(String.class));
        C0750a.m1785b(str);
    }

    @Deprecated
    public static void setUsername(@NonNull String str) {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("username").setType(String.class));
        C0750a.m1782a(str);
    }

    public static void identifyUser(@NonNull String str, @NonNull String str2) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("username").setType(String.class), new Api.Parameter().setName("email"));
        C0750a.m1781a(getApplicationContext(), str, str2);
    }

    public static void logoutUser() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0750a.m1779a();
    }

    public static void setState(InstabugState instabugState) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        INSTABUG_STATE = instabugState;
    }

    public static InstabugState getState() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return INSTABUG_STATE;
    }

    public static boolean isBuilt() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return (INSTANCE == null || getState() == InstabugState.NOT_BUILT) ? false : true;
    }

    public static boolean isEnabled() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0629b.m1160a().m1169a(Feature.INSTABUG) && C0629b.m1160a().m1170b(Feature.INSTABUG) == Feature.State.ENABLED;
    }

    public static void enable() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        if (!isEnabled()) {
            C0629b.m1160a().m1168a(Feature.INSTABUG, true);
            C0629b.m1160a().m1167a(Feature.INSTABUG, Feature.State.ENABLED);
            C0629b.m1160a().m1166a(getApplicationContext());
            getInstance().delegate.m998a();
        }
    }

    public static void disable() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        if (isEnabled()) {
            C0629b.m1160a().m1167a(Feature.INSTABUG, Feature.State.DISABLED);
            C0629b.m1160a().m1168a(Feature.INSTABUG, false);
            C0629b.m1160a().m1166a(getApplicationContext());
            getInstance().delegate.m1005g();
        }
    }

    @Deprecated
    public static void changeLocale(Locale locale) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName(State.KEY_LOCALE).setType(Locale.class).setValue(locale));
        SettingsManager.getInstance().setInstabugLocale(locale);
    }

    public static void setLocale(Locale locale) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName(State.KEY_LOCALE).setType(Locale.class).setValue(locale));
        SettingsManager.getInstance().setInstabugLocale(locale);
    }

    public static Locale getLocale(Context context) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("context").setType(Context.class));
        return SettingsManager.getInstance().getInstabugLocale(context);
    }

    public static int getUnreadMessagesCount() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0636b.m1206a();
    }

    public static void dismiss() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        getInstance().delegate.m1008j();
    }

    @Deprecated
    public static void setBugCategories(List<BugCategory> list) {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("bugCategories").setType(List.class));
        C0635a.m1196a(list);
    }

    @Deprecated
    public static void setReportCategories(List<ReportCategory> list) {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("reportCategories").setType(List.class));
        C0635a.m1200b(list);
    }

    public static void addTags(String... strArr) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        SettingsManager.getInstance().addTags(StringUtility.trimStrings(strArr));
    }

    public static ArrayList<String> getTags() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return SettingsManager.getInstance().getTags();
    }

    public static void resetTags() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        SettingsManager.getInstance().resetTags();
    }

    public static boolean isInstabugNotification(Bundle bundle) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(Bundle.class));
        return C0636b.m1213a(bundle);
    }

    public static boolean isInstabugNotification(Map<String, String> map) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(Map.class));
        return C0636b.m1214a(map);
    }

    public static void showNotification(Bundle bundle) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(Bundle.class));
        C0636b.m1216b(bundle);
    }

    public static void showNotification(Map<String, String> map) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(Map.class));
        C0636b.m1217b(map);
    }

    public static void setChatNotificationEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isChatNotificationEnable").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0636b.m1211a(z);
    }

    public static void setPushNotificationRegistrationToken(String str) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("token").setType(String.class));
        C0636b.m1210a(str);
    }

    public static void setCustomTextPlaceHolders(InstabugCustomTextPlaceHolder instabugCustomTextPlaceHolder) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugCustomTextPlaceHolder").setType(InstabugCustomTextPlaceHolder.class));
        SettingsManager.getInstance().setCustomPlaceHolders(instabugCustomTextPlaceHolder);
    }

    @Deprecated
    public static void setAttachmentTypesEnabled(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("initialScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)), new Api.Parameter().setName("extraScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z2)), new Api.Parameter().setName("galleryImage").setType(Boolean.TYPE).setValue(Boolean.valueOf(z3)), new Api.Parameter().setName("voiceNote").setType(Boolean.TYPE).setValue(Boolean.valueOf(z4)), new Api.Parameter().setName("screenRecording").setType(Boolean.TYPE).setValue(Boolean.valueOf(z5)));
        setAttachmentTypesEnabled(z, z2, z3, z5);
    }

    public static void setAttachmentTypesEnabled(boolean z, boolean z2, boolean z3, boolean z4) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("initialScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)), new Api.Parameter().setName("extraScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z2)), new Api.Parameter().setName("galleryImage").setType(Boolean.TYPE).setValue(Boolean.valueOf(z3)), new Api.Parameter().setName("screenRecording").setType(Boolean.TYPE).setValue(Boolean.valueOf(z4)));
        C0635a.m1198a(z, z2, z3, z4);
        C0636b.m1212a(z2, z3, z4);
    }

    @Deprecated
    public static void setCustomTextPlaceHolders(IBGCustomTextPlaceHolder iBGCustomTextPlaceHolder) {
        AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("ibgCustomTextPlaceHolder").setType(IBGCustomTextPlaceHolder.class));
        InstabugCustomTextPlaceHolder instabugCustomTextPlaceHolder = new InstabugCustomTextPlaceHolder();
        instabugCustomTextPlaceHolder.setPlaceHoldersMap(iBGCustomTextPlaceHolder.getConvertedHashMap());
        setCustomTextPlaceHolders(instabugCustomTextPlaceHolder);
    }

    @Nullable
    public static HashMap<String, String> getAllUserAttributes() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return UserAttributesCacheManager.getAll();
    }

    public static void setUserAttribute(@NonNull String str, String str2) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("key").setType(String.class), new Api.Parameter().setName("value").setType(String.class));
        UserAttributesCacheManager.putAttribute(str, StringUtility.trimString(str2));
    }

    @Nullable
    public static String getUserAttribute(@NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("key").setType(String.class));
        return UserAttributesCacheManager.getAttribute(str);
    }

    public static void removeUserAttribute(@NonNull String str) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("key").setType(String.class));
        UserAttributesCacheManager.deleteAttribute(str);
    }

    public static void clearAllUserAttributes() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        UserAttributesCacheManager.deleteAll();
    }

    public static void logUserEvent(@NonNull String str, UserEventParam... userEventParamArr) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("eventIdentifier").setType(String.class), new Api.Parameter().setName("userEventParams").setType(UserEventParam.class));
        InstabugUserEventLogger.getInstance().logUserEvent(str, userEventParamArr);
    }

    public static boolean showValidSurvey() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0638d.m1228a();
    }

    public static boolean hasValidSurveys() throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0638d.m1231b();
    }

    public static void setPreShowingSurveyRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0638d.m1226a(runnable);
    }

    public static void setAfterShowingSurveyRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0638d.m1230b(runnable);
    }

    public static Date getFirstRunAt() {
        return SettingsManager.getInstance().getFirstRunAt();
    }

    public static boolean isAppOnForeground() {
        return SettingsManager.getInstance().isAppOnForeground();
    }

    @Deprecated
    public static void setRequestedOrientation(int i) {
        SettingsManager.getInstance().setRequestedOrientation(i);
    }

    @CheckResult
    @Deprecated
    public static int getRequestedOrientation() {
        return OrientationUtils.getOrientation(SettingsManager.getInstance().getRequestedOrientation());
    }

    @Deprecated
    public static void resetRequestedOrientation() {
        SettingsManager.getInstance().resetRequestedOrientation();
    }

    public static void setShouldPlayConversationSounds(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySounds").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0636b.m1218b(z);
    }

    public static void setEnableSystemNotificationSound(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySound").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0636b.m1220c(z);
    }

    public static void setEnableInAppNotificationSound(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySound").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0636b.m1221d(z);
    }

    public static void setPromptOptionsEnabled(boolean z, boolean z2, boolean z3) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("chat").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)), new Api.Parameter().setName("bug").setType(Boolean.TYPE).setValue(Boolean.valueOf(z2)), new Api.Parameter().setName("feedback").setType(Boolean.TYPE).setValue(Boolean.toString(z3)));
        C0705c m1524e = C0704b.m1513c().m1524e();
        if (z) {
            m1524e.m1541d(4);
        } else {
            m1524e.m1542e(4);
        }
        if (z2) {
            m1524e.m1541d(1);
        } else {
            m1524e.m1542e(1);
        }
        if (z3) {
            m1524e.m1541d(2);
        } else {
            m1524e.m1542e(2);
        }
        if ((z && (z2 || z3)) || (z2 && z3)) {
            m1524e.m1533a(0);
            return;
        }
        if (z) {
            m1524e.m1533a(4);
        } else if (z2) {
            m1524e.m1533a(1);
        } else if (z3) {
            m1524e.m1533a(2);
        }
    }

    public static void setShakingThreshold(int i) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("threshold").setType(Integer.TYPE).setValue(Integer.toString(i)));
        C0704b.m1513c().m1524e().m1539c(i);
    }

    public static void setSuccessDialogEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0635a.m1205e(z);
    }

    public static void setIntroMessageEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        SettingsManager.getInstance().setIntroMessageEnabled(z);
    }

    public static void setCommentFieldRequired(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("commentFieldRequired").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0635a.m1203c(z);
    }

    public static void setEmailFieldVisibility(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("emailFieldVisibility").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0635a.m1204d(z);
    }

    public static void setEmailFieldRequired(boolean z) {
        C0635a.m1201b(z);
    }

    public static void setWillSkipScreenshotAnnotation(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("willSkipInitialScreenshotAnnotating").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0635a.m1197a(z);
        C0636b.m1222e(z);
    }

    public static void setTheme(@NonNull InstabugColorTheme instabugColorTheme) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugTheme").setType(InstabugColorTheme.class).setValue(instabugColorTheme));
        SettingsManager.getInstance().setTheme(instabugColorTheme);
        switch (instabugColorTheme) {
            case InstabugColorThemeDark:
                SettingsManager.getInstance().setPrimaryColor(-9580554);
                SettingsManager.getInstance().setStatusBarColor(-16119286);
                return;
            case InstabugColorThemeLight:
                SettingsManager.getInstance().setPrimaryColor(-15893761);
                SettingsManager.getInstance().setStatusBarColor(-3815737);
                return;
            default:
                return;
        }
    }

    public static boolean showSurvey(@NonNull String str) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0638d.m1229a(str);
    }

    public static boolean hasRespondToSurvey(@NonNull String str) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return C0638d.m1232b(str);
    }

    public static void setFloatingButtonEdge(@NonNull InstabugFloatingButtonEdge instabugFloatingButtonEdge) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugFloatingButtonEdge").setType(InstabugFloatingButtonEdge.class).setValue(instabugFloatingButtonEdge));
        C0704b.m1513c().m1524e().m1534a(instabugFloatingButtonEdge);
    }

    public static void setFloatingButtonOffsetFromTop(@IntRange(from = 0) int i) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("floatingButtonOffsetFromTop").setType(Integer.TYPE).setValue(Integer.toString(i)));
        C0704b.m1513c().m1524e().m1537b(i);
    }

    public static void setVideoRecordingFloatingButtonCorner(@NonNull InstabugVideoRecordingButtonCorner instabugVideoRecordingButtonCorner) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugViewRecordingButtonCorner").setType(InstabugVideoRecordingButtonCorner.class).setValue(instabugVideoRecordingButtonCorner));
        C0704b.m1513c().m1524e().m1535a(instabugVideoRecordingButtonCorner);
    }

    @Deprecated
    public static void addExtraReportField(CharSequence charSequence, boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("fieldHint").setType(CharSequence.class).setValue(charSequence), new Api.Parameter().setName("required").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
        C0635a.m1194a(charSequence, z);
    }

    public static void clearExtraReportFields() {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        C0635a.m1199b();
    }

    public static void setNotificationIcon(@DrawableRes int i) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("notificationIcon").setType(Integer.TYPE));
        C0636b.m1207a(i);
    }

    public static void setViewHierarchyState(@NonNull Feature.State state) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
        C0629b.m1160a().m1167a(Feature.VIEW_HIERARCHY, state);
    }

    public static void setExtendedBugReportState(ExtendedBugReport.State state) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(ExtendedBugReport.State.class).setValue(state));
        C0635a.m1193a(state);
    }

    public static void setCrashReportingState(@NonNull Feature.State state) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
        C0629b.m1160a().m1167a(Feature.CRASH_REPORTING, state);
    }

    public static void setAutoScreenRecordingEnabled(boolean z) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("setAutoScreenRecordingEnabled").setType(Boolean.class).setValue(String.valueOf(z)));
        SettingsManager.getInstance().setAutoScreenRecordingEnabled(z);
        InternalAutoScreenRecorderHelper.getInstance().start();
    }

    public static void setAutoScreenRecordingMaxDuration(int i) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("setAutoScreenRecordingMaxDuration").setType(Boolean.class).setValue(String.valueOf(i)));
        SettingsManager.getInstance().setAutoScreenRecordingMaxDuration(i);
    }

    public static void setTrackingUserStepsState(@NonNull Feature.State state) {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
        C0629b.m1160a().m1167a(Feature.TRACK_USER_STEPS, state);
    }

    public static void setReproStepsState(com.instabug.library.visualusersteps.State state) {
        if (state == com.instabug.library.visualusersteps.State.ENABLED) {
            C0629b.m1160a().m1167a(Feature.REPRO_STEPS, Feature.State.ENABLED);
            SettingsManager.getInstance().setReproStepsScreenshotEnabled(true);
        } else if (state == com.instabug.library.visualusersteps.State.ENABLED_WITH_NO_SCREENSHOTS) {
            C0629b.m1160a().m1167a(Feature.REPRO_STEPS, Feature.State.ENABLED);
            SettingsManager.getInstance().setReproStepsScreenshotEnabled(false);
        } else if (state == com.instabug.library.visualusersteps.State.DISABLED) {
            C0629b.m1160a().m1167a(Feature.REPRO_STEPS, Feature.State.DISABLED);
        }
    }

    @SuppressFBWarnings({"URF_UNREAD_FIELD"})
    /* loaded from: classes.dex */
    public static class Builder {
        private Application application;
        private Context applicationContext;
        private String applicationToken;
        private AttachmentsTypesParams attachmentsTypesParams;
        private boolean bugPromptOptionEnable;
        private boolean chatPromptOptionEnable;
        private boolean commentFieldRequired;
        private Feature.State consoleLogState;
        private Feature.State crashReportingState;
        private int defaultInvocationMode;
        private boolean emailFieldRequired;
        private boolean emailFieldVisibility;
        private boolean feedbackPromptOptionEnable;
        private int floatingButtonOffsetFromTop;
        private Feature.State inAppMessagingState;
        private InstabugFloatingButtonEdge instabugFloatingButtonEdge;
        private InstabugInvocationEvent instabugInvocationEvent;
        private Locale instabugLocale;
        private Feature.State instabugLogState;
        private int instabugPrimaryColor;
        private int instabugStatusBarColor;
        private InstabugColorTheme instabugTheme;
        private boolean introMessageEnabled;
        private boolean isSurveysAutoShowing;
        private int notificationIcon;
        private boolean playInAppNotificationSound;
        private boolean playSystemNotificationSound;
        private Feature.State pushNotificationState;
        private com.instabug.library.visualusersteps.State reproStepsState;
        private int shakingThreshold;
        private boolean shouldPlaySounds;
        private boolean successDialogEnabled;
        private Feature.State surveysState;
        private Feature.State trackingUserStepsState;
        private Feature.State userDataState;
        private Feature.State userEventsState;
        private Feature.State viewHierarchyState;
        private boolean willSkipInitialScreenshotAnnotating;

        public Builder(@NonNull Application application, @NonNull String str) {
            this(application, str, InstabugInvocationEvent.SHAKE);
        }

        public Builder(@NonNull Application application, @NonNull String str, @NonNull InstabugInvocationEvent instabugInvocationEvent) {
            this(application.getApplicationContext(), str, instabugInvocationEvent);
            this.application = application;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(@NonNull Context context, @NonNull String str, @NonNull InstabugInvocationEvent instabugInvocationEvent) {
            this.instabugTheme = InstabugColorTheme.InstabugColorThemeLight;
            this.instabugPrimaryColor = -15893761;
            this.instabugStatusBarColor = -3815737;
            this.instabugInvocationEvent = InstabugInvocationEvent.SHAKE;
            this.defaultInvocationMode = 0;
            this.userDataState = C0629b.f775a;
            this.consoleLogState = C0629b.f775a;
            this.instabugLogState = C0629b.f775a;
            this.inAppMessagingState = C0629b.f775a;
            this.crashReportingState = C0629b.f775a;
            this.pushNotificationState = C0629b.f775a;
            this.trackingUserStepsState = C0629b.f775a;
            this.reproStepsState = com.instabug.library.visualusersteps.State.ENABLED;
            this.viewHierarchyState = C0629b.f775a;
            this.surveysState = C0629b.f775a;
            this.userEventsState = C0629b.f775a;
            this.emailFieldRequired = true;
            this.emailFieldVisibility = true;
            this.attachmentsTypesParams = new AttachmentsTypesParams();
            this.willSkipInitialScreenshotAnnotating = false;
            this.commentFieldRequired = false;
            this.introMessageEnabled = true;
            this.shouldPlaySounds = false;
            this.successDialogEnabled = true;
            this.instabugFloatingButtonEdge = InstabugFloatingButtonEdge.RIGHT;
            this.shakingThreshold = 350;
            this.floatingButtonOffsetFromTop = -1;
            this.instabugLocale = Locale.getDefault();
            this.isSurveysAutoShowing = true;
            this.chatPromptOptionEnable = true;
            this.bugPromptOptionEnable = true;
            this.feedbackPromptOptionEnable = true;
            this.applicationContext = context;
            this.instabugInvocationEvent = instabugInvocationEvent;
            this.applicationToken = str;
        }

        @Deprecated
        public Builder setInvocationEvent(@NonNull IBGInvocationEvent iBGInvocationEvent) {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("invocationEvent").setType(IBGInvocationEvent.class).setValue(iBGInvocationEvent));
            switch (iBGInvocationEvent) {
                case IBGInvocationEventNone:
                    setInvocationEvent(InstabugInvocationEvent.NONE);
                    break;
                case IBGInvocationEventShake:
                    setInvocationEvent(InstabugInvocationEvent.SHAKE);
                    break;
                case IBGInvocationEventFloatingButton:
                    setInvocationEvent(InstabugInvocationEvent.FLOATING_BUTTON);
                    break;
                case IBGInvocationEventTwoFingersSwipeLeft:
                    setInvocationEvent(InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT);
                    break;
                case IBGInvocationScreenshotGesture:
                    setInvocationEvent(InstabugInvocationEvent.SCREENSHOT_GESTURE);
                    break;
            }
            return this;
        }

        public Builder setInvocationEvent(@NonNull InstabugInvocationEvent instabugInvocationEvent) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugInvocationEvent").setType(InstabugInvocationEvent.class).setValue(instabugInvocationEvent));
            this.instabugInvocationEvent = instabugInvocationEvent;
            return this;
        }

        @Deprecated
        public Builder setShouldPlayConversationSounds(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySounds").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.shouldPlaySounds = z;
            return this;
        }

        @Deprecated
        public Builder setEnableSystemNotificationSound(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySound").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.playSystemNotificationSound = z;
            return this;
        }

        @Deprecated
        public Builder setEnableInAppNotificationSound(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySound").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.playInAppNotificationSound = z;
            return this;
        }

        @Deprecated
        public Builder setDefaultInvocationMode(@NonNull IBGInvocationMode iBGInvocationMode) {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("invocationMode").setType(IBGInvocationMode.class).setValue(iBGInvocationMode));
            switch (iBGInvocationMode) {
                case IBGInvocationModeNA:
                    this.defaultInvocationMode = 0;
                    break;
                case IBGInvocationModeBugReporter:
                    this.defaultInvocationMode = 1;
                    break;
                case IBGInvocationModeFeedbackSender:
                    this.defaultInvocationMode = 2;
                    break;
            }
            return this;
        }

        @Deprecated
        public Builder setPromptOptionsEnabled(boolean z, boolean z2, boolean z3) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("chat").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)), new Api.Parameter().setName("bug").setType(Boolean.TYPE).setValue(Boolean.valueOf(z2)), new Api.Parameter().setName("feedback").setType(Boolean.TYPE).setValue(Boolean.toString(z3)));
            this.chatPromptOptionEnable = z;
            this.bugPromptOptionEnable = z2;
            this.feedbackPromptOptionEnable = z3;
            return this;
        }

        @Deprecated
        public Builder setShakingThreshold(int i) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("threshold").setType(Integer.TYPE).setValue(Integer.toString(i)));
            this.shakingThreshold = i;
            return this;
        }

        @Deprecated
        public Builder setShouldShowIntroDialog(boolean z) {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.introMessageEnabled = z;
            return this;
        }

        @Deprecated
        public Builder setSuccessDialogEnabled(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.successDialogEnabled = z;
            return this;
        }

        @Deprecated
        public Builder setIntroMessageEnabled(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enabled").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.introMessageEnabled = z;
            return this;
        }

        public Builder setTrackingUserStepsState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.trackingUserStepsState = state;
            return this;
        }

        public Builder setReproStepsState(com.instabug.library.visualusersteps.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(com.instabug.library.visualusersteps.State.class).setValue(state));
            this.reproStepsState = state;
            return this;
        }

        public Builder setPushNotificationState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.pushNotificationState = state;
            return this;
        }

        public Builder setConsoleLogState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.consoleLogState = state;
            return this;
        }

        public Builder setCrashReportingState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.crashReportingState = state;
            return this;
        }

        public Builder setInstabugLogState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.instabugLogState = state;
            return this;
        }

        public Builder setUserDataState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.userDataState = state;
            return this;
        }

        public Builder setInAppMessagingState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.inAppMessagingState = state;
            return this;
        }

        public Builder setViewHierarchyState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.viewHierarchyState = state;
            return this;
        }

        public Builder setSurveysState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.surveysState = state;
            return this;
        }

        public Builder setUserEventsState(@NonNull Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("state").setType(Feature.State.class).setValue(state));
            this.userEventsState = state;
            return this;
        }

        @Deprecated
        public Builder setCommentFieldRequired(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("commentFieldRequired").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.commentFieldRequired = z;
            return this;
        }

        @Deprecated
        public Builder setEmailFieldRequired(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("emailFieldRequired").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.emailFieldRequired = z;
            return this;
        }

        @Deprecated
        public Builder setEmailFieldVisibility(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("emailFieldVisibility").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.emailFieldVisibility = z;
            return this;
        }

        @Deprecated
        public Builder setWillTakeScreenshot(boolean z) throws IllegalStateException {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("willTakeInitialScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.attachmentsTypesParams.setShouldTakesInitialScreenshot(z);
            return this;
        }

        @Deprecated
        public Builder setAttachmentTypesEnabled(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("initialScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)), new Api.Parameter().setName("extraScreenshot").setType(Boolean.TYPE).setValue(Boolean.valueOf(z2)), new Api.Parameter().setName("galleryImage").setType(Boolean.TYPE).setValue(Boolean.valueOf(z3)), new Api.Parameter().setName("voiceNote").setType(Boolean.TYPE).setValue(Boolean.valueOf(z4)), new Api.Parameter().setName("screenRecording").setType(Boolean.TYPE).setValue(Boolean.valueOf(z5)));
            this.attachmentsTypesParams.setShouldTakesInitialScreenshot(z).setAllowTakeExtraScreenshot(z2).setAllowAttachImageFromGallery(z3).setAllowScreenRecording(z5);
            return this;
        }

        @Deprecated
        public Builder setWillSkipScreenshotAnnotation(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("willSkipInitialScreenshotAnnotating").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.willSkipInitialScreenshotAnnotating = z;
            return this;
        }

        @Deprecated
        public Builder setColorTheme(@NonNull IBGColorTheme iBGColorTheme) {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("theme").setType(IBGColorTheme.class).setValue(iBGColorTheme));
            switch (iBGColorTheme) {
                case IBGColorThemeDark:
                    this.instabugTheme = InstabugColorTheme.InstabugColorThemeDark;
                    this.instabugPrimaryColor = -9580554;
                    break;
                case IBGColorThemeLight:
                    this.instabugTheme = InstabugColorTheme.InstabugColorThemeLight;
                    this.instabugPrimaryColor = -15893761;
                    break;
                case theme:
                    this.instabugTheme = InstabugColorTheme.InstabugColorThemeLight;
                    this.instabugPrimaryColor = -15893761;
                    break;
            }
            return this;
        }

        @Deprecated
        public Builder setTheme(@NonNull InstabugColorTheme instabugColorTheme) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugTheme").setType(InstabugColorTheme.class).setValue(instabugColorTheme));
            this.instabugTheme = instabugColorTheme;
            switch (instabugColorTheme) {
                case InstabugColorThemeDark:
                    this.instabugPrimaryColor = -9580554;
                    this.instabugStatusBarColor = -16119286;
                    break;
                case InstabugColorThemeLight:
                    this.instabugPrimaryColor = -15893761;
                    this.instabugStatusBarColor = -3815737;
                    break;
            }
            return this;
        }

        @Deprecated
        public Builder setFloatingButtonEdge(@NonNull IBGFloatingButtonEdge iBGFloatingButtonEdge) {
            AnalyticsObserver.getInstance().catchDeprecatedApiUsage(new Api.Parameter().setName("floatingButtonEdge").setType(IBGFloatingButtonEdge.class).setValue(iBGFloatingButtonEdge));
            switch (iBGFloatingButtonEdge) {
                case Right:
                    setFloatingButtonEdge(InstabugFloatingButtonEdge.RIGHT);
                    break;
                case Left:
                    setFloatingButtonEdge(InstabugFloatingButtonEdge.LEFT);
                    break;
            }
            return this;
        }

        @Deprecated
        public Builder setFloatingButtonEdge(@NonNull InstabugFloatingButtonEdge instabugFloatingButtonEdge) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugFloatingButtonEdge").setType(IBGFloatingButtonEdge.class).setValue(instabugFloatingButtonEdge));
            this.instabugFloatingButtonEdge = instabugFloatingButtonEdge;
            return this;
        }

        @Deprecated
        public Builder setFloatingButtonOffsetFromTop(@IntRange(from = 0) int i) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("floatingButtonOffsetFromTop").setType(Integer.TYPE).setValue(Integer.toString(i)));
            this.floatingButtonOffsetFromTop = i;
            return this;
        }

        @Deprecated
        public Builder setLocale(Locale locale) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName(State.KEY_LOCALE).setType(Locale.class).setValue(locale));
            this.instabugLocale = locale;
            return this;
        }

        @Deprecated
        public Builder setNotificationIcon(int i) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("notificationIcon").setType(Integer.TYPE));
            this.notificationIcon = i;
            return this;
        }

        @Deprecated
        public Builder setSurveysAutoShowing(boolean z) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("isSurveysAutoShowing").setType(Boolean.TYPE).setValue(Boolean.valueOf(z)));
            this.isSurveysAutoShowing = z;
            return this;
        }

        public Instabug build(Feature.State state) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("instabugInitialState").setType(Feature.State.class).setValue(state));
            SettingsManager.init(this.applicationContext);
            setFeaturesStates(Boolean.valueOf(state == Feature.State.ENABLED));
            C0578a c0578a = new C0578a(this.applicationContext);
            Instabug unused = Instabug.INSTANCE = new Instabug(c0578a);
            Instabug.setState(InstabugState.BUILT);
            InstabugInternalTrackingDelegate.init(this.application);
            SettingsManager.getInstance().setAppToken(this.applicationToken);
            SettingsManager.getInstance().setIntroMessageEnabled(this.introMessageEnabled);
            c0578a.m998a();
            SettingsManager.getInstance().setInstabugLocale(this.instabugLocale);
            SettingsManager.getInstance().setTheme(this.instabugTheme);
            SettingsManager.getInstance().setPrimaryColor(this.instabugPrimaryColor);
            SettingsManager.getInstance().setStatusBarColor(this.instabugStatusBarColor);
            C0635a.m1198a(this.attachmentsTypesParams.isShouldTakesInitialScreenshot(), this.attachmentsTypesParams.isAllowTakeExtraScreenshot(), this.attachmentsTypesParams.isAllowAttachImageFromGallery(), this.attachmentsTypesParams.isAllowScreenRecording());
            C0635a.m1201b(this.emailFieldRequired);
            C0635a.m1204d(this.emailFieldVisibility);
            C0635a.m1197a(this.willSkipInitialScreenshotAnnotating);
            C0635a.m1205e(this.successDialogEnabled);
            C0704b.m1513c().m1522a(this.instabugInvocationEvent);
            Instabug.setPromptOptionsEnabled(this.chatPromptOptionEnable, this.bugPromptOptionEnable, this.feedbackPromptOptionEnable);
            C0704b.m1513c().m1524e().m1533a(this.defaultInvocationMode);
            C0704b.m1513c().m1524e().m1539c(this.shakingThreshold);
            C0704b.m1513c().m1524e().m1534a(this.instabugFloatingButtonEdge);
            if (this.floatingButtonOffsetFromTop != -1) {
                C0704b.m1513c().m1524e().m1537b(this.floatingButtonOffsetFromTop);
            }
            C0635a.m1203c(this.commentFieldRequired);
            C0636b.m1218b(this.shouldPlaySounds);
            C0636b.m1220c(this.playSystemNotificationSound);
            C0636b.m1221d(this.playInAppNotificationSound);
            C0636b.m1207a(this.notificationIcon);
            C0636b.m1212a(this.attachmentsTypesParams.isAllowTakeExtraScreenshot(), this.attachmentsTypesParams.isAllowAttachImageFromGallery(), this.attachmentsTypesParams.isAllowScreenRecording());
            C0636b.m1222e(this.willSkipInitialScreenshotAnnotating);
            C0638d.m1227a(this.isSurveysAutoShowing);
            return Instabug.INSTANCE;
        }

        public Instabug build() {
            if (Instabug.isBuilt()) {
                if (InternalScreenRecordHelper.getInstance().isRecording()) {
                    InternalScreenRecordHelper.getInstance().cancel();
                }
                Instabug.disable();
            }
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
            return build(Feature.State.ENABLED);
        }

        private void setFeaturesStates(Boolean bool) {
            InstabugSDKLogger.m1803v(this, "Setting user data feature state " + this.userDataState);
            C0629b.m1160a().m1167a(Feature.USER_DATA, this.userDataState);
            InstabugSDKLogger.m1803v(this, "Setting console log feature state " + this.consoleLogState);
            C0629b.m1160a().m1167a(Feature.CONSOLE_LOGS, this.consoleLogState);
            InstabugSDKLogger.m1803v(this, "Setting Instabug logs feature state " + this.instabugLogState);
            C0629b.m1160a().m1167a(Feature.INSTABUG_LOGS, this.instabugLogState);
            InstabugSDKLogger.m1803v(this, "Setting crash reporting feature state " + this.crashReportingState);
            C0629b.m1160a().m1167a(Feature.CRASH_REPORTING, this.crashReportingState);
            InstabugSDKLogger.m1803v(this, "Setting in-app messaging feature state " + this.inAppMessagingState);
            C0629b.m1160a().m1167a(Feature.IN_APP_MESSAGING, this.inAppMessagingState);
            InstabugSDKLogger.m1803v(this, "Setting push notification feature state " + this.pushNotificationState);
            C0629b.m1160a().m1167a(Feature.PUSH_NOTIFICATION, this.pushNotificationState);
            InstabugSDKLogger.m1803v(this, "Setting tracking user steps feature state " + this.trackingUserStepsState);
            C0629b.m1160a().m1167a(Feature.TRACK_USER_STEPS, this.trackingUserStepsState);
            InstabugSDKLogger.m1803v(this, "Setting repro steps feature state " + this.reproStepsState);
            Instabug.setReproStepsState(this.reproStepsState);
            InstabugSDKLogger.m1803v(this, "Setting view hierarchy  feature state " + this.viewHierarchyState);
            C0629b.m1160a().m1167a(Feature.VIEW_HIERARCHY, this.viewHierarchyState);
            InstabugSDKLogger.m1803v(this, "Setting surveys feature state " + this.surveysState);
            C0629b.m1160a().m1167a(Feature.SURVEYS, this.surveysState);
            InstabugSDKLogger.m1803v(this, "Setting user events feature state " + this.userEventsState);
            C0629b.m1160a().m1167a(Feature.USER_EVENTS, this.userEventsState);
            InstabugSDKLogger.m1803v(this, "Setting instabug overall state " + bool);
            C0629b.m1160a().m1168a(Feature.INSTABUG, bool.booleanValue());
        }
    }
}
