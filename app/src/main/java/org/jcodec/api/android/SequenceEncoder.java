package org.jcodec.api.android;

import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import org.jcodec.scale.BitmapUtil;

/* loaded from: classes.dex */
public class SequenceEncoder extends org.jcodec.api.SequenceEncoder {
    public SequenceEncoder(File out) throws IOException {
        super(out);
    }

    public void encodeImage(Bitmap bi) throws IOException {
        encodeNativeFrame(BitmapUtil.fromBitmap(bi));
    }
}
