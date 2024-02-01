package com.instabug.library.util;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.instabug.library.Instabug;
import com.instabug.library.settings.SettingsManager;

/* loaded from: classes.dex */
public class Colorizer {
    public static Drawable getPrimaryColorTintedDrawable(@NonNull Drawable drawable) {
        return getTintedDrawable(Instabug.getPrimaryColor(), drawable);
    }

    public static Drawable getTintedDrawable(int i, @NonNull Drawable drawable) {
        drawable.clearColorFilter();
        drawable.setColorFilter(i, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static void applyPrimaryColorTint(@NonNull ImageView imageView) {
        applyTint(Instabug.getPrimaryColor(), imageView);
    }

    public static void applyTint(int i, @NonNull ImageView imageView) {
        imageView.clearColorFilter();
        imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
    }

    public static ColorFilter getPrimaryColorFilter() {
        return new PorterDuffColorFilter(SettingsManager.getInstance().getPrimaryColor(), PorterDuff.Mode.SRC_IN);
    }
}
