package com.instabug.bug.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.instabug.bug.BugPlugin;
import com.instabug.bug.C0458R;
import com.instabug.bug.C0461b;
import com.instabug.bug.C0464c;
import com.instabug.bug.C0468d;
import com.instabug.bug.model.EnumC0472b;
import com.instabug.bug.model.ReportCategory;
import com.instabug.bug.view.actionList.C0491a;
import com.instabug.bug.view.actionList.ViewOnClickListenerC0494d;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library._InstabugActivity;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.p024ui.BaseFragmentActivity;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ReportCategoriesActivity extends BaseFragmentActivity implements _InstabugActivity {

    /* renamed from: a */
    private BugPlugin f167a;

    /* renamed from: a */
    public static Intent m321a(Context context, EnumC0472b enumC0472b) {
        Intent intent = new Intent(context, ReportCategoriesActivity.class);
        intent.addFlags(268435456);
        intent.putExtra("report_type", enumC0472b);
        return intent;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f167a = (BugPlugin) InstabugCore.getXPlugin(BugPlugin.class);
        if (this.f167a != null) {
            this.f167a.setState(1);
        }
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected int getLayout() {
        return C0458R.layout.instabug_activity_bug_reporting;
    }

    @Override // com.instabug.library.core.p024ui.BaseFragmentActivity
    protected void initViews() {
        m322a();
    }

    /* renamed from: a */
    private void m322a() {
        String str;
        EnumC0472b enumC0472b = (EnumC0472b) getIntent().getSerializableExtra("report_type");
        ArrayList<C0491a> m323a = m323a(ReportCategory.getSubReportCategories(enumC0472b), enumC0472b);
        if (enumC0472b == EnumC0472b.BUG) {
            C0468d.m86a().m103d().m115a(EnumC0472b.BUG);
            str = PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.REPORT_BUG, getString(C0458R.string.instabug_str_bug_header));
        } else if (enumC0472b != EnumC0472b.FEEDBACK) {
            str = null;
        } else {
            C0468d.m86a().m103d().m115a(EnumC0472b.FEEDBACK);
            str = PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.REPORT_FEEDBACK, getString(C0458R.string.instabug_str_feedback_header));
        }
        getSupportFragmentManager().beginTransaction().replace(C0458R.id.instabug_fragment_container, ViewOnClickListenerC0494d.m351a(str, m323a)).commit();
    }

    @VisibleForTesting
    @NonNull
    /* renamed from: a */
    ArrayList<C0491a> m323a(List<ReportCategory> list, final EnumC0472b enumC0472b) {
        ArrayList<C0491a> arrayList = new ArrayList<>();
        for (final ReportCategory reportCategory : list) {
            arrayList.add(new C0491a(reportCategory.getLabel(), reportCategory.getIcon(), new Runnable() { // from class: com.instabug.bug.view.ReportCategoriesActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    C0468d.m86a().m103d().m133f(reportCategory.getLabel());
                    if (enumC0472b == EnumC0472b.FEEDBACK) {
                        ReportCategoriesActivity.this.startActivity(C0461b.m52b(ReportCategoriesActivity.this));
                        ReportCategoriesActivity.this.finish();
                    } else if (enumC0472b == EnumC0472b.BUG) {
                        ReportCategoriesActivity.this.startActivity(C0461b.m51a(ReportCategoriesActivity.this));
                        ReportCategoriesActivity.this.finish();
                    }
                }
            }));
        }
        return arrayList;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        C0464c.m72a();
        if (this.f167a != null && this.f167a.getState() != 2) {
            this.f167a.setState(0);
        }
    }
}
