package com.instabug.bug.network;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.bug.cache.BugsCacheManager;
import com.instabug.bug.model.Bug;
import com.instabug.bug.settings.C0482a;
import com.instabug.library.broadcast.LastContactedChangedBroadcast;
import com.instabug.library.core.InstabugCore;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Observable;
import p045rx.Subscriber;

/* compiled from: BugsService.java */
/* renamed from: com.instabug.bug.network.a */
/* loaded from: classes.dex */
public class C0476a {

    /* renamed from: a */
    private static C0476a f107a;

    /* renamed from: b */
    private NetworkManager f108b = new NetworkManager();

    /* renamed from: a */
    public static C0476a m161a() {
        if (f107a == null) {
            f107a = new C0476a();
        }
        return f107a;
    }

    private C0476a() {
    }

    /* renamed from: a */
    public void m162a(final Context context, Bug bug, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1799d(this, "Reporting a bug with message: " + bug.m129d());
        Request buildRequest = this.f108b.buildRequest(context, Request.Endpoint.ReportBug, Request.RequestMethod.Post);
        ArrayList<State.StateItem> stateItems = bug.getState().getStateItems();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < stateItems.size()) {
                InstabugSDKLogger.m1799d(this, "Bug State Key: " + stateItems.get(i2).getKey() + ", Bug State value: " + stateItems.get(i2).getValue());
                buildRequest.addRequestBodyParameter(bug.getState().getStateItems().get(i2).getKey(), bug.getState().getStateItems().get(i2).getValue());
                i = i2 + 1;
            } else {
                buildRequest.addRequestBodyParameter("title", bug.m129d());
                buildRequest.addRequestBodyParameter("attachments_count", Integer.valueOf(bug.m131e().size()));
                buildRequest.addRequestBodyParameter("categories", bug.m141n());
                this.f108b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.bug.network.a.1
                    @Override // p045rx.Subscriber
                    public void onStart() {
                        InstabugSDKLogger.m1799d(this, "reportingBugRequest started");
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        InstabugSDKLogger.m1799d(this, "reportingBugRequest completed");
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable th) {
                        InstabugSDKLogger.m1801e(this, "reportingBugRequest got error: " + th.getMessage(), th);
                        callbacks.onFailed(th);
                    }

                    @Override // p045rx.Observer
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onNext(RequestResponse requestResponse) {
                        InstabugSDKLogger.m1803v(this, "reportingBugRequest onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                        try {
                            callbacks.onSucceeded(new JSONObject((String) requestResponse.getResponseBody()).getString("id"));
                        } catch (JSONException e) {
                            InstabugSDKLogger.m1801e(this, "reportingBugRequest onNext got error: " + e.getMessage(), e);
                        }
                        if (requestResponse.getResponseCode() == 200) {
                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            InstabugSDKLogger.m1799d(this, "Updating last_contacted_at to " + calendar.getTime());
                            C0482a.m236a().m238a(calendar.getTime().getTime());
                            InstabugCore.setLastContactedAt(calendar.getTime().getTime());
                            Intent intent = new Intent();
                            intent.setAction(LastContactedChangedBroadcast.LAST_CONTACTED_CHANGED);
                            intent.putExtra(LastContactedChangedBroadcast.LAST_CONTACTED_AT, calendar.getTime().getTime());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                });
                return;
            }
        }
    }

    /* renamed from: b */
    public void m163b(Context context, final Bug bug, final Request.Callbacks<Boolean, Bug> callbacks) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1799d(this, "Uploading Bug attachments");
        Observable[] observableArr = new Observable[bug.m131e().size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < observableArr.length) {
                Attachment attachment = bug.m131e().get(i2);
                Request buildRequest = this.f108b.buildRequest(context, Request.Endpoint.AddBugAttachment, Request.RequestMethod.Post, NetworkManager.RequestType.MULTI_PART);
                buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":bug_token", bug.m120a()));
                buildRequest.addParameter("metadata[file_type]", attachment.getType());
                if (attachment.getType() == Attachment.Type.AUDIO) {
                    buildRequest.addParameter("metadata[duration]", attachment.getDuration());
                }
                buildRequest.setFileToUpload(new Request.FileToUpload("file", attachment.getName(), attachment.getLocalPath(), attachment.getFileType()));
                observableArr[i2] = this.f108b.doRequest(buildRequest);
                i = i2 + 1;
            } else {
                Observable.merge(observableArr, 1).subscribe((Subscriber) new Subscriber<RequestResponse>() { // from class: com.instabug.bug.network.a.2
                    @Override // p045rx.Subscriber
                    public void onStart() {
                        InstabugSDKLogger.m1799d(this, "uploadingBugAttachmentRequest started");
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        InstabugSDKLogger.m1799d(this, "uploadingBugAttachmentRequest completed");
                        if (bug.m131e().size() == 0) {
                            callbacks.onSucceeded(true);
                        }
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable th) {
                        InstabugSDKLogger.m1801e(this, "uploadingBugAttachmentRequest got error: " + th.getMessage(), th);
                        callbacks.onFailed(bug);
                    }

                    @Override // p045rx.Observer
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onNext(RequestResponse requestResponse) {
                        InstabugSDKLogger.m1799d(this, "uploadingBugAttachmentRequest onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                        if (new File(bug.m131e().get(0).getLocalPath()).delete()) {
                            InstabugSDKLogger.m1799d(this, "uploadingBugAttachmentRequest onNext, attachment file deleted successfully");
                        }
                        bug.m131e().remove(0);
                        BugsCacheManager.addBug(bug);
                        BugsCacheManager.saveCacheToDisk();
                    }
                });
                return;
            }
        }
    }

    /* renamed from: c */
    public void m164c(Context context, final Bug bug, final Request.Callbacks<Boolean, Bug> callbacks) {
        try {
            Request buildRequest = this.f108b.buildRequest(context, Request.Endpoint.bugLogs, Request.RequestMethod.Post);
            buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":bug_token", bug.m120a()));
            Iterator<State.StateItem> it = bug.getState().getLogsItems().iterator();
            while (it.hasNext()) {
                State.StateItem next = it.next();
                buildRequest.addRequestBodyParameter(next.getKey(), next.getValue());
            }
            if (bug.m134g() != null) {
                buildRequest.addRequestBodyParameter("view_hierarchy", bug.m134g());
            }
            this.f108b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.bug.network.a.3
                @Override // p045rx.Subscriber
                public void onStart() {
                    InstabugSDKLogger.m1799d(this, "uploading bug logs started");
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(RequestResponse requestResponse) {
                    InstabugSDKLogger.m1803v(this, "uploading bug logs onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1799d(this, "uploading bug logs completed");
                    callbacks.onSucceeded(true);
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1799d(this, "uploading bug logs got error: " + th.getMessage());
                    callbacks.onFailed(bug);
                }
            });
        } catch (JSONException e) {
            InstabugSDKLogger.m1799d(this, "uploading bug logs got Json error: " + e.getMessage());
            callbacks.onFailed(bug);
        }
    }
}
