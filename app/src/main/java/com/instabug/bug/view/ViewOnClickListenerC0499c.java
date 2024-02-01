package com.instabug.bug.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.bug.C0458R;
import com.instabug.bug.C0468d;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.EnumC0472b;
import com.instabug.bug.p001a.C0460a;
import com.instabug.bug.p002b.C0463b;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.C0487a;
import com.instabug.bug.view.InterfaceC0503d;
import com.instabug.bug.view.p003a.C0489b;
import com.instabug.bug.view.p005c.C0501b;
import com.instabug.library.C0577R;
import com.instabug.library.Feature;
import com.instabug.library.FragmentVisibilityChangedListener;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.ScreenRecordingEventBus;
import com.instabug.library.core.p024ui.ToolbarFragment;
import com.instabug.library.internal.media.AudioPlayer;
import com.instabug.library.internal.video.ScreenRecordEvent;
import com.instabug.library.internal.video.VideoPlayerFragment;
import com.instabug.library.model.Attachment;
import com.instabug.library.model.State;
import com.instabug.library.util.AttrResolver;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PlaceHolderUtils;
import com.instabug.library.util.SystemServiceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: BugReportingFragment.java */
/* renamed from: com.instabug.bug.view.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0499c extends ToolbarFragment<InterfaceC0503d.a> implements View.OnClickListener, C0487a.a, InterfaceC0503d.b {

    /* renamed from: a */
    private EditText f215a;

    /* renamed from: b */
    private EditText f216b;

    /* renamed from: c */
    private TextView f217c;

    /* renamed from: d */
    private GridView f218d;

    /* renamed from: e */
    private ImageView f219e;

    /* renamed from: f */
    private AudioPlayer f220f;

    /* renamed from: g */
    private String f221g;

    /* renamed from: h */
    private String f222h;

    /* renamed from: i */
    private EnumC0472b f223i;

    /* renamed from: j */
    private boolean f224j;

    /* renamed from: k */
    private BroadcastReceiver f225k;

    /* renamed from: l */
    private List<View> f226l;

    /* renamed from: m */
    private ProgressDialog f227m;

    /* renamed from: n */
    private C0487a f228n;

    /* renamed from: o */
    private Subscription f229o;

    /* renamed from: p */
    private a f230p;

    /* compiled from: BugReportingFragment.java */
    /* renamed from: com.instabug.bug.view.c$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: e */
        void mo320e();
    }

    /* renamed from: a */
    public static Fragment m366a(EnumC0472b enumC0472b, String str, String str2) {
        ViewOnClickListenerC0499c viewOnClickListenerC0499c = new ViewOnClickListenerC0499c();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bug_type", enumC0472b);
        bundle.putString("bug_message", str);
        bundle.putString("bug_message_hint", str2);
        viewOnClickListenerC0499c.setArguments(bundle);
        return viewOnClickListenerC0499c;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m386q();
        m387r();
        this.f223i = (EnumC0472b) getArguments().getSerializable("bug_type");
        this.f221g = getArguments().getString("bug_message");
        this.f222h = getArguments().getString("bug_message_hint");
        if (this.presenter == 0) {
            this.presenter = m389a();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.f230p = (a) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement InstabugSuccessFragment.OnImageEditingListener");
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.f230p = null;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected int getContentLayout() {
        return C0458R.layout.instabug_lyt_feedback;
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void initContentViews(View view, Bundle bundle) {
        Spanned fromHtml;
        String userEmail;
        m379j();
        if (!((C0482a.m236a().m268p().isEmpty() && C0482a.m236a().m269q() == ExtendedBugReport.State.DISABLED) ? false : true)) {
            this.toolbarImageButtonDone.setImageDrawable(ContextCompat.getDrawable(getContext(), C0577R.drawable.instabug_ic_send));
            this.toolbarImageButtonDone.setColorFilter(Colorizer.getPrimaryColorFilter());
        } else {
            this.toolbarImageButtonDone.setImageDrawable(ContextCompat.getDrawable(getContext(), C0458R.drawable.instabug_ic_next));
        }
        this.f218d = (GridView) findViewById(C0458R.id.instabug_lyt_attachments_grid);
        this.f228n = new C0487a(getActivity(), null, this);
        this.f215a = (EditText) findViewById(C0458R.id.instabug_edit_text_email);
        this.f215a.setHint(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.EMAIL_FIELD_HINT, getString(C0458R.string.instabug_str_email_hint)));
        this.f215a.addTextChangedListener(new C0460a() { // from class: com.instabug.bug.view.c.1
            @Override // com.instabug.bug.p001a.C0460a, android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                ((InterfaceC0503d.a) ViewOnClickListenerC0499c.this.presenter).mo423a(ViewOnClickListenerC0499c.this.f215a.getText().toString());
            }
        });
        this.f216b = (EditText) findViewById(C0458R.id.instabug_edit_text_message);
        this.f216b.addTextChangedListener(new C0460a() { // from class: com.instabug.bug.view.c.2
            @Override // com.instabug.bug.p001a.C0460a, android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (ViewOnClickListenerC0499c.this.getActivity() != null) {
                    ((InterfaceC0503d.a) ViewOnClickListenerC0499c.this.presenter).mo426b(ViewOnClickListenerC0499c.this.f216b.getText().toString());
                }
            }
        });
        this.f217c = (TextView) findViewById(C0458R.id.instabug_text_view_disclaimer);
        if (Build.VERSION.SDK_INT < 11) {
            this.f216b.setBackgroundResource(C0458R.drawable.instabug_bg_edit_text);
            this.f215a.setBackgroundResource(C0458R.drawable.instabug_bg_edit_text);
        }
        if (!C0482a.m236a().m261i()) {
            this.f215a.setVisibility(8);
            this.f216b.setGravity(16);
        }
        if (this.f222h != null) {
            this.f216b.setHint(this.f222h);
        }
        if (this.f221g != null) {
            this.f216b.setText(this.f221g);
        }
        State state = C0468d.m86a().m103d().getState();
        if (state != null && (userEmail = state.getUserEmail()) != null && !userEmail.isEmpty()) {
            this.f215a.setText(userEmail);
        }
        String m266n = C0482a.m236a().m266n();
        if (m266n != null && !m266n.isEmpty() && InstabugCore.getFeatureState(Feature.DISCLAIMER) == Feature.State.ENABLED) {
            String mo427c = ((InterfaceC0503d.a) this.presenter).mo427c(m266n);
            if (Build.VERSION.SDK_INT >= 24) {
                fromHtml = Html.fromHtml(mo427c, 0);
            } else {
                fromHtml = Html.fromHtml(mo427c);
            }
            this.f217c.setText(fromHtml);
            this.f217c.setMovementMethod(LinkMovementMethod.getInstance());
            return;
        }
        this.f217c.setVisibility(8);
    }

    /* renamed from: j */
    private void m379j() {
        this.f226l = new ArrayList();
        m380k();
        if (C0482a.m236a().m254d().isAllowScreenRecording()) {
            findViewById(C0458R.id.instabug_attach_video).setOnClickListener(this);
            this.f226l.add(findViewById(C0458R.id.instabug_attach_video));
        } else {
            findViewById(C0458R.id.instabug_attach_video).setVisibility(8);
        }
        if (C0482a.m236a().m254d().isAllowTakeExtraScreenshot()) {
            findViewById(C0458R.id.instabug_attach_screenshot).setOnClickListener(this);
            this.f226l.add(findViewById(C0458R.id.instabug_attach_screenshot));
        } else {
            findViewById(C0458R.id.instabug_attach_screenshot).setVisibility(8);
        }
        if (C0482a.m236a().m254d().isAllowAttachImageFromGallery()) {
            findViewById(C0458R.id.instabug_attach_gallery_image).setOnClickListener(this);
            this.f226l.add(findViewById(C0458R.id.instabug_attach_gallery_image));
        } else {
            findViewById(C0458R.id.instabug_attach_gallery_image).setVisibility(8);
        }
        if (this.f226l.size() > 0) {
            m368a(this.f226l.get(0));
        }
    }

    /* renamed from: k */
    private void m380k() {
        ((TextView) findViewById(C0458R.id.instabug_attach_gallery_image_label)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_IMAGE_FROM_GALLERY, getString(C0458R.string.instabug_str_add_photo)));
        ((TextView) findViewById(C0458R.id.instabug_attach_screenshot_label)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_EXTRA_SCREENSHOT, getString(C0458R.string.instabug_str_take_screenshot)));
        ((TextView) findViewById(C0458R.id.instabug_attach_video_label)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_VIDEO, getString(C0458R.string.instabug_str_record_video)));
    }

    /* renamed from: a */
    private void m368a(View view) {
        for (View view2 : this.f226l) {
            ((ViewGroup) view2).getChildAt(1).setVisibility(8);
            ((TextView) ((ViewGroup) view2).getChildAt(0)).setTextColor(AttrResolver.getTintingColor(getContext()));
            view2.setTag(false);
        }
        View childAt = ((ViewGroup) view).getChildAt(1);
        View childAt2 = ((ViewGroup) view).getChildAt(0);
        childAt.setVisibility(0);
        ((TextView) childAt).setTextColor(Instabug.getPrimaryColor());
        ((TextView) childAt2).setTextColor(Instabug.getPrimaryColor());
        view.setTag(true);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        getActivity().getWindow().setSoftInputMode(16);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        ((InterfaceC0503d.a) this.presenter).mo419a();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.f225k, new IntentFilter("refresh.attachments"));
        ((InterfaceC0503d.a) this.presenter).mo428c();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        ((InterfaceC0503d.a) this.presenter).mo425b();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.f225k);
        if (!this.f229o.isUnsubscribed()) {
            this.f229o.unsubscribe();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        if (this.f220f != null) {
            this.f220f.release();
        }
        super.onPause();
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        ((InterfaceC0503d.a) this.presenter).mo421a(bundle);
        super.onSaveInstanceState(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        ((InterfaceC0503d.a) this.presenter).mo420a(i, i2, intent);
    }

    @Override // android.support.v4.app.Fragment
    public void onViewStateRestored(@Nullable Bundle bundle) {
        super.onViewStateRestored(bundle);
        ((InterfaceC0503d.a) this.presenter).mo421a(bundle);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected String getTitle() {
        if (this.f223i == EnumC0472b.BUG) {
            return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.BUG_REPORT_HEADER, getString(C0458R.string.instabug_str_bug_header));
        }
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.FEEDBACK_REPORT_HEADER, getString(C0458R.string.instabug_str_feedback_header));
    }

    /* renamed from: a */
    protected InterfaceC0503d.a m389a() {
        return new C0504e(this);
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onDoneButtonClicked() {
        ((InterfaceC0503d.a) this.presenter).mo432g();
    }

    @Override // com.instabug.library.core.p024ui.ToolbarFragment
    protected void onCloseButtonClicked() {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == C0458R.id.instabug_attach_screenshot) {
            if (view.getTag() == null || view.getTag().equals(false)) {
                m368a(view);
                return;
            } else if (C0468d.m86a().m103d().m136i() < 4) {
                ((InterfaceC0503d.a) this.presenter).mo430e();
                return;
            } else {
                m384o();
                return;
            }
        }
        if (id == C0458R.id.instabug_attach_gallery_image) {
            if (view.getTag() == null || view.getTag().equals(false)) {
                m368a(view);
                return;
            } else if (C0468d.m86a().m103d().m136i() < 4) {
                ((InterfaceC0503d.a) this.presenter).mo431f();
                return;
            } else {
                m384o();
                return;
            }
        }
        if (id == C0458R.id.instabug_attach_video) {
            if (view.getTag() == null || view.getTag().equals(false)) {
                m368a(view);
            } else if (C0468d.m86a().m103d().m136i() < 4) {
                m381l();
            } else {
                m384o();
            }
        }
    }

    /* renamed from: l */
    private void m381l() {
        if (!C0463b.m56a().m69b()) {
            m383n();
        } else {
            Toast.makeText(getContext(), C0458R.string.instabug_str_video_encoder_busy, 0).show();
        }
    }

    @Override // com.instabug.bug.view.C0487a.a
    /* renamed from: a */
    public void mo337a(View view, Attachment attachment) {
        m382m();
        int id = view.getId();
        if (id == C0458R.id.instabug_grid_img_item) {
            SystemServiceUtils.hideInputMethod(getActivity());
            m371a(attachment, getTitle());
            return;
        }
        if (id == C0458R.id.instabug_grid_audio_item) {
            if (this.f220f != null) {
                m385p();
                if (this.f219e != view.findViewById(C0458R.id.instabug_btn_audio_play_attachment)) {
                    m373b(view, attachment);
                    return;
                }
                return;
            }
            m373b(view, attachment);
            return;
        }
        if (id == C0458R.id.instabug_btn_remove_attachment) {
            if (this.f220f != null) {
                m385p();
            }
            ((InterfaceC0503d.a) this.presenter).mo422a(attachment);
        } else if (id == C0458R.id.instabug_grid_video_item) {
            SystemServiceUtils.hideInputMethod(getActivity());
            this.f224j = true;
            m392a(attachment.getLocalPath());
        }
    }

    /* renamed from: m */
    private void m382m() {
        this.f215a.clearFocus();
        this.f215a.setError(null);
        this.f216b.clearFocus();
        this.f216b.setError(null);
    }

    /* renamed from: n */
    private void m383n() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.RECORD_AUDIO") != 0) {
            requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 177);
        } else {
            ((InterfaceC0503d.a) this.presenter).mo429d();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (iArr.length > 0 && iArr[0] == 0) {
            switch (i) {
                case 177:
                    ((InterfaceC0503d.a) this.presenter).mo429d();
                    return;
                case 3873:
                    mo404g();
                    C0468d.m86a().m107h();
                    return;
                default:
                    super.onRequestPermissionsResult(i, strArr, iArr);
                    return;
            }
        }
        if (i == 177) {
            ((InterfaceC0503d.a) this.presenter).mo429d();
        }
    }

    /* renamed from: o */
    private void m384o() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(C0458R.string.instabug_str_alert_title_max_attachments);
        builder.setMessage(C0458R.string.instabug_str_alert_message_max_attachments);
        builder.setPositiveButton(C0458R.string.instabug_str_ok, new DialogInterface.OnClickListener() { // from class: com.instabug.bug.view.c.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    /* renamed from: b */
    private void m373b(View view, Attachment attachment) {
        InstabugSDKLogger.m1799d(this, "start audio player " + attachment.getLocalPath() + " view = " + view.getId());
        this.f219e = (ImageView) view.findViewById(C0458R.id.instabug_btn_audio_play_attachment);
        this.f219e.setImageResource(C0458R.drawable.instabug_ic_stop);
        this.f220f = new AudioPlayer();
        this.f220f.start(attachment.getLocalPath());
        this.f220f.addOnStopListener(new AudioPlayer.OnStopListener(attachment.getLocalPath()) { // from class: com.instabug.bug.view.c.4
            @Override // com.instabug.library.internal.media.AudioPlayer.OnStopListener
            public void onStop() {
                ViewOnClickListenerC0499c.this.m385p();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: p */
    public void m385p() {
        this.f219e.setImageResource(C0458R.drawable.instabug_ic_play);
        this.f220f.release();
        this.f220f = null;
    }

    /* renamed from: a */
    private void m371a(Attachment attachment, String str) {
        mo399c(false);
        getFragmentManager().beginTransaction().add(C0458R.id.instabug_fragment_container, C0489b.m342a(str, Uri.fromFile(new File(attachment.getLocalPath())), 1), "annotation").addToBackStack("annotation").commit();
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: a */
    public void mo393a(List<Attachment> list) {
        this.f228n.m336c();
        for (Attachment attachment : list) {
            if (attachment.getType().equals(Attachment.Type.MAIN_SCREENSHOT) || attachment.getType().equals(Attachment.Type.IMAGE) || attachment.getType().equals(Attachment.Type.AUDIO) || attachment.getType().equals(Attachment.Type.VIDEO)) {
                this.f228n.m333a(attachment);
            }
            if (attachment.getType().equals(Attachment.Type.VIDEO)) {
                C0468d.m86a().m103d().setHasVideo(true);
            }
        }
        this.f218d.setAdapter((ListAdapter) this.f228n);
        this.f228n.notifyDataSetChanged();
        if (InstabugCore.getFeatureState(Feature.MULTIPLE_ATTACHMENTS) == Feature.State.ENABLED && C0482a.m236a().m257e()) {
            findViewById(C0458R.id.HorizontalScrollActionBar).setVisibility(0);
        } else {
            findViewById(C0458R.id.HorizontalScrollActionBar).setVisibility(8);
        }
    }

    /* renamed from: b */
    public boolean m397b() {
        return this.f228n.m331a() != null && this.f228n.m331a().getVisibility() == 0;
    }

    /* renamed from: c */
    public boolean m400c() {
        return this.f228n.m334b() != null && this.f228n.m334b().getVisibility() == 0;
    }

    /* renamed from: a */
    public void m394a(boolean z) {
        if (z) {
            this.f228n.m331a().setVisibility(0);
        } else {
            this.f228n.m331a().setVisibility(8);
        }
    }

    /* renamed from: b */
    public void m396b(boolean z) {
        if (z) {
            this.f228n.m334b().setVisibility(0);
        } else {
            this.f228n.m334b().setVisibility(8);
        }
    }

    /* renamed from: d */
    public boolean m401d() {
        return this.f224j;
    }

    /* renamed from: a */
    public void m390a(Intent intent, int i) {
        startActivityForResult(intent, i);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: c */
    public void mo399c(boolean z) {
        if (getFragmentManager().findFragmentById(C0458R.id.instabug_fragment_container) instanceof FragmentVisibilityChangedListener) {
            ((FragmentVisibilityChangedListener) getFragmentManager().findFragmentById(C0458R.id.instabug_fragment_container)).onVisibilityChanged(z);
        }
    }

    /* renamed from: a */
    public void m392a(String str) {
        if (str != null) {
            getFragmentManager().beginTransaction().add(C0458R.id.instabug_fragment_container, VideoPlayerFragment.newInstance(str), "video_player").addToBackStack("play video").commit();
            return;
        }
        if (!m397b()) {
            m394a(true);
        }
        if (m400c()) {
            m396b(false);
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: e */
    public void mo402e() {
        if (C0482a.m236a().m265m()) {
            finishActivity();
            getActivity().startActivity(SuccessActivity.m324a(getContext()));
        } else if (this.f230p != null) {
            this.f230p.mo320e();
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: f */
    public void mo403f() {
        getFragmentManager().beginTransaction().add(C0458R.id.instabug_fragment_container, C0501b.m409a(getTitle())).addToBackStack("ExtraFieldsFragment").commit();
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: g */
    public void mo404g() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
        intent.setType("image/*");
        m390a(intent, 3862);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: h */
    public void mo405h() {
        if (this.f227m != null) {
            if (!this.f227m.isShowing()) {
                this.f227m.show();
            }
        } else {
            this.f227m = new ProgressDialog(getActivity());
            this.f227m.setCancelable(false);
            this.f227m.setMessage(getResources().getString(C0458R.string.instabug_str_dialog_message_preparing));
            this.f227m.show();
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: i */
    public void mo406i() {
        if (this.f227m != null && this.f227m.isShowing()) {
            this.f227m.dismiss();
        }
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: a */
    public void mo391a(Attachment attachment) {
        this.f228n.m335b(attachment);
        this.f228n.notifyDataSetChanged();
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: b */
    public void mo395b(String str) {
        this.f215a.requestFocus();
        this.f215a.setError(str);
    }

    @Override // com.instabug.bug.view.InterfaceC0503d.b
    /* renamed from: c */
    public void mo398c(String str) {
        this.f216b.requestFocus();
        this.f216b.setError(str);
    }

    /* renamed from: q */
    private void m386q() {
        this.f225k = new BroadcastReceiver() { // from class: com.instabug.bug.view.c.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InstabugSDKLogger.m1802i(this, "Refreshing Attachments");
                if (ViewOnClickListenerC0499c.this.getActivity() != null) {
                    ((InterfaceC0503d.a) ViewOnClickListenerC0499c.this.presenter).mo428c();
                }
            }
        };
    }

    /* renamed from: r */
    private void m387r() {
        if (this.f229o == null) {
            this.f229o = ScreenRecordingEventBus.getInstance().subscribe(new Action1<ScreenRecordEvent>() { // from class: com.instabug.bug.view.c.6
                @Override // p045rx.functions.Action1
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void call(final ScreenRecordEvent screenRecordEvent) {
                    ViewOnClickListenerC0499c.this.getActivity().runOnUiThread(new Runnable() { // from class: com.instabug.bug.view.c.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (screenRecordEvent.getStatus() == 1) {
                                ViewOnClickListenerC0499c.this.m370a(screenRecordEvent);
                            } else if (screenRecordEvent.getStatus() == 2) {
                                ViewOnClickListenerC0499c.this.m388s();
                            }
                        }
                    });
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: s */
    public void m388s() {
        Attachment mo418a = ((InterfaceC0503d.a) this.presenter).mo418a(C0468d.m86a().m103d().m131e());
        if (mo418a != null) {
            ((InterfaceC0503d.a) this.presenter).mo422a(mo418a);
        }
        ((InterfaceC0503d.a) this.presenter).mo428c();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m370a(ScreenRecordEvent screenRecordEvent) {
        String path = screenRecordEvent.getVideoUri().getPath();
        if (path == null) {
            m388s();
            return;
        }
        if (!new File(path).exists()) {
            m388s();
            return;
        }
        ((InterfaceC0503d.a) this.presenter).mo424a(C0468d.m86a().m103d().m131e(), path);
        C0468d.m86a().m103d().setVideoEncoded(true);
        if (m401d()) {
            if (m397b()) {
                m394a(false);
            }
            if (!m400c()) {
                m396b(true);
            }
            ((InterfaceC0503d.a) this.presenter).mo428c();
            m392a(path);
            return;
        }
        ((InterfaceC0503d.a) this.presenter).mo428c();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (!this.f229o.isUnsubscribed()) {
            this.f229o.unsubscribe();
        }
    }
}
