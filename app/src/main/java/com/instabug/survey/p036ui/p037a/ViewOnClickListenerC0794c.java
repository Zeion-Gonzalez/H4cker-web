package com.instabug.survey.p036ui.p037a;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.p031ui.custom.InstabugViewPager;
import com.instabug.library.p031ui.custom.MaterialMenuDrawable;
import com.instabug.library.util.InstabugLogoProvider;
import com.instabug.survey.C0764R;
import com.instabug.survey.network.service.InstabugSurveysSubmitterService;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p034c.C0774b;
import com.instabug.survey.p034c.C0775c;
import com.instabug.survey.p034c.C0777e;
import com.instabug.survey.p036ui.SurveyActivity;
import com.instabug.survey.p036ui.p037a.InterfaceC0796d;
import com.instabug.survey.p036ui.p037a.p038a.C0790a;
import com.instabug.survey.p036ui.p037a.p039b.C0792a;
import com.instabug.survey.p036ui.p037a.p040c.C0795a;
import com.instabug.survey.p036ui.p037a.p041d.C0797a;
import com.instabug.survey.p036ui.p037a.p042e.C0799a;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* compiled from: SurveyFragment.java */
/* renamed from: com.instabug.survey.ui.a.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0794c extends BaseFragment<C0798e> implements View.OnClickListener, InterfaceC0789a, InterfaceC0796d.b {
    @VisibleForTesting

    /* renamed from: a */
    C0769c f1397a;

    /* renamed from: b */
    private Button f1398b;

    /* renamed from: c */
    private InstabugViewPager f1399c;

    /* renamed from: d */
    private C0790a f1400d;

    /* renamed from: e */
    private ImageView f1401e;

    /* renamed from: f */
    private TextView f1402f;

    /* renamed from: g */
    private MaterialMenuDrawable f1403g;

    /* renamed from: h */
    private int f1404h = -1;

    /* renamed from: i */
    private String f1405i = "CURRENT_QUESTION_POSITION";

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_dialog_survey;
    }

    /* renamed from: a */
    public static ViewOnClickListenerC0794c m2074a(C0769c c0769c) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0769c);
        ViewOnClickListenerC0794c viewOnClickListenerC0794c = new ViewOnClickListenerC0794c();
        viewOnClickListenerC0794c.setArguments(bundle);
        return viewOnClickListenerC0794c;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.f1397a = (C0769c) getArguments().getSerializable("survey");
        this.presenter = new C0798e(this, this.f1397a);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        view.setOnKeyListener(new View.OnKeyListener() { // from class: com.instabug.survey.ui.a.c.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view2, int i, KeyEvent keyEvent) {
                return i == 4;
            }
        });
        ((C0798e) this.presenter).m2102b();
        this.f1398b = (Button) view.findViewById(C0764R.id.instabug_btn_submit);
        this.f1398b.setTextColor(Instabug.getPrimaryColor());
        this.f1398b.setOnClickListener(this);
        this.f1399c = (InstabugViewPager) findViewById(C0764R.id.instabug_survey_pager);
        this.f1399c.setSwipeable(false);
        this.f1401e = (ImageView) findViewById(C0764R.id.instabug_ic_survey_close);
        this.f1403g = new MaterialMenuDrawable(getActivity(), ContextCompat.getColor(getActivity(), C0764R.color.instabug_survey_back_icon_color), MaterialMenuDrawable.Stroke.THIN);
        this.f1401e.setImageDrawable(this.f1403g.getCurrent());
        this.f1401e.setOnClickListener(this);
        if (C0775c.m1991a()) {
            this.f1403g.setRTLEnabled(true);
        }
        this.f1403g.setIconState(MaterialMenuDrawable.IconState.X);
        this.f1402f = (TextView) findViewById(C0764R.id.instabug_question_count_indicator);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setFocusableInTouchMode(true);
        ((C0798e) this.presenter).m2100a();
        m2076a(bundle);
    }

    /* renamed from: a */
    private void m2076a(Bundle bundle) {
        if (bundle != null && bundle.getInt(this.f1405i) != -1) {
            this.f1404h = bundle.getInt(this.f1405i);
            if (this.f1397a.m1948b().get(this.f1404h).m1939e() != null && !this.f1397a.m1948b().get(this.f1404h).m1939e().isEmpty()) {
                m2086a(true);
                return;
            }
        }
        m2086a(false);
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0796d.b
    /* renamed from: a */
    public void mo2084a() {
        findViewById(C0764R.id.instabug_pbi_container).setVisibility(0);
        TextView textView = (TextView) findViewById(C0764R.id.text_view_pb);
        ImageView imageView = (ImageView) findViewById(C0764R.id.image_instabug_logo);
        imageView.setImageBitmap(InstabugLogoProvider.getInstabugLogo());
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeDark) {
            imageView.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            textView.setTextColor(ContextCompat.getColor(getActivity(), 17170443));
        } else {
            imageView.setColorFilter(ContextCompat.getColor(getActivity(), C0764R.color.instabug_survey_pbi_color), PorterDuff.Mode.SRC_ATOP);
            textView.setTextColor(ContextCompat.getColor(getActivity(), C0764R.color.instabug_survey_pbi_color));
        }
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0796d.b
    /* renamed from: b */
    public void mo2087b() {
        this.rootView.findViewById(C0764R.id.instabug_pbi_container).setVisibility(8);
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0796d.b
    /* renamed from: b */
    public void mo2088b(final C0769c c0769c) {
        this.f1400d = new C0790a(getChildFragmentManager(), m2089c(c0769c));
        this.f1399c.setAdapter(this.f1400d);
        if (c0769c.m1948b().size() > 1) {
            this.f1398b.setText(C0764R.string.instabug_str_survey_next);
            m2085a(0, c0769c.m1948b());
            this.f1399c.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.instabug.survey.ui.a.c.2
                @Override // android.support.v4.view.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i, float f, int i2) {
                }

                @Override // android.support.v4.view.ViewPager.OnPageChangeListener
                public void onPageSelected(int i) {
                    ViewOnClickListenerC0794c.this.f1404h = i;
                    ViewOnClickListenerC0794c.this.m2085a(i, c0769c.m1948b());
                    if (i == 0) {
                        ViewOnClickListenerC0794c.this.f1403g.animateIconState(MaterialMenuDrawable.IconState.X);
                        ViewOnClickListenerC0794c.this.f1398b.setText(C0764R.string.instabug_str_survey_next);
                    } else if (i == ViewOnClickListenerC0794c.this.f1400d.getCount() - 1) {
                        ViewOnClickListenerC0794c.this.f1403g.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                        ViewOnClickListenerC0794c.this.f1398b.setText(C0764R.string.instabug_str_action_submit);
                    } else {
                        ViewOnClickListenerC0794c.this.f1403g.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                        ViewOnClickListenerC0794c.this.f1398b.setText(C0764R.string.instabug_str_survey_next);
                    }
                    if (c0769c.m1948b().get(i).m1939e() != null && !c0769c.m1948b().get(i).m1939e().isEmpty()) {
                        ViewOnClickListenerC0794c.this.m2086a(true);
                    } else {
                        ViewOnClickListenerC0794c.this.m2086a(false);
                    }
                    ViewOnClickListenerC0794c.this.m2075a(i);
                }

                @Override // android.support.v4.view.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i) {
                }
            });
        } else {
            this.f1402f.setVisibility(8);
        }
        m2086a(false);
        this.f1404h = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m2075a(final int i) {
        this.f1399c.postDelayed(new Runnable() { // from class: com.instabug.survey.ui.a.c.3
            @Override // java.lang.Runnable
            public void run() {
                if (ViewOnClickListenerC0794c.this.f1397a.m1948b().get(i).m1937c() == C0768b.a.TEXT) {
                    ((C0799a) ViewOnClickListenerC0794c.this.f1400d.getItem(i)).m2106c();
                } else {
                    C0774b.m1989a(ViewOnClickListenerC0794c.this.getActivity());
                }
            }
        }, 200L);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        m2075a(this.f1399c.getCurrentItem());
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(this.f1405i, this.f1404h);
        super.onSaveInstanceState(bundle);
    }

    @VisibleForTesting
    /* renamed from: c */
    List<AbstractC0791b> m2089c(C0769c c0769c) {
        ArrayList arrayList = new ArrayList();
        Iterator<C0768b> it = c0769c.m1948b().iterator();
        while (it.hasNext()) {
            C0768b next = it.next();
            if (next.m1937c() == C0768b.a.MCQ) {
                arrayList.add(C0792a.m2060a(next, this));
            } else if (next.m1937c() == C0768b.a.TEXT) {
                arrayList.add(C0799a.m2104a(next, this));
            } else if (next.m1937c() == C0768b.a.STAR_RATE) {
                arrayList.add(C0797a.m2097a(next, this));
            } else if (next.m1937c() == C0768b.a.NPS) {
                this.f1402f.setVisibility(8);
                arrayList.add(C0795a.m2092a(next, this));
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public void m2086a(boolean z) {
        if (z) {
            this.f1398b.setEnabled(z);
            this.f1398b.setTextColor(Instabug.getPrimaryColor());
        } else {
            this.f1398b.setEnabled(z);
            this.f1398b.setTextColor(ContextCompat.getColor(getActivity(), C0764R.color.instabug_text_color_grey));
        }
    }

    @VisibleForTesting
    /* renamed from: a */
    void m2085a(int i, List<C0768b> list) {
        this.f1402f.setText(getString(C0764R.string.instabug_str_survey_counter, Integer.valueOf(i + 1), Integer.valueOf(list.size())));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == C0764R.id.instabug_btn_submit) {
            m2077a(view);
        } else if (id == C0764R.id.instabug_ic_survey_close) {
            m2081d();
        }
    }

    /* renamed from: a */
    private void m2077a(View view) {
        int currentItem = this.f1399c.getCurrentItem();
        Fragment findFragmentByTag = getChildFragmentManager().findFragmentByTag("android:switcher:" + C0764R.id.instabug_survey_pager + ":" + currentItem);
        String str = null;
        if (findFragmentByTag != null) {
            str = ((AbstractC0791b) findFragmentByTag).mo2056a();
        }
        if (str != null) {
            this.f1397a.m1948b().get(currentItem).m1935b(str);
            if (currentItem < this.f1400d.getCount() - 1) {
                this.f1399c.setCurrentItem(currentItem + 1, true);
            } else {
                C0774b.m1989a(getActivity());
                ((C0798e) this.presenter).m2101a(this.f1397a);
            }
        }
    }

    /* renamed from: d */
    private void m2081d() {
        if (m2082e()) {
            ((C0798e) this.presenter).m2103b(this.f1397a);
            ((SurveyActivity) getActivity()).finishActivity();
            C0777e.m1996c();
            return;
        }
        this.f1399c.setCurrentItem(this.f1399c.getCurrentItem() - 1);
    }

    /* renamed from: e */
    private boolean m2082e() {
        return this.f1399c.getCurrentItem() == 0;
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0796d.b
    /* renamed from: c */
    public void mo2090c() {
        getContext().startService(new Intent(getContext(), InstabugSurveysSubmitterService.class));
        ((SurveyActivity) getActivity()).m2046a();
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0789a
    /* renamed from: a */
    public void mo2047a(C0768b c0768b) {
        this.f1397a.m1948b().get(this.f1399c.getCurrentItem()).m1935b(c0768b.m1939e());
        m2083f();
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0789a
    /* renamed from: b */
    public void mo2048b(C0768b c0768b) {
        if (c0768b.m1939e() != null && c0768b.m1939e().length() > 0) {
            m2086a(true);
        } else {
            m2086a(false);
        }
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0789a
    /* renamed from: d */
    public void mo2050d(C0768b c0768b) {
        if (Integer.parseInt(c0768b.m1939e()) >= 1) {
            m2086a(true);
            this.f1397a.m1948b().get(this.f1399c.getCurrentItem()).m1935b(c0768b.m1939e());
        } else {
            m2086a(false);
        }
    }

    /* renamed from: f */
    private void m2083f() {
        if (this.f1399c.getCurrentItem() == this.f1400d.getCount() - 1) {
            m2086a(true);
        } else {
            m2077a(this.f1398b);
        }
    }

    @Override // com.instabug.survey.p036ui.p037a.InterfaceC0789a
    /* renamed from: c */
    public void mo2049c(C0768b c0768b) {
        this.f1397a.m1948b().get(this.f1399c.getCurrentItem()).m1935b(c0768b.m1939e());
        if (this.f1399c.getCurrentItem() == this.f1400d.getCount() - 1) {
            m2086a(true);
        } else {
            m2077a(this.f1398b);
        }
    }
}
