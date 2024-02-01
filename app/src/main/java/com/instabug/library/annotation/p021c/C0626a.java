package com.instabug.library.annotation.p021c;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.ViewCompat;
import java.lang.reflect.Array;

/* compiled from: BitmapUtility.java */
/* renamed from: com.instabug.library.annotation.c.a */
/* loaded from: classes.dex */
public class C0626a {
    /* renamed from: a */
    public static Bitmap m1138a(Bitmap bitmap, int i, Context context) {
        Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        if (Build.VERSION.SDK_INT > 16) {
            RenderScript create = RenderScript.create(context);
            Allocation createFromBitmap = Allocation.createFromBitmap(create, bitmap, Allocation.MipmapControl.MIPMAP_NONE, 1);
            Allocation createTyped = Allocation.createTyped(create, createFromBitmap.getType());
            ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
            create2.setRadius(i);
            create2.setInput(createFromBitmap);
            create2.forEach(createTyped);
            createTyped.copyTo(copy);
            return copy;
        }
        if (i < 1) {
            return null;
        }
        int width = copy.getWidth();
        int height = copy.getHeight();
        int[] iArr = new int[width * height];
        copy.getPixels(iArr, 0, width, 0, 0, width, height);
        int i2 = width - 1;
        int i3 = height - 1;
        int i4 = width * height;
        int i5 = i + i + 1;
        int[] iArr2 = new int[i4];
        int[] iArr3 = new int[i4];
        int[] iArr4 = new int[i4];
        int[] iArr5 = new int[Math.max(width, height)];
        int i6 = (i5 + 1) >> 1;
        int i7 = i6 * i6;
        int[] iArr6 = new int[i7 * 256];
        for (int i8 = 0; i8 < i7 * 256; i8++) {
            iArr6[i8] = i8 / i7;
        }
        int[][] iArr7 = (int[][]) Array.newInstance(Integer.TYPE, i5, 3);
        int i9 = i + 1;
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        while (true) {
            int i13 = i10;
            if (i13 >= height) {
                break;
            }
            int i14 = 0;
            int i15 = 0;
            int i16 = 0;
            int i17 = 0;
            int i18 = 0;
            int i19 = 0;
            int i20 = 0;
            int i21 = 0;
            int i22 = 0;
            for (int i23 = -i; i23 <= i; i23++) {
                int i24 = iArr[Math.min(i2, Math.max(i23, 0)) + i12];
                int[] iArr8 = iArr7[i23 + i];
                iArr8[0] = (16711680 & i24) >> 16;
                iArr8[1] = (65280 & i24) >> 8;
                iArr8[2] = i24 & 255;
                int abs = i9 - Math.abs(i23);
                i21 += iArr8[0] * abs;
                i20 += iArr8[1] * abs;
                i19 += abs * iArr8[2];
                if (i23 > 0) {
                    i15 += iArr8[0];
                    i22 += iArr8[1];
                    i14 += iArr8[2];
                } else {
                    i18 += iArr8[0];
                    i17 += iArr8[1];
                    i16 += iArr8[2];
                }
            }
            int i25 = i21;
            int i26 = i20;
            int i27 = i19;
            int i28 = i12;
            int i29 = i;
            for (int i30 = 0; i30 < width; i30++) {
                iArr2[i28] = iArr6[i25];
                iArr3[i28] = iArr6[i26];
                iArr4[i28] = iArr6[i27];
                int i31 = i25 - i18;
                int i32 = i26 - i17;
                int i33 = i27 - i16;
                int[] iArr9 = iArr7[((i29 - i) + i5) % i5];
                int i34 = i18 - iArr9[0];
                int i35 = i17 - iArr9[1];
                int i36 = i16 - iArr9[2];
                if (i13 == 0) {
                    iArr5[i30] = Math.min(i30 + i + 1, i2);
                }
                int i37 = iArr[iArr5[i30] + i11];
                iArr9[0] = (16711680 & i37) >> 16;
                iArr9[1] = (65280 & i37) >> 8;
                iArr9[2] = i37 & 255;
                int i38 = i15 + iArr9[0];
                int i39 = i22 + iArr9[1];
                int i40 = i14 + iArr9[2];
                i25 = i31 + i38;
                i26 = i32 + i39;
                i27 = i33 + i40;
                i29 = (i29 + 1) % i5;
                int[] iArr10 = iArr7[i29 % i5];
                i18 = i34 + iArr10[0];
                i17 = i35 + iArr10[1];
                i16 = i36 + iArr10[2];
                i15 = i38 - iArr10[0];
                i22 = i39 - iArr10[1];
                i14 = i40 - iArr10[2];
                i28++;
            }
            i10 = i13 + 1;
            i11 += width;
            i12 = i28;
        }
        for (int i41 = 0; i41 < width; i41++) {
            int i42 = 0;
            int i43 = (-i) * width;
            int i44 = 0;
            int i45 = 0;
            int i46 = 0;
            int i47 = 0;
            int i48 = -i;
            int i49 = 0;
            int i50 = 0;
            int i51 = 0;
            int i52 = 0;
            while (i48 <= i) {
                int max = Math.max(0, i43) + i41;
                int[] iArr11 = iArr7[i48 + i];
                iArr11[0] = iArr2[max];
                iArr11[1] = iArr3[max];
                iArr11[2] = iArr4[max];
                int abs2 = i9 - Math.abs(i48);
                int i53 = (iArr2[max] * abs2) + i51;
                int i54 = (iArr3[max] * abs2) + i50;
                int i55 = (iArr4[max] * abs2) + i49;
                if (i48 > 0) {
                    i44 += iArr11[0];
                    i52 += iArr11[1];
                    i42 += iArr11[2];
                } else {
                    i47 += iArr11[0];
                    i46 += iArr11[1];
                    i45 += iArr11[2];
                }
                if (i48 < i3) {
                    i43 += width;
                }
                i48++;
                i49 = i55;
                i50 = i54;
                i51 = i53;
            }
            int i56 = i50;
            int i57 = i51;
            int i58 = i49;
            int i59 = i41;
            int i60 = i42;
            int i61 = i52;
            int i62 = i44;
            int i63 = i45;
            int i64 = i46;
            int i65 = i47;
            int i66 = i;
            for (int i67 = 0; i67 < height; i67++) {
                iArr[i59] = ((-16777216) & iArr[i59]) | (iArr6[i57] << 16) | (iArr6[i56] << 8) | iArr6[i58];
                int i68 = i57 - i65;
                int i69 = i56 - i64;
                int i70 = i58 - i63;
                int[] iArr12 = iArr7[((i66 - i) + i5) % i5];
                int i71 = i65 - iArr12[0];
                int i72 = i64 - iArr12[1];
                int i73 = i63 - iArr12[2];
                if (i41 == 0) {
                    iArr5[i67] = Math.min(i67 + i9, i3) * width;
                }
                int i74 = iArr5[i67] + i41;
                iArr12[0] = iArr2[i74];
                iArr12[1] = iArr3[i74];
                iArr12[2] = iArr4[i74];
                int i75 = i62 + iArr12[0];
                int i76 = i61 + iArr12[1];
                int i77 = i60 + iArr12[2];
                i57 = i68 + i75;
                i56 = i69 + i76;
                i58 = i70 + i77;
                i66 = (i66 + 1) % i5;
                int[] iArr13 = iArr7[i66];
                i65 = i71 + iArr13[0];
                i64 = i72 + iArr13[1];
                i63 = i73 + iArr13[2];
                i62 = i75 - iArr13[0];
                i61 = i76 - iArr13[1];
                i60 = i77 - iArr13[2];
                i59 += width;
            }
        }
        copy.setPixels(iArr, 0, width, 0, 0, width, height);
        return copy;
    }

    /* renamed from: a */
    public static Bitmap m1137a(Bitmap bitmap, int i) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i2 = (width * i) / 100;
        int i3 = (height * i) / 100;
        return Bitmap.createBitmap(Bitmap.createScaledBitmap(bitmap, i2, i3, true), Math.abs(i2 - width) / 2, Math.abs(i3 - height) / 2, width, height);
    }

    /* renamed from: a */
    public static Bitmap m1136a(Bitmap bitmap) {
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        rect.inset(-6, -6);
        Bitmap createBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        float centerX = rect.centerX();
        float centerY = rect.centerY();
        float min = Math.min(centerX, centerY);
        canvas.drawCircle(centerX, centerY, min, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, (Rect) null, rect, paint);
        Paint paint2 = new Paint(1);
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(2.0f);
        canvas.drawCircle(centerX, centerY, min - 2.0f, paint2);
        return createBitmap;
    }
}
