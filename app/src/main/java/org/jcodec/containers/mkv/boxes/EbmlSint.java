package org.jcodec.containers.mkv.boxes;

import android.support.v4.media.session.PlaybackStateCompat;
import java.nio.ByteBuffer;
import org.jcodec.containers.mkv.util.EbmlUtil;

/* loaded from: classes.dex */
public class EbmlSint extends EbmlBin {
    public static final long[] signedComplement = {0, 63, 8191, 1048575, 134217727, 17179869183L, 2199023255551L, 281474976710655L, 36028797018963967L};

    public EbmlSint(byte[] id) {
        super(id);
    }

    public void set(long value) {
        this.data = ByteBuffer.wrap(convertToBytes(value));
    }

    public long get() {
        if (this.data.limit() - this.data.position() == 8) {
            return this.data.duplicate().getLong();
        }
        byte[] b = this.data.array();
        long l = 0;
        for (int i = b.length - 1; i >= 0; i--) {
            l |= (b[i] & 255) << (((b.length - 1) - i) * 8);
        }
        return l;
    }

    public static int ebmlSignedLength(long val) {
        if (val <= 64 && val >= -63) {
            return 1;
        }
        if (val <= PlaybackStateCompat.ACTION_PLAY_FROM_URI && val >= -8191) {
            return 2;
        }
        if (val <= 1048576 && val >= -1048575) {
            return 3;
        }
        if (val <= 134217728 && val >= -134217727) {
            return 4;
        }
        if (val <= 17179869184L && val >= -17179869183L) {
            return 5;
        }
        if (val <= 2199023255552L && val >= -2199023255551L) {
            return 6;
        }
        if (val <= 281474976710656L && val >= -281474976710655L) {
            return 7;
        }
        return 8;
    }

    public static byte[] convertToBytes(long val) {
        int num = ebmlSignedLength(val);
        return EbmlUtil.ebmlEncode(val + signedComplement[num], num);
    }
}
