package org.jcodec.scale;

import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class Yuv422pToYuv420p implements Transform {
    private int shiftDown;
    private int shiftUp;

    public Yuv422pToYuv420p(int shiftUp, int shiftDown) {
        this.shiftUp = shiftUp;
        this.shiftDown = shiftDown;
    }

    @Override // org.jcodec.scale.Transform
    public void transform(Picture src, Picture dst) {
        int lumaSize = src.getWidth() * src.getHeight();
        System.arraycopy(src.getPlaneData(0), 0, dst.getPlaneData(0), 0, lumaSize);
        copyAvg(src.getPlaneData(1), dst.getPlaneData(1), src.getPlaneWidth(1), src.getPlaneHeight(1));
        copyAvg(src.getPlaneData(2), dst.getPlaneData(2), src.getPlaneWidth(2), src.getPlaneHeight(2));
        if (this.shiftUp > this.shiftDown) {
            m2137up(dst.getPlaneData(0), this.shiftUp - this.shiftDown);
            m2137up(dst.getPlaneData(1), this.shiftUp - this.shiftDown);
            m2137up(dst.getPlaneData(2), this.shiftUp - this.shiftDown);
        } else if (this.shiftDown > this.shiftUp) {
            down(dst.getPlaneData(0), this.shiftDown - this.shiftUp);
            down(dst.getPlaneData(1), this.shiftDown - this.shiftUp);
            down(dst.getPlaneData(2), this.shiftDown - this.shiftUp);
        }
    }

    private void down(int[] dst, int down) {
        for (int i = 0; i < dst.length; i++) {
            dst[i] = dst[i] >> down;
        }
    }

    /* renamed from: up */
    private void m2137up(int[] dst, int up) {
        for (int i = 0; i < dst.length; i++) {
            dst[i] = dst[i] << up;
        }
    }

    private void copyAvg(int[] src, int[] dst, int width, int height) {
        int offSrc = 0;
        int offDst = 0;
        for (int y = 0; y < height / 2; y++) {
            int x = 0;
            while (x < width) {
                dst[offDst] = ((src[offSrc] + src[offSrc + width]) + 1) >> 1;
                x++;
                offDst++;
                offSrc++;
            }
            offSrc += width;
        }
    }
}
