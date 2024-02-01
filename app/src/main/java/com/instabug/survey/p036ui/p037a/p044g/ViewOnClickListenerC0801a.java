package com.instabug.survey.p036ui.p037a.p044g;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.util.InstabugLogoProvider;
import com.instabug.survey.C0764R;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p034c.C0777e;
import com.instabug.survey.p036ui.SurveyActivity;
import com.instabug.survey.p036ui.p037a.ViewOnClickListenerC0794c;
import com.instabug.survey.p036ui.p037a.p044g.InterfaceC0802b;

/* compiled from: WelcomeFragment.java */
/* renamed from: com.instabug.survey.ui.a.g.a */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0801a extends BaseFragment<C0803c> implements View.OnClickListener, InterfaceC0802b.b {

    /* renamed from: a */
    private TextView f1418a;

    /* renamed from: b */
    private Button f1419b;

    /* renamed from: c */
    private Button f1420c;

    /* renamed from: d */
    private C0769c f1421d;

    /* renamed from: a */
    public static ViewOnClickListenerC0801a m2108a(C0769c c0769c) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0769c);
        ViewOnClickListenerC0801a viewOnClickListenerC0801a = new ViewOnClickListenerC0801a();
        viewOnClickListenerC0801a.setArguments(bundle);
        return viewOnClickListenerC0801a;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f1421d = (C0769c) getArguments().getSerializable("survey");
        this.presenter = new C0803c(this);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_survey_fragment_welcome_dialog;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        this.f1418a = (TextView) findViewById(C0764R.id.ib_welcome_survey_text);
        this.f1419b = (Button) findViewById(C0764R.id.ib_welcome_survey_dismiss);
        this.f1420c = (Button) findViewById(C0764R.id.ib_welcome_survey_take_survey);
        this.f1419b.setOnClickListener(this);
        this.f1420c.setOnClickListener(this);
        this.f1420c.setBackgroundColor(Instabug.getPrimaryColor());
        this.f1420c.setTextColor(ContextCompat.getColor(getActivity(), 17170443));
        this.f1418a.setText(getString(C0764R.string.instabug_str_welcome_feedback_msg));
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((C0803c) this.presenter).m2113a();
    }

    @Override // com.instabug.survey.p036ui.p037a.p044g.InterfaceC0802b.b
    /* renamed from: a */
    public void mo2109a() {
        this.rootView.findViewById(C0764R.id.instabug_pbi_container).setVisibility(8);
    }

    @Override // com.instabug.survey.p036ui.p037a.p044g.InterfaceC0802b.b
    /* renamed from: b */
    public void mo2110b() {
        findViewById(C0764R.id.instabug_pbi_container).setVisibility(0);
        TextView textView = (TextView) findViewById(C0764R.id.text_view_pb);
        ImageView imageView = (ImageView) findViewById(C0764R.id.image_instabug_logo);
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeDark) {
            imageView.setImageBitmap(InstabugLogoProvider.getInstabugLogo());
            imageView.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            textView.setTextColor(ContextCompat.getColor(getActivity(), 17170443));
        } else {
            imageView.setImageBitmap(InstabugLogoProvider.getInstabugLogo());
            imageView.setColorFilter(ContextCompat.getColor(getActivity(), C0764R.color.instabug_survey_pbi_color), PorterDuff.Mode.SRC_ATOP);
            textView.setTextColor(ContextCompat.getColor(getActivity(), C0764R.color.instabug_survey_pbi_color));
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == C0764R.id.ib_welcome_survey_dismiss) {
            m2111c();
        } else if (id == C0764R.id.ib_welcome_survey_take_survey) {
            m2112d();
        }
    }

    /* renamed from: c */
    public void m2111c() {
        this.f1421d.m1962i();
        SurveysCacheManager.addSurvey(this.f1421d);
        SurveysCacheManager.saveCacheToDisk();
        ((SurveyActivity) getActivity()).finishActivity();
        C0777e.m1996c();
    }

    /* renamed from: d */
    public void m2112d() {
        Fragment findFragmentById = getActivity().getSupportFragmentManager().findFragmentById(C0764R.id.instabug_fragment_container);
        if (findFragmentById != null) {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(0, C0764R.anim.instabug_anim_slide_out_to_right).remove(findFragmentById).commit();
        }
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(C0764R.anim.instabug_anim_slide_from_right, 17432579).replace(C0764R.id.instabug_fragment_container, ViewOnClickListenerC0794c.m2074a(this.f1421d)).commit();
    }
}
