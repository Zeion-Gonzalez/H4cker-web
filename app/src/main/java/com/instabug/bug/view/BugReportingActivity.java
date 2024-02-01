package com.instabug.bug.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.instabug.bug.BugPlugin;
import com.instabug.bug.C0458R;
import com.instabug.bug.C0464c;
import com.instabug.bug.C0468d;
import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.bug.model.EnumC0472b;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.InterfaceC0497b;
import com.instabug.bug.view.ViewOnClickListenerC0499c;
import com.instabug.bug.view.p003a.C0489b;
import com.instabug.library.FragmentVisibilityChangedListener;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.InstabugState;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.VideoProcessingServiceEventBus;
import com.instabug.library.core.p024ui.BaseFragmentActivity;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.video.VideoProcessingService;
import com.instabug.library.model.Attachment;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BugReportingActivity extends BaseFragmentActivity<C0505f> implements FragmentManager.OnBackStackChangedListener, View.OnClickListener, C0489b.a, InterfaceC0497b.b, ViewOnClickListenerC0499c.a, _InstabugActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.presenter = new C0505f(this);
        ((C0505f) this.presenter).m443a(m313a(intent.getIntExtra("com.instabug.library.process", 162)));
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OrientationUtils.handelOrientation(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        this.presenter = new C0505f(this);
        int intExtra = getIntent().getIntExtra("com.instabug.library.process", 162);
        if (bundle == null) {
            ((C0505f) this.presenter).m443a(m313a(intExtra));
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null) {
            bugPlugin.setState(1);
        }
        InstabugSDKLogger.m1799d(this, "onStart(),  SDK Invoking State Changed: true");
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        BugPlugin bugPlugin = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (bugPlugin != null && bugPlugin.getState() != 2) {
            bugPlugin.setState(0);
        }
        InstabugSDKLogger.m1799d(this, "onPause(),  SDK Invoking State Changed: false");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        if (!C0468d.m86a().m105f() && C0468d.m86a().m104e() == OnSdkDismissedCallback.DismissType.ADD_ATTACHMENT) {
            C0468d.m86a().m96a(OnSdkDismissedCallback.DismissType.CANCEL);
        }
        OrientationUtils.unlockOrientation(this);
        super.onDestroy();
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0458R.layout.instabug_activity_bug_reporting;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        InstabugSDKLogger.m1799d(this, "onClick: " + view.getId());
        ArrayList arrayList = new ArrayList(getSupportFragmentManager().getFragments());
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(null);
        arrayList.removeAll(arrayList2);
        InstabugSDKLogger.m1803v(this, "Dark space clicked, fragments size is " + arrayList.size() + " fragments are " + arrayList);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            C0468d.m86a().m96a(OnSdkDismissedCallback.DismissType.CANCEL);
            InstabugSDKLogger.m1799d(this, "Reporting bug canceled. Deleting attachments");
            Cache cache = CacheManager.getInstance().getCache(CacheManager.DEFAULT_IN_MEMORY_CACHE_KEY);
            if (cache != null) {
                cache.delete("video.path");
            }
            VideoProcessingServiceEventBus.getInstance().post(VideoProcessingService.Action.STOP);
            C0464c.m72a();
        }
        if ((Instabug.getState() == InstabugState.TAKING_SCREENSHOT_FOR_CHAT || Instabug.getState() == InstabugState.IMPORTING_IMAGE_FROM_GALLERY_FOR_CHAT) && (getSupportFragmentManager().findFragmentById(C0458R.id.instabug_fragment_container) instanceof C0489b)) {
            Instabug.setState(InstabugState.ENABLED);
        }
        m309a(false, C0458R.id.instabug_fragment_container);
        super.onBackPressed();
    }

    @Override // android.support.v4.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        InstabugSDKLogger.m1803v(this, "Back stack changed, back stack size: " + getSupportFragmentManager().getBackStackEntryCount());
        m309a(true, C0458R.id.instabug_fragment_container);
    }

    @Override // com.instabug.bug.view.InterfaceC0497b.b
    /* renamed from: a */
    public void mo314a() {
        InstabugSDKLogger.m1803v(this, "startBugReporter");
        C0468d.m86a().m103d().m115a(EnumC0472b.BUG);
        String m124b = C0468d.m86a().m103d().m124b();
        if (!C0468d.m86a().m103d().m137j() && m124b != null) {
            C0468d.m86a().m103d().m112a(Uri.parse(m124b), Attachment.Type.MAIN_SCREENSHOT);
        }
        m309a(false, C0458R.id.instabug_fragment_container);
        if (C0482a.m236a().m262j()) {
            m306a(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0499c.m366a(EnumC0472b.BUG, C0468d.m86a().m103d().m129d(), m310b(162)), "feedback", false);
        } else if (m124b != null) {
            m306a(C0458R.id.instabug_fragment_container, C0489b.m342a(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.BUG_REPORT_HEADER, getString(C0458R.string.instabug_str_bug_header)), Uri.parse(m124b), 0), "annotation", false);
        } else {
            m306a(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0499c.m366a(EnumC0472b.BUG, C0468d.m86a().m103d().m129d(), m310b(162)), "feedback", false);
        }
        ((C0505f) this.presenter).m442a();
    }

    @Override // com.instabug.bug.view.InterfaceC0497b.b
    /* renamed from: b */
    public void mo317b() {
        InstabugSDKLogger.m1803v(this, "startFeedbackSender");
        C0468d.m86a().m103d().m115a(EnumC0472b.FEEDBACK);
        String m124b = C0468d.m86a().m103d().m124b();
        if (!C0468d.m86a().m103d().m137j() && m124b != null) {
            C0468d.m86a().m103d().m112a(Uri.parse(m124b), Attachment.Type.MAIN_SCREENSHOT);
        }
        m309a(false, C0458R.id.instabug_fragment_container);
        m306a(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0499c.m366a(EnumC0472b.FEEDBACK, C0468d.m86a().m103d().m129d(), m310b(161)), "feedback", false);
        ((C0505f) this.presenter).m442a();
    }

    @Override // com.instabug.bug.view.p003a.C0489b.a
    /* renamed from: a */
    public void mo315a(@Nullable Bitmap bitmap, Uri uri) {
        InstabugSDKLogger.m1803v(this, "onImageEditingDone");
        if (bitmap != null) {
            BitmapUtils.saveBitmap(bitmap, uri, this);
        }
        m309a(false, C0458R.id.instabug_fragment_container);
        m308a("annotation");
        m312f();
        if (getSupportFragmentManager().findFragmentByTag("feedback") == null) {
            InstabugSDKLogger.m1803v(this, "Instabug Feedback fragment equal null");
            m306a(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0499c.m366a(C0468d.m86a().m103d().m126c(), C0468d.m86a().m103d().m129d(), m310b(C0468d.m86a().m103d().m126c() == EnumC0472b.BUG ? 162 : 161)), "feedback", false);
        }
        C0468d.m86a().m100b(this);
    }

    @Override // com.instabug.bug.view.p003a.C0489b.a
    /* renamed from: c */
    public void mo318c() {
    }

    @Override // com.instabug.bug.view.InterfaceC0497b.b
    /* renamed from: d */
    public void mo319d() {
        InstabugSDKLogger.m1803v(this, "startWithHangingBug");
        InstabugSDKLogger.m1803v(this, "bug attachment size): " + C0468d.m86a().m103d().m131e().size());
        C0468d.m86a().m98a(false);
        if (getSupportFragmentManager().findFragmentByTag("feedback") == null) {
            m309a(false, C0458R.id.instabug_fragment_container);
            m311b(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0499c.m366a(C0468d.m86a().m103d().m126c(), C0468d.m86a().m103d().m129d(), m310b(C0468d.m86a().m103d().m126c() == EnumC0472b.BUG ? 162 : 161)), "feedback", false);
        }
        C0468d.m86a().m100b(this);
        ((C0505f) this.presenter).m442a();
    }

    /* renamed from: a */
    private void m309a(boolean z, int i) {
        if (getSupportFragmentManager().findFragmentById(i) instanceof FragmentVisibilityChangedListener) {
            ((FragmentVisibilityChangedListener) getSupportFragmentManager().findFragmentById(i)).onVisibilityChanged(z);
        }
    }

    /* renamed from: b */
    private String m310b(int i) {
        if (i == 161) {
            return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.COMMENT_FIELD_HINT_FOR_FEEDBACK, getString(C0458R.string.instabug_str_feedback_comment_hint));
        }
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.COMMENT_FIELD_HINT_FOR_BUG_REPORT, getString(C0458R.string.instabug_str_bug_comment_hint));
    }

    @Override // com.instabug.bug.view.InterfaceC0497b.b
    /* renamed from: a */
    public void mo316a(boolean z) {
        findViewById(C0458R.id.instabug_pbi_footer).setVisibility(z ? 0 : 8);
    }

    @Override // com.instabug.bug.view.ViewOnClickListenerC0499c.a
    /* renamed from: e */
    public void mo320e() {
        Cache cache = CacheManager.getInstance().getCache(CacheManager.DEFAULT_IN_MEMORY_CACHE_KEY);
        if (cache != null) {
            cache.delete("video.path");
        }
        finish();
    }

    /* renamed from: a */
    public int m313a(int i) {
        switch (i) {
            case 161:
                return 161;
            case 167:
                return 167;
            case 169:
                return 169;
            default:
                return 162;
        }
    }

    /* renamed from: a */
    private void m306a(@IdRes int i, Fragment fragment, @Nullable String str, boolean z) {
        FragmentTransaction replace = getSupportFragmentManager().beginTransaction().replace(i, fragment, str);
        if (z) {
            replace.addToBackStack(str);
        }
        replace.commit();
    }

    /* renamed from: b */
    private void m311b(@IdRes int i, Fragment fragment, @Nullable String str, boolean z) {
        FragmentTransaction add = getSupportFragmentManager().beginTransaction().add(i, fragment, str);
        if (z) {
            add.addToBackStack(str);
        }
        add.commit();
    }

    /* renamed from: a */
    private void m308a(@NonNull String str) {
        m307a(getSupportFragmentManager().findFragmentByTag(str));
    }

    /* renamed from: a */
    private void m307a(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    /* renamed from: f */
    private void m312f() {
        getSupportFragmentManager().popBackStack();
    }
}
