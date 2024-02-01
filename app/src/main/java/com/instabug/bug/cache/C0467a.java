package com.instabug.bug.cache;

import android.content.Context;
import com.instabug.bug.model.Bug;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.OnDiskCache;
import com.instabug.library.util.InstabugSDKLogger;

/* compiled from: CacheUtility.java */
/* renamed from: com.instabug.bug.cache.a */
/* loaded from: classes.dex */
public class C0467a {
    /* renamed from: a */
    public static void m85a(Context context) {
        InstabugSDKLogger.m1803v(C0467a.class, "Creating bugs disk cache");
        CacheManager.getInstance().addCache(new OnDiskCache(context, BugsCacheManager.BUGS_DISK_CACHE_KEY, BugsCacheManager.BUGS_DISK_CACHE_FILE_NAME, Bug.class));
    }

    /* renamed from: a */
    public static void m84a() {
        InstabugSDKLogger.m1799d(C0467a.class, "Bugs: Saving cache to disk");
        BugsCacheManager.saveCacheToDisk();
    }
}
