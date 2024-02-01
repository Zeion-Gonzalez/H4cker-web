package org.jcodec.codecs.common.biari;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class MQDecoder {
    private int availableBits;
    private int decodedBytes;

    /* renamed from: is */
    private InputStream f1446is;
    private int lastByte;
    private int range = 32768;
    private int value = 0;

    public MQDecoder(InputStream is) throws IOException {
        this.f1446is = is;
        fetchByte();
        this.value <<= 8;
        fetchByte();
        this.value <<= this.availableBits - 1;
        this.availableBits = 1;
    }

    public int decode(Context cm) throws IOException {
        int rangeLps = MQConst.pLps[cm.getState()];
        if (this.value > rangeLps) {
            this.range -= rangeLps;
            this.value -= rangeLps;
            if (this.range < 32768) {
                while (this.range < 32768) {
                    renormalize();
                }
                cm.setState(MQConst.transitMPS[cm.getState()]);
            }
            int decoded = cm.getMps();
            return decoded;
        }
        this.range = rangeLps;
        while (this.range < 32768) {
            renormalize();
        }
        if (MQConst.mpsSwitch[cm.getState()] != 0) {
            cm.setMps(1 - cm.getMps());
        }
        cm.setState(MQConst.transitLPS[cm.getState()]);
        int decoded2 = 1 - cm.getMps();
        return decoded2;
    }

    private void fetchByte() throws IOException {
        this.availableBits = 8;
        if (this.decodedBytes > 0 && this.lastByte == 255) {
            this.availableBits = 7;
        }
        this.lastByte = this.f1446is.read();
        int shiftCarry = 8 - this.availableBits;
        this.value += this.lastByte << shiftCarry;
        this.decodedBytes++;
    }

    private void renormalize() throws IOException {
        this.value <<= 1;
        this.range <<= 1;
        this.range &= SupportMenu.USER_MASK;
        this.availableBits--;
        if (this.availableBits == 0) {
            fetchByte();
        }
    }
}
