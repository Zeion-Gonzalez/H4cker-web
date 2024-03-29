package android.support.transition;

import android.annotation.SuppressLint;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(22)
/* loaded from: classes.dex */
class ViewUtilsApi22 extends ViewUtilsApi21 {
    private static final String TAG = "ViewUtilsApi22";
    private static Method sSetLeftTopRightBottomMethod;
    private static boolean sSetLeftTopRightBottomMethodFetched;

    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public void setLeftTopRightBottom(View v, int left, int top, int right, int bottom) {
        fetchSetLeftTopRightBottomMethod();
        if (sSetLeftTopRightBottomMethod != null) {
            try {
                sSetLeftTopRightBottomMethod.invoke(v, Integer.valueOf(left), Integer.valueOf(top), Integer.valueOf(right), Integer.valueOf(bottom));
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        }
    }

    @SuppressLint({"PrivateApi"})
    private void fetchSetLeftTopRightBottomMethod() {
        if (!sSetLeftTopRightBottomMethodFetched) {
            try {
                sSetLeftTopRightBottomMethod = View.class.getDeclaredMethod("setLeftTopRightBottom", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                sSetLeftTopRightBottomMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve setLeftTopRightBottom method", e);
            }
            sSetLeftTopRightBottomMethodFetched = true;
        }
    }
}
