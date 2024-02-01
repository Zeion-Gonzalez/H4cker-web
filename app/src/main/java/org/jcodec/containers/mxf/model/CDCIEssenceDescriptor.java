package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class CDCIEssenceDescriptor extends GenericPictureEssenceDescriptor {
    private int alphaSampleDepth;
    private int blackRefLevel;
    private int colorRange;
    private byte colorSiting;
    private int componentDepth;
    private int horizontalSubsampling;
    private short paddingBits;
    private byte reversedByteOrder;
    private int verticalSubsampling;
    private int whiteReflevel;

    public CDCIEssenceDescriptor(C0893UL ul) {
        super(ul);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mxf.model.GenericPictureEssenceDescriptor, org.jcodec.containers.mxf.model.FileDescriptor, org.jcodec.containers.mxf.model.GenericDescriptor, org.jcodec.containers.mxf.model.MXFInterchangeObject
    public void read(Map<Integer, ByteBuffer> tags) {
        super.read(tags);
        Iterator<Map.Entry<Integer, ByteBuffer>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ByteBuffer> entry = it.next();
            ByteBuffer _bb = entry.getValue();
            switch (entry.getKey().intValue()) {
                case 13057:
                    this.componentDepth = _bb.getInt();
                    break;
                case 13058:
                    this.horizontalSubsampling = _bb.getInt();
                    break;
                case 13059:
                    this.colorSiting = _bb.get();
                    break;
                case 13060:
                    this.blackRefLevel = _bb.getInt();
                    break;
                case 13061:
                    this.whiteReflevel = _bb.getInt();
                    break;
                case 13062:
                    this.colorRange = _bb.getInt();
                    break;
                case 13063:
                    this.paddingBits = _bb.getShort();
                    break;
                case 13064:
                    this.verticalSubsampling = _bb.getInt();
                    break;
                case 13065:
                    this.alphaSampleDepth = _bb.getInt();
                    break;
                case 13066:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 13067:
                    this.reversedByteOrder = _bb.get();
                    break;
            }
            it.remove();
        }
    }

    public int getComponentDepth() {
        return this.componentDepth;
    }

    public int getHorizontalSubsampling() {
        return this.horizontalSubsampling;
    }

    public int getVerticalSubsampling() {
        return this.verticalSubsampling;
    }

    public byte getColorSiting() {
        return this.colorSiting;
    }

    public byte getReversedByteOrder() {
        return this.reversedByteOrder;
    }

    public short getPaddingBits() {
        return this.paddingBits;
    }

    public int getAlphaSampleDepth() {
        return this.alphaSampleDepth;
    }

    public int getBlackRefLevel() {
        return this.blackRefLevel;
    }

    public int getWhiteReflevel() {
        return this.whiteReflevel;
    }

    public int getColorRange() {
        return this.colorRange;
    }
}
