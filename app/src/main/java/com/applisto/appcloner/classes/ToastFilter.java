package com.applisto.appcloner.classes;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.applisto.appcloner.classes.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes2.dex */
public class ToastFilter {
    private static final String TAG = ToastFilter.class.getSimpleName();
    private final boolean mBlockAllToasts;
    private final String mToastFilter;

    public ToastFilter(CloneSettings cloneSettings) {
        this.mBlockAllToasts = cloneSettings.getBoolean("blockAllToasts", false).booleanValue();
        this.mToastFilter = cloneSettings.getString("toastFilter", null);
        Log.m15i(TAG, "ToastFilter; mBlockAllToasts: " + this.mBlockAllToasts + ", mToastFilter: " + this.mToastFilter);
    }

    public void install(Context context) {
        Log.m15i(TAG, "install; ");
        if (this.mBlockAllToasts || !TextUtils.isEmpty(this.mToastFilter)) {
            try {
                final HashSet hashSet = new HashSet();
                if (!TextUtils.isEmpty(this.mToastFilter)) {
                    for (String str : this.mToastFilter.trim().split(",")) {
                        String trim = str.trim();
                        if (!TextUtils.isEmpty(trim)) {
                            hashSet.add(trim.toLowerCase(Locale.ENGLISH));
                        }
                    }
                }
                new Toast(context).cancel();
                Method declaredMethod = Toast.class.getDeclaredMethod("getService", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(null, new Object[0]);
                Field declaredField = Toast.class.getDeclaredField("sService");
                declaredField.setAccessible(true);
                final Object obj = declaredField.get(null);
                Object newProxyInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("android.app.INotificationManager")}, new InvocationHandler() { // from class: com.applisto.appcloner.classes.ToastFilter.1
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj2, Method method, Object[] objArr) throws Throwable {
                        if ("enqueueToast".equals(method.getName())) {
                            if (ToastFilter.this.mBlockAllToasts) {
                                Log.m15i(ToastFilter.TAG, "invoke; enqueueToast blocked");
                                return null;
                            }
                            String lowerCase = getToastText(objArr[1]).toLowerCase(Locale.ENGLISH);
                            Iterator it = hashSet.iterator();
                            while (it.hasNext()) {
                                if (lowerCase.contains((String) it.next())) {
                                    Log.m15i(ToastFilter.TAG, "invoke; enqueueToast blocked");
                                    return null;
                                }
                            }
                        }
                        return method.invoke(obj, objArr);
                    }

                    private String getToastText(Object obj2) {
                        TextView textView;
                        CharSequence text;
                        try {
                            Field declaredField2 = obj2.getClass().getDeclaredField("mNextView");
                            declaredField2.setAccessible(true);
                            View view = (View) declaredField2.get(obj2);
                            return (view == null || (textView = (TextView) view.findViewById(16908299)) == null || (text = textView.getText()) == null) ? "" : text.toString();
                        } catch (Exception e) {
                            Log.m21w(ToastFilter.TAG, e);
                            return "";
                        }
                    }
                });
                declaredField.set(null, newProxyInstance);
                Log.m15i(TAG, "init; installed proxy; proxy: " + newProxyInstance);
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
        }
    }
}
