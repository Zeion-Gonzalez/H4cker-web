package com.instabug.survey.p032a;

import com.instabug.library.internal.storage.cache.Cacheable;
import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: Condition.java */
/* renamed from: com.instabug.survey.a.a */
/* loaded from: classes.dex */
public class C0767a implements Cacheable, Serializable {

    /* renamed from: a */
    private String f1321a;

    /* renamed from: b */
    private String f1322b;

    /* renamed from: c */
    private String f1323c;

    /* renamed from: a */
    public static JSONArray m1920a(ArrayList<C0767a> arrayList) throws JSONException {
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
    public static ArrayList<C0767a> m1919a(JSONArray jSONArray) throws JSONException {
        ArrayList<C0767a> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            C0767a c0767a = new C0767a();
            c0767a.fromJson(jSONArray.getJSONObject(i).toString());
            arrayList.add(c0767a);
        }
        return arrayList;
    }

    /* renamed from: a */
    public String m1922a() {
        return this.f1321a;
    }

    /* renamed from: a */
    public C0767a m1921a(String str) {
        this.f1321a = str;
        return this;
    }

    /* renamed from: b */
    public String m1924b() {
        return this.f1322b;
    }

    /* renamed from: b */
    public C0767a m1923b(String str) {
        this.f1322b = str;
        return this;
    }

    /* renamed from: c */
    public String m1926c() {
        return this.f1323c;
    }

    /* renamed from: c */
    public C0767a m1925c(String str) {
        this.f1323c = str;
        return this;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("key", m1922a()).put("value", this.f1322b).put("operator", this.f1323c);
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("key")) {
            m1921a(jSONObject.getString("key"));
        }
        if (jSONObject.has("value")) {
            m1923b(jSONObject.getString("value"));
        }
        if (jSONObject.has("operator")) {
            m1925c(jSONObject.getString("operator"));
        }
    }
}
