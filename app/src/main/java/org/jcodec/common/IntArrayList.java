package org.jcodec.common;

import java.util.Arrays;

/* loaded from: classes.dex */
public class IntArrayList {
    private static final int DEFAULT_GROW_AMOUNT = 128;
    private int growAmount;
    private int size;
    private int[] storage;

    public IntArrayList() {
        this(128);
    }

    public IntArrayList(int growAmount) {
        this.growAmount = growAmount;
        this.storage = new int[growAmount];
    }

    public int[] toArray() {
        int[] result = new int[this.size];
        System.arraycopy(this.storage, 0, result, 0, this.size);
        return result;
    }

    public void add(int val) {
        if (this.size >= this.storage.length) {
            int[] ns = new int[this.storage.length + this.growAmount];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        int[] iArr = this.storage;
        int i = this.size;
        this.size = i + 1;
        iArr[i] = val;
    }

    public void push(int id) {
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

    public int get(int index) {
        return this.storage[index];
    }

    public void fill(int start, int end, int val) {
        if (end > this.storage.length) {
            int[] ns = new int[this.growAmount + end];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        Arrays.fill(this.storage, start, end, val);
        this.size = Math.max(this.size, end);
    }

    public int size() {
        return this.size;
    }

    public void addAll(int[] other) {
        if (this.size + other.length >= this.storage.length) {
            int[] ns = new int[this.size + this.growAmount + other.length];
            System.arraycopy(this.storage, 0, ns, 0, this.size);
            this.storage = ns;
        }
        System.arraycopy(other, 0, this.storage, this.size, other.length);
        this.size += other.length;
    }

    public void clear() {
        this.size = 0;
    }

    public boolean contains(int needle) {
        for (int i = 0; i < this.size; i++) {
            if (this.storage[i] == needle) {
                return true;
            }
        }
        return false;
    }
}
