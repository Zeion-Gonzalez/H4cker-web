package com.instabug.library.network.p030a;

import android.content.Context;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import org.json.JSONException;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.android.schedulers.AndroidSchedulers;
import p045rx.schedulers.Schedulers;

/* compiled from: FilesService.java */
/* renamed from: com.instabug.library.network.a.b */
/* loaded from: classes.dex */
public class C0724b {

    /* renamed from: a */
    private static C0724b f1165a;

    /* renamed from: b */
    private NetworkManager f1166b = new NetworkManager();

    /* renamed from: a */
    public static C0724b m1623a() {
        if (f1165a == null) {
            f1165a = new C0724b();
        }
        return f1165a;
    }

    private C0724b() {
    }

    /* renamed from: a */
    public Subscription m1624a(Context context, final AssetEntity assetEntity, final Request.Callbacks<AssetEntity, Throwable> callbacks) {
        Request request;
        JSONException e;
        InstabugSDKLogger.m1799d(this, "Downloading file request");
        try {
            request = this.f1166b.buildRequest(context, assetEntity.getUrl(), Request.RequestMethod.Get, NetworkManager.RequestType.FILE_DOWNLOAD);
        } catch (JSONException e2) {
            request = null;
            e = e2;
        }
        try {
            request.setDownloadedFile(assetEntity.getFile().getPath());
        } catch (JSONException e3) {
            e = e3;
            InstabugSDKLogger.m1799d(this, "create downloadFile request got error: " + e.getMessage());
            return this.f1166b.doRequest(request).subscribeOn(Schedulers.m2140io()).observeOn(AndroidSchedulers.mainThread()).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.network.a.b.1
                @Override // p045rx.Subscriber
                public void onStart() {
                    InstabugSDKLogger.m1799d(this, "downloadFile request started");
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1799d(this, "downloadFile request completed");
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1800e(this, "downloadFile request got error: " + th.getMessage());
                    callbacks.onFailed(th);
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(RequestResponse requestResponse) {
                    InstabugSDKLogger.m1803v(this, "downloadFile request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                    callbacks.onSucceeded(assetEntity);
                }
            });
        }
        return this.f1166b.doRequest(request).subscribeOn(Schedulers.m2140io()).observeOn(AndroidSchedulers.mainThread()).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.library.network.a.b.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "downloadFile request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "downloadFile request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1800e(this, "downloadFile request got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "downloadFile request onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                callbacks.onSucceeded(assetEntity);
            }
        });
    }
}
