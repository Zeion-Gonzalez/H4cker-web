package com.instabug.bug.view.p005c;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.instabug.bug.C0468d;
import com.instabug.bug.model.C0471a;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.SuccessActivity;
import com.instabug.bug.view.p005c.C0500a;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.ToolbarFragment;
import com.instabug.library.util.LocaleUtils;
import java.util.List;

/* compiled from: ExtraFieldsFragment.java */
/* renamed from: com.instabug.bug.view.c.b */
/* loaded from: classes.dex */
public class C0501b extends ToolbarFragment<C0502c> implements C0500a.b {

    /* renamed from: a */
    List<C0471a> f239a;

    /* renamed from: b */
    private String f240b;

    /* renamed from: a */
    public static C0501b m409a(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        C0501b c0501b = new C0501b();
        c0501b.setArguments(bundle);
        return c0501b;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(2);
        }
        this.f240b = getArguments().getString("title");
        this.presenter = new C0502c(this);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0577R.layout.instabug_lyt_extra_fields;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        this.toolbarImageButtonClose.setImageDrawable(ContextCompat.getDrawable(getContext(), C0577R.drawable.instabug_ic_back));
        if (LocaleUtils.isRTL(Instabug.getLocale(getContext()))) {
            this.toolbarImageButtonClose.setRotation(180.0f);
        }
        this.toolbarImageButtonDone.setImageDrawable(ContextCompat.getDrawable(getContext(), C0577R.drawable.instabug_ic_send));
        this.toolbarImageButtonDone.setColorFilter(new PorterDuffColorFilter(InstabugCore.getPrimaryColor(), PorterDuff.Mode.SRC_IN));
        m410b();
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        return this.f240b;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
        if (((C0502c) this.presenter).m417b()) {
            ((C0502c) this.presenter).m416a(this.f239a);
            C0468d.m86a().m102c(getContext());
            m411a();
        }
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
    }

    /* renamed from: b */
    private void m410b() {
        CharSequence m147c;
        this.f239a = ((C0502c) this.presenter).m415a();
        LinearLayout linearLayout = (LinearLayout) findViewById(C0577R.id.linearLayout);
        for (final int i = 0; i < this.f239a.size(); i++) {
            EditText editText = (EditText) LayoutInflater.from(getContext()).inflate(C0577R.layout.instabug_lyt_extra_field, (ViewGroup) linearLayout, false);
            editText.setId(i);
            if (this.f239a.get(i).m149e()) {
                m147c = String.valueOf(((Object) this.f239a.get(i).m147c()) + " *");
            } else {
                m147c = this.f239a.get(i).m147c();
            }
            editText.setHint(m147c);
            editText.setText(this.f239a.get(i).m146b());
            editText.addTextChangedListener(new a() { // from class: com.instabug.bug.view.c.b.1
                @Override // com.instabug.bug.view.p005c.C0501b.a, android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    C0501b.this.f239a.get(i).m145a(editable.toString());
                }
            });
            linearLayout.addView(editText);
        }
    }

    /* renamed from: a */
    public void m411a() {
        if (C0482a.m236a().m265m()) {
            finishActivity();
            getActivity().startActivity(SuccessActivity.m324a(getContext()));
        } else {
            finishActivity();
        }
    }

    @Override // com.instabug.bug.view.p005c.C0500a.b
    /* renamed from: a */
    public void mo408a(int i) {
        String string = getString(C0577R.string.instabug_err_invalid_extra_field, this.f239a.get(i).m147c());
        EditText editText = (EditText) findViewById(i);
        editText.requestFocus();
        editText.setError(string);
    }

    /* compiled from: ExtraFieldsFragment.java */
    /* renamed from: com.instabug.bug.view.c.b$a */
    /* loaded from: classes.dex */
    public static class a implements TextWatcher {
        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }
    }
}
