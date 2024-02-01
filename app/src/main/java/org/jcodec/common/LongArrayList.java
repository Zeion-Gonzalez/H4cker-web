package org.jcodec.common;

import java.util.Arrays;

/* loaded from: classes.dex */
public class LongArrayList {
    private static final int DEFAULT_GROW_AMOUNT = 128;
    private int growAmount;
    private int size;
    private long[] storage;

    public LongArrayList() {
        this(128);
    }

    public LongArrayList(int growAmount) {
        this.growAmount = growAmount;
        this.storage = new long[growAmount];
    }

    public long[] toArray() {
        long[] result = new long[this.size];
        System.arraycopy(this.storage, 0, result, 0, this.size);
        return result;
    }

    public void add(long val) {
        if (this.size >= this.storage.length) {
            long[] ns = new long[this.storage.length + this.growAmount];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        long[] jArr = this.storage;
        int i = this.size;
        this.size = i + 1;
        jArr[i] = val;
    }

    public void push(long id) {
        add(id);
    }

    public void pop() {
        if (this.size != 0) {
            this.size--;
        }
    }

    public void set(int index, int value) {
        this.storage[index] = value;
    }

    public long get(int index) {
        return this.storage[index];
    }

    public void fill(int start, int end, int val) {
        if (end > this.storage.length) {
            long[] ns = new long[this.growAmount + end];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        Arrays.fill(this.storage, start, end, val);
        this.size = Math.max(this.size, end);
    }

    public int size() {
        return this.size;
    }

    public void addAll(long[] other) {
        if (this.size + other.length >= this.storage.length) {
            long[] ns = new long[this.size + this.growAmount + other.length];
            System.arraycopy(this.storage, 0, ns, 0, this.size);
            this.storage = ns;
        }
        System.arraycopy(other, 0, this.storage, this.size, other.length);
        this.size += other.length;
    }

    public void clear() {
        this.size = 0;
    }

    public boolean contains(long needle) {
        for (int i = 0; i < this.size; i++) {
            if (this.storage[i] == needle) {
                return true;
            }
        }
        return false;
    }
}
