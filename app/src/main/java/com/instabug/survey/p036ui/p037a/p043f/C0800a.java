package com.instabug.survey.p036ui.p037a.p043f;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.instabug.library.Instabug;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.survey.C0764R;

/* compiled from: ThanksFragment.java */
/* renamed from: com.instabug.survey.ui.a.f.a */
/* loaded from: classes.dex */
public class C0800a extends BaseFragment {
    /* renamed from: a */
    public static C0800a m2107a() {
        Bundle bundle = new Bundle();
        C0800a c0800a = new C0800a();
        c0800a.setArguments(bundle);
        return c0800a;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_survey_fragment_thanks_dialog;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        ((ImageView) findViewById(C0764R.id.instabug_img_thanks)).setColorFilter(Instabug.getPrimaryColor());
    }
}
