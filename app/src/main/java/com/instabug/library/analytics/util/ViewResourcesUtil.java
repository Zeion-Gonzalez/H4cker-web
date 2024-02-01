package com.instabug.library.analytics.util;

import android.content.Context;
import android.content.res.Resources;

/* loaded from: classes.dex */
public class ViewResourcesUtil {
    public static String getViewResourceIdAsString(Context context, int i) {
        String resourceEntryName;
        if (context != null) {
            try {
                if (context.getResources() != null && context.getResources().getResourceEntryName(i) != null) {
                    resourceEntryName = context.getResources().getResourceEntryName(i);
                    return resourceEntryName;
                }
            } catch (Resources.NotFoundException e) {
                return String.valueOf(i);
            }
        }
        resourceEntryName = String.valueOf(i);
        return resourceEntryName;
    }
}
