package com.instabug.library.internal.storage.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CacheManager {
    public static final String DEFAULT_IN_MEMORY_CACHE_KEY = "DEFAULT_IN_MEMORY_CACHE_KEY";
    private static CacheManager INSTANCE;
    private final List<Cache> caches = new ArrayList();

    /* loaded from: classes.dex */
    public static abstract class KeyExtractor<K, V> {
        public abstract K extractKey(V v);
    }

    public CacheManager() {
        this.caches.add(new InMemoryCache(DEFAULT_IN_MEMORY_CACHE_KEY));
    }

    public static synchronized CacheManager getInstance() {
        CacheManager cacheManager;
        synchronized (CacheManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new CacheManager();
            }
            cacheManager = INSTANCE;
        }
        return cacheManager;
    }

    @Nullable
    public Cache getCache(String str) {
        synchronized (this.caches) {
            for (Cache cache : this.caches) {
                if (cache.getId().equals(str)) {
                    return cache;
                }
            }
            InstabugSDKLogger.m1799d(this, "No cache with this ID was found " + str + " returning null");
            return null;
        }
    }

    public Cache addCache(Cache cache) {
        Cache cache2 = getCache(cache.getId());
        if (cache2 != null) {
            return cache2;
        }
        synchronized (this.caches) {
            this.caches.add(cache);
        }
        return cache;
    }

    public boolean deleteCache(String str) {
        boolean remove;
        Cache cache = getCache(str);
        if (cache != null) {
            synchronized (this.caches) {
                remove = this.caches.remove(cache);
            }
            return remove;
        }
        InstabugSDKLogger.m1799d(this, "No cache was this ID was found " + str + " to be deleted");
        return false;
    }

    public boolean cacheExists(String str) {
        return getCache(str) != null;
    }

    public boolean subscribe(String str, CacheChangedListener cacheChangedListener) {
        if (cacheExists(str)) {
            return getCache(str).addOnCacheChangedListener(cacheChangedListener);
        }
        throw new IllegalArgumentException("No cache exists with this ID to subscribe to");
    }

    public boolean unSubscribe(String str, CacheChangedListener cacheChangedListener) {
        if (cacheExists(str)) {
            return getCache(str).removeOnCacheChangedListener(cacheChangedListener);
        }
        return false;
    }

    public void invalidateAllCaches() {
        synchronized (this.caches) {
            Iterator<Cache> it = this.caches.iterator();
            while (it.hasNext()) {
                it.next().invalidate();
            }
        }
        InstabugSDKLogger.m1799d(this, "All caches have been invalidated");
    }

    public void invalidateAllCachesButUserAttributes() {
        synchronized (this.caches) {
            for (Cache cache : this.caches) {
                if (!cache.getId().equalsIgnoreCase(UserAttributesCacheManager.USER_ATTRIBUTES_CACHE_KEY) && !cache.getId().equalsIgnoreCase(UserAttributesCacheManager.USER_ATTRIBUTES_DISK_CACHE_FILE_NAME) && !cache.getId().equalsIgnoreCase(UserAttributesCacheManager.USER_ATTRIBUTES_DISK_CACHE_KEY) && !cache.getId().equalsIgnoreCase(UserAttributesCacheManager.USER_ATTRIBUTES_MEMORY_CACHE_KEY)) {
                    cache.invalidate();
                }
            }
        }
        InstabugSDKLogger.m1799d(this, "All caches have been invalidated");
    }

    public <K, V> void migrateCache(@NonNull String str, @NonNull String str2, KeyExtractor<K, V> keyExtractor) throws IllegalArgumentException {
        Cache cache = getCache(str);
        Cache cache2 = getCache(str2);
        InstabugSDKLogger.m1803v(this, "Caches to be migrated " + cache + " - " + cache2);
        if (cache == null) {
            InstabugSDKLogger.m1801e(this, "Migration exception ", new IllegalArgumentException("No cache with these keys was found to migrate from"));
            return;
        }
        if (cache2 == null) {
            cache2 = new InMemoryCache(str2);
            addCache(cache2);
        }
        migrateCache(cache, cache2, keyExtractor);
    }

    public <K, V> void migrateCache(@NonNull Cache<K, V> cache, @NonNull Cache<K, V> cache2, KeyExtractor<K, V> keyExtractor) {
        InstabugSDKLogger.m1799d(this, "Invalidated migratingTo cache");
        if (cache2 == null || cache == null) {
            InstabugSDKLogger.m1804w(CacheManager.class, "cache migration process got failure, migratingToCache: " + cache2 + ", migratingFromCache: " + cache);
            return;
        }
        cache2.invalidate();
        List<V> values = cache.getValues();
        if (values == null || values.isEmpty()) {
            InstabugSDKLogger.m1804w(this, "Cache to migrate from doesn't contain any elements, not going further with the migration");
            return;
        }
        for (V v : values) {
            if (v != null) {
                InstabugSDKLogger.m1803v(this, "Adding value " + v + " with key " + keyExtractor.extractKey(v));
                cache2.put(keyExtractor.extractKey(v), v);
            }
        }
    }
}
