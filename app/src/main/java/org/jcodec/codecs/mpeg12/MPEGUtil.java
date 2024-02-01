package org.jcodec.codecs.mpeg12;

import android.support.v4.app.FrameMetricsAggregator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class MPEGUtil {
    public static final ByteBuffer gotoNextMarker(ByteBuffer buf) {
        return gotoMarker(buf, 0, 256, FrameMetricsAggregator.EVERY_DURATION);
    }

    public static final ByteBuffer gotoMarker(ByteBuffer buf, int n, int mmin, int mmax) {
        if (!buf.hasRemaining()) {
            return null;
        }
        int from = buf.position();
        ByteBuffer result = buf.slice();
        result.order(ByteOrder.BIG_ENDIAN);
        int val = -1;
        while (buf.hasRemaining()) {
            val = (val << 8) | (buf.get() & 255);
            if (val >= mmin && val <= mmax) {
                if (n == 0) {
                    buf.position(buf.position() - 4);
                    result.limit(buf.position() - from);
                    return result;
                }
                n--;
            }
        }
        return result;
    }

    public static final ByteBuffer nextSegment(ByteBuffer buf) {
        gotoMarker(buf, 0, 256, FrameMetricsAggregator.EVERY_DURATION);
        return gotoMarker(buf, 1, 256, FrameMetricsAggregator.EVERY_DURATION);
    }
}
