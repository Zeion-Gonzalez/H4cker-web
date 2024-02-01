package org.jcodec.codecs.aac;

/* loaded from: classes.dex */
public enum BlockType {
    TYPE_SCE(0),
    TYPE_CPE(1),
    TYPE_CCE(2),
    TYPE_LFE(3),
    TYPE_DSE(4),
    TYPE_PCE(5),
    TYPE_FIL(6),
    TYPE_END(7);

    private int code;

    BlockType(int code) {
        this.code = code;
    }

    public static BlockType fromCode(long readNBit) {
        return null;
    }

    public int getCode() {
        return this.code;
    }
}
