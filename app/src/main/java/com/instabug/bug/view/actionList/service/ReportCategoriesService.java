package com.instabug.bug.view.actionList.service;

import android.content.Context;
import android.content.Intent;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import java.io.IOException;
import org.json.JSONException;

/* loaded from: classes.dex */
public class ReportCategoriesService extends InstabugNetworkBasedBackgroundService {
    /* renamed from: a */
    public static void m355a(Context context) {
        if (System.currentTimeMillis() - C0496a.m360b() > 86400000) {
            context.startService(new Intent(context, ReportCategoriesService.class));
        }
    }

    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        m354a();
    }

    /* renamed from: a */
    private void m354a() throws IOException, JSONException {
        C0496a.m356a().m362a(this);
    }
}
