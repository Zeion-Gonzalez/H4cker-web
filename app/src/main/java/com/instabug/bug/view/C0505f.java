package com.instabug.bug.view;

import com.instabug.bug.view.InterfaceC0497b;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BasePresenter;

/* compiled from: BugReportingPresenter.java */
/* renamed from: com.instabug.bug.view.f */
/* loaded from: classes.dex */
public class C0505f extends BasePresenter<InterfaceC0497b.b> implements InterfaceC0497b.a {
    public C0505f(InterfaceC0497b.b bVar) {
        super(bVar);
    }

    /* renamed from: a */
    public void m443a(int i) {
        InterfaceC0497b.b bVar;
        if (this.view != null && (bVar = (InterfaceC0497b.b) this.view.get()) != null) {
            switch (i) {
                case 161:
                    bVar.mo317b();
                    return;
                case 162:
                    bVar.mo314a();
                    return;
                case 163:
                case 164:
                case 165:
                case 166:
                case 168:
                default:
                    return;
                case 167:
                    bVar.mo319d();
                    return;
                case 169:
                    bVar.finishActivity();
                    return;
            }
        }
    }

    /* renamed from: a */
    public void m442a() {
        InterfaceC0497b.b bVar;
        if (this.view != null && (bVar = (InterfaceC0497b.b) this.view.get()) != null) {
            if (InstabugCore.getFeatureState(Feature.WHITE_LABELING) == Feature.State.ENABLED) {
                bVar.mo316a(false);
            } else {
                bVar.mo316a(true);
            }
        }
    }
}
