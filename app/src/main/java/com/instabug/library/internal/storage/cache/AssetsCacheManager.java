package com.instabug.library.internal.storage.cache;

import android.content.Context;
import android.os.Environment;
import android.p000os.Environmenu;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.network.Request;
import com.instabug.library.network.p030a.C0724b;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import p045rx.Subscription;

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
/* loaded from: classes.dex */
public class AssetsCacheManager {
    private static final String ASSETS_MEMORY_CACHE_KEY = "assets_memory_cache";
    private static LinkedHashMap<String, C0667a> currentDownloadingFiles = new LinkedHashMap<>();

    /* loaded from: classes.dex */
    public interface OnDownloadFinished {
        void onFailed(Throwable th);

        void onSuccess(AssetEntity assetEntity);
    }

    public static AssetCache getCache() {
        if (!CacheManager.getInstance().cacheExists(ASSETS_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "In-memory assets cache not found, create it");
            CacheManager.getInstance().addCache(new AssetCache(ASSETS_MEMORY_CACHE_KEY));
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "In-memory assets created successfully");
        }
        InstabugSDKLogger.m1799d(AssetsCacheManager.class, "In-memory assets cache found");
        return (AssetCache) CacheManager.getInstance().getCache(ASSETS_MEMORY_CACHE_KEY);
    }

    public static AssetEntity createEmptyEntity(Context context, String str, AssetEntity.AssetType assetType) {
        return new AssetEntity(String.valueOf(str.hashCode()), assetType, str, new File(getCashDirectory(context), String.valueOf(str.hashCode())));
    }

    public static void getAssetEntity(Context context, AssetEntity assetEntity, OnDownloadFinished onDownloadFinished) {
        AssetEntity assetEntity2;
        AssetCache cache = getCache();
        if (cache != null) {
            assetEntity2 = cache.get(assetEntity.getKey());
        } else {
            assetEntity2 = null;
        }
        if (assetEntity2 != null) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "Get file from cache");
            onDownloadFinished.onSuccess(assetEntity2);
        } else if (isDownloading(assetEntity.getKey())) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "File currently downloading, wait download to finish");
            waitDownloadToFinish(assetEntity, onDownloadFinished);
        } else {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "File not exist download it");
            downloadAssetEntity(context, assetEntity, onDownloadFinished);
        }
    }

    public static void downloadAssetEntity(Context context, final AssetEntity assetEntity, OnDownloadFinished onDownloadFinished) {
        C0667a c0667a = new C0667a();
        c0667a.m1299a(assetEntity);
        List<OnDownloadFinished> m1304c = c0667a.m1304c();
        m1304c.add(onDownloadFinished);
        c0667a.m1300a(m1304c);
        c0667a.m1301a(C0724b.m1623a().m1624a(context, assetEntity, new Request.Callbacks<AssetEntity, Throwable>() { // from class: com.instabug.library.internal.storage.cache.AssetsCacheManager.1
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(AssetEntity assetEntity2) {
                AssetsCacheManager.addAssetEntity(assetEntity2);
                AssetsCacheManager.notifyDownloadFinishedSuccessfully(assetEntity2);
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Throwable th) {
                InstabugSDKLogger.m1801e(this, "downloading asset entity got error: ", th);
                AssetsCacheManager.notifyDownloadFailed(AssetEntity.this, th);
            }
        }));
        currentDownloadingFiles.put(c0667a.m1302a().getKey(), c0667a);
    }

    public static void addAssetEntity(AssetEntity assetEntity) {
        AssetCache cache = getCache();
        if (cache != null) {
            cache.put(assetEntity.getKey(), assetEntity);
        }
    }

    public static boolean isDownloading(String str) {
        return currentDownloadingFiles.get(str) != null;
    }

    public static void waitDownloadToFinish(AssetEntity assetEntity, OnDownloadFinished onDownloadFinished) {
        List<OnDownloadFinished> m1304c = currentDownloadingFiles.get(assetEntity.getKey()).m1304c();
        m1304c.add(onDownloadFinished);
        currentDownloadingFiles.get(assetEntity.getKey()).m1300a(m1304c);
    }

    public static void notifyDownloadFinishedSuccessfully(AssetEntity assetEntity) {
        for (OnDownloadFinished onDownloadFinished : currentDownloadingFiles.get(assetEntity.getKey()).m1304c()) {
            if (onDownloadFinished != null) {
                onDownloadFinished.onSuccess(assetEntity);
                currentDownloadingFiles.remove(assetEntity.getKey());
            }
        }
    }

    public static void notifyDownloadFailed(AssetEntity assetEntity, Throwable th) {
        for (OnDownloadFinished onDownloadFinished : currentDownloadingFiles.get(assetEntity.getKey()).m1304c()) {
            if (onDownloadFinished != null) {
                onDownloadFinished.onFailed(th);
                currentDownloadingFiles.remove(assetEntity.getKey());
            }
        }
    }

    public static void stopRunningDownloads() {
        Iterator<Map.Entry<String, C0667a>> it = currentDownloadingFiles.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().m1303b().unsubscribe();
        }
        currentDownloadingFiles.clear();
    }

    public static void clearRedundantFiles(Context context) {
        File[] listFiles = getCashDirectory(context).listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                file.delete();
            }
        }
    }

    public static void cleanUpCache(Context context) {
        Cache cache;
        stopRunningDownloads();
        if (CacheManager.getInstance().cacheExists(ASSETS_MEMORY_CACHE_KEY) && (cache = CacheManager.getInstance().getCache(ASSETS_MEMORY_CACHE_KEY)) != null) {
            cache.invalidate();
        }
        clearRedundantFiles(context);
    }

    public static File getCashDirectory(Context context) {
        String absolutePath;
        if (Environment.getExternalStorageState().equals(Environmenu.MEDIA_MOUNTED) && context.getExternalCacheDir() != null) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "Media Mounted");
            absolutePath = context.getExternalCacheDir().getPath();
        } else {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "External storage not available, saving file to internal storage.");
            absolutePath = context.getCacheDir().getAbsolutePath();
        }
        File file = new File(absolutePath + "/instabug/assetCache");
        if (!file.exists()) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "Is created: " + file.mkdirs());
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /* renamed from: com.instabug.library.internal.storage.cache.AssetsCacheManager$a */
    /* loaded from: classes.dex */
    public static class C0667a {

        /* renamed from: a */
        public AssetEntity f844a;

        /* renamed from: b */
        public Subscription f845b;

        /* renamed from: c */
        public List<OnDownloadFinished> f846c = new ArrayList();

        /* renamed from: a */
        public C0667a m1299a(AssetEntity assetEntity) {
            this.f844a = assetEntity;
            return this;
        }

        /* renamed from: a */
        public C0667a m1301a(Subscription subscription) {
            this.f845b = subscription;
            return this;
        }

        /* renamed from: a */
        public C0667a m1300a(List<OnDownloadFinished> list) {
            this.f846c = list;
            return this;
        }

        /* renamed from: a */
        public AssetEntity m1302a() {
            return this.f844a;
        }

        /* renamed from: b */
        public Subscription m1303b() {
            return this.f845b;
        }

        /* renamed from: c */
        public List<OnDownloadFinished> m1304c() {
            return this.f846c;
        }
    }
}
