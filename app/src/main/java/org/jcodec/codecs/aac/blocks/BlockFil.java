package org.jcodec.codecs.aac.blocks;

import org.jcodec.common.io.BitReader;

/* loaded from: classes.dex */
public class BlockFil extends Block {
    @Override // org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        int num = in.readNBit(4);
        if (num == 15) {
            num += in.readNBit(8) - 1;
        }
        if (num > 0 && in.skip(num * 8) != num * 8) {
            throw new RuntimeException("Overread");
        }
    }
}
