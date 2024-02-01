package com.instabug.chat.p009d;

import android.content.Context;
import android.os.Handler;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.cache.ReadQueueCacheManager;
import com.instabug.chat.eventbus.ChatTimeUpdatedEventBus;
import com.instabug.chat.model.C0530d;
import com.instabug.chat.network.p010a.C0536a;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.network.NetworkManager;
import com.instabug.library.network.Request;
import com.instabug.library.network.RequestResponse;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: SynchronizationManager.java */
/* renamed from: com.instabug.chat.d.c */
/* loaded from: classes.dex */
public class C0521c {

    /* renamed from: a */
    private static C0521c f321a = null;

    /* renamed from: b */
    private Handler f322b;

    /* renamed from: c */
    private a f323c;

    /* renamed from: d */
    private Subscription f324d;

    /* renamed from: e */
    private boolean f325e = false;

    /* renamed from: f */
    private boolean f326f = false;

    /* renamed from: g */
    private Action1<Long> f327g = new Action1<Long>() { // from class: com.instabug.chat.d.c.1
        @Override // p045rx.functions.Action1
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public void call(Long l) {
            if (C0521c.this.f325e) {
                InstabugSDKLogger.m1803v(this, "Waiting " + l + " seconds until the  next sync");
                C0521c.this.f322b.postDelayed(C0521c.this.f323c, l.longValue() * 1000);
            }
        }
    };

    /* renamed from: h */
    private Action1<Long> f328h = new Action1<Long>() { // from class: com.instabug.chat.d.c.2
        @Override // p045rx.functions.Action1
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public void call(Long l) {
            C0521c.this.m593b();
        }
    };

    /* renamed from: a */
    public static C0521c m571a() {
        if (f321a == null) {
            f321a = new C0521c();
        }
        return f321a;
    }

    /* renamed from: a */
    public void m592a(Context context) {
        this.f322b = new Handler();
        this.f323c = new a(context);
        m589g();
    }

    /* renamed from: b */
    public void m593b() {
        if (m591i() && !m587e()) {
            m594c();
            this.f325e = true;
            this.f322b.post(this.f323c);
        }
    }

    /* renamed from: c */
    public void m594c() {
        this.f325e = false;
        if (this.f322b != null && this.f323c != null) {
            this.f322b.removeCallbacks(this.f323c);
        }
    }

    /* renamed from: d */
    public void m595d() {
        m594c();
        m590h();
        this.f322b = null;
        this.f323c = null;
    }

    /* renamed from: e */
    private boolean m587e() {
        return this.f326f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m574a(final Context context, final Action1<Long> action1) {
        if (NetworkManager.isOnline(context) && m588f()) {
            try {
                this.f326f = true;
                final List<C0530d> all = ReadQueueCacheManager.getInstance().getAll();
                C0536a.m708a().m709a(context, ChatsCacheManager.getLastMessageMessagedAt(), ChatsCacheManager.getTotalMessagesCount(), ReadQueueCacheManager.getInstance().getReadMessagesArray(), new Request.Callbacks<RequestResponse, Throwable>() { // from class: com.instabug.chat.d.c.3
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(RequestResponse requestResponse) {
                        C0521c.this.m579a(requestResponse, context, action1);
                        C0521c.this.m580a(all);
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        C0521c.this.m581a(action1);
                    }
                });
                return;
            } catch (IOException | JSONException e) {
                m581a(action1);
                return;
            }
        }
        InstabugSDKLogger.m1804w(this, "device is offline, can't sync");
        action1.call(Long.valueOf(C0537a.m738e()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m579a(RequestResponse requestResponse, Context context, Action1<Long> action1) {
        InstabugSDKLogger.m1803v(this, "Chats synced successfully");
        this.f326f = false;
        try {
            m573a(context, m584b((String) requestResponse.getResponseBody()), requestResponse.getResponseCode() == 203);
            m572a(m570a((String) requestResponse.getResponseBody()), action1);
        } catch (Exception e) {
            InstabugSDKLogger.m1801e(this, e.getMessage(), e);
            action1.call(Long.valueOf(C0537a.m738e()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m581a(Action1<Long> action1) {
        InstabugSDKLogger.m1803v(this, "Something went wrong while sync messages");
        this.f326f = false;
        action1.call(Long.valueOf(C0537a.m738e()));
    }

    /* renamed from: a */
    private long m570a(String str) throws JSONException {
        return new JSONObject(str).getLong("ttl");
    }

    /* renamed from: a */
    private void m572a(long j, Action1<Long> action1) throws JSONException {
        InstabugSDKLogger.m1803v(this, "Next TTL: " + j);
        if (j != -1) {
            C0537a.m732b(j);
            action1.call(Long.valueOf(j));
        }
    }

    /* renamed from: b */
    private JSONArray m584b(String str) throws JSONException {
        return new JSONObject(str).getJSONArray("missing_messages");
    }

    /* renamed from: a */
    private void m573a(Context context, JSONArray jSONArray, boolean z) throws JSONException {
        if (jSONArray.length() != 0) {
            InstabugSDKLogger.m1803v(this, "new messages received: " + jSONArray.toString());
            JSONObject[] jSONObjectArr = new JSONObject[jSONArray.length()];
            for (int i = 0; i < jSONArray.length(); i++) {
                jSONObjectArr[i] = jSONArray.getJSONObject(i);
            }
            InstabugSDKLogger.m1803v(this, "messages count:" + jSONObjectArr.length);
            C0519a.m549a().m567a(context, z, jSONObjectArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m580a(List<C0530d> list) {
        ReadQueueCacheManager.getInstance().notify(list);
    }

    /* renamed from: f */
    private boolean m588f() {
        return InstabugCore.isFeaturesFetchedBefore();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: SynchronizationManager.java */
    /* renamed from: com.instabug.chat.d.c$a */
    /* loaded from: classes.dex */
    public class a implements Runnable {

        /* renamed from: a */
        WeakReference<Context> f335a;

        a(Context context) {
            this.f335a = new WeakReference<>(context);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.f335a == null || this.f335a.get() == null) {
                C0521c.this.f327g.call(Long.valueOf(C0537a.m738e()));
            } else {
                C0521c.this.m574a(this.f335a.get(), C0521c.this.f327g);
            }
        }
    }

    /* renamed from: g */
    private void m589g() {
        this.f324d = ChatTimeUpdatedEventBus.getInstance().subscribe(this.f328h);
    }

    /* renamed from: h */
    private void m590h() {
        if (this.f324d != null && !this.f324d.isUnsubscribed()) {
            this.f324d.unsubscribe();
        }
    }

    /* renamed from: i */
    private boolean m591i() {
        return InstabugCore.getFeatureState(Feature.IN_APP_MESSAGING) == Feature.State.ENABLED;
    }
}
