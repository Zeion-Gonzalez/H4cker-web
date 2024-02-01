package com.instabug.library.util;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;
import com.instabug.library.C0577R;

/* loaded from: classes.dex */
public class AttrResolver {
    @DrawableRes
    public static int getDrawableResourceId(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    @ColorInt
    public static int getTintingColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(C0577R.attr.instabug_theme_tinting_color, typedValue, true);
        return typedValue.data;
    }

    @ColorInt
    public static int getBackgroundColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(C0577R.attr.instabug_background_color, typedValue, true);
        return typedValue.data;
    }

    @ColorInt
    public static int getDividerColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(C0577R.attr.instabug_divider_color, typedValue, true);
        return typedValue.data;
    }

    public static int getDialogItemTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(C0577R.attr.instabug_dialog_item_text_color, typedValue, true);
        return typedValue.data;
    }

    @ColorInt
    public static int resolveAttributeColor(Context context, @AttrRes int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.data;
    }
}
