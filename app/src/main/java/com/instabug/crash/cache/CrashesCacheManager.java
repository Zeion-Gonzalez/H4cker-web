package com.instabug.crash.cache;

import android.support.annotation.Nullable;
import com.instabug.crash.models.Crash;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CrashesCacheManager {
    public static final String CRASHES_DISK_CACHE_FILE_NAME = "/crashes.cache";
    public static final String CRASHES_DISK_CACHE_KEY = "crashes_disk_cache";
    public static final String CRASHES_MEMORY_CACHE_KEY = "crashes_memory_cache";

    public static InMemoryCache<String, Crash> getCache() throws IllegalArgumentException {
        if (!CacheManager.getInstance().cacheExists(CRASHES_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(CrashesCacheManager.class, "In-memory Crashes cache not found, loading it from disk " + CacheManager.getInstance().getCache(CRASHES_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(CRASHES_DISK_CACHE_KEY, CRASHES_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<String, Crash>() { // from class: com.instabug.crash.cache.CrashesCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Crash crash) {
                    return crash.m944a();
                }
            });
            Cache cache = CacheManager.getInstance().getCache(CRASHES_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(CrashesCacheManager.class, "In-memory Crashes cache restored from disk, " + cache.getValues().size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(CrashesCacheManager.class, "In-memory Crashes cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(CRASHES_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        Cache cache = CacheManager.getInstance().getCache(CRASHES_MEMORY_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(CRASHES_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            CacheManager.getInstance().migrateCache(cache, cache2, new CacheManager.KeyExtractor<String, Crash>() { // from class: com.instabug.crash.cache.CrashesCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Crash crash) {
                    return crash.m944a();
                }
            });
        }
    }

    public static void addCrash(Crash crash) {
        InMemoryCache<String, Crash> cache = getCache();
        if (cache != null) {
            cache.put(crash.m944a(), crash);
        }
    }

    @Nullable
    public static Crash getCrash(String str) {
        InMemoryCache<String, Crash> cache = getCache();
        if (cache != null) {
            return cache.get(str);
        }
        return null;
    }

    public static List<Crash> getCrashes() {
        return getCache() != null ? getCache().getValues() : new ArrayList();
    }

    @Nullable
    public static Crash deleteCrash(String str) {
        if (getCache() != null) {
            return getCache().delete(str);
        }
        return null;
    }
}
