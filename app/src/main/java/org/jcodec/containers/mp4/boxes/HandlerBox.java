package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class HandlerBox extends FullBox {
    private int componentFlags;
    private int componentFlagsMask;
    private String componentManufacturer;
    private String componentName;
    private String componentSubType;
    private String componentType;

    public static String fourcc() {
        return "hdlr";
    }

    public HandlerBox(String componentType, String componentSubType, String componentManufacturer, int componentFlags, int componentFlagsMask) {
        super(new Header("hdlr"));
        this.componentType = componentType;
        this.componentSubType = componentSubType;
        this.componentManufacturer = componentManufacturer;
        this.componentFlags = componentFlags;
        this.componentFlagsMask = componentFlagsMask;
        this.componentName = "";
    }

    public HandlerBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.componentType = NIOUtils.readString(input, 4);
        this.componentSubType = NIOUtils.readString(input, 4);
        this.componentManufacturer = NIOUtils.readString(input, 4);
        this.componentFlags = input.getInt();
        this.componentFlagsMask = input.getInt();
        this.componentName = NIOUtils.readString(input, input.remaining());
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.put(JCodecUtil.asciiString(this.componentType));
        out.put(JCodecUtil.asciiString(this.componentSubType));
        out.put(JCodecUtil.asciiString(this.componentManufacturer));
        out.putInt(this.componentFlags);
        out.putInt(this.componentFlagsMask);
        if (this.componentName != null) {
            out.put(JCodecUtil.asciiString(this.componentName));
        }
    }

    public String getComponentType() {
        return this.componentType;
    }

    public String getComponentSubType() {
        return this.componentSubType;
    }

    public String getComponentManufacturer() {
        return this.componentManufacturer;
    }

    public int getComponentFlags() {
        return this.componentFlags;
    }

    public int getComponentFlagsMask() {
        return this.componentFlagsMask;
    }
}
