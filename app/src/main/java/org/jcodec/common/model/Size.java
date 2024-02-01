package org.jcodec.common.model;

/* loaded from: classes.dex */
public class Size {
    private int height;
    private int width;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int hashCode() {
        int result = this.height + 31;
        return (result * 31) + this.width;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Size other = (Size) obj;
            return this.height == other.height && this.width == other.width;
        }
        return false;
    }
}
