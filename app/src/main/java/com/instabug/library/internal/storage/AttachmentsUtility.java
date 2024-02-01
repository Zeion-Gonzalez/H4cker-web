package com.instabug.library.internal.storage;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class AttachmentsUtility {
    private static final double MAX_FILE_SIZE_IN_MB = 50.0d;

    public static File getVideoFile(Context context) {
        return new File(getNewDirectory(context, "videos"), "video-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.ENGLISH).format(new Date()) + ".mp4");
    }

    public static File getFilesAttachmentDirectory(Context context) {
        return getNewDirectory(context, "attachments");
    }

    public static File getNewDirectory(Context context, String str) {
        File file = new File(DiskUtils.getInstabugDirectory(context) + "/" + str + "/");
        if (!file.exists() && file.mkdirs()) {
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getVideoRecordingFramesDirectory(Context context) {
        return getNewDirectory(context, "frames");
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
    public static Uri getNewFileAttachmentUri(Context context, Uri uri, String str) {
        if (uri == null) {
            return null;
        }
        String lowerCase = uri.getLastPathSegment().toLowerCase();
        File filesAttachmentDirectory = getFilesAttachmentDirectory(context);
        if (str == null || !SettingsManager.getInstance().getExtraAttachmentFiles().containsKey(uri)) {
            str = lowerCase;
        }
        File file = new File(filesAttachmentDirectory, str);
        if (file.exists()) {
            file = new File(filesAttachmentDirectory, String.valueOf(System.currentTimeMillis()) + "_" + str);
        }
        try {
            DiskUtils.copyFromUriIntoFile(context, uri, file);
            if (!validateFileSize(file, MAX_FILE_SIZE_IN_MB)) {
                InstabugSDKLogger.m1802i(AttachmentsUtility.class, "Attachment file size exceeds than the limit 50.0");
                return null;
            }
            return Uri.fromFile(file);
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(AttachmentsUtility.class, e.getMessage(), e);
            return null;
        }
    }

    public static Uri getNewFileAttachmentUri(Context context, Uri uri) {
        return getNewFileAttachmentUri(context, uri, null);
    }

    public static File getAttachmentFile(Context context, String str) {
        File filesAttachmentDirectory = getFilesAttachmentDirectory(context);
        File file = new File(filesAttachmentDirectory, str);
        if (file.exists()) {
            return new File(filesAttachmentDirectory, String.valueOf(System.currentTimeMillis()) + "_" + str);
        }
        return file;
    }

    public static Uri getUriFromBytes(Context context, byte[] bArr, String str) {
        File attachmentFile = getAttachmentFile(context, str);
        try {
            saveBytesToFile(bArr, attachmentFile);
            return Uri.fromFile(attachmentFile);
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(AttachmentsUtility.class, e.getMessage(), e);
            return null;
        }
    }

    private static void saveBytesToFile(byte[] bArr, File file) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(bArr);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    private static boolean validateFileSize(File file, double d) {
        long length = file.length();
        double d2 = length / 1048576.0d;
        InstabugSDKLogger.m1799d(AttachmentsUtility.class, "External attachment file size is " + length + " bytes or " + d2 + " MBs");
        return d2 <= d;
    }
}
