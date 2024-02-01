package com.instabug.chat.p011ui.p013b;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.instabug.chat.C0507R;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.C0577R;
import com.instabug.library.FragmentVisibilityChangedListener;
import com.instabug.library.InstabugBaseFragment;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PlaceHolderUtils;

/* compiled from: AttachmentsBottomSheetFragment.java */
/* renamed from: com.instabug.chat.ui.b.a */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0545a extends InstabugBaseFragment implements View.OnClickListener, FragmentVisibilityChangedListener {

    /* renamed from: a */
    a f429a;

    /* compiled from: AttachmentsBottomSheetFragment.java */
    /* renamed from: com.instabug.chat.ui.b.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo802a();

        /* renamed from: b */
        void mo803b();

        /* renamed from: c */
        void mo804c();
    }

    /* renamed from: a */
    public static ViewOnClickListenerC0545a m798a(a aVar) {
        ViewOnClickListenerC0545a viewOnClickListenerC0545a = new ViewOnClickListenerC0545a();
        viewOnClickListenerC0545a.f429a = aVar;
        return viewOnClickListenerC0545a;
    }

    @Override // com.instabug.library.InstabugBaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (C0537a.m731b().isScreenshotEnabled()) {
            view.findViewById(C0507R.id.instabug_attach_screenshot).setOnClickListener(this);
        } else {
            view.findViewById(C0507R.id.instabug_attach_screenshot).setVisibility(8);
        }
        if (C0537a.m731b().isImageFromGalleryEnabled()) {
            view.findViewById(C0507R.id.instabug_attach_gallery_image).setOnClickListener(this);
        } else {
            view.findViewById(C0507R.id.instabug_attach_gallery_image).setVisibility(8);
        }
        if (C0537a.m731b().isScreenRecordingEnabled()) {
            view.findViewById(C0507R.id.instabug_attach_video).setOnClickListener(this);
        } else {
            view.findViewById(C0507R.id.instabug_attach_video).setVisibility(8);
        }
        view.findViewById(C0507R.id.instabug_attachments_bottom_sheet_dim_view).setOnClickListener(this);
        m801b(view);
        m800a(view);
    }

    /* renamed from: a */
    private void m800a(View view) {
        if (Build.VERSION.SDK_INT >= 12) {
            final View findViewById = view.findViewById(C0507R.id.instabug_attachments_actions_bottom_sheet);
            findViewById.setAlpha(0.0f);
            findViewById.post(new Runnable() { // from class: com.instabug.chat.ui.b.a.1
                @Override // java.lang.Runnable
                public void run() {
                    if (Build.VERSION.SDK_INT >= 12) {
                        findViewById.setTranslationY(findViewById.getHeight());
                        findViewById.setAlpha(1.0f);
                        findViewById.animate().setDuration(100L).translationYBy(-r0);
                    }
                }
            });
        }
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void saveState(Bundle bundle) {
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void restoreState(Bundle bundle) {
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected int getLayout() {
        return C0507R.layout.instabug_fragment_attachments_bottom_sheet;
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected String getTitle() {
        return getString(C0577R.string.instabug_str_empty);
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void consumeNewInstanceSavedArguments() {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == C0507R.id.instabug_attach_screenshot) {
            m799a();
            this.f429a.mo802a();
            return;
        }
        if (id == C0507R.id.instabug_attach_gallery_image) {
            m799a();
            this.f429a.mo803b();
        } else if (id == C0507R.id.instabug_attach_video) {
            m799a();
            this.f429a.mo804c();
        } else if (id == C0507R.id.instabug_attachments_bottom_sheet_dim_view) {
            m799a();
        }
    }

    /* renamed from: a */
    private void m799a() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack("attachments_bottom_sheet_fragment", 1);
    }

    @Override // com.instabug.library.FragmentVisibilityChangedListener
    public void onVisibilityChanged(boolean z) {
        InstabugSDKLogger.m1799d(this, "Is visible " + z);
    }

    /* renamed from: b */
    private void m801b(View view) {
        ((TextView) view.findViewById(C0507R.id.instabug_attach_gallery_image_text)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_IMAGE_FROM_GALLERY, getString(C0577R.string.instabug_str_add_photo)));
        ((TextView) view.findViewById(C0507R.id.instabug_attach_screenshot_text)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_EXTRA_SCREENSHOT, getString(C0577R.string.instabug_str_take_screenshot)));
        ((TextView) view.findViewById(C0507R.id.instabug_attach_video_text)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.ADD_VIDEO, getString(C0577R.string.instabug_str_record_video)));
    }
}
