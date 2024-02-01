package com.instabug.survey.p036ui.p037a.p041d;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p034c.C0774b;
import com.instabug.survey.p036ui.p037a.AbstractC0791b;
import com.instabug.survey.p036ui.p037a.InterfaceC0789a;

/* compiled from: StarRatingQuestionFragment.java */
/* renamed from: com.instabug.survey.ui.a.d.a */
/* loaded from: classes.dex */
public class C0797a extends AbstractC0791b implements RatingBar.OnRatingBarChangeListener {

    /* renamed from: d */
    RatingBar f1413d;

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
        this.f1413d = (RatingBar) view.findViewById(C0764R.id.ib_ratingbar);
        this.f1413d.setOnRatingBarChangeListener(this);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        m2098a(this.f1377a);
    }

    /* renamed from: a */
    private void m2098a(C0768b c0768b) {
        this.f1379c.setText(c0768b.m1934b());
    }

    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b
    /* renamed from: a */
    public String mo2056a() {
        return ((int) this.f1413d.getRating()) + "";
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_star_rating_question;
    }

    /* renamed from: a */
    public static C0797a m2097a(C0768b c0768b, InterfaceC0789a interfaceC0789a) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0768b);
        C0797a c0797a = new C0797a();
        c0797a.setArguments(bundle);
        c0797a.m2057a(interfaceC0789a);
        return c0797a;
    }

    @Override // android.widget.RatingBar.OnRatingBarChangeListener
    public void onRatingChanged(RatingBar ratingBar, float f, boolean z) {
        this.f1377a.m1935b(((int) f) + "");
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.a.d.a.1
            @Override // java.lang.Runnable
            public void run() {
                C0797a.this.f1378b.mo2050d(C0797a.this.f1377a);
            }
        }, 500L);
    }

    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b, android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation == 2) {
            new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.a.d.a.2
                @Override // java.lang.Runnable
                public void run() {
                    C0774b.m1989a(C0797a.this.getActivity());
                }
            }, 500L);
        }
    }
}
