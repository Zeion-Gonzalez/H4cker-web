package com.instabug.library.settings;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.OnSdkDismissedCallback;
import com.instabug.library.OnSdkInvokedCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;

/* compiled from: PerSessionSettings.java */
/* renamed from: com.instabug.library.settings.a */
/* loaded from: classes.dex */
public class C0736a {

    /* renamed from: a */
    private static C0736a f1200a;

    /* renamed from: d */
    private InstabugCustomTextPlaceHolder f1203d;

    /* renamed from: e */
    private int f1204e;

    /* renamed from: f */
    private int f1205f;

    /* renamed from: h */
    private long f1207h;

    /* renamed from: i */
    private Runnable f1208i;

    /* renamed from: j */
    private Runnable f1209j;

    /* renamed from: k */
    private boolean f1210k;

    /* renamed from: l */
    private OnSdkDismissedCallback f1211l;

    /* renamed from: m */
    private OnSdkInvokedCallback f1212m;

    /* renamed from: b */
    private Locale f1201b = null;

    /* renamed from: n */
    private boolean f1213n = false;

    /* renamed from: o */
    private int f1214o = -2;

    /* renamed from: p */
    private boolean f1215p = false;

    /* renamed from: q */
    private boolean f1216q = false;

    /* renamed from: r */
    private boolean f1217r = false;

    /* renamed from: s */
    private boolean f1218s = false;

    /* renamed from: t */
    private int f1219t = 30000;

    /* renamed from: u */
    private boolean f1220u = true;

    /* renamed from: c */
    private ArrayList<String> f1202c = new ArrayList<>();

    /* renamed from: g */
    private LinkedHashMap<Uri, String> f1206g = new LinkedHashMap<>(10);

    private C0736a() {
    }

    /* renamed from: a */
    public static void m1648a() {
        f1200a = new C0736a();
    }

    /* renamed from: b */
    public static C0736a m1649b() {
        return f1200a;
    }

    /* renamed from: c */
    public Runnable m1663c() {
        return this.f1208i;
    }

    /* renamed from: a */
    public void m1657a(Runnable runnable) {
        this.f1208i = runnable;
    }

    /* renamed from: d */
    public Runnable m1666d() {
        return this.f1209j;
    }

    /* renamed from: e */
    public OnSdkInvokedCallback m1669e() {
        return this.f1212m;
    }

    /* renamed from: a */
    public void m1656a(OnSdkInvokedCallback onSdkInvokedCallback) {
        this.f1212m = onSdkInvokedCallback;
    }

    /* renamed from: a */
    public Locale m1650a(Context context) {
        if (this.f1201b != null) {
            return this.f1201b;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            this.f1201b = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            this.f1201b = context.getResources().getConfiguration().locale;
        }
        return this.f1201b;
    }

    /* renamed from: a */
    public void m1658a(Locale locale) {
        this.f1201b = locale;
    }

    /* renamed from: f */
    public LinkedHashMap<Uri, String> m1671f() {
        return this.f1206g;
    }

    /* renamed from: a */
    public void m1653a(Uri uri, String str) {
        if (this.f1206g.size() == 10 && !this.f1206g.containsKey(uri)) {
            this.f1206g.remove(this.f1206g.keySet().iterator().next());
        }
        this.f1206g.put(uri, str);
    }

    /* renamed from: g */
    public void m1673g() {
        this.f1206g.clear();
    }

    /* renamed from: h */
    public ArrayList<String> m1675h() {
        return this.f1202c;
    }

    /* renamed from: a */
    public void m1660a(String... strArr) {
        Collections.addAll(this.f1202c, strArr);
    }

    /* renamed from: i */
    public void m1676i() {
        this.f1202c = new ArrayList<>();
    }

    /* renamed from: j */
    public long m1677j() {
        return this.f1207h;
    }

    /* renamed from: a */
    public void m1652a(long j) {
        this.f1207h = j;
    }

    /* renamed from: k */
    public int m1678k() {
        return this.f1204e;
    }

    /* renamed from: a */
    public void m1651a(@ColorInt int i) {
        this.f1204e = i;
    }

    /* renamed from: l */
    public boolean m1679l() {
        return this.f1213n;
    }

    /* renamed from: a */
    public void m1659a(boolean z) {
        this.f1213n = z;
    }

    /* renamed from: m */
    public InstabugCustomTextPlaceHolder m1680m() {
        return this.f1203d;
    }

    /* renamed from: a */
    public void m1654a(InstabugCustomTextPlaceHolder instabugCustomTextPlaceHolder) {
        this.f1203d = instabugCustomTextPlaceHolder;
    }

    /* renamed from: n */
    public int m1681n() {
        return this.f1205f;
    }

    /* renamed from: b */
    public void m1661b(int i) {
        this.f1205f = i;
    }

    /* renamed from: c */
    public void m1664c(int i) {
        this.f1214o = i;
    }

    /* renamed from: o */
    public int m1682o() {
        return this.f1214o;
    }

    /* renamed from: p */
    public void m1683p() {
        this.f1214o = -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: q */
    public boolean m1684q() {
        return this.f1210k;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public void m1662b(boolean z) {
        this.f1210k = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: r */
    public boolean m1685r() {
        return this.f1217r;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public void m1665c(boolean z) {
        this.f1217r = z;
    }

    /* renamed from: s */
    public OnSdkDismissedCallback m1686s() {
        return this.f1211l;
    }

    /* renamed from: a */
    public void m1655a(OnSdkDismissedCallback onSdkDismissedCallback) {
        this.f1211l = onSdkDismissedCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: d */
    public void m1668d(boolean z) {
        this.f1220u = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: t */
    public boolean m1687t() {
        return this.f1220u;
    }

    /* renamed from: u */
    public boolean m1688u() {
        return this.f1215p;
    }

    /* renamed from: e */
    public void m1670e(boolean z) {
        this.f1215p = z;
    }

    /* renamed from: v */
    public boolean m1689v() {
        return this.f1216q;
    }

    /* renamed from: f */
    public void m1672f(boolean z) {
        this.f1216q = z;
    }

    /* renamed from: w */
    public boolean m1690w() {
        return this.f1218s;
    }

    /* renamed from: g */
    public void m1674g(boolean z) {
        this.f1218s = z;
    }

    /* renamed from: d */
    public void m1667d(int i) {
        this.f1219t = i;
    }

    /* renamed from: x */
    public int m1691x() {
        return this.f1219t;
    }
}
