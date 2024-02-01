package com.instabug.bug.view.actionList.service;

import android.content.Context;
import com.instabug.bug.settings.C0482a;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import p045rx.Subscriber;

/* compiled from: ReportCategoriesServiceHelper.java */
/* renamed from: com.instabug.bug.view.actionList.service.a */
/* loaded from: classes.dex */
public class C0496a {

    /* renamed from: a */
    private static C0496a f212a;

    /* renamed from: b */
    private NetworkManager f213b = new NetworkManager();

    /* renamed from: a */
    public static C0496a m356a() {
        if (f212a == null) {
            f212a = new C0496a();
        }
        return f212a;
    }

    private C0496a() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static long m360b() {
        return C0482a.m236a().m270r();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public static void m361b(long j) {
        C0482a.m236a().m248b(j);
    }

    /* renamed from: a */
    public void m362a(Context context) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "Getting enabled features for this application");
        this.f213b.doRequest(this.f213b.buildRequestWithoutUUID(context, Request.Endpoint.ReportCategories, Request.RequestMethod.Get)).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.bug.view.actionList.service.a.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "getReportCategories request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "getReportCategories request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1799d(this, "getReportCategories request got error: " + th.getMessage());
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "getReportCategories request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                C0496a.m361b(System.currentTimeMillis());
                String str = (String) requestResponse.getResponseBody();
                try {
                    if (new JSONArray(str).length() == 0) {
                        C0496a.this.m359a((String) null);
                    } else {
                        C0496a.this.m359a(str);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m359a(String str) {
        C0482a.m236a().m244a(str);
    }
}
