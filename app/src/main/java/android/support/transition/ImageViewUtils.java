package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.os.Build;
import android.widget.ImageView;

/* loaded from: classes.dex */
class ImageViewUtils {
    private static final ImageViewUtilsImpl IMPL;

    ImageViewUtils() {
    }

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ImageViewUtilsApi21();
        } else {
            IMPL = new ImageViewUtilsApi14();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startAnimateTransform(ImageView view) {
        IMPL.startAnimateTransform(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void animateTransform(ImageView view, Matrix matrix) {
        IMPL.animateTransform(view, matrix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reserveEndAnimateTransform(ImageView view, Animator animator) {
        IMPL.reserveEndAnimateTransform(view, animator);
    }
}
