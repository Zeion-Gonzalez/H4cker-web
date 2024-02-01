package com.instabug.library.instacapture.screenshot;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FieldHelper {
    private static final String FIELD_NAME_GLOBAL = "mGlobal";
    private static final String FIELD_NAME_PARAMS = "mParams";
    private static final String FIELD_NAME_ROOTS = "mRoots";
    private static final String FIELD_NAME_VIEW = "mView";
    private static final String FIELD_NAME_WINDOW_MANAGER = "mWindowManager";

    private FieldHelper() {
    }

    public static List<RootViewInfo> getRootViews(Activity activity, @IdRes @Nullable int[] iArr) {
        Object fieldValue;
        WindowManager.LayoutParams[] layoutParamsArr;
        Object[] objArr;
        boolean z;
        ArrayList arrayList = new ArrayList();
        try {
            if (Build.VERSION.SDK_INT >= 17) {
                fieldValue = getFieldValue(FIELD_NAME_GLOBAL, activity.getWindowManager());
            } else {
                fieldValue = getFieldValue(FIELD_NAME_WINDOW_MANAGER, activity.getWindowManager());
            }
            Object fieldValue2 = getFieldValue(FIELD_NAME_ROOTS, fieldValue);
            Object fieldValue3 = getFieldValue(FIELD_NAME_PARAMS, fieldValue);
            if (Build.VERSION.SDK_INT >= 19) {
                objArr = ((List) fieldValue2).toArray();
                List list = (List) fieldValue3;
                layoutParamsArr = (WindowManager.LayoutParams[]) list.toArray(new WindowManager.LayoutParams[list.size()]);
            } else {
                layoutParamsArr = (WindowManager.LayoutParams[]) fieldValue3;
                objArr = (Object[]) fieldValue2;
            }
            for (int i = 0; i < objArr.length; i++) {
                try {
                    View view = (View) getFieldValue(FIELD_NAME_VIEW, objArr[i]);
                    if (iArr != null) {
                        z = false;
                        for (int i2 : iArr) {
                            if (i2 == view.getId()) {
                                z = true;
                            }
                        }
                    } else {
                        z = false;
                    }
                    if (view.getVisibility() == 0 && !z) {
                        arrayList.add(new RootViewInfo(view, layoutParamsArr[i]));
                    }
                } catch (Exception e) {
                    InstabugSDKLogger.m1801e(FieldHelper.class, "Screenshot capturing failed in one of the viewRoots", e);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            InstabugSDKLogger.m1801e(FieldHelper.class, "Can't fine one of (WindowManager, rootObjects, paramsObject) field name so screenshot capturing failed", e2);
            return arrayList;
        }
    }

    private static Object getFieldValue(String str, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField;
        if (str.equals(FIELD_NAME_WINDOW_MANAGER)) {
            declaredField = findField(str, obj.getClass());
        } else {
            declaredField = obj.getClass().getDeclaredField(str);
        }
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    private static Field findField(String str, Class cls) throws NoSuchFieldException {
        for (Class cls2 = cls; cls2 != Object.class; cls2 = cls2.getSuperclass()) {
            for (Field field : cls2.getDeclaredFields()) {
                if (str.equals(field.getName())) {
                    return field;
                }
            }
        }
        throw new NoSuchFieldException("Field: " + str + " is not found in class: " + cls.toString());
    }
}
