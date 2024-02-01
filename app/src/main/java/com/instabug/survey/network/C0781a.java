package com.instabug.survey.network;

import android.content.Context;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.network.Request;
import com.instabug.survey.network.service.C0784a;
import com.instabug.survey.p032a.C0769c;
import com.instabug.survey.p033b.C0772c;
import com.instabug.survey.p034c.C0777e;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

/* compiled from: SurveysFetcher.java */
/* renamed from: com.instabug.survey.network.a */
/* loaded from: classes.dex */
public class C0781a {

    /* renamed from: a */
    private a f1359a;

    /* compiled from: SurveysFetcher.java */
    /* renamed from: com.instabug.survey.network.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo1912a(Throwable th);

        /* renamed from: a */
        void mo1913a(List<C0769c> list);
    }

    public C0781a(a aVar) {
        this.f1359a = aVar;
    }

    /* renamed from: a */
    public void m2026a(Context context) throws IOException, JSONException {
        if (m2025a() && C0777e.m1994a() && System.currentTimeMillis() - C0772c.m1987d() > 10000) {
            C0784a.m2034a().m2035a(context, new Request.Callbacks<JSONArray, Throwable>() { // from class: com.instabug.survey.network.a.1
                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onSucceeded(JSONArray jSONArray) {
                    try {
                        C0772c.m1980a(System.currentTimeMillis());
                        C0781a.this.f1359a.mo1913a(C0769c.m1942a(jSONArray));
                    } catch (JSONException e) {
                        C0781a.this.f1359a.mo1912a(e);
                    }
                }

                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onFailed(Throwable th) {
                    C0781a.this.f1359a.mo1912a(th);
                }
            });
        }
    }

    /* renamed from: a */
    private boolean m2025a() {
        return InstabugCore.isFeaturesFetchedBefore();
    }
}
