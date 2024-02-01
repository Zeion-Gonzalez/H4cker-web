package org.jcodec.api.specific;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;

/* loaded from: classes.dex */
public class GenericAdaptor implements ContainerAdaptor {
    private VideoDecoder decoder;

    public GenericAdaptor(VideoDecoder detect) {
        this.decoder = detect;
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public Picture decodeFrame(Packet packet, int[][] data) {
        return this.decoder.decodeFrame(packet.getData(), data);
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public boolean canSeek(Packet data) {
        return true;
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public int[][] allocatePicture() {
        return Picture.create(1920, 1088, ColorSpace.YUV444).getData();
    }

    @Override // org.jcodec.api.specific.ContainerAdaptor
    public FrameGrab.MediaInfo getMediaInfo() {
        return new FrameGrab.MediaInfo(new Size(0, 0));
    }
}
