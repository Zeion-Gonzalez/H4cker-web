package com.instabug.survey.p036ui.p037a.p039b;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.library.util.OrientationUtils;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p034c.C0774b;
import com.instabug.survey.p036ui.p037a.AbstractC0791b;
import com.instabug.survey.p036ui.p037a.InterfaceC0789a;
import com.instabug.survey.p036ui.p037a.p039b.C0793b;

/* compiled from: MCQQuestionFragment.java */
/* renamed from: com.instabug.survey.ui.a.b.a */
/* loaded from: classes.dex */
public class C0792a extends AbstractC0791b implements C0793b.a {
    @VisibleForTesting

    /* renamed from: d */
    C0793b f1385d;
    @VisibleForTesting

    /* renamed from: e */
    GridView f1386e;

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_dialog_mcq_survey;
    }

    /* renamed from: a */
    public static C0792a m2060a(C0768b c0768b, InterfaceC0789a interfaceC0789a) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0768b);
        C0792a c0792a = new C0792a();
        c0792a.setArguments(bundle);
        c0792a.m2057a(interfaceC0789a);
        return c0792a;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.f1377a = (C0768b) getArguments().getSerializable("survey");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b, com.instabug.library.core.p024ui.BaseFragment
    public void initViews(View view, Bundle bundle) {
        super.initViews(view, bundle);
        this.f1379c = (TextView) view.findViewById(C0764R.id.instabug_text_view_question);
        this.f1386e = (GridView) view.findViewById(C0764R.id.instabug_survey_mcq_grid_view);
        m2058b();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setFocusableInTouchMode(true);
        m2064a(this.f1377a);
    }

    /* renamed from: a */
    public void m2064a(C0768b c0768b) {
        this.f1379c.setText(c0768b.m1934b());
        m2062b(c0768b);
        this.f1385d = new C0793b(getActivity(), c0768b, this);
        this.f1386e.setAdapter((ListAdapter) this.f1385d);
    }

    /* renamed from: b */
    private void m2062b(C0768b c0768b) {
        if (OrientationUtils.isInLandscape(getActivity())) {
            this.f1386e.setNumColumns(2);
        }
        if (c0768b.m1938d().size() % 3 == 0 || c0768b.m1938d().size() == 1) {
            this.f1386e.setNumColumns(1);
            return;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < c0768b.m1938d().size()) {
                if (c0768b.m1938d().get(i2).length() > 60) {
                    this.f1386e.setNumColumns(1);
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b
    /* renamed from: a */
    public String mo2056a() {
        C0774b.m1989a(getActivity());
        if (this.f1385d != null && this.f1385d.m2070a() != null) {
            return this.f1385d.m2070a();
        }
        Toast.makeText(getContext(), getString(C0764R.string.instabug_str_error_survey_without_answer), 0).show();
        return null;
    }

    @Override // com.instabug.survey.p036ui.p037a.p039b.C0793b.a
    /* renamed from: a */
    public void mo2063a(View view, String str) {
        this.f1377a.m1935b(str);
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.a.b.a.1
            @Override // java.lang.Runnable
            public void run() {
                C0792a.this.f1378b.mo2047a(C0792a.this.f1377a);
            }
        }, 500L);
    }
}
