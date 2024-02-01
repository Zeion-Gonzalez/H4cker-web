package org.jcodec.containers.mps.index;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.logging.Logger;
import org.jcodec.containers.mps.MPSDemuxer;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mps.MTSUtils;
import org.jcodec.containers.mps.index.MTSIndex;

/* loaded from: classes.dex */
public class MTSIndexer {
    public static final int BUFFER_SIZE = 96256;
    private MTSAnalyser[] indexers;

    public void index(File source, NIOUtils.FileReaderListener listener) throws IOException {
        index(listener, MTSUtils.getMediaPids(source)).readFile(source, 96256, listener);
    }

    public void index(SeekableByteChannel source, NIOUtils.FileReaderListener listener) throws IOException {
        index(listener, MTSUtils.getMediaPids(source)).readFile(source, 96256, listener);
    }

    public NIOUtils.FileReader index(NIOUtils.FileReaderListener listener, int[] targetGuids) throws IOException {
        this.indexers = new MTSAnalyser[targetGuids.length];
        for (int i = 0; i < targetGuids.length; i++) {
            this.indexers[i] = new MTSAnalyser(targetGuids[i]);
        }
        return new NIOUtils.FileReader() { // from class: org.jcodec.containers.mps.index.MTSIndexer.1
            @Override // org.jcodec.common.NIOUtils.FileReader
            protected void data(ByteBuffer data, long filePos) {
                analyseBuffer(data, filePos);
            }

            protected void analyseBuffer(ByteBuffer buf, long pos) {
                while (buf.hasRemaining()) {
                    ByteBuffer tsBuf = NIOUtils.read(buf, 188);
                    pos += 188;
                    Assert.assertEquals(71, tsBuf.get() & 255);
                    int guidFlags = ((tsBuf.get() & 255) << 8) | (tsBuf.get() & 255);
                    int guid = guidFlags & 8191;
                    for (int i2 = 0; i2 < MTSIndexer.this.indexers.length; i2++) {
                        if (guid == MTSIndexer.this.indexers[i2].targetGuid) {
                            int i3 = (guidFlags >> 14) & 1;
                            int b0 = tsBuf.get() & 255;
                            int i4 = b0 & 15;
                            if ((b0 & 32) != 0) {
                                NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                            }
                            MTSIndexer.this.indexers[i2].analyseBuffer(tsBuf, pos - tsBuf.remaining());
                        }
                    }
                }
            }

            @Override // org.jcodec.common.NIOUtils.FileReader
            protected void done() {
                MTSAnalyser[] arr$ = MTSIndexer.this.indexers;
                for (MTSAnalyser mtsAnalyser : arr$) {
                    mtsAnalyser.finishAnalyse();
                }
            }
        };
    }

    public MTSIndex serialize() {
        MTSIndex.MTSProgram[] programs = new MTSIndex.MTSProgram[this.indexers.length];
        for (int i = 0; i < this.indexers.length; i++) {
            programs[i] = this.indexers[i].serializeTo();
        }
        return new MTSIndex(programs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MTSAnalyser extends BaseIndexer {
        private long predFileStartInTsPkt;
        private int targetGuid;

        public MTSAnalyser(int targetGuid) {
            this.targetGuid = targetGuid;
        }

        public MTSIndex.MTSProgram serializeTo() {
            return new MTSIndex.MTSProgram(super.serialize(), this.targetGuid);
        }

        @Override // org.jcodec.containers.mps.MPSUtils.PESReader
        protected void pes(ByteBuffer pesBuffer, long start, int pesLen, int stream) {
            if (MPSUtils.mediaStream(stream)) {
                Logger.debug(String.format("PES: %08x, %d", Long.valueOf(start), Integer.valueOf(pesLen)));
                MPSDemuxer.PESPacket pesHeader = MPSUtils.readPESHeader(pesBuffer, start);
                int leadingTsPkt = 0;
                if (this.predFileStartInTsPkt != start) {
                    leadingTsPkt = (int) ((start / 188) - this.predFileStartInTsPkt);
                }
                this.predFileStartInTsPkt = (pesLen + start) / 188;
                int tsPktInPes = (int) (this.predFileStartInTsPkt - (start / 188));
                savePESMeta(stream, MPSIndex.makePESToken(leadingTsPkt, tsPktInPes, pesBuffer.remaining()));
                getAnalyser(stream).pkt(pesBuffer, pesHeader);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File src = new File(args[0]);
        MTSIndexer indexer = new MTSIndexer();
        indexer.index(src, new NIOUtils.FileReaderListener() { // from class: org.jcodec.containers.mps.index.MTSIndexer.2
            @Override // org.jcodec.common.NIOUtils.FileReaderListener
            public void progress(int percentDone) {
                System.out.println(percentDone);
            }
        });
        MTSIndex index = indexer.serialize();
        NIOUtils.writeTo(index.serialize(), new File(args[1]));
    }
}
