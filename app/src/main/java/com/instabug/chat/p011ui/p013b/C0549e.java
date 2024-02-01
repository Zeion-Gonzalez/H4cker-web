package com.instabug.chat.p011ui.p013b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.instabug.chat.C0507R;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.view.ScaleImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/* compiled from: ImageAttachmentViewerFragment.java */
/* renamed from: com.instabug.chat.ui.b.e */
/* loaded from: classes.dex */
public class C0549e extends Fragment {

    /* renamed from: a */
    private String f443a;

    /* renamed from: b */
    private ProgressBar f444b;

    /* renamed from: c */
    private ScaleImageView f445c;

    /* renamed from: d */
    private float f446d;

    /* renamed from: e */
    private float f447e;

    /* renamed from: a */
    public static C0549e m861a(String str) {
        C0549e c0549e = new C0549e();
        Bundle bundle = new Bundle();
        bundle.putString("img_url", str);
        c0549e.setArguments(bundle);
        return c0549e;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.f443a = getArguments().getString("img_url");
        } else if (bundle != null) {
            this.f443a = bundle.getString("img_url");
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("img_url", this.f443a);
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0507R.layout.instabug_fragment_image_attachment_viewer, viewGroup, false);
        this.f444b = (ProgressBar) inflate.findViewById(C0507R.id.instabug_attachment_progress_bar);
        this.f445c = (ScaleImageView) inflate.findViewById(C0507R.id.instabug_img_attachment);
        if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.DISABLED) {
            getActivity().findViewById(C0507R.id.instabug_pbi_footer).setVisibility(8);
        }
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        this.f446d = r0.widthPixels - ((int) m865a(24.0f, getActivity()));
        this.f447e = r0.heightPixels - ((int) m865a(24.0f, getActivity()));
        if (URLUtil.isValidUrl(this.f443a)) {
            AssetsCacheManager.getAssetEntity(getActivity(), AssetsCacheManager.createEmptyEntity(getActivity(), this.f443a, AssetEntity.AssetType.IMAGE), new AssetsCacheManager.OnDownloadFinished() { // from class: com.instabug.chat.ui.b.e.1
                @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
                public void onSuccess(AssetEntity assetEntity) {
                    InstabugSDKLogger.m1799d(this, "Asset Entity downloaded: " + assetEntity.getFile().getPath());
                    try {
                        Bitmap resizeBitmap = BitmapUtils.resizeBitmap(BitmapFactory.decodeStream(new FileInputStream(assetEntity.getFile())), C0549e.this.f446d, C0549e.this.f447e);
                        if (resizeBitmap != null) {
                            C0549e.this.f445c.setImageBitmap(resizeBitmap);
                        } else {
                            Toast.makeText(C0549e.this.getActivity(), C0507R.string.instabug_str_image_loading_error, 0).show();
                        }
                        if (C0549e.this.f444b.getVisibility() == 0) {
                            C0549e.this.f444b.setVisibility(8);
                        }
                    } catch (FileNotFoundException e) {
                        InstabugSDKLogger.m1801e(this, "Asset Entity downloading got FileNotFoundException error", e);
                    }
                }

                @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
                public void onFailed(Throwable th) {
                    InstabugSDKLogger.m1801e(this, "Asset Entity downloading got error", th);
                }
            });
        } else {
            BitmapUtils.loadBitmap(this.f443a, this.f445c, this.f446d, this.f447e);
        }
    }

    /* renamed from: a */
    public float m865a(float f, Context context) {
        return (context.getResources().getDisplayMetrics().densityDpi / 160.0f) * f;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.DISABLED) {
            getActivity().findViewById(C0507R.id.instabug_pbi_footer).setVisibility(0);
        }
    }
}
