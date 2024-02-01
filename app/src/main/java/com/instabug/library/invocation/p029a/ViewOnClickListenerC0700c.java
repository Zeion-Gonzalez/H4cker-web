package com.instabug.library.invocation.p029a;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.internal.media.AudioPlayer;
import com.instabug.library.internal.view.C0692a;
import com.instabug.library.internal.view.floatingactionbutton.C0695b;
import com.instabug.library.internal.view.floatingactionbutton.C0696c;
import com.instabug.library.internal.view.floatingactionbutton.RecordingFloatingActionButton;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.invocation.util.InstabugVideoRecordingButtonCorner;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.C0755c;
import com.instabug.library.util.InstabugSDKLogger;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* compiled from: ScreenRecordingFab.java */
/* renamed from: com.instabug.library.invocation.a.c */
/* loaded from: classes.dex */
public class ViewOnClickListenerC0700c implements View.OnClickListener {

    /* renamed from: A */
    private int f1000A;

    /* renamed from: B */
    private a f1001B;

    /* renamed from: C */
    private Subscription f1002C;

    /* renamed from: D */
    private c f1003D;

    /* renamed from: a */
    private FrameLayout.LayoutParams f1004a;

    /* renamed from: i */
    private float f1012i;

    /* renamed from: j */
    private int f1013j;

    /* renamed from: m */
    private boolean f1016m;

    /* renamed from: o */
    private C0695b f1018o;

    /* renamed from: p */
    private C0696c f1019p;

    /* renamed from: q */
    private C0692a f1020q;

    /* renamed from: r */
    private int f1021r;

    /* renamed from: s */
    private int f1022s;

    /* renamed from: t */
    private int f1023t;

    /* renamed from: u */
    private int f1024u;

    /* renamed from: v */
    private int f1025v;

    /* renamed from: x */
    private long f1027x;

    /* renamed from: z */
    private FrameLayout f1029z;

    /* renamed from: b */
    private int f1005b = 0;

    /* renamed from: c */
    private int f1006c = 0;

    /* renamed from: d */
    private int f1007d = 0;

    /* renamed from: e */
    private int f1008e = 0;

    /* renamed from: f */
    private int f1009f = 0;

    /* renamed from: g */
    private int f1010g = 0;

    /* renamed from: h */
    private int f1011h = 0;

    /* renamed from: k */
    private boolean f1014k = false;

    /* renamed from: l */
    private boolean f1015l = false;

    /* renamed from: n */
    private boolean f1017n = true;

    /* renamed from: w */
    private Handler f1026w = new Handler();

    /* renamed from: y */
    private Runnable f1028y = new Runnable() { // from class: com.instabug.library.invocation.a.c.1
        @Override // java.lang.Runnable
        public void run() {
            ViewOnClickListenerC0700c.this.f1001B.setText(AudioPlayer.getFormattedDurationText(System.currentTimeMillis() - ViewOnClickListenerC0700c.this.f1027x));
            ViewOnClickListenerC0700c.this.f1026w.postDelayed(this, 1000L);
        }
    };

    /* compiled from: ScreenRecordingFab.java */
    /* renamed from: com.instabug.library.invocation.a.c$c */
    /* loaded from: classes.dex */
    public interface c {
        void start();

        void stop();
    }

    public ViewOnClickListenerC0700c(c cVar) {
        this.f1003D = cVar;
    }

    /* renamed from: a */
    public void m1471a() {
        m1438e();
    }

    /* renamed from: b */
    public void m1472b() {
        this.f1002C.unsubscribe();
        m1473c();
    }

    /* renamed from: c */
    public void m1473c() {
        this.f1014k = false;
        this.f1017n = true;
        this.f1026w.removeCallbacks(this.f1028y);
        m1437d();
    }

