package com.instabug.library.network.p030a;

import android.content.Context;
import com.instabug.library.internal.device.InstabugDeviceProperties;
import com.instabug.library.model.Session;
import com.instabug.library.model.State;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.user.C0750a;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Subscriber;

/* compiled from: SessionService.java */
/* renamed from: com.instabug.library.network.a.d */
/* loaded from: classes.dex */
public class C0726d {

    /* renamed from: a */
    private static C0726d f1178a;

    /* renamed from: b */
    private NetworkManager f1179b = new NetworkManager();

    /* renamed from: a */
    public static C0726d m1632a() {
        if (f1178a == null) {
            f1178a = new C0726d();
        }
        return f1178a;
    }

    private C0726d() {
    }

    /* renamed from: a */
    public void m1633a(Context context, Session session, final Request.Callbacks<Boolean, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "Sending session");
        Request buildRequest = this.f1179b.buildRequest(context, Request.Endpoint.SendSession, Request.RequestMethod.Post);
        buildRequest.addParameter(State.KEY_DEVICE, InstabugDeviceProperties.getDeviceType()).addParameter(State.KEY_OS, "SDK Level " + Integer.toString(InstabugDeviceProperties.getCurrentOSLevel())).addParameter(State.KEY_APP_VERSION, InstabugDeviceProperties.getAppVersion(context)).addParameter(State.KEY_APP_PACKAGE_NAME, InstabugDeviceProperties.getPackageName(context)).addParameter(State.KEY_SDK_VERSION, "4.11.2").addParameter("email", C0750a.m1784b()).addParameter("name", C0750a.m1787c()).addParameter("started_at", String.valueOf(session.m1580b())).addParameter(State.KEY_DURATION, Long.valueOf(session.m1581c())).addParameter("custom_attributes", new JSONObject(session.m1582d())).addParameter(State.KEY_USER_EVENTS, new JSONArray(session.m1583e()));
        if (session.m1579a() != -1) {
            buildRequest.addParameter("session_number", Integer.valueOf(session.m1579a()));
        }
        this.f1179b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.network.a.d.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "sendSession request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "sendSession request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1799d(this, "sendSession request got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "sendSession request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                if (requestResponse.getResponseCode() == 200 && requestResponse.getResponseBody() != null) {
                    callbacks.onSucceeded(true);
                } else {
                    callbacks.onSucceeded(false);
                }
            }
        });
    }
}
