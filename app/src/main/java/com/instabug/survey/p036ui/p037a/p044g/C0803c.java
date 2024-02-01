package com.instabug.survey.p036ui.p037a.p044g;

import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.survey.p036ui.p037a.p044g.InterfaceC0802b;

/* compiled from: WelcomeFragmentPresenter.java */
/* renamed from: com.instabug.survey.ui.a.g.c */
/* loaded from: classes.dex */
class C0803c extends BasePresenter<InterfaceC0802b.b> implements InterfaceC0802b.a {
    public C0803c(InterfaceC0802b.b bVar) {
        super(bVar);
    }

    /* renamed from: a */
    public void m2113a() {
        InterfaceC0802b.b bVar;
        if (this.view != null && (bVar = (InterfaceC0802b.b) this.view.get()) != null) {
            if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.ENABLED) {
                bVar.mo2109a();
            } else {
                bVar.mo2110b();
            }
        }
    }
}
