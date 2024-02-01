package com.applisto.appcloner.classes;

import android.content.Context;
import android.os.Environment;
import com.applisto.appcloner.classes.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes2.dex */
public class BundleObb {
    private static final String TAG = BundleObb.class.getSimpleName();
    private boolean mBundleObb;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BundleObb(CloneSettings cloneSettings) {
        this.mBundleObb = cloneSettings.getBoolean("bundleObb", false).booleanValue();
        Log.m15i(TAG, "BundleObb; mBundleObb: " + this.mBundleObb);
    }

    public void install(Context context) {
        FileOutputStream fileOutputStream;
        Log.m15i(TAG, "install; ");
        if (!this.mBundleObb) {
            return;
        }
        try {
            File packageObbDirectory = getPackageObbDirectory(context.getPackageName());
            File file = new File(packageObbDirectory, Integer.toString(Utils.getApplicationVersionCode(context)));
            if (packageObbDirectory.exists() && file.exists()) {
                Log.m15i(TAG, "init; not unbundling OBB files");
                return;
            }
            Log.m15i(TAG, "init; unbundling OBB files");
            long currentTimeMillis = System.currentTimeMillis();
            try {
                deleteDirectory(packageObbDirectory);
                Utils.forceMkdir(packageObbDirectory);
                ZipInputStream zipInputStream = new ZipInputStream(context.getAssets().open("obb.zip"));
                while (true) {
                    try {
                        ZipEntry nextEntry = zipInputStream.getNextEntry();
                        if (nextEntry != null) {
                            String name = nextEntry.getName();
                            Log.m15i(TAG, "init; name: " + name);
                            File file2 = new File(packageObbDirectory, name);
                            fileOutputStream = new FileOutputStream(file2);
                            try {
                                Utils.copy(zipInputStream, fileOutputStream);
                                fileOutputStream.close();
                                Log.m15i(TAG, "init; file: " + file2 + ", file.length(): " + file2.length());
                            } catch (Throwable th) {
                                throw th;
                            }
                        } else {
                            zipInputStream.close();
                            fileOutputStream = new FileOutputStream(file);
                            try {
                                fileOutputStream.write(0);
                                return;
                            } finally {
                                fileOutputStream.close();
                            }
                        }
                    } catch (Throwable th2) {
                        zipInputStream.close();
                        throw th2;
                    }
                }
            } finally {
                Log.m15i(TAG, "init; took: " + (System.currentTimeMillis() - currentTimeMillis) + " millis");
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    private static File getObbDirectory() {
        return new File(Environment.getExternalStorageDirectory(), "/Android/obb/");
    }

    private static File getPackageObbDirectory(String str) {
        return new File(getObbDirectory(), str);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException(file + " does not exist");
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(file + " is not a directory");
        }
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            throw new IOException("Failed to list contents of " + file);
        }
        IOException e = null;
        for (File file2 : listFiles) {
            try {
                forceDelete(file2);
            } catch (IOException e2) {
                e = e2;
            }
        }
        if (e != null) {
            throw e;
        }
    }

    private static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
            return;
        }
        boolean exists = file.exists();
        if (file.delete()) {
            return;
        }
        if (!exists) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        throw new IOException("Unable to delete file: " + file);
    }

    private static void deleteDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!isSymlink(file)) {
                cleanDirectory(file);
            }
            if (file.delete()) {
                return;
            }
            throw new IOException("Unable to delete directory " + file + ".");
        }
    }

    private static boolean isSymlink(File file) throws IOException {
        if (file.getParent() != null) {
            file = new File(file.getParentFile().getCanonicalFile(), file.getName());
        }
        return !file.getCanonicalFile().equals(file.getAbsoluteFile());
    }
}
