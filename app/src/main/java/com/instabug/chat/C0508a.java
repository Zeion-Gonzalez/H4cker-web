package com.instabug.chat;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.chat.cache.C0518a;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.p009d.C0521c;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.C0577R;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEvent;
import com.instabug.library.core.eventbus.coreeventbus.SDKCoreEventSubscriber;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.LocaleUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.ArrayList;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ChatPluginWrapper.java */
/* renamed from: com.instabug.chat.a */
/* loaded from: classes.dex */
public class C0508a {
    C0508a() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m447a(Context context) {
        C0537a.m724a(context);
        C0518a.m547a(context);
        C0518a.m546a();
        m454d(context);
    }

    /* renamed from: d */
    private static void m454d(Context context) {
        C0521c.m571a().m592a(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m446a() {
        m453d();
        C0518a.m548b();
    }

    /* renamed from: d */
    private static void m453d() {
        C0521c.m571a().m595d();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static long m449b() {
        return C0537a.m736d();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static ArrayList<PluginPromptOption> m450b(Context context) {
        ArrayList<PluginPromptOption> arrayList = new ArrayList<>();
        if (m459g()) {
            arrayList.add(m455e(context));
        }
        return arrayList;
    }

    /* renamed from: e */
    private static PluginPromptOption m455e(Context context) {
        PluginPromptOption pluginPromptOption = new PluginPromptOption();
        pluginPromptOption.setOrder(1);
        pluginPromptOption.setInvocationMode(4);
        pluginPromptOption.setNotificationCount(ChatsCacheManager.getUnreadCount());
        pluginPromptOption.setIcon(C0507R.drawable.instabug_ic_talk_to_us);
        pluginPromptOption.setTitle(m457f(context));
        pluginPromptOption.setOnInvocationListener(new PluginPromptOption.OnInvocationListener() { // from class: com.instabug.chat.a.1
            @Override // com.instabug.library.core.plugin.PluginPromptOption.OnInvocationListener
            public void onInvoke(Uri uri) {
                C0508a.m456e();
            }
        });
        return pluginPromptOption;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public static void m456e() {
        m458f();
        if (ChatsCacheManager.getValidChats().size() > 0) {
            InstabugChat.showChats();
        } else {
            InstabugChat.openNewChat();
        }
    }

    /* renamed from: f */
    private static void m458f() {
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null) {
            chatPlugin.setState(1);
        }
        InstabugSDKLogger.m1799d(C0508a.class, "setPluginStateForeground->change plugin state to FOREGROUND");
    }

    @NonNull
    /* renamed from: f */
    private static String m457f(Context context) {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.START_CHATS, LocaleUtils.getLocaleStringResource(Instabug.getLocale(context), C0577R.string.instabug_str_talk_to_us, context));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static Subscription m445a(Action1<SDKCoreEvent> action1) {
        return SDKCoreEventSubscriber.subscribe(action1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public static Action1<SDKCoreEvent> m451c(final Context context) {
        return new Action1<SDKCoreEvent>() { // from class: com.instabug.chat.a.2
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(SDKCoreEvent sDKCoreEvent) {
                C0511b.m509a(context, sDKCoreEvent);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public static void m448a(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /* renamed from: g */
    private static boolean m459g() {
        return InstabugCore.getFeatureState(Feature.IN_APP_MESSAGING) == Feature.State.ENABLED;
    }
}
