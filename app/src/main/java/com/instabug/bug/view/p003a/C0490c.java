package com.instabug.bug.view.p003a;

import android.net.Uri;
import com.instabug.bug.view.p003a.C0488a;
import com.instabug.library.C0577R;
import com.instabug.library.core.p024ui.BasePresenter;

/* compiled from: AnnotationPresenter.java */
/* renamed from: com.instabug.bug.view.a.c */
/* loaded from: classes.dex */
class C0490c extends BasePresenter<C0488a.b> implements C0488a.a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public C0490c(C0488a.b bVar) {
        super(bVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m343a(int i, Uri uri) {
        C0488a.b bVar;
        if (this.view != null && (bVar = (C0488a.b) this.view.get()) != null) {
            if (i == 0) {
                bVar.mo339a(C0577R.drawable.instabug_ic_next);
            } else {
                bVar.mo339a(C0577R.drawable.instabug_ic_check);
                bVar.mo341b(0);
            }
            if (uri != null) {
                bVar.mo340a(uri);
            } else {
                bVar.mo338a();
            }
        }
    }
}
