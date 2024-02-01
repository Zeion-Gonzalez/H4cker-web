package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;

/* loaded from: classes.dex */
public class ColorExtension extends Box {
    private short matrixIndex;
    private short primariesIndex;
    private short transferFunctionIndex;
    private final String type;

    public ColorExtension(short primariesIndex, short transferFunctionIndex, short matrixIndex) {
        this();
        this.primariesIndex = primariesIndex;
        this.transferFunctionIndex = transferFunctionIndex;
        this.matrixIndex = matrixIndex;
    }

    public ColorExtension() {
        super(new Header(fourcc()));
        this.type = "nclc";
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        input.getInt();
        this.primariesIndex = input.getShort();
        this.transferFunctionIndex = input.getShort();
        this.matrixIndex = input.getShort();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.put(JCodecUtil.asciiString("nclc"));
        out.putShort(this.primariesIndex);
        out.putShort(this.transferFunctionIndex);
        out.putShort(this.matrixIndex);
    }

    public static String fourcc() {
        return "colr";
    }

    public short getPrimariesIndex() {
        return this.primariesIndex;
    }

    public short getTransferFunctionIndex() {
        return this.transferFunctionIndex;
    }

    public short getMatrixIndex() {
        return this.matrixIndex;
    }
}
