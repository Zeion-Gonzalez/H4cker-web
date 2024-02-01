package org.jcodec.common.model;

/* loaded from: classes.dex */
public class Picture {
    private ColorSpace color;
    private Rect crop;
    private int[][] data;
    private int height;
    private int width;

    public Picture(int width, int height, int[][] data, ColorSpace color) {
        this(width, height, data, color, new Rect(0, 0, width, height));
    }

    public Picture(int width, int height, int[][] data, ColorSpace color, Rect crop) {
        this.width = width;
        this.height = height;
        this.data = data;
        this.color = color;
        this.crop = crop;
    }

    public Picture(Picture other) {
        this(other.width, other.height, other.data, other.color, other.crop);
    }

    public static Picture create(int width, int height, ColorSpace colorSpace) {
        return create(width, height, colorSpace, null);
    }

    public static Picture create(int width, int height, ColorSpace colorSpace, Rect crop) {
        int plane;
        int[] planeSizes = new int[4];
        for (int i = 0; i < colorSpace.nComp; i++) {
            int i2 = colorSpace.compPlane[i];
            planeSizes[i2] = planeSizes[i2] + ((width >> colorSpace.compWidth[i]) * (height >> colorSpace.compHeight[i]));
        }
        int nPlanes = 0;
        for (int i3 = 0; i3 < 4; i3++) {
            nPlanes += planeSizes[i3] != 0 ? 1 : 0;
        }
        int[][] data = new int[nPlanes];
        int i4 = 0;
        int plane2 = 0;
        while (i4 < 4) {
            if (planeSizes[i4] != 0) {
                plane = plane2 + 1;
                data[plane2] = new int[planeSizes[i4]];
            } else {
                plane = plane2;
            }
            i4++;
            plane2 = plane;
        }
        return new Picture(width, height, data, colorSpace, crop);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int[] getPlaneData(int plane) {
        return this.data[plane];
    }

    public ColorSpace getColor() {
        return this.color;
    }

    public int[][] getData() {
        return this.data;
    }

    public Rect getCrop() {
        return this.crop;
    }

    public int getPlaneWidth(int plane) {
        return this.width >> this.color.compWidth[plane];
    }

    public int getPlaneHeight(int plane) {
        return this.height >> this.color.compHeight[plane];
    }

    public boolean compatible(Picture src) {
        return src.color == this.color && src.width == this.width && src.height == this.height;
    }

    public Picture createCompatible() {
        return create(this.width, this.height, this.color);
    }

    public void copyFrom(Picture src) {
        if (!compatible(src)) {
            throw new IllegalArgumentException("Can not copy to incompatible picture");
        }
        for (int plane = 0; plane < this.color.nComp; plane++) {
            if (this.data[plane] != null) {
                System.arraycopy(src.data[plane], 0, this.data[plane], 0, (this.width >> this.color.compWidth[plane]) * (this.height >> this.color.compHeight[plane]));
            }
        }
    }

    public Picture cropped() {
        if (this.crop == null || (this.crop.getX() == 0 && this.crop.getY() == 0 && this.crop.getWidth() == this.width && this.crop.getHeight() == this.height)) {
            return this;
        }
        Picture result = create(this.crop.getWidth(), this.crop.getHeight(), this.color);
        for (int plane = 0; plane < this.color.nComp; plane++) {
            if (this.data[plane] != null) {
                cropSub(this.data[plane], this.crop.getX() >> this.color.compWidth[plane], this.crop.getY() >> this.color.compHeight[plane], this.crop.getWidth() >> this.color.compWidth[plane], this.crop.getHeight() >> this.color.compHeight[plane], this.width >> this.color.compWidth[plane], result.data[plane]);
            }
        }
        return result;
    }

    private void cropSub(int[] src, int x, int y, int w, int h, int srcStride, int[] tgt) {
        int srcOff = (y * srcStride) + x;
        int dstOff = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                tgt[dstOff + j] = src[srcOff + j];
            }
            srcOff += srcStride;
            dstOff += w;
        }
    }

    public void setCrop(Rect crop) {
        this.crop = crop;
    }

    public int getCroppedWidth() {
        return this.crop == null ? this.width : this.crop.getWidth();
    }

    public int getCroppedHeight() {
        return this.crop == null ? this.height : this.crop.getHeight();
    }
}
