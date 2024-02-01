package com.instabug.library.network.p030a;

import android.content.Context;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONException;
import p045rx.Subscriber;

/* compiled from: FeaturesService.java */
/* renamed from: com.instabug.library.network.a.a */
/* loaded from: classes.dex */
public class C0723a {

    /* renamed from: a */
    private static C0723a f1161a;

    /* renamed from: b */
    private NetworkManager f1162b = new NetworkManager();

    /* renamed from: a */
    public static C0723a m1620a() {
        if (f1161a == null) {
            f1161a = new C0723a();
        }
        return f1161a;
    }

    private C0723a() {
    }

    /* renamed from: a */
    public void m1621a(Context context, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "Getting enabled features for this application");
        this.f1162b.doRequest(this.f1162b.buildRequest(context, Request.Endpoint.AppSettings, Request.RequestMethod.Get)).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.network.a.a.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "getAppFeatures request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "getAppFeatures request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1799d(this, "getAppFeatures request got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "getAppFeatures request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                callbacks.onSucceeded((String) requestResponse.getResponseBody());
            }
        });
    }
}
