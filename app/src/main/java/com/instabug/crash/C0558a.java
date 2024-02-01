package com.instabug.crash;

import android.content.Context;
import android.net.Uri;
import com.instabug.crash.cache.CrashesCacheManager;
import com.instabug.crash.models.Crash;
import com.instabug.crash.p015a.C0559a;
import com.instabug.crash.p015a.C0560b;
import com.instabug.crash.p016b.C0561a;
import com.instabug.crash.p017c.C0564a;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.AutoScreenRecordingEventBus;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.video.AutoScreenRecordingService;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.Thread;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: InstabugUncaughtExceptionHandler.java */
/* renamed from: com.instabug.crash.a */
/* loaded from: classes.dex */
public class C0558a implements Thread.UncaughtExceptionHandler {

    /* renamed from: a */
    Thread.UncaughtExceptionHandler f532a = Thread.getDefaultUncaughtExceptionHandler();

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        if (InstabugCore.getFeatureState(Feature.CRASH_REPORTING) == Feature.State.DISABLED) {
            this.f532a.uncaughtException(thread, th);
            return;
        }
        InstabugSDKLogger.m1801e(Instabug.class, "Instabug Caught an Unhandled Exception: " + th.getClass().getCanonicalName(), th);
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("threadName", thread.getName());
            jSONObject2.put("threadId", thread.getId());
            jSONObject2.put("threadPriority", thread.getPriority());
            jSONObject2.put("threadState", thread.getState().toString());
            ThreadGroup threadGroup = thread.getThreadGroup();
            if (threadGroup != null) {
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("name", threadGroup.getName());
                jSONObject3.put("maxPriority", threadGroup.getMaxPriority());
                jSONObject3.put("activeCount", threadGroup.activeCount());
                jSONObject2.put("threadGroup", jSONObject3);
            }
            jSONObject.put("thread", jSONObject2);
            jSONObject.put("error", C0564a.m934a(th, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (C0561a.m919a().m923b() != null) {
            try {
                C0561a.m919a().m923b().run();
            } catch (Exception e2) {
                InstabugSDKLogger.m1801e(Instabug.class, "Pre sending runnable failed to run.", e2);
            }
        }
        Crash m953a = new Crash.C0567a().m953a(Instabug.getApplicationContext());
        m953a.m947c(jSONObject.toString());
        m953a.m939a(Crash.CrashState.READY_TO_BE_SENT);
        m953a.m943a(false);
        Context applicationContext = Instabug.getApplicationContext();
        if (InstabugCore.getExtraAttachmentFiles().size() >= 1) {
            for (Map.Entry<Uri, String> entry : InstabugCore.getExtraAttachmentFiles().entrySet()) {
                m953a.m937a(AttachmentsUtility.getNewFileAttachmentUri(applicationContext, entry.getKey(), entry.getValue()));
            }
        }
        if (C0560b.m917a().isEnabled()) {
            AutoScreenRecordingEventBus.getInstance().post(AutoScreenRecordingService.Action.STOP_KEEP);
            C0559a.m916a(m953a);
        }
        CrashesCacheManager.addCrash(m953a);
        CrashesCacheManager.saveCacheToDisk();
        InstabugSDKLogger.m1802i(Instabug.class, "Crash persisted for upload at next startup");
        this.f532a.uncaughtException(thread, th);
    }
}
