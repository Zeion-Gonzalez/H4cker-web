package org.jcodec.codecs.common.biari;

import android.support.v4.view.ViewCompat;
import java.nio.ByteBuffer;
import org.jcodec.codecs.vpx.VPXConst;

/* loaded from: classes.dex */
public class VPxBooleanEncoder {
    private ByteBuffer out;
    private int lowvalue = 0;
    private int range = 255;
    private int count = -24;

    public VPxBooleanEncoder(ByteBuffer out) {
        this.out = out;
    }

    public void writeBit(int prob, int bb) {
        int split = (((this.range - 1) * prob) >> 8) + 1;
        if (bb != 0) {
            this.lowvalue += split;
            this.range -= split;
        } else {
            this.range = split;
        }
        int shift = VPXConst.vp8Norm[this.range];
        this.range <<= shift;
        this.count += shift;
        if (this.count >= 0) {
            int offset = shift - this.count;
            if (((this.lowvalue << (offset - 1)) & Integer.MIN_VALUE) != 0) {
                int x = this.out.position() - 1;
                while (x >= 0 && this.out.get(x) == -1) {
                    this.out.put(x, (byte) 0);
                    x--;
                }
                this.out.put(x, (byte) ((this.out.get(x) & 255) + 1));
            }
            this.out.put((byte) (this.lowvalue >> (24 - offset)));
            this.lowvalue <<= offset;
            shift = this.count;
            this.lowvalue &= ViewCompat.MEASURED_SIZE_MASK;
            this.count -= 8;
        }
        this.lowvalue <<= shift;
    }

    public void stop() {
        for (int i = 0; i < 32; i++) {
            writeBit(128, 0);
        }
    }

    public int position() {
        return this.out.position() + ((this.count + 24) >> 3);
    }
}
