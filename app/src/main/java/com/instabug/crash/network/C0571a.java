package com.instabug.crash.network;

import android.content.Context;
import com.instabug.crash.cache.CrashesCacheManager;
import com.instabug.crash.models.Crash;
import com.instabug.library.model.Attachment;
import com.instabug.library.model.State;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: CrashesService.java */
/* renamed from: com.instabug.crash.network.a */
/* loaded from: classes.dex */
public class C0571a {

    /* renamed from: a */
    private static C0571a f554a;

    /* renamed from: b */
    private NetworkManager f555b = new NetworkManager();

    private C0571a() {
    }

    /* renamed from: a */
    public static C0571a m969a() {
        if (f554a == null) {
            f554a = new C0571a();
        }
        return f554a;
    }

    /* renamed from: a */
    public void m970a(Context context, Crash crash, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "Reporting crash with crash message: " + crash.m948c());
        Request buildRequest = this.f555b.buildRequest(context, Request.Endpoint.ReportCrash, Request.RequestMethod.Post);
        if (crash.m948c() != null && crash.m948c().contains("InstabugSDK-v: ")) {
            buildRequest.addRequestBodyParameter(NetworkManager.APP_TOKEN, "b1a9630002b2cbdfbfecd942744b9018");
        }
        ArrayList<State.StateItem> stateItems = crash.m950e().getStateItems();
        if (stateItems != null && stateItems.size() > 0) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= stateItems.size()) {
                    break;
                }
                InstabugSDKLogger.m1799d(this, "Crash State Key: " + stateItems.get(i2).getKey() + ", Crash State value: " + stateItems.get(i2).getValue());
                buildRequest.addRequestBodyParameter(stateItems.get(i2).getKey(), stateItems.get(i2).getValue());
                i = i2 + 1;
            }
        }
        buildRequest.addRequestBodyParameter("title", crash.m948c());
        buildRequest.addRequestBodyParameter("attachments_count", Integer.valueOf(crash.m949d().size()));
        buildRequest.addRequestBodyParameter("handled", Boolean.valueOf(crash.m952g()));
        this.f555b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.crash.network.a.1
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "reportingCrashRequest started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "reportingCrashRequest completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1799d(this, "reportingCrashRequest got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "reportingCrashRequest onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                try {
                    callbacks.onSucceeded(new JSONObject((String) requestResponse.getResponseBody()).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* renamed from: b */
    public void m971b(Context context, final Crash crash, final Request.Callbacks<Boolean, Crash> callbacks) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1799d(this, "Uploading Crash attachments");
        Observable[] observableArr = new Observable[crash.m949d().size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < observableArr.length) {
                Attachment attachment = crash.m949d().get(i2);
                Request buildRequest = this.f555b.buildRequest(context, Request.Endpoint.AddCrashAttachment, Request.RequestMethod.Post, NetworkManager.RequestType.MULTI_PART);
                buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":crash_token", crash.m946b()));
                buildRequest.addParameter("metadata[file_type]", attachment.getType());
                if (attachment.getType() == Attachment.Type.AUDIO) {
                    buildRequest.addParameter("metadata[duration]", attachment.getDuration());
                }
                buildRequest.setFileToUpload(new Request.FileToUpload("file", attachment.getName(), attachment.getLocalPath(), attachment.getFileType()));
                observableArr[i2] = this.f555b.doRequest(buildRequest);
                i = i2 + 1;
            } else {
                Observable.merge(observableArr, 1).subscribe((Subscriber) new Subscriber<RequestResponse>() { // from class: com.instabug.crash.network.a.2
                    @Override // p045rx.Subscriber
                    public void onStart() {
                        InstabugSDKLogger.m1799d(this, "uploadingCrashAttachmentRequest started");
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        InstabugSDKLogger.m1799d(this, "uploadingCrashAttachmentRequest completed");
                        if (crash.m949d().size() == 0) {
                            callbacks.onSucceeded(true);
                        }
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable th) {
                        InstabugSDKLogger.m1799d(this, "uploadingCrashAttachmentRequest got error: " + th.getMessage());
                        callbacks.onFailed(crash);
                    }

                    @Override // p045rx.Observer
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onNext(RequestResponse requestResponse) {
                        InstabugSDKLogger.m1803v(this, "uploadingCrashAttachmentRequest onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                        boolean delete = new File(crash.m949d().get(0).getLocalPath()).delete();
                        Attachment remove = crash.m949d().remove(0);
                        if (!delete) {
                            InstabugSDKLogger.m1800e(this, "Attachment: " + remove + " is not removed");
                        } else {
                            InstabugSDKLogger.m1799d(this, "Attachment: " + remove + " is removed");
                        }
                        CrashesCacheManager.addCrash(crash);
                        CrashesCacheManager.saveCacheToDisk();
                    }
                });
                return;
            }
        }
    }

    /* renamed from: c */
    public void m972c(Context context, final Crash crash, final Request.Callbacks<Boolean, Crash> callbacks) {
        try {
            Request buildRequest = this.f555b.buildRequest(context, Request.Endpoint.crashLogs, Request.RequestMethod.Post);
            buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":crash_token", crash.m946b()));
            ArrayList<State.StateItem> logsItems = crash.m950e().getLogsItems();
            if (logsItems != null && logsItems.size() > 0) {
                Iterator<State.StateItem> it = logsItems.iterator();
                while (it.hasNext()) {
                    State.StateItem next = it.next();
                    buildRequest.addRequestBodyParameter(next.getKey(), next.getValue());
                }
            }
            this.f555b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.crash.network.a.3
                @Override // p045rx.Subscriber
                public void onStart() {
                    InstabugSDKLogger.m1799d(this, "uploading crash logs started");
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(RequestResponse requestResponse) {
                    InstabugSDKLogger.m1803v(this, "uploading crash logs onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1799d(this, "uploading crash logs completed");
                    callbacks.onSucceeded(true);
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1799d(this, "uploading crash logs got error: " + th.getMessage());
                    callbacks.onFailed(crash);
                }
            });
        } catch (JSONException e) {
            InstabugSDKLogger.m1799d(this, "uploading crash logs got Json error: " + e.getMessage());
            callbacks.onFailed(crash);
        }
    }
}
