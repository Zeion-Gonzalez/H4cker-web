package com.instabug.bug.cache;

import android.content.Intent;
import android.support.annotation.Nullable;
import com.instabug.bug.model.Bug;
import com.instabug.bug.network.InstabugBugsUploaderService;
import com.instabug.library.Instabug;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BugsCacheManager {
    public static final String BUGS_DISK_CACHE_FILE_NAME = "/bugs.cache";
    public static final String BUGS_DISK_CACHE_KEY = "bugs_disk_cache";
    public static final String BUGS_MEMORY_CACHE_KEY = "bugs_memory_cache";

    public static InMemoryCache<String, Bug> getCache() throws IllegalArgumentException {
        if (!CacheManager.getInstance().cacheExists(BUGS_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(BugsCacheManager.class, "In-memory Bugs cache not found, loading it from disk " + CacheManager.getInstance().getCache(BUGS_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(BUGS_DISK_CACHE_KEY, BUGS_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<String, Bug>() { // from class: com.instabug.bug.cache.BugsCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Bug bug) {
                    return bug.getId();
                }
            });
            Cache cache = CacheManager.getInstance().getCache(BUGS_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(BugsCacheManager.class, "In-memory Bugs cache restored from disk, " + cache.getValues().size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(BugsCacheManager.class, "In-memory Bugs cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(BUGS_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        Cache cache = CacheManager.getInstance().getCache(BUGS_MEMORY_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(BUGS_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            CacheManager.getInstance().migrateCache(cache, cache2, new CacheManager.KeyExtractor<String, Bug>() { // from class: com.instabug.bug.cache.BugsCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Bug bug) {
                    return bug.getId();
                }
            });
        }
    }

    public static void addBug(Bug bug) {
        InMemoryCache<String, Bug> cache = getCache();
        if (cache != null) {
            cache.put(bug.getId(), bug);
        }
        sendBug(bug);
    }

    private static void sendBug(Bug bug) {
        if (bug.m132f() == Bug.BugState.READY_TO_BE_SENT) {
            InstabugSDKLogger.m1799d(BugsCacheManager.class, "sending bug report to the server");
            Instabug.getApplicationContext().startService(new Intent(Instabug.getApplicationContext(), InstabugBugsUploaderService.class));
        }
    }

    @Nullable
    public static Bug getBug(String str) {
        InMemoryCache<String, Bug> cache = getCache();
        if (cache != null) {
            return cache.get(str);
        }
        return null;
    }

    public static List<Bug> getBugs() {
        InMemoryCache<String, Bug> cache = getCache();
        return cache != null ? cache.getValues() : new ArrayList();
    }

    @Nullable
    public static Bug deleteBug(String str) {
        InMemoryCache<String, Bug> cache = getCache();
        if (cache != null) {
            return cache.delete(str);
        }
        return null;
    }
}
