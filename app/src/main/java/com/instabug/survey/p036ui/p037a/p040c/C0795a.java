package com.instabug.survey.p036ui.p037a.p040c;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.TextView;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p036ui.custom.NPSLayout;
import com.instabug.survey.p036ui.p037a.AbstractC0791b;
import com.instabug.survey.p036ui.p037a.InterfaceC0789a;

/* compiled from: NPSQuestionFragment.java */
/* renamed from: com.instabug.survey.ui.a.c.a */
/* loaded from: classes.dex */
public class C0795a extends AbstractC0791b implements NPSLayout.InterfaceC0805a {

    /* renamed from: d */
    private NPSLayout f1411d;

    /* renamed from: a */
    public static C0795a m2092a(C0768b c0768b, InterfaceC0789a interfaceC0789a) {
        C0795a c0795a = new C0795a();
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0768b);
        c0795a.setArguments(bundle);
        c0795a.m2057a(interfaceC0789a);
        return c0795a;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.f1377a = (C0768b) getArguments().getSerializable("survey");
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_dialog_nps_survey;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b, com.instabug.library.core.p024ui.BaseFragment
    public void initViews(View view, Bundle bundle) {
        super.initViews(view, bundle);
        this.f1379c = (TextView) view.findViewById(C0764R.id.instabug_text_view_question);
        this.f1411d = (NPSLayout) view.findViewById(C0764R.id.instabug_survey_nps_layout);
        this.f1411d.setNPSClickListener(this);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        m2095a(this.f1377a);
    }

    @VisibleForTesting
    /* renamed from: a */
    void m2095a(C0768b c0768b) {
        this.f1379c.setText(c0768b.m1934b());
        if (c0768b.m1939e() != null && c0768b.m1939e().length() > 0) {
            this.f1411d.setSelectedAnswer(c0768b.m1939e());
        }
    }

    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b
    /* renamed from: a */
    public String mo2056a() {
        return this.f1377a.m1939e();
    }

    @Override // com.instabug.survey.p036ui.custom.NPSLayout.InterfaceC0805a
    /* renamed from: a */
    public void mo2094a(int i) {
        this.f1377a.m1935b(String.valueOf(i));
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.a.c.a.1
            @Override // java.lang.Runnable
            public void run() {
                C0795a.this.f1378b.mo2049c(C0795a.this.f1377a);
            }
        }, 200L);
    }
}
