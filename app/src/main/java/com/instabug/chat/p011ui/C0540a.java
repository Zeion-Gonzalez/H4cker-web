package com.instabug.chat.p011ui;

import android.content.Context;
import android.content.Intent;
import com.instabug.chat.model.Attachment;

/* compiled from: ChatActivityLauncher.java */
/* renamed from: com.instabug.chat.ui.a */
/* loaded from: classes.dex */
public class C0540a {
    /* renamed from: a */
    public static Intent m786a(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_process", 160);
        intent.addFlags(65536);
        intent.addFlags(268435456);
        return intent;
    }

    /* renamed from: a */
    public static Intent m787a(Context context, String str) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_process", 161);
        intent.putExtra("chat_number", str);
        intent.addFlags(65536);
        intent.addFlags(268435456);
        return intent;
    }

    /* renamed from: b */
    public static Intent m789b(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_process", 162);
        intent.addFlags(65536);
        intent.addFlags(268435456);
        return intent;
    }

    /* renamed from: a */
    public static Intent m788a(Context context, String str, Attachment attachment) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_process", 164);
        intent.putExtra("chat_number", str);
        intent.putExtra("attachment", attachment);
        intent.addFlags(65536);
        intent.addFlags(268435456);
        return intent;
    }
}
