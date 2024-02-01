package com.instabug.survey.p036ui.p037a.p038a;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.instabug.survey.p036ui.p037a.AbstractC0791b;
import java.util.List;

/* compiled from: QuestionsPagerAdapter.java */
/* renamed from: com.instabug.survey.ui.a.a.a */
/* loaded from: classes.dex */
public class C0790a extends FragmentPagerAdapter {

    /* renamed from: a */
    private List<AbstractC0791b> f1376a;

    public C0790a(@NonNull FragmentManager fragmentManager, @NonNull List<AbstractC0791b> list) {
        super(fragmentManager);
        this.f1376a = list;
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.f1376a.size();
    }

    @Override // android.support.v4.app.FragmentPagerAdapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public AbstractC0791b getItem(int i) {
        return this.f1376a.get(i);
    }
}
