package com.instabug.library.p022b;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.C0577R;

/* compiled from: InstabugSwipeAnimationDialog.java */
/* renamed from: com.instabug.library.b.b */
/* loaded from: classes.dex */
public class DialogC0631b extends Dialog implements DialogInterface.OnShowListener {

    /* renamed from: a */
    private ImageView f792a;

    /* renamed from: b */
    private ImageView f793b;

    /* renamed from: c */
    private String f794c;

    /* renamed from: d */
    private Runnable f795d;

    /* renamed from: e */
    private boolean f796e;

    /* renamed from: f */
    private ObjectAnimator f797f;

    /* renamed from: g */
    private ObjectAnimator f798g;

    /* renamed from: h */
    private ObjectAnimator f799h;

    public DialogC0631b(Context context, String str) {
        super(context, C0577R.style.InstabugBorderlessDialog);
        this.f796e = false;
        this.f794c = str;
        m1181a();
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        try {
            m1184c();
            this.f792a.getDrawable().setCallback(null);
            this.f792a = null;
        } catch (Exception e) {
            this.f792a = null;
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        this.f796e = false;
        findViewById(C0577R.id.animation_description).removeCallbacks(this.f795d);
        super.onDetachedFromWindow();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        this.f796e = true;
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
    private void m1181a() {
        requestWindowFeature(1);
        setContentView(C0577R.layout.instabug_lyt_dialog_two_fingers_swipe_animation);
        setOnShowListener(this);
        ((TextView) findViewById(C0577R.id.animation_description)).setText(Html.fromHtml(this.f794c));
        this.f792a = (ImageView) findViewById(C0577R.id.fingersImageView);
        this.f792a.setImageResource(C0577R.drawable.instabug_img_two_fingers);
        this.f793b = (ImageView) findViewById(C0577R.id.touchesImageView);
        this.f793b.setImageResource(C0577R.drawable.instabug_img_two_fingers_touch);
        FrameLayout frameLayout = (FrameLayout) findViewById(C0577R.id.animation_container);
        this.f798g = ObjectAnimator.ofFloat(frameLayout, "scaleX", 1.0f, 0.8f);
        this.f799h = ObjectAnimator.ofFloat(frameLayout, "scaleY", 1.0f, 0.8f);
        this.f798g.setStartDelay(200L);
        this.f798g.setDuration(800L);
        this.f799h.setStartDelay(200L);
        this.f799h.setDuration(800L);
        this.f797f = ObjectAnimator.ofFloat(frameLayout, "translationX", 0.0f, -120.0f);
        this.f797f.setStartDelay(1000L);
        this.f797f.setDuration(800L);
        new Handler().postDelayed(new Runnable() { // from class: com.instabug.library.b.b.1
            @Override // java.lang.Runnable
            public void run() {
                DialogC0631b.this.f793b.setVisibility(0);
            }
        }, 1000L);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        if (this.f792a != null) {
            m1182b();
            this.f795d = new Runnable() { // from class: com.instabug.library.b.b.2
                @Override // java.lang.Runnable
                public void run() {
                    if (DialogC0631b.this.f796e) {
                        DialogC0631b.this.dismiss();
                    }
                }
            };
            findViewById(C0577R.id.animation_description).postDelayed(this.f795d, 1800L);
        }
    }

    /* renamed from: b */
    private void m1182b() {
        if (this.f797f != null) {
            this.f797f.start();
            this.f798g.start();
            this.f799h.start();
        }
    }

    /* renamed from: c */
    private void m1184c() {
        if (this.f797f != null) {
            this.f797f.cancel();
            this.f798g.cancel();
            this.f799h.cancel();
        }
    }
}
