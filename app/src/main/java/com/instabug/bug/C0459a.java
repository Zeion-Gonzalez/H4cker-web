package com.instabug.bug;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.bug.cache.C0467a;
import com.instabug.bug.model.EnumC0472b;
import com.instabug.bug.model.ReportCategory;
import com.instabug.bug.p002b.C0462a;
import com.instabug.bug.screenshot.viewhierarchy.C0478a;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.ReportCategoriesActivity;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InitialScreenshotHelper;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEventSubscriber;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.model.Attachment;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugMemoryUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.LocaleUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.ArrayList;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: BugPluginWrapper.java */
/* renamed from: com.instabug.bug.a */
/* loaded from: classes.dex */
public class C0459a {
    /* renamed from: a */
    public static void m26a(Context context) {
        C0482a.m237a(context);
        C0467a.m85a(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static Subscription m22a(Action1<SDKCoreEvent> action1) {
        return SDKCoreEventSubscriber.subscribe(action1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static Action1<SDKCoreEvent> m30b(final Context context) {
        return new Action1<SDKCoreEvent>() { // from class: com.instabug.bug.a.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(SDKCoreEvent sDKCoreEvent) {
                C0464c.m74a(context, sDKCoreEvent);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public static ArrayList<PluginPromptOption> m35c(Context context) {
        ArrayList<PluginPromptOption> arrayList = new ArrayList<>();
        arrayList.add(m38d(context));
        arrayList.add(m41e(context));
        return arrayList;
    }

    /* renamed from: d */
    private static PluginPromptOption m38d(final Context context) {
        PluginPromptOption pluginPromptOption = new PluginPromptOption();
        pluginPromptOption.setOrder(2);
        pluginPromptOption.setInvocationMode(1);
        pluginPromptOption.setIcon(C0458R.drawable.instabug_ic_report_bug);
        pluginPromptOption.setTitle(m47h(context));
        pluginPromptOption.setOnInvocationListener(new PluginPromptOption.OnInvocationListener() { // from class: com.instabug.bug.a.2
            @Override // com.instabug.library.core.plugin.PluginPromptOption.OnInvocationListener
            public void onInvoke(Uri uri) {
                C0459a.m40d(context, uri);
            }
        });
        return pluginPromptOption;
    }

    /* renamed from: e */
    private static PluginPromptOption m41e(final Context context) {
        PluginPromptOption pluginPromptOption = new PluginPromptOption();
        pluginPromptOption.setOrder(3);
        pluginPromptOption.setInvocationMode(2);
        pluginPromptOption.setIcon(C0458R.drawable.instabug_ic_send_feedback);
        pluginPromptOption.setTitle(m49i(context));
        pluginPromptOption.setOnInvocationListener(new PluginPromptOption.OnInvocationListener() { // from class: com.instabug.bug.a.3
            @Override // com.instabug.library.core.plugin.PluginPromptOption.OnInvocationListener
            public void onInvoke(Uri uri) {
                C0459a.m37c(context, uri);
            }
        });
        return pluginPromptOption;
    }

    /* renamed from: a */
    private static void m24a(int i) {
        if (C0482a.m236a().m254d().isShouldTakesInitialScreenshot()) {
            m31b(i);
        } else {
            m32b(i, (Uri) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public static void m32b(int i, Uri uri) {
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            switch (i) {
                case 2:
                    m40d(bugPlugin.getAppContext(), uri);
                    return;
                case 3:
                    m37c(bugPlugin.getAppContext(), uri);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: b */
    private static void m31b(final int i) {
        InitialScreenshotHelper.captureScreenshot(new InitialScreenshotHelper.InitialScreenshotCapturingListener() { // from class: com.instabug.bug.a.4
            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturedSuccessfully(Uri uri) {
                C0459a.m32b(i, uri);
            }

            @Override // com.instabug.library.core.InitialScreenshotHelper.InitialScreenshotCapturingListener
            public void onScreenshotCapturingFailed(Throwable th) {
                InstabugSDKLogger.m1801e(this, "initial screenshot capturing got error: " + th.getMessage() + ", time in MS: " + System.currentTimeMillis(), th);
            }
        });
    }

    /* renamed from: e */
    private static void m42e() {
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            bugPlugin.setState(1);
        }
        InstabugSDKLogger.m1799d(C0459a.class, "setPluginStateForeground->change plugin state to FOREGROUND");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public static void m37c(Context context, Uri uri) {
        m42e();
        m43f();
        InstabugSDKLogger.m1799d(C0459a.class, "Handle invocation request new feedback");
        m28a(uri);
        C0468d.m86a().m103d().m133f(context.getString(C0458R.string.instabug_str_feedback_header));
        m45g();
        if (ReportCategory.hasSubCategories(EnumC0472b.FEEDBACK)) {
            context.startActivity(ReportCategoriesActivity.m321a(context, EnumC0472b.FEEDBACK));
        } else {
            m46g(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public static void m40d(Context context, Uri uri) {
        m42e();
        m43f();
        InstabugSDKLogger.m1799d(C0459a.class, "Handle invocation request new bug");
        m28a(uri);
        C0468d.m86a().m103d().m133f(context.getString(C0458R.string.instabug_str_bug_header));
        m45g();
        if (ReportCategory.hasSubCategories(EnumC0472b.BUG)) {
            context.startActivity(ReportCategoriesActivity.m321a(context, EnumC0472b.BUG));
        } else {
            m44f(context);
        }
    }

    /* renamed from: f */
    private static void m43f() {
        if (InstabugCore.getOnSdkInvokedCallback() != null) {
            InstabugCore.getOnSdkInvokedCallback().onSdkInvoked();
        }
    }

    /* renamed from: a */
    private static void m28a(@Nullable Uri uri) {
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            C0468d.m86a().m93a(bugPlugin.getAppContext());
        }
        if (uri != null && C0482a.m236a().m254d().isShouldTakesInitialScreenshot()) {
            C0468d.m86a().m103d().m127c(uri.getPath());
        }
        if (C0462a.m55a().isEnabled()) {
            Uri autoScreenRecordingFileUri = C0462a.m55a().getAutoScreenRecordingFileUri();
            C0462a.m55a().clear();
            if (autoScreenRecordingFileUri != null) {
                Attachment attachment = new Attachment();
                attachment.setName(autoScreenRecordingFileUri.getLastPathSegment());
                attachment.setLocalPath(autoScreenRecordingFileUri.getPath());
                attachment.setType(Attachment.Type.AUTO_SCREEN_RECORDING);
                C0468d.m86a().m103d().m131e().add(attachment);
            }
        }
    }

    /* renamed from: g */
    private static void m45g() {
        if (C0468d.m86a().m103d() != null && C0468d.m86a().m103d().m138k() && !InstabugMemoryUtils.isLowMemory()) {
            C0478a.m172a(InstabugInternalTrackingDelegate.getInstance().getTargetActivity());
        }
    }

    /* renamed from: f */
    private static void m44f(Context context) {
        context.startActivity(C0461b.m51a(context));
    }

    /* renamed from: g */
    private static void m46g(Context context) {
        context.startActivity(C0461b.m52b(context));
    }

    @NonNull
    /* renamed from: h */
    private static String m47h(Context context) {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.REPORT_BUG, LocaleUtils.getLocaleStringResource(Instabug.getLocale(context), C0458R.string.instabug_str_bug_header, context));
    }

    @NonNull
    /* renamed from: i */
    private static String m49i(Context context) {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.REPORT_FEEDBACK, LocaleUtils.getLocaleStringResource(Instabug.getLocale(context), C0458R.string.instabug_str_feedback_header, context));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m29a(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /* renamed from: a */
    public static void m23a() {
        C0467a.m84a();
    }

    /* renamed from: b */
    public static boolean m34b() {
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            return bugPlugin.isAppContextAvailable() && m48h();
        }
        return false;
    }

    /* renamed from: h */
    private static boolean m48h() {
        return InstabugCore.getFeatureState(Feature.INSTABUG) == Feature.State.ENABLED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public static void m36c() {
        if (m34b()) {
            m24a(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public static void m39d() {
        if (m34b()) {
            m24a(2);
        }
    }
}
