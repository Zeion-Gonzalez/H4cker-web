package com.instabug.bug.screenshot.viewhierarchy;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import java.util.ArrayList;
import org.json.JSONObject;

/* compiled from: ViewHierarchy.java */
/* renamed from: com.instabug.bug.screenshot.viewhierarchy.b */
/* loaded from: classes.dex */
public class C0479b {

    /* renamed from: a */
    private String f131a;

    /* renamed from: b */
    private String f132b;

    /* renamed from: c */
    private String f133c;

    /* renamed from: d */
    private JSONObject f134d;

    /* renamed from: e */
    private JSONObject f135e;

    /* renamed from: f */
    private C0479b f136f;

    /* renamed from: h */
    private boolean f138h;

    /* renamed from: i */
    private boolean f139i;

    /* renamed from: j */
    private Bitmap f140j;

    /* renamed from: k */
    private Uri f141k;

    /* renamed from: l */
    private Rect f142l;

    /* renamed from: m */
    private Rect f143m;

    /* renamed from: n */
    private View f144n;

    /* renamed from: g */
    private ArrayList<C0479b> f137g = new ArrayList<>();

    /* renamed from: o */
    private int f145o = 1;

    /* renamed from: a */
    public String m182a() {
        return this.f131a;
    }

    /* renamed from: a */
    public void m189a(String str) {
        this.f131a = str;
    }

    /* renamed from: b */
    public String m192b() {
        return this.f132b;
    }

    /* renamed from: b */
    public void m195b(String str) {
        this.f132b = str;
    }

    /* renamed from: c */
    public String m198c() {
        return this.f133c;
    }

    /* renamed from: c */
    public void m199c(String str) {
        this.f133c = str;
    }

    /* renamed from: d */
    public JSONObject m200d() {
        return this.f134d;
    }

    /* renamed from: a */
    public void m190a(JSONObject jSONObject) {
        this.f134d = jSONObject;
    }

    /* renamed from: e */
    public JSONObject m201e() {
        return this.f135e;
    }

    /* renamed from: b */
    public void m196b(JSONObject jSONObject) {
        this.f135e = jSONObject;
    }

    /* renamed from: f */
    public C0479b m202f() {
        return this.f136f;
    }

    /* renamed from: a */
    public void m188a(C0479b c0479b) {
        this.f136f = c0479b;
    }

    /* renamed from: g */
    public ArrayList<C0479b> m203g() {
        return this.f137g;
    }

    /* renamed from: b */
    public void m194b(C0479b c0479b) {
        this.f137g.add(c0479b);
    }

    /* renamed from: h */
    public boolean m204h() {
        return this.f138h;
    }

    /* renamed from: a */
    public void m191a(boolean z) {
        this.f138h = z;
    }

    /* renamed from: i */
    public boolean m205i() {
        return this.f139i;
    }

    /* renamed from: b */
    public void m197b(boolean z) {
        this.f139i = z;
    }

    /* renamed from: j */
    public Bitmap m206j() {
        return this.f140j;
    }

    /* renamed from: a */
    public void m184a(Bitmap bitmap) {
        this.f140j = bitmap;
    }

    /* renamed from: k */
    public void m207k() {
        this.f140j = null;
    }

    /* renamed from: l */
    public Uri m208l() {
        return this.f141k;
    }

    /* renamed from: a */
    public void m186a(Uri uri) {
        this.f141k = uri;
    }

    /* renamed from: m */
    public Rect m209m() {
        return this.f143m;
    }

    /* renamed from: a */
    public void m185a(Rect rect) {
        this.f143m = rect;
    }

    /* renamed from: n */
    public Rect m210n() {
        return this.f142l;
    }

    /* renamed from: b */
    public void m193b(Rect rect) {
        this.f142l = rect;
    }

    /* renamed from: o */
    public View m211o() {
        return this.f144n;
    }

    /* renamed from: a */
    public void m187a(View view) {
        this.f144n = view;
    }

    /* renamed from: p */
    public int m212p() {
        return this.f145o;
    }

    /* renamed from: a */
    public void m183a(int i) {
        this.f145o = i;
    }
}
