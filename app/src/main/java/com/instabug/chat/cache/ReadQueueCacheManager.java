package com.instabug.chat.cache;

import com.instabug.chat.model.C0530d;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ReadQueueCacheManager {
    public static final String READ_QUEUE_DISK_CACHE_FILE_NAME = "/read_queue.cache";
    public static final String READ_QUEUE_DISK_CACHE_KEY = "read_queue_disk_cache_key";
    public static final String READ_QUEUE_MEMORY_CACHE_KEY = "read_queue_memory_cache_key";
    private static ReadQueueCacheManager mReadQueueCacheManager;

    private ReadQueueCacheManager() {
        InstabugSDKLogger.m1799d(this, "Initializing ReadQueueCacheManager");
        CacheManager.getInstance().addCache(new InMemoryCache(READ_QUEUE_MEMORY_CACHE_KEY));
    }

    public static ReadQueueCacheManager getInstance() {
        if (mReadQueueCacheManager == null) {
            mReadQueueCacheManager = new ReadQueueCacheManager();
        }
        return mReadQueueCacheManager;
    }

    public static InMemoryCache<String, C0530d> getCache() {
        if (!CacheManager.getInstance().cacheExists(READ_QUEUE_MEMORY_CACHE_KEY) || CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY).getValues().size() > 0) {
            InstabugSDKLogger.m1799d(ReadQueueCacheManager.class, "In-memory cache not found, loading it from disk " + CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(READ_QUEUE_DISK_CACHE_KEY, READ_QUEUE_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<String, C0530d>() { // from class: com.instabug.chat.cache.ReadQueueCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(C0530d c0530d) {
                    return c0530d.m685a();
                }
            });
            Cache cache = CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(ReadQueueCacheManager.class, "In-memory cache restored from disk, " + cache.getValues().size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(ReadQueueCacheManager.class, "In-memory cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        Cache cache = CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(READ_QUEUE_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            InstabugSDKLogger.m1799d(ReadQueueCacheManager.class, "Saving In-memory cache to disk, no. of items to save is " + cache.getValues());
            CacheManager.getInstance().migrateCache(cache, cache2, new CacheManager.KeyExtractor<String, C0530d>() { // from class: com.instabug.chat.cache.ReadQueueCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(C0530d c0530d) {
                    return String.valueOf(c0530d.m685a());
                }
            });
        }
    }

    public void add(C0530d c0530d) {
        InstabugSDKLogger.m1803v(this, "Adding message " + c0530d + " to read queue in-memory cache");
        Cache cache = CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
        if (cache != null) {
            cache.put(c0530d.m685a(), c0530d);
            InstabugSDKLogger.m1803v(this, "Added message " + c0530d + " to read queue in-memory cache " + cache.size());
        }
    }

    public JSONArray getReadMessagesArray() {
        JSONArray jSONArray = new JSONArray();
        for (C0530d c0530d : getAll()) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("chat_number", c0530d.m685a());
                jSONObject.put("message_id", c0530d.m690c());
                jSONObject.put("read_at", c0530d.m688b());
                jSONArray.put(jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jSONArray;
    }

    public List<C0530d> getAll() {
        Cache cache = CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
        return cache != null ? cache.getValues() : new ArrayList();
    }

    private void remove(String str) {
        Cache cache = CacheManager.getInstance().getCache(READ_QUEUE_MEMORY_CACHE_KEY);
        if (cache != null) {
            cache.delete(str);
        }
    }

    public void notify(List<C0530d> list) {
        for (C0530d c0530d : getAll()) {
            for (C0530d c0530d2 : list) {
                if (c0530d.m685a().equals(c0530d2.m685a()) && c0530d.m690c().equals(c0530d2.m690c())) {
                    remove(c0530d2.m685a());
                }
            }
        }
    }
}
