package com.instabug.library.internal.storage.cache;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: classes.dex */
public class InMemoryCache<K, V> extends Cache<K, V> {
    private final LinkedHashMap<K, V> map;

    public InMemoryCache(String str) {
        this(str, 1);
    }

    public InMemoryCache(String str, int i) {
        super(str, i);
        this.map = new LinkedHashMap<>();
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    @Nullable
    public V get(K k) {
        V v;
        synchronized (this.map) {
            v = this.map.get(k);
        }
        return v;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public V put(K k, V v) {
        V put;
        if (v != null && k != null) {
            synchronized (this.map) {
                put = this.map.put(k, v);
            }
            if (put == null) {
                notifyItemAdded(v);
                return v;
            }
            notifyItemUpdated(put, v);
            return put;
        }
        return null;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public V delete(K k) {
        V remove;
        synchronized (this.map) {
            remove = this.map.remove(k);
        }
        if (remove != null) {
            notifyItemRemoved(remove);
        }
        return remove;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public long size() {
        long size;
        synchronized (this.map) {
            size = this.map.size();
        }
        return size;
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public void invalidate() {
        synchronized (this.map) {
            this.map.clear();
        }
        notifyCacheInvalidated();
    }

    @Override // com.instabug.library.internal.storage.cache.Cache
    public List<V> getValues() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.map) {
            Iterator<K> it = this.map.keySet().iterator();
            while (it.hasNext()) {
                arrayList.add(get(it.next()));
            }
        }
        return arrayList;
    }
}
