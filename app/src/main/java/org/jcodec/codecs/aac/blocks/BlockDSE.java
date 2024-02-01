package org.jcodec.codecs.aac.blocks;

import org.jcodec.common.io.BitReader;

/* loaded from: classes.dex */
public class BlockDSE extends Block {
    @Override // org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        in.readNBit(4);
        int byte_align = in.read1Bit();
        int count = in.readNBit(8);
        if (count == 255) {
            count += in.readNBit(8);
        }
        if (byte_align != 0) {
            in.align();
        }
        if (in.skip(count * 8) != count * 8) {
            throw new RuntimeException("Overread");
        }
    }
}
