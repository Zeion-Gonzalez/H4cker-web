package org.jcodec.api.specific;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public interface ContainerAdaptor {
    int[][] allocatePicture();

    boolean canSeek(Packet packet);

    Picture decodeFrame(Packet packet, int[][] iArr);

    FrameGrab.MediaInfo getMediaInfo();
}
