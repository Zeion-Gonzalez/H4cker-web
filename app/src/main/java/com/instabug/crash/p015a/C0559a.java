package com.instabug.crash.p015a;

import android.net.Uri;
import com.instabug.crash.models.Crash;
import com.instabug.library.model.Attachment;
import com.instabug.library.settings.SettingsManager;

/* compiled from: AutoScreenRecordingUtils.java */
/* renamed from: com.instabug.crash.a.a */
/* loaded from: classes.dex */
public class C0559a {
    /* renamed from: a */
    public static void m916a(Crash crash) {
        if (SettingsManager.getInstance().autoScreenRecordingEnabled()) {
            Uri autoScreenRecordingFileUri = C0560b.m917a().getAutoScreenRecordingFileUri();
            if (autoScreenRecordingFileUri != null) {
                Attachment attachment = new Attachment();
                attachment.setName(autoScreenRecordingFileUri.getLastPathSegment());
                attachment.setLocalPath(autoScreenRecordingFileUri.getPath());
                attachment.setType(Attachment.Type.AUTO_SCREEN_RECORDING);
                crash.m949d().add(attachment);
                crash.m939a(Crash.CrashState.WAITING_FOR_SCREEN_RECORDING_TO_BE_TRIMMED);
            }
            C0560b.m917a().clear();
        }
    }
}
