package org.jcodec.common;

import java.lang.reflect.Array;

/* loaded from: classes.dex */
public class IntObjectMap<T> {
    private static final int GROW_BY = 128;
    private int size;
    private Object[] storage = new Object[128];

    public void put(int key, T val) {
        if (this.storage.length <= key) {
            Object[] ns = new Object[key + 128];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        if (this.storage[key] == null) {
            this.size++;
        }
        this.storage[key] = val;
    }

    public T get(int key) {
        if (key >= this.storage.length) {
            return null;
        }
        return (T) this.storage[key];
    }

    public int[] keys() {
        int[] result = new int[this.size];
        int r = 0;
        for (int i = 0; i < this.storage.length; i++) {
            if (this.storage[i] != null) {
                result[r] = i;
                r++;
            }
        }
        return result;
    }

    public void clear() {
        for (int i = 0; i < this.storage.length; i++) {
            this.storage[i] = null;
        }
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public void remove(int key) {
        if (this.storage[key] != null) {
            this.size--;
        }
        this.storage[key] = null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T[] values(T[] runtime) {
        T[] result = (T[]) ((Object[]) Array.newInstance(runtime.getClass().getComponentType(), this.size));
        int r = 0;
        for (int i = 0; i < this.storage.length; i++) {
            if (this.storage[i] != null) {
                result[r] = this.storage[i];
                r++;
            }
        }
        return result;
    }
}
