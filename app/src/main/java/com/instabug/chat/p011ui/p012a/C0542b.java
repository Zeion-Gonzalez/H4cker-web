package com.instabug.chat.p011ui.p012a;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.instabug.chat.C0507R;
import com.instabug.chat.p011ui.p012a.C0541a;
import com.instabug.library.C0577R;
import com.instabug.library.annotation.AnnotationLayout;
import com.instabug.library.core.p024ui.ToolbarFragment;

/* compiled from: AnnotationFragment.java */
/* renamed from: com.instabug.chat.ui.a.b */
/* loaded from: classes.dex */
public class C0542b extends ToolbarFragment<C0541a.a> implements C0541a.b {

    /* renamed from: a */
    private String f424a;

    /* renamed from: b */
    private String f425b;

    /* renamed from: c */
    private Uri f426c;

    /* renamed from: d */
    private AnnotationLayout f427d;

    /* renamed from: e */
    private a f428e;

    /* compiled from: AnnotationFragment.java */
    /* renamed from: com.instabug.chat.ui.a.b$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo792a(String str, Uri uri);

        /* renamed from: b */
        void mo793b(String str, Uri uri);
    }

    /* renamed from: a */
    public static C0542b m791a(String str, String str2, Uri uri) {
        C0542b c0542b = new C0542b();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putString("chat_id", str2);
        bundle.putParcelable("image_uri", uri);
        c0542b.setArguments(bundle);
        return c0542b;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f428e = (a) getActivity().getSupportFragmentManager().findFragmentByTag("chat_fragment");
        this.f424a = getArguments().getString("title");
        this.f425b = getArguments().getString("chat_id");
        this.f426c = (Uri) getArguments().getParcelable("image_uri");
        this.presenter = new C0543c(this);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0507R.layout.instabug_fragment_annotation;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        ((ImageButton) view.findViewById(C0577R.id.instabug_btn_toolbar_right)).setImageResource(C0577R.drawable.instabug_ic_send);
        this.f427d = (AnnotationLayout) view.findViewById(C0577R.id.annotationLayout);
        this.f427d.setBaseImage(this.f426c, null);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        return this.f424a;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
        ((C0541a.a) this.presenter).mo790a(this.f427d.getAnnotatedBitmap(), this.f426c);
        if (this.f428e != null) {
            this.f428e.mo792a(this.f425b, this.f426c);
        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack("annotation_fragment_for_chat", 1);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
        if (this.f428e != null) {
            this.f428e.mo793b(this.f425b, this.f426c);
        }
    }
}
