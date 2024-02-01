package com.instabug.library.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.internal.storage.AttachmentManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import p045rx.Observable;
import p045rx.functions.Func0;
import p045rx.schedulers.Schedulers;

/* loaded from: classes.dex */
public class DiskUtils {
    @Nullable
    public static synchronized Uri zipFiles(Context context, String str, ArrayList<File> arrayList) {
        Uri uri;
        synchronized (DiskUtils.class) {
            InstabugSDKLogger.m1803v(DiskUtils.class, "zipping " + arrayList.size() + " files ... , time in MS:" + System.currentTimeMillis());
            try {
                File file = new File(AttachmentManager.getAttachmentDirectory(context) + File.separator + str + "_" + System.currentTimeMillis() + ".zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
                Iterator<File> it = arrayList.iterator();
                while (it.hasNext()) {
                    File next = it.next();
                    if (next != null) {
                        zipOutputStream.putNextEntry(new ZipEntry(next.getName()));
                        copy(next, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
                if (file.length() != 0) {
                    zipOutputStream.close();
                }
                InstabugSDKLogger.m1803v(DiskUtils.class, "zipping files have been finished successfully, path: " + file.getPath() + ", time in MS: " + System.currentTimeMillis());
                uri = Uri.fromFile(file);
            } catch (IOException e) {
                InstabugSDKLogger.m1801e(DiskUtils.class, "zipping files went wrong: " + e.getMessage() + ", time in MS: " + System.currentTimeMillis(), e);
                uri = null;
            }
        }
        return uri;
    }

    private static void copy(File file, OutputStream outputStream) throws IOException {
        try {
            copy(new FileInputStream(file), outputStream);
        } catch (FileNotFoundException e) {
            InstabugSDKLogger.m1800e(DiskUtils.class, "FileNotFoundException: can not copy file to another stream");
        }
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

    public static ArrayList<File> listFilesInDirectory(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] listFiles = file.listFiles();
        List asList = Arrays.asList(listFiles);
        InstabugSDKLogger.m1803v(DiskUtils.class, "listing " + listFiles.length + " files ... at Directory name: " + file.getName() + "Directory path: " + file.getPath() + ", time in MS:" + System.currentTimeMillis());
        arrayList.addAll(asList);
        return arrayList;
    }

    @NonNull
    public static Observable<List<File>> getCleanDirectoryObservable(final File file) {
        return Observable.defer(new Func0<Observable<List<File>>>() { // from class: com.instabug.library.util.DiskUtils.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public Observable<List<File>> call() {
                File[] listFiles;
                ArrayList arrayList = new ArrayList();
                if (file.exists() && file.isDirectory() && (listFiles = file.listFiles()) != null) {
                    for (File file2 : listFiles) {
                        if (!file2.delete()) {
                            arrayList.add(file2);
                        }
                    }
                }
                return Observable.just(arrayList);
            }
        }).subscribeOn(Schedulers.m2140io());
    }
}
