package com.instabug.library.p031ui.promptoptions;

import android.os.Handler;
import com.instabug.library.C0577R;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.p031ui.promptoptions.InterfaceC0747b;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.PlaceHolderUtils;

/* compiled from: PromptOptionsPresenter.java */
/* renamed from: com.instabug.library.ui.promptoptions.d */
/* loaded from: classes.dex */
public class C0749d extends BasePresenter<InterfaceC0747b.b> implements InterfaceC0747b.a {

    /* renamed from: a */
    private Handler f1245a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0749d(InterfaceC0747b.b bVar) {
        super(bVar);
    }

    /* renamed from: a */
    public void m1775a() {
        InterfaceC0747b.b bVar;
        if (this.view != null && (bVar = (InterfaceC0747b.b) this.view.get()) != null) {
            m1773g();
            bVar.mo1764a();
            if (m1774h()) {
                bVar.mo1766b();
            } else {
                bVar.mo1767c();
            }
        }
        if (C0704b.m1513c().m1523d() == InstabugInvocationEvent.SHAKE) {
            m1771e();
        }
    }

    /* renamed from: b */
    public void m1776b() {
        m1772f();
    }

    /* renamed from: e */
    private void m1771e() {
        this.f1245a = new Handler();
        this.f1245a.postDelayed(new Runnable() { // from class: com.instabug.library.ui.promptoptions.d.1
            @Override // java.lang.Runnable
            public void run() {
                ((InterfaceC0747b.b) C0749d.this.view.get()).finishActivity();
            }
        }, 10000L);
    }

    /* renamed from: f */
    private void m1772f() {
        if (this.f1245a != null) {
            this.f1245a.removeCallbacksAndMessages(null);
        }
    }

    /* renamed from: c */
    public void m1777c() {
        m1772f();
    }

    /* renamed from: d */
    public void m1778d() {
        if (SettingsManager.getInstance().getPreInvocationRunnable() != null) {
            SettingsManager.getInstance().getPreInvocationRunnable().run();
        }
    }

    /* renamed from: g */
    private void m1773g() {
        InterfaceC0747b.b bVar;
        if (this.view != null && (bVar = (InterfaceC0747b.b) this.view.get()) != null) {
            bVar.mo1765a(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.INVOCATION_HEADER, bVar.getViewContext().getContext().getString(C0577R.string.instabug_str_invocation_dialog_title)));
        }
    }

    /* renamed from: h */
    private boolean m1774h() {
        return C0629b.m1160a().m1170b(Feature.WHITE_LABELING) != Feature.State.ENABLED;
    }
}
