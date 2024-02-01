package com.instabug.survey.p032a;

import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: Survey.java */
/* renamed from: com.instabug.survey.a.c */
/* loaded from: classes.dex */
public class C0769c implements Cacheable, Serializable {

    /* renamed from: a */
    private long f1337a;

    /* renamed from: b */
    private String f1338b;

    /* renamed from: c */
    private String f1339c;

    /* renamed from: d */
    private ArrayList<C0768b> f1340d;

    /* renamed from: g */
    private ArrayList<C0767a> f1343g;

    /* renamed from: k */
    private long f1347k;

    /* renamed from: e */
    private ArrayList<C0767a> f1341e = new ArrayList<>();

    /* renamed from: f */
    private ArrayList<C0767a> f1342f = new ArrayList<>();

    /* renamed from: i */
    private boolean f1345i = false;

    /* renamed from: j */
    private boolean f1346j = false;

    /* renamed from: l */
    private boolean f1348l = false;

    /* renamed from: h */
    private String f1344h = "and";

    /* renamed from: a */
    public static List<C0769c> m1942a(JSONArray jSONArray) throws JSONException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            C0769c c0769c = new C0769c();
            c0769c.fromJson(jSONObject.toString());
            arrayList.add(c0769c);
        }
        return arrayList;
    }

    /* renamed from: a */
    public long m1943a() {
        return this.f1337a;
    }

    /* renamed from: a */
    public C0769c m1944a(long j) {
        this.f1337a = j;
        return this;
    }

    /* renamed from: a */
    public void m1945a(String str) {
        this.f1338b = str;
    }

    /* renamed from: b */
    public ArrayList<C0768b> m1948b() {
        return this.f1340d;
    }

    /* renamed from: a */
    public void m1946a(ArrayList<C0768b> arrayList) {
        this.f1340d = arrayList;
    }

    /* renamed from: c */
    public ArrayList<C0767a> m1952c() {
        return this.f1341e;
    }

    /* renamed from: b */
    public void m1950b(ArrayList<C0767a> arrayList) {
        this.f1341e = arrayList;
    }

    /* renamed from: d */
    public ArrayList<C0767a> m1956d() {
        return this.f1342f;
    }

    /* renamed from: c */
    public void m1954c(ArrayList<C0767a> arrayList) {
        this.f1342f = arrayList;
    }

    /* renamed from: e */
    public ArrayList<C0767a> m1958e() {
        return this.f1343g;
    }

    /* renamed from: d */
    public void m1957d(ArrayList<C0767a> arrayList) {
        this.f1343g = arrayList;
    }

    /* renamed from: f */
    public String m1959f() {
        return this.f1344h;
    }

    /* renamed from: b */
    public void m1949b(String str) {
        this.f1344h = str;
    }

    /* renamed from: g */
    public boolean m1960g() {
        return this.f1345i;
    }

    /* renamed from: a */
    public void m1947a(boolean z) {
        this.f1345i = z;
    }

    /* renamed from: h */
    public boolean m1961h() {
        return this.f1346j;
    }

    /* renamed from: b */
    public void m1951b(boolean z) {
        this.f1346j = z;
    }

    /* renamed from: i */
    public void m1962i() {
        this.f1347k = System.currentTimeMillis();
        m1955c(true);
    }

    /* renamed from: j */
    public long m1963j() {
        return this.f1347k;
    }

    /* renamed from: c */
    public void m1953c(String str) {
        this.f1339c = str;
    }

    /* renamed from: k */
    public String m1964k() {
        return this.f1339c;
    }

    /* renamed from: c */
    public void m1955c(boolean z) {
        this.f1348l = z;
    }

    /* renamed from: l */
    public boolean m1965l() {
        return this.f1348l;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", this.f1337a).put("title", this.f1338b).put("token", this.f1339c).put("questions", C0768b.m1928a(this.f1340d)).put("target", new JSONObject().put("primitive_types", C0767a.m1920a(this.f1341e)).put("custom_attributes", C0767a.m1920a(this.f1342f)).put(State.KEY_USER_EVENTS, C0767a.m1920a(this.f1343g)).put("operator", this.f1344h)).put("answered", this.f1345i).put("submitted", this.f1346j).put("dismissed_at", this.f1347k).put("submitted", this.f1346j).put("is_cancelled", this.f1348l);
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            m1944a(jSONObject.getLong("id"));
        }
        if (jSONObject.has("title")) {
            m1945a(jSONObject.getString("title"));
        }
        if (jSONObject.has("token")) {
            m1953c(jSONObject.getString("token"));
        }
        if (jSONObject.has("questions")) {
            m1946a(C0768b.m1927a(jSONObject.getJSONArray("questions")));
        } else if (jSONObject.has("question")) {
            InstabugSDKLogger.m1799d(this, "Migrating old surveys");
            C0768b c0768b = new C0768b();
            c0768b.m1931a(m1943a());
            c0768b.fromJson(jSONObject.get("question").toString());
            ArrayList<C0768b> arrayList = new ArrayList<>();
            arrayList.add(c0768b);
            m1946a(arrayList);
        }
        if (jSONObject.has("target")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("target");
            if (jSONObject2.has("primitive_types")) {
                m1950b(C0767a.m1919a(jSONObject2.getJSONArray("primitive_types")));
            }
            if (jSONObject2.has("custom_attributes")) {
                m1954c(C0767a.m1919a(jSONObject2.getJSONArray("custom_attributes")));
            }
            if (jSONObject2.has(State.KEY_USER_EVENTS)) {
                m1957d(C0767a.m1919a(jSONObject2.getJSONArray(State.KEY_USER_EVENTS)));
            }
            if (jSONObject2.has("operator")) {
                m1949b(jSONObject2.getString("operator"));
            }
        }
        if (jSONObject.has("answered")) {
            m1947a(jSONObject.getBoolean("answered"));
        }
        if (jSONObject.has("submitted")) {
            m1951b(jSONObject.getBoolean("submitted"));
        }
        if (jSONObject.has("is_cancelled")) {
            m1955c(jSONObject.getBoolean("is_cancelled"));
        }
    }

    public boolean equals(Object obj) {
        return obj != null && (obj instanceof C0769c) && ((C0769c) obj).m1943a() == m1943a();
    }

    public int hashCode() {
        return String.valueOf(m1943a()).hashCode();
    }
}
