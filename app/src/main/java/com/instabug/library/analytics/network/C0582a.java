package com.instabug.library.analytics.network;

import android.content.Context;
import com.instabug.library.analytics.model.Api;
import com.instabug.library.model.State;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import p045rx.Subscriber;

/* compiled from: AnalyticsService.java */
/* renamed from: com.instabug.library.analytics.network.a */
/* loaded from: classes.dex */
class C0582a {

    /* renamed from: a */
    private static C0582a f593a;

    /* renamed from: b */
    private NetworkManager f594b = new NetworkManager();

    /* renamed from: a */
    public static C0582a m1014a() {
        if (f593a == null) {
            f593a = new C0582a();
        }
        return f593a;
    }

    private C0582a() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m1015a(Context context, ArrayList<Api> arrayList, final Request.Callbacks<Boolean, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "starting upload SDK analytics");
        Request buildRequest = this.f594b.buildRequest(context, Request.Endpoint.Analytics, Request.RequestMethod.Post);
        buildRequest.addParameter(State.KEY_SDK_VERSION, "4.11.2");
        buildRequest.addParameter("platform", "android");
        buildRequest.addParameter("method_logs", Api.toJson(arrayList));
        this.f594b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.analytics.network.a.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1803v(this, "analyticsRequest started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1803v(this, "analyticsRequest completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "analyticsRequest onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                callbacks.onSucceeded(true);
            }
        });
    }
}
