package com.instabug.chat.p011ui.p013b;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.instabug.chat.C0507R;
import com.instabug.chat.ChatPlugin;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Message;
import com.instabug.chat.p007b.C0512a;
import com.instabug.chat.p011ui.p012a.C0542b;
import com.instabug.chat.p011ui.p013b.C0546b;
import com.instabug.chat.p011ui.p013b.C0550f;
import com.instabug.chat.p011ui.p013b.ViewOnClickListenerC0545a;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.ToolbarFragment;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.video.VideoPlayerFragment;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PermissionsUtils;
import com.instabug.library.util.PlaceHolderUtils;
import com.instabug.library.util.SystemServiceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* compiled from: ChatFragment.java */
/* renamed from: com.instabug.chat.ui.b.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0547c extends ToolbarFragment<C0546b.a> implements View.OnClickListener, C0542b.a, ViewOnClickListenerC0545a.a, C0546b.b, C0550f.a {

    /* renamed from: a */
    private String f432a;

    /* renamed from: b */
    private C0550f f433b;

    /* renamed from: c */
    private EditText f434c;

    /* renamed from: a */
    public static ViewOnClickListenerC0547c m827a(String str) {
        ViewOnClickListenerC0547c viewOnClickListenerC0547c = new ViewOnClickListenerC0547c();
        Bundle bundle = new Bundle();
        bundle.putString("chat_number", str);
        viewOnClickListenerC0547c.setArguments(bundle);
        return viewOnClickListenerC0547c;
    }

    /* renamed from: a */
    public static ViewOnClickListenerC0547c m828a(String str, Attachment attachment) {
        ViewOnClickListenerC0547c viewOnClickListenerC0547c = new ViewOnClickListenerC0547c();
        Bundle bundle = new Bundle();
        bundle.putString("chat_number", str);
        bundle.putSerializable("attachment", attachment);
        viewOnClickListenerC0547c.setArguments(bundle);
        return viewOnClickListenerC0547c;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f432a = getArguments().getString("chat_number");
        this.presenter = new C0548d(this);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0507R.layout.instabug_fragment_chat;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        view.findViewById(C0507R.id.instabug_btn_toolbar_right).setVisibility(8);
        ListView listView = (ListView) view.findViewById(C0507R.id.instabug_lst_messages);
        this.f434c = (EditText) view.findViewById(C0507R.id.instabug_edit_text_new_message);
        this.f434c.setHint(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.CONVERSATION_TEXT_FIELD_HINT, getString(C0507R.string.instabug_str_sending_message_hint)));
        ImageView imageView = (ImageView) view.findViewById(C0507R.id.instabug_btn_send);
        imageView.setImageDrawable(Colorizer.getPrimaryColorTintedDrawable(ContextCompat.getDrawable(getContext(), C0507R.drawable.instabug_ic_send)));
        imageView.setOnClickListener(this);
        this.f433b = new C0550f(new ArrayList(), getActivity(), listView, this);
        listView.setAdapter((ListAdapter) this.f433b);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        return ChatsCacheManager.getChat(this.f432a) != null ? ChatsCacheManager.getChat(this.f432a).m608a(getContext()) : getString(C0507R.string.instabug_str_empty);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
        SystemServiceUtils.hideInputMethod(getActivity());
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((C0546b.a) this.presenter).mo812a(this.f432a);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        ((C0546b.a) this.presenter).mo809a();
        Attachment attachment = (Attachment) getArguments().getSerializable("attachment");
        if (attachment != null) {
            ((C0546b.a) this.presenter).mo810a(attachment);
        }
        if (getArguments() != null) {
            getArguments().clear();
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        ((C0546b.a) this.presenter).mo813b();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ((C0546b.a) this.presenter).mo818g();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == C0507R.id.instabug_btn_send) {
            String obj = this.f434c.getText().toString();
            if (!TextUtils.isEmpty(obj.trim())) {
                ((C0546b.a) this.presenter).mo811a(((C0546b.a) this.presenter).mo807a(((C0546b.a) this.presenter).mo814c().getId(), obj));
                this.f434c.setText("");
                return;
            }
            return;
        }
        if (view.getId() == C0507R.id.instabug_btn_attach) {
            SystemServiceUtils.hideInputMethod(getActivity());
            m830k();
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0550f.a
    /* renamed from: b */
    public void mo834b(String str) {
        SystemServiceUtils.hideInputMethod(getActivity());
        getActivity().getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, VideoPlayerFragment.newInstance(str), VideoPlayerFragment.TAG).addToBackStack(VideoPlayerFragment.TAG).commit();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0550f.a
    /* renamed from: c */
    public void mo835c(String str) {
        SystemServiceUtils.hideInputMethod(getActivity());
        getActivity().getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, C0549e.m861a(str), "image_attachment_viewer_fragment").addToBackStack("image_attachment_viewer_fragment").commit();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0550f.a
    /* renamed from: d */
    public void mo836d(String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(str));
            startActivity(intent);
        } catch (Exception e) {
            InstabugSDKLogger.m1800e(this, "Unable to view this url " + str + "\nError message: " + e.getMessage());
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.ViewOnClickListenerC0545a.a
    /* renamed from: a */
    public void mo802a() {
        ((C0546b.a) this.presenter).mo815d();
    }

    @Override // com.instabug.chat.p011ui.p013b.ViewOnClickListenerC0545a.a
    /* renamed from: b */
    public void mo803b() {
        m831l();
    }

    @Override // com.instabug.chat.p011ui.p013b.ViewOnClickListenerC0545a.a
    /* renamed from: c */
    public void mo804c() {
        m837j();
    }

    @Override // com.instabug.chat.p011ui.p012a.C0542b.a
    /* renamed from: a */
    public void mo792a(String str, Uri uri) {
        if (str != null && str.equals(((C0546b.a) this.presenter).mo814c().getId())) {
            ((C0546b.a) this.presenter).mo811a(((C0546b.a) this.presenter).mo806a(((C0546b.a) this.presenter).mo814c().getId(), ((C0546b.a) this.presenter).mo805a(uri)));
        }
    }

    @Override // com.instabug.chat.p011ui.p012a.C0542b.a
    /* renamed from: b */
    public void mo793b(String str, Uri uri) {
    }

    @Override // android.support.v4.app.Fragment
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (iArr.length > 0 && iArr[0] == 0) {
            switch (i) {
                case 162:
                    ((C0546b.a) this.presenter).mo817f();
                    return;
                case 163:
                    ((C0546b.a) this.presenter).mo816e();
                    return;
                default:
                    super.onRequestPermissionsResult(i, strArr, iArr);
                    return;
            }
        }
        if (i == 163) {
            ((C0546b.a) this.presenter).mo816e();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        switch (i) {
            case 161:
                if (i2 == -1 && intent != null) {
                    String galleryImagePath = AttachmentsUtility.getGalleryImagePath(getActivity(), intent.getData());
                    if (galleryImagePath == null) {
                        galleryImagePath = intent.getData().getPath();
                    }
                    if (galleryImagePath != null) {
                        Uri newFileAttachmentUri = AttachmentsUtility.getNewFileAttachmentUri(getActivity(), Uri.fromFile(new File(galleryImagePath)));
                        InstabugSDKLogger.m1799d(this, "onActivityResult");
                        ((C0546b.a) this.presenter).mo809a();
                        ((C0546b.a) this.presenter).mo810a(((C0546b.a) this.presenter).mo805a(newFileAttachmentUri));
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: d */
    public void mo821d() {
        ((ImageButton) this.rootView.findViewById(C0507R.id.instabug_btn_toolbar_left)).setImageResource(C0507R.drawable.instabug_ic_close);
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: e */
    public void mo822e() {
        ((ImageButton) this.rootView.findViewById(C0507R.id.instabug_btn_toolbar_left)).setImageResource(C0507R.drawable.instabug_ic_back);
        if (Build.VERSION.SDK_INT >= 11) {
            this.rootView.findViewById(C0507R.id.instabug_btn_toolbar_left).setRotation(getResources().getInteger(C0507R.integer.instabug_icon_lang_rotation));
        }
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: f */
    public void mo823f() {
        ImageView imageView = (ImageView) this.rootView.findViewById(C0507R.id.instabug_btn_attach);
        Colorizer.applyPrimaryColorTint(imageView);
        imageView.setOnClickListener(this);
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: g */
    public void mo824g() {
        this.rootView.findViewById(C0507R.id.instabug_btn_attach).setVisibility(8);
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: a */
    public void mo820a(List<Message> list) {
        this.f433b.m880a(((C0546b.a) this.presenter).mo808a(list));
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: h */
    public void mo825h() {
        this.f433b.notifyDataSetChanged();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: a */
    public void mo819a(Uri uri) {
        getActivity().getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, C0542b.m791a(((C0546b.a) this.presenter).mo814c().m608a(getActivity()), ((C0546b.a) this.presenter).mo814c().getId(), uri), "annotation_fragment_for_chat").addToBackStack("annotation_fragment_for_chat").commit();
    }

    @Override // com.instabug.chat.p011ui.p013b.C0546b.b
    /* renamed from: i */
    public void mo826i() {
        startActivityForResult(m832m(), 161);
    }

    /* renamed from: k */
    private void m830k() {
        getActivity().getSupportFragmentManager().beginTransaction().add(C0507R.id.instabug_fragment_container, ViewOnClickListenerC0545a.m798a(this), "attachments_bottom_sheet_fragment").addToBackStack("attachments_bottom_sheet_fragment").commit();
    }

    /* renamed from: l */
    private void m831l() {
        PermissionsUtils.requestPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE", 162, new Runnable() { // from class: com.instabug.chat.ui.b.c.1
            @Override // java.lang.Runnable
            public void run() {
            }
        }, new Runnable() { // from class: com.instabug.chat.ui.b.c.2
            @Override // java.lang.Runnable
            public void run() {
                ((C0546b.a) ViewOnClickListenerC0547c.this.presenter).mo817f();
            }
        });
        ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
        if (chatPlugin != null) {
            chatPlugin.setState(2);
        }
    }

    /* renamed from: m */
    private Intent m832m() {
        Intent intent = new Intent("android.intent.action.PICK");
        if (Build.VERSION.SDK_INT >= 11) {
            intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
        }
        intent.setType("image/*");
        return intent;
    }

    /* renamed from: j */
    public void m837j() {
        if (!C0512a.m518a().m534b()) {
            m833n();
        } else {
            Toast.makeText(getContext(), C0507R.string.instabug_str_video_encoder_busy, 0).show();
        }
    }

    /* renamed from: n */
    private void m833n() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.RECORD_AUDIO") != 0) {
            requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 163);
        } else {
            ((C0546b.a) this.presenter).mo816e();
        }
    }
}
