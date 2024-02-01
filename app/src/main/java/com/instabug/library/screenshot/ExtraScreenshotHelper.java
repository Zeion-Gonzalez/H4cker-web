package com.instabug.library.screenshot;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.instabug.library.C0577R;
import com.instabug.library.core.eventbus.CurrentActivityLifeCycleEventBus;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.screenshot.C0735a;
import com.instabug.library.tracking.ActivityLifeCycleEvent;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.ScreenUtility;
import p045rx.Subscription;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class ExtraScreenshotHelper {
    private ImageButton captureButton;
    private Subscription currentActivityLifeCycleSubscription;
    private boolean isCaptureButtonShown = false;
    private OnCaptureListener onCaptureListener;

    /* loaded from: classes.dex */
    public interface OnCaptureListener {
        void onExtraScreenshotCaptured(Uri uri);
    }

    public void init(OnCaptureListener onCaptureListener) {
        this.onCaptureListener = onCaptureListener;
        subscribeToCurranActivityLifeCycle();
        C0704b.m1513c().m1527h();
    }

    public void release() {
        this.captureButton = null;
        this.onCaptureListener = null;
        if (this.currentActivityLifeCycleSubscription != null && !this.currentActivityLifeCycleSubscription.isUnsubscribed()) {
            this.currentActivityLifeCycleSubscription.unsubscribe();
        }
        C0704b.m1513c().m1528i();
    }

    private void show(@NonNull final Activity activity) {
        if (this.isCaptureButtonShown) {
            InstabugSDKLogger.m1803v(this, "bar already shown");
            return;
        }
        this.captureButton = createCaptureButton(activity);
        ((ViewGroup) activity.getWindow().getDecorView()).addView(this.captureButton, createCaptureButtonContainer(activity));
        this.isCaptureButtonShown = true;
        this.captureButton.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.library.screenshot.ExtraScreenshotHelper.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ExtraScreenshotHelper.this.hide();
                ExtraScreenshotHelper.this.captureExtraScreenshot(activity, ExtraScreenshotHelper.this.onCaptureListener);
            }
        });
    }

    private ImageButton createCaptureButton(Activity activity) {
        ImageButton imageButton = new ImageButton(activity);
        imageButton.setId(C0577R.id.instabug_extra_screenshot_button);
        imageButton.setScaleType(ImageView.ScaleType.CENTER);
        imageButton.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(ContextCompat.getDrawable(activity, C0577R.drawable.instabug_bg_white_oval)));
        imageButton.setImageResource(C0577R.drawable.instabug_ic_capture_screenshot);
        return imageButton;
    }

    private FrameLayout.LayoutParams createCaptureButtonContainer(Activity activity) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2, 81);
        layoutParams.setMargins(0, 0, 0, 20);
        Resources resources = activity.getResources();
        if (Build.VERSION.SDK_INT >= 21 && ScreenUtility.hasNavBar(resources) && !ScreenUtility.isLandscape(activity)) {
            layoutParams.bottomMargin = ScreenUtility.getNavigationBarHeight(resources) + layoutParams.bottomMargin;
        }
        return layoutParams;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void captureExtraScreenshot(final Activity activity, final OnCaptureListener onCaptureListener) {
        C0735a.m1646a(activity, new C0735a.a() { // from class: com.instabug.library.screenshot.ExtraScreenshotHelper.2
            @Override // com.instabug.library.screenshot.C0735a.a
            /* renamed from: a */
            public void mo1234a(Bitmap bitmap) {
                BitmapUtils.saveBitmap(bitmap, activity, new BitmapUtils.InterfaceC0751a() { // from class: com.instabug.library.screenshot.ExtraScreenshotHelper.2.1
                    @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                    /* renamed from: a */
                    public void mo1236a(Uri uri) {
                        onCaptureListener.onExtraScreenshotCaptured(uri);
                    }

                    @Override // com.instabug.library.util.BitmapUtils.InterfaceC0751a
                    /* renamed from: a */
                    public void mo1237a(Throwable th) {
                        InstabugSDKLogger.m1801e(ExtraScreenshotHelper.class, th.getMessage(), th);
                    }
                });
            }

            @Override // com.instabug.library.screenshot.C0735a.a
            /* renamed from: a */
            public void mo1235a(Throwable th) {
                InstabugSDKLogger.m1801e(ExtraScreenshotHelper.class, th.getMessage(), th);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hide() {
        if (this.isCaptureButtonShown && this.captureButton != null && this.captureButton.getParent() != null && (this.captureButton.getParent() instanceof ViewGroup)) {
            ((ViewGroup) this.captureButton.getParent()).removeView(this.captureButton);
            this.isCaptureButtonShown = false;
        }
    }

    private void subscribeToCurranActivityLifeCycle() {
        this.currentActivityLifeCycleSubscription = CurrentActivityLifeCycleEventBus.getInstance().subscribe(new Action1<ActivityLifeCycleEvent>() { // from class: com.instabug.library.screenshot.ExtraScreenshotHelper.3
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                switch (C07344.f1195a[activityLifeCycleEvent.ordinal()]) {
                    case 1:
                        ExtraScreenshotHelper.this.handleCurrentActivityResumeEvent();
                        return;
                    case 2:
                        ExtraScreenshotHelper.this.handleCurrentActivityPauseEvent();
                        return;
                    default:
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.screenshot.ExtraScreenshotHelper$4 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C07344 {

        /* renamed from: a */
        static final /* synthetic */ int[] f1195a = new int[ActivityLifeCycleEvent.values().length];

        static {
            try {
                f1195a[ActivityLifeCycleEvent.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f1195a[ActivityLifeCycleEvent.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCurrentActivityResumeEvent() {
        Activity currentActivity = InstabugInternalTrackingDelegate.getInstance().getCurrentActivity();
        if (currentActivity != null) {
            show(currentActivity);
        } else {
            InstabugSDKLogger.m1803v(ExtraScreenshotHelper.class, "current activity equal null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCurrentActivityPauseEvent() {
        hide();
    }
}
