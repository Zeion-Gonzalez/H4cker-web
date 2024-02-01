package com.instabug.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.network.InstabugPushNotificationTokenService;
import com.instabug.chat.p006a.C0510b;
import com.instabug.chat.p011ui.C0540a;
import com.instabug.chat.settings.AttachmentTypesState;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.core.InstabugCore;
import java.util.Map;

/* loaded from: classes.dex */
public class InstabugChat {
    public static void showChats() {
        ChatPlugin chatPlugin;
        Context appContext;
        if (isReadyToRun() && (chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class)) != null && (appContext = chatPlugin.getAppContext()) != null) {
            appContext.startActivity(C0540a.m786a(appContext));
        }
    }

    public static void openNewChat() {
        ChatPlugin chatPlugin;
        Context appContext;
        if (isReadyToRun() && (chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class)) != null && (appContext = chatPlugin.getAppContext()) != null) {
            appContext.startActivity(C0540a.m789b(appContext));
        }
    }

    public static void setDefaultInvocationMode(int i) {
        if (isReadyToRun()) {
            Instabug.setDefaultInvocationMode(i);
        }
    }

    public static void resetDefaultInvocationMode() {
        if (isReadyToRun()) {
            Instabug.resetDefaultInvocationMode();
        }
    }

    public static void setNewMessageHandler(Runnable runnable) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("runnable").setType(runnable.getClass()));
            C0537a.m727a(runnable);
        }
    }

    public static int getUnreadMessagesCount() throws IllegalStateException {
        if (!isReadyToRun()) {
            return 0;
        }
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter[0]);
        return ChatsCacheManager.getUnreadCount();
    }

    public static void enableNotification(boolean z) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enable").setType(Boolean.class).setValue(Boolean.toString(z)));
            C0537a.m729a(z);
        }
    }

    public static void enableSystemNotificationSound(boolean z) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySound").setType(Boolean.class).setValue(Boolean.toString(z)));
            C0537a.m733b(z);
        }
    }

    public static void enableInAppNotificationSound(boolean z) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("enable").setType(Boolean.class).setValue(Boolean.toString(z)));
            C0537a.m734c(z);
        }
    }

    public static void setNotificationIcon(@DrawableRes int i) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("notificationIcon").setType(Integer.class));
            C0537a.m722a(i);
        }
    }

    public static void setPushNotificationRegistrationToken(String str) {
        Context appContext;
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("token").setType(String.class));
            if (str != null && !str.isEmpty() && !C0537a.m747l().equalsIgnoreCase(str) && InstabugCore.getFeatureState(Feature.PUSH_NOTIFICATION) == Feature.State.ENABLED) {
                C0537a.m728a(str);
                C0537a.m740f(false);
                ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
                if (chatPlugin != null && (appContext = chatPlugin.getAppContext()) != null) {
                    appContext.startService(new Intent(appContext, InstabugPushNotificationTokenService.class));
                }
            }
        }
    }

    public static boolean isInstabugNotification(Bundle bundle) {
        if (!isReadyToRun()) {
            return false;
        }
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(bundle.getClass()));
        return C0510b.m494a().m505b(bundle);
    }

    public static boolean isInstabugNotification(Map<String, String> map) {
        if (!isReadyToRun()) {
            return false;
        }
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(map.getClass()));
        return C0510b.m494a().m506b(map);
    }

    public static void showNotification(Bundle bundle) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(bundle.getClass()));
            C0510b.m494a().m503a(bundle);
        }
    }

    public static void showNotification(Map<String, String> map) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("data").setType(map.getClass()));
            C0510b.m494a().m504a(map);
        }
    }

    public static void enableConversationSound(boolean z) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("shouldPlaySounds").setType(Boolean.class).setValue(Boolean.toString(z)));
            C0537a.m737d(z);
        }
    }

    public static void setAttachmentTypesEnabled(boolean z, boolean z2, boolean z3) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("extraScreenshot").setType(Boolean.class).setValue(Boolean.toString(z)), new Api.Parameter().setName("galleryImage").setType(Boolean.class).setValue(Boolean.toString(z2)), new Api.Parameter().setName("screenRecording").setType(Boolean.class).setValue(Boolean.toString(z3)));
            C0537a.m725a(new AttachmentTypesState(z, z2, z3));
        }
    }

    public static void skipImageAttachmentAnnotation(boolean z) {
        if (isReadyToRun()) {
            AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("skipImageAnnotation").setType(Boolean.class).setValue(Boolean.toString(z)));
            C0537a.m739e(z);
        }
    }

    private static boolean isReadyToRun() {
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null) {
            return chatPlugin.isAppContextAvailable() && isChatFeatureEnabled();
        }
        return false;
    }

    private static boolean isChatFeatureEnabled() {
        return InstabugCore.getFeatureState(Feature.IN_APP_MESSAGING) == Feature.State.ENABLED;
    }

    public static void setOnSdkDismissedCallback(OnSdkDismissedCallback onSdkDismissedCallback) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("onSdkDismissedCallback").setType(OnSdkDismissedCallback.class));
        C0537a.m726a(onSdkDismissedCallback);
    }
}
