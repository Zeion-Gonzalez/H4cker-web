package org.jcodec.containers.mps.index;

import java.nio.ByteBuffer;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.RunLength;
import org.jcodec.containers.mps.index.MPSIndex;

/* loaded from: classes.dex */
public class MTSIndex {
    private MTSProgram[] programs;

    /* loaded from: classes.dex */
    public static class MTSProgram extends MPSIndex {
        private int targetGuid;

        public MTSProgram(long[] pesTokens, RunLength.Integer pesStreamIds, MPSIndex.MPSStreamIndex[] streams, int targetGuid) {
            super(pesTokens, pesStreamIds, streams);
            this.targetGuid = targetGuid;
        }

        public MTSProgram(MPSIndex mpsIndex, int target) {
            super(mpsIndex);
            this.targetGuid = target;
        }

        public int getTargetGuid() {
            return this.targetGuid;
        }

        @Override // org.jcodec.containers.mps.index.MPSIndex
        public void serializeTo(ByteBuffer index) {
            index.putInt(this.targetGuid);
            super.serializeTo(index);
        }

        public static MTSProgram parse(ByteBuffer read) {
            int targetGuid = read.getInt();
            return new MTSProgram(MPSIndex.parseIndex(read), targetGuid);
        }
    }

    public MTSIndex(MTSProgram[] programs) {
        this.programs = programs;
    }

    public MTSProgram[] getPrograms() {
        return this.programs;
    }

    public static MTSIndex parse(ByteBuffer buf) {
        int numPrograms = buf.getInt();
        MTSProgram[] programs = new MTSProgram[numPrograms];
        for (int i = 0; i < numPrograms; i++) {
            int programDataSize = buf.getInt();
            programs[i] = MTSProgram.parse(NIOUtils.read(buf, programDataSize));
        }
        return new MTSIndex(programs);
    }

    public int estimateSize() {
        int totalSize = 64;
        MTSProgram[] arr$ = this.programs;
        for (MTSProgram mtsProgram : arr$) {
            totalSize += mtsProgram.estimateSize() + 4;
        }
        return totalSize;
    }

    public void serializeTo(ByteBuffer buf) {
        buf.putInt(this.programs.length);
        MTSProgram[] arr$ = this.programs;
        for (MTSProgram mtsAnalyser : arr$) {
            ByteBuffer dup = buf.duplicate();
            NIOUtils.skip(buf, 4);
            mtsAnalyser.serializeTo(buf);
            dup.putInt((buf.position() - dup.position()) - 4);
        }
    }

    public ByteBuffer serialize() {
        ByteBuffer bb = ByteBuffer.allocate(estimateSize());
        serializeTo(bb);
        bb.flip();
        return bb;
    }
}
