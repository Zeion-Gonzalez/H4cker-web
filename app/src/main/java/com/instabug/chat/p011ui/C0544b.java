package com.instabug.chat.p011ui;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.instabug.chat.model.Attachment;
import com.instabug.library.core.p024ui.BaseContract;

/* compiled from: ChatContract.java */
/* renamed from: com.instabug.chat.ui.b */
/* loaded from: classes.dex */
public class C0544b {

    /* compiled from: ChatContract.java */
    /* renamed from: com.instabug.chat.ui.b$a */
    /* loaded from: classes.dex */
    interface a extends BaseContract.Presenter {
        /* renamed from: a */
        void mo794a();

        /* renamed from: a */
        void mo795a(int i);

        /* renamed from: a */
        void mo796a(String str);

        /* renamed from: b */
        void mo797b();
    }

    /* compiled from: ChatContract.java */
    /* renamed from: com.instabug.chat.ui.b$b */
    /* loaded from: classes.dex */
    interface b extends BaseContract.View<FragmentActivity> {
        /* renamed from: a */
        void mo779a();

        /* renamed from: a */
        void mo780a(String str);

        /* renamed from: a */
        void mo781a(String str, Attachment attachment);

        @Nullable
        /* renamed from: b */
        String mo782b();

        @Nullable
        /* renamed from: c */
        Attachment mo784c();
    }
}
