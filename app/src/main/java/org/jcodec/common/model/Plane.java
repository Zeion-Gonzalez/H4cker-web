package org.jcodec.common.model;

/* loaded from: classes.dex */
public class Plane {
    int[] data;
    Size size;

    public Plane(int[] data, Size size) {
        this.data = data;
        this.size = size;
    }

    public int[] getData() {
        return this.data;
    }

    public Size getSize() {
        return this.size;
    }
}
