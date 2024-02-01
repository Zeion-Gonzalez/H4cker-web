package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class VideoSampleEntry extends SampleEntry {
    private static final MyFactory FACTORY = new MyFactory();
    private short clrTbl;
    private String compressorName;
    private short depth;
    private short frameCount;
    private float hRes;
    private short height;
    private short revision;
    private int spacialQual;
    private int temporalQual;
    private float vRes;
    private String vendor;
    private short version;
    private short width;

    public VideoSampleEntry(Header atom, short version, short revision, String vendor, int temporalQual, int spacialQual, short width, short height, long hRes, long vRes, short frameCount, String compressorName, short depth, short drefInd, short clrTbl) {
        super(atom, drefInd);
        this.factory = FACTORY;
        this.version = version;
        this.revision = revision;
        this.vendor = vendor;
        this.temporalQual = temporalQual;
        this.spacialQual = spacialQual;
        this.width = width;
        this.height = height;
        this.hRes = (float) hRes;
        this.vRes = (float) vRes;
        this.frameCount = frameCount;
        this.compressorName = compressorName;
        this.depth = depth;
        this.clrTbl = clrTbl;
    }

    public VideoSampleEntry(Header atom) {
        super(atom);
        this.factory = FACTORY;
    }

    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.version = input.getShort();
        this.revision = input.getShort();
        this.vendor = NIOUtils.readString(input, 4);
        this.temporalQual = input.getInt();
        this.spacialQual = input.getInt();
        this.width = input.getShort();
        this.height = input.getShort();
        this.hRes = input.getInt() / 65536.0f;
        this.vRes = input.getInt() / 65536.0f;
        input.getInt();
        this.frameCount = input.getShort();
        this.compressorName = NIOUtils.readPascalString(input, 31);
        this.depth = input.getShort();
        this.clrTbl = input.getShort();
        parseExtensions(input);
    }

    @Override // org.jcodec.containers.mp4.boxes.SampleEntry, org.jcodec.containers.mp4.boxes.NodeBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putShort(this.version);
        out.putShort(this.revision);
        out.put(JCodecUtil.asciiString(this.vendor), 0, 4);
        out.putInt(this.temporalQual);
        out.putInt(this.spacialQual);
        out.putShort(this.width);
        out.putShort(this.height);
        out.putInt((int) (this.hRes * 65536.0f));
        out.putInt((int) (this.vRes * 65536.0f));
        out.putInt(0);
        out.putShort(this.frameCount);
        NIOUtils.writePascalString(out, this.compressorName, 31);
        out.putShort(this.depth);
        out.putShort(this.clrTbl);
        writeExtensions(out);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float gethRes() {
        return this.hRes;
    }

    public float getvRes() {
        return this.vRes;
    }

    public long getFrameCount() {
        return this.frameCount;
    }

    public String getCompressorName() {
        return this.compressorName;
    }

    public long getDepth() {
        return this.depth;
    }

    public String getVendor() {
        return this.vendor;
    }

    public short getVersion() {
        return this.version;
    }

    public short getRevision() {
        return this.revision;
    }

    public int getTemporalQual() {
        return this.temporalQual;
    }

    public int getSpacialQual() {
        return this.spacialQual;
    }

    public short getClrTbl() {
        return this.clrTbl;
    }

    /* loaded from: classes.dex */
    public static class MyFactory extends BoxFactory {
        private Map<String, Class<? extends Box>> mappings = new HashMap();

        public MyFactory() {
            this.mappings.put(PixelAspectExt.fourcc(), PixelAspectExt.class);
            this.mappings.put(ColorExtension.fourcc(), ColorExtension.class);
            this.mappings.put(GamaExtension.fourcc(), GamaExtension.class);
            this.mappings.put(CleanApertureExtension.fourcc(), CleanApertureExtension.class);
            this.mappings.put(FielExtension.fourcc(), FielExtension.class);
        }

        @Override // org.jcodec.containers.mp4.boxes.BoxFactory
        public Class<? extends Box> toClass(String fourcc) {
            return this.mappings.get(fourcc);
        }
    }
}
