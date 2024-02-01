package com.instabug.survey.cache;

import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.p032a.C0769c;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SurveysCacheManager {
    public static final String SURVEYS_DISK_CACHE_FILE_NAME = "/surveys.cache";
    public static final String SURVEYS_DISK_CACHE_KEY = "surveys_disk_cache";
    public static final String SURVEYS_MEMORY_CACHE_KEY = "surveys_memory_cache";

    public static InMemoryCache<Long, C0769c> getCache() throws IllegalArgumentException {
        if (!CacheManager.getInstance().cacheExists(SURVEYS_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(SurveysCacheManager.class, "In-memory Surveys cache not found, loading it from disk " + CacheManager.getInstance().getCache(SURVEYS_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(SURVEYS_DISK_CACHE_KEY, SURVEYS_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<Long, C0769c>() { // from class: com.instabug.survey.cache.SurveysCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public Long extractKey(C0769c c0769c) {
                    return Long.valueOf(c0769c.m1943a());
                }
            });
            Cache cache = CacheManager.getInstance().getCache(SURVEYS_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(SurveysCacheManager.class, "In-memory Surveys cache restored from disk, " + cache.size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(SurveysCacheManager.class, "In-memory Surveys cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(SURVEYS_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() {
        Cache cache = CacheManager.getInstance().getCache(SURVEYS_DISK_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(SURVEYS_MEMORY_CACHE_KEY);
        if (cache != null && cache2 != null) {
            CacheManager.getInstance().migrateCache(cache2, cache, new CacheManager.KeyExtractor<String, C0769c>() { // from class: com.instabug.survey.cache.SurveysCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(C0769c c0769c) {
                    return String.valueOf(c0769c.m1943a());
                }
            });
        }
    }

    public static void addSurvey(C0769c c0769c) {
        InMemoryCache<Long, C0769c> cache = getCache();
        if (cache != null) {
            cache.put(Long.valueOf(c0769c.m1943a()), c0769c);
        }
    }

    public static void addSurveys(List<C0769c> list) {
        Iterator<C0769c> it = list.iterator();
        while (it.hasNext()) {
            addSurvey(it.next());
        }
    }

    public static List<C0769c> getSurveys() {
        InMemoryCache<Long, C0769c> cache = getCache();
        return cache != null ? cache.getValues() : new ArrayList();
    }

    public static List<C0769c> getNotAnsweredSurveys() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<Long, C0769c> cache = getCache();
        if (cache != null) {
            for (C0769c c0769c : cache.getValues()) {
                if (c0769c.m1964k() == null || String.valueOf(c0769c.m1964k()).equals("null")) {
                    if (!c0769c.m1960g()) {
                        arrayList.add(c0769c);
                        InstabugSDKLogger.m1799d(SurveysCacheManager.class, "survey id: " + c0769c.m1943a());
                    }
                }
            }
        }
        InstabugSDKLogger.m1799d(SurveysCacheManager.class, "NotAnsweredSurveys size: " + arrayList.size());
        return arrayList;
    }

    public static List<C0769c> getAnsweredAndNotSubmittedSurveys() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<Long, C0769c> cache = getCache();
        if (cache != null) {
            List<C0769c> values = cache.getValues();
            InstabugSDKLogger.m1799d(SurveysCacheManager.class, "size: " + values.size());
            for (C0769c c0769c : values) {
                if (c0769c.m1960g() && !c0769c.m1961h()) {
                    arrayList.add(c0769c);
                }
            }
        }
        return arrayList;
    }
}
