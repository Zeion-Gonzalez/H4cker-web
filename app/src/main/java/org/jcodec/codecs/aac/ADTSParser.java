package org.jcodec.codecs.aac;

import java.nio.ByteBuffer;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class ADTSParser {

    /* loaded from: classes.dex */
    public static class Header {
        private int chanConfig;
        private int crcAbsent;
        private int numAACFrames;
        private int objectType;
        private int samples;
        private int samplingIndex;

        public Header(int object_type, int chanConfig, int crcAbsent, int numAACFrames, int samplingIndex) {
            this.objectType = object_type;
            this.chanConfig = chanConfig;
            this.crcAbsent = crcAbsent;
            this.numAACFrames = numAACFrames;
            this.samplingIndex = samplingIndex;
        }

        public int getObjectType() {
            return this.objectType;
        }

        public int getChanConfig() {
            return this.chanConfig;
        }

        public int getCrcAbsent() {
            return this.crcAbsent;
        }

        public int getNumAACFrames() {
            return this.numAACFrames;
        }

        public int getSamplingIndex() {
            return this.samplingIndex;
        }

        public int getSamples() {
            return this.samples;
        }
    }

    public static Header read(ByteBuffer data) {
        ByteBuffer dup = data.duplicate();
        BitReader br = new BitReader(dup);
        if (br.readNBit(12) != 4095) {
            return null;
        }
        br.read1Bit();
        br.readNBit(2);
        int crc_abs = br.read1Bit();
        int aot = br.readNBit(2);
        int sr = br.readNBit(4);
        br.read1Bit();
        int ch = br.readNBit(3);
        br.read1Bit();
        br.read1Bit();
        br.read1Bit();
        br.read1Bit();
        int size = br.readNBit(13);
        if (size < 7) {
            return null;
        }
        br.readNBit(11);
        int rdb = br.readNBit(2);
        br.stop();
        data.position(dup.position());
        return new Header(aot + 1, ch, crc_abs, rdb + 1, sr);
    }

    public static ByteBuffer write(Header header, ByteBuffer buf, int frameSize) {
        ByteBuffer data = buf.duplicate();
        BitWriter br = new BitWriter(data);
        br.writeNBit(4095, 12);
        br.write1Bit(1);
        br.writeNBit(0, 2);
        br.write1Bit(header.getCrcAbsent());
        br.writeNBit(header.getObjectType(), 2);
        br.writeNBit(header.getSamplingIndex(), 4);
        br.write1Bit(0);
        br.writeNBit(header.getChanConfig(), 3);
        br.write1Bit(0);
        br.write1Bit(0);
        br.write1Bit(0);
        br.write1Bit(0);
        br.writeNBit(frameSize + 7, 13);
        br.writeNBit(0, 11);
        br.writeNBit(header.getNumAACFrames(), 2);
        br.flush();
        data.flip();
        return data;
    }
}
