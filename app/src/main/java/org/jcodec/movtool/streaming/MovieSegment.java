package org.jcodec.movtool.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public interface MovieSegment {
    ByteBuffer getData() throws IOException;

    int getDataLen() throws IOException;

    int getNo();

    long getPos();
}
