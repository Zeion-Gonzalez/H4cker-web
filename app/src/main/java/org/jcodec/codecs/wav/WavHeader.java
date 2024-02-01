package org.jcodec.codecs.wav;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.IOUtils;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ChannelLabel;

/* loaded from: classes.dex */
public class WavHeader {
    public static final int WAV_HEADER_SIZE = 44;
    static ChannelLabel[] mapping = {ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.FRONT_CENTER_LEFT, ChannelLabel.FRONT_CENTER_RIGHT, ChannelLabel.REAR_CENTER, ChannelLabel.SIDE_LEFT, ChannelLabel.SIDE_RIGHT, ChannelLabel.CENTER, ChannelLabel.FRONT_LEFT, ChannelLabel.CENTER, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_CENTER, ChannelLabel.REAR_RIGHT, ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT};
    public String chunkId;
    public int chunkSize;
    public int dataOffset;
    public long dataSize;
    public FmtChunk fmt;
    public String format;

    /* loaded from: classes.dex */
    public static class FmtChunkExtended extends FmtChunk {
        short bitsPerCodedSample;
        short cbSize;
        int channelLayout;
        int guid;

        public FmtChunkExtended(FmtChunk fmtChunk, short cbSize, short bitsPerCodedSample, int channelLayout, int guid) {
            super(fmtChunk);
            this.cbSize = cbSize;
            this.bitsPerCodedSample = bitsPerCodedSample;
            this.channelLayout = channelLayout;
            this.guid = guid;
        }

        public FmtChunkExtended(FmtChunkExtended fmt) {
            this(fmt, fmt.cbSize, fmt.bitsPerCodedSample, fmt.channelLayout, fmt.guid);
        }

        public static FmtChunk read(ByteBuffer bb) throws IOException {
            FmtChunk fmtChunk = FmtChunk.get(bb);
            ByteOrder old = bb.order();
            try {
                bb.order(ByteOrder.LITTLE_ENDIAN);
                return new FmtChunkExtended(fmtChunk, bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt());
            } finally {
                bb.order(old);
            }
        }

        @Override // org.jcodec.codecs.wav.WavHeader.FmtChunk
        public void put(ByteBuffer bb) throws IOException {
            super.put(bb);
            ByteOrder old = bb.order();
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort(this.cbSize);
            bb.putShort(this.bitsPerCodedSample);
            bb.putInt(this.channelLayout);
            bb.putInt(this.guid);
            bb.order(old);
        }

        @Override // org.jcodec.codecs.wav.WavHeader.FmtChunk
        public int size() {
            return super.size() + 12;
        }

        public ChannelLabel[] getLabels() {
            List<ChannelLabel> labels = new ArrayList<>();
            for (int i = 0; i < WavHeader.mapping.length; i++) {
                if ((this.channelLayout & (1 << i)) != 0) {
                    labels.add(WavHeader.mapping[i]);
                }
            }
            return (ChannelLabel[]) labels.toArray(new ChannelLabel[0]);
        }
    }

    /* loaded from: classes.dex */
    public static class FmtChunk {
        public short audioFormat;
        public short bitsPerSample;
        public short blockAlign;
        public int byteRate;
        public short numChannels;
        public int sampleRate;

        public FmtChunk() {
            this.audioFormat = (short) 1;
        }

        public FmtChunk(short audioFormat, short numChannels, int sampleRate, int byteRate, short blockAlign, short bitsPerSample) {
            this.audioFormat = audioFormat;
            this.numChannels = numChannels;
            this.sampleRate = sampleRate;
            this.byteRate = byteRate;
            this.blockAlign = blockAlign;
            this.bitsPerSample = bitsPerSample;
        }

        public FmtChunk(FmtChunk other) {
            this(other.audioFormat, other.numChannels, other.sampleRate, other.byteRate, other.blockAlign, other.bitsPerSample);
        }

        public static FmtChunk get(ByteBuffer bb) throws IOException {
            ByteOrder old = bb.order();
            try {
                bb.order(ByteOrder.LITTLE_ENDIAN);
                return new FmtChunk(bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort());
            } finally {
                bb.order(old);
            }
        }

