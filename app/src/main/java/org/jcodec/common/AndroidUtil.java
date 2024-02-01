package org.jcodec.common;

import android.graphics.Bitmap;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.BitmapUtil;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

/* loaded from: classes.dex */
public class AndroidUtil {
    public static Bitmap toBitmap(Picture pic) {
        if (pic == null) {
            return null;
        }
        Transform transform = ColorUtil.getTransform(pic.getColor(), ColorSpace.RGB);
        Picture rgb = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.RGB, pic.getCrop());
        transform.transform(pic, rgb);
        return BitmapUtil.toBitmap(rgb);
    }

    public static void toBitmap(Picture pic, Bitmap out) {
        if (pic == null) {
            throw new IllegalArgumentException("Input pic is null");
        }
        if (out == null) {
            throw new IllegalArgumentException("Out bitmap is null");
        }
        Transform transform = ColorUtil.getTransform(pic.getColor(), ColorSpace.RGB);
        Picture rgb = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.RGB, pic.getCrop());
        transform.transform(pic, rgb);
        BitmapUtil.toBitmap(rgb, out);
    }
}
