package com.instabug.library.tracking;

import com.instabug.library.model.C0718c;
import com.instabug.library.model.C0719d;
import com.instabug.library.util.InstabugDateFormatter;
import java.util.ArrayList;

/* compiled from: InstabugTrackingStepsProvider.java */
/* renamed from: com.instabug.library.tracking.d */
/* loaded from: classes.dex */
public class C0741d {

    /* renamed from: a */
    private static C0741d f1227a;

    /* renamed from: c */
    private String f1229c = "";

    /* renamed from: b */
    private ArrayList<C0719d> f1228b = new ArrayList<>(100);

    private C0741d() {
    }

    /* renamed from: a */
    public static C0741d m1746a() {
        if (f1227a == null) {
            f1227a = new C0741d();
        }
        return f1227a;
    }

    /* renamed from: a */
    public void m1751a(String str, C0719d.a aVar) {
        this.f1229c = str;
        m1750d();
        this.f1228b.add(m1748b(str, aVar));
    }

    /* renamed from: a */
    public void m1752a(String str, String str2, C0719d.a aVar) {
        this.f1229c = str;
        m1750d();
        this.f1228b.add(m1749b(str, str2, aVar));
    }

    /* renamed from: a */
    public void m1754a(String str, String str2, String str3, C0719d.a aVar) {
        this.f1229c = str;
        C0719d m1749b = m1749b(str, str2, aVar);
        m1749b.m1615e(str3);
        m1750d();
        this.f1228b.add(m1749b);
    }

    /* renamed from: d */
    private void m1750d() {
        if (this.f1228b.size() == 100) {
            this.f1228b.remove(0);
        }
    }

    /* renamed from: b */
    private C0719d m1749b(String str, String str2, C0719d.a aVar) {
        C0719d m1748b = m1748b(str, aVar);
        m1748b.m1609b(str2);
        return m1748b;
    }

    /* renamed from: b */
    private C0719d m1748b(String str, C0719d.a aVar) {
        C0719d c0719d = new C0719d();
        c0719d.m1606a(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds());
        c0719d.m1607a(aVar);
        c0719d.m1608a(str);
        return c0719d;
    }

    /* renamed from: a */
    public void m1753a(String str, String str2, String str3) {
        C0719d c0719d = new C0719d();
        c0719d.m1607a(C0719d.a.TAP);
        c0719d.m1608a(str);
        c0719d.m1606a(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds());
        if (str2 != null) {
            c0719d.m1611c(str2);
        }
        if (str3 != null) {
            c0719d.m1613d(str3);
        }
        m1750d();
        this.f1228b.add(c0719d);
    }

    /* renamed from: b */
    public String m1755b() {
        return this.f1229c;
    }

    /* renamed from: c */
    public ArrayList<C0718c> m1756c() {
        ArrayList<C0718c> arrayList = new ArrayList<>();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.f1228b.size()) {
                C0718c c0718c = new C0718c();
                c0718c.m1602a(m1747a(this.f1228b.get(i2)));
                c0718c.m1599a(this.f1228b.get(i2).m1605a());
                c0718c.m1601a(this.f1228b.get(i2).m1617f());
                arrayList.add(c0718c);
                i = i2 + 1;
            } else {
                return arrayList;
            }
        }
    }

    /* renamed from: a */
    private String m1747a(C0719d c0719d) {
        StringBuilder sb = new StringBuilder();
        switch (c0719d.m1617f()) {
            case TAP:
                sb.append("In activity ");
                sb.append(c0719d.m1610b());
                sb.append(": ");
                if (c0719d.m1614d() != null) {
                    sb.append("View(");
                    sb.append(c0719d.m1614d());
                    sb.append(")");
                } else {
                    sb.append("View");
                }
                sb.append(" of type ");
                sb.append(c0719d.m1616e());
                sb.append(" received a click event");
                break;
            case SHAKE:
                sb.append("In activity ");
                sb.append(c0719d.m1610b());
                sb.append(": the user shook the phone");
                break;
            case APPLICATION_CREATED:
                sb.append(c0719d.m1610b());
                sb.append(" was created.");
                break;
            case ACTIVITY_CREATED:
                sb.append(c0719d.m1610b());
                sb.append(" was created.");
                break;
            case ACTIVITY_STARTED:
                sb.append(c0719d.m1610b());
                sb.append(" was started.");
                break;
            case ACTIVITY_RESUMED:
                sb.append(c0719d.m1610b());
                sb.append(" was resumed.");
                break;
            case ACTIVITY_PAUSED:
                sb.append(c0719d.m1610b());
                sb.append(" was paused.");
                break;
            case ACTIVITY_STOPPED:
                sb.append(c0719d.m1610b());
                sb.append(" was stopped.");
                break;
            case ACTIVITY_DESTROYED:
                sb.append(c0719d.m1610b());
                sb.append(" was destroyed.");
                break;
            case OPEN_DIALOG:
                sb.append("In container ");
                sb.append(c0719d.m1612c());
                sb.append(": dialog ");
                sb.append(c0719d.m1610b());
                sb.append(" was displayed.");
                break;
            case FRAGMENT_ATTACHED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was attached.");
                break;
            case FRAGMENT_VIEW_CREATED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was created.");
                break;
            case FRAGMENT_STARTED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was started.");
                break;
            case FRAGMENT_RESUMED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was resumed.");
                break;
            case FRAGMENT_PAUSED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was paused.");
                break;
            case FRAGMENT_STOPPED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was stopped.");
                break;
            case FRAGMENT_DETACHED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" was detached.");
                break;
            case FRAGMENT_VISIBILITY_CHANGED:
                sb.append("In activity ");
                sb.append(c0719d.m1612c());
                sb.append(": visibility of fragment ");
                sb.append(c0719d.m1610b());
                sb.append(" changed, ");
                sb.append(c0719d.m1618g());
                sb.append(".");
                break;
        }
        return sb.toString();
    }
}
