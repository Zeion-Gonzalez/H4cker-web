package com.instabug.bug.network;

import com.instabug.bug.cache.BugsCacheManager;
import com.instabug.bug.model.Bug;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugBugsUploaderService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        m150a();
    }

    /* renamed from: a */
    private void m150a() throws IOException, JSONException {
        InstabugSDKLogger.m1799d(this, "Found " + BugsCacheManager.getBugs().size() + " bugs in cache");
        for (final Bug bug : BugsCacheManager.getBugs()) {
            if (bug.m132f().equals(Bug.BugState.READY_TO_BE_SENT)) {
                InstabugSDKLogger.m1799d(this, "Uploading bug: " + bug.toString());
                C0476a.m161a().m162a(this, bug, new Request.Callbacks<String, Throwable>() { // from class: com.instabug.bug.network.InstabugBugsUploaderService.1
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(String str) {
                        InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Bug uploaded successfully, setting bug TemporaryServerToken equal " + str);
                        bug.m123b(str);
                        bug.m113a(Bug.BugState.LOGS_READY_TO_BE_UPLOADED);
                        BugsCacheManager.saveCacheToDisk();
                        InstabugBugsUploaderService.this.m151a(bug);
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Something went wrong while uploading bug");
                    }
                });
            } else if (bug.m132f().equals(Bug.BugState.LOGS_READY_TO_BE_UPLOADED)) {
                InstabugSDKLogger.m1799d(this, "Bug: " + bug.toString() + " already uploaded but has unsent logs, uploading now");
                m151a(bug);
            } else if (bug.m132f().equals(Bug.BugState.ATTACHMENTS_READY_TO_BE_UPLOADED)) {
                InstabugSDKLogger.m1799d(this, "Bug: " + bug.toString() + " already uploaded but has unsent attachments, uploading now");
                m153b(bug);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m151a(final Bug bug) {
        InstabugSDKLogger.m1799d(this, "START uploading all logs related to this bug id = " + bug.getId());
        C0476a.m161a().m164c(this, bug, new Request.Callbacks<Boolean, Bug>() { // from class: com.instabug.bug.network.InstabugBugsUploaderService.2
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Bug logs uploaded successfully, change its state");
                bug.m113a(Bug.BugState.ATTACHMENTS_READY_TO_BE_UPLOADED);
                BugsCacheManager.saveCacheToDisk();
                try {
                    InstabugBugsUploaderService.this.m153b(bug);
                } catch (FileNotFoundException | JSONException e) {
                    InstabugSDKLogger.m1800e(InstabugBugsUploaderService.this, "Something went wrong while uploading bug attachments e: " + e.getMessage());
                }
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Bug bug2) {
                InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Something went wrong while uploading bug logs");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m153b(final Bug bug) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1799d(this, "Found " + bug.m131e().size() + " attachments related to bug: " + bug.m129d());
        C0476a.m161a().m163b(this, bug, new Request.Callbacks<Boolean, Bug>() { // from class: com.instabug.bug.network.InstabugBugsUploaderService.3
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Bug attachments uploaded successfully, deleting bug");
                BugsCacheManager.deleteBug(bug.getId());
                BugsCacheManager.saveCacheToDisk();
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Bug bug2) {
                InstabugSDKLogger.m1799d(InstabugBugsUploaderService.this, "Something went wrong while uploading bug attachments");
            }
        });
    }
}
