package com.instabug.library.network.worker.uploader;

import com.instabug.library.internal.storage.cache.SessionsCacheManager;
import com.instabug.library.model.Session;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.network.p030a.C0726d;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugSessionUploaderService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws IOException, JSONException {
        m1642a();
    }

    /* renamed from: a */
    private void m1642a() throws JSONException, IOException {
        List<Session> sessions = SessionsCacheManager.getSessions();
        InstabugSDKLogger.m1799d(this, "Found " + sessions.size() + " sessions in cache");
        for (final Session session : sessions) {
            InstabugSDKLogger.m1799d(this, "Syncing session " + session);
            C0726d.m1632a().m1633a(this, session, new Request.Callbacks<Boolean, Throwable>() { // from class: com.instabug.library.network.worker.uploader.InstabugSessionUploaderService.1
                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onSucceeded(Boolean bool) {
                    InstabugSDKLogger.m1799d(InstabugSessionUploaderService.this, "Session " + session + " synced successfully");
                    InstabugSDKLogger.m1799d(InstabugSessionUploaderService.this, "Session deleted: " + SessionsCacheManager.deleteSession(session));
                    SessionsCacheManager.saveCacheToDisk();
                }

                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onFailed(Throwable th) {
                    InstabugSDKLogger.m1799d(InstabugSessionUploaderService.this, "Something went wrong while sending session: " + session);
                }
            });
        }
    }
}
