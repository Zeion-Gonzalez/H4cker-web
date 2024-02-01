package org.jcodec.movtool.streaming.tracks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jcodec.movtool.streaming.CodecMeta;
import org.jcodec.movtool.streaming.VirtualPacket;
import org.jcodec.movtool.streaming.VirtualTrack;

/* loaded from: classes.dex */
public class CachingTrack implements VirtualTrack {
    private List<CachingPacket> cachedPackets = Collections.synchronizedList(new ArrayList());
    private ScheduledFuture<?> policyFuture;
    private VirtualTrack src;

    public CachingTrack(VirtualTrack src, final int policy, ScheduledExecutorService policyExecutor) {
        if (policy < 1) {
            throw new IllegalArgumentException("Caching track with less then 1 entry.");
        }
        this.src = src;
        this.policyFuture = policyExecutor.scheduleAtFixedRate(new Runnable() { // from class: org.jcodec.movtool.streaming.tracks.CachingTrack.1
            @Override // java.lang.Runnable
            public void run() {
                while (CachingTrack.this.cachedPackets.size() > policy) {
                    ((CachingPacket) CachingTrack.this.cachedPackets.get(0)).wipe();
                }
            }
        }, 200L, 200L, TimeUnit.MILLISECONDS);
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public CodecMeta getCodecMeta() {
        return this.src.getCodecMeta();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualPacket nextPacket() throws IOException {
        VirtualPacket pkt = this.src.nextPacket();
        if (pkt == null) {
            return null;
        }
        return new CachingPacket(pkt);
    }

    /* loaded from: classes.dex */
    public class CachingPacket extends VirtualPacketWrapper {
        private ByteBuffer cache;

        public CachingPacket(VirtualPacket src) {
            super(src);
        }

        public synchronized void wipe() {
            if (CachingTrack.this.cachedPackets.indexOf(this) == 0) {
                CachingTrack.this.cachedPackets.remove(0);
                this.cache = null;
            }
        }

        @Override // org.jcodec.movtool.streaming.tracks.VirtualPacketWrapper, org.jcodec.movtool.streaming.VirtualPacket
        public synchronized ByteBuffer getData() throws IOException {
            CachingTrack.this.cachedPackets.remove(this);
            if (this.cache == null) {
                this.cache = this.src.getData();
            }
            CachingTrack.this.cachedPackets.add(this);
            return this.cache == null ? null : this.cache.duplicate();
        }
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public void close() throws IOException {
        if (this.policyFuture != null) {
            this.policyFuture.cancel(false);
        }
        this.cachedPackets.clear();
        this.src.close();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public VirtualTrack.VirtualEdit[] getEdits() {
        return this.src.getEdits();
    }

    @Override // org.jcodec.movtool.streaming.VirtualTrack
    public int getPreferredTimescale() {
        return this.src.getPreferredTimescale();
    }
}
