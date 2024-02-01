package org.jcodec.codecs.common.biari;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class MQEncoder {
    public static final int CARRY_MASK = 134217728;
    private int byteToGo;
    private long bytesOutput;
    private OutputStream out;
    private int range = 32768;
    private int offset = 0;
    private int bitsToCode = 12;

    public MQEncoder(OutputStream out) {
        this.out = out;
    }

    public void encode(int symbol, Context cm) throws IOException {
        int rangeLps = MQConst.pLps[cm.getState()];
        if (symbol == cm.getMps()) {
            this.range -= rangeLps;
            this.offset += rangeLps;
            if (this.range < 32768) {
                while (this.range < 32768) {
                    renormalize();
                }
                cm.setState(MQConst.transitMPS[cm.getState()]);
                return;
            }
            return;
        }
        this.range = rangeLps;
        while (this.range < 32768) {
            renormalize();
        }
        if (MQConst.mpsSwitch[cm.getState()] != 0) {
            cm.setMps(1 - cm.getMps());
        }
        cm.setState(MQConst.transitLPS[cm.getState()]);
    }

    public void finish() throws IOException {
        finalizeValue();
        this.offset <<= this.bitsToCode;
        int bitsToOutput = 12 - this.bitsToCode;
        outputByte();
        if (bitsToOutput - this.bitsToCode > 0) {
            this.offset <<= this.bitsToCode;
            outputByte();
        }
        this.out.write(this.byteToGo);
    }

    private void finalizeValue() {
        int halfBit = this.offset & 32768;
        this.offset &= SupportMenu.CATEGORY_MASK;
        if (halfBit == 0) {
            this.offset |= 32768;
        } else {
            this.offset += 65536;
        }
    }

    private void renormalize() throws IOException {
        this.offset <<= 1;
        this.range <<= 1;
        this.range = (int) (this.range & 65535);
        this.bitsToCode--;
        if (this.bitsToCode == 0) {
            outputByte();
        }
    }

    private void outputByte() throws IOException {
        if (this.bytesOutput == 0) {
            outputByteNoStuffing();
            return;
        }
        if (this.byteToGo == 255) {
            outputByteWithStuffing();
            return;
        }
        if ((this.offset & CARRY_MASK) != 0) {
            this.byteToGo++;
            this.offset &= 134217727;
            if (this.byteToGo == 255) {
                outputByteWithStuffing();
                return;
            } else {
                outputByteNoStuffing();
                return;
            }
        }
        outputByteNoStuffing();
    }

    private void outputByteWithStuffing() throws IOException {
        this.bitsToCode = 7;
        if (this.bytesOutput > 0) {
            this.out.write(this.byteToGo);
        }
        this.byteToGo = (this.offset >> 20) & 255;
        this.offset &= 1048575;
        this.bytesOutput++;
    }

    private void outputByteNoStuffing() throws IOException {
        this.bitsToCode = 8;
        if (this.bytesOutput > 0) {
            this.out.write(this.byteToGo);
        }
        this.byteToGo = (this.offset >> 19) & 255;
        this.offset &= 524287;
        this.bytesOutput++;
    }
}