    /* renamed from: a */
    private void m1429a(Activity activity) {
        this.f1029z = new FrameLayout(activity);
        this.f1011h = activity.getResources().getConfiguration().orientation;
        int m1431b = m1431b(activity);
        this.f1012i = activity.getResources().getDisplayMetrics().density;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int i = this.f1007d;
        int i2 = this.f1008e;
        this.f1008e = activity.getResources().getDisplayMetrics().heightPixels;
        this.f1007d = activity.getResources().getDisplayMetrics().widthPixels;
        if (Build.VERSION.SDK_INT >= 17) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            this.f1010g = displayMetrics.heightPixels;
            this.f1009f = displayMetrics.widthPixels;
        }
        this.f1000A = (int) activity.getResources().getDimension(C0577R.dimen.instabug_fab_size_normal);
        this.f1013j = (int) activity.getResources().getDimension(C0577R.dimen.instabug_fab_size_mini);
        this.f1021r = (int) activity.getResources().getDimension(C0577R.dimen.instabug_fab_actions_spacing);
        this.f1022s = 0;
        this.f1023t = this.f1007d - (this.f1000A + this.f1021r);
        this.f1024u = m1431b;
        this.f1025v = this.f1008e - (this.f1000A + this.f1021r);
        this.f1020q = new C0692a(activity);
        this.f1020q.setText(C0577R.string.instabug_str_video_recording_hint);
        this.f1018o = new C0695b(activity);
        if (!C0755c.m1809a() && this.f1018o.getVisibility() == 0) {
            this.f1018o.setVisibility(8);
        }
        if (this.f1017n) {
            this.f1018o.m1404d();
        } else {
            this.f1018o.m1403c();
        }
        this.f1018o.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.invocation.a.c.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewOnClickListenerC0700c.this.f1018o.m1402b()) {
                    C0755c.m1810b(Instabug.getApplicationContext());
                    ViewOnClickListenerC0700c.this.f1017n = false;
                } else {
                    C0755c.m1808a(Instabug.getApplicationContext());
                    ViewOnClickListenerC0700c.this.f1017n = true;
                }
            }
        });
        this.f1019p = new C0696c(activity);
        this.f1019p.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.invocation.a.c.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewOnClickListenerC0700c.this.f1014k) {
                    ViewOnClickListenerC0700c.this.m1437d();
                    if (ViewOnClickListenerC0700c.this.f1003D != null) {
                        ViewOnClickListenerC0700c.this.f1003D.stop();
                    }
                    ViewOnClickListenerC0700c.this.f1014k = false;
                    ViewOnClickListenerC0700c.this.f1026w.removeCallbacks(ViewOnClickListenerC0700c.this.f1028y);
                }
            }
        });
        this.f1001B = new a(activity);
        if (this.f1004a == null) {
            this.f1004a = new FrameLayout.LayoutParams(this.f1000A, this.f1000A, 51);
            this.f1001B.setLayoutParams(this.f1004a);
            switch (C0704b.m1513c().m1524e().m1544f()) {
                case BOTTOM_RIGHT:
                    this.f1001B.m1478a(this.f1023t, this.f1025v);
                    break;
                case BOTTOM_LEFT:
                    this.f1001B.m1478a(this.f1022s, this.f1025v);
                    break;
                case TOP_LEFT:
                    this.f1001B.m1478a(this.f1022s, this.f1024u);
                    break;
                case TOP_RIGHT:
                    this.f1001B.m1478a(this.f1023t, this.f1024u);
                    break;
                default:
                    this.f1001B.m1478a(this.f1023t, this.f1025v);
                    break;
            }
        } else {
            this.f1005b = Math.round((this.f1005b * this.f1007d) / i);
            this.f1006c = Math.round((this.f1006c * this.f1008e) / i2);
            this.f1004a.leftMargin = this.f1005b;
            this.f1004a.rightMargin = this.f1007d - this.f1005b;
            this.f1004a.topMargin = this.f1006c;
            this.f1004a.bottomMargin = this.f1008e - this.f1006c;
            this.f1001B.setLayoutParams(this.f1004a);
            this.f1001B.m1476b();
        }
        if (!this.f1014k) {
            m1457n();
        }
        this.f1001B.setOnClickListener(this);
        this.f1029z.addView(this.f1001B);
        m1445h();
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.f1029z, new ViewGroup.LayoutParams(-1, -1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m1437d() {
        if (this.f1029z != null) {
            this.f1029z.setOnClickListener(null);
            if (this.f1029z.getParent() != null && (this.f1029z.getParent() instanceof ViewGroup)) {
                ((ViewGroup) this.f1029z.getParent()).removeView(this.f1029z);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        m1448j();
        if (!this.f1014k) {
            this.f1014k = true;
            if (this.f1003D != null) {
                this.f1003D.start();
            }
            C0755c.m1808a(Instabug.getApplicationContext());
            this.f1001B.setRecordingState(RecordingFloatingActionButton.RecordingState.RECORDING);
            m1446i();
        }
        m1459o();
    }

    /* renamed from: e */
    private void m1438e() {
        this.f1002C = CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.library.invocation.a.c.4
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                switch (AnonymousClass6.f1037b[activityLifeCycleEvent.ordinal()]) {
                    case 1:
                        ViewOnClickListenerC0700c.this.m1440f();
                        return;
                    case 2:
                        ViewOnClickListenerC0700c.this.m1443g();
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ScreenRecordingFab.java */
    /* renamed from: com.instabug.library.invocation.a.c$6  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass6 {

        /* renamed from: b */
        static final /* synthetic */ int[] f1037b = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f1037b[ActivityLifeCycleEvent.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f1037b[ActivityLifeCycleEvent.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f1036a = new int[InstabugVideoRecordingButtonCorner.values().length];
            try {
                f1036a[InstabugVideoRecordingButtonCorner.BOTTOM_RIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f1036a[InstabugVideoRecordingButtonCorner.BOTTOM_LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f1036a[InstabugVideoRecordingButtonCorner.TOP_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f1036a[InstabugVideoRecordingButtonCorner.TOP_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public void m1440f() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            m1429a(currentActivity);
        } else {
            InstabugSDKLogger.m1803v(ViewOnClickListenerC0700c.class, "current activity equal null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public void m1443g() {
        m1437d();
    }

    /* compiled from: ScreenRecordingFab.java */
    /* renamed from: com.instabug.library.invocation.a.c$a */
    /* loaded from: classes.dex */
    public class a extends RecordingFloatingActionButton {

        /* renamed from: k */
        private GestureDetector f1039k;

        /* renamed from: l */
        private boolean f1040l;

        /* renamed from: m */
        private RunnableC1241a f1041m;

        /* renamed from: n */
        private long f1042n;

        /* renamed from: o */
        private float f1043o;

        /* renamed from: p */
        private float f1044p;

        /* renamed from: q */
        private boolean f1045q;

        public a(Context context) {
            super(context);
            this.f1040l = true;
            this.f1045q = false;
            this.f1039k = new GestureDetector(context, new b());
            this.f1041m = new RunnableC1241a();
            setId(C0577R.id.instabug_floating_button);
        }

        @Override // android.view.View
        public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            ViewOnClickListenerC0700c.this.f1004a = (FrameLayout.LayoutParams) layoutParams;
            super.setLayoutParams(layoutParams);
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.f1040l ? this.f1039k.onTouchEvent(motionEvent) : false) {
                m1476b();
            } else {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.f1042n = System.currentTimeMillis();
                    this.f1041m.m1479a();
                    this.f1045q = true;
                } else if (action == 1) {
                    if (System.currentTimeMillis() - this.f1042n < 200) {
                        performClick();
                    }
                    this.f1045q = false;
                    m1476b();
                } else if (action == 2 && this.f1045q) {
                    m1477a(rawX - this.f1043o, rawY - this.f1044p);
                }
                this.f1043o = rawX;
                this.f1044p = rawY;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: b */
        public void m1476b() {
            this.f1041m.m1480a(ViewOnClickListenerC0700c.this.f1005b >= ViewOnClickListenerC0700c.this.f1007d / 2 ? ViewOnClickListenerC0700c.this.f1023t : ViewOnClickListenerC0700c.this.f1022s, ViewOnClickListenerC0700c.this.f1006c >= ViewOnClickListenerC0700c.this.f1008e / 2 ? ViewOnClickListenerC0700c.this.f1025v : ViewOnClickListenerC0700c.this.f1024u);
        }

        /* renamed from: a */
        void m1478a(int i, int i2) {
            ViewOnClickListenerC0700c.this.f1005b = i;
            ViewOnClickListenerC0700c.this.f1006c = i2;
            ViewOnClickListenerC0700c.this.f1004a.leftMargin = ViewOnClickListenerC0700c.this.f1005b;
            ViewOnClickListenerC0700c.this.f1004a.rightMargin = ViewOnClickListenerC0700c.this.f1007d - ViewOnClickListenerC0700c.this.f1005b;
            if (ViewOnClickListenerC0700c.this.f1011h == 2 && ViewOnClickListenerC0700c.this.f1009f > ViewOnClickListenerC0700c.this.f1007d) {
                ViewOnClickListenerC0700c.this.f1004a.rightMargin = (int) (ViewOnClickListenerC0700c.this.f1004a.rightMargin + (48.0f * ViewOnClickListenerC0700c.this.f1012i));
            }
            ViewOnClickListenerC0700c.this.f1004a.topMargin = ViewOnClickListenerC0700c.this.f1006c;
            ViewOnClickListenerC0700c.this.f1004a.bottomMargin = ViewOnClickListenerC0700c.this.f1008e - ViewOnClickListenerC0700c.this.f1006c;
            setLayoutParams(ViewOnClickListenerC0700c.this.f1004a);
        }

        /* renamed from: a */
        void m1477a(float f, float f2) {
            if (ViewOnClickListenerC0700c.this.f1006c + f2 > 50.0f) {
                m1478a((int) (ViewOnClickListenerC0700c.this.f1005b + f), (int) (ViewOnClickListenerC0700c.this.f1006c + f2));
                ViewOnClickListenerC0700c.this.m1455m();
                if (ViewOnClickListenerC0700c.this.f1015l) {
                    ViewOnClickListenerC0700c.this.m1453l();
                }
                ViewOnClickListenerC0700c.this.m1459o();
            }
            if (this.f1040l && !this.f1045q && Math.abs(ViewOnClickListenerC0700c.this.f1004a.rightMargin) < 50 && Math.abs(ViewOnClickListenerC0700c.this.f1004a.topMargin - (getContext().getResources().getDisplayMetrics().heightPixels / 2)) < 250) {
                m1476b();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* compiled from: ScreenRecordingFab.java */
        /* renamed from: com.instabug.library.invocation.a.c$a$a  reason: collision with other inner class name */
        /* loaded from: classes.dex */
        public class RunnableC1241a implements Runnable {

            /* renamed from: b */
            private Handler f1047b;

            /* renamed from: c */
            private float f1048c;

            /* renamed from: d */
            private float f1049d;

            /* renamed from: e */
            private long f1050e;

            private RunnableC1241a() {
                this.f1047b = new Handler(Looper.getMainLooper());
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* renamed from: a */
            public void m1480a(float f, float f2) {
                this.f1048c = f;
                this.f1049d = f2;
                this.f1050e = System.currentTimeMillis();
                this.f1047b.post(this);
            }

            @Override // java.lang.Runnable
            public void run() {
                if (a.this.getParent() != null) {
                    float min = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.f1050e)) / 400.0f);
                    a.this.m1478a((int) (((this.f1048c - ViewOnClickListenerC0700c.this.f1005b) * min) + ViewOnClickListenerC0700c.this.f1005b), (int) (((this.f1049d - ViewOnClickListenerC0700c.this.f1006c) * min) + ViewOnClickListenerC0700c.this.f1006c));
                    if (min < 1.0f) {
                        this.f1047b.post(this);
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* renamed from: a */
            public void m1479a() {
                this.f1047b.removeCallbacks(this);
            }
        }
    }

    /* compiled from: ScreenRecordingFab.java */
    /* renamed from: com.instabug.library.invocation.a.c$b */
    /* loaded from: classes.dex */
    static class b extends GestureDetector.SimpleOnGestureListener {
        b() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return Math.abs(motionEvent2.getX() - motionEvent.getX()) < 90.0f && motionEvent2.getY() - motionEvent.getY() > 90.0f;
        }
    }

    /* renamed from: h */
    private void m1445h() {
        this.f1001B.setRecordingState(this.f1014k ? RecordingFloatingActionButton.RecordingState.RECORDING : RecordingFloatingActionButton.RecordingState.STOPPED);
    }

    /* renamed from: i */
    private void m1446i() {
        this.f1027x = System.currentTimeMillis();
        this.f1026w.removeCallbacks(this.f1028y);
        this.f1026w.postDelayed(this.f1028y, 0L);
    }

    /* renamed from: j */
    private void m1448j() {
        if (this.f1015l) {
            m1453l();
        } else {
            m1451k();
        }
    }

    /* renamed from: k */
    private void m1451k() {
        if (Math.abs(this.f1004a.leftMargin - this.f1022s) <= 20 || Math.abs(this.f1004a.leftMargin - this.f1023t) <= 20) {
            if (Math.abs(this.f1004a.topMargin - this.f1024u) <= 20 || Math.abs(this.f1004a.topMargin - this.f1025v) <= 20) {
                m1455m();
                this.f1029z.addView(this.f1018o);
                this.f1029z.addView(this.f1019p);
                this.f1015l = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public void m1453l() {
        this.f1029z.removeView(this.f1018o);
        this.f1029z.removeView(this.f1019p);
        this.f1015l = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: m */
    public void m1455m() {
        int i;
        int i2;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(this.f1013j, this.f1013j);
        layoutParams.leftMargin = this.f1004a.leftMargin + ((this.f1000A - this.f1013j) / 2);
        layoutParams.rightMargin = this.f1004a.rightMargin + ((this.f1000A - this.f1013j) / 2);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(this.f1019p.getWidth(), this.f1019p.getHeight());
        layoutParams2.leftMargin = this.f1004a.leftMargin + ((this.f1000A - this.f1013j) / 2);
        layoutParams2.rightMargin = this.f1004a.rightMargin + ((this.f1000A - this.f1013j) / 2);
        if (this.f1004a.topMargin > (this.f1013j + (this.f1021r * 2)) * 2) {
            i = this.f1004a.topMargin - (this.f1013j + this.f1021r);
            i2 = i - (this.f1013j + this.f1021r);
        } else {
            i = this.f1021r + this.f1004a.topMargin + this.f1000A;
            i2 = this.f1013j + i + this.f1021r;
        }
        layoutParams2.topMargin = i;
        layoutParams.topMargin = i2;
        this.f1018o.setLayoutParams(layoutParams);
        this.f1019p.setLayoutParams(layoutParams2);
    }

    /* renamed from: b */
    private static int m1431b(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /* renamed from: n */
    private void m1457n() {
        if (!this.f1016m && this.f1004a.leftMargin != this.f1022s) {
            this.f1016m = true;
            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            this.f1020q.setLayoutParams(layoutParams);
            this.f1020q.post(new Runnable() { // from class: com.instabug.library.invocation.a.c.5
                @Override // java.lang.Runnable
                public void run() {
                    layoutParams.leftMargin = ViewOnClickListenerC0700c.this.f1004a.leftMargin - ViewOnClickListenerC0700c.this.f1020q.getWidth();
                    layoutParams.rightMargin = ViewOnClickListenerC0700c.this.f1007d - ViewOnClickListenerC0700c.this.f1004a.leftMargin;
                    layoutParams.topMargin = ViewOnClickListenerC0700c.this.f1004a.topMargin + ((((ViewOnClickListenerC0700c.this.f1004a.height + ViewOnClickListenerC0700c.this.f1000A) / 2) - ViewOnClickListenerC0700c.this.f1020q.getHeight()) / 2);
                    ViewOnClickListenerC0700c.this.f1020q.setLayoutParams(layoutParams);
                }
            });
            this.f1029z.addView(this.f1020q);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: o */
    public void m1459o() {
        if (this.f1016m) {
            this.f1016m = false;
            this.f1029z.removeView(this.f1020q);
        }
    }
}
