package com.instabug.library.util;

import android.support.annotation.StyleRes;
import com.instabug.library.C0577R;
import com.instabug.library.InstabugColorTheme;

/* loaded from: classes.dex */
public class InstabugThemeResolver {
    @StyleRes
    public static int resolveTheme(InstabugColorTheme instabugColorTheme) {
        return instabugColorTheme == InstabugColorTheme.InstabugColorThemeLight ? C0577R.style.InstabugSdkTheme_Light : C0577R.style.InstabugSdkTheme_Dark;
    }
}
