package com.instabug.survey.p036ui.p037a;

import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p033b.C0771b;
import com.instabug.survey.p036ui.p037a.InterfaceC0796d;

/* compiled from: SurveyFragmentPresenter.java */
/* renamed from: com.instabug.survey.ui.a.e */
/* loaded from: classes.dex */
public class C0798e extends BasePresenter<InterfaceC0796d.b> implements InterfaceC0796d.a {

    /* renamed from: a */
    private C0769c f1416a;

    public C0798e(InterfaceC0796d.b bVar, C0769c c0769c) {
        super(bVar);
        this.f1416a = c0769c;
    }

    /* renamed from: a */
    public void m2100a() {
        ((InterfaceC0796d.b) this.view.get()).mo2088b(this.f1416a);
    }

    /* renamed from: a */
    public void m2101a(C0769c c0769c) {
        c0769c.m1947a(true);
        SurveysCacheManager.addSurvey(c0769c);
        SurveysCacheManager.saveCacheToDisk();
        C0771b.m1974a().m1976a(System.currentTimeMillis());
        ((InterfaceC0796d.b) this.view.get()).mo2090c();
    }

    /* renamed from: b */
    public void m2102b() {
        InterfaceC0796d.b bVar;
        if (this.view != null && (bVar = (InterfaceC0796d.b) this.view.get()) != null) {
            if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.ENABLED) {
                bVar.mo2087b();
            } else {
                bVar.mo2084a();
            }
        }
    }

    /* renamed from: b */
    public void m2103b(C0769c c0769c) {
        c0769c.m1962i();
        SurveysCacheManager.addSurvey(c0769c);
        SurveysCacheManager.saveCacheToDisk();
    }
}
