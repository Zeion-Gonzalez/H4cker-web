package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import com.applisto.appcloner.classes.PasswordActivity;
import com.applisto.appcloner.classes.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class PasswordActivity extends Activity {
    public static boolean sUnlocked;
    private String mDecoyPackageName;
    private String mDecoyPassword;
    private EditText mEditText;
    private boolean mExitAppIfPasswordIncorrect;
    private boolean mHidePasswordCharacters;
    private String mOriginalActivityName;
    private String mPassword;
    private boolean mPasswordProtectApp;
    private SharedPreferences mPreferences;
    private boolean mStealthMode;
    private boolean mStealthModeUseFingerprint;
    private static final String TAG = PasswordActivity.class.getSimpleName();
    public static final String PREF_KEY_PASSWORD_ENTERED = PasswordActivity.class.getName() + "_passwordEntered";
    private List<Dialog> mDialogs = new ArrayList();
    private Handler mHandler = new Handler();

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Bundle bundle2 = getPackageManager().getActivityInfo(getComponentName(), 129).metaData;
            if (bundle2 != null) {
                this.mOriginalActivityName = bundle2.getString("com.applisto.appcloner.originalActivityName");
                if (this.mOriginalActivityName != null && this.mOriginalActivityName.startsWith(".")) {
                    this.mOriginalActivityName = getPackageName() + this.mOriginalActivityName;
                }
                CloneSettings cloneSettings = CloneSettings.getInstance(this);
                this.mPasswordProtectApp = cloneSettings.getBoolean("passwordProtectApp", false).booleanValue();
                this.mPassword = cloneSettings.getString("appPassword", null);
                if (cloneSettings.has("stealthMode")) {
                    this.mStealthMode = cloneSettings.getBoolean("stealthMode", false).booleanValue();
                } else {
                    this.mStealthMode = cloneSettings.getBoolean("appPasswordStealthMode", false).booleanValue();
                }
                this.mStealthModeUseFingerprint = cloneSettings.getBoolean("stealthModeUseFingerprint", false).booleanValue();
                this.mHidePasswordCharacters = cloneSettings.getBoolean("hidePasswordCharacters", false).booleanValue();
                boolean z = true;
                this.mExitAppIfPasswordIncorrect = cloneSettings.getBoolean("exitAppIfPasswordIncorrect", true).booleanValue();
                if (TextUtils.isEmpty(this.mPassword) || !cloneSettings.getBoolean("appPasswordAskOnlyOnce", false).booleanValue()) {
                    z = false;
                }
                this.mDecoyPassword = cloneSettings.getString("decoyPassword", null);
                this.mDecoyPackageName = cloneSettings.getString("decoyPackageName", null);
                if (z) {
                    boolean z2 = this.mPreferences.getBoolean(PREF_KEY_PASSWORD_ENTERED, false);
                    Log.m15i(TAG, "onCreate; passwordEntered: " + z2);
                    if (z2) {
                        startApp();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
            exit();
        }
        showDialog();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00fe A[Catch: all -> 0x0210, TryCatch #1 {all -> 0x0210, blocks: (B:3:0x0004, B:8:0x0015, B:10:0x0026, B:14:0x0036, B:16:0x0049, B:31:0x00f4, B:32:0x00f9, B:34:0x00fe, B:36:0x010f, B:51:0x0182, B:53:0x0188, B:54:0x0190, B:56:0x01a5, B:57:0x01ad, B:58:0x01b5, B:60:0x01d5, B:64:0x01e5, B:66:0x0202, B:63:0x01e0, B:35:0x0109, B:37:0x0113, B:39:0x011b, B:41:0x011f, B:44:0x0132, B:50:0x0169, B:49:0x0164, B:9:0x001e, B:46:0x014d), top: B:73:0x0004, inners: #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0109 A[Catch: all -> 0x0210, TryCatch #1 {all -> 0x0210, blocks: (B:3:0x0004, B:8:0x0015, B:10:0x0026, B:14:0x0036, B:16:0x0049, B:31:0x00f4, B:32:0x00f9, B:34:0x00fe, B:36:0x010f, B:51:0x0182, B:53:0x0188, B:54:0x0190, B:56:0x01a5, B:57:0x01ad, B:58:0x01b5, B:60:0x01d5, B:64:0x01e5, B:66:0x0202, B:63:0x01e0, B:35:0x0109, B:37:0x0113, B:39:0x011b, B:41:0x011f, B:44:0x0132, B:50:0x0169, B:49:0x0164, B:9:0x001e, B:46:0x014d), top: B:73:0x0004, inners: #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0188 A[Catch: all -> 0x0210, TryCatch #1 {all -> 0x0210, blocks: (B:3:0x0004, B:8:0x0015, B:10:0x0026, B:14:0x0036, B:16:0x0049, B:31:0x00f4, B:32:0x00f9, B:34:0x00fe, B:36:0x010f, B:51:0x0182, B:53:0x0188, B:54:0x0190, B:56:0x01a5, B:57:0x01ad, B:58:0x01b5, B:60:0x01d5, B:64:0x01e5, B:66:0x0202, B:63:0x01e0, B:35:0x0109, B:37:0x0113, B:39:0x011b, B:41:0x011f, B:44:0x0132, B:50:0x0169, B:49:0x0164, B:9:0x001e, B:46:0x014d), top: B:73:0x0004, inners: #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x01a5 A[Catch: all -> 0x0210, TryCatch #1 {all -> 0x0210, blocks: (B:3:0x0004, B:8:0x0015, B:10:0x0026, B:14:0x0036, B:16:0x0049, B:31:0x00f4, B:32:0x00f9, B:34:0x00fe, B:36:0x010f, B:51:0x0182, B:53:0x0188, B:54:0x0190, B:56:0x01a5, B:57:0x01ad, B:58:0x01b5, B:60:0x01d5, B:64:0x01e5, B:66:0x0202, B:63:0x01e0, B:35:0x0109, B:37:0x0113, B:39:0x011b, B:41:0x011f, B:44:0x0132, B:50:0x0169, B:49:0x0164, B:9:0x001e, B:46:0x014d), top: B:73:0x0004, inners: #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01d5 A[Catch: Exception -> 0x01df, all -> 0x0210, TRY_LEAVE, TryCatch #2 {Exception -> 0x01df, blocks: (B:58:0x01b5, B:60:0x01d5), top: B:74:0x01b5, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0202 A[Catch: all -> 0x0210, TRY_LEAVE, TryCatch #1 {all -> 0x0210, blocks: (B:3:0x0004, B:8:0x0015, B:10:0x0026, B:14:0x0036, B:16:0x0049, B:31:0x00f4, B:32:0x00f9, B:34:0x00fe, B:36:0x010f, B:51:0x0182, B:53:0x0188, B:54:0x0190, B:56:0x01a5, B:57:0x01ad, B:58:0x01b5, B:60:0x01d5, B:64:0x01e5, B:66:0x0202, B:63:0x01e0, B:35:0x0109, B:37:0x0113, B:39:0x011b, B:41:0x011f, B:44:0x0132, B:50:0x0169, B:49:0x0164, B:9:0x001e, B:46:0x014d), top: B:73:0x0004, inners: #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void showDialog() {
        /*
            Method dump skipped, instructions count: 538
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.applisto.appcloner.classes.PasswordActivity.showDialog():void");
    }

    public /* synthetic */ void lambda$showDialog$0$PasswordActivity(View view) {
        exit();
    }

    public /* synthetic */ boolean lambda$showDialog$1$PasswordActivity(TextView textView, int i, KeyEvent keyEvent) {
        onOk();
        return false;
    }

    public /* synthetic */ void lambda$showDialog$2$PasswordActivity(DialogInterface dialogInterface, int i) {
        exit();
    }

    public /* synthetic */ void lambda$showDialog$3$PasswordActivity(DialogInterface dialogInterface, int i) {
        onOk();
    }

    public /* synthetic */ void lambda$showDialog$4$PasswordActivity(DialogInterface dialogInterface) {
        if (sUnlocked) {
            return;
        }
        exit();
    }

    public /* synthetic */ void lambda$showDialog$5$PasswordActivity(View view) {
        onOk();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listenFingerprints(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService("fingerprint");
        if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
            return;
        }
        fingerprintManager.authenticate(null, null, 0, new C04392(context), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.applisto.appcloner.classes.PasswordActivity$2 */
    /* loaded from: classes2.dex */
    public class C04392 extends FingerprintManager.AuthenticationCallback {
        final /* synthetic */ Context val$context;

        C04392(Context context) {
            this.val$context = context;
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
            Log.m15i(PasswordActivity.TAG, "onAuthenticationSucceeded; ");
            try {
                PasswordActivity.this.onDoubleLongPress();
            } catch (Exception e) {
                Log.m21w(PasswordActivity.TAG, e);
            }
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationFailed() {
            Log.m15i(PasswordActivity.TAG, "onAuthenticationFailed; ");
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationError(int i, CharSequence charSequence) {
            Log.m15i(PasswordActivity.TAG, "onAuthenticationError; errorCode: " + i + ", errString: " + ((Object) charSequence));
            Handler handler = PasswordActivity.this.mHandler;
            final Context context = this.val$context;
            handler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$PasswordActivity$2$W-9YASUACguDr_7MTu_lz-eCQhs
                @Override // java.lang.Runnable
                public final void run() {
                    PasswordActivity.C04392.this.lambda$onAuthenticationError$0$PasswordActivity$2(context);
                }
            }, 1000L);
        }

        public /* synthetic */ void lambda$onAuthenticationError$0$PasswordActivity$2(Context context) {
            PasswordActivity.this.listenFingerprints(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.applisto.appcloner.classes.PasswordActivity$3 */
    /* loaded from: classes2.dex */
    public class RunnableC04403 implements Runnable {
        private int mCount;
        private Handler mHandler = new Handler();

        RunnableC04403() {
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mCount++;
            if (this.mCount >= 2) {
                PasswordActivity.this.onDoubleLongPress();
            } else {
                this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$PasswordActivity$3$dtKTXKLiCIp97MWlz7ZSfdVOtDc
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasswordActivity.RunnableC04403.this.lambda$run$0$PasswordActivity$3();
                    }
                }, 3000L);
            }
        }

        public /* synthetic */ void lambda$run$0$PasswordActivity$3() {
            this.mCount = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Runnable getLongPressRunnable() {
        return new RunnableC04403();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDoubleLongPress() {
        if (!this.mPasswordProtectApp || TextUtils.isEmpty(this.mPassword)) {
            startApp();
        } else {
            this.mStealthMode = false;
            showDialog();
        }
    }

    private void onOk() {
        boolean z = false;
        try {
            if (this.mEditText != null) {
                String obj = this.mEditText.getText().toString();
                if (!TextUtils.isEmpty(this.mDecoyPassword) && obj.equals(this.mDecoyPassword)) {
                    startDecoyApp();
                    exit();
                } else {
                    z = this.mPassword.equals(obj);
                }
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        if (z) {
            try {
                this.mPreferences.edit().putBoolean(PREF_KEY_PASSWORD_ENTERED, true).apply();
            } catch (Exception e2) {
                Log.m21w(TAG, e2);
            }
            startApp();
            return;
        }
        if (this.mExitAppIfPasswordIncorrect) {
            exit();
            return;
        }
        EditText editText = this.mEditText;
        if (editText != null) {
            editText.setText("");
            this.mEditText.getRootView().animate().translationX(Utils.dp2px(this, 4.0f)).setInterpolator(new CycleInterpolator(6.0f));
        }
    }

    private void startApp() {
        Log.m15i(TAG, "startApp; ");
        sUnlocked = true;
        try {
            Iterator<Dialog> it = this.mDialogs.iterator();
            while (it.hasNext()) {
                it.next().dismiss();
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        try {
            Intent intent = new Intent(getIntent());
            intent.setComponent(new ComponentName(this, Class.forName(this.mOriginalActivityName)));
            intent.setFlags(268435456);
            startActivity(intent);
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
        finish();
    }

    private void startDecoyApp() {
        Log.m15i(TAG, "startDecoyApp; ");
        try {
            Intent launchIntent = Utils.getLaunchIntent(this, this.mDecoyPackageName);
            if (launchIntent != null) {
                launchIntent.setFlags(335544320);
                startActivity(launchIntent);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        finish();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [com.applisto.appcloner.classes.PasswordActivity$4] */
    private void exit() {
        Log.m15i(TAG, "exit; ");
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
        new Thread() { // from class: com.applisto.appcloner.classes.PasswordActivity.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(250L);
                } catch (InterruptedException unused) {
                }
                System.exit(0);
            }
        }.start();
    }
}
