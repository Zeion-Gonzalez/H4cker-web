package com.instabug.library.util;

import android.support.annotation.NonNull;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public class PlaceHolderUtils {
    @NonNull
    public static String getPlaceHolder(InstabugCustomTextPlaceHolder.Key key, String str) {
        String str2;
        InstabugCustomTextPlaceHolder customPlaceHolders = SettingsManager.getInstance().getCustomPlaceHolders();
        return (customPlaceHolders == null || (str2 = customPlaceHolders.get(key)) == null || str2.trim().equals("")) ? str : str2;
    }
}
