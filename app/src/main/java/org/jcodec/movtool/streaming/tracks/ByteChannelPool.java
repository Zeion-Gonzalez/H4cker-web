package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import org.jcodec.common.SeekableByteChannel;

/* loaded from: classes.dex */
public interface ByteChannelPool {
    void close();

    SeekableByteChannel getChannel() throws IOException;
}
