package org.jcodec.containers.mxf.model;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import org.jcodec.common.logging.Logger;

/* loaded from: classes.dex */
public class RGBAEssenceDescriptor extends GenericPictureEssenceDescriptor {
    private int alphaMaxRef;
    private int alphaMinRef;
    private int componentMaxRef;
    private int componentMinRef;
    private ByteBuffer palette;
    private ByteBuffer paletteLayout;
    private ByteBuffer pixelLayout;
    private byte scanningDirection;

    public RGBAEssenceDescriptor(C0893UL ul) {
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
                case 13313:
                    this.pixelLayout = _bb;
                    break;
                case 13314:
                default:
                    Logger.warn(String.format("Unknown tag [ " + this.f1552ul + "]: %04x", entry.getKey()));
                    continue;
                case 13315:
                    this.palette = _bb;
                    break;
                case 13316:
                    this.paletteLayout = _bb;
                    break;
                case 13317:
                    this.scanningDirection = _bb.get();
                    break;
                case 13318:
                    this.componentMaxRef = _bb.getInt();
                    break;
                case 13319:
                    this.componentMinRef = _bb.getInt();
                    break;
                case 13320:
                    this.alphaMaxRef = _bb.getInt();
                    break;
                case 13321:
                    this.alphaMinRef = _bb.getInt();
                    break;
            }
            it.remove();
        }
    }

    public int getComponentMaxRef() {
        return this.componentMaxRef;
    }

    public int getComponentMinRef() {
        return this.componentMinRef;
    }

    public int getAlphaMaxRef() {
        return this.alphaMaxRef;
    }

    public int getAlphaMinRef() {
        return this.alphaMinRef;
    }

    public byte getScanningDirection() {
        return this.scanningDirection;
    }

    public ByteBuffer getPixelLayout() {
        return this.pixelLayout;
    }

    public ByteBuffer getPalette() {
        return this.palette;
    }

    public ByteBuffer getPaletteLayout() {
        return this.paletteLayout;
    }
}
