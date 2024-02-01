package org.jcodec.common;

import java.util.Arrays;

/* loaded from: classes.dex */
public class ByteArrayList {
    private static final int DEFAULT_GROW_AMOUNT = 2048;
    private int growAmount;
    private int size;
    private byte[] storage;

    public ByteArrayList() {
        this(2048);
    }

    public ByteArrayList(int growAmount) {
        this.growAmount = growAmount;
        this.storage = new byte[growAmount];
    }

    public byte[] toArray() {
        byte[] result = new byte[this.size];
        System.arraycopy(this.storage, 0, result, 0, this.size);
        return result;
    }

    public void add(byte val) {
        if (this.size >= this.storage.length) {
            byte[] ns = new byte[this.storage.length + this.growAmount];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        byte[] bArr = this.storage;
        int i = this.size;
        this.size = i + 1;
        bArr[i] = val;
    }

    public void push(byte id) {
        add(id);
    }

    public void pop() {
        if (this.size != 0) {
            this.size--;
        }
    }

    public void set(int index, byte value) {
        this.storage[index] = value;
    }

    public byte get(int index) {
        return this.storage[index];
    }

    public void fill(int start, int end, byte val) {
        if (end > this.storage.length) {
            byte[] ns = new byte[this.growAmount + end];
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            this.storage = ns;
        }
        Arrays.fill(this.storage, start, end, val);
        this.size = Math.max(this.size, end);
    }

    public int size() {
        return this.size;
    }

    public void addAll(byte[] other) {
        if (this.size + other.length >= this.storage.length) {
            byte[] ns = new byte[this.size + this.growAmount + other.length];
            System.arraycopy(this.storage, 0, ns, 0, this.size);
            this.storage = ns;
        }
        System.arraycopy(other, 0, this.storage, this.size, other.length);
        this.size += other.length;
    }

    public boolean contains(byte needle) {
        for (int i = 0; i < this.size; i++) {
            if (this.storage[i] == needle) {
                return true;
            }
        }
        return false;
    }
}
