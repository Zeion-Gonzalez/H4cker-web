package org.jcodec.codecs.h264.decode.aso;

/* loaded from: classes.dex */
public class PrebuiltMBlockMapper implements Mapper {
    private int firstMBInSlice;
    private int groupId;
    private int indexOfFirstMb;
    private MBToSliceGroupMap map;
    private int picWidthInMbs;

    public PrebuiltMBlockMapper(MBToSliceGroupMap map, int firstMBInSlice, int picWidthInMbs) {
        this.map = map;
        this.firstMBInSlice = firstMBInSlice;
        this.groupId = map.getGroups()[firstMBInSlice];
        this.picWidthInMbs = picWidthInMbs;
        this.indexOfFirstMb = map.getIndices()[firstMBInSlice];
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getAddress(int mbIndex) {
        return this.map.getInverse()[this.groupId][this.indexOfFirstMb + mbIndex];
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean leftAvailable(int mbIndex) {
        int mbAddr = this.map.getInverse()[this.groupId][this.indexOfFirstMb + mbIndex];
        int leftMBAddr = mbAddr - 1;
        return leftMBAddr >= this.firstMBInSlice && mbAddr % this.picWidthInMbs != 0 && this.map.getGroups()[leftMBAddr] == this.groupId;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topAvailable(int mbIndex) {
        int mbAddr = this.map.getInverse()[this.groupId][this.indexOfFirstMb + mbIndex];
        int topMBAddr = mbAddr - this.picWidthInMbs;
        return topMBAddr >= this.firstMBInSlice && this.map.getGroups()[topMBAddr] == this.groupId;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getMbX(int index) {
        return getAddress(index) % this.picWidthInMbs;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getMbY(int index) {
        return getAddress(index) / this.picWidthInMbs;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topRightAvailable(int mbIndex) {
        int mbAddr = this.map.getInverse()[this.groupId][this.indexOfFirstMb + mbIndex];
        int topRMBAddr = (mbAddr - this.picWidthInMbs) + 1;
        return topRMBAddr >= this.firstMBInSlice && (mbAddr + 1) % this.picWidthInMbs != 0 && this.map.getGroups()[topRMBAddr] == this.groupId;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topLeftAvailable(int mbIndex) {
        int mbAddr = this.map.getInverse()[this.groupId][this.indexOfFirstMb + mbIndex];
        int topLMBAddr = (mbAddr - this.picWidthInMbs) - 1;
        return topLMBAddr >= this.firstMBInSlice && mbAddr % this.picWidthInMbs != 0 && this.map.getGroups()[topLMBAddr] == this.groupId;
    }
}
