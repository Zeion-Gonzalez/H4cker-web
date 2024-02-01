package com.instabug.library.internal.storage;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.p000os.Environmenu;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
/* loaded from: classes.dex */
public class AttachmentManager {
    private static final double MAX_FILE_SIZE_IN_MB = 50.0d;

    /* renamed from: com.instabug.library.internal.storage.AttachmentManager$a */
    /* loaded from: classes.dex */
    public interface InterfaceC0665a {
        /* renamed from: a */
        void m1295a(Uri uri);

        /* renamed from: a */
        void m1296a(Throwable th);
    }

    public static File getAttachmentDirectory(@NonNull Context context) {
        String internalStoragePath;
        if (context.getExternalFilesDir(null) != null && Environment.getExternalStorageState().equals(Environmenu.MEDIA_MOUNTED)) {
            try {
                internalStoragePath = context.getExternalFilesDir(null).getAbsolutePath();
            } catch (NullPointerException e) {
                internalStoragePath = getInternalStoragePath(context);
            }
        } else {
            internalStoragePath = getInternalStoragePath(context);
        }
        File file = new File(internalStoragePath + "/instabug/");
        if (!file.exists()) {
            file.mkdirs();
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return file;
    }

    public static File getNewDirectory(Context context, String str) {
        File file = new File(getAttachmentDirectory(context) + "/" + str + "/");
        if (!file.exists()) {
            file.mkdirs();
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getVideoFile(Context context) {
        return new File(getVideoRecordingVideosDirectory(context), "video-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault()).format(new Date()) + ".mp4");
    }

    public static File getVideoRecordingFramesDirectory(Context context) {
        File file = new File(getAttachmentDirectory(context) + "/frames/");
        if (!file.exists()) {
            file.mkdirs();
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getVideoRecordingVideosDirectory(Context context) {
        File file = new File(getAttachmentDirectory(context) + "/videos/");
        if (!file.exists()) {
            file.mkdirs();
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getAutoScreenRecordingVideosDirectory(Context context) {
        File file = new File(getAttachmentDirectory(context) + "/auto_recording/");
        if (!file.exists()) {
            file.mkdirs();
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getAutoScreenRecordingFile(Context context) {
        return new File(getAutoScreenRecordingVideosDirectory(context), "auto-recording-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.ENGLISH).format(new Date()) + ".mp4");
    }

    private static String getInternalStoragePath(Context context) {
        InstabugSDKLogger.m1802i(AttachmentManager.class, "External storage not available, saving file to internal storage.");
        return context.getFilesDir().getAbsolutePath();
    }

    private static void saveBytesToFile(byte[] bArr, File file) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(bArr);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    private static File getAttachmentFile(Context context, String str) {
        File attachmentDirectory = getAttachmentDirectory(context);
        File file = new File(attachmentDirectory, str);
        if (file.exists()) {
            return new File(attachmentDirectory, String.valueOf(System.currentTimeMillis()) + "_" + str);
        }
        return file;
    }

    public static Uri getUriFromBytes(Context context, byte[] bArr, String str) {
        File attachmentFile = getAttachmentFile(context, str);
        try {
            saveBytesToFile(bArr, attachmentFile);
            return Uri.fromFile(attachmentFile);
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(AttachmentManager.class, e.getMessage(), e);
            return null;
        }
    }

    public static void copyFromUriIntoFile(Context context, Uri uri, File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
        InstabugSDKLogger.m1799d(AttachmentManager.class, "Target file path: " + file.getPath());
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

    @Nullable
    public static String getGalleryImagePath(Activity activity, Uri uri) {
        Cursor managedQuery = activity.managedQuery(uri, new String[]{"_data"}, null, null, null);
        if (managedQuery == null) {
            return null;
        }
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    @Nullable
    public static Uri getNewFileUri(Context context, Uri uri, String str) {
        if (uri == null) {
            return null;
        }
        String lowerCase = uri.getLastPathSegment().toLowerCase();
        if (str == null || !SettingsManager.getInstance().getExtraAttachmentFiles().containsKey(uri)) {
            str = lowerCase;
        }
        File attachmentFile = getAttachmentFile(context, str);
        try {
            copyFromUriIntoFile(context, uri, attachmentFile);
            if (validateFileSize(uri, attachmentFile)) {
                return Uri.fromFile(attachmentFile);
            }
            return null;
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(AttachmentManager.class, e.getMessage(), e);
            return null;
        }
    }

    public static Uri getNewFileUri(Context context, Uri uri) {
        return getNewFileUri(context, uri, null);
    }

    private static boolean validateFileSize(Uri uri, File file) {
        if (SettingsManager.getInstance().getExtraAttachmentFiles().containsKey(uri)) {
            long length = file.length();
            double d = length / 1048576.0d;
            InstabugSDKLogger.m1799d(AttachmentManager.class, "External attachment file size is " + length + " bytes or " + d + " MBs");
            if (d > MAX_FILE_SIZE_IN_MB) {
                InstabugSDKLogger.m1802i(AttachmentManager.class, "Attachment exceeds 50.0 MBs file size limit, ignoring attachment");
                return false;
            }
        }
        return true;
    }

    public static void saveBitmap(Bitmap bitmap, Context context, InterfaceC0665a interfaceC0665a) {
        File file = new File(getAttachmentDirectory(context), "bug_" + System.currentTimeMillis() + "_.jpg");
        InstabugSDKLogger.m1803v(AttachmentManager.class, "image path: " + file.toString());
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.close();
            Uri fromFile = Uri.fromFile(file);
            if (fromFile != null) {
                interfaceC0665a.m1295a(fromFile);
            } else {
                interfaceC0665a.m1296a(new Throwable("Uri equal null"));
            }
        } catch (IOException e) {
            interfaceC0665a.m1296a(e);
        }
    }

    public static void saveBitmap(Bitmap bitmap, File file, InterfaceC0665a interfaceC0665a) {
        File file2 = new File(file, "frame_" + System.currentTimeMillis() + "_.jpg");
        InstabugSDKLogger.m1803v(AttachmentManager.class, "video frame path: " + file2.toString());
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
                interfaceC0665a.m1295a(fromFile);
            } else {
                interfaceC0665a.m1296a(new Throwable("Uri equal null"));
            }
        } catch (IOException e) {
            interfaceC0665a.m1296a(e);
        }
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
}
