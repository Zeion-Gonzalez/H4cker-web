package com.instabug.chat.p011ui.p014c;

import android.content.Context;
import android.support.annotation.NonNull;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.p006a.C0510b;
import com.instabug.chat.p009d.C0519a;
import com.instabug.chat.p009d.InterfaceC0520b;
import com.instabug.chat.p011ui.p014c.C0553b;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.library.internal.storage.cache.CacheChangedListener;
import com.instabug.library.internal.storage.cache.CacheManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import p045rx.Observer;
import p045rx.Subscription;
import p045rx.android.schedulers.AndroidSchedulers;
import p045rx.subjects.PublishSubject;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ChatsPresenter.java */
/* renamed from: com.instabug.chat.ui.c.d */
/* loaded from: classes.dex */
public class C0555d extends BasePresenter<C0553b.b> implements InterfaceC0520b, C0553b.a, CacheChangedListener<Chat> {

    /* renamed from: a */
    private PublishSubject<Long> f502a;

    /* renamed from: b */
    private Subscription f503b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0555d(C0553b.b bVar) {
        super(bVar);
    }

    @Override // com.instabug.chat.p011ui.p014c.C0553b.a
    /* renamed from: a */
    public void mo895a() {
        m903c();
        CacheManager.getInstance().subscribe(ChatsCacheManager.CHATS_MEMORY_CACHE_KEY, this);
        C0519a.m549a().m568a(this);
        m905e();
    }

    @Override // com.instabug.chat.p011ui.p014c.C0553b.a
    /* renamed from: b */
    public void mo896b() {
        CacheManager.getInstance().unSubscribe(ChatsCacheManager.CHATS_MEMORY_CACHE_KEY, this);
        C0519a.m549a().m569b(this);
        m904d();
    }

    @Override // com.instabug.chat.p009d.InterfaceC0520b
    public List<Message> onNewMessagesReceived(@NonNull List<Message> list) {
        C0553b.b bVar;
        if (this.view != null && (bVar = (C0553b.b) this.view.get()) != null && bVar.getViewContext().getActivity() != null) {
            if (bVar.mo899b()) {
                C0510b.m494a().m501a((Context) bVar.getViewContext().getActivity());
                return null;
            }
            C0510b.m494a().m502a((Context) bVar.getViewContext().getActivity(), list);
            return null;
        }
        return null;
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public void onCachedItemRemoved(Chat chat) {
        m901a(System.currentTimeMillis());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: b  reason: merged with bridge method [inline-methods] */
    public void onCachedItemAdded(Chat chat) {
        m901a(System.currentTimeMillis());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public void onCachedItemUpdated(Chat chat, Chat chat2) {
        m901a(System.currentTimeMillis());
    }

    @Override // com.instabug.library.internal.storage.cache.CacheChangedListener
    public void onCacheInvalidated() {
        m901a(System.currentTimeMillis());
    }

    /* renamed from: a */
    private void m901a(long j) {
        this.f502a.onNext(Long.valueOf(j));
    }

    /* renamed from: c */
    private void m903c() {
        this.f502a = PublishSubject.create();
        this.f503b = this.f502a.debounce(300L, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() { // from class: com.instabug.chat.ui.c.d.1
            @Override // p045rx.Observer
            public void onCompleted() {
            }

            @Override // p045rx.Observer
            public void onError(Throwable th) {
            }

            @Override // p045rx.Observer
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onNext(Long l) {
                C0555d.this.m905e();
            }
        });
    }

    /* renamed from: d */
    private void m904d() {
        if (this.f503b != null && !this.f503b.isUnsubscribed()) {
            this.f503b.unsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m905e() {
        C0553b.b bVar;
        ArrayList<Chat> m906f = m906f();
        Collections.sort(m906f, Collections.reverseOrder(new Chat.C0523a()));
        if (this.view != null && (bVar = (C0553b.b) this.view.get()) != null) {
            bVar.mo898a(m906f);
            bVar.mo897a();
        }
    }

    /* renamed from: f */
    private ArrayList<Chat> m906f() {
        ArrayList<Chat> arrayList;
        if (ChatsCacheManager.getCache() != null) {
            arrayList = new ArrayList<>(ChatsCacheManager.getValidChats());
        } else {
            arrayList = new ArrayList<>();
        }
        Collections.sort(arrayList, Collections.reverseOrder(new Chat.C0523a()));
        return arrayList;
    }
}
