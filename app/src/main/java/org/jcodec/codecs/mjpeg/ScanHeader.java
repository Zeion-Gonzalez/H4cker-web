package org.jcodec.codecs.mjpeg;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ScanHeader {

    /* renamed from: ah */
    int f1465ah;

    /* renamed from: al */
    int f1466al;
    Component[] components;

    /* renamed from: ls */
    int f1467ls;

    /* renamed from: ns */
    int f1468ns;

    /* renamed from: se */
    int f1469se;

    /* renamed from: ss */
    int f1470ss;

    /* loaded from: classes.dex */
    public static class Component {

        /* renamed from: cs */
        int f1471cs;

        /* renamed from: ta */
        int f1472ta;

        /* renamed from: td */
        int f1473td;
    }

    public boolean isInterleaved() {
        return this.f1468ns > 1;
    }

    public static ScanHeader read(ByteBuffer bb) {
        ScanHeader scan = new ScanHeader();
        scan.f1467ls = bb.getShort() & 65535;
        scan.f1468ns = bb.get() & 255;
        scan.components = new Component[scan.f1468ns];
        for (int i = 0; i < scan.components.length; i++) {
            Component[] componentArr = scan.components;
            Component c = new Component();
            componentArr[i] = c;
            c.f1471cs = bb.get() & 255;
            int tdta = bb.get() & 255;
            c.f1473td = (tdta & 240) >>> 4;
            c.f1472ta = tdta & 15;
        }
        scan.f1470ss = bb.get() & 255;
        scan.f1469se = bb.get() & 255;
        int ahal = bb.get() & 255;
        scan.f1465ah = (ahal & 240) >>> 4;
        scan.f1466al = ahal & 15;
        return scan;
    }
}
