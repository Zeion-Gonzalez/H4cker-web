package com.instabug.library.internal.storage.cache;

import com.instabug.library.model.AssetEntity;
import com.instabug.library.util.InstabugSDKLogger;

/* loaded from: classes.dex */
public class AssetCache extends InMemoryCache<String, AssetEntity> {
    public AssetCache(String str) {
        super(str);
    }

    @Override // com.instabug.library.internal.storage.cache.InMemoryCache, com.instabug.library.internal.storage.cache.Cache
    public void invalidate() {
        for (AssetEntity assetEntity : getValues()) {
            InstabugSDKLogger.m1799d(AssetsCacheManager.class, "Delete file: " + assetEntity.getFile().getPath() + "," + assetEntity.getFile().delete());
        }
        super.invalidate();
    }
}
