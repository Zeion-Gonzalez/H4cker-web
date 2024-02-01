package com.instabug.survey.p036ui.p037a.p039b;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.instabug.library.Instabug;
import com.instabug.library.util.AttrResolver;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;

/* compiled from: SurveyMCQGridAdapter.java */
/* renamed from: com.instabug.survey.ui.a.b.b */
/* loaded from: classes.dex */
public class C0793b extends BaseAdapter {

    /* renamed from: a */
    private final LayoutInflater f1388a;

    /* renamed from: b */
    private a f1389b;

    /* renamed from: c */
    private C0768b f1390c;

    /* renamed from: d */
    private int f1391d = -1;

    /* renamed from: e */
    private Context f1392e;

    /* compiled from: SurveyMCQGridAdapter.java */
    /* renamed from: com.instabug.survey.ui.a.b.b$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo2063a(View view, String str);
    }

    public C0793b(Activity activity, C0768b c0768b, a aVar) {
        this.f1392e = activity;
        this.f1388a = LayoutInflater.from(activity);
        this.f1390c = c0768b;
        m2067a(c0768b);
        this.f1389b = aVar;
    }

    /* renamed from: a */
    private void m2067a(C0768b c0768b) {
        for (int i = 0; i < c0768b.m1938d().size(); i++) {
            if (c0768b.m1939e() != null && c0768b.m1939e().equals(c0768b.m1938d().get(i))) {
                this.f1391d = i;
                return;
            }
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this.f1390c == null || this.f1390c.m1938d() == null) {
            return 0;
        }
        return this.f1390c.m1938d().size();
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public String getItem(int i) {
        return this.f1390c.m1938d().get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        b bVar;
        if (view == null) {
            bVar = new b();
            view = this.f1388a.inflate(C0764R.layout.instabug_survey_mcq_item, (ViewGroup) null);
            bVar.f1396a = (TextView) view.findViewById(C0764R.id.ib_survey_optionalAnswer_tv);
            view.setTag(bVar);
        } else {
            bVar = (b) view.getTag();
        }
        bVar.f1396a.setText(this.f1390c.m1938d().get(i));
        if (i == this.f1391d) {
            bVar.f1396a.setBackgroundColor(Instabug.getPrimaryColor());
            bVar.f1396a.setTextColor(-1);
        } else {
            bVar.f1396a.setBackgroundColor(AttrResolver.resolveAttributeColor(this.f1392e, C0764R.attr.instabug_survey_mcq_unselected_bg));
            bVar.f1396a.setTextColor(AttrResolver.resolveAttributeColor(this.f1392e, C0764R.attr.instabug_survey_mcq_text_color));
        }
        bVar.f1396a.setOnClickListener(m2065a(this.f1390c.m1938d().get(i), i));
        return view;
    }

    /* renamed from: a */
    private View.OnClickListener m2065a(final String str, final int i) {
        return new View.OnClickListener() { // from class: com.instabug.survey.ui.a.b.b.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                C0793b.this.m2069b(i);
                C0793b.this.f1389b.mo2063a(view, str);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m2069b(int i) {
        this.f1391d = i;
        notifyDataSetChanged();
    }

    /* renamed from: a */
    public String m2070a() {
        if (this.f1391d == -1) {
            return null;
        }
        return getItem(this.f1391d);
    }

    /* compiled from: SurveyMCQGridAdapter.java */
    /* renamed from: com.instabug.survey.ui.a.b.b$b */
    /* loaded from: classes.dex */
    private static class b {

        /* renamed from: a */
        TextView f1396a;

        private b() {
        }
    }
}
