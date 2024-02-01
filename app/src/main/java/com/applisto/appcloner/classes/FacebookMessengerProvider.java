package com.applisto.appcloner.classes;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.classes.util.activity.AbstractContentProvider;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes2.dex */
public class FacebookMessengerProvider extends AbstractContentProvider {
    private static final String TAG = FacebookMessengerProvider.class.getSimpleName();

    @Override // com.applisto.appcloner.classes.util.activity.AbstractContentProvider, android.content.ContentProvider
    public boolean onCreate() {
        try {
            AccountManager accountManager = AccountManager.get(getContext());
            Field declaredField = accountManager.getClass().getDeclaredField("mService");
            declaredField.setAccessible(true);
            final Object obj = declaredField.get(accountManager);
            Object newProxyInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("android.accounts.IAccountManager")}, new InvocationHandler() { // from class: com.applisto.appcloner.classes.-$$Lambda$FacebookMessengerProvider$0XaPBqq638-XOLiuFh8d4Jh_YJ4
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj2, Method method, Object[] objArr) {
                    return FacebookMessengerProvider.lambda$onCreate$0(obj, obj2, method, objArr);
                }
            });
            declaredField.set(accountManager, newProxyInstance);
            Log.m15i(TAG, "onCreate; installed proxy; proxy: " + newProxyInstance);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Object lambda$onCreate$0(Object obj, Object obj2, Method method, Object[] objArr) throws Throwable {
        if ("getAccountsAsUser".equals(method.getName())) {
            return new Account[0];
        }
        if ("addAccountExplicitly".equals(method.getName())) {
            return false;
        }
        return method.invoke(obj, objArr);
    }
}
