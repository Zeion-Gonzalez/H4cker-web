package com.instabug.chat.p007b;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.eventbus.C0522a;
import com.instabug.chat.eventbus.ChatTriggeringEventBus;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.network.InstabugMessageUploaderService;
import com.instabug.chat.p011ui.C0540a;
import com.instabug.library.core.eventbus.ScreenRecordingEventBus;
import com.instabug.library.core.eventbus.VideoProcessingServiceEventBus;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.internal.video.InternalScreenRecordHelper;
import com.instabug.library.internal.video.ScreenRecordEvent;
import com.instabug.library.internal.video.VideoProcessingService;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: ExternalScreenRecordHelper.java */
/* renamed from: com.instabug.chat.b.a */
/* loaded from: classes.dex */
public class C0512a {

    /* renamed from: a */
    private static C0512a f306a;

    /* renamed from: b */
    private String f307b;

    /* renamed from: c */
    private String f308c;

    /* renamed from: d */
    private Subscription f309d;

    /* renamed from: e */
    private Subscription f310e;

    /* renamed from: a */
    public static C0512a m518a() {
        if (f306a == null) {
            f306a = new C0512a();
        }
        return f306a;
    }

    /* renamed from: a */
    public void m533a(final String str) {
        this.f307b = str;
        InternalScreenRecordHelper.getInstance().init();
        if (this.f309d == null || this.f309d.isUnsubscribed()) {
            this.f309d = ScreenRecordingEventBus.getInstance().subscribe(new Action1<ScreenRecordEvent>() { // from class: com.instabug.chat.b.a.1
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(ScreenRecordEvent screenRecordEvent) {
                    if (screenRecordEvent.getStatus() == 1) {
                        C0512a.this.m519a(screenRecordEvent.getVideoUri());
                        C0512a.this.m532d();
                    } else if (screenRecordEvent.getStatus() == 0) {
                        C0512a.this.m525a(screenRecordEvent);
                    } else if (screenRecordEvent.getStatus() == 2) {
                        C0512a.this.m530c();
                        C0512a.this.m532d();
                    }
                }
            });
        }
        VideoProcessingServiceEventBus.getInstance().subscribe(new Action1<VideoProcessingService.Action>() { // from class: com.instabug.chat.b.a.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(VideoProcessingService.Action action) {
                if (C0512a.this.f309d != null) {
                    C0512a.this.m532d();
                }
            }
        });
        this.f310e = ChatTriggeringEventBus.getInstance().subscribe(new Action1<C0522a>() { // from class: com.instabug.chat.b.a.3
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(C0522a c0522a) {
                if (str.equalsIgnoreCase(c0522a.m600a())) {
                    C0512a.this.m528b(c0522a.m601b());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m528b(String str) {
        this.f307b = str;
    }

    /* renamed from: b */
    public boolean m534b() {
        return InternalScreenRecordHelper.getInstance().isRecording();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m525a(ScreenRecordEvent screenRecordEvent) {
        m526a(this.f307b, screenRecordEvent.getVideoUri());
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.startActivity(C0540a.m787a(currentActivity, this.f307b));
        }
    }

    /* renamed from: a */
    private void m526a(String str, Uri uri) {
        Message m625a = new Message().m631b(str).m634c("").m630b(InstabugDateFormatter.getCurrentUTCTimeStampInSeconds()).m622a(InstabugDateFormatter.getCurrentUTCTimeStampInSeconds()).m625a(Message.EnumC0526b.INBOUND);
        if (uri != null) {
            Attachment attachment = new Attachment();
            attachment.setName(uri.getLastPathSegment());
            attachment.setLocalPath(uri.getPath());
            attachment.setType(Attachment.TYPE_VIDEO);
            attachment.setState(Attachment.STATE_OFFLINE);
            attachment.setVideoEncoded(false);
            InstabugSDKLogger.m1802i(this, "Adding hanging message with ID: " + m625a.m629a());
            m531c(m625a.m629a());
            m625a.m624a(Message.MessageState.STAY_OFFLINE);
            m625a.m645j().add(attachment);
        }
        Chat chat = ChatsCacheManager.getChat(str);
        if (chat != null && chat.m609a() != null) {
            chat.m609a().add(m625a);
            InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
            if (cache != null) {
                cache.put(chat.getId(), chat);
            }
        }
    }

    /* renamed from: c */
    private void m531c(String str) {
        this.f308c = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m519a(Uri uri) {
        Chat chat = ChatsCacheManager.getChat(this.f307b);
        if (chat != null) {
            ArrayList<Message> m609a = chat.m609a();
            String str = this.f308c;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= m609a.size()) {
                    break;
                }
                Message message = m609a.get(i2);
                InstabugSDKLogger.m1799d(this, "getting message with ID: " + message.m629a());
                if (message.m629a().equals(str)) {
                    m524a(message, uri);
                    message.m624a(Message.MessageState.READY_TO_BE_SENT);
                }
                i = i2 + 1;
            }
            InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
            if (cache != null) {
                cache.put(chat.getId(), chat);
            }
            InstabugSDKLogger.m1799d(this, "video is encoded and updated in its message");
            Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
            if (currentActivity != null) {
                currentActivity.startService(new Intent(currentActivity, InstabugMessageUploaderService.class));
                return;
            }
            return;
        }
        InstabugSDKLogger.m1800e(this, "Hanging Chat is null and can't be updated");
    }

    /* renamed from: a */
    private void m524a(Message message, Uri uri) {
        for (Attachment attachment : message.m645j()) {
            if (attachment.getType().equals(Attachment.TYPE_VIDEO)) {
                InstabugSDKLogger.m1799d(this, "Setting attachment type to Video");
                attachment.setName(uri.getLastPathSegment());
                attachment.setLocalPath(uri.getPath());
                attachment.setVideoEncoded(true);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m530c() {
        Chat chat = ChatsCacheManager.getChat(this.f307b);
        if (chat != null) {
            ArrayList<Message> m609a = chat.m609a();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= m609a.size()) {
                    break;
                }
                Message message = m609a.get(i2);
                InstabugSDKLogger.m1799d(this, "getting message with ID: " + message.m629a());
                if (!message.m629a().equals(this.f308c)) {
                    i = i2 + 1;
                } else {
                    m609a.remove(message);
                    break;
                }
            }
            InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
            if (cache != null) {
                cache.put(chat.getId(), chat);
            }
            InstabugSDKLogger.m1799d(this, "video message is removed from this chat");
            return;
        }
        InstabugSDKLogger.m1800e(this, "Hanging Chat is null and can't remove video message");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m532d() {
        if (!this.f309d.isUnsubscribed()) {
            this.f309d.unsubscribe();
        }
        if (!this.f310e.isUnsubscribed()) {
            this.f310e.unsubscribe();
        }
    }
}
