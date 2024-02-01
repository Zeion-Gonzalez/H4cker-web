package com.instabug.library.internal.storage.cache;

/* loaded from: classes.dex */
public interface CacheChangedListener<V> {
    void onCacheInvalidated();

    void onCachedItemAdded(V v);

    void onCachedItemRemoved(V v);

    void onCachedItemUpdated(V v, V v2);
}