        public void put(ByteBuffer bb) throws IOException {
            ByteOrder old = bb.order();
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort(this.audioFormat);
            bb.putShort(this.numChannels);
            bb.putInt(this.sampleRate);
            bb.putInt(this.byteRate);
            bb.putShort(this.blockAlign);
            bb.putShort(this.bitsPerSample);
            bb.order(old);
        }

        public int size() {
            return 16;
        }
    }

    public WavHeader(String chunkId, int chunkSize, String format, FmtChunk fmt, int dataOffset, long dataSize) {
        this.chunkId = chunkId;
        this.chunkSize = chunkSize;
        this.format = format;
        this.fmt = fmt;
        this.dataOffset = dataOffset;
        this.dataSize = dataSize;
    }

    public WavHeader(WavHeader header) {
        this(header.chunkId, header.chunkSize, header.format, header.fmt instanceof FmtChunkExtended ? new FmtChunkExtended((FmtChunkExtended) header.fmt) : new FmtChunk(header.fmt), header.dataOffset, header.dataSize);
    }

    public static WavHeader copyWithRate(WavHeader header, int rate) {
        WavHeader result = new WavHeader(header);
        result.fmt.sampleRate = rate;
        return result;
    }

    public static WavHeader copyWithChannels(WavHeader header, int channels) {
        WavHeader result = new WavHeader(header);
        result.fmt.numChannels = (short) channels;
        return result;
    }

    public WavHeader(AudioFormat format, int samples) {
        this("RIFF", 40, "WAVE", new FmtChunk((short) 1, (short) format.getChannels(), format.getSampleRate(), format.getSampleRate() * format.getChannels() * (format.getSampleSizeInBits() >> 3), (short) (format.getChannels() * (format.getSampleSizeInBits() >> 3)), (short) format.getSampleSizeInBits()), 44, calcDataSize(format.getChannels(), format.getSampleSizeInBits() >> 3, samples));
    }

    public static WavHeader stereo48k() {
        return stereo48k(0L);
    }

    public static WavHeader stereo48k(long samples) {
        return new WavHeader("RIFF", 40, "WAVE", new FmtChunk((short) 1, (short) 2, 48000, 192000, (short) 4, (short) 16), 44, calcDataSize(2, 2, samples));
    }

    public static WavHeader mono48k(long samples) {
        return new WavHeader("RIFF", 40, "WAVE", new FmtChunk((short) 1, (short) 1, 48000, 96000, (short) 2, (short) 16), 44, calcDataSize(1, 2, samples));
    }

    public static WavHeader emptyWavHeader() {
        return new WavHeader("RIFF", 40, "WAVE", new FmtChunk(), 44, 0L);
    }

