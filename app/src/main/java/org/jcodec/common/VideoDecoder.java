package org.jcodec.common;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public interface VideoDecoder {
    Picture decodeFrame(ByteBuffer byteBuffer, int[][] iArr);

    int probe(ByteBuffer byteBuffer);
}
