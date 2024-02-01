package com.instabug.bug.view.actionList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.C0577R;
import java.util.List;

/* compiled from: ActionsListAdapter.java */
/* renamed from: com.instabug.bug.view.actionList.b */
/* loaded from: classes.dex */
public class C0492b extends BaseAdapter {

    /* renamed from: a */
    private final List<C0491a> f205a;

    public C0492b(List<C0491a> list) {
        this.f205a = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.f205a.size();
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public C0491a getItem(int i) {
        return this.f205a.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(C0577R.layout.instabug_item_actions_list, viewGroup, false);
            aVar = new a(view);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        m348a(aVar, getItem(i));
        return view;
    }

    /* renamed from: a */
    private void m348a(a aVar, C0491a c0491a) {
        aVar.f206a.setText(c0491a.m345b());
        if (c0491a.m347d()) {
            aVar.f207b.setImageResource(c0491a.m346c());
            aVar.f207b.setVisibility(0);
        } else {
            aVar.f207b.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ActionsListAdapter.java */
    /* renamed from: com.instabug.bug.view.actionList.b$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        TextView f206a;

        /* renamed from: b */
        ImageView f207b;

        a(View view) {
            this.f206a = (TextView) view.findViewById(C0577R.id.instabug_label);
            this.f207b = (ImageView) view.findViewById(C0577R.id.instabug_icon);
        }
    }

    /* renamed from: b */
    public void m350b(int i) {
        this.f205a.get(i).m344a().run();
    }
}
