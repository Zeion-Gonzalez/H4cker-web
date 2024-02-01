package org.jcodec.movtool.streaming.tracks;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.Assert;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.tracks.MPSTrackFactory;

/* loaded from: classes.dex */
public class MTSTrackFactory {
    private List<MTSProgram> programs = new ArrayList();

    public MTSTrackFactory(ByteBuffer index, FilePool fp) throws IOException {
        while (index.remaining() >= 6) {
            int len = index.getInt() - 4;
            ByteBuffer sub = NIOUtils.read(index, len);
            this.programs.add(new MTSProgram(sub, fp));
        }
    }

    /* loaded from: classes.dex */
    public class MTSProgram extends MPSTrackFactory {
        private int targetGuid;

        public MTSProgram(ByteBuffer index, FilePool fp) throws IOException {
            super(index, fp);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.jcodec.movtool.streaming.tracks.MPSTrackFactory
        public void readIndex(ByteBuffer index) throws IOException {
            this.targetGuid = index.getShort() & 65535;
            super.readIndex(index);
        }

        @Override // org.jcodec.movtool.streaming.tracks.MPSTrackFactory
        protected MPSTrackFactory.Stream createStream(int streamId) {
            return new MTSStream(streamId);
        }

        /* loaded from: classes.dex */
        public class MTSStream extends MPSTrackFactory.Stream {
            public MTSStream(int streamId) {
                super(streamId);
            }

            @Override // org.jcodec.movtool.streaming.tracks.MPSTrackFactory.Stream
            protected ByteBuffer readPes(SeekableByteChannel ch, long pesPosition, int pesSize, int payloadSize, int pesAbsIdx) throws IOException {
                ch.position(188 * pesPosition);
                ByteBuffer buf = NIOUtils.fetchFrom(ch, pesSize * 188);
                ByteBuffer dst = buf.duplicate();
                while (buf.hasRemaining()) {
                    ByteBuffer tsBuf = NIOUtils.read(buf, 188);
                    Assert.assertEquals(71, tsBuf.get() & 255);
                    int guidFlags = ((tsBuf.get() & 255) << 8) | (tsBuf.get() & 255);
                    int guid = guidFlags & 8191;
                    if (guid == MTSProgram.this.targetGuid) {
                        int b0 = tsBuf.get() & 255;
                        int i = b0 & 15;
                        if ((b0 & 32) != 0) {
                            NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                        }
                        dst.put(tsBuf);
                    }
                }
                dst.flip();
                MPSUtils.readPESHeader(dst, 0L);
                dst.limit(dst.position() + payloadSize);
                return dst;
            }
        }
    }

    public List<MPSTrackFactory.Stream> getVideoStreams() {
        List<MPSTrackFactory.Stream> ret = new ArrayList<>();
        for (MTSProgram mtsProgram : this.programs) {
            ret.addAll(mtsProgram.getVideoStreams());
        }
        return ret;
    }

    public List<MPSTrackFactory.Stream> getAudioStreams() {
        List<MPSTrackFactory.Stream> ret = new ArrayList<>();
        for (MTSProgram mtsProgram : this.programs) {
            ret.addAll(mtsProgram.getAudioStreams());
        }
        return ret;
    }

    public List<MPSTrackFactory.Stream> getStreams() {
        List<MPSTrackFactory.Stream> ret = new ArrayList<>();
        for (MTSProgram mtsProgram : this.programs) {
            ret.addAll(mtsProgram.getStreams());
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        FilePool fp = new FilePool(new File(args[0]), 10);
        MTSTrackFactory factory = new MTSTrackFactory(NIOUtils.fetchFrom(new File(args[1])), fp);
        MPSTrackFactory.Stream stream = factory.getVideoStreams().get(0);
        FileChannelWrapper ch = NIOUtils.writableFileChannel(new File(args[2]));
        List<VirtualPacket> pkt = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            pkt.add(stream.nextPacket());
        }
        for (VirtualPacket virtualPacket : pkt) {
            ch.write(virtualPacket.getData());
        }
        ch.close();
    }
}
