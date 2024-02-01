package org.jcodec.codecs.mpeg12.bitstream;

import java.nio.ByteBuffer;
import org.jcodec.codecs.mpeg12.MPEGConst;
import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class PictureHeader implements MPEGHeader {
    public static final int BiPredictiveCoded = 3;
    public static final int Copyright_Extension = 4;
    public static final int IntraCoded = 1;
    public static final int Picture_Coding_Extension = 8;
    public static final int Picture_Display_Extension = 7;
    public static final int Picture_Spatial_Scalable_Extension = 9;
    public static final int Picture_Temporal_Scalable_Extension = 16;
    public static final int PredictiveCoded = 2;
    public static final int Quant_Matrix_Extension = 3;
    public int backward_f_code;
    public CopyrightExtension copyrightExtension;
    public int forward_f_code;
    public int full_pel_backward_vector;
    public int full_pel_forward_vector;
    private boolean hasExtensions;
    public PictureCodingExtension pictureCodingExtension;
    public PictureDisplayExtension pictureDisplayExtension;
    public PictureSpatialScalableExtension pictureSpatialScalableExtension;
    public PictureTemporalScalableExtension pictureTemporalScalableExtension;
    public int picture_coding_type;
    public QuantMatrixExtension quantMatrixExtension;
    public int temporal_reference;
    public int vbv_delay;

    public PictureHeader(int temporal_reference, int picture_coding_type, int vbv_delay, int full_pel_forward_vector, int forward_f_code, int full_pel_backward_vector, int backward_f_code) {
        this.temporal_reference = temporal_reference;
        this.picture_coding_type = picture_coding_type;
        this.vbv_delay = vbv_delay;
        this.full_pel_forward_vector = full_pel_forward_vector;
        this.forward_f_code = forward_f_code;
        this.full_pel_backward_vector = full_pel_backward_vector;
        this.backward_f_code = backward_f_code;
    }

    private PictureHeader() {
    }

    public static PictureHeader read(ByteBuffer bb) {
        BitReader in = new BitReader(bb);
        PictureHeader ph = new PictureHeader();
        ph.temporal_reference = in.readNBit(10);
        ph.picture_coding_type = in.readNBit(3);
        ph.vbv_delay = in.readNBit(16);
        if (ph.picture_coding_type == 2 || ph.picture_coding_type == 3) {
            ph.full_pel_forward_vector = in.read1Bit();
            ph.forward_f_code = in.readNBit(3);
        }
        if (ph.picture_coding_type == 3) {
            ph.full_pel_backward_vector = in.read1Bit();
            ph.backward_f_code = in.readNBit(3);
        }
        while (in.read1Bit() == 1) {
            in.readNBit(8);
        }
        return ph;
    }

    public static void readExtension(ByteBuffer bb, PictureHeader ph, SequenceHeader sh) {
        ph.hasExtensions = true;
        BitReader in = new BitReader(bb);
        int extType = in.readNBit(4);
        switch (extType) {
            case 3:
                ph.quantMatrixExtension = QuantMatrixExtension.read(in);
                return;
            case 4:
                ph.copyrightExtension = CopyrightExtension.read(in);
                return;
            case 5:
            case 6:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                throw new RuntimeException("Unsupported extension: " + extType);
            case 7:
                ph.pictureDisplayExtension = PictureDisplayExtension.read(in, sh.sequenceExtension, ph.pictureCodingExtension);
                return;
            case 8:
                ph.pictureCodingExtension = PictureCodingExtension.read(in);
                return;
            case 9:
                ph.pictureSpatialScalableExtension = PictureSpatialScalableExtension.read(in);
                return;
            case 16:
                ph.pictureTemporalScalableExtension = PictureTemporalScalableExtension.read(in);
                return;
        }
    }

    @Override // org.jcodec.codecs.mpeg12.bitstream.MPEGHeader
    public void write(ByteBuffer os) {
        BitWriter out = new BitWriter(os);
        out.writeNBit(this.temporal_reference, 10);
        out.writeNBit(this.picture_coding_type, 3);
        out.writeNBit(this.vbv_delay, 16);
        if (this.picture_coding_type == 2 || this.picture_coding_type == 3) {
            out.write1Bit(this.full_pel_forward_vector);
            out.write1Bit(this.forward_f_code);
        }
        if (this.picture_coding_type == 3) {
            out.write1Bit(this.full_pel_backward_vector);
            out.writeNBit(this.backward_f_code, 3);
        }
        out.write1Bit(0);
        out.flush();
        writeExtensions(os);
    }

    private void writeExtensions(ByteBuffer out) {
        if (this.quantMatrixExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.quantMatrixExtension.write(out);
        }
        if (this.copyrightExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.copyrightExtension.write(out);
        }
        if (this.pictureCodingExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.pictureCodingExtension.write(out);
        }
        if (this.pictureDisplayExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.pictureDisplayExtension.write(out);
        }
        if (this.pictureSpatialScalableExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.pictureSpatialScalableExtension.write(out);
        }
        if (this.pictureTemporalScalableExtension != null) {
            out.putInt(MPEGConst.EXTENSION_START_CODE);
            this.pictureTemporalScalableExtension.write(out);
        }
    }

    public boolean hasExtensions() {
        return this.hasExtensions;
    }
}
