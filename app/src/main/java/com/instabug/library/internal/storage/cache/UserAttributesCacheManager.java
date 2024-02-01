package com.instabug.library.internal.storage.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.model.C0717b;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.HashMap;
import org.json.JSONException;

/* loaded from: classes.dex */
public class UserAttributesCacheManager {
    public static final String USER_ATTRIBUTES_CACHE_KEY = "attrs";
    public static final String USER_ATTRIBUTES_DISK_CACHE_FILE_NAME = "/user_attributes.cache";
    public static final String USER_ATTRIBUTES_DISK_CACHE_KEY = "user_attributes_disk_cache";
    public static final String USER_ATTRIBUTES_MEMORY_CACHE_KEY = "user_attributes_memory_cache";

    public static InMemoryCache<String, C0717b> getCache() {
        if (!CacheManager.getInstance().cacheExists(USER_ATTRIBUTES_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(UserAttributesCacheManager.class, "In-memory user attributes cache not found, loading it from disk " + CacheManager.getInstance().getCache(USER_ATTRIBUTES_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(USER_ATTRIBUTES_DISK_CACHE_KEY, USER_ATTRIBUTES_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<String, C0717b>() { // from class: com.instabug.library.internal.storage.cache.UserAttributesCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(C0717b c0717b) {
                    return UserAttributesCacheManager.USER_ATTRIBUTES_CACHE_KEY;
                }
            });
        }
        InstabugSDKLogger.m1799d(UserAttributesCacheManager.class, "In-memory user attributes cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(USER_ATTRIBUTES_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        Cache cache = CacheManager.getInstance().getCache(USER_ATTRIBUTES_MEMORY_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(USER_ATTRIBUTES_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            CacheManager.getInstance().migrateCache(cache, cache2, new CacheManager.KeyExtractor<String, C0717b>() { // from class: com.instabug.library.internal.storage.cache.UserAttributesCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(C0717b c0717b) {
                    return UserAttributesCacheManager.USER_ATTRIBUTES_CACHE_KEY;
                }
            });
        }
    }

    public static void putAttribute(@NonNull String str, String str2) {
        InMemoryCache<String, C0717b> cache = getCache();
        if (cache != null) {
            if (cache.get(USER_ATTRIBUTES_CACHE_KEY) == null) {
                cache.put(USER_ATTRIBUTES_CACHE_KEY, new C0717b());
            }
            cache.get(USER_ATTRIBUTES_CACHE_KEY).m1591a(str, str2);
        }
    }

    @Nullable
    public static String getAttribute(@NonNull String str) {
        C0717b c0717b;
        InMemoryCache<String, C0717b> cache = getCache();
        if (cache == null || (c0717b = cache.get(USER_ATTRIBUTES_CACHE_KEY)) == null) {
            return null;
        }
        return c0717b.m1592a(str);
    }

    public static void deleteAttribute(String str) {
        C0717b c0717b;
        InMemoryCache<String, C0717b> cache = getCache();
        if (cache != null && (c0717b = cache.get(USER_ATTRIBUTES_CACHE_KEY)) != null) {
            c0717b.m1595b(str);
        }
    }

    public static void deleteAll() {
        InMemoryCache<String, C0717b> cache = getCache();
        if (cache != null) {
            cache.invalidate();
        }
    }

    @Nullable
    public static HashMap<String, String> getAll() {
        C0717b c0717b = getCache() != null ? getCache().get(USER_ATTRIBUTES_CACHE_KEY) : null;
        if (c0717b == null || c0717b.m1593a() == null || c0717b.m1593a().isEmpty()) {
            return null;
        }
        return c0717b.m1593a();
    }

    public static String getUserAttributes() {
        HashMap<String, String> all = getAll();
        if (all == null || all.size() == 0) {
            return "{}";
        }
        C0717b c0717b = new C0717b();
        c0717b.m1594a(all);
        try {
            return c0717b.toJson();
        } catch (JSONException e) {
            InstabugSDKLogger.m1801e(State.class, "parsing user attributes got error: " + e.getMessage(), e);
            return "{}";
        }
    }
}
