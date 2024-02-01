package com.instabug.survey.p036ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.util.DrawableUtils;
import com.instabug.library.view.ViewUtils;
import com.instabug.survey.C0764R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class NPSLayout extends LinearLayout {

    /* renamed from: a */
    private int f1422a;

    /* renamed from: b */
    private List<TextView> f1423b;

    /* renamed from: c */
    private final int f1424c;

    /* renamed from: d */
    private InterfaceC0805a f1425d;

    /* renamed from: com.instabug.survey.ui.custom.NPSLayout$a */
    /* loaded from: classes.dex */
    public interface InterfaceC0805a {
        /* renamed from: a */
        void mo2094a(int i);
    }

    public NPSLayout(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0764R.styleable.NPSLayout);
        this.f1422a = obtainStyledAttributes.getInteger(C0764R.styleable.NPSLayout_rows, 2);
        obtainStyledAttributes.recycle();
        this.f1424c = ViewUtils.convertDpToPx(context, 4.0f);
        this.f1423b = new ArrayList();
        setOrientation(1);
        m2118a();
    }

    /* renamed from: a */
    void m2118a() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, ViewUtils.convertDpToPx(getContext(), 8.0f), 0, ViewUtils.convertDpToPx(getContext(), 8.0f));
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        linearLayout2.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout2.setGravity(17);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout2.setLayoutParams(layoutParams);
        for (int i = 0; i < 11; i++) {
            if (i < 6) {
                m2116a(i, linearLayout);
            } else if (i >= 6) {
                if (this.f1422a == 2) {
                    m2116a(i, linearLayout2);
                } else {
                    m2116a(i, linearLayout);
                }
            }
        }
        addView(linearLayout);
        addView(linearLayout2);
    }

    /* renamed from: a */
    private void m2116a(final int i, LinearLayout linearLayout) {
        final TextView textView = new TextView(getContext());
        textView.setText(String.valueOf(i));
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeLight) {
            textView.setBackgroundResource(C0764R.drawable.survey_nps_bg_light_tv);
            textView.setTextColor(ContextCompat.getColor(getContext(), C0764R.color.survey_nps_txt_color_light));
        } else {
            textView.setBackgroundResource(C0764R.drawable.survey_nps_bg_dark_tv);
            textView.setTextColor(ContextCompat.getColor(getContext(), C0764R.color.survey_nps_txt_color_dark));
        }
        textView.setTextSize(2, 24.0f);
        textView.setTypeface(null, 1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewUtils.convertDpToPx(getContext(), 40.0f), ViewUtils.convertDpToPx(getContext(), 40.0f));
        layoutParams.setMargins(this.f1424c, 0, this.f1424c, 0);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(17);
        linearLayout.addView(textView);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.survey.ui.custom.NPSLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NPSLayout.this.m2115a(i);
                if (NPSLayout.this.f1425d != null) {
                    NPSLayout.this.f1425d.mo2094a(Integer.parseInt(textView.getText().toString()));
                }
            }
        });
        this.f1423b.add(textView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m2115a(int i) {
        int i2 = 0;
        Iterator<TextView> it = this.f1423b.iterator();
        while (true) {
            int i3 = i2;
            if (it.hasNext()) {
                TextView next = it.next();
                if (i3 == i) {
                    setTextViewColorsSelected(next);
                } else {
                    setTextViewColorsAccordingToTheme(next);
                }
                i2 = i3 + 1;
            } else {
                return;
            }
        }
    }

    private void setTextViewColorsAccordingToTheme(TextView textView) {
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeLight) {
            DrawableUtils.setColor(textView, ContextCompat.getColor(getContext(), C0764R.color.surveys_nps_box_color_white));
            textView.setTextColor(ContextCompat.getColor(getContext(), C0764R.color.survey_nps_txt_color_light));
        } else {
            DrawableUtils.setColor(textView, ContextCompat.getColor(getContext(), C0764R.color.survey_nps_txt_bg_color_dark));
            textView.setTextColor(ContextCompat.getColor(getContext(), C0764R.color.survey_nps_txt_color_dark));
        }
    }

    private void setTextViewColorsSelected(TextView textView) {
        DrawableUtils.setColor(textView, Instabug.getPrimaryColor());
        textView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void setNPSClickListener(InterfaceC0805a interfaceC0805a) {
        this.f1425d = interfaceC0805a;
    }

    public void setSelectedAnswer(String str) {
        for (TextView textView : this.f1423b) {
            if (textView.getText().toString().equals(str)) {
                setTextViewColorsSelected(textView);
            } else {
                setTextViewColorsAccordingToTheme(textView);
            }
        }
    }
}
