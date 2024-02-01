package com.instabug.library.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import java.util.Locale;

/* loaded from: classes.dex */
public class LocaleUtils {
    public static void setLocale(Activity activity, Locale locale) {
        Configuration configuration = activity.getApplicationContext().getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 24) {
            configuration.setLocales(new LocaleList(locale));
            updateResources(activity, locale);
        } else if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
    }

    @TargetApi(24)
    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    public static String getLocaleStringResource(Locale locale, int i, Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            Configuration configuration = new Configuration(context.getResources().getConfiguration());
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration).getText(i).toString();
        }
        Resources resources = context.getResources();
        Configuration configuration2 = resources.getConfiguration();
        Locale locale2 = configuration2.locale;
        configuration2.locale = locale;
        resources.updateConfiguration(configuration2, null);
        String string = resources.getString(i);
        configuration2.locale = locale2;
        resources.updateConfiguration(configuration2, null);
        return string;
    }

    public static boolean isRTL(Locale locale) {
        if (Build.VERSION.SDK_INT >= 17) {
            return TextUtils.getLayoutDirectionFromLocale(locale) == 1;
        }
        byte directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == 1 || directionality == 2;
    }
}
