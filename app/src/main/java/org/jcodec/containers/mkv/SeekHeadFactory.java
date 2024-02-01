package org.jcodec.containers.mkv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlBin;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlUint;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class SeekHeadFactory {

    /* renamed from: a */
    List<SeekMock> f1538a = new ArrayList();
    long currentDataOffset = 0;

    public void add(EbmlBase e) {
        SeekMock z = SeekMock.make(e);
        z.dataOffset = this.currentDataOffset;
        z.seekPointerSize = EbmlUint.calculatePayloadSize(z.dataOffset);
        this.currentDataOffset += z.size;
        this.f1538a.add(z);
    }

    public EbmlMaster indexSeekHead() {
        int seekHeadSize = computeSeekHeadSize();
        EbmlMaster seekHead = (EbmlMaster) MKVType.createByType(MKVType.SeekHead);
        for (SeekMock z : this.f1538a) {
            EbmlMaster seek = (EbmlMaster) MKVType.createByType(MKVType.Seek);
            EbmlBin seekId = (EbmlBin) MKVType.createByType(MKVType.SeekID);
            seekId.set(ByteBuffer.wrap(z.f1539id));
            seek.add(seekId);
            EbmlUint seekPosition = (EbmlUint) MKVType.createByType(MKVType.SeekPosition);
            seekPosition.set(z.dataOffset + seekHeadSize);
            if (seekPosition.data.limit() != z.seekPointerSize) {
                System.err.println("estimated size of seekPosition differs from the one actually used. ElementId: " + EbmlUtil.toHexString(z.f1539id) + " " + seekPosition.getData().limit() + " vs " + z.seekPointerSize);
            }
            seek.add(seekPosition);
            seekHead.add(seek);
        }
        ByteBuffer mux = seekHead.getData();
        if (mux.limit() != seekHeadSize) {
            System.err.println("estimated size of seekHead differs from the one actually used. " + mux.limit() + " vs " + seekHeadSize);
        }
        return seekHead;
    }

    public int computeSeekHeadSize() {
        boolean reindex;
        SeekMock z;
        int minSize;
        int seekHeadSize = estimateSize();
        do {
            reindex = false;
            Iterator i$ = this.f1538a.iterator();
            do {
                if (i$.hasNext()) {
                    z = i$.next();
                    minSize = EbmlUint.calculatePayloadSize(z.dataOffset + seekHeadSize);
                    if (minSize > z.seekPointerSize) {
                        System.out.println("Size " + seekHeadSize + " seems too small for element " + EbmlUtil.toHexString(z.f1539id) + " increasing size by one.");
                        z.seekPointerSize++;
                        seekHeadSize++;
                        reindex = true;
                    }
                }
            } while (minSize >= z.seekPointerSize);
            throw new RuntimeException("Downsizing the index is not well thought through.");
        } while (reindex);
        return seekHeadSize;
    }

    int estimateSize() {
        int s = MKVType.SeekHead.f1537id.length + 1;
        int s2 = s + estimeteSeekSize(this.f1538a.get(0).f1539id.length, 1);
        for (int i = 1; i < this.f1538a.size(); i++) {
            s2 += estimeteSeekSize(this.f1538a.get(i).f1539id.length, this.f1538a.get(i).seekPointerSize);
        }
        return s2;
    }

    public static int estimeteSeekSize(int idLength, int offsetSizeInBytes) {
        int seekIdSize = MKVType.SeekID.f1537id.length + EbmlUtil.ebmlLength(idLength) + idLength;
        int seekPositionSize = MKVType.SeekPosition.f1537id.length + EbmlUtil.ebmlLength(offsetSizeInBytes) + offsetSizeInBytes;
        int seekSize = MKVType.Seek.f1537id.length + EbmlUtil.ebmlLength(seekIdSize + seekPositionSize) + seekIdSize + seekPositionSize;
        return seekSize;
    }

    /* loaded from: classes.dex */
    public static class SeekMock {
        public long dataOffset;

        /* renamed from: id */
        byte[] f1539id;
        int seekPointerSize;
        int size;

        public static SeekMock make(EbmlBase e) {
            SeekMock z = new SeekMock();
            z.f1539id = e.f1540id;
            z.size = (int) e.size();
            return z;
        }
    }
}
