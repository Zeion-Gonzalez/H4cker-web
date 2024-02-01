package com.instabug.bug.screenshot.viewhierarchy.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.instabug.bug.screenshot.viewhierarchy.C0479b;
import com.instabug.bug.screenshot.viewhierarchy.C0480c;
import com.instabug.library.internal.storage.DiskUtils;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* loaded from: classes.dex */
public class ViewHierarchyDiskUtils {
    public static void saveViewHierarchyImage(C0479b c0479b) {
        File file = new File(getViewHierarchyImagesDirectory(c0479b.m211o().getContext()).getAbsolutePath() + File.separator + c0479b.m182a() + ".png");
        try {
            if (c0479b.m206j() != null) {
                DiskUtils.saveBitmapOnDisk(c0479b.m206j(), file);
                c0479b.m186a(Uri.fromFile(file));
            } else {
                InstabugSDKLogger.m1799d(ViewHierarchyDiskUtils.class, "trying to save a null value bitmap, time in MS: " + System.currentTimeMillis());
            }
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(ViewHierarchyDiskUtils.class, "save viewHierarchy image got error: " + e.getMessage() + ", time in MS: " + System.currentTimeMillis(), e);
        }
    }

    @Nullable
    public static synchronized Uri zipViewHierarchyImages(C0479b c0479b) {
        Uri uri;
        synchronized (ViewHierarchyDiskUtils.class) {
            InstabugSDKLogger.m1803v(ViewHierarchyDiskUtils.class, "zip viewHierarchy images just started, time in MS: " + System.currentTimeMillis());
            try {
                File file = new File(DiskUtils.getInstabugDirectory(c0479b.m211o().getContext()) + File.separator + "view_hierarchy_attachment_" + System.currentTimeMillis() + ".zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
                for (C0479b c0479b2 : C0480c.m222b(c0479b)) {
                    if (c0479b2.m208l() != null) {
                        File file2 = new File(c0479b2.m208l().getPath());
                        zipOutputStream.putNextEntry(new ZipEntry(c0479b2.m182a() + ".png"));
                        copy(file2, zipOutputStream);
                        zipOutputStream.closeEntry();
                        if (file2.delete()) {
                            InstabugSDKLogger.m1803v(ViewHierarchyDiskUtils.class, "file zipped successfully, path: " + file2.getPath() + ", time in MS: " + System.currentTimeMillis());
                        }
                    }
                }
                if (file.length() != 0) {
                    zipOutputStream.close();
                }
                InstabugSDKLogger.m1803v(ViewHierarchyDiskUtils.class, "zip viewHierarchy images done successfully, path: " + file.getPath() + ", time in MS: " + System.currentTimeMillis());
                uri = Uri.fromFile(file);
            } catch (IOException e) {
                InstabugSDKLogger.m1801e(ViewHierarchyDiskUtils.class, "zip viewHierarchy images got error: " + e.getMessage() + ", time in MS: " + System.currentTimeMillis(), e);
                uri = null;
            }
        }
        return uri;
    }

    private static void copy(File file, OutputStream outputStream) throws IOException {
        copy(new FileInputStream(file), outputStream);
    }

    private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[2048];
        while (true) {
            int read = inputStream.read(bArr);
            if (read >= 0) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    public static File getViewHierarchyImagesDirectory(Context context) {
        File file = new File(DiskUtils.getInstabugDirectory(context) + "/view-hierarchy-images/");
        if (!file.exists() && file.mkdir()) {
            InstabugSDKLogger.m1802i(ViewHierarchyDiskUtils.class, "temp directory created successfully: " + file.getPath() + ", time in MS: " + System.currentTimeMillis());
        }
        return file;
    }
}
