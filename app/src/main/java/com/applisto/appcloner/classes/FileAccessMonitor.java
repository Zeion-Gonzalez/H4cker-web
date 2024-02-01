package com.applisto.appcloner.classes;

import com.applisto.appcloner.classes.util.Log;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes2.dex */
public class FileAccessMonitor {
    private static final int MAX_SIZE = 10000;
    private static final String TAG = FileAccessMonitor.class.getSimpleName();
    private static final AtomicLong sIndex = new AtomicLong();
    private static TreeMap<Long, String> sMap;

    public static void install() {
        Log.m15i(TAG, "install; ");
        try {
            sMap = new TreeMap<>();
            AppClonerNative.registerFilenameFilter(new FilenameFilter() { // from class: com.applisto.appcloner.classes.FileAccessMonitor.1
                private String mLastEntry;

                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str) {
                    long incrementAndGet = FileAccessMonitor.sIndex.incrementAndGet();
                    synchronized (FileAccessMonitor.sMap) {
                        String str2 = System.currentTimeMillis() + " " + str;
                        if (!str2.equals(this.mLastEntry)) {
                            FileAccessMonitor.sMap.put(Long.valueOf(incrementAndGet), str2);
                            if (FileAccessMonitor.sMap.size() > 10000) {
                                FileAccessMonitor.sMap.remove(FileAccessMonitor.sMap.firstKey());
                            }
                            this.mLastEntry = str2;
                        }
                    }
                    return true;
                }
            });
        } catch (Throwable th) {
            Log.m21w(TAG, th);
        }
    }

    public static Map<Long, String> getFileAccessMonitorEntries(long j) {
        NavigableMap<Long, String> tailMap;
        TreeMap<Long, String> treeMap = sMap;
        if (treeMap == null) {
            return null;
        }
        synchronized (treeMap) {
            tailMap = sMap.tailMap(Long.valueOf(j), false);
        }
        return tailMap;
    }
}
