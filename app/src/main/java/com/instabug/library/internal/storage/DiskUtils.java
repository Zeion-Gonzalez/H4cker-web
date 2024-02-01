package com.instabug.library.internal.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.p000os.Environmenu;
import android.support.annotation.NonNull;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
/* loaded from: classes.dex */
public class DiskUtils {
    public static File getInstabugDirectory(@NonNull Context context) {
        String internalStoragePath;
        if (context.getExternalFilesDir(null) != null && Environment.getExternalStorageState().equals(Environmenu.MEDIA_MOUNTED)) {
            try {
                internalStoragePath = context.getExternalFilesDir(null).getAbsolutePath();
            } catch (NullPointerException e) {
                internalStoragePath = getInternalStoragePath(context);
                InstabugSDKLogger.m1802i(AttachmentsUtility.class, "External storage not available, saving file to internal storage.");
            }
        } else {
            internalStoragePath = getInternalStoragePath(context);
            InstabugSDKLogger.m1802i(AttachmentsUtility.class, "External storage not available, saving file to internal storage.");
        }
        File file = new File(internalStoragePath + "/instabug/");
        if (!file.exists() && file.mkdirs()) {
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return file;
    }

    private static String getInternalStoragePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static void saveBitmapOnDisk(Bitmap bitmap, File file) throws IOException {
        if (bitmap != null && file != null) {
            InstabugSDKLogger.m1803v(DiskUtils.class, "starting save viewHierarchy image, path: " + file.getAbsolutePath() + ", time in MS: " + System.currentTimeMillis());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
            InstabugSDKLogger.m1803v(DiskUtils.class, "viewHierarchy image saved, path: " + file.getAbsolutePath() + ", time in MS: " + System.currentTimeMillis());
        }
    }

    public static void cleanDirectory(File file) {
        File[] listFiles;
        if (file.exists() && file.isDirectory() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
                if (file2.delete()) {
                    InstabugSDKLogger.m1803v(DiskUtils.class, "file deleted successfully, path: " + file2.getPath() + ", time in MS: " + System.currentTimeMillis());
                }
            }
        }
    }

    public static void copyFromUriIntoFile(Context context, Uri uri, File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
        InstabugSDKLogger.m1799d(AttachmentsUtility.class, "Target file path: " + file.getPath());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(context.getContentResolver().openOutputStream(Uri.fromFile(file)));
        byte[] bArr = new byte[32768];
        while (true) {
            int read = bufferedInputStream.read(bArr);
            if (read > 0) {
                bufferedOutputStream.write(bArr, 0, read);
            } else {
                bufferedOutputStream.close();
                bufferedInputStream.close();
                return;
            }
        }
    }

    public static void deleteFile(String str) {
        new File(str).delete();
    }
}
