package org.jcodec.codecs.common.biari;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class BitIO {

    /* loaded from: classes.dex */
    public interface InputBits {
        int getBit() throws IOException;
    }

    /* loaded from: classes.dex */
    public interface OutputBits {
        void flush() throws IOException;

        void putBit(int i) throws IOException;
    }

    public static InputBits inputFromStream(InputStream is) {
        return new StreamInputBits(is);
    }

    public static OutputBits outputFromStream(OutputStream out) {
        return new StreamOutputBits(out);
    }

    public static InputBits inputFromArray(byte[] bytes) {
        return new StreamInputBits(new ByteArrayInputStream(bytes));
    }

    public static OutputBits outputFromArray(final byte[] bytes) {
        return new StreamOutputBits(new OutputStream() { // from class: org.jcodec.codecs.common.biari.BitIO.1
            int ptr;

            @Override // java.io.OutputStream
            public void write(int b) throws IOException {
                if (this.ptr >= bytes.length) {
                    throw new IOException("Buffer is full");
                }
                byte[] bArr = bytes;
                int i = this.ptr;
                this.ptr = i + 1;
                bArr[i] = (byte) b;
            }
        });
    }

    public static byte[] compressBits(int[] decompressed) {
        byte[] compressed = new byte[(decompressed.length >> 3) + 1];
        OutputBits out = outputFromArray(compressed);
        try {
            for (int bit : decompressed) {
                out.putBit(bit);
            }
        } catch (IOException e) {
        }
        return compressed;
    }

    public static int[] decompressBits(byte[] compressed) {
        int[] decompressed = new int[compressed.length << 3];
        InputBits inputFromArray = inputFromArray(compressed);
        int i = 0;
        while (true) {
            try {
                int read = inputFromArray.getBit();
                if (read == -1) {
                    break;
                }
                decompressed[i] = read;
                i++;
            } catch (IOException e) {
            }
        }
        return decompressed;
    }

    /* loaded from: classes.dex */
    public static class StreamInputBits implements InputBits {
        private int bit = 8;
        private int cur;

        /* renamed from: in */
        private InputStream f1443in;

        public StreamInputBits(InputStream in) {
            this.f1443in = in;
        }

        @Override // org.jcodec.codecs.common.biari.BitIO.InputBits
        public int getBit() throws IOException {
            if (this.bit > 7) {
                this.cur = this.f1443in.read();
                if (this.cur == -1) {
                    return -1;
                }
                this.bit = 0;
            }
            int i = this.cur;
            int i2 = this.bit;
            this.bit = i2 + 1;
            return (i >> (7 - i2)) & 1;
        }
    }

    /* loaded from: classes.dex */
    public static class StreamOutputBits implements OutputBits {
        private int bit;
        private int cur;
        private OutputStream out;

        public StreamOutputBits(OutputStream out) {
            this.out = out;
        }

        @Override // org.jcodec.codecs.common.biari.BitIO.OutputBits
        public void putBit(int symbol) throws IOException {
            if (this.bit > 7) {
                this.out.write(this.cur);
                this.cur = 0;
                this.bit = 0;
            }
            int i = this.cur;
            int i2 = this.bit;
            this.bit = i2 + 1;
            this.cur = i | ((symbol & 1) << (7 - i2));
        }

        @Override // org.jcodec.codecs.common.biari.BitIO.OutputBits
        public void flush() throws IOException {
            if (this.bit > 0) {
                this.out.write(this.cur);
            }
        }
    }
}
