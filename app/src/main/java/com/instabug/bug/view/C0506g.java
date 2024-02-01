package com.instabug.bug.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.bug.C0458R;
import com.instabug.library.C0577R;
import com.instabug.library.Feature;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.util.InstabugAppData;
import com.instabug.library.util.InstabugLogoProvider;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PlaceHolderUtils;

/* compiled from: InstabugSuccessFragment.java */
/* renamed from: com.instabug.bug.view.g */
/* loaded from: classes.dex */
public class C0506g extends BaseFragment {
    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected int getLayout() {
        return C0458R.layout.instabug_lyt_success;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragment
    protected void initViews(View view, Bundle bundle) {
        ((TextView) findViewById(C0458R.id.instabug_txt_success_note)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.REPORT_SUCCESSFULLY_SENT, m444a()));
        if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.ENABLED) {
            findViewById(C0458R.id.instabug_pbi_container).setVisibility(8);
        } else {
            getActivity().findViewById(C0458R.id.instabug_pbi_footer).setVisibility(8);
            ((ImageView) findViewById(C0458R.id.image_instabug_logo)).setImageBitmap(InstabugLogoProvider.getInstabugLogo());
        }
        ((TextView) findViewById(C0577R.id.instabug_fragment_title)).setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.SUCCESS_DIALOG_HEADER, getString(C0458R.string.instabug_str_thank_you)));
        findViewById(C0458R.id.instabug_success_dialog_container).setOnClickListener(new View.OnClickListener() { // from class: com.instabug.bug.view.g.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                C0506g.this.finishActivity();
            }
        });
    }

    /* renamed from: a */
    private String m444a() {
        String appName = new InstabugAppData(getActivity().getApplicationContext()).getAppName();
        if (appName == null) {
            InstabugSDKLogger.m1804w(this, "It seems app:name isn't defined in your manifest. Using a generic name instead");
        }
        int i = C0458R.string.instabug_str_success_note;
        Object[] objArr = new Object[1];
        if (appName == null) {
            appName = "App";
        }
        objArr[0] = appName;
        return getString(i, objArr);
    }
}
