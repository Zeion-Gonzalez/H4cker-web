package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;

/* loaded from: classes.dex */
public abstract class Descriptor {
    private static DescriptorFactory factory = new DescriptorFactory();
    private int size;
    private int tag;

    protected abstract void doWrite(ByteBuffer byteBuffer);

    protected abstract void parse(ByteBuffer byteBuffer);

    public Descriptor(int tag) {
        this(tag, 0);
    }

    public Descriptor(int tag, int size) {
        this.tag = tag;
        this.size = size;
    }

    public void write(ByteBuffer out) {
        ByteBuffer fork = out.duplicate();
        NIOUtils.skip(out, 5);
        doWrite(out);
        int length = (out.position() - fork.position()) - 5;
        fork.put((byte) this.tag);
        JCodecUtil.writeBER32(fork, length);
    }

    public static Descriptor read(ByteBuffer input) {
        if (input.remaining() < 2) {
            return null;
        }
        int tag = input.get() & 255;
        int size = JCodecUtil.readBER32(input);
        Class<? extends Descriptor> cls = factory.byTag(tag);
        try {
            Descriptor descriptor = cls.getConstructor(Integer.TYPE, Integer.TYPE).newInstance(Integer.valueOf(tag), Integer.valueOf(size));
            descriptor.parse(NIOUtils.read(input, size));
            return descriptor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T find(Descriptor es, Class<T> class1, int tag) {
        if (es.getTag() != tag) {
            if (es instanceof NodeDescriptor) {
                for (Descriptor descriptor : ((NodeDescriptor) es).getChildren()) {
                    T res = (T) find(descriptor, class1, tag);
                    if (res != null) {
                        return res;
                    }
                }
            }
            return null;
        }
        return es;
    }

    private int getTag() {
        return this.tag;
    }
}
