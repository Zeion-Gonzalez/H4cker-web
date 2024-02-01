package com.instabug.library.model;

import android.support.annotation.NonNull;
import com.instabug.library.internal.storage.cache.Cacheable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: UserAttributes.java */
/* renamed from: com.instabug.library.model.b */
/* loaded from: classes.dex */
public class C0717b implements Cacheable, Serializable {

    /* renamed from: a */
    private HashMap<String, String> f1120a;

    /* renamed from: a */
    public String m1592a(@NonNull String str) {
        return this.f1120a.get(str);
    }

    /* renamed from: a */
    public void m1594a(@NonNull HashMap<String, String> hashMap) {
        this.f1120a = hashMap;
    }

    /* renamed from: a */
    public HashMap<String, String> m1593a() {
        return this.f1120a;
    }

    /* renamed from: a */
    public C0717b m1591a(@NonNull String str, String str2) {
        if (this.f1120a == null) {
            this.f1120a = new HashMap<>();
        }
        this.f1120a.put(str, str2);
        return this;
    }

    /* renamed from: b */
    public void m1595b(@NonNull String str) {
        this.f1120a.remove(str);
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        for (Map.Entry<String, String> entry : this.f1120a.entrySet()) {
            jSONObject.put(entry.getKey(), entry.getValue());
        }
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        Iterator<String> keys = jSONObject.keys();
        this.f1120a = new HashMap<>();
        while (keys.hasNext()) {
            String next = keys.next();
            this.f1120a.put(next, jSONObject.optString(next));
        }
    }

    public String toString() {
        try {
            return "userAttributesMap = " + toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
