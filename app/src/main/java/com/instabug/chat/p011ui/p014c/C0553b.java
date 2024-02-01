package com.instabug.chat.p011ui.p014c;

import android.support.v4.app.Fragment;
import com.instabug.chat.model.Chat;
import com.instabug.library.core.p024ui.BaseContract;
import java.util.ArrayList;

/* compiled from: ChatsContract.java */
/* renamed from: com.instabug.chat.ui.c.b */
/* loaded from: classes.dex */
public class C0553b {

    /* compiled from: ChatsContract.java */
    /* renamed from: com.instabug.chat.ui.c.b$a */
    /* loaded from: classes.dex */
    interface a extends BaseContract.Presenter {
        /* renamed from: a */
        void mo895a();

        /* renamed from: b */
        void mo896b();
    }

    /* compiled from: ChatsContract.java */
    /* renamed from: com.instabug.chat.ui.c.b$b */
    /* loaded from: classes.dex */
    interface b extends BaseContract.View<Fragment> {
        /* renamed from: a */
        void mo897a();

        /* renamed from: a */
        void mo898a(ArrayList<Chat> arrayList);

        /* renamed from: b */
        boolean mo899b();
    }
}
