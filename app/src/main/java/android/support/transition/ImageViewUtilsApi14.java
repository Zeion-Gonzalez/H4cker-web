package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

@RequiresApi(14)
/* loaded from: classes.dex */
class ImageViewUtilsApi14 implements ImageViewUtilsImpl {
    @Override // android.support.transition.ImageViewUtilsImpl
    public void startAnimateTransform(ImageView view) {
        ImageView.ScaleType scaleType = view.getScaleType();
        view.setTag(C0104R.id.save_scale_type, scaleType);
        if (scaleType == ImageView.ScaleType.MATRIX) {
            view.setTag(C0104R.id.save_image_matrix, view.getImageMatrix());
        } else {
            view.setScaleType(ImageView.ScaleType.MATRIX);
        }
        view.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
    }

    @Override // android.support.transition.ImageViewUtilsImpl
    public void animateTransform(ImageView view, Matrix matrix) {
        view.setImageMatrix(matrix);
    }

    @Override // android.support.transition.ImageViewUtilsImpl
    public void reserveEndAnimateTransform(final ImageView view, Animator animator) {
        animator.addListener(new AnimatorListenerAdapter() { // from class: android.support.transition.ImageViewUtilsApi14.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ImageView.ScaleType scaleType = (ImageView.ScaleType) view.getTag(C0104R.id.save_scale_type);
                view.setScaleType(scaleType);
                view.setTag(C0104R.id.save_scale_type, null);
                if (scaleType == ImageView.ScaleType.MATRIX) {
                    view.setImageMatrix((Matrix) view.getTag(C0104R.id.save_image_matrix));
                    view.setTag(C0104R.id.save_image_matrix, null);
                }
                animation.removeListener(this);
            }
        });
    }
}
