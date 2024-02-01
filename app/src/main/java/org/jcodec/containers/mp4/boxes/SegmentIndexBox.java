package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
public class SegmentIndexBox extends FullBox {
    public long earliest_presentation_time;
    public long first_offset;
    public long reference_ID;
    public int reference_count;
    public Reference[] references;
    public int reserved;
    public long timescale;

    /* loaded from: classes.dex */
    public static class Reference {
        public long SAP_delta_time;
        public int SAP_type;
        public boolean reference_type;
        public long referenced_size;
        public boolean starts_with_SAP;
        public long subsegment_duration;

        public String toString() {
            return "Reference [reference_type=" + this.reference_type + ", referenced_size=" + this.referenced_size + ", subsegment_duration=" + this.subsegment_duration + ", starts_with_SAP=" + this.starts_with_SAP + ", SAP_type=" + this.SAP_type + ", SAP_delta_time=" + this.SAP_delta_time + "]";
        }
    }

    public static String fourcc() {
        return "sidx";
    }

    public SegmentIndexBox() {
        super(new Header(fourcc()));
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.reference_ID = input.getInt() & 4294967295L;
        this.timescale = input.getInt() & 4294967295L;
        if (this.version == 0) {
            this.earliest_presentation_time = input.getInt() & 4294967295L;
            this.first_offset = input.getInt() & 4294967295L;
        } else {
            this.earliest_presentation_time = input.getLong();
            this.first_offset = input.getLong();
        }
        this.reserved = input.getShort();
        this.reference_count = input.getShort() & 65535;
        this.references = new Reference[this.reference_count];
        for (int i = 0; i < this.reference_count; i++) {
            long i0 = input.getInt() & 4294967295L;
            long i1 = input.getInt() & 4294967295L;
            long i2 = input.getInt() & 4294967295L;
            Reference ref = new Reference();
            ref.reference_type = (i0 >> 31) == 1;
            ref.referenced_size = 2147483647L & i0;
            ref.subsegment_duration = i1;
            ref.starts_with_SAP = (i2 >> 31) == 1;
            ref.SAP_type = (int) ((i2 >> 28) & 7);
            ref.SAP_delta_time = 268435455 & i2;
            this.references[i] = ref;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt((int) this.reference_ID);
        out.putInt((int) this.timescale);
        if (this.version == 0) {
            out.putInt((int) this.earliest_presentation_time);
            out.putInt((int) this.first_offset);
        } else {
            out.putLong(this.earliest_presentation_time);
            out.putLong(this.first_offset);
        }
        out.putShort((short) this.reserved);
        out.putShort((short) this.reference_count);
        for (int i = 0; i < this.reference_count; i++) {
            Reference ref = this.references[i];
            int i0 = (int) (((ref.reference_type ? 1 : 0) << 31) | ref.referenced_size);
            int i1 = (int) ref.subsegment_duration;
            int i2 = 0;
            if (ref.starts_with_SAP) {
                i2 = 0 | Integer.MIN_VALUE;
            }
            int i22 = (int) (i2 | ((ref.SAP_type & 7) << 28) | (ref.SAP_delta_time & 268435455));
            out.putInt(i0);
            out.putInt(i1);
            out.putInt(i22);
        }
    }

    @Override // org.jcodec.containers.mp4.boxes.Box
    public String toString() {
        return "SegmentIndexBox [reference_ID=" + this.reference_ID + ", timescale=" + this.timescale + ", earliest_presentation_time=" + this.earliest_presentation_time + ", first_offset=" + this.first_offset + ", reserved=" + this.reserved + ", reference_count=" + this.reference_count + ", references=" + Arrays.toString(this.references) + ", version=" + ((int) this.version) + ", flags=" + this.flags + ", header=" + this.header + "]";
    }
}
