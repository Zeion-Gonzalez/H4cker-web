package com.instabug.bug.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.instabug.bug.BugPlugin;
import com.instabug.bug.C0458R;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragmentActivity;

/* loaded from: classes.dex */
public class SuccessActivity extends BaseFragmentActivity implements View.OnClickListener, _InstabugActivity {
    /* renamed from: a */
    public static Intent m324a(Context context) {
        return new Intent(context, SuccessActivity.class);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.bug.view.SuccessActivity.1
            @Override // java.lang.Runnable
            public void run() {
                SuccessActivity.this.finishActivity();
            }
        }, 3000L);
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0458R.layout.instabug_activity_bug_reporting;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
        m325a();
    }

    /* renamed from: a */
    private void m325a() {
        getSupportFragmentManager().beginTransaction().replace(C0458R.id.instabug_fragment_container, new C0506g()).commit();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        finish();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            bugPlugin.setState(1);
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null && bugPlugin.getState() != 2) {
            bugPlugin.setState(0);
        }
    }
}
