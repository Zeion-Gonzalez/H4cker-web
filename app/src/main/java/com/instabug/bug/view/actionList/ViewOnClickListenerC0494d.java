package com.instabug.bug.view.actionList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.instabug.bug.BugPlugin;
import com.instabug.bug.C0464c;
import com.instabug.bug.view.actionList.C0493c;
import com.instabug.library.C0577R;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.util.InstabugLogoProvider;
import java.util.ArrayList;

/* compiled from: ActionsListFragment.java */
/* renamed from: com.instabug.bug.view.actionList.d */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0494d extends BaseFragment<C0495e> implements View.OnClickListener, C0493c.a {

    /* renamed from: a */
    private ArrayList<C0491a> f208a;

    /* renamed from: b */
    private String f209b;

    /* renamed from: a */
    public static ViewOnClickListenerC0494d m351a(String str, ArrayList<C0491a> arrayList) {
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putSerializable("actionsList", arrayList);
        ViewOnClickListenerC0494d viewOnClickListenerC0494d = new ViewOnClickListenerC0494d();
        viewOnClickListenerC0494d.setArguments(bundle);
        return viewOnClickListenerC0494d;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        if (this.presenter == 0) {
            this.presenter = m353a();
        }
        if (getArguments() != null) {
            this.f209b = getArguments().getString("title");
            this.f208a = (ArrayList) getArguments().getSerializable("actionsList");
            getArguments().remove("actionsList");
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0577R.layout.instabug_lyt_actions_list;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        m352a(this.f209b);
        ListView listView = (ListView) findViewById(C0577R.id.instabug_actions_list);
        final C0492b c0492b = new C0492b(this.f208a);
        listView.setAdapter((ListAdapter) c0492b);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.instabug.bug.view.actionList.d.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                c0492b.m350b(i);
                ViewOnClickListenerC0494d.this.getActivity().getSupportFragmentManager().beginTransaction().remove(ViewOnClickListenerC0494d.this).commit();
            }
        });
        findViewById(C0577R.id.instabug_container).setOnClickListener(this);
        findViewById(C0577R.id.instabug_fragment_title).setOnClickListener(this);
        if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.ENABLED) {
            findViewById(C0577R.id.instabug_pbi_container).setVisibility(8);
        } else {
            findViewById(C0577R.id.instabug_pbi_container).setVisibility(0);
            ((ImageView) findViewById(C0577R.id.image_instabug_logo)).setImageBitmap(InstabugLogoProvider.getInstabugLogo());
        }
    }

    /* renamed from: a */
    protected C0495e m353a() {
        return new C0495e(this);
    }

    /* renamed from: a */
    private void m352a(String str) {
        ((TextView) findViewById(C0577R.id.instabug_fragment_title)).setText(str);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == C0577R.id.instabug_container || id == C0577R.id.instabug_fragment_title) {
            getActivity().finish();
            C0464c.m72a();
            BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
            if (bugPlugin != null && bugPlugin.getState() != 2) {
                bugPlugin.setState(0);
            }
        }
    }
}
