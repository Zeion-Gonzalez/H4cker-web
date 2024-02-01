package org.jcodec.codecs.ppm;

import android.support.v7.widget.helper.ItemTouchHelper;
import java.nio.ByteBuffer;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class PPMEncoder {
    public ByteBuffer encodeFrame(Picture picture) {
        if (picture.getColor() != ColorSpace.RGB) {
            throw new IllegalArgumentException("Only RGB image can be stored in PPM");
        }
        ByteBuffer buffer = ByteBuffer.allocate((picture.getWidth() * picture.getHeight() * 3) + ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        buffer.put(JCodecUtil.asciiString("P6 " + picture.getWidth() + " " + picture.getHeight() + " 255\n"));
        int[][] data = picture.getData();
        for (int i = 0; i < picture.getWidth() * picture.getHeight() * 3; i += 3) {
            buffer.put((byte) data[0][i + 2]);
            buffer.put((byte) data[0][i + 1]);
            buffer.put((byte) data[0][i]);
        }
        buffer.flip();
        return buffer;
    }
}
