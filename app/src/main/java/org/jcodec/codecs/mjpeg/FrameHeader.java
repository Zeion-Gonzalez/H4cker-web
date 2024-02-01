package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FrameHeader {
    int bitsPerSample;
    Component[] components;
    int headerLength;
    int height;
    int nComp;
    int width;

    /* loaded from: classes.dex */
    public static class Component {
        int index;
        int quantTable;
        int subH;
        int subV;
    }

    public int getHmax() {
        int max = 0;
        Component[] arr$ = this.components;
        for (Component c : arr$) {
            max = Math.max(max, c.subH);
        }
        return max;
    }

    public int getVmax() {
        int max = 0;
        Component[] arr$ = this.components;
        for (Component c : arr$) {
            max = Math.max(max, c.subV);
        }
        return max;
    }

    public static FrameHeader read(ByteBuffer is) {
        FrameHeader frame = new FrameHeader();
        frame.headerLength = is.getShort() & 65535;
        frame.bitsPerSample = is.get() & 255;
        frame.height = is.getShort() & 65535;
        frame.width = is.getShort() & 65535;
        frame.nComp = is.get() & 255;
        frame.components = new Component[frame.nComp];
        for (int i = 0; i < frame.components.length; i++) {
            Component[] componentArr = frame.components;
            Component c = new Component();
            componentArr[i] = c;
            c.index = is.get() & 255;
            int hv = is.get() & 255;
            c.subH = (hv & 240) >>> 4;
            c.subV = hv & 15;
            c.quantTable = is.get() & 255;
        }
        return frame;
    }
}
