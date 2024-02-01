package org.jcodec.common.io;

import org.jcodec.common.IntArrayList;
import org.jcodec.common.IntIntMap;

/* loaded from: classes.dex */
public class VLCBuilder {
    private IntIntMap forward = new IntIntMap();
    private IntIntMap inverse = new IntIntMap();
    private IntArrayList codes = new IntArrayList();
    private IntArrayList codesSizes = new IntArrayList();

    public VLCBuilder() {
    }

    public VLCBuilder(int[] codes, int[] lens, int[] vals) {
        for (int i = 0; i < codes.length; i++) {
            set(codes[i], lens[i], vals[i]);
        }
    }

    public VLCBuilder set(int val, String code) {
        set(Integer.parseInt(code, 2), code.length(), val);
        return this;
    }

    public VLCBuilder set(int code, int len, int val) {
        this.codes.add(code << (32 - len));
        this.codesSizes.add(len);
        this.forward.put(val, this.codes.size() - 1);
        this.inverse.put(this.codes.size() - 1, val);
        return this;
    }

    public VLC getVLC() {
        return new VLC(this.codes.toArray(), this.codesSizes.toArray()) { // from class: org.jcodec.common.io.VLCBuilder.1
            @Override // org.jcodec.common.io.VLC
            public int readVLC(BitReader in) {
                return VLCBuilder.this.inverse.get(super.readVLC(in));
            }

            @Override // org.jcodec.common.io.VLC
            public int readVLC16(BitReader in) {
                return VLCBuilder.this.inverse.get(super.readVLC16(in));
            }

            @Override // org.jcodec.common.io.VLC
            public void writeVLC(BitWriter out, int code) {
                super.writeVLC(out, VLCBuilder.this.forward.get(code));
            }
        };
    }
}
