package org.jcodec.codecs.aac.blocks;

import org.jcodec.codecs.aac.ChannelPosition;
import org.jcodec.common.io.BitReader;

/* loaded from: classes.dex */
public class BlockPCE extends Block {
    private static final int MAX_ELEM_ID = 16;

    /* loaded from: classes.dex */
    public static class ChannelMapping {
        ChannelPosition position;
        int someInt;
        RawDataBlockType syn_ele;
    }

    @Override // org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        in.readNBit(2);
        in.readNBit(4);
        int num_front = in.readNBit(4);
        int num_side = in.readNBit(4);
        int num_back = in.readNBit(4);
        int num_lfe = in.readNBit(2);
        int num_assoc_data = in.readNBit(3);
        int num_cc = in.readNBit(4);
        if (in.read1Bit() != 0) {
            in.readNBit(4);
        }
        if (in.read1Bit() != 0) {
            in.readNBit(4);
        }
        if (in.read1Bit() != 0) {
            in.readNBit(3);
        }
        ChannelMapping[] layout_map = new ChannelMapping[64];
        decodeChannelMap(layout_map, 0, ChannelPosition.AAC_CHANNEL_FRONT, in, num_front);
        decodeChannelMap(layout_map, num_front, ChannelPosition.AAC_CHANNEL_SIDE, in, num_side);
        int tags = num_front + num_side;
        decodeChannelMap(layout_map, tags, ChannelPosition.AAC_CHANNEL_BACK, in, num_back);
        int tags2 = tags + num_back;
        decodeChannelMap(layout_map, tags2, ChannelPosition.AAC_CHANNEL_LFE, in, num_lfe);
        int tags3 = tags2 + num_lfe;
        in.skip(num_assoc_data * 4);
        decodeChannelMap(layout_map, tags3, ChannelPosition.AAC_CHANNEL_CC, in, num_cc);
        int i = tags3 + num_cc;
        in.align();
        int comment_len = in.readNBit(8) * 8;
        in.skip(comment_len);
    }

    private void decodeChannelMap(ChannelMapping[] layout_map, int offset, ChannelPosition type, BitReader in, int n) {
        while (true) {
            int n2 = n;
            n = n2 - 1;
            if (n2 > 0) {
                RawDataBlockType syn_ele = null;
                switch (type) {
                    case AAC_CHANNEL_FRONT:
                    case AAC_CHANNEL_BACK:
                    case AAC_CHANNEL_SIDE:
                        syn_ele = RawDataBlockType.fromOrdinal(in.read1Bit());
                        break;
                    case AAC_CHANNEL_CC:
                        in.read1Bit();
                        syn_ele = RawDataBlockType.TYPE_CCE;
                        break;
                    case AAC_CHANNEL_LFE:
                        syn_ele = RawDataBlockType.TYPE_LFE;
                        break;
                }
                layout_map[offset].syn_ele = syn_ele;
                layout_map[offset].someInt = in.readNBit(4);
                layout_map[offset].position = type;
                offset++;
            } else {
                return;
            }
        }
    }
}
