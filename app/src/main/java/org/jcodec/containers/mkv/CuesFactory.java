package org.jcodec.containers.mkv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlUint;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class CuesFactory {

    /* renamed from: a */
    List<CuePointMock> f1533a = new ArrayList();
    private long currentDataOffset = 0;
    private final long offsetBase;
    private long videoTrackNr;

    public CuesFactory(long offset, long videoTrack) {
        this.offsetBase = offset;
        this.videoTrackNr = videoTrack;
        this.currentDataOffset += this.offsetBase;
    }

    public void addFixedSize(CuePointMock z) {
        z.elementOffset = this.currentDataOffset;
        z.cueClusterPositionSize = 8;
        this.currentDataOffset += z.size;
        this.f1533a.add(z);
    }

    public void add(CuePointMock z) {
        z.elementOffset = this.currentDataOffset;
        z.cueClusterPositionSize = EbmlUint.calculatePayloadSize(z.elementOffset);
        this.currentDataOffset += z.size;
        this.f1533a.add(z);
    }

    public EbmlMaster createCues() {
        int estimatedSize = computeCuesSize();
        EbmlMaster cues = (EbmlMaster) MKVType.createByType(MKVType.Cues);
        for (CuePointMock cpm : this.f1533a) {
            EbmlMaster cuePoint = (EbmlMaster) MKVType.createByType(MKVType.CuePoint);
            EbmlUint cueTime = (EbmlUint) MKVType.createByType(MKVType.CueTime);
            cueTime.set(cpm.timecode);
            cuePoint.add(cueTime);
            EbmlMaster cueTrackPositions = (EbmlMaster) MKVType.createByType(MKVType.CueTrackPositions);
            EbmlUint cueTrack = (EbmlUint) MKVType.createByType(MKVType.CueTrack);
            cueTrack.set(this.videoTrackNr);
            cueTrackPositions.add(cueTrack);
            EbmlUint cueClusterPosition = (EbmlUint) MKVType.createByType(MKVType.CueClusterPosition);
            cueClusterPosition.set(cpm.elementOffset + estimatedSize);
            if (cueClusterPosition.data.limit() != cpm.cueClusterPositionSize) {
                System.err.println("estimated size of CueClusterPosition differs from the one actually used. ElementId: " + EbmlUtil.toHexString(cpm.f1534id) + " " + cueClusterPosition.getData().limit() + " vs " + cpm.cueClusterPositionSize);
            }
            cueTrackPositions.add(cueClusterPosition);
            cuePoint.add(cueTrackPositions);
            cues.add(cuePoint);
        }
        return cues;
    }

    public int computeCuesSize() {
        boolean reindex;
        CuePointMock z;
        int minByteSize;
        int cuesSize = estimateSize();
        do {
            reindex = false;
            Iterator i$ = this.f1533a.iterator();
            do {
                if (i$.hasNext()) {
                    z = i$.next();
                    minByteSize = EbmlUint.calculatePayloadSize(z.elementOffset + cuesSize);
                    if (minByteSize > z.cueClusterPositionSize) {
                        System.err.println("Size " + cuesSize + " seems too small for element " + EbmlUtil.toHexString(z.f1534id) + " increasing size by one.");
                        z.cueClusterPositionSize++;
                        cuesSize++;
                        reindex = true;
                    }
                }
            } while (minByteSize >= z.cueClusterPositionSize);
            throw new RuntimeException("Downsizing the index is not well thought through");
        } while (reindex);
        return cuesSize;
    }

    public int estimateFixedSize(int numberOfClusters) {
        int s = numberOfClusters * 34;
        return s + MKVType.Cues.f1537id.length + EbmlUtil.ebmlLength(s);
    }

    public int estimateSize() {
        int s = 0;
        for (CuePointMock cpm : this.f1533a) {
            s += estimateCuePointSize(EbmlUint.calculatePayloadSize(cpm.timecode), EbmlUint.calculatePayloadSize(this.videoTrackNr), EbmlUint.calculatePayloadSize(cpm.elementOffset));
        }
        return s + MKVType.Cues.f1537id.length + EbmlUtil.ebmlLength(s);
    }

    public static int estimateCuePointSize(int timecodeSizeInBytes, int trackNrSizeInBytes, int clusterPositionSizeInBytes) {
        int cueTimeSize = MKVType.CueTime.f1537id.length + EbmlUtil.ebmlLength(timecodeSizeInBytes) + timecodeSizeInBytes;
        int cueTrackPositionSize = MKVType.CueTrack.f1537id.length + EbmlUtil.ebmlLength(trackNrSizeInBytes) + trackNrSizeInBytes + MKVType.CueClusterPosition.f1537id.length + EbmlUtil.ebmlLength(clusterPositionSizeInBytes) + clusterPositionSizeInBytes;
        int cuePointSize = MKVType.CuePoint.f1537id.length + EbmlUtil.ebmlLength(cueTimeSize + r2) + cueTimeSize + cueTrackPositionSize + MKVType.CueTrackPositions.f1537id.length + EbmlUtil.ebmlLength(cueTrackPositionSize);
        return cuePointSize;
    }

    /* loaded from: classes.dex */
    public static class CuePointMock {
        public int cueClusterPositionSize;
        public long elementOffset;

        /* renamed from: id */
        private byte[] f1534id;
        private long size;
        private long timecode;

        public static CuePointMock make(EbmlMaster c) {
            EbmlUint tc = (EbmlUint) MKVType.findFirst(c, MKVType.Cluster, MKVType.Timecode);
            return make(c.f1540id, tc.get(), c.size());
        }

        public static CuePointMock make(byte[] id, long timecode, long size) {
            CuePointMock mock = new CuePointMock();
            mock.f1534id = id;
            mock.timecode = timecode;
            mock.size = size;
            return mock;
        }
    }
}
