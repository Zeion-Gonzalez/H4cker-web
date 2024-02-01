package org.jcodec.codecs.aac.blocks;

import org.jcodec.common.io.BitReader;

/* loaded from: classes.dex */
public class BlockCPE extends BlockICS {
    private int[] ms_mask;

    @Override // org.jcodec.codecs.aac.blocks.BlockICS, org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        int common_window = in.read1Bit();
        if (common_window != 0) {
            parseICSInfo(in);
            int ms_present = in.readNBit(2);
            if (ms_present == 3) {
                throw new RuntimeException("ms_present = 3 is reserved.");
            }
            if (ms_present != 0) {
                decodeMidSideStereo(in, ms_present, 0, 0);
            }
        }
        BlockICS ics1 = new BlockICS();
        ics1.parse(in);
        BlockICS ics2 = new BlockICS();
        ics2.parse(in);
    }

    private void decodeMidSideStereo(BitReader in, int ms_present, int numWindowGroups, int maxSfb) {
        if (ms_present == 1) {
            for (int idx = 0; idx < numWindowGroups * maxSfb; idx++) {
                this.ms_mask[idx] = in.read1Bit();
            }
        }
    }
}
