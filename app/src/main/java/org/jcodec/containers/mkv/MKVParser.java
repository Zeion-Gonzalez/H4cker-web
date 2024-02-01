package org.jcodec.containers.mkv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mkv.boxes.EbmlBase;
import org.jcodec.containers.mkv.boxes.EbmlBin;
import org.jcodec.containers.mkv.boxes.EbmlMaster;
import org.jcodec.containers.mkv.boxes.EbmlVoid;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class MKVParser {
    private SeekableByteChannel channel;
    private LinkedList<EbmlMaster> trace = new LinkedList<>();

    public MKVParser(SeekableByteChannel channel) {
        this.channel = channel;
    }

    public List<EbmlMaster> parse() throws IOException {
        List<EbmlMaster> tree = new ArrayList<>();
        while (true) {
            EbmlBase e = nextElement();
            if (e != null) {
                if (!isKnownType(e.f1540id)) {
                    System.err.println("Unspecified header: " + EbmlUtil.toHexString(e.f1540id) + " at " + e.offset);
                }
                while (!possibleChild(this.trace.peekFirst(), e)) {
                    closeElem(this.trace.removeFirst(), tree);
                }
                openElem(e);
                if (e instanceof EbmlMaster) {
                    this.trace.push((EbmlMaster) e);
                } else if (e instanceof EbmlBin) {
                    EbmlBin bin = (EbmlBin) e;
                    EbmlMaster traceTop = this.trace.peekFirst();
                    if (traceTop.dataOffset + traceTop.dataLen < e.dataOffset + e.dataLen) {
                        this.channel.position(traceTop.dataOffset + traceTop.dataLen);
                    } else {
                        try {
                            bin.read(this.channel);
                        } catch (OutOfMemoryError oome) {
                            throw new RuntimeException(e.type + " 0x" + EbmlUtil.toHexString(bin.f1540id) + " size: " + Long.toHexString(bin.dataLen) + " offset: 0x" + Long.toHexString(e.offset), oome);
                        }
                    }
                    this.trace.peekFirst().add(e);
                } else if (e instanceof EbmlVoid) {
                    ((EbmlVoid) e).skip(this.channel);
                } else {
                    throw new RuntimeException("Currently there are no elements that are neither Master nor Binary, should never actually get here");
                }
            } else {
                while (this.trace.peekFirst() != null) {
                    closeElem(this.trace.removeFirst(), tree);
                }
                return tree;
            }
        }
    }

    private boolean possibleChild(EbmlMaster parent, EbmlBase child) {
        if (parent == null || !MKVType.Cluster.equals(parent.type) || child == null || MKVType.Cluster.equals(child.type) || MKVType.Info.equals(child.type) || MKVType.SeekHead.equals(child.type) || MKVType.Tracks.equals(child.type) || MKVType.Cues.equals(child.type) || MKVType.Attachments.equals(child.type) || MKVType.Tags.equals(child.type) || MKVType.Chapters.equals(child.type)) {
            return MKVType.possibleChild(parent, child);
        }
        return true;
    }

    private void openElem(EbmlBase e) {
    }

    private void closeElem(EbmlMaster e, List<EbmlMaster> tree) {
        if (this.trace.peekFirst() == null) {
            tree.add(e);
        } else {
            this.trace.peekFirst().add(e);
        }
    }

    private EbmlBase nextElement() throws IOException {
        long offset = this.channel.position();
        if (offset >= this.channel.size()) {
            return null;
        }
        byte[] typeId = readEbmlId(this.channel);
        while (typeId == null && !isKnownType(typeId) && offset < this.channel.size()) {
            offset++;
            this.channel.position(offset);
            typeId = readEbmlId(this.channel);
        }
        long dataLen = readEbmlInt(this.channel);
        EbmlBase elem = MKVType.createById(typeId, offset);
        elem.offset = offset;
        elem.typeSizeLength = (int) (this.channel.position() - offset);
        elem.dataOffset = this.channel.position();
        elem.dataLen = (int) dataLen;
        return elem;
    }

    public boolean isKnownType(byte[] b) {
        if (this.trace.isEmpty() || !MKVType.Cluster.equals(this.trace.peekFirst().type)) {
            return MKVType.isSpecifiedHeader(b);
        }
        return true;
    }

    public static byte[] readEbmlId(SeekableByteChannel source) throws IOException {
        if (source.position() == source.size()) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.limit(1);
        source.read(buffer);
        buffer.flip();
        byte firstByte = buffer.get();
        int numBytes = EbmlUtil.computeLength(firstByte);
        if (numBytes == 0) {
            return null;
        }
        if (numBytes > 1) {
            buffer.limit(numBytes);
            source.read(buffer);
        }
        buffer.flip();
        ByteBuffer val = ByteBuffer.allocate(buffer.remaining());
        val.put(buffer);
        return val.array();
    }

    public static long readEbmlInt(SeekableByteChannel source) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.limit(1);
        source.read(buffer);
        buffer.flip();
        byte firstByte = buffer.get();
        int length = EbmlUtil.computeLength(firstByte);
        if (length == 0) {
            throw new RuntimeException("Invalid ebml integer size.");
        }
        buffer.limit(length);
        source.read(buffer);
        buffer.position(1);
        long value = (255 >>> length) & firstByte;
        for (int length2 = length - 1; length2 > 0; length2--) {
            value = (value << 8) | (buffer.get() & 255);
        }
        return value;
    }
}
