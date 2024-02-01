package org.jcodec.containers.mp4;

import java.io.IOException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.boxes.AliasBox;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.DataInfoBox;
import org.jcodec.containers.mp4.boxes.DataRefBox;
import org.jcodec.containers.mp4.boxes.MediaInfoBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class ChunkWriter {
    byte[] buf = new byte[8092];
    private int curChunk;
    private SampleEntry[] entries;
    private SeekableByteChannel[] inputs;
    private long[] offsets;
    private SeekableByteChannel out;
    private TrakBox trak;

    public ChunkWriter(TrakBox trak, SeekableByteChannel[] inputs, SeekableByteChannel out) {
        int size;
        this.entries = (SampleEntry[]) NodeBox.findAll(trak, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
        ChunkOffsetsBox stco = (ChunkOffsetsBox) NodeBox.findFirst(trak, ChunkOffsetsBox.class, "mdia", "minf", "stbl", "stco");
        ChunkOffsets64Box co64 = (ChunkOffsets64Box) NodeBox.findFirst(trak, ChunkOffsets64Box.class, "mdia", "minf", "stbl", "co64");
        if (stco != null) {
            size = stco.getChunkOffsets().length;
        } else {
            size = co64.getChunkOffsets().length;
        }
        this.inputs = inputs;
        this.offsets = new long[size];
        this.out = out;
        this.trak = trak;
    }

    public void apply() {
        NodeBox stbl = (NodeBox) NodeBox.findFirst(this.trak, NodeBox.class, "mdia", "minf", "stbl");
        stbl.removeChildren("stco", "co64");
        stbl.add(new ChunkOffsets64Box(this.offsets));
        cleanDrefs(this.trak);
    }

    private void cleanDrefs(TrakBox trak) {
        MediaInfoBox minf = trak.getMdia().getMinf();
        DataInfoBox dinf = trak.getMdia().getMinf().getDinf();
        if (dinf == null) {
            dinf = new DataInfoBox();
            minf.add(dinf);
        }
        DataRefBox dref = dinf.getDref();
        if (dref == null) {
            dref = new DataRefBox();
            dinf.add(dref);
        }
        dref.getBoxes().clear();
        dref.add(AliasBox.createSelfRef());
        SampleEntry[] arr$ = (SampleEntry[]) NodeBox.findAll(trak, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null);
        for (SampleEntry entry : arr$) {
            entry.setDrefInd((short) 1);
        }
    }

    private SeekableByteChannel getInput(Chunk chunk) {
        SampleEntry se = this.entries[chunk.getEntry() - 1];
        return this.inputs[se.getDrefInd() - 1];
    }

    public void write(Chunk chunk) throws IOException {
        SeekableByteChannel input = getInput(chunk);
        input.position(chunk.getOffset());
        long pos = this.out.position();
        this.out.write(NIOUtils.fetchFrom(input, (int) chunk.getSize()));
        long[] jArr = this.offsets;
        int i = this.curChunk;
        this.curChunk = i + 1;
        jArr[i] = pos;
    }
}
