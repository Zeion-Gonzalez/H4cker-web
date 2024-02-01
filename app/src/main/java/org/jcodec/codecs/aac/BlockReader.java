package org.jcodec.codecs.aac;

import org.jcodec.codecs.aac.blocks.Block;
import org.jcodec.common.io.BitReader;

/* loaded from: classes.dex */
public class BlockReader {
    public Block nextBlock(BitReader bits) {
        BlockType type = BlockType.fromCode(bits.readNBit(3));
        if (type != BlockType.TYPE_END) {
            bits.readNBit(4);
        }
        return null;
    }
}
