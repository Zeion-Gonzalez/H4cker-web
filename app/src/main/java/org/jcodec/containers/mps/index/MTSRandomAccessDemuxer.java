package org.jcodec.containers.mps.index;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mps.index.MPSIndex;
import org.jcodec.containers.mps.index.MPSRandomAccessDemuxer;
import org.jcodec.containers.mps.index.MTSIndex;

/* loaded from: classes.dex */
public class MTSRandomAccessDemuxer {

    /* renamed from: ch */
    private SeekableByteChannel f1548ch;
    private MTSIndex.MTSProgram[] programs;

    public MTSRandomAccessDemuxer(SeekableByteChannel ch, MTSIndex index) {
        this.programs = index.getPrograms();
        this.f1548ch = ch;
    }

    public int[] getGuids() {
        int[] guids = new int[this.programs.length];
        for (int i = 0; i < this.programs.length; i++) {
            guids[i] = this.programs[i].getTargetGuid();
        }
        return guids;
    }

    public MPSRandomAccessDemuxer getProgramDemuxer(final int tgtGuid) throws IOException {
        MPSIndex index = getProgram(tgtGuid);
        return new MPSRandomAccessDemuxer(this.f1548ch, index) { // from class: org.jcodec.containers.mps.index.MTSRandomAccessDemuxer.1
            @Override // org.jcodec.containers.mps.index.MPSRandomAccessDemuxer
            protected MPSRandomAccessDemuxer.Stream newStream(SeekableByteChannel ch, MPSIndex.MPSStreamIndex streamIndex) throws IOException {
                return new MPSRandomAccessDemuxer.Stream(streamIndex, ch) { // from class: org.jcodec.containers.mps.index.MTSRandomAccessDemuxer.1.1
                    @Override // org.jcodec.containers.mps.index.MPSRandomAccessDemuxer.Stream
                    protected ByteBuffer fetch(int pesLen) throws IOException {
                        ByteBuffer bb = ByteBuffer.allocate(pesLen * 188);
                        for (int i = 0; i < pesLen; i++) {
                            ByteBuffer tsBuf = NIOUtils.fetchFrom(this.source, 188);
                            Assert.assertEquals(71, tsBuf.get() & 255);
                            int guidFlags = ((tsBuf.get() & 255) << 8) | (tsBuf.get() & 255);
                            int guid = guidFlags & 8191;
                            if (guid == tgtGuid) {
                                int i2 = (guidFlags >> 14) & 1;
                                int b0 = tsBuf.get() & 255;
                                int i3 = b0 & 15;
                                if ((b0 & 32) != 0) {
                                    NIOUtils.skip(tsBuf, tsBuf.get() & 255);
                                }
                                bb.put(tsBuf);
                            }
                        }
                        bb.flip();
                        return bb;
                    }

                    @Override // org.jcodec.containers.mps.index.MPSRandomAccessDemuxer.Stream
                    protected void skip(long leadingSize) throws IOException {
                        this.source.position(this.source.position() + (188 * leadingSize));
                    }

                    @Override // org.jcodec.containers.mps.index.MPSRandomAccessDemuxer.Stream
                    protected void reset() throws IOException {
                        this.source.position(0L);
                    }
                };
            }
        };
    }

    private MPSIndex getProgram(int guid) {
        MTSIndex.MTSProgram[] arr$ = this.programs;
        for (MTSIndex.MTSProgram mtsProgram : arr$) {
            if (mtsProgram.getTargetGuid() == guid) {
                return mtsProgram;
            }
        }
        return null;
    }
}
