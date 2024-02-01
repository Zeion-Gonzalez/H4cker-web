package org.jcodec.codecs.h264.mp4;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.mjpeg.JpegConst;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.Header;

/* loaded from: classes.dex */
public class AvcCBox extends Box {
    private int level;
    private int nalLengthSize;
    private List<ByteBuffer> ppsList;
    private int profile;
    private int profileCompat;
    private List<ByteBuffer> spsList;

    public AvcCBox(Box other) {
        super(other);
        this.spsList = new ArrayList();
        this.ppsList = new ArrayList();
    }

    public AvcCBox() {
        super(new Header(fourcc()));
        this.spsList = new ArrayList();
        this.ppsList = new ArrayList();
    }

    public AvcCBox(Header header) {
        super(header);
        this.spsList = new ArrayList();
        this.ppsList = new ArrayList();
    }

    public AvcCBox(int profile, int profileCompat, int level, int nalLengthSize, List<ByteBuffer> spsList, List<ByteBuffer> ppsList) {
        this();
        this.profile = profile;
        this.profileCompat = profileCompat;
        this.level = level;
        this.nalLengthSize = nalLengthSize;
        this.spsList = spsList;
        this.ppsList = ppsList;
    }

    public static String fourcc() {
        return "avcC";
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        NIOUtils.skip(input, 1);
        this.profile = input.get() & 255;
        this.profileCompat = input.get() & 255;
        this.level = input.get() & 255;
        int flags = input.get() & 255;
        this.nalLengthSize = (flags & 3) + 1;
        int nSPS = input.get() & 31;
        for (int i = 0; i < nSPS; i++) {
            int spsSize = input.getShort();
            Assert.assertEquals(39, input.get() & 63);
            this.spsList.add(NIOUtils.read(input, spsSize - 1));
        }
        int nPPS = input.get() & 255;
        for (int i2 = 0; i2 < nPPS; i2++) {
            int ppsSize = input.getShort();
            Assert.assertEquals(40, input.get() & 63);
            this.ppsList.add(NIOUtils.read(input, ppsSize - 1));
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        out.put((byte) 1);
        out.put((byte) this.profile);
        out.put((byte) this.profileCompat);
        out.put((byte) this.level);
        out.put((byte) -1);
        out.put((byte) (this.spsList.size() | JpegConst.APP0));
        for (ByteBuffer sps : this.spsList) {
            out.putShort((short) (sps.remaining() + 1));
            out.put((byte) 103);
            NIOUtils.write(out, sps);
        }
        out.put((byte) this.ppsList.size());
        for (ByteBuffer pps : this.ppsList) {
            out.putShort((byte) (pps.remaining() + 1));
            out.put((byte) 104);
            NIOUtils.write(out, pps);
        }
    }

    public int getProfile() {
        return this.profile;
    }

    public int getProfileCompat() {
        return this.profileCompat;
    }

    public int getLevel() {
        return this.level;
    }

    public List<ByteBuffer> getSpsList() {
        return this.spsList;
    }

    public List<ByteBuffer> getPpsList() {
        return this.ppsList;
    }

    public int getNalLengthSize() {
        return this.nalLengthSize;
    }
}
