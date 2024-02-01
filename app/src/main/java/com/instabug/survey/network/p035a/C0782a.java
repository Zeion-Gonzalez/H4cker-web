package com.instabug.survey.network.p035a;

import com.instabug.library.Instabug;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.network.Request;
import com.instabug.survey.p032a.C0768b;
import com.instabug.survey.p032a.C0769c;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: SubmittingSurveysUtil.java */
/* renamed from: com.instabug.survey.network.a.a */
/* loaded from: classes.dex */
public class C0782a {
    /* renamed from: a */
    public static void m2030a(Request request, C0769c c0769c) throws JSONException {
        request.addParameter("responses", m2029a(c0769c.m1948b()));
        request.addParameter("responded_at", Long.valueOf(c0769c.m1948b().get(c0769c.m1948b().size() - 1).m1940f()));
        request.addParameter("name", InstabugCore.getUsername());
        request.addParameter("email", Instabug.getUserEmail());
    }

    /* renamed from: a */
    public static JSONArray m2029a(ArrayList<C0768b> arrayList) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        Iterator<C0768b> it = arrayList.iterator();
        while (it.hasNext()) {
            C0768b next = it.next();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("value", next.m1939e());
            jSONObject.put("question_id", next.m1930a());
            jSONArray.put(jSONObject);
        }
        return jSONArray;
    }
}
