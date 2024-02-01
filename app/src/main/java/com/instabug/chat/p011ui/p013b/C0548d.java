package com.instabug.chat.p011ui.p013b;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.instabug.chat.ChatPlugin;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.cache.ReadQueueCacheManager;
import com.instabug.chat.eventbus.C0522a;
import com.instabug.chat.eventbus.ChatTriggeringEventBus;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.C0527a;
import com.instabug.chat.model.C0530d;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.network.InstabugMessageUploaderService;
import com.instabug.chat.p006a.C0510b;
import com.instabug.chat.p007b.C0512a;
import com.instabug.chat.p008c.C0513a;
import com.instabug.chat.p009d.C0519a;
import com.instabug.chat.p009d.InterfaceC0520b;
import com.instabug.chat.p011ui.p013b.C0546b;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.library.internal.storage.cache.CacheChangedListener;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import p045rx.Observer;
import p045rx.Subscription;
import p045rx.android.schedulers.AndroidSchedulers;
import p045rx.functions.Action1;
import p045rx.subjects.PublishSubject;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ChatPresenter.java */
/* renamed from: com.instabug.chat.ui.b.d */
/* loaded from: classes.dex */
public class C0548d extends BasePresenter<C0546b.b> implements InterfaceC0520b, C0546b.a, CacheChangedListener<Chat> {

    /* renamed from: a */
    private PublishSubject<String> f437a;

    /* renamed from: b */
    private Subscription f438b;

    /* renamed from: c */
    private Subscription f439c;

