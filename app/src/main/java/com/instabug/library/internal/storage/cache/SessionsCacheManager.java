package com.instabug.library.internal.storage.cache;

import android.support.annotation.Nullable;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.model.Session;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SessionsCacheManager {
    public static final String SESSIONS_DISK_CACHE_FILE_NAME = "/sessions.cache";
    public static final String SESSIONS_DISK_CACHE_KEY = "sessions_disk_cache";
    public static final String SESSIONS_MEMORY_CACHE_KEY = "sessions_memory_cache";

    @Nullable
    public static InMemoryCache<Long, Session> getCache() throws IllegalArgumentException {
        if (!CacheManager.getInstance().cacheExists(SESSIONS_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(SessionsCacheManager.class, "In-memory Sessions cache not found, loading it from disk " + CacheManager.getInstance().getCache(SESSIONS_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(SESSIONS_DISK_CACHE_KEY, SESSIONS_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<Long, Session>() { // from class: com.instabug.library.internal.storage.cache.SessionsCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public Long extractKey(Session session) {
                    return Long.valueOf(session.m1580b());
                }
            });
            Cache cache = CacheManager.getInstance().getCache(SESSIONS_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(SessionsCacheManager.class, "In-memory Sessions cache restored from disk, " + cache.getValues().size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(SessionsCacheManager.class, "In-memory Sessions cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(SESSIONS_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        final Cache cache = CacheManager.getInstance().getCache(SESSIONS_MEMORY_CACHE_KEY);
        final Cache cache2 = CacheManager.getInstance().getCache(SESSIONS_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            new Thread(new Runnable() { // from class: com.instabug.library.internal.storage.cache.SessionsCacheManager.2
                @Override // java.lang.Runnable
                public void run() {
                    CacheManager.getInstance().migrateCache(Cache.this, cache2, new CacheManager.KeyExtractor<String, Session>() { // from class: com.instabug.library.internal.storage.cache.SessionsCacheManager.2.1
                        @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                        /* renamed from: a  reason: merged with bridge method [inline-methods] */
                        public String extractKey(Session session) {
                            return String.valueOf(session.m1580b());
                        }
                    });
                }
            }).start();
        }
    }

    public static void addSession(Session session) {
        InMemoryCache<Long, Session> cache = getCache();
        if (cache != null) {
            cache.put(Long.valueOf(session.m1580b()), session);
        }
    }

    @Nullable
    public static Session getSession(long j) {
        InMemoryCache<Long, Session> cache = getCache();
        if (cache != null) {
            return cache.get(Long.valueOf(j));
        }
        return null;
    }

    @Nullable
    public static Session deleteSession(Session session) {
        InMemoryCache<Long, Session> cache = getCache();
        if (cache != null) {
            return cache.delete(Long.valueOf(session.m1580b()));
        }
        return null;
    }

    public static List<Session> getSessions() {
        InMemoryCache<Long, Session> cache = getCache();
        return cache != null ? cache.getValues() : new ArrayList();
    }
}
