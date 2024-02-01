package com.instabug.chat.p006a;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.instabug.chat.C0507R;
import com.instabug.chat.model.C0529c;
import com.instabug.chat.p011ui.view.CircularImageView;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.ScreenUtility;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import p045rx.functions.Action1;

/* compiled from: NotificationBarInvoker.java */
/* renamed from: com.instabug.chat.a.a */
/* loaded from: classes.dex */
public class C0509a {

    /* renamed from: a */
    private View f264a;

    /* renamed from: b */
    private boolean f265b = false;

    /* renamed from: c */
    private boolean f266c;

    /* renamed from: d */
    private boolean f267d;

    /* renamed from: e */
    private C0529c f268e;

    /* renamed from: f */
    private b f269f;

    /* compiled from: NotificationBarInvoker.java */
    /* renamed from: com.instabug.chat.a.a$b */
    /* loaded from: classes.dex */
    public interface b {
        /* renamed from: a */
        void mo491a();

        /* renamed from: b */
        void mo492b();
    }

    public C0509a() {
        m479d();
    }

    /* renamed from: a */
    private void m464a(final Activity activity, final c cVar) {
        View findViewById = activity.findViewById(C0507R.id.instabug_in_app_notification);
        if (findViewById != null) {
            this.f264a = findViewById;
            cVar.mo490b();
            return;
        }
        m470a(false);
        this.f264a = ((LayoutInflater) activity.getSystemService("layout_inflater")).inflate(C0507R.layout.instabug_lyt_notification, (ViewGroup) null);
        this.f264a.setVisibility(4);
        this.f264a.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.a.a.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 80;
        Resources resources = activity.getResources();
        if (Build.VERSION.SDK_INT >= 21 && ScreenUtility.isLandscape(activity) && ScreenUtility.hasNavBar(resources)) {
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == 1) {
                layoutParams.rightMargin = ScreenUtility.getNavigationBarWidth(resources);
            } else if (rotation == 3) {
                if (Build.VERSION.SDK_INT >= 24) {
                    layoutParams.leftMargin = ScreenUtility.getNavigationBarWidth(resources);
                } else {
                    layoutParams.rightMargin = ScreenUtility.getNavigationBarWidth(resources);
                }
            }
        }
        this.f264a.setLayoutParams(layoutParams);
        activity.runOnUiThread(new Runnable() { // from class: com.instabug.chat.a.a.5
            @Override // java.lang.Runnable
            public void run() {
                ((ViewGroup) activity.getWindow().getDecorView()).addView(C0509a.this.f264a, layoutParams);
                C0509a.this.f264a.postDelayed(cVar, 100L);
            }
        });
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.instabug.chat.a.a.6
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(new Rect());
                if (r1 - r0.bottom > activity.getWindow().getDecorView().getRootView().getHeight() * 0.15d) {
                    C0509a.this.f266c = true;
                    return;
                }
                C0509a.this.f266c = false;
                if (C0509a.this.f267d && !C0509a.this.f265b) {
                    C0509a.this.m463a(activity);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m469a(InstabugColorTheme instabugColorTheme) {
        View findViewById = this.f264a.findViewById(C0507R.id.instabug_notification_layout);
        Button button = (Button) this.f264a.findViewById(C0507R.id.replyButton);
        Button button2 = (Button) this.f264a.findViewById(C0507R.id.dismissButton);
        TextView textView = (TextView) this.f264a.findViewById(C0507R.id.senderNameTextView);
        TextView textView2 = (TextView) this.f264a.findViewById(C0507R.id.senderMessageTextView);
        button.getBackground().setColorFilter(Instabug.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        button2.getBackground().setColorFilter(-1, PorterDuff.Mode.MULTIPLY);
        button.setTextColor(-1);
        if (instabugColorTheme == InstabugColorTheme.InstabugColorThemeLight) {
            findViewById.setBackgroundColor(-1);
            textView.setTextColor(-11908534);
            textView2.setTextColor(-7697777);
            button2.setTextColor(-6579301);
            return;
        }
        findViewById.setBackgroundColor(-12434878);
        textView.setTextColor(-1);
        textView2.setTextColor(-2631721);
        button2.setTextColor(-6579301);
    }

    /* renamed from: a */
    public void m487a(final Activity activity, final C0529c c0529c, b bVar) {
        this.f268e = c0529c;
        this.f269f = bVar;
        m464a(activity, new c() { // from class: com.instabug.chat.a.a.7
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // com.instabug.chat.p006a.C0509a.c
            /* renamed from: a */
            void mo489a() {
                C0509a.this.f264a.setY(ScreenUtility.getScreenHeight(activity));
                C0509a.this.m465a(activity, c0529c);
            }

            @Override // com.instabug.chat.p006a.C0509a.c
            /* renamed from: b */
            public void mo490b() {
                C0509a.this.m465a(activity, c0529c);
            }
        });
        m462a();
    }

    /* renamed from: a */
    private void m462a() {
        Button button = (Button) this.f264a.findViewById(C0507R.id.replyButton);
        Button button2 = (Button) this.f264a.findViewById(C0507R.id.dismissButton);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.a.a.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                C0509a.this.m472b();
                C0509a.this.m470a(false);
                C0509a.this.f269f.mo491a();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.a.a.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                C0509a.this.m472b();
                C0509a.this.m476c();
                C0509a.this.f269f.mo492b();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m472b() {
        this.f268e = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m465a(Activity activity, C0529c c0529c) {
        m473b(activity, c0529c);
    }

    /* renamed from: b */
    private void m473b(final Activity activity, final C0529c c0529c) {
        final CircularImageView circularImageView = (CircularImageView) this.f264a.findViewById(C0507R.id.senderAvatarImageView);
        activity.runOnUiThread(new Runnable() { // from class: com.instabug.chat.a.a.10
            @Override // java.lang.Runnable
            public void run() {
                C0509a.this.m469a(Instabug.getTheme());
                circularImageView.setBackgroundResource(C0507R.drawable.instabug_ic_avatar);
                TextView textView = (TextView) C0509a.this.f264a.findViewById(C0507R.id.senderNameTextView);
                TextView textView2 = (TextView) C0509a.this.f264a.findViewById(C0507R.id.senderMessageTextView);
                textView.setText(c0529c.m681b());
                textView2.setText(c0529c.m679a());
            }
        });
        AssetsCacheManager.getAssetEntity(activity, AssetsCacheManager.createEmptyEntity(activity, c0529c.m683c(), AssetEntity.AssetType.IMAGE), new AssetsCacheManager.OnDownloadFinished() { // from class: com.instabug.chat.a.a.11
            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onSuccess(AssetEntity assetEntity) {
                InstabugSDKLogger.m1799d(this, "Asset Entity downloaded: " + assetEntity.getFile().getPath());
                try {
                    final Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(assetEntity.getFile()));
                    activity.runOnUiThread(new Runnable() { // from class: com.instabug.chat.a.a.11.1
                        @Override // java.lang.Runnable
                        public void run() {
                            circularImageView.setBackgroundResource(0);
                            circularImageView.setImageBitmap(decodeStream);
                        }
                    });
                } catch (FileNotFoundException e) {
                    InstabugSDKLogger.m1801e(this, "Asset Entity downloading got FileNotFoundException error", e);
                }
                if (!C0509a.this.f265b) {
                    C0509a.this.m463a(activity);
                }
            }

            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onFailed(Throwable th) {
                if (!C0509a.this.f265b) {
                    C0509a.this.m463a(activity);
                }
                InstabugSDKLogger.m1801e(this, "Asset Entity downloading got error", th);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m476c() {
        m470a(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m470a(boolean z) {
        if (this.f265b && this.f264a != null) {
            int screenHeight = ScreenUtility.getScreenHeight((Activity) this.f264a.getContext());
            if (z) {
                this.f264a.animate().y(screenHeight).setListener(new a() { // from class: com.instabug.chat.a.a.12
                    @Override // com.instabug.chat.p006a.C0509a.a, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        C0509a.this.f264a.setVisibility(4);
                    }
                }).start();
            } else {
                this.f264a.setY(screenHeight);
                this.f264a.setVisibility(4);
            }
            this.f265b = false;
            this.f267d = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m463a(final Activity activity) {
        if (this.f266c) {
            this.f267d = true;
            return;
        }
        activity.runOnUiThread(new Runnable() { // from class: com.instabug.chat.a.a.2
            @Override // java.lang.Runnable
            public void run() {
                C0509a.this.f264a.setVisibility(0);
                C0509a.this.f264a.animate().y(ScreenUtility.getScreenHeight(activity) - C0509a.this.f264a.getHeight()).setListener(null).start();
                C0509a.this.f265b = true;
            }
        });
        if (C0537a.m743h()) {
            C0510b.m494a().m501a((Context) activity);
        }
    }

    /* compiled from: NotificationBarInvoker.java */
    /* renamed from: com.instabug.chat.a.a$a */
    /* loaded from: classes.dex */
    private class a implements Animator.AnimatorListener {
        private a() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }
    }

    /* renamed from: d */
    private void m479d() {
        CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.chat.a.a.3
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                switch (AnonymousClass4.f283a[activityLifeCycleEvent.ordinal()]) {
                    case 1:
                        C0509a.this.m482e();
                        return;
                    case 2:
                        C0509a.this.m483f();
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: NotificationBarInvoker.java */
    /* renamed from: com.instabug.chat.a.a$4  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass4 {

        /* renamed from: a */
        static final /* synthetic */ int[] f283a = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f283a[ActivityLifeCycleEvent.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f283a[ActivityLifeCycleEvent.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m482e() {
        if (this.f268e != null && this.f269f != null) {
            m487a(InstabugCore.getTargetActivity(), this.f268e, this.f269f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public void m483f() {
        m470a(false);
    }

    /* compiled from: NotificationBarInvoker.java */
    /* renamed from: com.instabug.chat.a.a$c */
    /* loaded from: classes.dex */
    public abstract class c implements Runnable {
        /* renamed from: a */
        abstract void mo489a();

        /* renamed from: b */
        abstract void mo490b();

        public c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            mo489a();
        }
    }
}
