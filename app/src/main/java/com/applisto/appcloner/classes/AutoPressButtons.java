package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.OnAppExitListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes2.dex */
public class AutoPressButtons extends OnAppExitListener {
    private static final int KEY_TAG_DELAY_SECONDS = 346548731;
    private static final int KEY_TAG_RUNNABLE = 346548732;
    private static final String TAG = AutoPressButtons.class.getSimpleName();
    private boolean mReady;
    private Handler mHandler = new Handler();
    private Map<Pair<String, String>, Info> mAutoPressButtons = new HashMap();

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected int getActivityTimerDelayMillis() {
        return 500;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Info {
        Integer delaySeconds;
        Boolean enabled;
        String screenText;

        private Info() {
        }

        public String toString() {
            return "Info{screenText='" + this.screenText + "', delaySeconds=" + this.delaySeconds + ", enabled=" + this.enabled + '}';
        }
    }

    public AutoPressButtons(CloneSettings cloneSettings) {
        List<CloneSettings> forObjectArray = cloneSettings.forObjectArray("autoPressButtons");
        Log.m15i(TAG, "AutoPressButtons; autoPressButtons: " + forObjectArray);
        if (forObjectArray != null) {
            for (CloneSettings cloneSettings2 : forObjectArray) {
                String string = cloneSettings2.getString("buttonId", null);
                String string2 = cloneSettings2.getString("buttonLabel", null);
                if (!TextUtils.isEmpty(string) || !TextUtils.isEmpty(string2)) {
                    String lowerCase = string2.trim().toLowerCase(Locale.ENGLISH);
                    Info info = new Info();
                    String string3 = cloneSettings2.getString("screenText", null);
                    if (!TextUtils.isEmpty(string3)) {
                        info.screenText = string3.trim().toLowerCase(Locale.ENGLISH);
                    }
                    info.delaySeconds = cloneSettings2.getInteger("delaySeconds", null);
                    if (cloneSettings2.getBoolean("pressOnceOnly", false).booleanValue()) {
                        info.enabled = true;
                    }
                    this.mAutoPressButtons.put(new Pair<>(string, lowerCase), info);
                }
            }
        }
        Log.m15i(TAG, "AutoPressButtons; mAutoPressButtons: " + this.mAutoPressButtons);
    }

    public void install() {
        Log.m15i(TAG, "install; ");
        if (this.mAutoPressButtons.isEmpty()) {
            return;
        }
        onCreate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener, com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    public void onActivityCreated(final Activity activity) {
        super.onActivityCreated(activity);
        if (Build.VERSION.SDK_INT >= 18) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$AutoPressButtons$r_ZT5KMnSm9Q1F6792uqyQ700Yc
                @Override // java.lang.Runnable
                public final void run() {
                    AutoPressButtons.this.lambda$onActivityCreated$1$AutoPressButtons(activity);
                }
            }, 1000L);
        }
    }

    public /* synthetic */ void lambda$onActivityCreated$1$AutoPressButtons(Activity activity) {
        View findViewById = activity.findViewById(16908290);
        Log.m15i(TAG, "run; rootView: " + findViewById);
        if (findViewById != null) {
            findViewById.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() { // from class: com.applisto.appcloner.classes.-$$Lambda$AutoPressButtons$lIlimpYQIWc-cpThJE2A5QfdhKs
                @Override // android.view.ViewTreeObserver.OnWindowFocusChangeListener
                public final void onWindowFocusChanged(boolean z) {
                    AutoPressButtons.this.lambda$null$0$AutoPressButtons(z);
                }
            });
            this.mReady = true;
            Log.m15i(TAG, "run; now ready");
        }
    }

    public /* synthetic */ void lambda$null$0$AutoPressButtons(boolean z) {
        Log.m15i(TAG, "onWindowFocusChanged; ");
        checkForButtons();
    }

    @Override // com.applisto.appcloner.classes.util.activity.ActivityLifecycleListener
    protected void onActivityTimer(Activity activity) {
        Log.m15i(TAG, "onActivityTimer; activity: " + activity);
        checkForButtons();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.applisto.appcloner.classes.util.activity.OnAppExitListener
    /* renamed from: onAppExit */
    public void lambda$onActivityDestroyed$0$OnAppExitListener(Context context) {
        Log.m15i(TAG, "onAppExit; ");
        try {
            this.mReady = false;
            for (Info info : this.mAutoPressButtons.values()) {
                if (info.enabled != null) {
                    info.enabled = true;
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private synchronized void checkForButtons() {
        Log.m15i(TAG, "checkForButtons; ");
        if (!this.mReady) {
            Log.m15i(TAG, "checkForButtons; not ready");
            return;
        }
        try {
            for (ViewParent viewParent : Utils.getViewRoots()) {
                try {
                    Field declaredField = viewParent.getClass().getDeclaredField("mView");
                    declaredField.setAccessible(true);
                    for (final View view : new ButtonViewsFinder((View) declaredField.get(viewParent)).findViews()) {
                        Integer num = (Integer) view.getTag(KEY_TAG_DELAY_SECONDS);
                        Log.m15i(TAG, "checkForButtons; button: " + view + ", delaySeconds: " + num);
                        if (num != null && num.intValue() != 0) {
                            if (view.getTag(KEY_TAG_RUNNABLE) == null) {
                                Runnable runnable = new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$AutoPressButtons$No7Vz19UzTa0qAUcAvMBrJNiYy0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AutoPressButtons.this.lambda$checkForButtons$2$AutoPressButtons(view);
                                    }
                                };
                                view.setTag(KEY_TAG_RUNNABLE, runnable);
                                view.postDelayed(runnable, num.intValue() * 1000);
                            }
                        }
                        performClick(view);
                    }
                } catch (Exception e) {
                    Log.m21w(TAG, e);
                }
            }
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
    }

    public /* synthetic */ void lambda$checkForButtons$2$AutoPressButtons(View view) {
        performClick(view);
        view.setTag(KEY_TAG_RUNNABLE, null);
    }

    private void performClick(View view) {
        if (view.performClick()) {
            view.setTag(KEY_TAG_DELAY_SECONDS, Long.valueOf(System.currentTimeMillis()));
            Log.m15i(TAG, "performClick; button clicked: " + view);
            return;
        }
        Log.m19w(TAG, "performClick; button not clicked " + view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class ViewsFinder {
        boolean mFindAll;
        View mRootView;
        private final List<View> mViews = new ArrayList();

        abstract boolean matchesView(View view);

        ViewsFinder(View view, boolean z) {
            this.mRootView = view;
            this.mFindAll = z;
        }

        List<View> findViews() {
            this.mViews.clear();
            findViews(this.mRootView);
            return this.mViews;
        }

        boolean findViews(View view) {
            try {
                if (matchesView(view)) {
                    this.mViews.add(view);
                    if (!this.mFindAll) {
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.m21w(AutoPressButtons.TAG, e);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (findViews(viewGroup.getChildAt(i)) && !this.mFindAll) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class ButtonViewsFinder extends ViewsFinder {
        ButtonViewsFinder(View view) {
            super(view, true);
        }

        /* JADX WARN: Removed duplicated region for block: B:16:0x0039  */
        /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
        @Override // com.applisto.appcloner.classes.AutoPressButtons.ViewsFinder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        boolean matchesView(android.view.View r4) {
            /*
                r3 = this;
                boolean r0 = r4.isClickable()
                if (r0 == 0) goto L41
                int r0 = r4.getId()
                r1 = -1
                r2 = 0
                if (r0 == r1) goto L17
                android.content.res.Resources r1 = r4.getResources()     // Catch: java.lang.Exception -> L17
                java.lang.String r0 = r1.getResourceEntryName(r0)     // Catch: java.lang.Exception -> L17
                goto L18
            L17:
                r0 = r2
            L18:
                boolean r1 = r4 instanceof android.widget.TextView
                if (r1 == 0) goto L33
                r1 = r4
                android.widget.TextView r1 = (android.widget.TextView) r1
                java.lang.CharSequence r1 = r1.getText()
                if (r1 == 0) goto L33
                java.lang.String r1 = r1.toString()
                java.lang.String r1 = r1.trim()
                java.util.Locale r2 = java.util.Locale.ENGLISH
                java.lang.String r2 = r1.toLowerCase(r2)
            L33:
                java.lang.Integer r0 = r3.checkIdLabel(r0, r2)
                if (r0 == 0) goto L41
                r1 = 346548731(0x14a7e9fb, float:1.695498E-26)
                r4.setTag(r1, r0)
                r4 = 1
                return r4
            L41:
                r4 = 0
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.applisto.appcloner.classes.AutoPressButtons.ButtonViewsFinder.matchesView(android.view.View):boolean");
        }

        private Integer checkIdLabel(String str, String str2) {
            for (Pair pair : AutoPressButtons.this.mAutoPressButtons.keySet()) {
                String str3 = (String) pair.first;
                String str4 = (String) pair.second;
                if ((!TextUtils.isEmpty(str3) && str3.equals(str)) || (!TextUtils.isEmpty(str4) && str4.equals(str2))) {
                    Info info = (Info) AutoPressButtons.this.mAutoPressButtons.get(pair);
                    if ((info.enabled != null && !info.enabled.booleanValue()) || (!TextUtils.isEmpty(info.screenText) && new TextViewsFinder(this.mRootView, info.screenText).findViews().isEmpty())) {
                        return null;
                    }
                    if (info.enabled != null) {
                        info.enabled = false;
                    }
                    return info.delaySeconds;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class TextViewsFinder extends ViewsFinder {
        private String mScreenText;

        TextViewsFinder(View view, String str) {
            super(view, false);
            this.mScreenText = str;
        }

        @Override // com.applisto.appcloner.classes.AutoPressButtons.ViewsFinder
        boolean matchesView(View view) {
            CharSequence text;
            return (view instanceof TextView) && (text = ((TextView) view).getText()) != null && text.toString().trim().toLowerCase(Locale.ENGLISH).contains(this.mScreenText);
        }
    }
}
