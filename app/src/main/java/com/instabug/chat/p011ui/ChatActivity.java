package com.instabug.chat.p011ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import com.instabug.chat.C0507R;
import com.instabug.chat.ChatPlugin;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.p011ui.C0544b;
import com.instabug.chat.p011ui.p013b.ViewOnClickListenerC0547c;
import com.instabug.chat.p011ui.p014c.ViewOnClickListenerC0554c;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragmentActivity;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;

/* loaded from: classes.dex */
public class ChatActivity extends BaseFragmentActivity<C0544b.a> implements C0544b.b, ViewOnClickListenerC0554c.a, _InstabugActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (m778a(intent)) {
            case 161:
                mo783b(intent.getExtras().getString("chat_number"));
                return;
            default:
                return;
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OrientationUtils.handelOrientation(this);
        this.presenter = new C0551c(this);
        ((C0544b.a) this.presenter).mo795a(m778a(getIntent()));
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0507R.layout.instabug_activity;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null) {
            chatPlugin.setState(1);
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null && chatPlugin.getState() != 2) {
            chatPlugin.setState(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        OrientationUtils.unlockOrientation(this);
        super.onDestroy();
    }

    @Override // com.instabug.chat.p011ui.C0544b.b
    /* renamed from: a */
    public void mo779a() {
        getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, ViewOnClickListenerC0554c.m900c(), "chats_fragment").commit();
    }

    @Override // com.instabug.chat.p011ui.C0544b.b
    /* renamed from: a */
    public void mo780a(String str) {
        getSupportFragmentManager().executePendingTransactions();
        FragmentTransaction add = getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, ViewOnClickListenerC0547c.m827a(str), "chat_fragment");
        if (getSupportFragmentManager().findFragmentById(C0507R.id.instabug_fragment_container) != null) {
            add.addToBackStack("chat_fragment");
        }
        add.commit();
    }

    @Override // com.instabug.chat.p011ui.C0544b.b
    /* renamed from: a */
    public void mo781a(String str, Attachment attachment) {
        getSupportFragmentManager().executePendingTransactions();
        FragmentTransaction add = getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, ViewOnClickListenerC0547c.m828a(str, attachment), "chat_fragment");
        if (getSupportFragmentManager().findFragmentById(C0507R.id.instabug_fragment_container) != null) {
            add.addToBackStack("chat_fragment");
        }
        add.commit();
    }

    /* renamed from: a */
    public int m778a(Intent intent) {
        switch (intent.getExtras().getInt("chat_process")) {
            case 160:
            case 163:
            default:
                return 160;
            case 161:
                return 161;
            case 162:
                return 162;
            case 164:
                return 164;
        }
    }

    @Override // com.instabug.chat.p011ui.C0544b.b
    @Nullable
    /* renamed from: b */
    public String mo782b() {
        return getIntent().getExtras().getString("chat_number");
    }

    @Override // com.instabug.chat.p011ui.C0544b.b
    @Nullable
    /* renamed from: c */
    public Attachment mo784c() {
        return (Attachment) getIntent().getExtras().getSerializable("attachment");
    }

    @Override // com.instabug.chat.p011ui.p014c.ViewOnClickListenerC0554c.a
    /* renamed from: b */
    public void mo783b(String str) {
        InstabugSDKLogger.m1803v(ViewOnClickListenerC0554c.class, "Chat id: " + str);
        ((C0544b.a) this.presenter).mo796a(str);
    }

    @Override // com.instabug.chat.p011ui.p014c.ViewOnClickListenerC0554c.a
    /* renamed from: d */
    public void mo785d() {
        ((C0544b.a) this.presenter).mo794a();
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        ((C0544b.a) this.presenter).mo797b();
    }
}
