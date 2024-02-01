package com.instabug.library.invocation.p029a;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.InterfaceC0697a;
import com.instabug.library.invocation.util.InstabugFloatingButtonEdge;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;

/* compiled from: FloatingButtonInvoker.java */
/* renamed from: com.instabug.library.invocation.a.b */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0699b implements View.OnClickListener, InterfaceC0698a<Void> {

    /* renamed from: g */
    static final /* synthetic */ boolean f972g;

    /* renamed from: a */
    int f973a;

    /* renamed from: f */
    float f978f;

    /* renamed from: h */
    private FrameLayout.LayoutParams f979h;

    /* renamed from: i */
    private int f980i;

    /* renamed from: k */
    private InterfaceC0697a f982k;

    /* renamed from: l */
    private c f983l;

    /* renamed from: m */
    private int f984m;

    /* renamed from: b */
    int f974b = 0;

    /* renamed from: j */
    private int f981j = 0;

    /* renamed from: c */
    int f975c = 0;

    /* renamed from: d */
    int f976d = 0;

    /* renamed from: e */
    int f977e = 0;

    /* compiled from: FloatingButtonInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.b$d */
    /* loaded from: classes.dex */
    public static class d {

        /* renamed from: a */
        public InstabugFloatingButtonEdge f998a = InstabugFloatingButtonEdge.RIGHT;

        /* renamed from: b */
        public int f999b = ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
    }

    static {
        f972g = !ViewOnClickListenerC0699b.class.desiredAssertionStatus();
    }

    public ViewOnClickListenerC0699b(InterfaceC0697a interfaceC0697a) {
        this.f982k = interfaceC0697a;
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: a */
    public void mo1407a() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            m1411a(currentActivity);
        }
    }

    @Override // com.instabug.library.invocation.p029a.InterfaceC0698a
    /* renamed from: b */
    public void mo1408b() {
        m1414c();
    }

    /* renamed from: a */
    private void m1411a(Activity activity) {
        this.f983l = new c(activity);
        this.f977e = activity.getResources().getConfiguration().orientation;
        this.f978f = activity.getResources().getDisplayMetrics().density;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int i = this.f980i;
        int i2 = this.f981j;
        this.f981j = activity.getResources().getDisplayMetrics().heightPixels;
        this.f980i = activity.getResources().getDisplayMetrics().widthPixels;
        if (Build.VERSION.SDK_INT >= 17) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            this.f976d = displayMetrics.heightPixels;
            this.f975c = displayMetrics.widthPixels;
        }
        this.f984m = (int) (56.0f * this.f978f);
        b bVar = new b(activity);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(new OvalShape());
        shapeDrawable2.getPaint().setColor(Instabug.getPrimaryColor());
        shapeDrawable.getPaint().setColor(Instabug.getPrimaryColor());
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shapeDrawable2, shapeDrawable});
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, 2, 2, 2, 2);
        bVar.setBackgroundDrawable(layerDrawable);
        Drawable drawable = activity.getResources().getDrawable(C0577R.drawable.instabug_ic_floating_btn);
        if (!f972g && drawable == null) {
            throw new AssertionError();
        }
        bVar.setImageDrawable(drawable);
        bVar.setScaleType(ImageView.ScaleType.CENTER);
        if (this.f979h == null) {
            if (C0704b.m1513c().m1524e().m1538c().f998a == InstabugFloatingButtonEdge.LEFT) {
                this.f979h = new FrameLayout.LayoutParams(this.f984m, this.f984m, 51);
                bVar.setLayoutParams(this.f979h);
                bVar.m1419a(-10, C0704b.m1513c().m1524e().m1538c().f999b);
            } else {
                this.f979h = new FrameLayout.LayoutParams(this.f984m, this.f984m, 53);
                bVar.setLayoutParams(this.f979h);
                bVar.m1419a(this.f980i + 10, C0704b.m1513c().m1524e().m1538c().f999b);
            }
        } else {
            this.f973a = Math.round((this.f973a * this.f980i) / i);
            this.f974b = Math.round((this.f974b * this.f981j) / i2);
            this.f979h.leftMargin = this.f973a;
            this.f979h.rightMargin = this.f980i - this.f973a;
            this.f979h.topMargin = this.f974b;
            this.f979h.bottomMargin = this.f981j - this.f974b;
            bVar.setLayoutParams(this.f979h);
            bVar.m1416a();
        }
        bVar.setOnClickListener(this);
        bVar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.f983l.addView(bVar);
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.f983l, new ViewGroup.LayoutParams(-1, -1));
    }

    /* renamed from: c */
    private void m1414c() {
        if (this.f983l != null) {
            this.f983l.setOnClickListener(null);
            if (this.f983l.getParent() != null && (this.f983l.getParent() instanceof ViewGroup)) {
                ((ViewGroup) this.f983l.getParent()).removeView(this.f983l);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.f982k.mo1405a();
    }

    /* compiled from: FloatingButtonInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.b$c */
    /* loaded from: classes.dex */
    public static class c extends FrameLayout {
        public c(Context context) {
            super(context);
        }
    }

    /* compiled from: FloatingButtonInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.b$b */
    /* loaded from: classes.dex */
    public class b extends ImageButton {

        /* renamed from: b */
        private GestureDetector f986b;

        /* renamed from: c */
        private boolean f987c;

        /* renamed from: d */
        private a f988d;

        /* renamed from: e */
        private long f989e;

        /* renamed from: f */
        private float f990f;

        /* renamed from: g */
        private float f991g;

        /* renamed from: h */
        private boolean f992h;

        public b(Context context) {
            super(context);
            this.f987c = true;
            this.f992h = false;
            this.f986b = new GestureDetector(context, new a());
            this.f988d = new a();
            setId(C0577R.id.instabug_floating_button);
        }

        @Override // android.view.View
        public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            ViewOnClickListenerC0699b.this.f979h = (FrameLayout.LayoutParams) layoutParams;
            super.setLayoutParams(layoutParams);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.f987c ? this.f986b.onTouchEvent(motionEvent) : false) {
                m1416a();
            } else {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.f989e = System.currentTimeMillis();
                    this.f988d.m1420a();
                    this.f992h = true;
                } else if (action == 1) {
                    if (System.currentTimeMillis() - this.f989e < 200) {
                        performClick();
                    }
                    this.f992h = false;
                    m1416a();
                } else if (action == 2 && this.f992h) {
                    m1418a(rawX - this.f990f, rawY - this.f991g);
                }
                this.f990f = rawX;
                this.f991g = rawY;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: a */
        public void m1416a() {
            float f;
            float f2;
            float f3;
            if (C0704b.m1513c().m1524e().m1538c().f998a != InstabugFloatingButtonEdge.LEFT) {
                float f4 = ((float) ViewOnClickListenerC0699b.this.f973a) >= ((float) ViewOnClickListenerC0699b.this.f980i) / 2.0f ? ViewOnClickListenerC0699b.this.f980i + 10 : (float) (ViewOnClickListenerC0699b.this.f984m - 10);
                a aVar = this.f988d;
                if (ViewOnClickListenerC0699b.this.f974b > ViewOnClickListenerC0699b.this.f981j - ViewOnClickListenerC0699b.this.f984m) {
                    f = ViewOnClickListenerC0699b.this.f981j - (ViewOnClickListenerC0699b.this.f984m * 2);
                } else {
                    f = ViewOnClickListenerC0699b.this.f974b;
                }
                aVar.m1421a(f4, f);
                return;
            }
            if (ViewOnClickListenerC0699b.this.f973a >= ViewOnClickListenerC0699b.this.f980i / 2.0f) {
                f2 = (ViewOnClickListenerC0699b.this.f980i - ViewOnClickListenerC0699b.this.f984m) + 10;
            } else {
                f2 = -10.0f;
            }
            a aVar2 = this.f988d;
            if (ViewOnClickListenerC0699b.this.f974b > ViewOnClickListenerC0699b.this.f981j - ViewOnClickListenerC0699b.this.f984m) {
                f3 = ViewOnClickListenerC0699b.this.f981j - (ViewOnClickListenerC0699b.this.f984m * 2);
            } else {
                f3 = ViewOnClickListenerC0699b.this.f974b;
            }
            aVar2.m1421a(f2, f3);
        }

        /* renamed from: a */
        void m1419a(int i, int i2) {
            ViewOnClickListenerC0699b.this.f973a = i;
            ViewOnClickListenerC0699b.this.f974b = i2;
            ViewOnClickListenerC0699b.this.f979h.leftMargin = ViewOnClickListenerC0699b.this.f973a + 0;
            ViewOnClickListenerC0699b.this.f979h.rightMargin = ViewOnClickListenerC0699b.this.f980i - ViewOnClickListenerC0699b.this.f973a;
            if (ViewOnClickListenerC0699b.this.f977e == 2 && ViewOnClickListenerC0699b.this.f975c > ViewOnClickListenerC0699b.this.f980i) {
                ViewOnClickListenerC0699b.this.f979h.rightMargin = (int) (ViewOnClickListenerC0699b.this.f979h.rightMargin + (48.0f * ViewOnClickListenerC0699b.this.f978f));
            }
            ViewOnClickListenerC0699b.this.f979h.topMargin = ViewOnClickListenerC0699b.this.f974b + 0;
            ViewOnClickListenerC0699b.this.f979h.bottomMargin = ViewOnClickListenerC0699b.this.f981j - ViewOnClickListenerC0699b.this.f974b;
            setLayoutParams(ViewOnClickListenerC0699b.this.f979h);
        }

        /* renamed from: a */
        void m1418a(float f, float f2) {
            if (ViewOnClickListenerC0699b.this.f974b + f2 > 50.0f) {
                m1419a((int) (ViewOnClickListenerC0699b.this.f973a + f), (int) (ViewOnClickListenerC0699b.this.f974b + f2));
            }
            if (this.f987c && !this.f992h && Math.abs(ViewOnClickListenerC0699b.this.f979h.rightMargin) < 50 && Math.abs(ViewOnClickListenerC0699b.this.f979h.topMargin - (getContext().getResources().getDisplayMetrics().heightPixels / 2)) < 250) {
                m1416a();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* compiled from: FloatingButtonInvoker.java */
        /* renamed from: com.instabug.library.invocation.a.b$b$a */
        /* loaded from: classes.dex */
        public class a implements Runnable {

            /* renamed from: b */
            private Handler f994b;

            /* renamed from: c */
            private float f995c;

            /* renamed from: d */
            private float f996d;

            /* renamed from: e */
            private long f997e;

            private a() {
                this.f994b = new Handler(Looper.getMainLooper());
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* renamed from: a */
            public void m1421a(float f, float f2) {
                this.f995c = f;
                this.f996d = f2;
                this.f997e = System.currentTimeMillis();
                this.f994b.post(this);
            }

            @Override // java.lang.Runnable
            public void run() {
                if (b.this.getParent() != null) {
                    float min = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.f997e)) / 400.0f);
                    b.this.m1419a((int) (((this.f995c - ViewOnClickListenerC0699b.this.f973a) * min) + ViewOnClickListenerC0699b.this.f973a), (int) (((this.f996d - ViewOnClickListenerC0699b.this.f974b) * min) + ViewOnClickListenerC0699b.this.f974b));
                    if (min < 1.0f) {
                        this.f994b.post(this);
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* renamed from: a */
            public void m1420a() {
                this.f994b.removeCallbacks(this);
            }
        }
    }

    /* compiled from: FloatingButtonInvoker.java */
    /* renamed from: com.instabug.library.invocation.a.b$a */
    /* loaded from: classes.dex */
    static class a extends GestureDetector.SimpleOnGestureListener {
        a() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return Math.abs(motionEvent2.getX() - motionEvent.getX()) < 90.0f && motionEvent2.getY() - motionEvent.getY() > 90.0f;
        }
    }
}
