package org.jcodec.codecs.h264.decode.aso;

/* loaded from: classes.dex */
public class FlatMBlockMapper implements Mapper {
    private int firstMBAddr;
    private int frameWidthInMbs;

    public FlatMBlockMapper(int frameWidthInMbs, int firstMBAddr) {
        this.frameWidthInMbs = frameWidthInMbs;
        this.firstMBAddr = firstMBAddr;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean leftAvailable(int index) {
        int mbAddr = index + this.firstMBAddr;
        boolean atTheBorder = mbAddr % this.frameWidthInMbs == 0;
        return !atTheBorder && mbAddr > this.firstMBAddr;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topAvailable(int index) {
        int mbAddr = index + this.firstMBAddr;
        return mbAddr - this.frameWidthInMbs >= this.firstMBAddr;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getAddress(int index) {
        return this.firstMBAddr + index;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getMbX(int index) {
        return getAddress(index) % this.frameWidthInMbs;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public int getMbY(int index) {
        return getAddress(index) / this.frameWidthInMbs;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topRightAvailable(int index) {
        int mbAddr = index + this.firstMBAddr;
        boolean atTheBorder = (mbAddr + 1) % this.frameWidthInMbs == 0;
        return !atTheBorder && (mbAddr - this.frameWidthInMbs) + 1 >= this.firstMBAddr;
    }

    @Override // org.jcodec.codecs.h264.decode.aso.Mapper
    public boolean topLeftAvailable(int index) {
        int mbAddr = index + this.firstMBAddr;
        boolean atTheBorder = mbAddr % this.frameWidthInMbs == 0;
        return !atTheBorder && (mbAddr - this.frameWidthInMbs) + (-1) >= this.firstMBAddr;
    }
}
