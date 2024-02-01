package com.instabug.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.instabug.chat.model.Message;
import com.instabug.chat.network.InstabugPushNotificationTokenService;
import com.instabug.chat.p006a.C0510b;
import com.instabug.chat.p009d.C0519a;
import com.instabug.chat.p009d.InterfaceC0520b;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.core.plugin.Plugin;
import com.instabug.library.core.plugin.PluginPromptOption;
import java.util.ArrayList;
import java.util.List;
import p045rx.Subscription;

/* loaded from: classes.dex */
public class ChatPlugin extends Plugin implements InterfaceC0520b {
    private Subscription coreEventsSubscription;

    @Override // com.instabug.library.core.plugin.Plugin
    public void init(Context context) {
        super.init(context);
        C0508a.m447a(context);
        subscribeOnCoreEvents();
        C0519a.m549a().m568a(this);
        sendPushNotificationToken();
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public void release() {
        unSubscribeFromCoreEvents();
        C0508a.m446a();
        C0519a.m549a().m569b(this);
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public long getLastActivityTime() {
        return C0508a.m449b();
    }

    @Override // com.instabug.library.core.plugin.Plugin
    public ArrayList<PluginPromptOption> getPromptOptions() {
        return C0508a.m450b(this.contextWeakReference.get());
    }

    @Override // com.instabug.chat.p009d.InterfaceC0520b
    public List<Message> onNewMessagesReceived(@NonNull List<Message> list) {
        Context context;
        if (this.contextWeakReference != null && (context = this.contextWeakReference.get()) != null) {
            C0510b.m494a().m502a(context, list);
            return null;
        }
        return null;
    }

    private void subscribeOnCoreEvents() {
        this.coreEventsSubscription = C0508a.m445a(C0508a.m451c(this.contextWeakReference.get()));
    }

    private void unSubscribeFromCoreEvents() {
        C0508a.m448a(this.coreEventsSubscription);
    }

    private void sendPushNotificationToken() {
        Context context;
        if (!C0537a.m748m() && !C0537a.m747l().isEmpty() && this.contextWeakReference != null && (context = this.contextWeakReference.get()) != null) {
            context.startService(new Intent(context, InstabugPushNotificationTokenService.class));
        }
    }
}
