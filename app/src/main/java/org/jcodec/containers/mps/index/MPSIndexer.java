package org.jcodec.containers.mps.index;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.MPSDemuxer;
import org.jcodec.containers.mps.MPSUtils;

/* loaded from: classes.dex */
public class MPSIndexer extends BaseIndexer {
    private long predFileStart;

    public void index(File source, NIOUtils.FileReaderListener listener) throws IOException {
        newReader().readFile(source, 65536, listener);
    }

    public void index(SeekableByteChannel source, NIOUtils.FileReaderListener listener) throws IOException {
        newReader().readFile(source, 65536, listener);
    }

    private NIOUtils.FileReader newReader() {
        return new NIOUtils.FileReader() { // from class: org.jcodec.containers.mps.index.MPSIndexer.1
            @Override // org.jcodec.common.NIOUtils.FileReader
            protected void data(ByteBuffer data, long filePos) {
                MPSIndexer.this.analyseBuffer(data, filePos);
            }

            @Override // org.jcodec.common.NIOUtils.FileReader
            protected void done() {
                MPSIndexer.this.finishAnalyse();
            }
        };
    }

    @Override // org.jcodec.containers.mps.MPSUtils.PESReader
    protected void pes(ByteBuffer pesBuffer, long start, int pesLen, int stream) {
        if (MPSUtils.mediaStream(stream)) {
            MPSDemuxer.PESPacket pesHeader = MPSUtils.readPESHeader(pesBuffer, start);
            int leading = this.predFileStart != start ? 0 + ((int) (start - this.predFileStart)) : 0;
            this.predFileStart = pesLen + start;
            savePESMeta(stream, MPSIndex.makePESToken(leading, pesLen, pesBuffer.remaining()));
            getAnalyser(stream).pkt(pesBuffer, pesHeader);
        }
    }

    public static void main(String[] args) throws IOException {
        MPSIndexer indexer = new MPSIndexer();
        indexer.index(new File(args[0]), new NIOUtils.FileReaderListener() { // from class: org.jcodec.containers.mps.index.MPSIndexer.2
            @Override // org.jcodec.common.NIOUtils.FileReaderListener
            public void progress(int percentDone) {
                System.out.println(percentDone);
            }
        });
        ByteBuffer index = ByteBuffer.allocate(65536);
        indexer.serialize().serializeTo(index);
        NIOUtils.writeTo(index, new File(args[1]));
    }
}
