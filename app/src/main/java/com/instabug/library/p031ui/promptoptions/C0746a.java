package com.instabug.library.p031ui.promptoptions;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.core.plugin.PluginPromptOption;
import com.instabug.library.util.Colorizer;
import java.util.ArrayList;

/* compiled from: PromptOptionsAdapter.java */
/* renamed from: com.instabug.library.ui.promptoptions.a */
/* loaded from: classes.dex */
public class C0746a extends BaseAdapter {

    /* renamed from: a */
    private ArrayList<PluginPromptOption> f1236a = new ArrayList<>();

    @Override // android.widget.Adapter
    public int getCount() {
        return this.f1236a.size();
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public PluginPromptOption getItem(int i) {
        return this.f1236a.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return this.f1236a.get(i).getOrder();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(C0577R.layout.instabug_list_item_prompt_option, viewGroup, false);
            aVar = new a(view);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        m1761a(aVar, getItem(i));
        return view;
    }

    /* renamed from: a */
    private void m1761a(a aVar, PluginPromptOption pluginPromptOption) {
        aVar.f1238b.setText(pluginPromptOption.getTitle());
        aVar.f1237a.setImageResource(pluginPromptOption.getIcon());
        if (pluginPromptOption.getNotificationCount() > 0) {
            aVar.f1239c.setVisibility(0);
            aVar.f1239c.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(ContextCompat.getDrawable(aVar.f1239c.getContext(), C0577R.drawable.instabug_bg_white_oval)));
            aVar.f1239c.setText(String.valueOf(pluginPromptOption.getNotificationCount()));
            return;
        }
        aVar.f1239c.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: PromptOptionsAdapter.java */
    /* renamed from: com.instabug.library.ui.promptoptions.a$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        public ImageView f1237a;

        /* renamed from: b */
        public TextView f1238b;

        /* renamed from: c */
        public TextView f1239c;

        a(View view) {
            this.f1237a = (ImageView) view.findViewById(C0577R.id.instabug_prompt_option_icon);
            this.f1238b = (TextView) view.findViewById(C0577R.id.instabug_prompt_option_title);
            this.f1239c = (TextView) view.findViewById(C0577R.id.instabug_notification_count);
        }
    }

    /* renamed from: a */
    public void m1763a(ArrayList<PluginPromptOption> arrayList) {
        this.f1236a = arrayList;
    }
}
