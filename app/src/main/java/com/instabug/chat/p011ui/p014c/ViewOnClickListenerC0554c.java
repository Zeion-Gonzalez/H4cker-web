package com.instabug.chat.p011ui.p014c;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.instabug.chat.C0507R;
import com.instabug.chat.model.Chat;
import com.instabug.chat.p011ui.p014c.C0553b;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.p024ui.ToolbarFragment;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.ArrayList;

/* compiled from: ChatsFragment.java */
/* renamed from: com.instabug.chat.ui.c.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0554c extends ToolbarFragment<C0553b.a> implements View.OnClickListener, AdapterView.OnItemClickListener, C0553b.b {

    /* renamed from: a */
    private C0552a f499a;

    /* renamed from: b */
    private ArrayList<Chat> f500b;

    /* renamed from: c */
    private a f501c;

    /* compiled from: ChatsFragment.java */
    /* renamed from: com.instabug.chat.ui.c.c$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: b */
        void mo783b(String str);

        /* renamed from: d */
        void mo785d();
    }

    /* renamed from: c */
    public static ViewOnClickListenerC0554c m900c() {
        ViewOnClickListenerC0554c viewOnClickListenerC0554c = new ViewOnClickListenerC0554c();
        viewOnClickListenerC0554c.setArguments(new Bundle());
        return viewOnClickListenerC0554c;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f501c = (a) getActivity();
        this.presenter = new C0555d(this);
        this.f500b = new ArrayList<>();
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0507R.layout.instabug_fragment_chats;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        view.findViewById(C0507R.id.instabug_btn_toolbar_right).setVisibility(8);
        ListView listView = (ListView) view.findViewById(C0507R.id.instabug_lst_chats);
        listView.setOnItemClickListener(this);
        this.f499a = new C0552a(getActivity(), this.f500b);
        listView.setAdapter((ListAdapter) this.f499a);
        ImageButton imageButton = (ImageButton) view.findViewById(C0507R.id.instabug_btn_new_chat);
        imageButton.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(ContextCompat.getDrawable(getContext(), C0507R.drawable.instabug_bg_white_oval)));
        imageButton.setOnClickListener(this);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.CONVERSATIONS_LIST_TITLE, getString(C0507R.string.instabug_str_conversations));
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        ((C0553b.a) this.presenter).mo895a();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        ((C0553b.a) this.presenter).mo896b();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        InstabugSDKLogger.m1803v(ViewOnClickListenerC0554c.class, "Chat id: " + ((Chat) adapterView.getItemAtPosition(i)).getId());
        if (this.f501c != null) {
            this.f501c.mo783b(((Chat) adapterView.getItemAtPosition(i)).getId());
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == C0507R.id.instabug_btn_new_chat && this.f501c != null) {
            this.f501c.mo785d();
        }
    }

    @Override // com.instabug.chat.p011ui.p014c.C0553b.b
    /* renamed from: a */
    public void mo898a(ArrayList<Chat> arrayList) {
        this.f500b = arrayList;
    }

    @Override // com.instabug.chat.p011ui.p014c.C0553b.b
    /* renamed from: a */
    public void mo897a() {
        this.f499a.m888a(this.f500b);
        this.f499a.notifyDataSetChanged();
    }

    @Override // com.instabug.chat.p011ui.p014c.C0553b.b
    /* renamed from: b */
    public boolean mo899b() {
        return getFragmentManager().findFragmentById(C0507R.id.instabug_fragment_container) instanceof ViewOnClickListenerC0554c;
    }
}
