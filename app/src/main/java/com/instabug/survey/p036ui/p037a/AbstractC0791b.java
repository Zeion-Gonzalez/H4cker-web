package com.instabug.survey.p036ui.p037a;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.instabug.library.core.p024ui.BaseFragment;
import com.instabug.library.p031ui.custom.InstabugAutoResizeTextView;
import com.instabug.library.p031ui.custom.InstabugScrollView;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.OrientationUtils;
import com.instabug.survey.C0764R;
import com.instabug.survey.p032a.C0768b;

/* compiled from: QuestionFragment.java */
/* renamed from: com.instabug.survey.ui.a.b */
/* loaded from: classes.dex */
public abstract class AbstractC0791b extends BaseFragment {

    /* renamed from: a */
    protected C0768b f1377a;

    /* renamed from: b */
    protected InterfaceC0789a f1378b;

    /* renamed from: c */
    protected TextView f1379c;

    /* renamed from: d */
    private View f1380d;

    /* renamed from: e */
    private View f1381e;

    /* renamed from: f */
    private InstabugScrollView f1382f;

    /* renamed from: a */
    public abstract String mo2056a();

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: a */
    public void m2057a(InterfaceC0789a interfaceC0789a) {
        this.f1378b = interfaceC0789a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.instabug.library.core.p024ui.BaseFragment
    public void initViews(View view, Bundle bundle) {
        this.f1382f = (InstabugScrollView) view.findViewById(C0764R.id.ib_dialog_survey_scrollview_container);
        this.f1380d = view.findViewById(C0764R.id.instabug_survey_top_separator);
        this.f1381e = view.findViewById(C0764R.id.instabug_survey_bottom_separator);
        this.f1379c = (TextView) view.findViewById(C0764R.id.instabug_text_view_question);
        if (this.f1379c != null && (this.f1379c instanceof InstabugAutoResizeTextView)) {
            ((InstabugAutoResizeTextView) this.f1379c).setMinTextSize(40.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: b */
    public void m2058b() {
        if (this.f1379c != null && OrientationUtils.isInLandscape(getActivity())) {
            this.f1379c.setMaxLines(3);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        m2055c();
    }

    /* renamed from: c */
    private void m2055c() {
        if (this.f1382f != null) {
            this.f1382f.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.instabug.survey.ui.a.b.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    if (AbstractC0791b.this.f1382f.getHeight() >= AbstractC0791b.this.f1382f.getChildAt(0).getHeight()) {
                        AbstractC0791b.this.f1381e.setVisibility(8);
                        AbstractC0791b.this.f1380d.setVisibility(8);
                    } else {
                        AbstractC0791b.this.f1382f.setOnScrollListener(new InstabugScrollView.OnScrollListener() { // from class: com.instabug.survey.ui.a.b.1.1
                            @Override // com.instabug.library.ui.custom.InstabugScrollView.OnScrollListener
                            public void onTopReached() {
                                AbstractC0791b.this.f1380d.setVisibility(4);
                                AbstractC0791b.this.f1381e.setVisibility(0);
                                InstabugSDKLogger.m1799d(this, "onTopReached");
                                InstabugSDKLogger.m1799d(this, "scrollViewHeight " + AbstractC0791b.this.f1382f.getHeight());
                                InstabugSDKLogger.m1799d(this, "childHeight" + AbstractC0791b.this.f1382f.getChildAt(0).getHeight());
                            }

                            @Override // com.instabug.library.ui.custom.InstabugScrollView.OnScrollListener
                            public void onBottomReached() {
                                AbstractC0791b.this.f1380d.setVisibility(0);
                                AbstractC0791b.this.f1381e.setVisibility(4);
                                InstabugSDKLogger.m1799d(this, "onBottomReached");
                                InstabugSDKLogger.m1799d(this, "scrollViewHeight " + AbstractC0791b.this.f1382f.getHeight());
                                InstabugSDKLogger.m1799d(this, "childHeight" + AbstractC0791b.this.f1382f.getChildAt(0).getHeight());
                            }

                            @Override // com.instabug.library.ui.custom.InstabugScrollView.OnScrollListener
                            public void onScrolling() {
                                AbstractC0791b.this.f1380d.setVisibility(0);
                                AbstractC0791b.this.f1381e.setVisibility(0);
                                InstabugSDKLogger.m1799d(this, "onScrolling");
                                InstabugSDKLogger.m1799d(this, "scrollViewHeight " + AbstractC0791b.this.f1382f.getHeight());
                                InstabugSDKLogger.m1799d(this, "childHeight" + AbstractC0791b.this.f1382f.getChildAt(0).getHeight());
                            }
                        });
                    }
                }
            });
        }
    }
}
