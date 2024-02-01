package com.instabug.library.p031ui.promptoptions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.instabug.library.C0577R;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library._InstabugActivity;
import com.instabug.library.bugreporting.model.Bug;
import com.instabug.library.core.p024ui.BaseFragmentActivity;
import com.instabug.library.p031ui.promptoptions.ViewOnClickListenerC0748c;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public class PromptOptionsActivity extends BaseFragmentActivity implements _InstabugActivity, ViewOnClickListenerC0748c.a {

    /* renamed from: a */
    private boolean f1235a = false;

    /* renamed from: a */
    public static Intent m1757a(Context context, Uri uri) {
        Intent intent = new Intent(context, PromptOptionsActivity.class);
        intent.putExtra("screenshotUri", uri);
        return intent;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m1759c();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        SettingsManager.getInstance().setPromptOptionsScreenShown(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        SettingsManager.getInstance().setPromptOptionsScreenShown(false);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0577R.layout.instabug_activity;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
        m1758b();
    }

    /* renamed from: b */
    private void m1758b() {
        getSupportFragmentManager().beginTransaction().replace(C0577R.id.instabug_fragment_container, ViewOnClickListenerC0748c.m1768a((Uri) getIntent().getParcelableExtra("screenshotUri")), "prompt_options_fragment").commit();
    }

    /* renamed from: c */
    private void m1759c() {
        if (SettingsManager.getInstance().getOnSdkInvokedCallback() != null) {
            SettingsManager.getInstance().getOnSdkInvokedCallback().onSdkInvoked();
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        OnSdkDismissedCallback onSdkDismissedCallback = SettingsManager.getInstance().getOnSdkDismissedCallback();
        if (onSdkDismissedCallback != null && !this.f1235a) {
            onSdkDismissedCallback.onSdkDismissed(OnSdkDismissedCallback.DismissType.CANCEL, Bug.Type.NOT_AVAILABLE);
        }
    }

    @Override // com.instabug.library.p031ui.promptoptions.ViewOnClickListenerC0748c.a
    /* renamed from: a */
    public void mo1760a() {
        this.f1235a = true;
    }
}
