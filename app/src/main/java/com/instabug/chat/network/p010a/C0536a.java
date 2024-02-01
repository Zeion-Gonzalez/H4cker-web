package com.instabug.chat.network.p010a;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.model.State;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.schedulers.Schedulers;

/* compiled from: MessagingService.java */
/* renamed from: com.instabug.chat.network.a.a */
/* loaded from: classes.dex */
public class C0536a {

    /* renamed from: a */
    private static C0536a f401a;

    /* renamed from: b */
    private NetworkManager f402b = new NetworkManager();

    private C0536a() {
    }

    /* renamed from: a */
    public static C0536a m708a() {
        if (f401a == null) {
            f401a = new C0536a();
        }
        return f401a;
    }

    /* renamed from: a */
    public void m712a(Context context, State state, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1803v(this, "trigger chat");
        Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.TriggerChat, Request.RequestMethod.Post);
        ArrayList<State.StateItem> stateItems = state.getStateItems();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < state.getStateItems().size()) {
                InstabugSDKLogger.m1803v(this, "Chat State Key: " + stateItems.get(i2).getKey() + ", Chat State value: " + stateItems.get(i2).getValue());
                buildRequest.addRequestBodyParameter(state.getStateItems().get(i2).getKey(), state.getStateItems().get(i2).getValue());
                i = i2 + 1;
            } else {
                this.f402b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.1
                    @Override // p045rx.Subscriber
                    public void onStart() {
                        InstabugSDKLogger.m1803v(this, "triggeringChatRequest started");
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        InstabugSDKLogger.m1803v(this, "triggeringChatRequest completed");
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable th) {
                        InstabugSDKLogger.m1803v(this, "triggeringChatRequest got error: " + th.getMessage());
                        callbacks.onFailed(th);
                    }

                    @Override // p045rx.Observer
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onNext(RequestResponse requestResponse) {
                        InstabugSDKLogger.m1803v(this, "triggeringChatRequest onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                        if (requestResponse.getResponseCode() == 200) {
                            try {
                                callbacks.onSucceeded(new JSONObject((String) requestResponse.getResponseBody()).getString("chat_number"));
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        callbacks.onFailed(new Throwable("Triggering chat got error with response code:" + requestResponse.getResponseCode()));
                    }
                });
                return;
            }
        }
    }

    /* renamed from: a */
    public void m711a(Context context, Message message, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1803v(this, "Sending message");
        Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.SendMessage, Request.RequestMethod.Post);
        buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":chat_number", message.m633b()));
        buildRequest.addParameter("message", new JSONObject().put("body", message.m636c()).put("messaged_at", message.m641f()).put("email", InstabugCore.getUserEmail()).put("name", InstabugCore.getUsername()));
        this.f402b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.2
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1803v(this, "sendMessage request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1803v(this, "sendMessage request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1803v(this, "sendMessage request got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "sendMessage request onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                if (requestResponse.getResponseCode() == 200) {
                    try {
                        callbacks.onSucceeded(new JSONObject((String) requestResponse.getResponseBody()).getString("message_id"));
                        return;
                    } catch (JSONException e) {
                        InstabugSDKLogger.m1801e(this, "Sending message got error", e);
                        return;
                    }
                }
                callbacks.onFailed(new Throwable("Sending message got error with response code:" + requestResponse.getResponseCode()));
            }
        });
    }

    /* renamed from: b */
    public void m714b(Context context, final Message message, final Request.Callbacks<Boolean, Message> callbacks) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1803v(this, "Uploading message attachments, Message: " + message.m636c());
        Observable[] observableArr = new Observable[message.m645j().size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < observableArr.length) {
                Attachment attachment = message.m645j().get(i2);
                InstabugSDKLogger.m1803v(this, "Uploading attachment with type: " + attachment.getType());
                Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.AddMessageAttachment, Request.RequestMethod.Post, NetworkManager.RequestType.MULTI_PART);
                buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":chat_number", message.m633b()));
                buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":message_id", String.valueOf(message.m629a())));
                buildRequest.addParameter("metadata[file_type]", attachment.getType());
                if (attachment.getType().equals(Attachment.TYPE_AUDIO)) {
                    buildRequest.addParameter("metadata[duration]", attachment.getDuration());
                }
                buildRequest.setFileToUpload(new Request.FileToUpload("file", attachment.getName(), attachment.getLocalPath(), attachment.getFileType()));
                InstabugSDKLogger.m1803v(this, "Uploading attachment with name: " + attachment.getName() + " path: " + attachment.getLocalPath() + " file type: " + attachment.getFileType());
                observableArr[i2] = this.f402b.doRequest(buildRequest);
                i = i2 + 1;
            } else {
                Observable.merge(observableArr, 1).subscribe((Subscriber) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.3
                    @Override // p045rx.Subscriber
                    public void onStart() {
                        InstabugSDKLogger.m1803v(this, "uploadingMessageAttachmentRequest started");
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        InstabugSDKLogger.m1803v(this, "uploadingMessageAttachmentRequest completed");
                        callbacks.onSucceeded(true);
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable th) {
                        InstabugSDKLogger.m1803v(this, "uploadingMessageAttachmentRequest got error: " + th.getMessage());
                        callbacks.onFailed(message);
                    }

                    @Override // p045rx.Observer
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onNext(RequestResponse requestResponse) {
                        InstabugSDKLogger.m1803v(this, "uploadingMessageAttachmentRequest onNext, Response code: " + requestResponse.getResponseCode() + ", Response body: " + requestResponse.getResponseBody());
                    }
                });
                return;
            }
        }
    }

    /* renamed from: a */
    public void m709a(Context context, long j, int i, JSONArray jSONArray, final Request.Callbacks<RequestResponse, Throwable> callbacks) throws JSONException, IOException {
        InstabugSDKLogger.m1803v(this, "Syncing messages with server");
        Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.SyncChats, Request.RequestMethod.Post);
        if (j != 0) {
            buildRequest.addParameter("last_message_messaged_at", InstabugDateFormatter.formatUTCDate(j));
        }
        buildRequest.addParameter("messages_count", Integer.valueOf(i));
        if (jSONArray != null && jSONArray.length() != 0) {
            buildRequest.addParameter("read_messages", jSONArray);
        }
        this.f402b.doRequest(buildRequest).subscribeOn(Schedulers.m2140io()).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.4
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1803v(this, "syncMessages request started");
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1803v(this, "syncMessages request completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1803v(this, "syncMessages request got error: " + th.getMessage());
                callbacks.onFailed(th);
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "syncMessages request onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                callbacks.onSucceeded(requestResponse);
            }
        });
    }

    /* renamed from: a */
    public void m710a(Context context, final Chat chat, final Request.Callbacks<Boolean, Chat> callbacks) {
        try {
            Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.chatLogs, Request.RequestMethod.Post);
            buildRequest.setEndpoint(buildRequest.getEndpoint().replaceAll(":chat_token", chat.getId()));
            if (chat.getState() != null) {
                Iterator<State.StateItem> it = chat.getState().getLogsItems().iterator();
                while (it.hasNext()) {
                    State.StateItem next = it.next();
                    if (!next.getKey().equals(State.KEY_VISUAL_USER_STEPS)) {
                        buildRequest.addRequestBodyParameter(next.getKey(), next.getValue());
                    }
                }
            }
            this.f402b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.5
                @Override // p045rx.Subscriber
                public void onStart() {
                    InstabugSDKLogger.m1799d(this, "uploading chat logs started");
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(RequestResponse requestResponse) {
                    InstabugSDKLogger.m1803v(this, "uploading chat logs onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    InstabugSDKLogger.m1799d(this, "uploading chat logs completed");
                    callbacks.onSucceeded(true);
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                    InstabugSDKLogger.m1799d(this, "uploading chat logs got error: " + th.getMessage());
                    callbacks.onFailed(chat);
                }
            });
        } catch (JSONException e) {
            InstabugSDKLogger.m1799d(this, "uploading chat logs got Json error: " + e.getMessage());
            callbacks.onFailed(chat);
        }
    }

    /* renamed from: a */
    public void m713a(Context context, String str, final Request.Callbacks<String, Throwable> callbacks) throws JSONException, IOException {
        Request buildRequest = this.f402b.buildRequest(context, Request.Endpoint.PushToken, Request.RequestMethod.Post);
        buildRequest.addRequestBodyParameter("push_token", str);
        this.f402b.doRequest(buildRequest).subscribe((Subscriber<? super RequestResponse>) new Subscriber<RequestResponse>() { // from class: com.instabug.chat.network.a.a.6
            @Override // p045rx.Subscriber
            public void onStart() {
                InstabugSDKLogger.m1799d(this, "sending push notification token started");
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(RequestResponse requestResponse) {
                InstabugSDKLogger.m1803v(this, "sending push notification token onNext, Response code: " + requestResponse.getResponseCode() + "Response body: " + requestResponse.getResponseBody());
                if (requestResponse.getResponseCode() == 200) {
                    try {
                        callbacks.onSucceeded(new JSONObject((String) requestResponse.getResponseBody()).getString(NotificationCompat.CATEGORY_STATUS));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbacks.onFailed(e);
                        return;
                    }
                }
                callbacks.onFailed(new Throwable("sending push notification token got error with response code: " + requestResponse.getResponseCode()));
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                InstabugSDKLogger.m1799d(this, "sending push notification token completed");
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
                InstabugSDKLogger.m1799d(this, "sending push notification token got error: " + th.getMessage());
                callbacks.onFailed(th);
            }
        });
    }
}