    public static WavHeader read(File file) throws IOException {
        ReadableByteChannel is = null;
        try {
            is = NIOUtils.readableFileChannel(file);
            return read(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static WavHeader read(ReadableByteChannel in) throws IOException {
        String fourcc;
        int size;
        ByteBuffer buf = ByteBuffer.allocate(128);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        in.read(buf);
        if (buf.remaining() > 0) {
            throw new IOException("Incomplete wav header found");
        }
        buf.flip();
        String chunkId = NIOUtils.readString(buf, 4);
        int chunkSize = buf.getInt();
        String format = NIOUtils.readString(buf, 4);
        FmtChunk fmt = null;
        if (!"RIFF".equals(chunkId) || !"WAVE".equals(format)) {
            return null;
        }
        do {
            fourcc = NIOUtils.readString(buf, 4);
            size = buf.getInt();
            if ("fmt ".equals(fourcc) && size >= 14 && size <= 1048576) {
                switch (size) {
                    case 16:
                        fmt = FmtChunk.get(buf);
                        break;
                    case 18:
                        fmt = FmtChunk.get(buf);
                        NIOUtils.skip(buf, 2);
                        break;
                    case 28:
                        fmt = FmtChunkExtended.get(buf);
                        break;
                    case 40:
                        fmt = FmtChunkExtended.get(buf);
                        NIOUtils.skip(buf, 12);
                        break;
                    default:
                        throw new IllegalStateException("Don't know how to handle fmt size: " + size);
                }
            } else if (!"data".equals(fourcc)) {
                NIOUtils.skip(buf, size);
            }
        } while (!"data".equals(fourcc));
        return new WavHeader(chunkId, chunkSize, format, fmt, buf.position(), size);
    }

    public static WavHeader multiChannelWav(List<File> wavs) throws IOException {
        return multiChannelWav((File[]) wavs.toArray(new File[0]));
    }

    public static WavHeader multiChannelWav(File... wavs) throws IOException {
        WavHeader[] headers = new WavHeader[wavs.length];
        for (int i = 0; i < wavs.length; i++) {
            headers[i] = read(wavs[i]);
        }
        return multiChannelWav(headers);
    }

    public static WavHeader multiChannelWav(WavHeader... wavs) {
        WavHeader w = emptyWavHeader();
        int totalSize = 0;
        for (WavHeader wavHeader : wavs) {
            totalSize = (int) (totalSize + wavHeader.dataSize);
        }
        w.dataSize = totalSize;
        FmtChunk fmt = wavs[0].fmt;
        int bitsPerSample = fmt.bitsPerSample;
        int bytesPerSample = bitsPerSample / 8;
        int sampleRate = fmt.sampleRate;
        w.fmt.bitsPerSample = (short) bitsPerSample;
        w.fmt.blockAlign = (short) (wavs.length * bytesPerSample);
        w.fmt.byteRate = wavs.length * bytesPerSample * sampleRate;
        w.fmt.numChannels = (short) wavs.length;
        w.fmt.sampleRate = sampleRate;
        return w;
    }

    public void write(WritableByteChannel out) throws IOException {
        long chunkSize;
        ByteBuffer bb = ByteBuffer.allocate(44);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        if (this.dataSize <= 4294967295L) {
            chunkSize = this.dataSize + 36;
        } else {
            chunkSize = 40;
        }
        bb.put(JCodecUtil.asciiString("RIFF"));
        bb.putInt((int) chunkSize);
        bb.put(JCodecUtil.asciiString("WAVE"));
        bb.put(JCodecUtil.asciiString("fmt "));
        bb.putInt(this.fmt.size());
        this.fmt.put(bb);
        bb.put(JCodecUtil.asciiString("data"));
        if (this.dataSize <= 4294967295L) {
            bb.putInt((int) this.dataSize);
        } else {
            bb.putInt(0);
        }
        bb.flip();
        out.write(bb);
    }

    public static long calcDataSize(int numChannels, int bytesPerSample, long samples) {
        return numChannels * samples * bytesPerSample;
    }

    public static WavHeader create(AudioFormat af, int size) {
        WavHeader w = emptyWavHeader();
        w.dataSize = size;
        new FmtChunk();
        int bitsPerSample = af.getSampleSizeInBits();
        int i = bitsPerSample / 8;
        af.getSampleRate();
        w.fmt.bitsPerSample = (short) bitsPerSample;
        w.fmt.blockAlign = af.getFrameSize();
        w.fmt.byteRate = af.getFrameRate() * af.getFrameSize();
        w.fmt.numChannels = (short) af.getChannels();
        w.fmt.sampleRate = af.getSampleRate();
        return w;
    }

    public ChannelLabel[] getChannelLabels() {
        if (this.fmt instanceof FmtChunkExtended) {
            return ((FmtChunkExtended) this.fmt).getLabels();
        }
        switch (this.fmt.numChannels) {
            case 1:
                return new ChannelLabel[]{ChannelLabel.MONO};
            case 2:
                return new ChannelLabel[]{ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT};
            case 3:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_CENTER};
            case 4:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
            case 5:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
            case 6:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
            case 7:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.REAR_CENTER};
            case 8:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
            default:
                ChannelLabel[] labels = new ChannelLabel[this.fmt.numChannels];
                Arrays.fill(labels, ChannelLabel.MONO);
                return labels;
        }
    }

    public AudioFormat getFormat() {
        return new AudioFormat(this.fmt.sampleRate, this.fmt.bitsPerSample, this.fmt.numChannels, true, false);
    }
}
