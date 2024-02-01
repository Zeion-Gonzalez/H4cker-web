package com.instabug.bug.view.p003a;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import com.instabug.bug.C0458R;
import com.instabug.bug.view.p003a.C0488a;
import com.instabug.library.C0577R;
import com.instabug.library.annotation.AnnotationLayout;
import com.instabug.library.core.p024ui.ToolbarFragment;
import java.io.Serializable;

/* compiled from: AnnotationFragment.java */
/* renamed from: com.instabug.bug.view.a.b */
/* loaded from: classes.dex */
public class C0489b extends ToolbarFragment<C0490c> implements C0488a.b {

    /* renamed from: a */
    private String f188a;

    /* renamed from: b */
    private Uri f189b;

    /* renamed from: c */
    private AnnotationLayout f190c;

    /* renamed from: d */
    private a f191d;

    /* renamed from: e */
    private int f192e;

    /* renamed from: f */
    private ImageButton f193f;

    /* compiled from: AnnotationFragment.java */
    /* renamed from: com.instabug.bug.view.a.b$a */
    /* loaded from: classes.dex */
    public interface a extends Serializable {
        /* renamed from: a */
        void mo315a(@Nullable Bitmap bitmap, Uri uri);

        /* renamed from: c */
        void mo318c();
    }

    /* renamed from: a */
    public static C0489b m342a(String str, Uri uri, int i) {
        C0489b c0489b = new C0489b();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putParcelable("image_uri", uri);
        bundle.putSerializable("type", Integer.valueOf(i));
        c0489b.setArguments(bundle);
        return c0489b;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f188a = getArguments().getString("title");
        this.f189b = (Uri) getArguments().getParcelable("image_uri");
        this.f192e = getArguments().getInt("type");
        this.presenter = new C0490c(this);
        this.f191d = (a) getActivity();
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0458R.layout.instabug_fragment_annotation;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        this.f193f = (ImageButton) view.findViewById(C0577R.id.instabug_btn_toolbar_right);
        this.f190c = (AnnotationLayout) findViewById(C0577R.id.annotationLayout);
        ((C0490c) this.presenter).m343a(this.f192e, this.f189b);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        return this.f188a;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
        this.f191d.mo315a(this.f190c.getAnnotatedBitmap(), this.f189b);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack("annotation_fragment_for_bug", 1);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
        this.f191d.mo318c();
    }

    @Override // com.instabug.bug.view.p003a.C0488a.b
    /* renamed from: a */
    public void mo339a(@DrawableRes int i) {
        this.f193f.setImageResource(i);
    }

    @Override // com.instabug.bug.view.p003a.C0488a.b
    /* renamed from: a */
    public void mo340a(Uri uri) {
        this.f190c.setBaseImage(uri, null);
    }

    @Override // com.instabug.bug.view.p003a.C0488a.b
    /* renamed from: a */
    public void mo338a() {
    }

    @Override // com.instabug.bug.view.p003a.C0488a.b
    /* renamed from: b */
    public void mo341b(int i) {
        this.f193f.setRotation(i);
    }
}
