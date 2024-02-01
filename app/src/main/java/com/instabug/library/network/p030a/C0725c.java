package com.instabug.library.network.p030a;

import android.content.Context;
import com.instabug.library.network.C0729d;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.schedulers.Schedulers;

/* compiled from: MigrateUUIDService.java */
/* renamed from: com.instabug.library.network.a.c */
/* loaded from: classes.dex */
public class C0725c {

    /* renamed from: a */
    private static C0725c f1170a;

    /* renamed from: b */
    private NetworkManager f1171b = new NetworkManager();

    /* renamed from: a */
    public static C0725c m1626a() {
        if (f1170a == null) {
            f1170a = new C0725c();
        }
        return f1170a;
    }

    private C0725c() {
    }

    /* renamed from: a */
    public void m1627a(Context context, String str, String str2, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        Request buildRequest = this.f1171b.buildRequest(context, Request.Endpoint.MigrateUUID, Request.RequestMethod.put);
        buildRequest.addRequestBodyParameter("old_uuid", str);
        buildRequest.addRequestBodyParameter("new_uuid", str2);
        buildRequest.addRequestBodyParameter(NetworkManager.APP_TOKEN, SettingsManager.getInstance().getAppToken());
        this.f1171b.doRequest(buildRequest).subscribeOn(Schedulers.newThread()).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() { // from class: com.instabug.library.network.a.c.2
            @Override // p045rx.functions.Func1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<?> call(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, 15), new Func2<Throwable, Integer, Integer>() { // from class: com.instabug.library.network.a.c.2.2
                    @Override // p045rx.functions.Func2
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public Integer call(Throwable th, Integer num) {
                        callbacks.onFailed(th);
                        return num;
                    }
                }).flatMap(new Func1<Integer, Observable<?>>() { // from class: com.instabug.library.network.a.c.2.1
                    @Override // p045rx.functions.Func1
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public Observable<?> call(Integer num) {
                        return SettingsManager.getInstance().shouldMakeUUIDMigrationRequest() ? Observable.timer((long) Math.pow(2.718281828459045d, num.intValue()), TimeUnit.SECONDS) : Observable.error(new C0729d());
                    }
                });
            }
        }).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.network.a.c.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "migrateUUID request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "migrateUUID request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1801e(this, "migrateUUID request got error: " + th.getMessage(), th);
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "migrateUUID request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                callbacks.onSucceeded((String) requestResponse.getResponseBody());
            }
        });
    }
}
