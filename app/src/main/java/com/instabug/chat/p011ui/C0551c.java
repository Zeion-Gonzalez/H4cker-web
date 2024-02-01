package com.instabug.chat.p011ui;

import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.p011ui.C0544b;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.bugreporting.model.Bug;
import com.instabug.library.core.p024ui.BasePresenter;

/* compiled from: ChatPresenter.java */
/* renamed from: com.instabug.chat.ui.c */
/* loaded from: classes.dex */
public class C0551c extends BasePresenter<C0544b.b> implements C0544b.a {
    public C0551c(C0544b.b bVar) {
        super(bVar);
    }

    @Override // com.instabug.chat.p011ui.C0544b.a
    /* renamed from: a */
    public void mo795a(int i) {
        C0544b.b bVar;
        C0544b.b bVar2;
        if (i == 160) {
            m883c();
            return;
        }
        if (i == 161) {
            if (this.view != null && (bVar2 = (C0544b.b) this.view.get()) != null) {
                m882b(bVar2.mo782b());
                return;
            }
            return;
        }
        if (i == 162) {
            mo794a();
        } else if (i == 164 && this.view != null && (bVar = (C0544b.b) this.view.get()) != null) {
            m881a(bVar.mo782b(), bVar.mo784c());
        }
    }

    @Override // com.instabug.chat.p011ui.C0544b.a
    /* renamed from: a */
    public void mo796a(String str) {
        m882b(str);
    }

    @Override // com.instabug.chat.p011ui.C0544b.a
    /* renamed from: a */
    public void mo794a() {
        m882b(m885e().getId());
    }

    @Override // com.instabug.chat.p011ui.C0544b.a
    /* renamed from: b */
    public void mo797b() {
        OnSdkDismissedCallback m746k = C0537a.m746k();
        if (m746k != null) {
            m746k.onSdkDismissed(OnSdkDismissedCallback.DismissType.CANCEL, Bug.Type.NOT_AVAILABLE);
        }
    }

    /* renamed from: c */
    private void m883c() {
        C0544b.b bVar;
        if (m884d()) {
            if (this.view != null && (bVar = (C0544b.b) this.view.get()) != null) {
                bVar.mo779a();
                return;
            }
            return;
        }
        mo794a();
    }

    /* renamed from: b */
    private void m882b(String str) {
        C0544b.b bVar = null;
        if (this.view != null) {
            bVar = (C0544b.b) this.view.get();
        }
        if (m884d() && bVar != null) {
            bVar.mo779a();
        }
        if (bVar != null) {
            bVar.mo780a(str);
        }
    }

    /* renamed from: a */
    private void m881a(String str, Attachment attachment) {
        C0544b.b bVar = null;
        if (this.view != null) {
            bVar = (C0544b.b) this.view.get();
        }
        if (m884d() && bVar != null) {
            bVar.mo779a();
        }
        if (bVar != null) {
            bVar.mo781a(str, attachment);
        }
    }

    /* renamed from: d */
    private boolean m884d() {
        return ChatsCacheManager.getValidChats().size() > 0;
    }

    /* renamed from: e */
    private Chat m885e() {
        C0544b.b bVar;
        if (this.view == null || (bVar = (C0544b.b) this.view.get()) == null) {
            return null;
        }
        return ChatsCacheManager.addOfflineChat(bVar.getViewContext());
    }
}
