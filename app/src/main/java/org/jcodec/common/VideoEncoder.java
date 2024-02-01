package org.jcodec.common;

import java.nio.ByteBuffer;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public interface VideoEncoder {
    ByteBuffer encodeFrame(Picture picture, ByteBuffer byteBuffer);

    ColorSpace[] getSupportedColorSpaces();
}