    /* renamed from: d */
    private Chat f440d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0548d(C0546b.b bVar) {
        super(bVar);
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public void mo812a(String str) {
        this.f440d = m844d(str);
        m847i();
        m848j();
        m845d(this.f440d);
        m854a(this.f440d);
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public void mo809a() {
        m853o();
        m849k();
        CacheManager.getInstance().subscribe(ChatsCacheManager.CHATS_MEMORY_CACHE_KEY, this);
        C0519a.m549a().m568a(this);
        m851m();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: b */
    public void mo813b() {
        CacheManager.getInstance().unSubscribe(ChatsCacheManager.CHATS_MEMORY_CACHE_KEY, this);
        m850l();
        C0519a.m549a().m569b(this);
        m852n();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public List<C0527a> mo808a(List<Message> list) {
        ArrayList arrayList = new ArrayList();
        for (Message message : list) {
            if (message.m645j() != null && message.m645j().size() > 0) {
                Iterator<Attachment> it = message.m645j().iterator();
                while (it.hasNext()) {
                    Attachment next = it.next();
                    C0527a m654a = new C0527a().m653a(message.m636c()).m657b(message.m643h()).m650a(message.m641f()).m661c(next.getUrl()).m662d(next.getLocalPath()).m654a(message.m648m());
                    InstabugSDKLogger.m1802i(this, "type" + next.getFileType());
                    String type = next.getType();
                    char c = 65535;
                    switch (type.hashCode()) {
                        case 93166550:
                            if (type.equals(Attachment.TYPE_AUDIO)) {
                                c = 1;
                                break;
                            }
                            break;
                        case 100313435:
                            if (type.equals(Attachment.TYPE_IMAGE)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 112202875:
                            if (type.equals(Attachment.TYPE_VIDEO)) {
                                c = 2;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            m654a.m652a(C0527a.b.IMAGE);
                            break;
                        case 1:
                            m654a.m652a(C0527a.b.AUDIO);
                            m654a.m651a(C0527a.a.NONE);
                            break;
                        case 2:
                            m654a.m652a(C0527a.b.VIDEO);
                            if (next.isVideoEncoded()) {
                                m654a.m658b(true);
                                break;
                            } else {
                                m654a.m658b(false);
                                break;
                            }
                    }
                    arrayList.add(m654a);
                }
            }
            if (!TextUtils.isEmpty(message.m636c())) {
                C0527a c0527a = new C0527a();
                c0527a.m653a(message.m636c()).m657b(message.m643h()).m650a(message.m641f()).m654a(message.m648m()).m652a(C0527a.b.MESSAGE);
                if (message.m646k() != null && message.m646k().size() > 0) {
                    c0527a.m656a(message.m646k());
                }
                arrayList.add(c0527a);
            } else if (!message.m648m() && message.m646k() != null && message.m646k().size() > 0) {
                C0527a c0527a2 = new C0527a();
                c0527a2.m653a(message.m636c()).m657b(message.m643h()).m650a(message.m641f()).m654a(message.m648m()).m652a(C0527a.b.MESSAGE);
                c0527a2.m656a(message.m646k());
                arrayList.add(c0527a2);
            }
        }
        return arrayList;
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public void mo811a(Message message) {
        C0546b.b bVar;
        InstabugSDKLogger.m1803v(C0548d.class, "chat id: " + message.m633b());
        this.f440d.m609a().add(message);
        InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
        if (cache != null) {
            cache.put(this.f440d.getId(), this.f440d);
        }
        if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
            bVar.getViewContext().getActivity().startService(new Intent(bVar.getViewContext().getActivity(), InstabugMessageUploaderService.class));
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public void mo810a(Attachment attachment) {
        String type = attachment.getType();
        char c = 65535;
        switch (type.hashCode()) {
            case 93166550:
                if (type.equals(Attachment.TYPE_AUDIO)) {
                    c = 1;
                    break;
                }
                break;
            case 100313435:
                if (type.equals(Attachment.TYPE_IMAGE)) {
                    c = 0;
                    break;
                }
                break;
            case 112202875:
                if (type.equals(Attachment.TYPE_VIDEO)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.view != null) {
                    C0546b.b bVar = (C0546b.b) this.view.get();
                    if (C0537a.m745j()) {
                        mo811a(mo806a(this.f440d.getId(), attachment));
                        return;
                    } else {
                        if (bVar != null) {
                            bVar.mo819a(Uri.fromFile(new File(attachment.getLocalPath())));
                            return;
                        }
                        return;
                    }
                }
                return;
            default:
                mo811a(mo806a(this.f440d.getId(), attachment));
                return;
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public Message mo807a(String str, String str2) {
        Message message = new Message();
        message.m631b(str).m634c(str2).m630b(InstabugDateFormatter.getCurrentUTCTimeStampInSeconds()).m622a(InstabugDateFormatter.getCurrentUTCTimeStampInSeconds()).m625a(Message.EnumC0526b.INBOUND).m637d(InstabugCore.getUsername()).m624a(Message.MessageState.READY_TO_BE_SENT);
        return message;
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public Message mo806a(String str, Attachment attachment) {
        Message mo807a = mo807a(str, "");
        mo807a.m623a(attachment);
        return mo807a;
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: a */
    public Attachment mo805a(Uri uri) {
        Attachment m846h = m846h();
        m846h.setType(Attachment.TYPE_IMAGE).setLocalPath(uri.getPath()).setName(uri.getLastPathSegment());
        return m846h;
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: c */
    public Chat mo814c() {
        return this.f440d;
    }

    /* renamed from: h */
    private Attachment m846h() {
        Attachment attachment = new Attachment();
        attachment.setState(Attachment.STATE_OFFLINE);
        return attachment;
    }

    /* renamed from: a */
    public void m854a(Chat chat) {
        chat.m612d();
        if (ChatsCacheManager.getCache() != null) {
            ChatsCacheManager.getCache().put(chat.getId(), chat);
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: d */
    public void mo815d() {
        C0546b.b bVar;
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null && chatPlugin.getAppContext() != null && this.f440d != null) {
            InstabugSDKLogger.m1803v(ViewOnClickListenerC0547c.class, "take extra screenshot");
            chatPlugin.setState(2);
            this.f440d.m604a(Chat.ChatState.WAITING_ATTACHMENT_MESSAGE);
            C0513a.m538a().m541a(chatPlugin.getAppContext(), this.f440d.getId());
            if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
                bVar.finishActivity();
            }
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: e */
    public void mo816e() {
        C0546b.b bVar;
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null && chatPlugin.getAppContext() != null && this.f440d != null) {
            InstabugSDKLogger.m1803v(ViewOnClickListenerC0547c.class, "start record screen");
            chatPlugin.setState(2);
            this.f440d.m604a(Chat.ChatState.WAITING_ATTACHMENT_MESSAGE);
            C0512a.m518a().m533a(this.f440d.getId());
            if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
                bVar.finishActivity();
            }
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: f */
    public void mo817f() {
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null && chatPlugin.getAppContext() != null && this.f440d != null) {
            InstabugSDKLogger.m1803v(ViewOnClickListenerC0547c.class, "pick image from gallery");
            chatPlugin.setState(2);
            this.f440d.m604a(Chat.ChatState.WAITING_ATTACHMENT_MESSAGE);
            C0546b.b bVar = (C0546b.b) this.view.get();
            if (bVar != null) {
                bVar.mo826i();
            }
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.a
    /* renamed from: g */
    public void mo818g() {
        InMemoryCache<String, Chat> cache;
        if (this.f440d != null && this.f440d.m609a().size() == 0 && this.f440d.m610b() != Chat.ChatState.WAITING_ATTACHMENT_MESSAGE && (cache = ChatsCacheManager.getCache()) != null) {
            cache.delete(this.f440d.getId());
        }
    }

    @Override // com.instabug.chat.p009d.InterfaceC0520b
    public List<Message> onNewMessagesReceived(@NonNull List<Message> list) {
        C0546b.b bVar;
        if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null && bVar.getViewContext().getActivity() != null) {
            for (Message message : list) {
                if (message.m633b().equals(this.f440d.getId())) {
                    list.remove(message);
                    C0510b.m494a().m501a((Context) bVar.getViewContext().getActivity());
                    m854a(this.f440d);
                }
            }
        }
        return list;
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: b  reason: merged with bridge method [inline-methods] */
    public void onCachedItemRemoved(Chat chat) {
        m841b(chat.getId());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: c  reason: merged with bridge method [inline-methods] */
    public void onCachedItemAdded(Chat chat) {
        m841b(chat.getId());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public void onCachedItemUpdated(Chat chat, Chat chat2) {
        m841b(chat2.getId());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    public void onCacheInvalidated() {
        InstabugSDKLogger.m1799d(this, "Chats cache was invalidated, Time: " + System.currentTimeMillis());
    }

    /* renamed from: i */
    private void m847i() {
        C0546b.b bVar;
        if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
            if (ChatsCacheManager.getValidChats().size() > 0) {
                bVar.mo822e();
            } else {
                bVar.mo821d();
            }
        }
    }

    /* renamed from: j */
    private void m848j() {
        C0546b.b bVar;
        if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
            if (C0537a.m735c()) {
                bVar.mo823f();
            } else {
                bVar.mo824g();
            }
        }
    }

    /* renamed from: b */
    private void m841b(String str) {
        if (str.equals(this.f440d.getId())) {
            this.f437a.onNext(str);
        }
    }

    /* renamed from: k */
    private void m849k() {
        if (!m840a(this.f438b)) {
            this.f437a = PublishSubject.create();
            this.f438b = this.f437a.debounce(300L, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() { // from class: com.instabug.chat.ui.b.d.1
                @Override // p045rx.Observer
                public void onCompleted() {
                }

                @Override // p045rx.Observer
                public void onError(Throwable th) {
                }

                @Override // p045rx.Observer
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onNext(String str) {
                    C0548d.this.m843c(str);
                }
            });
        }
    }

    /* renamed from: l */
    private void m850l() {
        if (m840a(this.f438b)) {
            this.f438b.unsubscribe();
        }
    }

    /* renamed from: m */
    private void m851m() {
        if (!m840a(this.f439c)) {
            this.f439c = ChatTriggeringEventBus.getInstance().subscribe(new Action1<C0522a>() { // from class: com.instabug.chat.ui.b.d.2
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(C0522a c0522a) {
                    if (C0548d.this.f440d.getId().equals(c0522a.m600a())) {
                        C0548d.this.f440d.setId(c0522a.m601b());
                    }
                }
            });
        }
    }

    /* renamed from: n */
    private void m852n() {
        if (m840a(this.f439c)) {
            this.f439c.unsubscribe();
        }
    }

    /* renamed from: a */
    private boolean m840a(Subscription subscription) {
        return (subscription == null || subscription.isUnsubscribed()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m843c(String str) {
        this.f440d = m844d(str);
        if (this.f440d != null) {
            m845d(this.f440d);
        }
    }

    /* renamed from: d */
    private Chat m844d(String str) {
        if (ChatsCacheManager.getCache() != null && ChatsCacheManager.getChat(str) != null) {
            return ChatsCacheManager.getChat(str);
        }
        return new Chat();
    }

    /* renamed from: o */
    private void m853o() {
        if (this.f440d.m610b() == Chat.ChatState.WAITING_ATTACHMENT_MESSAGE) {
            this.f440d.m604a(Chat.ChatState.READY_TO_BE_SENT);
        }
    }

    /* renamed from: b */
    private void m842b(List<Message> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            if (!list.get(size).m648m() && !list.get(size).m638d()) {
                C0530d c0530d = new C0530d();
                c0530d.m687a(list.get(size).m633b());
                c0530d.m689b(list.get(size).m629a());
                c0530d.m686a(InstabugDateFormatter.getCurrentUTCTimeStampInSeconds());
                ReadQueueCacheManager.getInstance().add(c0530d);
                return;
            }
        }
    }

    /* renamed from: d */
    private void m845d(Chat chat) {
        C0546b.b bVar;
        m842b(chat.m609a());
        Collections.sort(chat.m609a(), new Message.C0525a());
        if (this.view != null && (bVar = (C0546b.b) this.view.get()) != null) {
            bVar.mo820a(chat.m609a());
            bVar.mo825h();
        }
    }
}
