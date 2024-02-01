package org.jcodec.common;

import java.util.Arrays;

/* loaded from: classes.dex */
public class IntIntMap {
    private static final int GROW_BY = 128;
    private int size;
    private int[] storage = createArray(128);

    public IntIntMap() {
        Arrays.fill(this.storage, Integer.MIN_VALUE);
    }

    public void put(int key, int val) {
        if (val == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("This implementation can not store -2147483648");
        }
        if (this.storage.length <= key) {
            int[] ns = createArray(key + 128);
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            Arrays.fill(ns, this.storage.length, ns.length, Integer.MIN_VALUE);
            this.storage = ns;
        }
        if (this.storage[key] == Integer.MIN_VALUE) {
            this.size++;
        }
        this.storage[key] = val;
    }

    public int get(int key) {
        return (key >= this.storage.length ? null : Integer.valueOf(this.storage[key])).intValue();
    }

    public int[] keys() {
        int[] result = new int[this.size];
        int r = 0;
        for (int i = 0; i < this.storage.length; i++) {
            if (this.storage[i] != Integer.MIN_VALUE) {
                result[r] = i;
                r++;
            }
        }
        return result;
    }

    public void clear() {
        for (int i = 0; i < this.storage.length; i++) {
            this.storage[i] = Integer.MIN_VALUE;
        }
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public void remove(int key) {
        if (this.storage[key] != Integer.MIN_VALUE) {
            this.size--;
        }
        this.storage[key] = Integer.MIN_VALUE;
    }

    public int[] values() {
        int[] result = createArray(this.size);
        int r = 0;
        for (int i = 0; i < this.storage.length; i++) {
            if (this.storage[i] != Integer.MIN_VALUE) {
                result[r] = this.storage[i];
                r++;
            }
        }
        return result;
    }

    private int[] createArray(int size) {
        return new int[size];
    }
}
