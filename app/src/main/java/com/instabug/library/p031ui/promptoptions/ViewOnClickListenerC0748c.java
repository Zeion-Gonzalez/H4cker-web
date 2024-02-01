package com.instabug.library.p031ui.promptoptions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.p031ui.promptoptions.InterfaceC0747b;
import com.instabug.library.util.InstabugLogoProvider;
import java.util.ArrayList;

/* compiled from: PromptOptionsFragment.java */
/* renamed from: com.instabug.library.ui.promptoptions.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0748c extends BaseFragment<C0749d> implements View.OnClickListener, AdapterView.OnItemClickListener, InterfaceC0747b.b {

    /* renamed from: a */
    private Uri f1240a;

    /* renamed from: b */
    private a f1241b;

    /* renamed from: c */
    private TextView f1242c;

    /* renamed from: d */
    private C0746a f1243d;

    /* renamed from: e */
    private ArrayList<PluginPromptOption> f1244e;

    /* compiled from: PromptOptionsFragment.java */
    /* renamed from: com.instabug.library.ui.promptoptions.c$a */
    /* loaded from: classes.dex */
    interface a {
        /* renamed from: a */
        void mo1760a();
    }

    /* renamed from: a */
    public static ViewOnClickListenerC0748c m1768a(Uri uri) {
        ViewOnClickListenerC0748c viewOnClickListenerC0748c = new ViewOnClickListenerC0748c();
        Bundle bundle = new Bundle();
        bundle.putParcelable("screenshotUri", uri);
        viewOnClickListenerC0748c.setArguments(bundle);
        return viewOnClickListenerC0748c;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.presenter == 0) {
            this.presenter = m1769d();
        }
        this.f1244e = C0704b.m1513c().m1530k();
        this.f1240a = (Uri) getArguments().getParcelable("screenshotUri");
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof a) {
            this.f1241b = (a) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnHomeFragmentInteractionListener");
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.f1241b = null;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((C0749d) this.presenter).m1778d();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0577R.layout.instabug_fragment_prompt_options;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        this.f1242c = (TextView) findViewById(C0577R.id.instabug_fragment_title);
        ListView listView = (ListView) findViewById(C0577R.id.instabug_prompt_options_list_view);
        listView.setOnItemClickListener(this);
        this.f1243d = new C0746a();
        listView.setAdapter((ListAdapter) this.f1243d);
        findViewById(C0577R.id.instabug_prompt_options_dialog_container).setOnClickListener(this);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        ((C0749d) this.presenter).m1775a();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        ((C0749d) this.presenter).m1776b();
    }

    /* renamed from: d */
    protected C0749d m1769d() {
        return new C0749d(this);
    }

    @Override // com.instabug.library.p031ui.promptoptions.InterfaceC0747b.b
    /* renamed from: a */
    public void mo1765a(String str) {
        this.f1242c.setText(str);
    }

    @Override // com.instabug.library.p031ui.promptoptions.InterfaceC0747b.b
    /* renamed from: a */
    public void mo1764a() {
        if (this.f1244e != null && this.f1244e.size() > 0) {
            this.f1243d.m1763a(this.f1244e);
            this.f1243d.notifyDataSetChanged();
        }
    }

    @Override // com.instabug.library.p031ui.promptoptions.InterfaceC0747b.b
    /* renamed from: b */
    public void mo1766b() {
        getActivity().findViewById(C0577R.id.instabug_pbi_footer).setVisibility(8);
        findViewById(C0577R.id.instabug_pbi_container).setVisibility(0);
        ImageView imageView = (ImageView) findViewById(C0577R.id.image_instabug_logo);
        imageView.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        imageView.setImageBitmap(InstabugLogoProvider.getInstabugLogo());
    }

    @Override // com.instabug.library.p031ui.promptoptions.InterfaceC0747b.b
    /* renamed from: c */
    public void mo1767c() {
        findViewById(C0577R.id.instabug_pbi_container).setVisibility(8);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == C0577R.id.instabug_prompt_options_dialog_container) {
            finishActivity();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ((C0749d) this.presenter).m1777c();
        this.f1244e.get(i).invoke(this.f1240a);
        if (this.f1241b != null) {
            this.f1241b.mo1760a();
        }
        finishActivity();
    }
}
