package org.jcodec.common.tools;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.jcodec.codecs.wav.WavHeader;
import org.jcodec.common.AudioUtil;
import org.jcodec.common.IOUtils;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public class WavMerge {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("wavmerge <output wav> <input wav> .... <input wav>");
            System.exit(-1);
        }
        File out = new File(args[0]);
        File[] ins = new File[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            ins[i - 1] = new File(args[i]);
        }
        merge(out, ins);
    }

    public static void merge(File result, File... src) throws IOException {
        WritableByteChannel out = null;
        ReadableByteChannel[] inputs = new ReadableByteChannel[src.length];
        WavHeader[] headers = new WavHeader[src.length];
        ByteBuffer[] ins = new ByteBuffer[src.length];
        int sampleSize = -1;
        for (int i = 0; i < src.length; i++) {
            try {
                inputs[i] = NIOUtils.readableFileChannel(src[i]);
                WavHeader hdr = WavHeader.read(inputs[i]);
                if (sampleSize != -1 && sampleSize != hdr.fmt.bitsPerSample) {
                    throw new RuntimeException("Input files have different sample sizes");
                }
                sampleSize = hdr.fmt.bitsPerSample;
                headers[i] = hdr;
                ins[i] = ByteBuffer.allocate(hdr.getFormat().framesToBytes(4096));
            } finally {
                IOUtils.closeQuietly(out);
                for (ReadableByteChannel inputStream : inputs) {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }
        ByteBuffer outb = ByteBuffer.allocate(headers[0].getFormat().framesToBytes(4096) * src.length);
        WavHeader newHeader = WavHeader.multiChannelWav(headers);
        out = NIOUtils.writableFileChannel(result);
        newHeader.write(out);
        while (true) {
            boolean readOnce = false;
            for (int i2 = 0; i2 < ins.length; i2++) {
                if (inputs[i2] != null) {
                    ins[i2].clear();
                    if (inputs[i2].read(ins[i2]) == -1) {
                        NIOUtils.closeQuietly(inputs[i2]);
                        inputs[i2] = null;
                    } else {
                        readOnce = true;
                    }
                    ins[i2].flip();
                }
            }
            if (!readOnce) {
                break;
            }
            outb.clear();
            AudioUtil.interleave(headers[0].getFormat(), ins, outb);
            outb.flip();
            out.write(outb);
        }
    }
}
