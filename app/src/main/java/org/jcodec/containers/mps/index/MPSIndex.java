package org.jcodec.containers.mps.index;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import java.nio.ByteBuffer;
import org.jcodec.common.RunLength;

/* loaded from: classes.dex */
public class MPSIndex {
    private RunLength.Integer pesStreamIds;
    private long[] pesTokens;
    private MPSStreamIndex[] streams;

    /* loaded from: classes.dex */
    public static class MPSStreamIndex {
        protected int[] fdur;
        protected int[] fpts;
        protected int[] fsizes;
        protected int streamId;
        protected int[] sync;

        public MPSStreamIndex(int streamId, int[] fsizes, int[] fpts, int[] fdur, int[] sync) {
            this.streamId = streamId;
            this.fsizes = fsizes;
            this.fpts = fpts;
            this.fdur = fdur;
            this.sync = sync;
        }

        public MPSStreamIndex(MPSStreamIndex other) {
            this(other.streamId, other.fsizes, other.fpts, other.fdur, other.sync);
        }

        public int getStreamId() {
            return this.streamId;
        }

        public int[] getFsizes() {
            return this.fsizes;
        }

        public int[] getFpts() {
            return this.fpts;
        }

        public int[] getFdur() {
            return this.fdur;
        }

        public int[] getSync() {
            return this.sync;
        }

        public static MPSStreamIndex parseIndex(ByteBuffer index) {
            int streamId = index.get() & 255;
            int fCnt = index.getInt();
            int[] fsizes = new int[fCnt];
            for (int i = 0; i < fCnt; i++) {
                fsizes[i] = index.getInt();
            }
            int fptsCnt = index.getInt();
            int[] fpts = new int[fptsCnt];
            for (int i2 = 0; i2 < fptsCnt; i2++) {
                fpts[i2] = index.getInt();
            }
            int fdurCnt = index.getInt();
            int[] fdur = new int[fdurCnt];
            for (int i3 = 0; i3 < fdurCnt; i3++) {
                fdur[i3] = index.getInt();
            }
            int syncCount = index.getInt();
            int[] sync = new int[syncCount];
            for (int i4 = 0; i4 < syncCount; i4++) {
                sync[i4] = index.getInt();
            }
            return new MPSStreamIndex(streamId, fsizes, fpts, fdur, sync);
        }

        public void serialize(ByteBuffer index) {
            index.put((byte) this.streamId);
            index.putInt(this.fsizes.length);
            for (int i = 0; i < this.fsizes.length; i++) {
                index.putInt(this.fsizes[i]);
            }
            index.putInt(this.fpts.length);
            for (int i2 = 0; i2 < this.fpts.length; i2++) {
                index.putInt(this.fpts[i2]);
            }
            index.putInt(this.fdur.length);
            for (int i3 = 0; i3 < this.fdur.length; i3++) {
                index.putInt(this.fdur[i3]);
            }
            index.putInt(this.sync.length);
            for (int i4 = 0; i4 < this.sync.length; i4++) {
                index.putInt(this.sync[i4]);
            }
        }

        public int estimateSize() {
            return (this.fpts.length << 2) + (this.fdur.length << 2) + (this.sync.length << 2) + (this.fsizes.length << 2) + 64;
        }
    }

    public MPSIndex(long[] pesTokens, RunLength.Integer pesStreamIds, MPSStreamIndex[] streams) {
        this.pesTokens = pesTokens;
        this.pesStreamIds = pesStreamIds;
        this.streams = streams;
    }

    public MPSIndex(MPSIndex mpsIndex) {
        this(mpsIndex.pesTokens, mpsIndex.pesStreamIds, mpsIndex.streams);
    }

    public long[] getPesTokens() {
        return this.pesTokens;
    }

    public RunLength.Integer getPesStreamIds() {
        return this.pesStreamIds;
    }

    public MPSStreamIndex[] getStreams() {
        return this.streams;
    }

    public static MPSIndex parseIndex(ByteBuffer index) {
        int pesCnt = index.getInt();
        long[] pesTokens = new long[pesCnt];
        for (int i = 0; i < pesCnt; i++) {
            pesTokens[i] = index.getLong();
        }
        RunLength.Integer pesStreamId = RunLength.Integer.parse(index);
        int nStreams = index.getInt();
        MPSStreamIndex[] streams = new MPSStreamIndex[nStreams];
        for (int i2 = 0; i2 < nStreams; i2++) {
            streams[i2] = MPSStreamIndex.parseIndex(index);
        }
        return new MPSIndex(pesTokens, pesStreamId, streams);
    }

    public void serializeTo(ByteBuffer index) {
        index.putInt(this.pesTokens.length);
        for (int i = 0; i < this.pesTokens.length; i++) {
            index.putLong(this.pesTokens[i]);
        }
        this.pesStreamIds.serialize(index);
        index.putInt(this.streams.length);
        MPSStreamIndex[] arr$ = this.streams;
        for (MPSStreamIndex mpsStreamIndex : arr$) {
            mpsStreamIndex.serialize(index);
        }
    }

    public int estimateSize() {
        int size = (this.pesTokens.length << 3) + this.pesStreamIds.estimateSize();
        MPSStreamIndex[] arr$ = this.streams;
        for (MPSStreamIndex mpsStreamIndex : arr$) {
            size += mpsStreamIndex.estimateSize();
        }
        return size + 64;
    }

    public static long makePESToken(long leading, long pesLen, long payloadLen) {
        return (leading << 48) | (pesLen << 24) | payloadLen;
    }

    public static int leadingSize(long token) {
        return ((int) (token >> 48)) & SupportMenu.USER_MASK;
    }

    public static int pesLen(long token) {
        return ((int) (token >> 24)) & ViewCompat.MEASURED_SIZE_MASK;
    }

    public static int payLoadSize(long token) {
        return ((int) token) & ViewCompat.MEASURED_SIZE_MASK;
    }
}
