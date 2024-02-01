package com.instabug.survey.p032a;

import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: Question.java */
/* renamed from: com.instabug.survey.a.b */
/* loaded from: classes.dex */
public class C0768b implements Cacheable, Serializable {

    /* renamed from: b */
    private String f1325b;

    /* renamed from: d */
    private ArrayList<String> f1327d;

    /* renamed from: e */
    private String f1328e;

    /* renamed from: f */
    private long f1329f;

    /* renamed from: a */
    private long f1324a = -1;

    /* renamed from: c */
    private a f1326c = a.NOT_AVAILABLE;

    /* renamed from: a */
    public static ArrayList<C0768b> m1927a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0768b> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            C0768b c0768b = new C0768b();
            c0768b.fromJson(jSONArray.getJSONObject(i).toString());
            arrayList.add(c0768b);
        }
        return arrayList;
    }

    /* renamed from: a */
    public static JSONArray m1928a(ArrayList<C0768b> arrayList) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < arrayList.size()) {
                jSONArray.put(new JSONObject(arrayList.get(i2).toJson()));
                i = i2 + 1;
            } else {
                return jSONArray;
            }
        }
    }

    /* renamed from: a */
    public long m1930a() {
        return this.f1324a;
    }

    /* renamed from: a */
    public void m1931a(long j) {
        this.f1324a = j;
    }

    /* renamed from: b */
    public String m1934b() {
        return this.f1325b;
    }

    /* renamed from: a */
    public void m1933a(String str) {
        this.f1325b = str;
    }

    /* renamed from: c */
    public a m1937c() {
        return this.f1326c;
    }

    /* renamed from: a */
    public void m1932a(a aVar) {
        this.f1326c = aVar;
    }

    /* renamed from: d */
    public ArrayList<String> m1938d() {
        return this.f1327d;
    }

    /* renamed from: b */
    public void m1936b(ArrayList<String> arrayList) {
        this.f1327d = arrayList;
    }

    /* renamed from: e */
    public String m1939e() {
        return this.f1328e;
    }

    /* renamed from: b */
    public void m1935b(String str) {
        this.f1328e = str;
        m1929b(System.currentTimeMillis() / 1000);
    }

    /* renamed from: f */
    public long m1940f() {
        return this.f1329f;
    }

    /* renamed from: b */
    private void m1929b(long j) {
        this.f1329f = j;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", m1930a()).put("title", m1934b()).put("type", m1937c().m1941a()).put("options", new JSONArray((Collection) this.f1327d)).put("answer", m1939e()).put("answered_at", m1940f());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        InstabugSDKLogger.m1799d(this, str);
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            m1931a(jSONObject.getLong("id"));
        }
        if (jSONObject.has("title")) {
            m1933a(jSONObject.getString("title"));
        }
        if (jSONObject.has("type")) {
            switch (jSONObject.getInt("type")) {
                case 0:
                    m1932a(a.TEXT);
                    break;
                case 1:
                    m1932a(a.MCQ);
                    break;
                case 2:
                    m1932a(a.STAR_RATE);
                    break;
                case 3:
                    m1932a(a.NPS);
                    break;
            }
        }
        if (jSONObject.has("options")) {
            JSONArray jSONArray = jSONObject.getJSONArray("options");
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
            m1936b(arrayList);
        }
        if (jSONObject.has("answer")) {
            m1935b(jSONObject.getString("answer"));
        }
        if (jSONObject.has("answered_at")) {
            m1929b(jSONObject.getLong("answered_at"));
        }
    }

    /* compiled from: Question.java */
    /* renamed from: com.instabug.survey.a.b$a */
    /* loaded from: classes.dex */
    public enum a {
        TEXT(0),
        MCQ(1),
        STAR_RATE(2),
        NPS(3),
        NOT_AVAILABLE(-1);


        /* renamed from: f */
        private int f1336f;

        a(int i) {
            this.f1336f = i;
        }

        /* renamed from: a */
        public int m1941a() {
            return this.f1336f;
        }
    }
}
