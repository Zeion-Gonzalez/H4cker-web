package com.instabug.library.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.instabug.library.Instabug;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.util.AsyncTaskC0753a;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class BitmapUtils {

    /* renamed from: com.instabug.library.util.BitmapUtils$a */
    /* loaded from: classes.dex */
    public interface InterfaceC0751a {
        /* renamed from: a */
        void mo1236a(Uri uri);

        /* renamed from: a */
        void mo1237a(Throwable th);
    }

    public static int getInMemoryByteSizeOfBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static int getOnDiskByteSizeOfBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray().length;
    }

    public static Bitmap getBitmapFromFilePath(@NonNull String str) {
        try {
            return MediaStore.Images.Media.getBitmap(Instabug.getApplicationContext().getContentResolver(), Uri.fromFile(new File(str)));
        } catch (IOException e) {
            e.printStackTrace();
            InstabugSDKLogger.m1800e(BitmapUtils.class, "getBitmapFromFilePath returns null because of " + e.getMessage());
            return null;
        }
    }

    public static void compressBitmapAndSave(@NonNull File file) {
        int i = 1;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            while ((options.outWidth / i) / 2 >= 900 && (options.outHeight / i) / 2 >= 900) {
                i *= 2;
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = i;
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
            decodeStream.compress(getImageMimeType(file), 100, new FileOutputStream(file));
            decodeStream.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            InstabugSDKLogger.m1800e(BitmapUtils.class, "compressBitmapAndSave bitmap doesn't compressed correctly " + e.getMessage());
        }
    }

    private static Bitmap.CompressFormat getImageMimeType(File file) {
        return file.getName().contains("png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
    }

    public static void loadBitmap(String str, ImageView imageView) {
        new AsyncTaskC0753a(imageView).execute(str);
    }

    public static void loadBitmap(String str, ImageView imageView, float f, float f2) {
        new AsyncTaskC0753a(imageView, f, f2).execute(str);
    }

    public static void loadBitmap(String str, ImageView imageView, AsyncTaskC0753a.a aVar) {
        new AsyncTaskC0753a(imageView, aVar).execute(str);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
        int i = options.outHeight;
        int i2 = options.outWidth;
        int i3 = 1;
        if (i > 500 || i2 > 500) {
            int i4 = i / 2;
            int i5 = i2 / 2;
            while (i4 / i3 >= 500 && i5 / i3 >= 500) {
                i3 *= 2;
            }
        }
        return i3;
    }

    public static Bitmap decodeSampledBitmapFromLocalPath(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(str, options);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float f, float f2) {
        if (bitmap == null) {
            return null;
        }
        if (f != 0.0f || f2 != 0.0f) {
            Bitmap createBitmap = Bitmap.createBitmap((int) f, (int) f2, Bitmap.Config.ARGB_8888);
            if (bitmap.getWidth() >= bitmap.getHeight() || f <= f2) {
                if (bitmap.getWidth() <= bitmap.getHeight() || f >= f2) {
                    Canvas canvas = new Canvas(createBitmap);
                    Matrix matrix = new Matrix();
                    if (bitmap.getWidth() < bitmap.getHeight()) {
                        matrix.setScale(f / bitmap.getWidth(), f2 / bitmap.getHeight());
                    } else {
                        matrix.setScale(f2 / bitmap.getHeight(), f / bitmap.getWidth());
                    }
                    canvas.drawBitmap(bitmap, matrix, new Paint());
                    return createBitmap;
                }
                return bitmap;
            }
            return bitmap;
        }
        return bitmap;
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int i) {
        int i2;
        float width = bitmap.getWidth() / bitmap.getHeight();
        if (width > 1.0f) {
            int i3 = (int) (i / width);
            i2 = i;
            i = i3;
        } else {
            i2 = (int) (width * i);
        }
        return Bitmap.createScaledBitmap(bitmap, i2, i, false);
    }

    public static void saveBitmapAsPNG(Bitmap bitmap, int i, File file, String str, InterfaceC0751a interfaceC0751a) {
        File file2 = new File(file, str + "_" + System.currentTimeMillis() + ".png");
        InstabugSDKLogger.m1803v(AttachmentsUtility.class, "image path: " + file2.toString());
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
            bitmap.compress(Bitmap.CompressFormat.PNG, i, bufferedOutputStream);
            bufferedOutputStream.close();
            Uri fromFile = Uri.fromFile(file2);
            if (fromFile != null) {
                interfaceC0751a.mo1236a(fromFile);
            } else {
                interfaceC0751a.mo1237a(new Throwable("Uri equal null"));
            }
        } catch (IOException e) {
            interfaceC0751a.mo1237a(e);
        }
    }

    public static void saveBitmap(Bitmap bitmap, Context context, InterfaceC0751a interfaceC0751a) {
        File file = new File(com.instabug.library.internal.storage.DiskUtils.getInstabugDirectory(context), "bug_" + System.currentTimeMillis() + "_.jpg");
        InstabugSDKLogger.m1803v(AttachmentsUtility.class, "image path: " + file.toString());
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.close();
            Uri fromFile = Uri.fromFile(file);
            if (fromFile != null) {
                interfaceC0751a.mo1236a(fromFile);
            } else {
                interfaceC0751a.mo1237a(new Throwable("Uri equal null"));
            }
        } catch (IOException e) {
            interfaceC0751a.mo1237a(e);
        }
    }

    public static void saveBitmap(Bitmap bitmap, Uri uri, Context context) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, context.getContentResolver().openOutputStream(Uri.fromFile(new File(uri.getPath()))));
        } catch (FileNotFoundException e) {
            InstabugSDKLogger.m1801e(BitmapUtils.class, e.getMessage(), e);
        }
    }

    public static void saveBitmap(Bitmap bitmap, File file, InterfaceC0751a interfaceC0751a) {
        File file2 = new File(file, "frame_" + System.currentTimeMillis() + "_.jpg");
        InstabugSDKLogger.m1803v(AttachmentsUtility.class, "video frame path: " + file2.toString());
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
            if ((bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight()) > 640) {
                resizeBitmap(bitmap, 640).compress(Bitmap.CompressFormat.JPEG, 90, bufferedOutputStream);
            } else {
                resizeBitmap(bitmap, 320).compress(Bitmap.CompressFormat.JPEG, 90, bufferedOutputStream);
            }
            bufferedOutputStream.close();
            Uri fromFile = Uri.fromFile(file2);
            if (fromFile != null) {
                interfaceC0751a.mo1236a(fromFile);
            } else {
                interfaceC0751a.mo1237a(new Throwable("Uri equal null"));
            }
        } catch (IOException e) {
            interfaceC0751a.mo1237a(e);
        }
    }
}
