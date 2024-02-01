package org.jcodec.codecs.aac.blocks;

import org.jcodec.codecs.aac.BlockType;
import org.jcodec.codecs.aac.blocks.BlockICS;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.VLC;

/* loaded from: classes.dex */
public class BlockCCE extends Block {
    static VLC vlc = new VLC(AACTab.ff_aac_scalefactor_code, AACTab.ff_aac_scalefactor_bits);
    private BlockICS.BandType[] bandType;
    private BlockICS blockICS;
    private Object[] cce_scale;
    private int[] ch_select;
    private int coupling_point;
    private int[] id_select;
    private int num_coupled;
    private Object scale;
    private int sign;
    private BlockType[] type;

    /* loaded from: classes.dex */
    enum CouplingPoint {
        BEFORE_TNS,
        BETWEEN_TNS_AND_IMDCT,
        UNDEF,
        AFTER_IMDCT
    }

    public BlockCCE(BlockICS.BandType[] bandType) {
        this.bandType = bandType;
    }

    @Override // org.jcodec.codecs.aac.blocks.Block
    public void parse(BitReader in) {
        int num_gain = 0;
        this.coupling_point = in.read1Bit() * 2;
        this.num_coupled = in.readNBit(3);
        for (int c = 0; c <= this.num_coupled; c++) {
            num_gain++;
            this.type[c] = in.read1Bit() != 0 ? BlockType.TYPE_CPE : BlockType.TYPE_SCE;
            this.id_select[c] = in.readNBit(4);
            if (this.type[c] == BlockType.TYPE_CPE) {
                this.ch_select[c] = in.readNBit(2);
                if (this.ch_select[c] == 3) {
                    num_gain++;
                }
            } else {
                this.ch_select[c] = 2;
            }
        }
        this.coupling_point += in.read1Bit() | (this.coupling_point >> 1);
        this.sign = in.read1Bit();
        this.scale = this.cce_scale[in.readNBit(2)];
        this.blockICS = new BlockICS();
        this.blockICS.parse(in);
        for (int c2 = 0; c2 < num_gain; c2++) {
            int idx = 0;
            int cge = 1;
            if (c2 != 0) {
                cge = this.coupling_point == CouplingPoint.AFTER_IMDCT.ordinal() ? 1 : in.read1Bit();
                if (cge != 0) {
                    int gain = vlc.readVLC(in) - 60;
                }
            }
            if (this.coupling_point != CouplingPoint.AFTER_IMDCT.ordinal()) {
                for (int g = 0; g < this.blockICS.num_window_groups; g++) {
                    int sfb = 0;
                    while (sfb < this.blockICS.maxSfb) {
                        if (this.bandType[idx] != BlockICS.BandType.ZERO_BT && cge == 0) {
                            int readVLC = vlc.readVLC(in) - 60;
                        }
                        sfb++;
                        idx++;
                    }
                }
            }
        }
    }
}
