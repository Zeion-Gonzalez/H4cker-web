package com.instabug.chat.p011ui.p013b;

import android.net.Uri;
import android.support.v4.app.Fragment;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.C0527a;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.library.core.p024ui.BaseContract;
import java.util.List;

/* compiled from: ChatContract.java */
/* renamed from: com.instabug.chat.ui.b.b */
/* loaded from: classes.dex */
public class C0546b {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ChatContract.java */
    /* renamed from: com.instabug.chat.ui.b.b$a */
    /* loaded from: classes.dex */
    public interface a extends BaseContract.Presenter {
        /* renamed from: a */
        Attachment mo805a(Uri uri);

        /* renamed from: a */
        Message mo806a(String str, Attachment attachment);

        /* renamed from: a */
        Message mo807a(String str, String str2);

        /* renamed from: a */
        List<C0527a> mo808a(List<Message> list);

        /* renamed from: a */
        void mo809a();

        /* renamed from: a */
        void mo810a(Attachment attachment);

        /* renamed from: a */
        void mo811a(Message message);

        /* renamed from: a */
        void mo812a(String str);

        /* renamed from: b */
        void mo813b();

        /* renamed from: c */
        Chat mo814c();

        /* renamed from: d */
        void mo815d();

        /* renamed from: e */
        void mo816e();

        /* renamed from: f */
        void mo817f();

        /* renamed from: g */
        void mo818g();
    }

    /* compiled from: ChatContract.java */
    /* renamed from: com.instabug.chat.ui.b.b$b */
    /* loaded from: classes.dex */
    interface b extends BaseContract.View<Fragment> {
        /* renamed from: a */
        void mo819a(Uri uri);

        /* renamed from: a */
        void mo820a(List<Message> list);

        /* renamed from: d */
        void mo821d();

        /* renamed from: e */
        void mo822e();

        /* renamed from: f */
        void mo823f();

        /* renamed from: g */
        void mo824g();

        /* renamed from: h */
        void mo825h();

        /* renamed from: i */
        void mo826i();
    }
}
