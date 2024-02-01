package com.instabug.library.p022b;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.internal.view.AnimatedImageView;

/* compiled from: InstabugShakeAnimationDialog.java */
/* renamed from: com.instabug.library.b.a */
/* loaded from: classes.dex */
public class DialogC0630a extends Dialog implements DialogInterface.OnShowListener {

    /* renamed from: a */
    private ImageView f786a;

    /* renamed from: b */
    private String f787b;

    /* renamed from: c */
    private Runnable f788c;

    /* renamed from: d */
    private boolean f789d;

    /* renamed from: e */
    private ObjectAnimator f790e;

    public DialogC0630a(Context context, String str) {
        super(context, C0577R.style.InstabugBorderlessDialog);
        this.f789d = false;
        this.f787b = str;
        m1176a();
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        try {
            m1179c();
            this.f786a.getDrawable().setCallback(null);
            this.f786a = null;
        } catch (Exception e) {
            this.f786a = null;
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        this.f789d = false;
        findViewById(C0577R.id.animation_description).removeCallbacks(this.f788c);
        super.onDetachedFromWindow();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        this.f789d = true;
        super.onAttachedToWindow();
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
    }

    /* renamed from: a */
    private void m1176a() {
        requestWindowFeature(1);
        setContentView(C0577R.layout.instabug_lyt_dialog_shake_animation);
        setOnShowListener(this);
        TextView textView = (TextView) findViewById(C0577R.id.animation_description);
        textView.setText(Html.fromHtml(this.f787b));
        this.f786a = (AnimatedImageView) findViewById(C0577R.id.animation_frame);
        this.f786a.setImageResource(C0577R.drawable.instabug_img_shake);
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeDark) {
            findViewById(C0577R.id.instabug_intro_dialog).setBackgroundColor(ContextCompat.getColor(getContext(), C0577R.color.instabug_dialog_dark_bg_color));
            textView.setTextColor(ContextCompat.getColor(getContext(), 17170443));
            textView.setBackgroundColor(ContextCompat.getColor(getContext(), C0577R.color.instabug_dialog_dark_bg_color));
        }
        this.f790e = ObjectAnimator.ofFloat(this.f786a, "rotation", 0.0f, 8.0f, 16.0f, 25.0f, 0.0f, -12.0f, -25.0f, -12.0f, 0.0f, 4.0f, 0.0f);
        this.f790e.setDuration(1000L);
        this.f790e.setRepeatCount(3);
        this.f790e.setStartDelay(200L);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        if (this.f786a != null) {
            m1178b();
            this.f788c = new Runnable() { // from class: com.instabug.library.b.a.1
                @Override // java.lang.Runnable
                public void run() {
                    if (DialogC0630a.this.f789d) {
                        DialogC0630a.this.dismiss();
                    }
                }
            };
            findViewById(C0577R.id.animation_description).postDelayed(this.f788c, 3000L);
        }
    }

    /* renamed from: b */
    private void m1178b() {
        if (this.f790e != null) {
            this.f790e.start();
        }
    }

    /* renamed from: c */
    private void m1179c() {
        if (this.f790e != null) {
            this.f790e.cancel();
        }
    }
}
