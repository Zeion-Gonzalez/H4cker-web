package com.instabug.bug.view.p005c;

import com.instabug.bug.C0468d;
import com.instabug.bug.extendedbugreport.ExtendedBugReport;
import com.instabug.bug.model.C0471a;
import com.instabug.bug.settings.C0482a;
import com.instabug.bug.view.p005c.C0500a;
import com.instabug.library.core.p024ui.BasePresenter;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ExtraFieldsPresenter.java */
/* renamed from: com.instabug.bug.view.c.c */
/* loaded from: classes.dex */
class C0502c extends BasePresenter<C0500a.b> implements C0500a.a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public C0502c(C0500a.b bVar) {
        super(bVar);
    }

    /* renamed from: a */
    public List<C0471a> m415a() {
        List<C0471a> list;
        List<C0471a> m140m = C0468d.m86a().m103d().m140m();
        if (m140m == null) {
            ExtendedBugReport.State m269q = C0482a.m236a().m269q();
            switch (m269q) {
                case ENABLED_WITH_OPTIONAL_FIELDS:
                case ENABLED_WITH_REQUIRED_FIELDS:
                    C0500a.b bVar = (C0500a.b) this.view.get();
                    if (bVar == null) {
                        list = m140m;
                        break;
                    } else {
                        list = ExtendedBugReport.m108a(bVar.getViewContext().getContext(), m269q);
                        break;
                    }
                default:
                    list = C0482a.m236a().m268p();
                    break;
            }
            C0468d.m86a().m103d().m121a(list);
            return list;
        }
        return m140m;
    }

    /* renamed from: a */
    public void m416a(List<C0471a> list) {
        ExtendedBugReport.State m269q = C0482a.m236a().m269q();
        if (m269q == ExtendedBugReport.State.ENABLED_WITH_OPTIONAL_FIELDS || m269q == ExtendedBugReport.State.ENABLED_WITH_REQUIRED_FIELDS) {
            m412b(list);
        } else {
            m414c(list);
        }
    }

    /* renamed from: b */
    private void m412b(List<C0471a> list) {
        String m129d = C0468d.m86a().m103d().m129d();
        JSONArray jSONArray = new JSONArray();
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", "description");
            jSONObject.put("name", "Description");
            if (m129d == null) {
                m129d = "";
            }
            jSONObject.put("value", m129d);
            jSONArray.put(jSONObject);
            for (C0471a c0471a : list) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("id", c0471a.m144a());
                jSONObject2.put("name", c0471a.m148d());
                jSONObject2.put("value", c0471a.m146b() != null ? c0471a.m146b() : "");
                jSONArray.put(jSONObject2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        C0468d.m86a().m103d().m128d(jSONArray.toString());
        m413c();
    }

    /* renamed from: c */
    private void m413c() {
        Iterator<C0471a> it = C0482a.m236a().m268p().iterator();
        while (it.hasNext()) {
            it.next().m145a(null);
        }
    }

    /* renamed from: c */
    private void m414c(List<C0471a> list) {
        String m129d = C0468d.m86a().m103d().m129d();
        StringBuilder sb = new StringBuilder();
        if (m129d != null) {
            sb.append(m129d);
        }
        for (C0471a c0471a : list) {
            if (sb.length() > 0) {
                sb.append(IOUtils.LINE_SEPARATOR_UNIX);
            }
            sb.append(c0471a.m147c());
            sb.append(":");
            sb.append(IOUtils.LINE_SEPARATOR_UNIX);
            sb.append(c0471a.m146b());
        }
        C0468d.m86a().m103d().m128d(sb.toString());
        m413c();
    }

    /* renamed from: b */
    public boolean m417b() {
        List<C0471a> m140m = C0468d.m86a().m103d().m140m();
        C0500a.b bVar = (C0500a.b) this.view.get();
        if (bVar != null) {
            for (int i = 0; i < m140m.size(); i++) {
                C0471a c0471a = m140m.get(i);
                if (c0471a.m149e()) {
                    if (c0471a.m146b() == null) {
                        bVar.mo408a(i);
                        return false;
                    }
                    if (c0471a.m146b().trim().isEmpty()) {
                        bVar.mo408a(i);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
