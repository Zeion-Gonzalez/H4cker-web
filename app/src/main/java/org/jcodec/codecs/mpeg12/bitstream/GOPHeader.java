package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.TapeTimecode;

/* loaded from: classes.dex */
public class GOPHeader implements MPEGHeader {
    private boolean brokenLink;
    private boolean closedGop;
    private TapeTimecode timeCode;

    public GOPHeader(TapeTimecode timeCode, boolean closedGop, boolean brokenLink) {
        this.timeCode = timeCode;
        this.closedGop = closedGop;
        this.brokenLink = brokenLink;
    }

    public static GOPHeader read(ByteBuffer bb) {
        BitReader in = new BitReader(bb);
        boolean dropFrame = in.read1Bit() == 1;
        short hours = (short) in.readNBit(5);
        byte minutes = (byte) in.readNBit(6);
        in.skip(1);
        byte seconds = (byte) in.readNBit(6);
        byte frames = (byte) in.readNBit(6);
        boolean closedGop = in.read1Bit() == 1;
        boolean brokenLink = in.read1Bit() == 1;
        return new GOPHeader(new TapeTimecode(hours, minutes, seconds, frames, dropFrame), closedGop, brokenLink);
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer bb) {
        BitWriter bw = new BitWriter(bb);
        if (this.timeCode == null) {
            bw.writeNBit(0, 25);
        } else {
            bw.write1Bit(this.timeCode.isDropFrame() ? 1 : 0);
            bw.writeNBit(this.timeCode.getHour(), 5);
            bw.writeNBit(this.timeCode.getMinute(), 6);
            bw.write1Bit(1);
            bw.writeNBit(this.timeCode.getSecond(), 6);
            bw.writeNBit(this.timeCode.getFrame(), 6);
        }
        bw.write1Bit(this.closedGop ? 1 : 0);
        bw.write1Bit(this.brokenLink ? 1 : 0);
        bw.flush();
    }

    public TapeTimecode getTimeCode() {
        return this.timeCode;
    }

    public boolean isClosedGop() {
        return this.closedGop;
    }

    public boolean isBrokenLink() {
        return this.brokenLink;
    }
}
