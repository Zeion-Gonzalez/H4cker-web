package com.instabug.chat.p011ui.p012a;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.chat.p011ui.p012a.C0541a;
import com.instabug.library.core.p024ui.BasePresenter;
import com.instabug.library.util.BitmapUtils;

/* compiled from: AnnotationPresenter.java */
/* renamed from: com.instabug.chat.ui.a.c */
/* loaded from: classes.dex */
class C0543c extends BasePresenter<C0541a.b> implements C0541a.a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public C0543c(C0541a.b bVar) {
        super(bVar);
    }

    @Override // com.instabug.chat.p011ui.p012a.C0541a.a
    /* renamed from: a */
    public void mo790a(@Nullable Bitmap bitmap, Uri uri) {
        C0541a.b bVar;
        if (this.view != null && (bVar = (C0541a.b) this.view.get()) != null && bitmap != null) {
            BitmapUtils.saveBitmap(bitmap, uri, bVar.getViewContext().getContext());
        }
    }
}
