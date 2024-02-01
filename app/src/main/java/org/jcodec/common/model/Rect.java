package org.jcodec.common.model;

/* loaded from: classes.dex */
public class Rect {
    private int height;
    private int width;

    /* renamed from: x */
    private int f1531x;

    /* renamed from: y */
    private int f1532y;

    public Rect(int x, int y, int width, int height) {
        this.f1531x = x;
        this.f1532y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return this.f1531x;
    }

    public int getY() {
        return this.f1532y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int hashCode() {
        int result = this.height + 31;
        return (((((result * 31) + this.width) * 31) + this.f1531x) * 31) + this.f1532y;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Rect other = (Rect) obj;
            return this.height == other.height && this.width == other.width && this.f1531x == other.f1531x && this.f1532y == other.f1532y;
        }
        return false;
    }

    public String toString() {
        return "Rect [x=" + this.f1531x + ", y=" + this.f1532y + ", width=" + this.width + ", height=" + this.height + "]";
    }
}
