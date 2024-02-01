package com.instabug.survey.network.service;

import android.content.Context;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.network.p035a.C0782a;
import com.instabug.survey.p032a.C0769c;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import p045rx.Subscriber;
import p045rx.schedulers.Schedulers;

/* compiled from: SurveysService.java */
/* renamed from: com.instabug.survey.network.service.a */
/* loaded from: classes.dex */
public class C0784a {

    /* renamed from: a */
    private static C0784a f1363a;

    /* renamed from: b */
    private NetworkManager f1364b = new NetworkManager();

    private C0784a() {
    }

    /* renamed from: a */
    public static C0784a m2034a() {
        if (f1363a == null) {
            f1363a = new C0784a();
        }
        return f1363a;
    }

    /* renamed from: a */
    public void m2035a(Context context, final Request.Callbacks<JSONArray, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1803v(this, "fetch surveys");
        Request buildRequest = this.f1364b.buildRequest(context, Request.Endpoint.GetSurveys, Request.RequestMethod.Get);
        buildRequest.addHeader(new Request.RequestParameter("Accept", "application/vnd.instabug.v2"));
        buildRequest.addHeader(new Request.RequestParameter("version", "2"));
        this.f1364b.doRequest(buildRequest).subscribeOn(Schedulers.newThread()).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.survey.network.service.a.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1803v(C0784a.class.getSimpleName(), "fetchingSurveysRequest started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1803v(C0784a.class.getSimpleName(), "fetchingSurveysRequest completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1801e(C0784a.class.getSimpleName(), "fetchingSurveysRequest got error: " + th.getMessage(), th);
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(C0784a.class.getSimpleName(), "fetchingSurveysRequest onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                if (requestResponse.getResponseCode() == 200) {
                    try {
                        callbacks.onSucceeded(new JSONArray((String) requestResponse.getResponseBody()));
                        return;
                    } catch (JSONException e) {
                        InstabugSDKLogger.m1801e(C0784a.class.getSimpleName(), "submittingSurveyRequest got JSONException: " + e.getMessage(), e);
                        callbacks.onFailed(e);
                        return;
                    }
                }
                callbacks.onFailed(new Throwable("Fetching Surveys got error with response code:" + requestResponse.getResponseCode()));
            }
        });
    }

    /* renamed from: a */
    public void m2036a(Context context, C0769c c0769c, final Request.Callbacks<Boolean, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1803v(this, "submitting survey");
        Request buildRequest = this.f1364b.buildRequest(context, Request.Endpoint.SubmitSurvey, Request.RequestMethod.Post);
        buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":survey_id", String.valueOf(c0769c.m1943a())));
        C0782a.m2030a(buildRequest, c0769c);
        this.f1364b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.survey.network.service.a.2
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1803v(this, "submittingSurveyRequest started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1803v(C0784a.class.getSimpleName(), "submittingSurveyRequest completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1801e(C0784a.class.getSimpleName(), "submittingSurveyRequest got error: " + th.getMessage(), th);
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(C0784a.class.getSimpleName(), "submittingSurveyRequest onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                if (requestResponse.getResponseCode() == 200) {
                    callbacks.onSucceeded(true);
                } else {
                    callbacks.onSucceeded(false);
                    callbacks.onFailed(new Throwable("submittingSurveyRequest got error with response code:" + requestResponse.getResponseCode()));
                }
            }
        });
    }
}
