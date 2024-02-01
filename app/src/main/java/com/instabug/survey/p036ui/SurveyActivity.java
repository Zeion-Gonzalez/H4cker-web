package com.instabug.survey.p036ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.instabug.library.Instabug;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragmentActivity;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.C0764R;
import com.instabug.survey.C0766a;
import com.instabug.survey.SurveyPlugin;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p034c.C0776d;
import com.instabug.survey.p034c.C0777e;
import com.instabug.survey.p036ui.p037a.ViewOnClickListenerC0794c;
import com.instabug.survey.p036ui.p037a.p043f.C0800a;
import com.instabug.survey.p036ui.p037a.p044g.ViewOnClickListenerC0801a;

/* loaded from: classes.dex */
public class SurveyActivity extends BaseFragmentActivity implements _InstabugActivity {

    /* renamed from: a */
    boolean f1369a = false;

    /* renamed from: b */
    Handler f1370b;

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        setTheme(C0776d.m1993a(Instabug.getTheme()));
        setContentView(C0764R.layout.instabug_activity);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, C0764R.color.instabug_dialog_bg_color));
        findViewById(C0764R.id.instabug_fragment_container).postDelayed(new Runnable() { // from class: com.instabug.survey.ui.SurveyActivity.1
            @Override // java.lang.Runnable
            public void run() {
                if (InstabugCore.getStartedActivitiesCount() <= 1) {
                    SurveyActivity.this.finish();
                    return;
                }
                try {
                    if (!SurveyActivity.this.isFinishing() && SurveyActivity.this.f1369a) {
                        C0769c c0769c = (C0769c) SurveyActivity.this.getIntent().getSerializableExtra("survey");
                        if (bundle == null) {
                            if (c0769c.m1964k() == null || String.valueOf(c0769c.m1964k()).equals("null")) {
                                SurveyActivity.this.m2039a(c0769c);
                            } else {
                                SurveyActivity.this.m2043b(c0769c);
                            }
                        }
                    }
                } catch (Exception e) {
                    InstabugSDKLogger.m1800e(SurveyActivity.class, "Survey has not been shown due to this error: " + e.getMessage());
                }
            }
        }, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m2039a(C0769c c0769c) {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(C0764R.anim.instabug_anim_flyin_from_bottom, C0764R.anim.instabug_anim_flyout_to_bottom).replace(C0764R.id.instabug_fragment_container, ViewOnClickListenerC0801a.m2108a(c0769c)).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m2043b(C0769c c0769c) {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(C0764R.anim.instabug_anim_flyin_from_bottom, C0764R.anim.instabug_anim_flyout_to_bottom).replace(C0764R.id.instabug_fragment_container, ViewOnClickListenerC0794c.m2074a(c0769c)).commit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.f1369a = true;
        C0766a.m1901a(this).m1914a(true);
        SurveyPlugin surveyPlugin = (SurveyPlugin) InstabugCore.getXPlugin(SurveyPlugin.class);
        if (surveyPlugin != null) {
            surveyPlugin.setState(1);
        }
        m2042b();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.f1369a = false;
        super.onPause();
        if (this.f1370b != null) {
            this.f1370b.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0764R.layout.instabug_activity;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, com.instabug.library.core.ui.BaseContract.View
    public void finishActivity() {
        if (getSupportFragmentManager().findFragmentById(C0764R.id.instabug_fragment_container) != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(0, C0764R.anim.instabug_anim_flyout_to_bottom).remove(getSupportFragmentManager().findFragmentById(C0764R.id.instabug_fragment_container)).commit();
        }
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.SurveyActivity.2
            @Override // java.lang.Runnable
            public void run() {
                SurveyActivity.this.finish();
            }
        }, 400L);
    }

    /* renamed from: a */
    public void m2046a() {
        if (getSupportFragmentManager().findFragmentById(C0764R.id.instabug_fragment_container) != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(0, C0764R.anim.instabug_anim_flyout_to_bottom).remove(getSupportFragmentManager().findFragmentById(C0764R.id.instabug_fragment_container)).commit();
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(C0764R.anim.instabug_anim_pop_in, C0764R.anim.instabug_anim_pop_out).replace(C0764R.id.instabug_fragment_container, C0800a.m2107a(), "THANKS_FRAGMENT").commit();
        m2045c();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m2042b() {
        if (getSupportFragmentManager().findFragmentByTag("THANKS_FRAGMENT") != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(0, C0764R.anim.instabug_anim_flyout_to_bottom).remove(getSupportFragmentManager().findFragmentByTag("THANKS_FRAGMENT")).commit();
            new Handler().postDelayed(new Runnable() { // from class: com.instabug.survey.ui.SurveyActivity.3
                @Override // java.lang.Runnable
                public void run() {
                    SurveyActivity.this.finish();
                    C0777e.m1996c();
                }
            }, 400L);
        }
    }

    /* renamed from: c */
    private void m2045c() {
        this.f1370b = new Handler();
        this.f1370b.postDelayed(new Runnable() { // from class: com.instabug.survey.ui.SurveyActivity.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    SurveyActivity.this.m2042b();
                } catch (Exception e) {
                    SurveyActivity.this.getSupportFragmentManager().popBackStack();
                    SurveyActivity.this.finish();
                    InstabugSDKLogger.m1800e(SurveyActivity.this, "ThanksFragment couldn't save it's state");
                }
            }
        }, 3000L);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        C0766a.m1901a(this).m1914a(false);
        SurveyPlugin surveyPlugin = (SurveyPlugin) InstabugCore.getXPlugin(SurveyPlugin.class);
        if (surveyPlugin != null) {
            surveyPlugin.setState(0);
        }
    }
}
