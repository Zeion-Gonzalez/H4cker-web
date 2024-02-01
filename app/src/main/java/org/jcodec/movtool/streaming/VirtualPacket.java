package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;

/* loaded from: classes.dex */
public interface VirtualPacket {
    public static final Comparator<? super VirtualPacket> byPts = new Comparator<VirtualPacket>() { // from class: org.jcodec.movtool.streaming.VirtualPacket.1
        @Override // java.util.Comparator
        public int compare(VirtualPacket o1, VirtualPacket o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.getPts() >= o2.getPts()) {
                return o1.getPts() == o2.getPts() ? 0 : 1;
            }
            return -1;
        }
    };

    ByteBuffer getData() throws IOException;

    int getDataLen() throws IOException;

    double getDuration();

    int getFrameNo();

    double getPts();

    boolean isKeyframe();
}
