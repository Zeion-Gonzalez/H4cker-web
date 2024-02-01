package com.instabug.survey.p036ui.p037a.p042e;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p034c.C0774b;
import com.instabug.survey.p036ui.p037a.AbstractC0791b;
import com.instabug.survey.p036ui.p037a.InterfaceC0789a;

/* compiled from: TextQuestionFragment.java */
/* renamed from: com.instabug.survey.ui.a.e.a */
/* loaded from: classes.dex */
public class C0799a extends AbstractC0791b implements TextWatcher {

    /* renamed from: d */
    private EditText f1417d;

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0764R.layout.instabug_dialog_text_survey;
    }

    /* renamed from: a */
    public static C0799a m2104a(C0768b c0768b, InterfaceC0789a interfaceC0789a) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("survey", c0768b);
        C0799a c0799a = new C0799a();
        c0799a.setArguments(bundle);
        c0799a.m2057a(interfaceC0789a);
        return c0799a;
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
        this.f1417d = (EditText) view.findViewById(C0764R.id.instabug_edit_text_answer);
        m2058b();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setFocusableInTouchMode(true);
        m2105a(this.f1377a);
    }

    /* renamed from: a */
    public void m2105a(C0768b c0768b) {
        this.f1379c.setText(c0768b.m1934b());
        this.f1417d.setHint(getContext().getString(C0764R.string.instabug_str_hint_enter_your_answer));
        this.f1417d.addTextChangedListener(this);
    }

    @Override // com.instabug.survey.p036ui.p037a.AbstractC0791b
    /* renamed from: a */
    public String mo2056a() {
        if (!this.f1417d.getText().toString().trim().equals("")) {
            return this.f1417d.getText().toString();
        }
        Toast.makeText(getContext(), getString(C0764R.string.instabug_str_error_survey_without_answer), 0).show();
        return null;
    }

    /* renamed from: c */
    public void m2106c() {
        if (getActivity() != null) {
            this.f1417d.requestFocus();
            C0774b.m1990a(getActivity(), this.f1417d);
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.f1417d.removeTextChangedListener(this);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        this.f1377a.m1935b(editable.toString());
        this.f1378b.mo2048b(this.f1377a);
    }
}
