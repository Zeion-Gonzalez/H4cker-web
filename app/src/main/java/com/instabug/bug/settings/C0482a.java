package com.instabug.bug.settings;

import android.content.Context;
import android.support.annotation.Nullable;
import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.C0471a;
import com.instabug.bug.model.ReportCategory;
import com.instabug.library.core.InstabugCore;
import java.util.List;

/* compiled from: BugSettings.java */
/* renamed from: com.instabug.bug.settings.a */
/* loaded from: classes.dex */
public class C0482a {

    /* renamed from: a */
    private static C0482a f152a;

    private C0482a() {
    }

    /* renamed from: a */
    public static void m237a(Context context) {
        f152a = new C0482a();
        C0484c.m293a(context);
        C0483b.m271a();
    }

    /* renamed from: a */
    public static C0482a m236a() {
        if (f152a == null) {
            f152a = new C0482a();
        }
        return f152a;
    }

    /* renamed from: b */
    public List<ReportCategory> m247b() {
        return C0483b.m272b().m282c();
    }

    /* renamed from: a */
    public void m245a(List<ReportCategory> list) {
        C0483b.m272b().m279a(list);
    }

    @Nullable
    /* renamed from: c */
    public String m251c() {
        return C0484c.m292a().m304f();
    }

    /* renamed from: a */
    public void m244a(String str) {
        C0484c.m292a().m295a(str);
    }

    /* renamed from: d */
    public AttachmentsTypesParams m254d() {
        return C0483b.m272b().m283d();
    }

    /* renamed from: a */
    public void m241a(AttachmentsTypesParams attachmentsTypesParams) {
        C0483b.m272b().m273a(attachmentsTypesParams);
    }

    /* renamed from: e */
    public boolean m257e() {
        return C0483b.m272b().m283d().isAllowTakeExtraScreenshot() || C0483b.m272b().m283d().isAllowAttachImageFromGallery() || C0483b.m272b().m283d().isAllowScreenRecording();
    }

    /* renamed from: f */
    public boolean m258f() {
        return C0484c.m292a().m299b();
    }

    /* renamed from: a */
    public void m246a(boolean z) {
        C0484c.m292a().m296a(z);
    }

    /* renamed from: g */
    public Runnable m259g() {
        return C0483b.m272b().m287h();
    }

    /* renamed from: a */
    public void m243a(Runnable runnable) {
        C0483b.m272b().m277a(runnable);
    }

    /* renamed from: b */
    public void m249b(String str) {
        InstabugCore.setUserEmail(str);
    }

    /* renamed from: h */
    public OnSdkDismissedCallback m260h() {
        return C0483b.m272b().m284e();
    }

    /* renamed from: i */
    public boolean m261i() {
        return C0484c.m292a().m301c();
    }

    /* renamed from: a */
    public void m239a(OnSdkDismissedCallback onSdkDismissedCallback) {
        C0483b.m272b().m274a(onSdkDismissedCallback);
    }

    /* renamed from: j */
    public boolean m262j() {
        return C0483b.m272b().m285f();
    }

    /* renamed from: b */
    public void m250b(boolean z) {
        C0483b.m272b().m280a(z);
    }

    /* renamed from: c */
    public void m253c(boolean z) {
        C0484c.m292a().m298b(z);
    }

    /* renamed from: d */
    public void m255d(boolean z) {
        C0483b.m272b().m281b(z);
    }

    /* renamed from: k */
    public boolean m263k() {
        return C0483b.m272b().m286g();
    }

    /* renamed from: l */
    public long m264l() {
        return C0484c.m292a().m302d();
    }

    /* renamed from: a */
    public void m238a(long j) {
        C0484c.m292a().m294a(j);
    }

    /* renamed from: m */
    public boolean m265m() {
        return C0484c.m292a().m303e();
    }

    /* renamed from: e */
    public void m256e(boolean z) {
        C0484c.m292a().m300c(z);
    }

    /* renamed from: c */
    public void m252c(String str) {
        C0483b.m272b().m278a(str);
    }

    /* renamed from: n */
    public String m266n() {
        return C0483b.m272b().m288i();
    }

    /* renamed from: a */
    public void m242a(CharSequence charSequence, boolean z) {
        C0483b.m272b().m276a(charSequence, z);
    }

    /* renamed from: o */
    public void m267o() {
        C0483b.m272b().m290k();
    }

    /* renamed from: p */
    public List<C0471a> m268p() {
        return C0483b.m272b().m289j();
    }

    /* renamed from: a */
    public void m240a(ExtendedBugReport.State state) {
        C0483b.m272b().m275a(state);
    }

    /* renamed from: q */
    public ExtendedBugReport.State m269q() {
        return C0483b.m272b().m291l();
    }

    /* renamed from: r */
    public long m270r() {
        return C0484c.m292a().m305g();
    }

    /* renamed from: b */
    public void m248b(long j) {
        C0484c.m292a().m297b(j);
    }
}
