package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class FileTypeBox extends Box {
    private Collection<String> compBrands;
    private String majorBrand;
    private int minorVersion;

    public static String fourcc() {
        return "ftyp";
    }

    public FileTypeBox(String majorBrand, int minorVersion, Collection<String> compBrands) {
        super(new Header(fourcc()));
        this.compBrands = new LinkedList();
        this.majorBrand = majorBrand;
        this.minorVersion = minorVersion;
        this.compBrands = compBrands;
    }

    public FileTypeBox() {
        super(new Header(fourcc()));
        this.compBrands = new LinkedList();
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        this.majorBrand = NIOUtils.readString(input, 4);
        this.minorVersion = input.getInt();
        while (true) {
            String brand = NIOUtils.readString(input, 4);
            if (brand != null) {
                this.compBrands.add(brand);
            } else {
                return;
            }
        }
    }

    public String getMajorBrand() {
        return this.majorBrand;
    }

    public Collection<String> getCompBrands() {
        return this.compBrands;
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.put(JCodecUtil.asciiString(this.majorBrand));
        out.putInt(this.minorVersion);
        for (String string : this.compBrands) {
            out.put(JCodecUtil.asciiString(string));
        }
    }
}
