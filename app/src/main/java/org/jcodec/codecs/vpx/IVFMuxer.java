package org.jcodec.codecs.vpx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Packet;

/* loaded from: classes.dex */
public class IVFMuxer {

    /* renamed from: ch */
    private SeekableByteChannel f1491ch;
    private int nFrames;

    public IVFMuxer(SeekableByteChannel ch, int w, int h, int frameRate) throws IOException {
        ByteBuffer ivf = ByteBuffer.allocate(32);
        ivf.order(ByteOrder.LITTLE_ENDIAN);
        ivf.put((byte) 68);
        ivf.put((byte) 75);
        ivf.put((byte) 73);
        ivf.put((byte) 70);
        ivf.putShort((short) 0);
        ivf.putShort((short) 32);
        ivf.putInt(808996950);
        ivf.putShort((short) w);
        ivf.putShort((short) h);
        ivf.putInt(frameRate);
        ivf.putInt(1);
        ivf.putInt(1);
        ivf.clear();
        ch.write(ivf);
        this.f1491ch = ch;
    }

    public void addFrame(Packet pkt) throws IOException {
        ByteBuffer fh = ByteBuffer.allocate(12);
        fh.order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer frame = pkt.getData();
        fh.putInt(frame.remaining());
        fh.putLong(this.nFrames);
        fh.clear();
        this.f1491ch.write(fh);
        this.f1491ch.write(frame);
        this.nFrames++;
    }

    public void close() throws IOException {
        this.f1491ch.position(24L);
        NIOUtils.writeIntLE(this.f1491ch, this.nFrames);
    }
}
