package com.instabug.library.core.p024ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.p024ui.BaseContract;
import com.instabug.library.core.ui.BaseContract.Presenter;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.InstabugThemeResolver;
import com.instabug.library.util.LocaleUtils;
import com.instabug.library.util.StatusBarUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"})
/* loaded from: classes.dex */
public abstract class BaseFragmentActivity<P extends BaseContract.Presenter> extends FragmentActivity implements _InstabugActivity, BaseContract.View<FragmentActivity> {
    protected P presenter;

    @LayoutRes
    protected abstract int getLayout();

    protected abstract void initViews();

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        InstabugSDKLogger.m1802i(this, "onCreate called");
        StatusBarUtils.setStatusBar(this);
        LocaleUtils.setLocale(this, Instabug.getLocale(this));
        super.onCreate(bundle);
        setTheme(InstabugThemeResolver.resolveTheme(SettingsManager.getInstance().getTheme()));
        setContentView(getLayout());
        initViews();
        getWindow().getDecorView().setId(C0577R.id.instabug_decor_view);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        InstabugSDKLogger.m1802i(this, "onStart called");
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        InstabugSDKLogger.m1799d(this, "onResume(),  SDK Invoking State Changed: true");
        Intent intent = new Intent();
        intent.setAction("SDK invoked");
        intent.putExtra("SDK invoking state", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        InstabugSDKLogger.m1799d(this, "onPause(),  SDK Invoking State Changed: false");
        Intent intent = new Intent();
        intent.setAction("SDK invoked");
        intent.putExtra("SDK invoking state", false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        InstabugSDKLogger.m1802i(this, "onStop called");
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        InstabugSDKLogger.m1802i(this, "onDestroy called");
        super.onDestroy();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.instabug.library.core.ui.BaseContract.View
    public FragmentActivity getViewContext() {
        return this;
    }

    @Override // com.instabug.library.core.ui.BaseContract.View
    public void finishActivity() {
        finish();
    }
}
