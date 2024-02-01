package com.instabug.crash.network;

import android.net.Uri;
import com.instabug.crash.cache.CrashesCacheManager;
import com.instabug.crash.models.Crash;
import com.instabug.crash.p016b.C0561a;
import com.instabug.library.internal.storage.AttachmentManager;
import com.instabug.library.internal.video.InstabugVideoUtils;
import com.instabug.library.model.Attachment;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugCrashesUploaderService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        m955a();
        m959b();
    }

    /* renamed from: a */
    private void m955a() throws IOException {
        if (SettingsManager.getInstance().autoScreenRecordingEnabled()) {
            int autoScreenRecordingMaxDuration = SettingsManager.getInstance().autoScreenRecordingMaxDuration();
            for (Crash crash : CrashesCacheManager.getCrashes()) {
                if (crash.m951f() == Crash.CrashState.WAITING_FOR_SCREEN_RECORDING_TO_BE_TRIMMED) {
                    Iterator<Attachment> it = crash.m949d().iterator();
                    while (true) {
                        if (it.hasNext()) {
                            Attachment next = it.next();
                            if (next.getType().toString().equalsIgnoreCase(Attachment.Type.AUTO_SCREEN_RECORDING.toString())) {
                                Uri fromFile = Uri.fromFile(InstabugVideoUtils.startTrim(new File(next.getLocalPath()), AttachmentManager.getAutoScreenRecordingFile(getApplicationContext()), autoScreenRecordingMaxDuration));
                                next.setName(fromFile.getLastPathSegment());
                                next.setLocalPath(fromFile.getPath());
                                InstabugSDKLogger.m1799d(this, "auto screen recording trimmed");
                                crash.m939a(Crash.CrashState.READY_TO_BE_SENT);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: b */
    private void m959b() throws IOException, JSONException {
        InstabugSDKLogger.m1799d(this, "Found " + CrashesCacheManager.getCrashes().size() + " crashes in cache");
        for (final Crash crash : CrashesCacheManager.getCrashes()) {
            if (crash.m951f().equals(Crash.CrashState.READY_TO_BE_SENT)) {
                InstabugSDKLogger.m1799d(this, "Uploading crash: " + crash.toString());
                C0571a.m969a().m970a(this, crash, new Request.Callbacks<String, Throwable>() { // from class: com.instabug.crash.network.InstabugCrashesUploaderService.1
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(String str) {
                        InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "crash uploaded successfully, setting crash TemporaryServerToken equal " + str);
                        crash.m945b(str);
                        crash.m939a(Crash.CrashState.LOGS_READY_TO_BE_UPLOADED);
                        CrashesCacheManager.saveCacheToDisk();
                        InstabugCrashesUploaderService.this.m956a(crash);
                        InstabugCrashesUploaderService.this.m962c();
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "Something went wrong while uploading crash");
                    }
                });
            } else if (crash.m951f().equals(Crash.CrashState.LOGS_READY_TO_BE_UPLOADED)) {
                InstabugSDKLogger.m1799d(this, "crash: " + crash.toString() + " already uploaded but has unsent logs, uploading now");
                m956a(crash);
            } else if (crash.m951f().equals(Crash.CrashState.ATTACHMENTS_READY_TO_BE_UPLOADED)) {
                InstabugSDKLogger.m1799d(this, "crash: " + crash.toString() + " already uploaded but has unsent attachments, uploading now");
                m960b(crash);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m956a(final Crash crash) {
        InstabugSDKLogger.m1799d(this, "START uploading all logs related to this crash id = " + crash.m944a());
        C0571a.m969a().m972c(this, crash, new Request.Callbacks<Boolean, Crash>() { // from class: com.instabug.crash.network.InstabugCrashesUploaderService.2
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "crash logs uploaded successfully, change its state");
                crash.m939a(Crash.CrashState.ATTACHMENTS_READY_TO_BE_UPLOADED);
                CrashesCacheManager.saveCacheToDisk();
                try {
                    InstabugCrashesUploaderService.this.m960b(crash);
                } catch (FileNotFoundException | JSONException e) {
                    InstabugSDKLogger.m1800e(InstabugCrashesUploaderService.this, "Something went wrong while uploading crash attachments e: " + e.getMessage());
                }
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Crash crash2) {
                InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "Something went wrong while uploading crash logs");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m960b(final Crash crash) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1799d(this, "Found " + crash.m949d().size() + " attachments related to crash: " + crash.m948c());
        C0571a.m969a().m971b(this, crash, new Request.Callbacks<Boolean, Crash>() { // from class: com.instabug.crash.network.InstabugCrashesUploaderService.3
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "Crash attachments uploaded successfully, deleting crash");
                CrashesCacheManager.deleteCrash(crash.m944a());
                CrashesCacheManager.saveCacheToDisk();
                InstabugCrashesUploaderService.this.m962c();
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Crash crash2) {
                InstabugSDKLogger.m1799d(InstabugCrashesUploaderService.this, "Something went wrong while uploading crash attachments");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m962c() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        InstabugSDKLogger.m1803v(this, "Updating last_crash_time to " + calendar.getTime());
        C0561a.m919a().m921a(calendar.getTime().getTime());
    }
}
