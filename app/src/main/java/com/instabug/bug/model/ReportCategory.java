package com.instabug.bug.model;

import android.support.annotation.DrawableRes;
import com.instabug.bug.settings.C0482a;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ReportCategory {
    private static final String CATEGORY_REPORT_A_PROBLEM = "report-a-problem";
    private static final String CATEGORY_SUGGEST_AN_IMPROVEMENT = "suggest-an-improvement";
    private static final String KEY_LABEL = "name";
    private static final String KEY_SLUG = "slug";
    private static final String KEY_SUBS = "subs";
    private int icon;
    private String label;
    private List<ReportCategory> subs;

    private ReportCategory() {
    }

    public static ReportCategory getInstance() {
        return new ReportCategory();
    }

    public ReportCategory withLabel(String str) {
        this.label = str;
        return this;
    }

    public ReportCategory withIcon(@DrawableRes int i) {
        this.icon = i;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(@DrawableRes int i) {
        this.icon = i;
    }

    public List<ReportCategory> getSubs() {
        return this.subs;
    }

    private void setSubs(List<ReportCategory> list) {
        this.subs = list;
    }

    private static ReportCategory fromJsonObject(JSONObject jSONObject) throws JSONException {
        ReportCategory reportCategory = new ReportCategory();
        if (jSONObject.has(KEY_LABEL)) {
            reportCategory.setLabel(jSONObject.getString(KEY_LABEL));
        }
        ArrayList arrayList = new ArrayList();
        if (jSONObject.has(KEY_SUBS)) {
            JSONArray jSONArray = jSONObject.getJSONArray(KEY_SUBS);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(fromJsonObject(jSONArray.getJSONObject(i)));
            }
        }
        reportCategory.setSubs(arrayList);
        return reportCategory;
    }

    public static List<ReportCategory> getSubReportCategories(EnumC0472b enumC0472b) {
        List<ReportCategory> remoteSubReportCategories = getRemoteSubReportCategories(enumC0472b);
        return remoteSubReportCategories != null ? remoteSubReportCategories : C0482a.m236a().m247b();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x0037 -> B:17:0x0034). Please submit an issue!!! */
    private static List<ReportCategory> getRemoteSubReportCategories(EnumC0472b enumC0472b) {
        List<ReportCategory> subCategories;
        String m251c = C0482a.m236a().m251c();
        if (m251c != null) {
            try {
                JSONArray jSONArray = new JSONArray(m251c);
                if (enumC0472b == EnumC0472b.BUG) {
                    List<ReportCategory> subCategories2 = getSubCategories(jSONArray, CATEGORY_REPORT_A_PROBLEM);
                    if (subCategories2 != null && subCategories2.size() > 0) {
                        return subCategories2;
                    }
                } else if (enumC0472b == EnumC0472b.FEEDBACK && (subCategories = getSubCategories(jSONArray, CATEGORY_SUGGEST_AN_IMPROVEMENT)) != null && subCategories.size() > 0) {
                    return subCategories;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static List<ReportCategory> getSubCategories(JSONArray jSONArray, String str) throws JSONException {
        for (int i = 0; i < jSONArray.length(); i++) {
            if (str.equals(jSONArray.getJSONObject(i).getString(KEY_SLUG))) {
                return fromJsonObject(jSONArray.getJSONObject(i)).getSubs();
            }
        }
        return null;
    }

    public static boolean hasSubCategories(EnumC0472b enumC0472b) {
        boolean z;
        List<ReportCategory> remoteSubReportCategories = getRemoteSubReportCategories(EnumC0472b.BUG);
        List<ReportCategory> remoteSubReportCategories2 = getRemoteSubReportCategories(EnumC0472b.FEEDBACK);
        if (remoteSubReportCategories == null || remoteSubReportCategories.isEmpty()) {
            z = false;
        } else {
            if (enumC0472b == EnumC0472b.BUG) {
                return true;
            }
            z = true;
        }
        if (remoteSubReportCategories2 != null && !remoteSubReportCategories2.isEmpty()) {
            if (enumC0472b == EnumC0472b.FEEDBACK) {
                return true;
            }
            z = true;
        }
        List<ReportCategory> m247b = C0482a.m236a().m247b();
        return (z || m247b == null || m247b.size() <= 0) ? false : true;
    }
}
