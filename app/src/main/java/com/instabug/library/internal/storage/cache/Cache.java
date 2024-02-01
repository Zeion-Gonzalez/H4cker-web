package com.instabug.library.internal.storage.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class Cache<K, V> {
    private int appVersion;

    /* renamed from: id */
    private String f847id;
    private final List<CacheChangedListener<V>> listeners;

    public abstract V delete(K k);

    public abstract V get(K k);

    public abstract List<V> getValues();

    public abstract void invalidate();

    public abstract V put(K k, V v);

    public abstract long size();

    public Cache(String str) {
        this(str, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cache(String str, int i) {
        this.appVersion = -1;
        this.f847id = str;
        this.appVersion = i;
        this.listeners = new ArrayList();
    }

    public void notifyItemRemoved(V v) {
        Iterator<CacheChangedListener<V>> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onCachedItemRemoved(v);
        }
    }

    public void notifyItemAdded(V v) {
        Iterator<CacheChangedListener<V>> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onCachedItemAdded(v);
        }
    }

    public void notifyItemUpdated(V v, V v2) {
        Iterator<CacheChangedListener<V>> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onCachedItemUpdated(v, v2);
        }
    }

    public void notifyCacheInvalidated() {
        Iterator<CacheChangedListener<V>> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onCacheInvalidated();
        }
    }

    public String getId() {
        return this.f847id;
    }

    public int getAppVersion() {
        return this.appVersion;
    }

    public boolean addOnCacheChangedListener(CacheChangedListener<V> cacheChangedListener) {
        return !this.listeners.contains(cacheChangedListener) && this.listeners.add(cacheChangedListener);
    }

    public boolean removeOnCacheChangedListener(CacheChangedListener<V> cacheChangedListener) {
        return this.listeners.remove(cacheChangedListener);
    }
}
