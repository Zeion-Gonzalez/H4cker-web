package com.instabug.library.analytics.network;

import com.instabug.library.analytics.AnalyticsObserver;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.analytics.util.C0583a;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugAnalyticsUploaderService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        if (System.currentTimeMillis() - AnalyticsObserver.getLastUploadedAt(this) > 86400000) {
            m1011a();
        }
    }

    /* renamed from: a */
    private void m1011a() throws IOException, JSONException {
        ArrayList<Api> m1019b = C0583a.m1019b();
        if (m1019b != null && m1019b.size() > 0) {
            C0582a.m1014a().m1015a(this, m1019b, new Request.Callbacks<Boolean, Throwable>() { // from class: com.instabug.library.analytics.network.InstabugAnalyticsUploaderService.1
                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onSucceeded(Boolean bool) {
                    AnalyticsObserver.setLastUploadedAt(System.currentTimeMillis(), InstabugAnalyticsUploaderService.this);
                    try {
                        C0583a.m1017a();
                        C0583a.m1020c();
                    } catch (JSONException e) {
                        InstabugSDKLogger.m1801e(InstabugAnalyticsUploaderService.class, e.getMessage(), e);
                    }
                }

                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onFailed(Throwable th) {
                    InstabugSDKLogger.m1801e(InstabugAnalyticsUploaderService.class, th.getMessage(), th);
                }
            });
        }
    }
}
