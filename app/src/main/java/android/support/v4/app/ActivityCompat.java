package android.support.v4.app;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.view.View;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ActivityCompat extends ContextCompat {

    /* loaded from: classes.dex */
    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public interface RequestPermissionsRequestCodeValidator {
        void validateRequestPermissionsRequestCode(int i);
    }

    protected ActivityCompat() {
    }

    public static boolean invalidateOptionsMenu(Activity activity) {
        activity.invalidateOptionsMenu();
        return true;
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, @Nullable Bundle options) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void startIntentSenderForResult(Activity activity, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public static void finishAffinity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.finishAffinity();
        } else {
            activity.finish();
        }
    }

    public static void finishAfterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    @Nullable
    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= 22) {
            return activity.getReferrer();
        }
        Intent intent = activity.getIntent();
        Uri referrer = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        if (referrer == null) {
            String referrerName = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
            if (referrerName != null) {
                return Uri.parse(referrerName);
            }
            return null;
        }
        return referrer;
    }

    public static void setEnterSharedElementCallback(Activity activity, SharedElementCallback callback) {
        android.app.SharedElementCallback frameworkCallback;
        if (Build.VERSION.SDK_INT >= 23) {
            frameworkCallback = callback != null ? new SharedElementCallback23Impl(callback) : null;
            activity.setEnterSharedElementCallback(frameworkCallback);
        } else if (Build.VERSION.SDK_INT >= 21) {
            frameworkCallback = callback != null ? new SharedElementCallback21Impl(callback) : null;
            activity.setEnterSharedElementCallback(frameworkCallback);
        }
    }

    public static void setExitSharedElementCallback(Activity activity, SharedElementCallback callback) {
        android.app.SharedElementCallback frameworkCallback;
        if (Build.VERSION.SDK_INT >= 23) {
            frameworkCallback = callback != null ? new SharedElementCallback23Impl(callback) : null;
            activity.setExitSharedElementCallback(frameworkCallback);
        } else if (Build.VERSION.SDK_INT >= 21) {
            frameworkCallback = callback != null ? new SharedElementCallback21Impl(callback) : null;
            activity.setExitSharedElementCallback(frameworkCallback);
        }
    }

    public static void postponeEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.postponeEnterTransition();
        }
    }

    public static void startPostponedEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.startPostponedEnterTransition();
        }
    }

    public static void requestPermissions(@NonNull final Activity activity, @NonNull final String[] permissions, @IntRange(from = 0) final int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity instanceof RequestPermissionsRequestCodeValidator) {
                ((RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(requestCode);
            }
            activity.requestPermissions(permissions, requestCode);
        } else if (activity instanceof OnRequestPermissionsResultCallback) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() { // from class: android.support.v4.app.ActivityCompat.1
                @Override // java.lang.Runnable
                public void run() {
                    int[] grantResults = new int[permissions.length];
                    PackageManager packageManager = activity.getPackageManager();
                    String packageName = activity.getPackageName();
                    int permissionCount = permissions.length;
                    for (int i = 0; i < permissionCount; i++) {
                        grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
                    }
                    ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            });
        }
    }

    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    @RequiresApi(21)
    /* loaded from: classes.dex */
    private static class SharedElementCallback21Impl extends android.app.SharedElementCallback {
        protected SharedElementCallback mCallback;

        public SharedElementCallback21Impl(SharedElementCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            this.mCallback.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            this.mCallback.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        }

        @Override // android.app.SharedElementCallback
        public void onRejectSharedElements(List<View> rejectedSharedElements) {
            this.mCallback.onRejectSharedElements(rejectedSharedElements);
        }

        @Override // android.app.SharedElementCallback
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            this.mCallback.onMapSharedElements(names, sharedElements);
        }

        @Override // android.app.SharedElementCallback
        public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
            return this.mCallback.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        }

        @Override // android.app.SharedElementCallback
        public View onCreateSnapshotView(Context context, Parcelable snapshot) {
            return this.mCallback.onCreateSnapshotView(context, snapshot);
        }
    }

    @RequiresApi(23)
    /* loaded from: classes.dex */
    private static class SharedElementCallback23Impl extends SharedElementCallback21Impl {
        public SharedElementCallback23Impl(SharedElementCallback callback) {
            super(callback);
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, final SharedElementCallback.OnSharedElementsReadyListener listener) {
            this.mCallback.onSharedElementsArrived(sharedElementNames, sharedElements, new SharedElementCallback.OnSharedElementsReadyListener() { // from class: android.support.v4.app.ActivityCompat.SharedElementCallback23Impl.1
                @Override // android.support.v4.app.SharedElementCallback.OnSharedElementsReadyListener
                public void onSharedElementsReady() {
                    listener.onSharedElementsReady();
                }
            });
        }
    }
}
