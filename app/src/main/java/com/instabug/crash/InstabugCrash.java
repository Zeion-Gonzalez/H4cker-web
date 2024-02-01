package com.instabug.crash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.crash.cache.CrashesCacheManager;
import com.instabug.crash.models.Crash;
import com.instabug.crash.network.InstabugCrashesUploaderService;
import com.instabug.crash.p015a.C0559a;
import com.instabug.crash.p015a.C0560b;
import com.instabug.crash.p016b.C0561a;
import com.instabug.crash.p017c.C0564a;
import com.instabug.library.Feature;
import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.AutoScreenRecordingEventBus;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.video.AutoScreenRecordingService;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class InstabugCrash {
    public static void reportCaughtException(Context context, Throwable th, @Nullable String str) {
        if (InstabugCore.getFeatureState(Feature.CRASH_REPORTING) != Feature.State.DISABLED) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("error", C0564a.m934a(th, str));
                if (C0561a.m919a().m923b() != null) {
                    try {
                        C0561a.m919a().m923b().run();
                    } catch (Exception e) {
                        InstabugSDKLogger.m1801e(InstabugCrash.class, "Pre sending runnable failed to run.", e);
                    }
                }
                Crash m953a = new Crash.C0567a().m953a(context);
                m953a.m947c(jSONObject.toString());
                m953a.m939a(Crash.CrashState.READY_TO_BE_SENT);
                m953a.m943a(true);
                if (InstabugCore.getExtraAttachmentFiles().size() >= 1) {
                    for (Map.Entry<Uri, String> entry : InstabugCore.getExtraAttachmentFiles().entrySet()) {
                        m953a.m937a(AttachmentsUtility.getNewFileAttachmentUri(context, entry.getKey(), entry.getValue()));
                    }
                }
                if (C0560b.m917a().isEnabled()) {
                    AutoScreenRecordingEventBus.getInstance().post(AutoScreenRecordingService.Action.STOP_KEEP);
                    C0559a.m916a(m953a);
                }
                CrashesCacheManager.addCrash(m953a);
                InstabugSDKLogger.m1802i(InstabugCrash.class, "ReportCaughtException: Your exception has been reported");
                context.startService(new Intent(context, InstabugCrashesUploaderService.class));
                C0560b.m917a().m918b();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void setPreSendingRunnable(Runnable runnable) throws IllegalStateException {
        AnalyticsObserver.getInstance().catchApiUsage(new Api.Parameter().setName("runnable").setType(Runnable.class));
        C0561a.m919a().m922a(runnable);
    }
}
