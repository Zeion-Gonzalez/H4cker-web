package com.instabug.bug.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.instabug.library.core.p024ui.BaseContract;
import com.instabug.library.model.Attachment;
import java.util.ArrayList;
import java.util.List;

/* compiled from: BugReportingFragmentContract.java */
/* renamed from: com.instabug.bug.view.d */
/* loaded from: classes.dex */
public interface InterfaceC0503d {

    /* compiled from: BugReportingFragmentContract.java */
    /* renamed from: com.instabug.bug.view.d$a */
    /* loaded from: classes.dex */
    public interface a extends BaseContract.Presenter {
        @Nullable
        /* renamed from: a */
        Attachment mo418a(ArrayList<Attachment> arrayList);

        /* renamed from: a */
        void mo419a();

        /* renamed from: a */
        void mo420a(int i, int i2, Intent intent);

        /* renamed from: a */
        void mo421a(Bundle bundle);

        /* renamed from: a */
        void mo422a(@NonNull Attachment attachment);

        /* renamed from: a */
        void mo423a(String str);

        /* renamed from: a */
        void mo424a(ArrayList<Attachment> arrayList, String str);

        /* renamed from: b */
        void mo425b();

        /* renamed from: b */
        void mo426b(String str);

        /* renamed from: c */
        String mo427c(String str);

        /* renamed from: c */
        void mo428c();

        /* renamed from: d */
        void mo429d();

        /* renamed from: e */
        void mo430e();

        /* renamed from: f */
        void mo431f();

        /* renamed from: g */
        void mo432g();
    }

    /* compiled from: BugReportingFragmentContract.java */
    /* renamed from: com.instabug.bug.view.d$b */
    /* loaded from: classes.dex */
    public interface b extends BaseContract.View<Fragment> {
        /* renamed from: a */
        void mo391a(Attachment attachment);

        /* renamed from: a */
        void mo393a(List<Attachment> list);

        /* renamed from: b */
        void mo395b(String str);

        /* renamed from: c */
        void mo398c(String str);

        /* renamed from: c */
        void mo399c(boolean z);

        /* renamed from: e */
        void mo402e();

        /* renamed from: f */
        void mo403f();

        /* renamed from: g */
        void mo404g();

        /* renamed from: h */
        void mo405h();

        /* renamed from: i */
        void mo406i();
    }
}
