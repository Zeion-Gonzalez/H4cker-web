package com.instabug.bug.settings;

import android.net.Uri;
import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.C0471a;
import com.instabug.bug.model.ReportCategory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/* compiled from: PerSessionSettings.java */
/* renamed from: com.instabug.bug.settings.b */
/* loaded from: classes.dex */
public class C0483b {

    /* renamed from: a */
    private static C0483b f153a;

    /* renamed from: d */
    private List<ReportCategory> f156d;

    /* renamed from: f */
    private OnSdkDismissedCallback f158f;

    /* renamed from: h */
    private Runnable f160h;

    /* renamed from: i */
    private String f161i;

    /* renamed from: k */
    private ExtendedBugReport.State f163k;

    /* renamed from: g */
    private boolean f159g = true;

    /* renamed from: b */
    private boolean f154b = false;

    /* renamed from: c */
    private AttachmentsTypesParams f155c = new AttachmentsTypesParams();

    /* renamed from: e */
    private LinkedHashMap<Uri, String> f157e = new LinkedHashMap<>(3);

    /* renamed from: j */
    private List<C0471a> f162j = new ArrayList();

    private C0483b() {
    }

    /* renamed from: a */
    public static void m271a() {
        f153a = new C0483b();
    }

    /* renamed from: b */
    public static C0483b m272b() {
        return f153a;
    }

    /* renamed from: c */
    public List<ReportCategory> m282c() {
        return this.f156d;
    }

    /* renamed from: a */
    public void m279a(List<ReportCategory> list) {
        this.f156d = list;
    }

    /* renamed from: d */
    public AttachmentsTypesParams m283d() {
        return this.f155c;
    }

    /* renamed from: a */
    public C0483b m273a(AttachmentsTypesParams attachmentsTypesParams) {
        this.f155c = attachmentsTypesParams;
        return this;
    }

    /* renamed from: e */
    public OnSdkDismissedCallback m284e() {
        return this.f158f;
    }

    /* renamed from: a */
    public void m274a(OnSdkDismissedCallback onSdkDismissedCallback) {
        this.f158f = onSdkDismissedCallback;
    }

    /* renamed from: f */
    public boolean m285f() {
        return this.f154b;
    }

    /* renamed from: a */
    public void m280a(boolean z) {
        this.f154b = z;
    }

    /* renamed from: b */
    public void m281b(boolean z) {
        this.f159g = z;
    }

    /* renamed from: g */
    public boolean m286g() {
        return this.f159g;
    }

    /* renamed from: h */
    public Runnable m287h() {
        return this.f160h;
    }

    /* renamed from: a */
    public void m277a(Runnable runnable) {
        this.f160h = runnable;
    }

    /* renamed from: a */
    public void m278a(String str) {
        this.f161i = str;
    }

    /* renamed from: i */
    public String m288i() {
        return this.f161i;
    }

    /* renamed from: a */
    public void m276a(CharSequence charSequence, boolean z) {
        this.f162j.add(new C0471a(charSequence, z));
    }

    /* renamed from: j */
    public List<C0471a> m289j() {
        return this.f162j;
    }

    /* renamed from: k */
    public void m290k() {
        this.f162j.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m275a(ExtendedBugReport.State state) {
        this.f163k = state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: l */
    public ExtendedBugReport.State m291l() {
        return this.f163k == null ? ExtendedBugReport.State.DISABLED : this.f163k;
    }
}
